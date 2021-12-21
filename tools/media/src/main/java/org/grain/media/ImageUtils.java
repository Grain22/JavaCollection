package org.grain.media;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author laowu
 */
@SuppressWarnings("unused")
public class ImageUtils {

    public static final String jpg = "jpg";
    public static final String png = "png";

    private static final String pic = "C:\\Users\\wulifu\\Desktop\\20191220142914_4_ykBDMbZoCenVIhCS9dEjkjPR4UoBVcxMruKRGcmzkeq8QVRPFw.png";
    private static final String tem = "C:\\Users\\wulifu\\Desktop\\res.png";

    /**
     * 简单文字添加
     */
    private static BufferedImage addWord(BufferedImage bufferedImage, String s, Font font, int x, int y) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.setFont(font);
        graphics.drawString(s, x, y);
        return bufferedImage;
    }

    private static BufferedImage addPic(BufferedImage base, BufferedImage add, int x, int y) {
        Graphics2D graphics = base.createGraphics();
        graphics.drawImage(add, x, y, null);
        graphics.dispose();
        return base;
    }

    /**
     * 裁剪
     */
    private static BufferedImage sub(BufferedImage image, int x, int y, int width, int height) {
        if (x >= image.getWidth() || y >= image.getHeight()) {
            throw new RuntimeException("选取范围有误");
        }
        if (image.getWidth() < x + width) {
            width = image.getWidth() - x;
        }
        if (image.getHeight() < y + height) {
            height = image.getHeight() - y;
        }
        return image.getSubimage(x, y, width, height);
    }

    /**
     * 缩放图片
     */
    public static BufferedImage resize(BufferedImage bufferedImage, int width, int height) {
        BufferedImage image = getTranslucent(width, height);
        Graphics graphics = image.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0, width, height, null);
        graphics.dispose();
        return image;
    }


    /**
     * 有损旋转
     */
    private static BufferedImage rotate(BufferedImage bufferedImage, int angle) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        image = graphics.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        graphics.dispose();
        graphics = image.createGraphics();
        graphics.rotate(Math.toRadians(angle), width >> 1, height >> 1);
        graphics.drawImage(bufferedImage, 0, 0, null);
        graphics.dispose();
        return image;
    }

    /**
     * 无损旋转
     *
     */
    public static BufferedImage rotateLossLess(BufferedImage bufferedImage, int angle) {
        int width = bufferedImage.getWidth(null);
        int height = bufferedImage.getHeight(null);
        Rectangle rectangle = CalcRotatedSize(new Rectangle(new Dimension(width, height)), angle);
        BufferedImage image = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        image = getTranslucent(rectangle.width, rectangle.height);
        graphics.dispose();
        graphics = image.createGraphics();
        graphics.translate((rectangle.width - width) / 2, (rectangle.height - height) / 2);
        graphics.rotate(Math.toRadians(angle), width >> 1, height >> 1);
        graphics.drawImage(bufferedImage, null, null);
        return image;
    }

    /**
     * 获取蒙版
     *
     */
    public static BufferedImage getTranslucent(int width, int height) {
        return new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics().getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
        // 如果旋转的角度大于90度做相应的转换
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double length = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angelAlpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angelDeltaWidth = Math.atan((double) src.height / src.width);
        double angelDeltaHeight = Math.atan((double) src.width / src.height);
        int lenDeltaWidth = (int) (length * Math.cos(Math.PI - angelAlpha - angelDeltaWidth));
        int lenDeltaHeight = (int) (length * Math.cos(Math.PI - angelAlpha - angelDeltaHeight));
        int desWidth = src.width + lenDeltaWidth * 2;
        int desHeight = src.height + lenDeltaHeight * 2;
        return new Rectangle(new Dimension(desWidth, desHeight));
    }

    public static BufferedImage getImg(String string) throws IOException {
        return getImg(new File(string));
    }

    private static BufferedImage getImg(File file) throws IOException {
        return ImageIO.read(file);
    }

    /**
     * 清理格式信息
     *
     */
    private static BufferedImage cleanFormat(BufferedImage bufferedImage) {
        BufferedImage image = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        graphics.dispose();
        return image;
    }

    private static boolean writeJpg(BufferedImage image, String path) throws IOException {
        return ImageIO.write(image, jpg, new File(path));
    }

    private static boolean writePng(BufferedImage image, String path) throws IOException {
        return ImageIO.write(image, png, new File(path));
    }

}
