package com.niu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.reggie.entity.DishFlavor;
import com.niu.reggie.mapper.DishFlavorMapper;
import com.niu.reggie.service.DishFlacorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class DishFlacorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlacorService {
}
