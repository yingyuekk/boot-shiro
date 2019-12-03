package cn.xq.boot;

import cn.xq.boot.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 功能描述
 * 测试
 * @author xieqiong
 * @date 2019/12/1
 */
@Controller
public class TestController
{
    /**
     * controller测试
     * */
    @RequestMapping("/test")
    @ResponseBody
    public String hello(){
        System.out.println("TestController.hello()");
        return "hello Spring boot!!!!";
    }
    /**
     * 测试 thymeleaf 的使用
     * */
    @RequestMapping("/testThymeleaf")
    public String testThymeleaf(Model model){
        System.out.println("TestController.testThymeleaf()");
        model.addAttribute("name","测试 thymeleaf 的使用");
        return "test";
    }

    /** 测试user需求 */

    @RequestMapping("/add")
    public String add(){
        return "user/add";
    }

    @RequestMapping("/update")
    public String update(){
        return "user/update";
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/noAuth")
    public String noAuth(){
        return "noAuth";
    }

    /**
     * 登录逻辑处理
     * @param name
     * @param password
     * @return
     */
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
}
