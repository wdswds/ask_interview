## SpringBoot自动装配原理
* SpringBoot运行原理要从@SpringBootApplication注解上，这个注解是一个组合注解，它的核心功能是由@EnableAutoConfiguration注解提供的。这个注解关键功能是@Import注解导入的配置功能，EnableAutoConfigurationImportSelector使用SpringFactoriesLoader.loadFactoryNames方法来扫描具有META-INF/spring.factories文件的jar包，而我们的spring-boot-autoconfigure-版本号.jar文件声明了哪些自动配置。

## 核心注解
* @ConditionalOnBean:当容器里有指定的Bean的条件下
* @ConditionalOnClass:当类路径下有指定的类的条件下
* @ConditionalOnExpression:基于SpEL表达式作为判断条件
* @ConditionalOnJava:基于JVM版本作为判断条件
* @ConditionalOnJndi：在JNDI存在的条件下查找指定位置
* @ConditionalOnMissingBean：当容器里没有指定Bean的情况下
* @ConditionalOnNotWebApplication：当前项目不是Web项目的条件下
* @ConditionalOnWebApplication:当前项目是Web项目的条件下
* @ConditionalOnProperty:指定的属性是否有指定的值
* @ConditionalOnResource：类路径是否有指定的值
* @ConditionalOnSingleCandidate:当指定Bean在容器中只有一个，或者虽然有多个但是指定首选的Bean
