package MultiThreadDownload; /**
 *
 * Created by Ericwyn on 17-7-3.
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MultiThreadDownload {
    //最大的下载线程数
    private static final int MAX_THREAD_NUM=5;
    static int[] precent=new int[MAX_THREAD_NUM];
    public static void main(String[] args) {
        //文件下载路径
        String filePath = "http://www.burano.tk/shadowsocks-nightly-4.1.7.apk";
        //文件保存路径
        String destination = "Download/ss";
        //打算开启的线程数
        int threadNum = MAX_THREAD_NUM;
        new MultiThreadDownload().download(filePath, destination, threadNum);

        while (true){
            Boolean flag=true;
            for (int i:precent){
                if (i<9){
                    flag=false;
                    break;
                }
            }
            if (flag){
                showPrecent(1);
                System.out.println("下载结束");
                break;
            }else {
                showPrecent(0);
                try {
                    Thread.sleep(100L);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 打印下载进度条，
     * @param flag  1的话是打印下载结束时候的进度条，其他数字是打印正在下载时候的进度条
     */
    public static void showPrecent(int flag){
        StringBuffer output=new StringBuffer();

        if (flag==1){
            for (int i=0;i<precent.length;i++){
                for (int j=0;j<10;j++){
                    output.append("|");
                }
                output.append(" ");
            }
        }else {
            for (int i=0;i<precent.length;i++){
                for (int j=0;j<precent[i];j++){
                    output.append("|");
                }
                for (int j=0;j<10-precent[i];j++){
                    output.append(".");
                }
                output.append(" ");
            }
        }

        System.out.println(output.toString());

    }

    /**
     * 下载文件
     */
    private void download(String filePath, String destination, int threadNum) {
        try {
            File dir=new File(destination);
            if (!dir.isDirectory()){
                dir.mkdirs();
            }
            //通过下载路径获取连接
            URL url = new URL(filePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置连接的相关属性
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            //判断连接是否正确。
            if (conn.getResponseCode() == 200) {
                // 获取文件大小。
                int fileSize = conn.getContentLength();
                //得到文件名
                String fileName = getFileName(filePath);
                //根据文件大小及文件名，创建一个同样大小，同样文件名的文件
                File file = new File(destination + File.separator + fileName);
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.setLength(fileSize);
                raf.close();
                // 将文件分成threadNum = 5份。
                int block = fileSize % threadNum == 0 ? fileSize / threadNum
                        : fileSize / threadNum + 1;
                for (int threadId = 0; threadId < threadNum; threadId++) {
                    //传入线程编号，并开始下载。
                    new DownloadThread(threadId,block, file, url).start();
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //由路径获取文件名。
    private String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }

}

//文件下载线程
class DownloadThread extends Thread {
    int start, end,threadId;
    File file = null;
    URL url = null;
    int precent=0;

    public DownloadThread(int threadId,int block, File file, URL url) {
        this.threadId = threadId;
        start = block * threadId;
        end = block * (threadId + 1) - 1;
        this.file = file;
        this.url = url;
    }

    public void run() {
        try {
            //获取连接并设置相关属性。
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            //此步骤是关键。
            conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
            int precentTemp=(end-start)/10;  //每一份多大

            if (conn.getResponseCode() == 206) {
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                //移动指针至该线程负责写入数据的位置。
                raf.seek(start);
                //读取数据并写入
                InputStream inStream = conn.getInputStream();
                byte[] b = new byte[1024];
                int len = 0;
                int hadDownloadBit=0;

                while ((len = inStream.read(b)) != -1) {
                    raf.write(b, 0, len);
                    hadDownloadBit+=len;

                    //强行加上的多线程显示进度的方法
                    if(hadDownloadBit/precentTemp>precent){
                        precent=hadDownloadBit/precentTemp;
                        MultiThreadDownload.precent[threadId]=precent;
//                        MultiThread.showPrecent();
                    }

                }
//                System.out.println("线程"+threadId+"下载完毕");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
