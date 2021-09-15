package com.mybatis.sqlsession;

import com.mybatis.config.Configuration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class DefaultSqlSession implements SqlSession {

  private final Configuration configuration;

  public DefaultSqlSession(Configuration configuration) {
    this.configuration = configuration;
  }


  @Override
  public <E> List<E> selectList(String statementId, Object... params) throws Exception {
    SimpleExecutor simpleExecutor = new SimpleExecutor(configuration);
    return simpleExecutor.selectList(statementId, params);
  }

  @Override
  public <T> T selectOne(String statementId, Object... params) throws Exception {
    List<Object> objects = selectList(statementId, params);
    if (objects.size() == 1) {
      return (T) objects.get(0);
    } else {
      throw new RuntimeException("查询结果为空或者为多个");
    }
  }

  @Override
  public <T> T add(String statementId, Object... params) throws Exception {
    SimpleExecutor simpleExecutor = new SimpleExecutor(configuration);
    return simpleExecutor.add(statementId, params);
  }

  @Override
  public int update(String statementId, Object... params) throws Exception {
    SimpleExecutor simpleExecutor = new SimpleExecutor(configuration);
    return simpleExecutor.update(statementId, params);
  }

  @Override
  public <T> T getMapper(Class<?> mapperClass) {

    Object o = Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[]{mapperClass},
        (proxy, method, args) -> {
          //这里使用代理类调用具体方法
          //String statementId, Object... params

          String nameSpace = method.getDeclaringClass().getName();
          String mapperId = method.getName();
          String statementId = nameSpace + "." + mapperId;

          //selectList 或者 selectOne
          Type genericReturnType = method.getGenericReturnType();

          if (genericReturnType instanceof ParameterizedType) {
            return selectList(statementId, args);
          } else if (Objects.equals(genericReturnType.getTypeName(), "int")) {
            return update(statementId, args);
          } else if (Objects.equals(genericReturnType.getTypeName(), "void")) {
            delete(statementId, args);
            return null;
          } else if (mapperId.contains("add")) {
            return add(statementId, args);
          } else {
            return selectOne(statementId, args);
          }
        });

    return (T) o;
  }

  @Override
  public void delete(String statementId, Object... params) throws Exception {
    SimpleExecutor simpleExecutor = new SimpleExecutor(configuration);
    simpleExecutor.delete(statementId, params);
  }
}
