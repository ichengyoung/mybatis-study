package com.mybatis.sqlsession;

import com.mybatis.config.Configuration;
import com.mybatis.config.MappedStatement;
import com.mybatis.utils.GenericTokenParser;
import com.mybatis.utils.ParameterMapping;
import com.mybatis.utils.ParameterMappingTokenHandler;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 1、内省和反射的操作：内省是先得到属性描述器PropertyDescriptor后再进行各种操作，反射则是先得到类的字节码Class后再进行各种操作的。
 * <p>
 * 2、反射：
 * <p>
 * 一个类的所有成员都可以进行反射操作
 * <p>
 * <p>
 * 3、内省：
 * <p>
 * * 1.内省操作只针对JavaBean，只有符合JavaBean规则的类的成员才可以采用内省API进行操作
 * <p>
 * * 2.API存放于包java.beans中
 * <p>
 * * 3.对一个Bean类来讲，我可以没有属性，但是只要有getter/setter方法中的其中一个，那么Java的内省机制就会认为存在一个属性
 */
public class SimpleExecutor implements Executor {

  private final Configuration configuration;

  public SimpleExecutor(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public <E> List<E> selectList(String statementId, Object... params)
      throws Exception {

    //1.获取连接
    Connection connection = configuration.getDataSource().getConnection();

    //2.获取sql
    MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);

    //select * from user where id = #{id} and name = #{name}
    String sql = mappedStatement.getSql();
    //select * from user where id = ? and name = ?
    BoundSql boundSql = getBoundSql(sql);

    //3.获取PreparedStatement
    Class<?> paramTypeClass = getClassType(mappedStatement.getParameterType());
    PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
    //id、name
    setParam(boundSql, paramTypeClass, preparedStatement, params[0]);

    //4.获取结果集
    List<Object> result = new ArrayList<>();

    Class<?> resultTypeClass = getClassType(mappedStatement.getResultType());
    ResultSet resultSet = preparedStatement.executeQuery();
    while (resultSet.next()) {
      //反射
      Object resultObj = resultTypeClass.newInstance();

      ResultSetMetaData metaData = resultSet.getMetaData();
      for (int i = 1; i <= metaData.getColumnCount(); i++) {
        //列名
        String columnName = metaData.getColumnName(i);
        //值
        Object columnValue = resultSet.getObject(columnName);

        //使用内省
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);

        Method writeMethod = propertyDescriptor.getWriteMethod();
        writeMethod.setAccessible(true);
        writeMethod.invoke(resultObj, columnValue);
      }

      result.add(resultObj);
    }
    return (List<E>) result;
  }

  private Class<?> getClassType(Object param) throws ClassNotFoundException {
    if (param != null) {
      return Class.forName(param.toString());
    }
    return null;
  }

  private BoundSql getBoundSql(String sql) {
    ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
    GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}",
        parameterMappingTokenHandler);
    String boundSql = genericTokenParser.parse(sql);

    return new BoundSql(parameterMappingTokenHandler.getParameterMappings(), boundSql);
  }

  @Override
  public <T> T selectOne(String statementId, Object... params) {
    return null;
  }

  @Override
  public int update(String statementId, Object... params) throws Exception {

    //1.获取连接
    Connection connection = configuration.getDataSource().getConnection();

    //2.获取sql
    MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);

    //update user set name = #{name} where id = #{id}
    String sql = mappedStatement.getSql();
    //update user set name = ? where id = ?
    BoundSql boundSql = getBoundSql(sql);

    //3.获取PreparedStatement
    Class<?> paramTypeClass = getClassType(mappedStatement.getParameterType());
    PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
    //id、name
    setParam(boundSql, paramTypeClass, preparedStatement, params[0]);

    return preparedStatement.executeUpdate();
  }

  @Override
  public void delete(String statementId, Object... params) throws Exception {

    //1.获取连接
    Connection connection = configuration.getDataSource().getConnection();

    //2.获取sql
    MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);

    //delete from user where id = #{id}
    String sql = mappedStatement.getSql();
    //delete from user where id = ?
    BoundSql boundSql = getBoundSql(sql);

    //3.获取PreparedStatement
    Class<?> paramTypeClass = getClassType(mappedStatement.getParameterType());
    PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
    //id、name
    setParam(boundSql, paramTypeClass, preparedStatement, params[0]);

    preparedStatement.execute();
  }

  @Override
  public <T> T add(String statementId, Object... params) throws Exception {

    //1.获取连接
    Connection connection = configuration.getDataSource().getConnection();

    //2.获取sql
    MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);

    //insert into user values(#{id},#{name})
    String sql = mappedStatement.getSql();
    //insert into user values(?,?)
    BoundSql boundSql = getBoundSql(sql);

    //3.获取PreparedStatement
    Class<?> paramTypeClass = getClassType(mappedStatement.getParameterType());
    PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());

    //id、name
    setParam(boundSql, paramTypeClass, preparedStatement, params[0]);

    //查询返回
    if (preparedStatement.execute()) {
      return selectOne(statementId, params);
    }
    return null;
  }

  private void setParam(BoundSql boundSql, Class<?> paramTypeClass,
      PreparedStatement preparedStatement, Object param)
      throws NoSuchFieldException, IllegalAccessException, SQLException {
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    for (int i = 0; i < parameterMappings.size(); i++) {
      ParameterMapping parameterMapping = parameterMappings.get(i);
      String property = parameterMapping.getContent();

      //使用反射填充参数
      Field declaredField = paramTypeClass.getDeclaredField(property);
      declaredField.setAccessible(true);
      Object o = declaredField.get(param);

      preparedStatement.setObject(i + 1, o);
    }
  }
}
