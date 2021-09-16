### 客户端 client项目

* 引入jar包
* 提供配置文件： sqlMapConfig.xml mapper.xml

### 服务端 封装jdbc framework项目

* 读取配置文件 **Resources**
  `InputStream getResourceAsStream(String path)`
* 建立配置容器
    * **Configuration** 存放数据库配置信息 和 所有的MappedStatement
    * **MappedStatement** 存放具体的单个select标签信息
      ```xml
       <!--每个mapper的ID是：namespace.id-->
        <mapper id="selectList" resultType="com.mybatis.example.bean.User">
        select * from user
       </mapper>
      ```
* 创建 SqlSessionFactory，使用 **SqlSessionFactoryBuilder** SqlSessionFactory build(InputStream in)
* 创建 SqlSession，使用 **SqlSessionFactory** SqlSession openSession
* 定义数据库表操作 selectList(),selectOne()
  ,update(),delete(),add().使用**SqlSession**接口及实现DefaultSqlSession
* **Executor**接口及实现SimpleExecutor 具体干活的

### 使用技术

* dom4j 解析xml
* c3p0 连接池
* TokenHandler 解析sql #{} 参数
* Proxy 动态代理 处理方法调用
* Type ParameterizedType 判断具体方法
* 反射 处理参数
* 内省 处理返回值

### mybatis测试 mybatis_test项目

## 插件

1.自定义插件就是一个拦截器，需要实现Interceptor接口

```java
interface Interceptor {

  //做增强
  Object intercept(Invocation invocation) throws Throwable;

  //把自己加入到拦截器链
  default Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }
}
```

2.Plugin

```java
class Plugin implements InvocationHandler {

  //动态代理。这里new 了 一个自己，因为自己也是一个InvocationHandler
  wrap(Object target, Interceptor interceptor) {
    Proxy.newProxyInstance(
        type.getClassLoader(),
        interfaces,
        new Plugin(target, interceptor, signatureMap));
  }

  //调用插件的intercept方法
  invoke(Object proxy, Method method, Object[] args) {
    interceptor.intercept(new Invocation(target, method, args));
  }
}

```

### docker

* redis
  `docker run -itd --name localhost -p 6379:6379 redis`
* mysql
  `docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=my-secret-pw mysql:5.7`