package com.nenood.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nenood.reggie.common.R;
import com.nenood.reggie.dto.DishDto;
import com.nenood.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    R<DishDto> getDishDto(Long dishId);

    void update(DishDto dishDto);

    void delete(List<Long> dishIds);
}
