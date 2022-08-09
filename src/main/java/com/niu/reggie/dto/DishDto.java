package com.niu.reggie.dto;

import com.niu.reggie.entity.Dish;
import com.niu.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于展示层和服务层之间的数据传输
 */
@Data
public class DishDto extends Dish {

    // 扩展了 flavors 属性，具体扩展什么属性，都是前端页面返回的数据 决定的
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
