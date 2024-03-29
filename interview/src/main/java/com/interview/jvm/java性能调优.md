## 设计调优
1.亨元模式：
    核心思想：一个系统中如果存在多个相同的对象，那么只需共享一份对象的拷贝，而不必为每一次使用都创建新的对象。
    对象能的提升：
        可以节省重复创建对象的开销，被亨元模式维护的相同对象只会被创建一次，如果创建独享比较耗时，便可以节省大量时间，
        由于创建对象的数量减少，所以对系统内存的需求也减小，是的GC的压力也降低了，
## JVM优化


## Linux命令行工具
top：能够实时显示系统进程资源占用状况
sar：可以周期性地对内存和CPU使用情况进行采样
   -A 所有报告总和
   -u CPU利用率
   -d 硬盘使用报告
   -b I/O的情况
   -q 查看队列长度
   -r 内存使用统计信息
   -n 网络信息统计
   -o 采样结果输出到文件
   例：sar -u 1 3  统计CPU使用情况，每秒钟一次，共计采样3次
vmstat：可以统计CPU、内存使用情况、swap使用情况；可以查看交互分区、I/O操作、上下文切换、时钟中断
iostat：查看详尽的I/O信息。
        磁盘I/O很容易成为性能瓶颈。通过iostat可以快速定位系统是否产生大量的I/O操作
        例：iostat 1 3    更多统计信息  iostat -x 1 3
pidstat：
    1.CPU使用率监控：
        可以先用jps命令找到Java程序的PID，然后再用pidstat命令输出程序的CPU使用情况
        pidstat -p 端口号 1 3 -u -t
        -p用于指定进程ID，-u表示CPU使用率的监控
        pidstat不仅可以定位到进程，甚至可以进一步定位到线程。
        jstack -l 端口号 >/tmp/t.txt 将指定java进程的所有线程输出到文本，输出的nid（native ID）为线程
    2.I/O使用监控
        pidstat -p 端口号 -d -t 1 3  -d表明监控对象为磁盘I/O。
