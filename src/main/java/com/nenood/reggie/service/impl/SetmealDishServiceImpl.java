package com.nenood.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nenood.reggie.entity.SetmealDish;
import com.nenood.reggie.mapper.SetmealDishMapper;
import com.nenood.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl
        extends ServiceImpl<SetmealDishMapper, SetmealDish>
        implements SetmealDishService {
}
