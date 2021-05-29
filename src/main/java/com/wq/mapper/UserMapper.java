package com.wq.mapper;

import com.wq.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/***
 * 数据库的Component @Repository
 */
@Repository
@Mapper
public interface UserMapper {
    public User queryUserByName(@Param("userName") String name);
}
