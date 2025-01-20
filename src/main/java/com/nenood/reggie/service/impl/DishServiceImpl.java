package com.nenood.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nenood.reggie.common.R;
import com.nenood.reggie.dto.DishDto;
import com.nenood.reggie.entity.Dish;
import com.nenood.reggie.entity.DishFlavor;
import com.nenood.reggie.mapper.DishMapper;
import com.nenood.reggie.service.CategoryService;
import com.nenood.reggie.service.DishFlavorService;
import com.nenood.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl
        extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 存入基本信息，Springboot会自动忽略冗余内容
        this.save(dishDto);

        // 获取菜品的ID
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((flavor -> {
            flavor.setDishId(dishId);
            return flavor;
        })).collect(Collectors.toList());
//        flavors.forEach((flavor) -> {
//            flavor.setDishId(dishId);
//        });

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public R<DishDto> getDishDto(Long dishId) {

        DishDto dishDto = new DishDto();

        Dish dish = this.getById(dishId);
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishDto.setFlavors(dishFlavorService.list(queryWrapper));

        Long categoryId = dish.getCategoryId();
        dishDto.setCategoryName(categoryService.getById(categoryId).getName());

        return R.success(dishDto);
    }

    @Override
    @Transactional
    public void update(DishDto dishDto) {

        // 更新菜品的基本信息
        this.updateById(dishDto);

        // 将原先菜品的所有口味信息清除
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        // 将新的口味插入
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((flavor) -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void delete(List<Long> dishIds) {

        // 删除dish_flavor表内的信息
        dishIds.forEach(dishId -> {
            dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dishId));
        });

        // 删除dish表内的信息
        this.removeByIds(dishIds);
    }

}
