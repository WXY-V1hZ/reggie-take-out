package com.nenood.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nenood.reggie.common.R;
import com.nenood.reggie.dto.SetmealDto;
import com.nenood.reggie.entity.Dish;
import com.nenood.reggie.entity.Setmeal;
import com.nenood.reggie.service.CategoryService;
import com.nenood.reggie.service.DishService;
import com.nenood.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    // 分页查询套餐
    @GetMapping("page")
    public R<Page<SetmealDto>> page(@RequestParam("page") int page,
                                    @RequestParam("pageSize") int pageSize,
                                    @RequestParam(value = "name", required = false) String name) {

        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(setmealPage, queryWrapper);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        List<SetmealDto> setmealDtoList =
                setmealPage.getRecords().stream().map((setmeal) -> {
                    SetmealDto setmealDto = new SetmealDto();
                    BeanUtils.copyProperties(setmeal, setmealDto);

                    Long categoryId = setmeal.getCategoryId();
                    if (categoryId != null) {
                        String categoryName = categoryService.getById(categoryId).getName();
                        setmealDto.setCategoryName(categoryName);
                    }

                    return setmealDto;
                }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    // 新增套餐
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐“{}”...", setmealDto.getName());
        setmealService.saveSetmeal(setmealDto);
        log.info("新增套餐成功");
        return R.success("新增套餐成功！");
    }

    // 根据套餐id查询
    @GetMapping("{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id) {
        return setmealService.getSetmeal(id);
    }

    // 根据分类id查询
    @GetMapping("list")
    public R<List<Setmeal>> listSetmeal(Setmeal setmeal) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        return R.success(setmealService.list(queryWrapper));
    }

    // 删除套餐
    @DeleteMapping
    public R<String> deleteSetmeals(@RequestParam("ids") String ids) {
        String[] idArray = ids.split(",");
        List<Long> setmealIds = Arrays.stream(idArray).map(Long::parseLong).collect(Collectors.toList());
        setmealService.deleteSetmeals(setmealIds);
        return R.success("删除套餐成功！");
    }

    // 修改套餐信息
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto) {
        log.info("修改套餐“{}”...", setmealDto.getName());
        setmealService.updateSetmeal(setmealDto);
        log.info("修改套餐成功");
        return R.success("修改套餐成功！");
    }

    // 查看菜品详情
    @GetMapping("dish/{id}")
    public R<Dish> dish(@PathVariable Long id) {
        return R.success(dishService.getById(id));
    }
}
