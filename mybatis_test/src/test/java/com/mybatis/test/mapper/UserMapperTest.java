package com.mybatis.test.mapper;

import com.mybatis.test.bean.User;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

public class UserMapperTest {

  private SqlSession sqlSession;
  private SqlSessionFactory sqlSessionFactory;

  @Before
  public void before() throws Exception {
    String resource = "sqlMapConfigure.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    sqlSession = sqlSessionFactory.openSession(false);
  }

  @Test
  public void testMapper() {
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);

    User param = new User() {{
      setId(1L);
    }};

    User user = mapper.selectOne(param);

    System.out.println(user);

  }

  @Test
  public void testOneLevelCache() {
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);

    User param = new User() {{
      setId(1L);
      setName("lisis");
    }};

    User user = mapper.selectOne(param);

    //进行更新操作 也会清空缓存
    mapper.update(param);

    //清空一级缓存
//    sqlSession.clearCache();
//    sqlSession.close();

    User user1 = mapper.selectOne(param);

    System.out.println(user == user1);

  }

  @Test
  public void testOneLevelCache1() {
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    UserMapper mapper1 = sqlSession.getMapper(UserMapper.class);

    User param = new User() {{
      setId(1L);
    }};

    User user = mapper.selectOne(param);
    sqlSession.clearCache();

    User user1 = mapper1.selectOne(param);

    //true 一级缓存是SqlSession级别的 同一个sqlSession获取的mapper，查询使用了缓存
    System.out.println(user == user1);
  }

  /**
   * 二级缓存 两个对象，存储的是相同数据
   * <p>
   * 1、配置文件中开启
   * <setting name="cacheEnabled" value="true"/>
   * 2、mapper配置启用 3、pojo实现Serializable接口 4、事务需要提交
   */
  @Test
  public void testTwoLevelCache() {
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);

    //另一个sqlSession获取的mapper
    UserMapper mapper1 = sqlSessionFactory.openSession().getMapper(UserMapper.class);

    User param = new User() {{
      setId(1L);
    }};

    User user = mapper.selectOne(param);
    sqlSession.commit();

    User user1 = mapper1.selectOne(param);

    //true 一级缓存是SqlSession级别的 同一个sqlSession获取的mapper，查询使用了缓存
    System.out.println(user == user1);
  }


}