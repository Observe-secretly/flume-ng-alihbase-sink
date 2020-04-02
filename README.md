# 介绍
flume-ng-alihbase-sink通过flume-ng-hbase-sink源码进行修改得到。它将会教会你如何使用自建的Flume集群，往阿里云的Hbase写数据。

# 需要注意的
- 1、请注意作者使用的Flume-ng版本，它需要和你的Flume集群版本保持一致；
- 2、请留意aliHbase的版本；
- 3、除了Flume-ng和Hbase的依赖，其他依赖均属于作者的使用场景自行添加进去的。请使用者自行删除

# 配置示例

```
# Name the components on this agent
appRelation.sources = ars
appRelation.sinks = ark
appRelation.channels = arc

# Describe/configure the source
appRelation.sources.ars.type = org.apache.flume.source.kafka.KafkaSource
appRelation.sources.ars.zookeeperConnect = h2:2181,h3:2181,h4:2181
appRelation.sources.ars.topic = cat-agent-trace-new
appRelation.sources.ars.groupId = app_relation

# Describe the sink
appRelation.sinks.ark.type = hbase
appRelation.sinks.ark.table = app_relation
appRelation.sinks.ark.columnFamily = 0
appRelation.sinks.ark.zookeeperQuorum =XXXX.aliyuncs.com:2181
appRelation.sinks.ark.znodeParent = /hbase
appRelation.sinks.ark.serializer = org.apache.flume.sink.hbase.AppRelationHaseEventSerializer
appRelation.sinks.ark.batchSize=300

# Use a channel which buffers events in memory
appRelation.channels.arc.type = memory  
appRelation.channels.arc.capacity = 30000
appRelation.channels.arc.transactionCapacity  =  30000
appRelation.channels.arc.byteCapacityBufferPercentage  =  30 
appRelation.channels.arc.keep-alive=10


# Bind the source and sink to the channel
appRelation.sources.ars.channels = arc
appRelation.sinks.ark.channel = arc

```

# 沟通交流
QQ:914245697
