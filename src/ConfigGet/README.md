# ConfigGet 使用说明
Config是一个简单的配置文件读取工具，因为之前的项目当中自己往往习惯将全局配置写在一个config类里面,以public static final String的形式定义，为了方便后期将配置单独抽取成由配置文件配置，于是写了这么一个工具类

## 使用
引入`Config.java`文件后

    private static ConfigGet configGet=new ConfigGet("burano.cfg",true);
    configGet.getValue("2016-2017","SCORE_YEAR");

更多用法请看java文件当中的注释