package com.nenood.reggie.dto;

import com.nenood.reggie.entity.Setmeal;
import com.nenood.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
