# rpclib
Dubbo rpc optimize library by cache.

利用缓存来优化Dubbo RPC调用的jar包。框架是基于Dubbo Filter。
如果以后开发Spring Cloud的同类框架，可以采用http的Interceptor。

## 使用步骤:
### step1. maven install本框架的jar到本地仓库
方法为：
```shell script
git clone https://github.com/bootsrc/rpclib.git

cd rpclib

mvn clean install
```
### step2.在自己的项目的dubbo consumer项目的pom.xml中增加依赖
```xml
<dependency>
    <groupId>com.github.bootsrc.rpclib</groupId>
    <artifactId>rpclib-dubbo</artifactId>
    <version>1.0</version>
</dependency>
```

### step3. 创建bean，继承AbstractRpcConfigHolder
并实现方法initConfigs(). 
内部使用RpcOptionBuilder，并调用register()方法来注册各个method的优化配置.
如果需要进行字节反序列化，则需要配置OptimizeConfigHolder的实现类中的方法

下面是例子. RpcConfigHolderImpl.java

```java
package com.lsm.dubbodemo.main.config;

import bootsrc.rpclib.core.AbstractRpcConfigHolder;
import bootsrc.rpclib.core.LocalInvokeResult;
import bootsrc.rpclib.core.RpcOption;
import bootsrc.rpclib.core.RpcOptionBuilder;
import com.lsm.dubbodemo.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RpcConfigHolderImpl extends AbstractRpcConfigHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConfigHolderImpl.class);

    @Override
    public void initConfigs() {
        DemoServiceHello();
    }

    private void DemoServiceHello() {
        try {
            RpcOption rpcOption = RpcOptionBuilder.builder()
                    .interfaceClass(DemoService.class)
                    .addParamTypeAndName(String.class, "input")
                    .methodName("hello")
                    .throwsException(true)
                    .defaultResult(null)
                    .function((Object[] args) -> {
                        String input = (String) args[0];

                        // 模拟从Redis等缓存中直接读取数据, result = jedis.get("x-input")
                        String result = "Hello " + input;

                        LocalInvokeResult localInvokeResult = new LocalInvokeResult();
                        localInvokeResult.setParamValid(true);
                        localInvokeResult.setException(null);
                        localInvokeResult.setResultValue(result);
                        localInvokeResult.setNeedInvokeRemote(Objects.isNull(result));
                        return localInvokeResult;
                    }).build();

            register(rpcOption);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
```
### 4. 启动rpclib开关
这里分2种情况。
情况1. Spring Boot项目，或者AnnotationConfigApplicationContext启动的项目
在@Configuration注解的类里加上@EnableRpcLib
```java
package com.lsm.dubbodemo.main.config;

import bootsrc.rpclib.core.config.EnableRpcLib;
import bootsrc.rpclib.core.consts.RpcLibConstants;
import com.lsm.dubbodemo.api.CommonConstants;
import com.lsm.dubbodemo.api.DemoService;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRpcLib
public class Config {
    @Bean
    public ReferenceBean<DemoService> demoServiceReferenceBean() {
        ReferenceBean<DemoService> referenceBean = new ReferenceBean<>();
        referenceBean.setInterface(DemoService.class);
        referenceBean.setProvidedBy(CommonConstants.APPLICATION_DEMO_SERVICE);
        // referenceBean.setFilter("rpc-lib");或者下面这种方法启用RpcLibFilter拦截
        referenceBean.setFilter(RpcLibConstants.FILTER_EXTENSION_NAME);
        return referenceBean;
    }
}

```
情况2. spring是通过xml配置beans的。
则在spring.xml中添加下面的配置
```xml
<import resource="classpath:rpclib-core.xml" />
```
### 5. spring的xml方式启动情况下，spring-dubbo.xml中被优化的服务的<code>dubbo:reference</code>
中增加<code>attribute filter</code>
```xml
<dubbo:reference interface="my.DemoService" filter="rpc-lib" />
```
注意：如果项目在spring的注解启动或者spring boot启动方式下，需要在Config.java里设置下面这行
```java
referenceBean.setFilter(RpcLibConstants.FILTER_EXTENSION_NAME);
```
两种做法，根据自己的项目的加载beans配置的方式来选择。

## demo环境中查看是否优化成功
日志里搜素关键字<code>rpcLibResult</code>, 字段<code>resultValue</code>就是读到的缓存数据

## 运行demo
使用demo在examples文件夹中

step1. 本地准备一个zookeeper，端口为2181

step2. 启动dubbo-boot-demo-provider
启动了provider

step3. 启动dubbo-boot-demo-main
这个是个consumer

step4. 浏览器访问启动dubbo-boot-demo-main提供的http接口
[http://localhost:3401/hello?input=x](http://localhost:3401/hello?input=x)

显示结果，说明运行成功！input的值自己可以随便修改.
```text
Hello x
```
console log如下
```text
rpcLibResult={"needInvokeRemote":false,"paramValid":true,"resultValue":"Hello x"}
```
说明consumer应用了rpclib-dubbo.jar后能直接从内存中读取数据，不用真正的调用远程的provider。
只有当内存中数据为空的时候，才会调用远程的provider

## 原理

### 原理图

![rpclib-dubbo.png](/doc/rpclib-dubbo-flow-chat.png)

github的图片被屏蔽了。开发者可以把源码下载下来。
在/doc/rpclib-dubbo-flow-chat.png位置可以找到rpclib-dubbo的原理图。

### 原理分析
原本dubbo调用流程是dubbo consumer经过网络来远程调用provider。
provider首先从Redis中读取数据，如果数据有效，则直接返回数据。
否则会从MySQL中读取数据，数据不为空，则会回写到Redis。
这中间有RPC过程的网络开销,还有数据的序列化和反序列化等开销。

我们使用了rpclib框架后，consumer端直接用Dubbo Filter(RpcLibFilter)拦截RPC请求。
然后从Redis中直接读取数据，如果数据有效则直接完成本次方法调用。
能直接减少来一次远程的RPC调用（provider没有被真正调用）。提升了RPC的性能。

<br>
如果数据无效，则跟原本流程一样去调用远程的provider，走原有的方法调用流程。
对业务逻辑没有任何改变。
<br>

经过实验。发现consumer在并发量越大的情况下，缓存发挥的作用越大，用了rpclib的框架
后，在缓存数据有效的一个周期内（缓存expire之前），provider的方法最多只会被调用一次。
consumer端提前去读取redis数据了。这样缩短了RPC过程的响应时间，大大减少了provider被调用次数，
提升了QPS，从而在大大提升了微服务性能的同时，节省了服务器成本。
