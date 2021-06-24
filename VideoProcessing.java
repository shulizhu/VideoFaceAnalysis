import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VideoProcessing {
    //Video file path：change to yourself.
    public static String videoPath = "/Users/shulizhu/IdeaProjects/VideoFaceAnalysis/src/main/resources/video";

    //Store a picture of a certain frame of the intercepted video
    public static String videoFramesPath = "/Users/shulizhu/IdeaProjects/VideoFaceAnalysis/src/main/resources/image";
    /**
     * Frame the video file and store it in "jpg" format.
     * Rely on the FrameToBufferedImage method: convert the frame to a bufferedImage object
     *
     * @param videoFileName
     */
    public static List<String> grabberVideoFramer(String videoFileName){
        //The path of the image of the last obtained video
        List<String> list = new ArrayList<>();

        //Frame object
        Frame frame = null;
        //raise a flag
        int flag = 0;
        try {
			 /*
            Get video files
            */
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(videoPath+"/"+videoFileName);
            fFmpegFrameGrabber.start();

            //Get the total number of video frames
            int ftp = fFmpegFrameGrabber.getLengthInFrames();



            while (flag <= ftp) {
                frame = fFmpegFrameGrabber.grabImage();

				//Process video frames

                if (frame != null && flag %50==0) {
                    //File absolute path + name
                    String uuid = UUID.randomUUID().toString()+"_" + String.valueOf(flag) + ".jpg";
                    list.add(uuid);
                    String fileName = videoFramesPath +"/"+ uuid;

                    //File storage object
                    File outPut = new File(fileName);
                    ImageIO.write(FrameToBufferedImage(frame), "jpg", outPut);
                }
                flag++;
            }
            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();
        } catch (Exception E) {
            E.printStackTrace();
        }
        return list;
    }

    public static BufferedImage FrameToBufferedImage(Frame frame) {
        //Create a BufferedImage object
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }


    /**
     * test：
     * 1、Create a new test folder, divide the test into video and img, save a video under video, and name it test
     * D:/test/video     D:/test/img
     * @param args
     */
    public static void main(String[] args) {
        String videoFileName = "001.mp4";
        List<String> list = grabberVideoFramer(videoFileName);
        for(int i = 0 ; i < list.size();i++){
            System.out.println(list.get(i));
        }
    }
}

