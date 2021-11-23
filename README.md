
# unidbg-server
springboot运行unidbg


### 使用方式:
1.加载到idea

2.add 个maven工程

3.运行 `UnidbgServerApplication.java` 即可启动服务

### 修改配置和代码
通过 `application.properties` 自行修改服务的地址(默认`0.0.0.0`就行)和端口(默认是`9090`)

Controller示例文件文件参考`SignController.py`

### 打jar包
配置好maven环境的前提下，项目主目录执行
````java
 mvn clean package -Dmaven.test.skip=true  
````
使用maven的package即可，之后会发现生成一个target目录其中里面就有jar包了。
```
mvn package
```

### 使用jar包
```
java -jar target\unidbg-server-0.0.2.jar 
```
### curl调用示例
```
curl -XGET 'http://127.0.0.1:9090/unidbg/dySign?url=https://aweme-eagle.snssdk.com/aweme/v1/feed/?type=0%26max_cursor=0%26min_cursor=-1%26count=30%26volume=0.06666666666666667%26pull_type=2%26need_relieve_aweme=0%26ts=1604989727%26app_type=lite%26manifest_version_code=180%26_rticket=1604989727594%26ac=wifi%26device_id=123411234%26iid=123411234%26os_version=8.1.0%26channel=xiaoshangdian_douyin_and19%26version_code=180%26device_type=Pixel%26language=zh%26resolution=1080*1758%26openudid=2dc3087ecc9addf9%26update_version_code=1800%26app_name=aweme%26version_name=1.8.0%26os_api=27%26device_brand=google%26ssmix=a%26device_platform=android%26dpi=540%26aid=1128'
```

### Python调用示例
```
import requests
url = 'http://127.0.0.1:9090/unidbg/dySign'
data = {'url': 'https://aweme-eagle.snssdk.com/aweme/v1/feed/?type=0&max_cursor=0&min_cursor=-1&count=30&volume=0.06666666666666667&pull_type=2&need_relieve_aweme=0&ts=1604989727&app_type=lite&manifest_version_code=180&_rticket=1604989727594&ac=wifi&device_id=123411234&iid=123411234&os_version=8.1.0&channel=xiaoshangdian_douyin_and19&version_code=180&device_type=Pixel&language=zh&resolution=1080*1758&openudid=2dc3087ecc9addf9&update_version_code=1800&app_name=aweme&version_name=1.8.0&os_api=27&device_brand=google&ssmix=a&device_platform=android&dpi=540&aid=1128'}
req=requests.post(url,data=data)
print(req.json())
```

### 感谢
https://github.com/zhkl0228/unidbg

学unidbg强烈推荐龙哥的星球，更新频率高
![](https://img2020.cnblogs.com/blog/736399/202111/736399-20211108182436999-806188411.jpg)
