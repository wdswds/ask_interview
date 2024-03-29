## redis基础数据结构
>   1. String
>      >* 一个常见的用途就是缓存用户信息，我们将用户信息结构体使用 JSON 序列化成字符串，然后将序列化后的字符串塞进 Redis 来缓存。同样，取用户信息会经过一次反序列化的过程。
>   2. hash
>      >* hash 结构也可以用来存储用户信息，不同于字符串一次性需要全部序列化整个对象，hash 可以对 用户结构中的每个字段单独存储。这样当我们需要获取用户信息时可以进行部分获取。而以整个字符串的形式去保存用户信息的话就只能一次性全部读取，这样就会比较浪费网络流量。
>      >* hash 也有缺点，hash 结构的存储消耗要高于单个字符串，到底该使用 hash 还是字符串，需要根据实际情况再三权衡。
>   3. list
>      >* 实现最新消息排行等功能
>      >* 消息队列，可以利用 List 的 *PUSH 操作，将任务存在 List 中，
>        然后工作线程再用 POP 操作将任务取出进行执行。
>   4. set
>      >* 利用唯一性，可以统计访问网站的所有独立 IP
>      >* 利用交集求共同好友，通过sadd命令和sinter命令
>  5. zset
>      >* 一个存储全班同学成绩的 Sorted Sets，其集合 value可以是同学的学号，而 score 就可以是其考试得分，这样在数据插入集合的时候，就已经进行了天然的排序
## Redis 单线程为什么还能这么快？
>   *   因为它所有的数据都在内存中，所有的运算都是内存级别的运算，而且单线程避免了多线程的切换性能损耗问题。正因为 Redis 是单线程，所以要小心使用 Redis 指令，对于那些耗时的指令(比如keys)，一定要谨慎使用，一不小心就可能会导致 Redis 卡顿。
## 缓存雪崩
> * 由于原有缓存失效，新缓存未到期间(例如：我们设置缓存时采用了相同的过期时间，在同一时刻出现大面积的缓存过期)，所有原本应该访问缓存的请求都去查询数据库了，而对数据库CPU和内存造成巨大压力，严重的会造成数据库宕机。从而形成一系列连锁反应，造成整个系统崩溃
> * 解决办法： 
>  >* 将系统中key的缓存失效时间均匀地错开，防止同一时间点有大量的key对应的缓存失效。比如我们可以在原有的失效时间基础上增加一个随机值。
## 缓存穿透
> * 指用户查询数据，在数据库没有，自然在缓存中也不会有。这样就导致用户查询的时候，在缓存中找不到，每次都要去数据库再查询一遍，然后返回空（相当于进行了两次无用的查询）。这样请求就绕过缓存直接查数据库，这也是经常提的缓存命中率问题	
> * 解决办法：
>  >1. 采用布隆过滤器，将所有可能存在的数据哈希到一个足够大的bitmap中，一个一定不存在的数据会被这个bitmap拦截掉，从而避免了对底层存储系统的查询压力。
>  >2. 如果一个查询返回的数据为空（不管是数据不存在，还是系统故障），我们仍然把这个空结果进行缓存，但它的过期时间会很短，最长不超过五分钟。通过这个直接设置的默认值存放到缓存，这样第二次到缓存中获取就有值了，而不会继续访问数据库，这种办法最简单粗暴！
## 缓存击穿（热点key）
> * 是指一个key非常热点，在不停的扛着大并发，大并发集中对这一个点进行访问，当这个key在失效的瞬间，持续的大并发就穿破缓存，直接请求数据库，就像在一个屏障上凿开了一个洞。
> * 解决办法：
>  >1. 让缓存永不过期。    
>  >2. 对缓存查询加锁，如果KEY不存在，就加锁，然后查DB入缓存，然后解锁；其他进程如果发现有锁就等待，然后等解锁后返回数据或者进入DB查询**
     
## Redis过期机制
 > * 定时删除
 > * 惰性删除
 >      > 不管过期的键，在这种策略下，当键在键空间中被取出时，首先检查取出的键是否过期，若过期删除该键，否则，返回该键。   
        缺点：不管过期的键，在这种策略下，当键在键空间中被取出时，首先检查取出的键是否过期，若过期删除该键，否则，返回该键。
 > * 定期删除
 >      > 由定时删除算法，定期的去检查一定的数据库，删除一定的过期键。
    通过合理的删除操作执行的时长和频率，达到合理的删除过期键。
 > ### 其他模块对过期键的处理
 > * 生成RDB文件时
 >      > 执行 SAVE 或 BGSAVE 时 ，数据库键空间中的过期键不会被保存在RDB文件中
 > * 载入RDB文件时
 >     > Master 载入RDB时，文件中的未过期的键会被正常载入，过期键则会被忽略。
     Slave 载入 RDB 时，文件中的所有键都会被载入，当同步进行时，会和Master 保持一致。
 > * AOF 文件写入时
 >    > 数据库键空间的过期键的过期但并未被删除释放的状态会被正常记录到 AOF 文件中，当过期键发生释放删除时，DEL 也会被同步到 AOF 文件中去。
 > * 重新生成 AOF文件时
 >    > 执行 BGREWRITEAOF 时 ，数据库键中过期的键不会被记录到 AOF 文件中
 > * 复制
 >     > Master 删除 过期 Key 之后，会向所有 Slave 服务器发送一个 DEL命令，从服务器收到之后，会删除这些 Key
     Slave 在被动的读取过期键时，不会做出操作，而是继续返回该键，只有当Master 发送 DEL 通知来，才会删除过期键，这是统一、中心化的键删除策略，保证主从服务器的数据一致性。
    
    
## 持久化
>   * Redis提供了RDB和AOF两种持久化方式，RDB是把内存中的数据集以快照形式写入磁盘，实际操作是通过fork子进程执行，采用二进制压缩存储在硬盘上（默认名称dump.rdb）；AOF是以文本日志的形式记录Redis处理的每一个写入或删除操作。bgsave做镜像全量持久化，aof做增量持久化。
>### RDB持久化
>    >* RDB把整个Redis的数据保存在单一文件中，比较适合做灾备，但缺点是快照保存完成之前如果宕机，这段时间的数据将会丢失，另外保存快照时可能导致服务短时间不可用。
>    >* Save命令：强制redis执行快照，这时候redis处于阻塞状态，不会响应任何其他客户端发来的请求，直到RDB快照文件执行完毕
>    >* Bgsave命令：当执行bgsave命令时，redis会fork出一个子进程来执行快照生成操作，需要注意的redis是在创建fork子进程这个简短的时间redis是阻塞的（此段时间不会响应客户端请求，），当子进程创建完成以后redis响应客户端请求。其实redis自动快照也是使用bgsave来完成的。
>### AOF持久化
>    >* AOF对日志文件的写入操作使用的追加模式，有灵活的同步策略，支持每秒同步，每次修改同步和不同步，缺点就是相同规模的数据集，AOF要大于RDB，AOF在运行效率上往往会慢于RDB
>    >* redis4.0相对与3.X版本其中一个比较大的变化是4.0添加了新的混合持久化方式。
    
## 缓存淘汰策略
>  * noeviction 不会继续服务写请求 (DEL 请求可以继续服务)，读请求可以继续进行。这样可以保证不会丢失数据，但是会让线上的业务不能持续进行。这是默认的淘汰策略。
>  * volatile-lru 尝试淘汰设置了过期时间的 key，最少使用的 key 优先被淘汰。没有设置过期时间的 key 不会被淘汰，这样可以保证需要持久化的数据不会突然丢失。
>  * volatile-ttl 跟上面一样，除了淘汰的策略不是 LRU，而是 key 的剩余寿命 ttl 的值，ttl 越小越优先被淘汰。
>  * volatile-random 跟上面一样，不过淘汰的 key 是过期 key 集合中随机的 key。
>  * allkeys-lru 区别于 volatile-lru，这个策略要淘汰的 key 对象是全体的 key 集合，而不只是过期的 key 集合。这意味着没有设置过期时间的 key 也会被淘汰。
>  * allkeys-random 跟上面一样，不过淘汰的策略是随机的 key。
>  * volatile-xxx 策略只会针对带过期时间的 key 进行淘汰，allkeys-xxx 策略会对所有的 key 进行淘汰。如果你只是拿 Redis 做缓存，那应该使用 allkeys-xxx，客户端写缓存时不必携带过期时间。如果你还想同时使用 Redis 的持久化功能，那就使用 volatile-xxx 策略，这样可以保留没有设置过期时间的 key，它们是永久的 key 不会被 LRU 算法淘汰。