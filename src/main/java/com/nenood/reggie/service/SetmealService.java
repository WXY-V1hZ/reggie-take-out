package com.nenood.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nenood.reggie.common.R;
import com.nenood.reggie.dto.SetmealDto;
import com.nenood.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveSetmeal(SetmealDto setmealDto);

    R<SetmealDto> getSetmeal(Long id);

    void updateSetmeal(SetmealDto setmealDto);

    void deleteSetmeals(List<Long> setmealIds);
}
