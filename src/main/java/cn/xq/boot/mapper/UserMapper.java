package cn.xq.boot.mapper;

import cn.xq.boot.domain.User;

/**
 * 功能描述
 * 编写UserMapper接口
 * @author xieqiong
 * @date 2019/12/2
 */
public interface UserMapper {

    public User findByName(String name);
    public User findById(Integer id);
}
