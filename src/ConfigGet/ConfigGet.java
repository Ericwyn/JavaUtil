package ConfigGet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 获取本地config文件的方法
 * Created by Ericwyn on 17-7-15.
 */
public class ConfigGet {
    private static SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    //配置文件
    private File configFile;
    //配置的configMap
    private HashMap<String ,String > configMap;
    //配置文件是否存在
    private boolean haveConfig=false;
    //是否依据默认值自动生成配置文件
    private boolean autoCreateConfig=false;

    public ConfigGet(File configFile){
        this.configFile=configFile;
        configMap=getConfigMap();
    }

    public ConfigGet(File configFile,boolean autoCreateConfig){
        this.configFile=configFile;
        this.autoCreateConfig=autoCreateConfig;
        configMap=getConfigMap();
    }

    public ConfigGet(String configFilePath,boolean autoCreateConfig){
        this.configFile=new File(configFilePath);
        this.autoCreateConfig=autoCreateConfig;
        configMap=getConfigMap();
    }

    /**
     * 获取所有的配置项目
     * @return
     */
    private HashMap<String ,String> getConfigMap(){
        HashMap<String ,String > configMapTemp=new HashMap<>();
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(configFile));
            String line=null;
            while ((line=bufferedReader.readLine())!=null){
                line=line.trim().replaceAll(" ","");
                if(!line.startsWith(";")){
                    if(line.contains(";")){
                        String[] temps=line.split(";");
                        if(temps[0]!=null && !temps[0].equals("")){
                            line=temps[0];
                        }
                    }
                    if(!line.trim().equals("")){
                        String[] temps=line.split("=");
                        if(temps[0]!=null && temps[1]!=null
                                && !temps[0].equals("") && !temps[1].equals("")){
                            configMapTemp.put(temps[0],temps[1]);
                        }
                    }
                }
            }
            bufferedReader.close();
            haveConfig=true;
            return configMapTemp;
        }catch (FileNotFoundException e){
            return null;
        }catch (IOException e){
            errorFileOupFile(e);
            return null;
        }
    }

    /**
     * 共用的getValue方法，对用户开放
     * @param defaultValue  默认的value，当key不存在的时候返回该值
     * @param valueKey key
     * @return
     */
    public String getValue(String valueKey,String defaultValue){
        String value=null;
        if((value=getValue(valueKey))!=null){
            return value;
        }else {
            if(autoCreateConfig){
                writeVale(valueKey,defaultValue);
                return defaultValue;
            }else {
                return defaultValue;
            }
        }
    }

    /**
     * 私有的getValue方法
     * @param valueKey
     * @return
     */
    private String getValue(String valueKey){
        if(haveConfig && configMap.get(valueKey)!=null){
            return configMap.get(valueKey);
        }else {
            return null;
        }
    }

    private ArrayList<String> readConfFile(){
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(configFile));
            String line=null;
            ArrayList<String> oldConfList=new ArrayList<>();
            while ((line=bufferedReader.readLine())!=null){
                oldConfList.add(line);
            }
            bufferedReader.close();
            return oldConfList;
        }catch (IOException e){
            errorFileOupFile(e);
            return null;
        }
    }

    /**
     * 更改配置
     * @param key 需要更改的key
     * @return
     */
    public boolean updateValue(String key,String newValue){
        if(!haveConfig || getValue(key)==null){
            return false;
        }
        try {
            ArrayList<String> oldConfList=readConfFile();
            if(oldConfList!=null){
                BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(configFile));
                for (String strTemp:oldConfList){
                    if(!strTemp.trim().replace(" ","").contains(key+"=")){
                        bufferedWriter.write(strTemp);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }else {
                        String newValueLine = key+" = "+newValue;
                        if(strTemp.contains(";")){
                            String[] temps=strTemp.split(";");
                            for (int i=1;i<temps.length;i++){
                                newValueLine = newValueLine + ";"+temps[i];
                            }
                        }
                        bufferedWriter.write(newValueLine);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }
                bufferedWriter.close();
                configMap=getConfigMap();
                return true;
            }else {
                return false;
            }
        }catch (IOException e){
            errorFileOupFile(e);
            return false;
        }
    }

    /**
     * 删
     * @param keys
     * @return
     */
    public boolean deleteValue(String... keys){
        try {
            ArrayList<String> oldConfList=readConfFile();
            if(oldConfList!=null){
                BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(configFile));
                for (String strTemp:oldConfList){
                    boolean haveDeleteKey=false;
                    for (String keyTemp:keys){
                        if(strTemp.trim().replace(" ","").contains(keyTemp+"=")){
                            haveDeleteKey=true;
                            break;
                        }
                    }
                    if(!haveDeleteKey){
                        bufferedWriter.write(strTemp);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }
                bufferedWriter.close();
                configMap=getConfigMap();
                return true;
            }else {
                return false;
            }
        }catch (IOException e){
            errorFileOupFile(e);
            return false;
        }
    }

    /**
     * 建一个配置项目写入到配置文件当中
     * @param key   项目的key
     * @param value 项目的值
     */
    private void writeVale(String key,String value){
        if(!configFile.isFile()){
            try {
                configFile.createNewFile();
            }catch (IOException e){
                errorFileOupFile(e);
            }
        }
        try {
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(configFile,true));
            bufferedWriter.newLine();
            bufferedWriter.write(key+" = "+value +" ; auto create by ConfigGet");
            bufferedWriter.newLine();
            bufferedWriter.close();
            configMap=getConfigMap();
        }catch (IOException e){
            errorFileOupFile(e);
        }

    }

    /**
     * 配置文件无法读取的时候将错误信息输出成错误文件
     */
    private void errorFileOupFile(Exception e){
        File errorFile=new File("ConfigGet_error.log");
        try {
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(errorFile,true));
            bufferedWriter.write(sdf.format(new Date())+":"+e.toString());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
