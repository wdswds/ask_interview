## HashMap为什么是线程不安全的？
   * 1、put的时候导致的多线程数据不一致。
            比如有两个线程A和B，首先A希望插入一个key-value对到HashMap中，首先计算记录所要落到的桶的索引坐标，
        然后获取到该桶里面的链表头结点，此时线程A的时间片用完了，而此时线程B被调度得以执行，和线程A一样
        执行，只不过线程B成功将记录插到了桶里面，假设线程A插入的记录计算出来的桶索引和线程B要插入的记录
        计算出来的桶索引是一样的，那么当线程B成功插入之后，线程A再次被调度运行时，它依然持有过期的链表头
        但是它对此一无所知，以至于它认为它应该这样做，如此一来就覆盖了线程B插入的记录，这样线程B插入的记
        录就凭空消失了，造成了数据不一致的行为。
   * 2、HashMap的get操作可能因为resize而引起死循环（cpu100%）
            在扩容的时候，jdk1.8之前是采用头插法，当两个线程同时检测到hashmap需要扩容，在进行同时扩容的时候有可
        能会造成链表的循环，主要原因就是，采用头插法，新链表与旧链表的顺序是反的
    
## HashMap在JDK8为什么将链表转为红黑树？
   * 理想情况下，在随机哈希代码下，桶中的节点频率遵循泊松分布，文中给出了桶长度k的频率表。
        8: 0.00000006
     由频率表可以看出，桶的长度超过8的概率非常非常小。

## ConcurrentHashMap】JDK1.7与JDK1.8源码区别
  * JDK1.8的实现已经摒弃了Segment的概念，而是直接用Node数组+链表+红黑树的数据结构来实现，并发控制使用Synchronized和CAS来操作，整个看起来就像是优化过且线程安全的HashMap
  Node
    Node是ConcurrentHashMap存储结构的基本单元，继承于HashMap中的Entry，用于存储数据,就是一个链表，但是只允许对数据进行查找，不允许进行修改
  TreeNode
    TreeNode继承与Node，但是数据结构换成了二叉树结构，它是红黑树的数据的存储结构，用于红黑树中存储数据，当链表的节点数大于8时会转换成红黑树的结构，他就是通过TreeNode作为存储结构
    
## ConcurrentHashMap和hashtable有什么区别
   * 