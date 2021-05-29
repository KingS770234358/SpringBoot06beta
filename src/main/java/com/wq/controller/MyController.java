package com.wq.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    // 测试页面
    @RequestMapping({"","/","/index","/index/","index.html"})
    public String toIndex(Model model){
        model.addAttribute("msg","Hello shiro");
        return "index";
    }

    // 调到登录页面
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }
    // 登录
    @RequestMapping("/login")
    public String login(String username, String password, Model model){
        System.out.println("登录控制器");
        // 2.1 获取当前用户 (参考Quickstart)
        Subject subject = SecurityUtils.getSubject();
        // 2.2 封装用户的登录数据
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        try{
            // 这里调用 认证方法 没有异常说明登录成功
            subject.login(usernamePasswordToken);
            // 登录成功将信息存入Shiro的session方便前端使用
            Session session = subject.getSession();
            session.setAttribute("loginUser",username);
            // 登录成功进入主页面
            return "index";
        }catch (UnknownAccountException e){
            // 账号不正确 返回一些错误信息
            System.out.println("用户名错误");
            model.addAttribute("msg","用户名错误!");
            // 登录失败 停留在登录页面
            return "login";
        }catch (IncorrectCredentialsException e){
            System.out.println("密码不正确");
            model.addAttribute("msg", "密码不正确");
            // 登录失败 停留在登录页面
            return "login";
        }
    }
    // 注销
    @RequestMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/index";
    }

    // 到添加页面
    @RequestMapping("/user/add")
    public String add(){
         return "user/add";
    }

    // 到更新页面
    @RequestMapping("/user/update")
    public String update(){
        return "user/update";
    }

    // 未授权页面
    @RequestMapping("/user/unauthor")
    @ResponseBody
    public String unauthor(){
        return "该用户未授权!";
    }

}
