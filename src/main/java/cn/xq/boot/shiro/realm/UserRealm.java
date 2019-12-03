package cn.xq.boot.shiro.realm;

import cn.xq.boot.domain.User;
import cn.xq.boot.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 功能描述
 * 自定义Realm 处理登录 权限
 * @author xieqiong
 * @date 2019/12/1
 */
public class UserRealm  extends AuthorizingRealm
{

    @Autowired
    private UserService userService;
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

    /**
     * 执行认证逻辑
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");

        //编写Shiro的判断逻辑,判断用户名和密码
        //1、判断用户名
        UsernamePasswordToken token =(UsernamePasswordToken)authenticationToken;
        User user = userService.findByName(token.getUsername());
        if (user == null){
            //用户名不存在
            return null;//Shiro底层会抛出UnknownAccountException
        }
        //2、判断密码
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }
}
