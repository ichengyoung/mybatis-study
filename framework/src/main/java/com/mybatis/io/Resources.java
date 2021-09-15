package com.mybatis.io;

import java.io.InputStream;

/**
 * 映射文件到内存
 */
public class Resources {

  public static InputStream getResourceAsStream(String path) {
    return Resources.class.getClassLoader().getResourceAsStream(path);
  }
}

