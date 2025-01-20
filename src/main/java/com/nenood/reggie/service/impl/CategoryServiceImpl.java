package com.nenood.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nenood.reggie.common.CustomException;
import com.nenood.reggie.entity.Category;
import com.nenood.reggie.entity.Dish;
import com.nenood.reggie.entity.Setmeal;
import com.nenood.reggie.mapper.CategoryMapper;
import com.nenood.reggie.service.CategoryService;
import com.nenood.reggie.service.DishService;
import com.nenood.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl
        extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void delete(Long id) {
        log.info("删除分类{}...", id);

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<Dish>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);

        if (dishCount > 0) {
            throw new CustomException("当前分类与菜品关联, 删除失败.");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);

        if (setmealCount > 0) {
            throw new CustomException("当前分类与套餐关联, 删除失败.");
        }

        super.removeById(id);
    }
}
