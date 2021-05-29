package com.wq.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro的配置
 */
@Configuration
public class ShiroConfig {

    // 4.整合Shirodialect:Shiro整合Thymeleaf ===>这样前端才能使用Shiro的一些方法
    @Bean
    public ShiroDialect getShiroDialect(){
        return new  ShiroDialect();
    }

    // 1.ShiroFilterFactoryBean Shiro过滤器工厂对象
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("defaultWSM") DefaultWebSecurityManager defaultWSM){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 中间这部分如何设置 直接点进ShiroFilterFactoryBean源码查看有哪些属性需要配置就知道了
        // 1.1设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWSM);
        /**
         * anon: 无需认证就可以访问
         * authc: 必须认证了才能访问
         * user: 必须拥有记住我功能才能使用
         * perms: 拥有对某个资源的权限才能访问
         * role: 拥有某个角色的权限才能访问
         */
        // 1.2 全局设置
        // · shiro的内置全局拦截
        Map<String, String> filterMap = new LinkedHashMap<String, String>();
        // /user/add 这个URL可以被所有人访问
        filterMap.put("/user/add", "anon");
        // 要认证过才能访问 /user/update
        filterMap.put("/user/update", "authc");
        // · shiro的内置全局授权 只有拥有权限user:add用户才可以访问 /user/add页面 没有权限的用户访问报错401
        filterMap.put("/user/add", "perms[user:add]");
        filterMap.put("/user/update", "perms[user:update]");
        // 设置过滤器链式定义Map
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        // 没有过滤成功的情况下发起跳转至登录页面的请求
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        // 没有授权的情况下跳转至的页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/user/unauthor");
        return shiroFilterFactoryBean;
    }


    // 2.DefaultWebSecurityManager安全对象
    @Bean(name = "defaultWSM")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("getUserRealm") UserRealm userRealm){
        // 2.1 创建安全对象实例
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        // @Autowired   // 无法使用诸如的方式传入userRealm
        // UserRealm userRealm;
        // 2.2安全对象关联realm
        defaultWebSecurityManager.setRealm(userRealm);
        return defaultWebSecurityManager;
    }

    // 3.创建realm对象  需要自定义(这里自定义UserRealm.java)--认证 授权
    @Bean // 自己写的Realm类给spring托管  @Bean(name="xxx") 上面的函数@Qualifier("xxx")就要这样引用了
    public UserRealm getUserRealm(){
        return new UserRealm();
    }



}
