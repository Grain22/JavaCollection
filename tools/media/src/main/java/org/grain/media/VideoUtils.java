package org.grain.media;

import com.jhlabs.image.BoxBlurFilter;
import org.bytedeco.javacv.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

/**
 * @author wulifu
 */
public class VideoUtils {

    private static final String pic = "C:\\Users\\wulifu\\Desktop\\20191220142914_4_ykBDMbZoCenVIhCS9dEjkjPR4UoBVcxMruKRGcmzkeq8QVRPFw.png";
    private static String video_url = "C:\\Users\\wulifu\\Desktop\\StarLight.mp4";
    private static String video_result = "C:\\Users\\wulifu\\Desktop\\result.mp4";

    public static void doRenderExample() {
        FFmpegFrameGrabber grabber = null;
        FFmpegFrameRecorder recorder = null;
        try {
            File file = new File(video_url);
            grabber = new FFmpegFrameGrabber(file);
            grabber.start();
            int imageWidth = grabber.getImageWidth();
            int imageHeight = grabber.getImageHeight();
            double frameRate = grabber.getFrameRate();
            int videoCodec = grabber.getVideoCodec();

            recorder = new FFmpegFrameRecorder(video_result, imageWidth, imageHeight);
            recorder.setVideoCodec(videoCodec);
            recorder.setFormat("mp4");
            recorder.setFrameRate(frameRate);
            recorder.setVideoBitrate(grabber.getVideoBitrate());
            recorder.start();

            Frame frame = grabber.grabImage();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            while (!Objects.isNull(frame)) {
                BufferedImage bufferedImage = converter.convert(frame);
                int frameNumber = grabber.getFrameNumber();
                System.out.println(frameNumber);
                BoxBlurFilter boxBlurFilter = new BoxBlurFilter(10f, 10f, 2);
                BufferedImage dst = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
                boxBlurFilter.filter(bufferedImage, dst);
                recorder.record(converter.convert(dst));
                bufferedImage.flush();
                frame = grabber.grabFrame();
            }
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(recorder).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (grabber != null) {
                    grabber.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
