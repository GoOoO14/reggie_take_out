package com.niu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.reggie.entity.Category;
import com.niu.reggie.entity.Employee;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
