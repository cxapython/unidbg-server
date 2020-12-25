# unidbg-server
springboot运行unidbg


### 使用方式:
1.加载到idea

2.add 个maven工程

3.运行UnidbgServerApplication.java即可启动服务

### 修改配置和代码
通过application.properties自行修改服务的地址和端口

Controller示例文件文件参考SignController.py
### 打jar包
使用maven的package即可，之后会发现生成一个target目录其中里面就有jar包了。
### 使用jar包
```
cd target
jar -xf unidbg-server-0.0.1-SNAPSHOT.jar
java -cp "BOOT-INF/classes:BOOT-INF/lib/*" com.spider.unidbgserver.UnidbgServerApplication  
```
`注意使用zsh的话BOOT-INF/classes:BOOT-INF/lib/*必须加引号,bash就可以不用。`

### Python调用示例
```
import requests
url = 'http://0.0.0.0:9090/unidbg/dySign'
data = {'url': 'https://aweme-eagle.snssdk.com/aweme/v1/feed/?type=0&max_cursor=0&min_cursor=-1&count=30&volume=0.06666666666666667&pull_type=2&need_relieve_aweme=0&ts=1604989727&app_type=lite&manifest_version_code=180&_rticket=1604989727594&ac=wifi&device_id=123411234&iid=123411234&os_version=8.1.0&channel=xiaoshangdian_douyin_and19&version_code=180&device_type=Pixel&language=zh&resolution=1080*1758&openudid=2dc3087ecc9addf9&update_version_code=1800&app_name=aweme&version_name=1.8.0&os_api=27&device_brand=google&ssmix=a&device_platform=android&dpi=540&aid=1128'}
req=requests.post(url,data=data)
print(req.json())
```
### 当前问题:
springboot使用unidbg遇到logback和sl4j依赖冲突
目前通过pom.xml暂时解决了
### 感谢
https://github.com/zhkl0228/unidbg
