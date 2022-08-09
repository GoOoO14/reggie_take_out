package com.niu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.reggie.common.CustomException;
import com.niu.reggie.entity.Category;
import com.niu.reggie.entity.Dish;
import com.niu.reggie.entity.Setmeal;
import com.niu.reggie.mapper.CategoryMapper;
import com.niu.reggie.service.CategoryService;
import com.niu.reggie.service.DishService;
import com.niu.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     *
     * @param ids
     */
    @Override
    public void remove(Long ids) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：分类id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        // 查询当前分类是否关联了菜品，如果已经关联，则抛出一个业务异常
        if (count1 > 0) {
            // 已经关联菜品，则抛出一个业务异常
            throw new CustomException("当前分类关联菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count2 = setMealService.count(setmealLambdaQueryWrapper);
        // 查询当前分类是否关联了套餐，如果已经关联，则抛出一个业务异常
        if (count2 > 0) {
            // 已经关联套餐，则抛出一个业务异常
            throw new CustomException("当前分类关联套餐，不能删除");
        }

        // 都没有关联，删除分类
        super.removeById(ids);

    }
}
