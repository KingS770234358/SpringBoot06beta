Shiro

1.1 什么是Shiro
·Apache Shiro是一个java安全权限的框架
·可以在Java SE和 JavaEE容易的开发出足够好的应用
·Shiro可以完成[认证、授权、加密、会话管理、web集成、缓存等]
·下载地址 http://shiro.apache.org/
 github地址: github.com/apache/shiro
 
1.2快速开始
· pom.xml中导入依赖
· 编写配置文件shiro.ini
· Quickstart.java
2020-02-03 16:37:42,530 INFO [org.apache.shiro.session.mgt.AbstractValidatingSessionManager] - Enabling session validation scheduler... 
2020-02-03 16:37:42,820 INFO [Quickstart] - Retrieved the correct value! [aValue] 
2020-02-03 16:37:42,823 INFO [Quickstart] - User [lonestarr] logged in successfully. 
2020-02-03 16:37:42,823 INFO [Quickstart] - May the Schwartz be with you! 
2020-02-03 16:37:42,824 INFO [Quickstart] - You may use a lightsaber ring.  Use it wisely. 
2020-02-03 16:37:42,824 INFO [Quickstart] - You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  Here are the keys - have fun! 
===>成功打印出日志信息
·注意点:pom.xml文件中导入依赖的时候 日志门面scope设置成runtime test会报错
[默认使用的日志门面 commos-logging(需要在pom.xml中导入相应的包才行)]

1.3开始读代码和配置Quickstart.java=====[Subject对象的大部分方法-----类似SpringSecurity]
//1.获得当前执行用户对象Subject:###########################################
Subject currentUser = SecurityUtils.getSubject();
//2.通过当前用户使用shiro的session#(而不是http session)############
Session session = currentUser.getSession();
//3.登录当前用户 角色 和 权限 是否被认证!###########################################
currentUser.isAuthenticated()
//4.输出身份信息 (一个用户名): 
currentUser.getPrincipal()
//5.测试当前用户是否有某个角色:
currentUser.hasRole("schwartz")
//6.测试用户是否有以下权限 (非实例级) 是否有lightsabe:wield权限  [粗粒度-权限更多]
currentUser.isPermitted("lightsaber:wield")
//7.测试用户是否有winnebago:drive:eagle5权限  [细粒度-权限更少]
currentUser.isPermitted("winnebago:drive:eagle5")
//8.结束退出!
currentUser.logout();

2.如何在SpringBoot中集成
2.1 先跑通一个项目
## 回忆Shiro核心三大对象
· Subject 用户
· SecurityManager 管理所有用户
· Realm 访问数据
2.2[pom.xml中导入shiro整合Spring的包]
Spring整合Shiro的依赖
dependency
    groupId org.apache.shiro  groupId 
    artifactId shiro-spring  artifactId 
    version 1.5.0 version 
dependency 
2.3[编写Config配置类 UserRealm.java ShiroConfig.java]
编写认证授权的Realm USerRealm.java 就相当于之前的shiro.ini 相当于SpringSecurity的Config
## 知识点: 将Spring容器里的bean当做参数传入一个函数
## public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("getUserRealm") UserRealm userRealm){...}
## @Bean // 自己写的Realm类给spring托管
## public UserRealm getUserRealm(){
##     return new UserRealm();
## }
主要是配置userRealm,然后[通用基本配置]userRealm传给安全管理器defaultWebSecurityManager设置Realm,
然后安全管理器传defaultWebSecurityManager给ShiroFilterFactoryBean设置安全管理器
defaultWebSecurityManager.setRealm(userRealm);
shiroFilterFactoryBean.setSecurityManager(defaultWSM);
// 1.2添加shiro的内置过滤器=====[登录拦截]
/**
 * anon: 无需认证就可以访问
 * authc: 必须认证了才能访问
 * user: 必须拥有记住我功能才能使用
 * perms: 拥有对某个资源的权限才能访问
 * role: 拥有某个角色的权限才能访问
 */
Map<String, String> filterMap = new LinkedHashMap<String, String>();
// /user/add 这个URL可以被所有人访问  ==== 这里支持通配符filterMap.put("/user/*", "authc");
filterMap.put("/user/add", "anon");
// 要认证过才能访问 /user/update
filterMap.put("/user/update", "authc");
// 设置过滤器链式定义Map
shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
// 没有权限的情况下发起跳转至的登录页面的请求
shiroFilterFactoryBean.setLoginUrl("/toLogin");
2.4[在UserRealm.java中进行认证授权]
2.4.1认证

2.4编写增加和更新用户页面 及其 控制其进行测试  

2.5.shiro整合mybatis
//================连接真实数据库(看不到明文密码了)=======================
UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
User user = userService.queryUserByName(userToken.getUsername());
if(user==null){
    //说明用户不存在
    return null; // UnknownAccountException
}
// 密码认证 (密码可以加密:MD5 MD5盐值加密(加上username))===返回简单的认证信息对象
// 重写父类的方法 就可以实现自己的加密方式===返回成功 用户即为认证成功对象拥有 authc
// [在这里要把这个用户放入 简单认证信息对象 交给授权处理 
(其他需要使用的时候 也可以Subject currSubject = SecurityUtils.getSubject();获取)]
return new SimpleAuthenticationInfo(user,user.getPwd(),"");
连通数据库之后 在UserRealm中使用
UserRealm 继承 AuthorizingRealm 继承 AuthenticatingRealm 
有方法 返回一个     CredentialsMatcher 接口  实现加密操作
public CredentialsMatcher getCredentialsMatcher() {
   return credentialsMatcher;
}

2.6 授权
// · 授权 只有拥有权限user:add用户才可以访问 /user/add页面 没有权限的用户访问报错401
ShiroConfig全局设置
filterMap.put("/user/add", "perms[user:add]");
filterMap.put("/user/update", "perms[user:update]");
// 设置过滤器链式定义Map
shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
// 没有授权的情况下跳转至的页面
shiroFilterFactoryBean.setUnauthorizedUrl("/user/unauthor");
// 未授权页面
@RequestMapping("/user/unauthor")
@ResponseBody
public String unauthor(){
    return "该用户未授权!";
}
UserRealm设置
System.out.println("执行授权方法");
// 使用简单的授权信息对象
SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
// 应该要从数据库中获取当前用户的权限信息===>1.首先要想办法拿到当前用户
// 这里获取的Subject存储着 认证的时候 认证模块放入的user对象
Subject currSubject = SecurityUtils.getSubject();
User currUser = (User)currSubject.getPrincipal(); //这样就拿到了user对象
// 获取当前用户本身具有的权限   设置ta的权限
simpleAuthorizationInfo.addStringPermission(currUser.getPerm());
return simpleAuthorizationInfo;

2.7 shiro整合thymeleaf
· 导入pom.xml依赖
<!-- https://mvnrepository.com/artifact/com.github.theborakompanioni/thymeleaf-extras-shiro -->
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>thymeleaf-extras-shiro</artifactId>
    <version>2.0.0</version>
</dependency>
· ShiroConfig中整合Thymeleaf Shiro
@Bean // 注入Spring容器
public ShiroDialect getShiroDialect(){
    return new  ShiroDialect();
}
· 在index标签使用Shiro方言 控制板块的呈现
<div shiro:hasPermission="user:add">
    <a th:href="@{/user/add}">add</a>
</div>
前端使用Shiro的session
<p th:if="${session.loginUser==null}">
    <a th:href="@{/toLogin}">登录</a>
</p>
### 去码云上多看看别人的源码如何实现认证授权这一块(几百行都是正常的)

2.8 logout