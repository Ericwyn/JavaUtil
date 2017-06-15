import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;

/**
 * 引入字体到Swing 当中的类别，包含了我最爱的思源黑体
 * 参考：https://my.oschina.net/aixingwuyue/blog/57635
 * Created by Ericwyn on 17-6-15.
 */
public class FontUtil {

    public static Font boldSNSK=
            loadFont("font/SourceHanSansK-Bold.ttf",18);
    public static Font extraLightSNSK=
            loadFont("font/SourceHanSansK-ExtraLight.ttf",18);
    public static Font heavySNSK=
            loadFont("font/SourceHanSansK-Heavy.ttf",18);
    public static Font lightSNSK=
            loadFont("font/SourceHanSansK-Light.ttf",18);
    public static Font mediumSNSK=
            loadFont("font/SourceHanSansK-Medium.ttf",18);
    public static Font normalSNSK=
            loadFont("font/SourceHanSansK-Normal.ttf",18);
    public static Font regularSNSK=
            loadFont("font/SourceHanSansK-Regular.ttf",18);

    /**
     * 载入自定义字体的方法
     * @param fontFileName  字体文件的路径（只支持ttf）
     * @param fontSize  文字的大小
     * @return  返回Font
     */
    public static Font loadFont(String fontFileName, float fontSize) {
        try
        {
            File file = new File(fontFileName);
            FileInputStream aixing = new FileInputStream(file);
            Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, aixing);
            Font dynamicFontPt = dynamicFont.deriveFont(fontSize);
            aixing.close();
            return dynamicFontPt;
        }
        catch(Exception e)//异常处理
        {
            e.printStackTrace();
            return new java.awt.Font("宋体", Font.PLAIN, 14);
        }
    }
}
