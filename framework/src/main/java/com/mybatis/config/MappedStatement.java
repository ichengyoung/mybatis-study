package com.mybatis.config;

/**
 * <mapper id="selectOne" resultType="com.mybatis.example.bean.User"
 * parameterType="com.mybatis.example.bean.User"> select * from user where id = #{id} and name =
 * #{name}
 * </mapper>
 */
public class MappedStatement {

  private String id;
  private String resultType;
  private String parameterType;
  private String sql;

  public MappedStatement() {
  }

  public MappedStatement(String id, String resultType, String parameterType, String sql) {
    this.id = id;
    this.resultType = resultType;
    this.parameterType = parameterType;
    this.sql = sql;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public String getParameterType() {
    return parameterType;
  }

  public void setParameterType(String parameterType) {
    this.parameterType = parameterType;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }
}
