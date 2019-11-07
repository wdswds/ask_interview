## dubbo默认配置
|  变量名 |    描述  | 默认值   |
|:-------:|:------|--------:|
|DEFAULT_IO_THREADS|默认IO线程|Math.min(Runtime.getRuntime().availableProcessors() + 1, 32)|
|DEFAULT_PROXY|默认代理|javasist|
|DEFAULT_PAYLOAD|默认最大数据大小|8 * 1024 * 1024/8M|
|DEFAULT_CLUSTER|默认集群容错方案|failover|
|DEFAULT_DIRECTORY|默认集群目录服务|dubbo|
|DEFAULT_LOADBALANCE|默认负载均衡方案|random（随机）|
|DEFAULT_PROTOCOL|默认协议|dubbo|
|DEFAULT_EXCHANGER|默认信息交换方式|header|
|DEFAULT_TRANSPORTER|默认传输方式|netty(netty3)|
|DEFAULT_REMOTING_SERVER|默认远程客户端|netty(netty3)|