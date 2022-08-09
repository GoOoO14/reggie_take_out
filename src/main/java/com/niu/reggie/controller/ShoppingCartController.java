package com.niu.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.reggie.common.BaseContext;
import com.niu.reggie.common.R;
import com.niu.reggie.entity.ShoppingCart;
import com.niu.reggie.service.ShoppingCartService;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import org.yaml.snakeyaml.events.Event;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据: {}", shoppingCart);

        // 设置用户id， 指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null) {
            // 添加到购物车的菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            // 添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        }

        // 查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne != null){
            // 如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);

            shoppingCartService.updateById(cartServiceOne);
        } else {
            // 如果不存在，则添加到购物车，数量默认就是1
            shoppingCart.setNumber(1);

            // 注意添加位置，这里需要在save之前添加时间
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartService.save(shoppingCart);

            // 为了方便返回
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    /**
     * 减少菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据: {}", shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询当前商品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 根据登录用户的 userId去ShoppingCart表中查询该用户的购物车数据
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        if (setmealId != null){

            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }else {
            // 添加进购物车的是菜品，且 购物车中已经添加过 该菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }

        ShoppingCart oneCart = shoppingCartService.getOne(queryWrapper);
        Integer number1 = oneCart.getNumber();

        if (number1 == 1){
            shoppingCartService.remove(queryWrapper);
        } else {
            oneCart.setNumber(number1 -1 );
            shoppingCartService.updateById(oneCart);
        }
        return R.success(oneCart);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车。。。");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        // 添加时间生序排列
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功！");
    }
}
