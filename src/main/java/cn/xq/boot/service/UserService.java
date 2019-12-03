package cn.xq.boot.service;

import cn.xq.boot.domain.User;

/**
 * 功能描述
 * user Servicre 接口
 * @author xieqiong
 * @date 2019/12/2
 */
public interface UserService {

    public User findByName(String name);
    public User findById(Integer id);
}
