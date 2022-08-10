package com.niu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.reggie.common.CustomException;
import com.niu.reggie.dto.DishDto;
import com.niu.reggie.entity.Category;
import com.niu.reggie.entity.Dish;
import com.niu.reggie.entity.DishFlavor;
import com.niu.reggie.entity.Setmeal;
import com.niu.reggie.mapper.DishMapper;
import com.niu.reggie.service.CategoryService;
import com.niu.reggie.service.DishFlacorService;
import com.niu.reggie.service.DishService;
import com.niu.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlacorService dishFlacorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private DishMapper dishMapper;


    /**
     * 新增菜品，同时插入菜品对应的口味数据,即操作 dish   dish_flavor 表
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        // 我们 dishDto.getFlavors() 获得的只有名字和，口味，dishId 没有！，所以我们要手动把id加上去
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到菜品口味表 dish_flavor
        dishFlacorService.saveBatch(flavors);

    }



    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询讯菜品基本信息， dish表查
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询当前菜品对应口味信息，从 dish_flavor 表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlacorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }


    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 更新 dish 表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应口味数据  -- dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlacorService.remove(queryWrapper);
//        dishFlacorService.


        // 添加当前提交过来的口味数据 -- dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 这样子后 falvors里面没有 dishId

        flavors = flavors.stream().map((item) -> {
            // 再把id都手动加上去
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());


        dishFlacorService.saveBatch(flavors);
    }


    /**
     * 根据id删除菜品，删除之前需要进行判断
     *
     * @param ids
     */
    @Override
    public void remove(List<Long> ids) {

        /*
       菜品关联分类无所谓把，菜品本就在分类下面
         */

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：分类id
        dishLambdaQueryWrapper.in(Dish::getId, ids);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        // 查询当前状态
        if (count1 > 0) {
            // 在售，则抛出一个业务异常
            throw new CustomException("当前菜品正在售卖中，不能删除");
        }


        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getCategoryId, ids);

        int count2 = setMealService.count(setmealLambdaQueryWrapper);
        // 查询当前菜品是否关联了套餐，如果已经关联，则抛出一个业务异常
        if (count2 > 0) {
            // 已经关联套餐，则抛出一个业务异常
            throw new CustomException("当前菜品关联套餐，不能删除");
        }

        // 都没有关联，删除菜品
        dishMapper.deleteBatchIds(ids);
//        dishMapper.deleteById(ids);


    }
}
