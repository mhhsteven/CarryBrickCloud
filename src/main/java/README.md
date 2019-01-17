# 云搬砖
顾名思义就是大家一起搬砖的意思。因为砖太多了，一个人搬很累，而且完成不了包工头制定的OKR。那么就需要多几个人来一起搬，简称云搬砖。既然是大家一起搬，肯定需要一个指挥小队长：我搬一，你搬二，他搬三。具体怎么搬呢，需要了解以下几步：  
1. 写一个类JobImpl继承org.mao.job.BaseBatchJob，实现方法：`List<T> bunch()`取一堆砖，实现方法：`void process(T t)`处理这堆砖里的每一块砖。  
2. 修改conf/job.properties的net.master.ip为指挥小队长的ip地址。如果是单机跑，把net.role设置为master，执行JobImpl类，这样指挥小队长就启动了。然后把net.role设置为slave，再执行JobImpl类，这样一个搬砖工就来搬砖了。如果在不同的机器上跑，不用关心net.role配置，先执行ip是指挥小队长的那台机器的JobImpl类， 后执行其他机器的JobImpl类。  
3. 如果不想写实现类，可以直接跑org.mao.job.impl.BatchJobImpl来观察。  