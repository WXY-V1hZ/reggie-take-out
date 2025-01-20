package com.nenood.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nenood.reggie.common.R;
import com.nenood.reggie.dto.SetmealDto;
import com.nenood.reggie.entity.Setmeal;
import com.nenood.reggie.entity.SetmealDish;
import com.nenood.reggie.mapper.SetmealMapper;
import com.nenood.reggie.service.SetmealDishService;
import com.nenood.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl
        extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    SetmealService setmealService;

    @Override
    @Transactional
    public void saveSetmeal(SetmealDto setmealDto) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        setmealService.save(setmeal);

        List<SetmealDish> setmealDishes =
                setmealDto.getSetmealDishes().stream().map((setmealDish) -> {
                    setmealDish.setSetmealId(setmeal.getId());
                    return setmealDish;
                }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public R<SetmealDto> getSetmeal(Long id) {

        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = setmealService.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());

        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishes);

        return R.success(setmealDto);
    }

    // 删除套餐
    @Override
    @Transactional
    public void deleteSetmeals(List<Long> setmealIds) {

        // 删除setmeal_dish中的数据
        setmealIds.forEach(setmealId -> {
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
            setmealDishService.remove(queryWrapper);
        });

        // 删除setmeal中的数据
        setmealService.removeByIds(setmealIds);
    }

    // 修改套餐
    @Override
    @Transactional
    public void updateSetmeal(SetmealDto setmealDto) {

        // 更新基本信息
        this.updateById(setmealDto);

        // 更新菜品列表信息，先删除旧列表，在添加新列表
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((setmealDish) -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
