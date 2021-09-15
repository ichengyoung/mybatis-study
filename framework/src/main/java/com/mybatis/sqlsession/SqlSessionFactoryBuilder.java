package com.mybatis.sqlsession;

import com.mybatis.config.Configuration;
import com.mybatis.io.Resources;
import com.mybatis.parse.XmlParser;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import org.dom4j.DocumentException;

public class SqlSessionFactoryBuilder {

  public static SqlSessionFactory build(String path)
      throws PropertyVetoException, DocumentException {
    InputStream resourceAsStream = Resources.getResourceAsStream(path);

    Configuration configuration = XmlParser.parseConfig(resourceAsStream);

    return new DefaultSqlSessionFactory(configuration);
  }
}
