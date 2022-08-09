package com.niu.reggie.common;

/**
 * 基于ThreadLocal风窗工具类，用于保存和获取当前用户id
 * @author mac
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }


}
