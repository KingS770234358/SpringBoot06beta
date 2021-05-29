package com.wq.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {
    // 测试页面
    @RequestMapping({"admin","admin/","admin/index","admin/index/","admin/index.html"})
    public String toIndex(Model model){
        model.addAttribute("msg","Hello Admin");
        return "index";
    }
}
