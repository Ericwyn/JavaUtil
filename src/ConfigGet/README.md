# ConfigGet 使用说明
ConfigGet是一个简单的配置文件读取工具，因为之前的项目当中自己往往习惯将全局配置写在一个config类里面,以`public static final String`的形式定义，为了方便后期将配置单独抽取成由配置文件配置，于是写了这么一个工具类

# 使用
## 配置文件格式
    key1 = value1
    key2 = value2
    key3 = value3
    ; 这一行是一行注释
    ; key4 = value 这一行被注释掉了也不起作用
    key 4 = value4 ; 这一行才是真正能起作用的
## 在代码中使用
引入`Config.java`文件后

    private static ConfigGet configGet = new ConfigGet("burano.cfg");
    //获取配置文件项中 SCORE_YEAR 的值
    //没有这个值或者不存在配置文件的话就直接使用 "2016-2017" 
    private static final String value = configGet.getValue("SCORE_YEAR","2016-2017");

### 自动生成配置文件
ConfigGet能够根据代码中设置的默认返回值，自动生成配置文件，方便下一次启动当中使用。只需要再Config对象新建时候传入一个`true`参数就可以了
    
    private static ConfigGet configGet = new ConfigGet("burano.cfg",true);

### 更新配置项

    config.updateValue("KEY","NEW_VALUE");
    
### 删除配置项

    config.deleteValue("KEY1");
    config.deleteValue("KEY2","KEY3","KEY4");


### 错误日志
在ConfigGet无法使用的时候，将会有error的日志文件输出为`ConfigGet_error.log`