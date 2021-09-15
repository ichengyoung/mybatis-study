package com.mybatis.parse;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mybatis.config.Configuration;
import com.mybatis.config.MappedStatement;
import com.mybatis.io.Resources;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlParser {

  private static Configuration configuration = new Configuration();

  public static Configuration parseConfig(InputStream inputStream)
      throws DocumentException, PropertyVetoException {
    SAXReader saxReader = new SAXReader();
    Document document = saxReader.read(inputStream);

    //1、读取解析数据库配置
    Properties dbProperties = new Properties();
    List<Element> list = document.selectNodes("//property");
    for (Element element : list) {
      String name = element.attributeValue("name");
      String value = element.attributeValue("value");
      dbProperties.put(name, value);
    }

    ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
    comboPooledDataSource.setDriverClass(dbProperties.getProperty("driverClass"));
    comboPooledDataSource.setJdbcUrl(dbProperties.getProperty("jdbcUrl"));
    comboPooledDataSource.setUser(dbProperties.getProperty("user"));
    comboPooledDataSource.setPassword(dbProperties.getProperty("password"));

    configuration.setDataSource(comboPooledDataSource);

    List<Element> mapperList = document.selectNodes("//mapper");
    for (Element element : mapperList) {
      String mapperResource = element.attributeValue("resource");
      parseMapper(mapperResource);
    }

    return configuration;
  }

  /**
   * 解析单个mapper配置文件
   *
   * @param mapperResource
   * @throws DocumentException
   */
  private static void parseMapper(String mapperResource) throws DocumentException {

    SAXReader saxReader = new SAXReader();
    Document document = saxReader.read(Resources.getResourceAsStream(mapperResource));

    Element rootElement = document.getRootElement();
    String namespace = rootElement.attributeValue("namespace");

    List<Element> list = document.selectNodes("//mapper");

    for (Element element : list) {
      String id = element.attributeValue("id");
      String resultType = element.attributeValue("resultType");
      String parameterType = element.attributeValue("parameterType");
      String sql = element.getTextTrim();

      MappedStatement mappedStatement = new MappedStatement(id, resultType, parameterType, sql);
      String key = namespace + "." + id;
      configuration.getMappedStatementMap().put(key, mappedStatement);
    }
  }
}
