package com.mybatis.test.mapper;

import com.mybatis.test.bean.User;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mybatis.caches.redis.RedisCache;

@CacheNamespace(implementation = RedisCache.class)
public interface UserMapper {

  @Select("select * from user where id=${user.id}")
  User selectOne(@Param("user") User user);

  //#{} 会加替换为？ 防止注入；${} 字面值
  @Update("update user set name=#{user.name} where id=#{user.id}")
  void update(@Param("user") User user);
}
