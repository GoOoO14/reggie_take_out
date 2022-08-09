package com.niu.reggie.dto;

import com.niu.reggie.entity.Setmeal;
import com.niu.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
