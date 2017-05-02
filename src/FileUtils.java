import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 封装文件的增删改，和文件、文件夹的复制，粘贴
 * Created by Ericwyn on 17-5-2.
 */
public class FileUtils {

    /**
     * 创建文件
     * @param absolutePath  文件所在路径
     */
    public static void createFile(String absolutePath){
        try {
            File file=new File(absolutePath);
            file.createNewFile();
        }catch (IOException ioe){
            ioe.printStackTrace();
            System.out.println("创建文件"+absolutePath+"失败");
        }
    }

    /**
     * 创建文件夹
     * @param absolutePath  文件夹所在路径
     */
    public static void createDir(String absolutePath){
        try {
            File file=new File(absolutePath);
            file.mkdir();
        }catch (Exception ioe){
            ioe.printStackTrace();
            System.out.println("创建文件夹"+absolutePath+"失败");
        }
    }

    /**
     * 删除空目录
     * @param dir 将要删除的目录路径
     */
    public static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return 是否成功删除
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 使用transferFrom方法对文件进行复制，
     * @param fileFromPath  文件来源路径
     * @param fileToPath    文件复制路径
     */
    public static void copyFile(String fileFromPath, String fileToPath){
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(new File(fileFromPath)).getChannel();
            outputChannel = new FileOutputStream(new File(fileToPath)).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException ioe){
            ioe.printStackTrace();
            System.out.println("复制文件"+fileFromPath+"失败");
        }finally {
            try {
                inputChannel.close();
                outputChannel.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
                System.out.println("关闭文件复制流"+fileFromPath+"失败");
            }
        }
    }

    /**
     * 递归复制文件夹
     * @param dirFromPath 复制文件夹的路径
     * @param dirToPath   目标文件夹的路径
     */
    public static void copyDir(String dirFromPath,String dirToPath){
        File dirFrom=new File(dirFromPath);
        File dirTo=new File(dirToPath);
        if(!dirTo.isDirectory()){
            dirTo.mkdir();
        }
        if(!dirFrom.isDirectory()){
            System.out.println(dirFromPath+"文件夹不存在");
            return;
        }
        File[] files=dirFrom.listFiles();
        for (File fileFlag:files){
            if(fileFlag.isDirectory()){
                copyDir(fileFlag.getAbsolutePath(), dirFromPath+"/"+fileFlag.getName());
            }else {
                copyFile(fileFlag.getAbsolutePath(),dirFromPath+"/"+fileFlag.getName());
            }
        }
    }


}
