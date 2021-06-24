
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetectFaces {
    public static void main(String[] args) throws Exception {
        double score1 = detectFaces("001.mp4");
        double score2 = detectFaces("002.mp4");
        System.out.println("001.mp4 socre is:"+score1);
        System.out.println("002.mp4 score is:"+score2);
    }


    // Detects faces in the specified local image.
    public static double detectFaces(String filePath) throws IOException {
        double score = 0.00f;
        int length = 0;
        List<AnnotateImageRequest> requests = new ArrayList<>();

        List<String> list = VideoProcessing.grabberVideoFramer(filePath);
        length = list.size();
        for(int i = 0; i < list.size();i++){
            String path = "/Users/shulizhu/IdeaProjects/VideoFaceAnalysis/src/main/resources/image"+"/"+list.get(i);
            ByteString imgBytes = ByteString.readFrom(new FileInputStream(path));

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);
        }

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {

            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return 0.0;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    score+=ScoreSwitch(annotation.getJoyLikelihood().toString());
                    score-=ScoreSwitch(annotation.getAngerLikelihood().toString());

                    System.out.format(
                            "anger: %s%njoy: %s%nsurprise: %s%nposition: %s",
                            annotation.getAngerLikelihood(),
                            annotation.getJoyLikelihood(),
                            annotation.getSurpriseLikelihood(),
                            annotation.getBoundingPoly());
                }

            }
        }
        return score/length;
    }

    public static double ScoreSwitch(String str){
        double score;
        switch (str){
            case "LIKELY":
                score=1.0;
                break;
            case "POSSIBLE":
                score=0.5;
                break;
            case "UNKNOWN":
                score=0.25;
                break;
            default:
                score=0;
        }
        return score;
    }
}