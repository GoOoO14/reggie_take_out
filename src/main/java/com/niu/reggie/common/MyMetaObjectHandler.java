package com.niu.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 自定义的元数据对象处理器
 *
 * @author mac
 */

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    // 插入操作自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充【insert】。。。");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        /**
         * 这里我们无法通过：HttpServletRequest request的
         * request.getSession().getAttribute("employee");
         * 来获取session
         *
         * 客户端每次发送的http请求，对应的在服务端都会分配一个新的线程来处理，在处理过程中涉及到的下面类的方法都属于同一个线程
         * LoginCheckFilter 的 doFilter
         * EmployeeController 的 update
         * MyMetaObjectHandler 的 updateFill
         *
         * 所以我们换个方法：
         *
         */
        metaObject.setValue("createUser", BaseContext.getCurrentId());      // 先写死一下
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新操作自动填充
        log.info("公共字段自动填充【update】。。。");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }
}
