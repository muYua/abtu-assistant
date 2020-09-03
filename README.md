## 一、环境依赖
开发语言：Java1.8\
开发工具IDE：Eclipse或Idea\
应用服务器：Tomcat 9.0\
关系型数据库：MySQL 5.7\
非关系型数据库：Redis 3.x（windows版）
## 二、部署步骤
0. 部署好上面提到的环境依赖。\
1. 由于pageoffice不能在官方的Maven中央仓库中下载，需要自己去pageoffice官网中自行下载Jar包。
这里Maven引入Jar包的方式，只要在pom.xml中将相应的Jar包本地路径修改为自己本地的正确的Jar路径即可。\
2. 在注册的步骤中需要使用Redis存储验证码，应该在该操作时完成Redis的环境部署。\
3. 关于QQ邮箱通过SMTP电子邮件传输协议的代理完成收发邮件的功能，需要自己校验在application.yml中spring.mail中的配置是否符合自己的要求。也就是说，需要自己配置自己的邮箱信息。
4. 项目中使用到了Lombok插件（简化代码，使代码更简洁，比如生成getter方法，只需要注解上@Getter），需要在运行项目时在IDE中安装好Lombok插件
## 三、目录结构描述
例子:

├──&ensp;README.md &emsp;              // help\
├── app &emsp;                         // 应用\
├── config &emsp;                      // 配置\
│   ├── default.json\
│   ├── dev.json &emsp;                // 开发环境\
│   ├── experiment.json &emsp;         // 实验\
│   ├── index.js &emsp;                // 配置控制\
│   ├── local.json &emsp;              // 本地\
│   ├── production.json &emsp;         // 生产环境\
│   └── test.json &emsp;               // 测试环境\
├── data\
├── doc &emsp;                         // 文档\
├── environment\
├── gulpfile.js\
├── locales\
├── logger-service.js &emsp;           // 启动日志配置\
├── node_modules\
├── package.json\
├── app-service.js &emsp;              // 启动应用配置\
├── static &emsp;                      // web静态资源加载\
│   └── initjson\
│       └── config.js &emsp;           // 提供给前端的配置\
├── test\
├── test-service.js\
└── tools

## 四、版本内容更新
1. 新功能 &emsp; XXXX
2. 新功能 &emsp; XXXX

---
### Markdown语法笔记
#### 空格
- 半角的空白：[&ensp;]或[&#8194;]
- 全角的空白：[&emsp;]或[&#8195;]
- 不换行的空白：[&nbsp;]或[&#160;]
