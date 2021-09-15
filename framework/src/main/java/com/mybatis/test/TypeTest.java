package com.mybatis.test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * https://www.cnblogs.com/baiqiantao/p/7460580.html
 */
public class TypeTest {

  public static void main(String[] args) throws NoSuchMethodException, SecurityException {
    Method method = TypeTest.class.getMethod("testType",
        List.class, List.class, List.class, List.class, List.class, Map.class);

    Type[] types = method.getGenericParameterTypes();//按照声明顺序返回 Type 对象的数组
    for (int i = 0; i < types.length; i++) {
      ParameterizedType pType = (ParameterizedType) types[i];//最外层都是ParameterizedType
      Type[] types2 = pType.getActualTypeArguments();//返回表示此类型【实际类型参数】的 Type 对象的数组
      for (int j = 0; j < types2.length; j++) {
        Type type2 = types2[j];
        System.out.println("第个" + i + "参数：" +
            j + "  类型【" + type2 + "】\t类型接口【" + type2.getClass().getInterfaces()[0].getSimpleName()
            + "】");
      }
    }
  }

  public <T> void testType(List<String> a1, List<ArrayList<String>> a2, List<T> a3, //
      List<? extends Number> a4, List<ArrayList<String>[]> a5, Map<String, Integer> a6) {
  }
}