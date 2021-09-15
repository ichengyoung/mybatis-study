package com.mybatis.sqlsession;

import com.mybatis.utils.ParameterMapping;
import java.util.List;

public class BoundSql {

  private List<ParameterMapping> parameterMappings;
  private String sql;

  public BoundSql(List<ParameterMapping> parameterMappings, String sql) {
    this.parameterMappings = parameterMappings;
    this.sql = sql;
  }

  public List<ParameterMapping> getParameterMappings() {
    return parameterMappings;
  }

  public void setParameterMappings(List<ParameterMapping> parameterMappings) {
    this.parameterMappings = parameterMappings;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }
}
