package com.niu.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.reggie.common.R;
import com.niu.reggie.entity.Employee;
import com.niu.reggie.service.EmployeeService;
import jdk.jfr.Category;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    /**
     * 自动注入一下下
     */

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // request的作用： 登录成功后把 员工对象的 id 存到 session 中一份，之后就很方便获取当前登陆用户

        /**
         * 1。将页面提交的密码password进行md5加密
         * 2。 根据页面提交的用户名username查询数据库
         * 3。如果没有查询到，则返回登录失败
         * 4。密码对比，如果不一致则返回登录失败
         * 5。查看员工状态，如果为已禁用，则返回：员工已禁用
         * 6。登录成功，将员工id存入Session并返回登录成功结果
         *
         */
        // 1。将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        // password：明文处理后的密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2。根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        // 表设计的：id唯一，所以用 getOne 即可
        Employee emp = employeeService.getOne(queryWrapper);

        // 3。如果没有查询到，则返回登录失败
        if (emp == null){
            return R.error("账户名或密码错误!");
        }

        // 4。密码对比，如果不一致则返回登录失败
        //       数据库的             用户输入后加密的
        if (! emp.getPassword().equals(password)){
            return R.error("账户名或密码错误!");
        }

        // 5。查看员工状态，如果为已禁用，则返回：员工已禁用
        if (emp.getStatus() == 0 ){
            // 员工被禁用
            return R.error("账号已禁用!");
        }

        // 6。登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);

    }


    /**
     * 员工退出
     * @param request
     * @return
     *
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // request 对象 是方便操作session

        // 1.清理Session中保存的房前登录员工的 id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping                                     // 接受的是 json 数据
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息: {}",employee.toString());


        // 新增员工我们统一给一个 初始密码；123456 需要MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//         获得当前登录用户的 id
//        Long empId= (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);


        return R.success("新增员工成功");
    }

    /**
     * 分页查询
     * 注意范型是 Page 这是因为页面需要的 json数据在Page中才会提供
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")            // 这里需要前端返回json的数据的名字和你接口定义的变量名一样
    public R<Page> page(int page, int pageSize,String name){
        log.info("page={}, pageSize={}, name={}",page,pageSize,name);

        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条卷构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 添加一个过滤条件      这里我们用 like（模糊查询）

        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName,name);
        // 添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功！");
    }


    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")        // 意思是 id 是在我们的请求路径里面
    public R<Employee> getById(@PathVariable Long id){

        log.info("根据id查询员工信息。。。");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }



}
