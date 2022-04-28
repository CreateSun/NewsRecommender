# ECommerceRecommendSystemIntroduction

> 新闻推荐网站采用前后端分离开发的方式，通过 JSON 交互数据。总共分为前端、服务端、推荐系统三个部分，其中服务端与推荐系统共同由Maven搭建。
## 题记
本项目是在大神tqz的电商推荐系统[点击查看原项目](https://github.com/ittqqzz/ECommerceRecommendSystem)的基础上进行二次修改，前端部分对页面的显示与交互逻辑进行了修改。服务端部分修改了部分Model层内容，并新增了部分接口以满足开发需要，推荐系统中修改了dataLoader部分的数据初始化的逻辑，修改了StatisticRecmmender的统计模块的计算逻辑，新增了评分计算模块用于提取用户的行为日志并量化为用户对于新闻的评分，其他的部分基本保持了原作者的内容。
在此非常感谢大佬的项目让我仔细思考了在不同平台下的大数据分析方式的区别，尤其是算法部分的ALS算法、基于评分的协同过滤、基于物品关键词相似度的推荐，这三个部分我仍在学习之中，收获颇丰，特此分享出来与各位共勉。
## 项目简介

**前端**使用 Vue + TypeScript + ElementUI 构建，build 的时候自动部署到后端业务工程的 webapps/static 目录下，随 Tomcat 一同启动

[点击查看前端工程目录及详细介绍]( https://github.com/CreateSun/NewsRecommender/tree/Main/front)

**后端**又分为业务模块和推荐模块，业务模块与前端交互、接收与反馈数据。推荐模块监听 Kafka 的用户行为数据，然后进行实时计算，将结果写回 MongoDB，并周期性执行离线计算，根据用户最近的操作记录进行离线推荐，并将推荐结果写入到 MongoDB 

[点击查看后端工程目录及详细介绍]( https://github.com/CreateSun/NewsRecommender/tree/Main/backend )

**开发工具**

1. 环境：Win 10、JDK-1.8、Scala-2.11、Spark-2.1
2. IDE：IDEA
3. 组件：Kafka、Redis、MongoDB



