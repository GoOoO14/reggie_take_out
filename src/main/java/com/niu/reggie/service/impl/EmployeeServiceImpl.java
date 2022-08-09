package com.niu.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.reggie.entity.Employee;
import com.niu.reggie.mapper.EmployeeMapper;
import com.niu.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service            //        需要指定两个范型      继承     Mapper  和    实体      实现         service借口
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
