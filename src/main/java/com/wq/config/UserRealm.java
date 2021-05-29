package com.wq.config;

import com.wq.pojo.User;
import com.wq.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Security;

/***
 * 自定义的Realm对象
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    // 1.授权 给用户
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权方法");
        // 使用简单的授权信息对象
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        // 给所有用户赋予 user:add 的权限
//        System.out.println("给所有用户赋予 user:add 的权限");
//        simpleAuthorizationInfo.addStringPermission("user:add");

        // 应该要从数据库中获取当前用户的权限信息===>1.首先要想办法拿到当前用户
        // 这里获取的Subject存储着 认证的时候 认证模块放入的user对象
        Subject currSubject = SecurityUtils.getSubject();
        User currUser = (User)currSubject.getPrincipal(); //这样就拿到了user对象
        // 获取当前用户本身具有的权限   设置ta的权限
        simpleAuthorizationInfo.addStringPermission(currUser.getPerm());
        return simpleAuthorizationInfo;
    }

    // 2.认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证方法");
//        // 2.1 获取当前用户 (参考Quickstart)
//        Subject subject = SecurityUtils.getSubject();
//        // 2.2 封装用户的登录数据
//        new UsernamePasswordToken();
        // 设置当前用户的 用户名 和 密码
//        String name = "root";
//        String password = "123456";
//        // 取得传入的UsernamePasswordToken
//        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
//        // 开始认证 调用登录方法
//        if(!name.equals(userToken.getUsername())){
//            // 用户名不一致 返回空 Controller里就会抛出 用户名未知的异常
//            return null;
//        }
//        // 密码认证 ====
//        // 看到当前这个方法的返回值是AuthenticationInfo 是一个接口
//        // 返回它的实现类就对了 这里返回 SimpleAuthenticationInfo 参数1:当前用户的认证(用户名) 2:密码 3:认证名
//        // 这方法目前只检查密码对不对 不验证用户名对不对 用户名还是需要靠上面的方法判断
//        return new SimpleAuthenticationInfo("", password,"");


        //================连接真实数据库(看不到明文密码了)=======================
        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
        User user = userService.queryUserByName(userToken.getUsername());
        if(user==null){
            //说明用户不存在
            return null; // UnknownAccountException
        }
        // 密码认证 (密码可以加密:MD5 MD5盐值加密(加上username))===返回简单的认证信息对象
        // 重写父类的方法 就可以实现自己的加密方式===返回成功 用户即为认证成功对象拥有 authc
        // 在这里要把这个用户放入 简单认证信息对象 交给授权处理(其他需要使用的时候 也可以获取)
        return new SimpleAuthenticationInfo(user,user.getPwd(),"");

    }
}
