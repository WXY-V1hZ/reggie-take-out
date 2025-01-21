package com.nenood.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nenood.reggie.common.R;
import com.nenood.reggie.dto.DishDto;
import com.nenood.reggie.entity.Category;
import com.nenood.reggie.entity.Dish;
import com.nenood.reggie.entity.DishFlavor;
import com.nenood.reggie.service.CategoryService;
import com.nenood.reggie.service.DishFlavorService;
import com.nenood.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;

    // 新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("新增菜品...");
        dishService.saveWithFlavor(dishDto);
        log.info("新增菜品成功");
        return R.success("新增菜品成功！");
    }

    // 分页查询菜品，显示菜品图片
    @GetMapping("page")
    public R<Page<DishDto>> page(@RequestParam("page") int page,
                              @RequestParam("pageSize") int pageSize,
                              @RequestParam(value = "name", required = false) String name) {
        // 两个page变量，一个用于查询Dish信息，一个用于保存菜品名称
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器，根据名字进行相似查找，根据更新时间降序排序
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 进行分页查询
        dishService.page(dishPage, queryWrapper);

        // 拷贝信息，records需要修改
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<Dish> dishList = dishPage.getRecords();
        List<DishDto> dtoList = dishList.stream().map((dish) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dtoList);

        return R.success(dishDtoPage);
    }

    @GetMapping("list")
    public R<List<DishDto>> list(Dish dish) {
        // 根据传来的参数，构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        // 从redis中获取缓存数据
        List<DishDto> dishDtoList;
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        // 如果存在则直接返回，无需查询
        if (dishDtoList != null) {
            log.info("缓存中查询到数据...");
            return R.success(dishDtoList);
        }

        // 查询分类下所有的菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);

        // 获取菜品的口味信息
        dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            // 根据dishId查询菜品口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        // redis中不存在缓存数据，需要将查询到的数据缓存到redis
        log.info("缓存中未查询到数据");
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    @GetMapping("{dishId}")
    public R<DishDto> getDishDto(@PathVariable Long dishId) {
        return dishService.getDishDto(dishId);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.update(dishDto);

        // 更新后清除缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功！");
    }

    @DeleteMapping
    public R<String> deleteById(@RequestParam("ids") String ids) {
        String[] idsArr = ids.split(",");
        List<Long> dishIds = Arrays
                .stream(idsArr)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        dishService.delete(dishIds);
        return R.success("删除菜品成功！");
    }
}
