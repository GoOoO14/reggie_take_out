package com.niu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.reggie.dto.SetmealDto;
import com.niu.reggie.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {

    /**
     * 套餐基本信息和关联信息保存
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐和菜品的关联关系
     * @param ids
     */
    void removeWithDish(List<Long> ids);
}
