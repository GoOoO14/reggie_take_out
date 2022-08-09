package com.niu.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局的异常处理器
 * xannotations :注解   即有后面类型的注解都会被拦截
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody           // 我们会返回一些 json 数据
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * 表示他处理             这种      异常
     * 即只要有这种异常，都会统一到这个方法来处理
     *
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            // ex.getMessage()： Duplicate entry 'zhangsan' for key 'employee.idx_username'
            String[] split = ex.getMessage().split(" ");

            //          'zhangsan'
            String msg = split[2] + " 已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 全局的异常处理器来处理我们自定义的业务异常
     * 这样子提示信息就可以在前端页面展示了
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
