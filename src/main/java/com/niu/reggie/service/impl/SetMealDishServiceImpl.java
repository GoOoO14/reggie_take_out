package com.niu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.reggie.dto.SetmealDto;
import com.niu.reggie.entity.SetmealDish;
import com.niu.reggie.mapper.SetMealDishMapper;
import com.niu.reggie.service.SetMealDishService;
import com.niu.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetmealDish> implements SetMealDishService {


}
