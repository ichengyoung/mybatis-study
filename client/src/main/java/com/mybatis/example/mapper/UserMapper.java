package com.mybatis.example.mapper;

import com.mybatis.example.bean.User;
import java.util.List;

public interface UserMapper {

  List<User> selectList();

  User selectOne(User param);

  int update(User param);

  void delete(User param);

  User add(User param);

}
