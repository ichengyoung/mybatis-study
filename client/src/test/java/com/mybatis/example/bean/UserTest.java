package com.mybatis.example.bean;

import com.mybatis.example.mapper.UserMapper;
import com.mybatis.sqlsession.SqlSession;
import com.mybatis.sqlsession.SqlSessionFactory;
import com.mybatis.sqlsession.SqlSessionFactoryBuilder;
import org.junit.Test;

public class UserTest {

  @Test
  public void name() throws Exception {
    SqlSessionFactory sqlSessionFactory = SqlSessionFactoryBuilder.build("sqlMapConfigure.xml");
    SqlSession sqlSession = sqlSessionFactory.openSession();

    User user = new User();
    user.setId(3L);
    user.setName("mazi");


    /*User user1 = sqlSession.selectOne("com.mybatis.example.mapper.UserMapper.selectOne", user);
    System.out.println(user1);*/

   /* List<Object> objects = sqlSession.selectList("com.mybatis.example.mapper.UserMapper.selectList");
    System.out.println(objects);*/

    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    /*
    User user1 = mapper.selectOne(user);
    System.out.println(user1);*/

//    mapper.delete(user);
    mapper.add(user);
  }

}