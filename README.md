# File-storage-service-system.m
核心功能 :文件存储服务的核心功能是：上传和下载。除了这两个核心功能，还具有：  可用性：作为基础性服务，通过集群化部署实现高可用 配置性：结合nacos配置中心，可动态配置上传下载的方式等配置 扩展性：采用策略设计模式能方便的进行扩展，如添加新的OSS服务商等 本系统的文件服务提供两种类型的服务：  ​ 1、面对应用系统的通用附件服务  ​ 提供统一的上传接口，屏蔽底层的存储方案（本地存储、FastDFS、MinIo、阿里云存储、七牛云存储等），可独立运行服务  ​ 2、面对用户的网盘服务  ​ 有文件夹和文件的概念，支持大文件分片上传、合并  ​ 3、面对大屏展示的数据统计服务  ​ 有云盘首页数据概览，按照类型/时间等维度统计各种类型文件的大小和数量等 
