####### SpringBoot与Shiro整合-权限管理实例
###### springboot整合Shiro实现用户认证
######1分析Shiro的核心APi
    三个核心的类
    Subject: 当前用户的主体(登录、注销、判断授权等等的一些方法)
    SecurityManager: 安全管理器
    Realm: Shiro连接数据的桥梁(操作数据库、获取用户信息)
    Subject(关联)->SecurityManager(关联)->Realm
    
######2导入整合依赖
    <!-- Spring对Shiro支持 -->
    <dependency>
                <groupId>org.apache.shiro</groupId>
                <artifactId>shiro-spring</artifactId>
                <version>1.4.0</version>
    </dependency>
    
######3自定义Realm类
    public class UserRealm  extends AuthorizingRealm
    {
   
       /**
        * 执行授权逻辑
        * @param principalCollection
        * @return
        */
       @Override
       protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
           System.out.println("执行授权逻辑");
           return null;
       }
   
       /**
        * 执行认证逻辑
        * @param authenticationToken
        * @return
        * @throws AuthenticationException
        */
       @Override
       protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
           System.out.println("执行认证逻辑");
           return null;
       }
       }
    
######4编写配置类
    @Configuration
    public class ShiroConfig
    {
        /**
         * 创建Realm(自定义Realm)
         * @return
         */
        @Bean
        public UserRealm userRealm(){
            return new UserRealm();
        }
    
        /**
         * 创建DefaultWebSecurityManager(安全管理器)
         * @param userRealm
         * @return
         */
        @Bean
        public SecurityManager securityManager(UserRealm userRealm){
            DefaultSecurityManager securityManager = new DefaultSecurityManager();
            //关联Realm
            securityManager.setRealm(userRealm);
            return securityManager;
        }
        /**
         * 创建ShiroFilterFactoryBean(Shiro过滤器配置)
         */
        @Bean
        public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager)
        {
            ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
            //设置安全管理器(关联 securityManager)
            shiroFilterFactoryBean.setSecurityManager(securityManager);
            return shiroFilterFactoryBean;
        }
    
    }
######4-1使用Shiro内置过滤器实现页面拦截
            /**
             * Shiro内置过滤器，可以实现权限相关的拦截器
             *  常用的过滤器
             *      anon：无需认证(登录)，可以访问
             *      authc：必须认证才可以访问
             *      user：如果使用rememberMe的功能可以直接访问
             *      perms：该资源必须得到资源权限才可以访问
             *      role：该资源必须得到角色资源才可以访问
             * */
            Map<String,String> filterMap = new LinkedHashMap<String,String>();
    /*        filterMap.put("/user/add","authc");
            filterMap.put("/user/update","authc");*/
            filterMap.put("/testThymeleaf","anon");
            filterMap.put("/*","authc");
            
######4-2实现用户认证(登录)操作
######4-2-1编写controller的登录逻辑
            @RequestMapping("/login")
            public String login(String name,String password,Model model){
                /**
                 * 使用Shiro编写认证操作
                 * */
                //1、获取subject
                Subject subject = SecurityUtils.getSubject();
                //2、封装用户数据
                UsernamePasswordToken token = new UsernamePasswordToken(name, password);
                //3、执行登录方法
                try {
                    subject.login(token);
                    //登录成功
                    //跳转到test.html
                    return "redirect:/testThymeleaf";
                } catch (UnknownAccountException e) {
                    //e.printStackTrace();
                    //登录失败!用户名不存在
                    model.addAttribute("msg","用户名不存在！！");
                    return "login";
                }catch (IncorrectCredentialsException e){
                    //e.printStackTrace();
                    //登录失败!密码错误
                    model.addAttribute("msg","密码错误！！");
                    return "login";
                }
            }     
             
######4-2-2编写Realm的判断逻辑
            protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
                System.out.println("执行认证逻辑");
                //设定用户名和密码假数据
                String name = "admin";
                String password = "123";
        
                //编写Shiro的判断逻辑,判断用户名和密码
                //1、判断用户名
                UsernamePasswordToken token =(UsernamePasswordToken)authenticationToken;
                if (!token.getUsername().equals(name)){
                    //用户名不存在
                    return null;//Shiro底层会抛出UnknownAccountException
                }
                //2、判断密码
                return new SimpleAuthenticationInfo("",password,"");
            }
      
######4-2实现用户授权操作
######4-2-1使用Shiro内置过滤拦截资源
    @Configuration
    public class ShiroConfig
    {
    .......
    //授权过滤器
    //注意：当前授权拦截后,Shiro会自动跳转到未授权页面
    filterMap.put("/add","perms[user:add]");
    filterMap.put("/update","perms[user:update]");
    //设置未授权页面
    shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");
    .......
    }
    
######4-2-2自定义Realm中的授权
            /**
             * 执行授权逻辑
             * @param principalCollection
             * @return
             */
            @Override
            protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
                System.out.println("执行授权逻辑");
        
                //给资源进行授权
                SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
                //到数据库中查询当前登录用户的授权字符串，再添加资源的授权字符串
                Subject subject = SecurityUtils.getSubject();
                User user = (User) subject.getPrincipal();
                User user1 = userService.findById(user.getId());
                info.addStringPermission(user1.getPerms());
                return info;
            }

######5 thymeleaf和shiro的整合
    1、再ShiroConfig类中添加ShiroDialect方法
        /**
         * thymeleaf模板引擎和shiro框架的整合
         */
        @Bean
        public ShiroDialect shiroDialect()
        {
            return new ShiroDialect();
        }
    2、在页面中添加shiro字段
        <!DOCTYPE html>
        <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:shiro="http://www.pollix.at/thymeleaf/shiro">
        <head>
            <meta charset="UTF-8">
            <title>测试 thymeleaf 的使用</title>
        </head>
        <body>
            <h1 th:text="${name}"></h1>
        <hr/>
        <div shiro:hasPermission="user:add">
        <h1>进入用户添加功能：<a href="/add">用户添加</a></h1><br/>
        </div>
        <div shiro:hasPermission="user:update">
        <h1>进入用户更新功能：<a href="/update">用户更新</a></h1><br/>
        </div>
        <h4><a href="/toLogin">登录</a></h4>
        </body>
        </html>