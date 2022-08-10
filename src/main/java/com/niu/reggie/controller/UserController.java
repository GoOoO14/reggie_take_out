package com.niu.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.reggie.common.R;
import com.niu.reggie.entity.User;
import com.niu.reggie.service.UserService;
import com.niu.reggie.utils.SMSUtils;
import com.niu.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            // 生成 4位 随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}", code);

            // 调用阿里云提供的短信服务API发送短信
            SMSUtils.sendMessage("阿里云短信测试", "阿里云短信测试", phone, code);


            // 需要将生成的验证码保存到Session
//            session.setAttribute(phone, code);

            // 将生成的验证码缓存到Redis中,并且设置有效期5mins
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("手机验证码发送成功");

        }
        return R.error("短信验证码发送失败");
    }

    /**
     * 移动端用户登录
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        // 获取手机号
        String phone = map.get("phone").toString();

        // 获取验证码
        String code = map.get("code").toString();

        // session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        // 从redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        // 验证码比较，页面提交的和session保存的
        if (codeInSession != null && codeInSession.equals(code)){
            // 比对成功，则登录成功
            // 当前手机号对应的用户是否为新用户，是新用户就自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);

            if (user == null){
                // 新用户==》 就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);

                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            // 如果登录成功,则删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/logout")
    public R<String> loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}



























