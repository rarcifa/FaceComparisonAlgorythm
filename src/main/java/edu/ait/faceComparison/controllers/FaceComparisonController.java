package edu.ait.faceComparison.controllers;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

@RestController
public class FaceComparisonController {
    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

    @PostMapping("/faceCompare")
    public CompareFacesResult faceCompare(@RequestParam("image1") MultipartFile multipartFile1, @RequestParam("image2") MultipartFile multipartFile2) throws IOException {

        String sourceImageName = multipartFile1.getOriginalFilename();

        ByteBuffer sourceImageBytes = ByteBuffer.wrap(multipartFile1.getBytes());

        String targetImageName = multipartFile2.getOriginalFilename();

        ByteBuffer targetImageBytes = ByteBuffer.wrap(multipartFile2.getBytes());

        Float similarityThreshold = 70F;

        /* String sourceImage = "/Users/ricardo.arcifa/Desktop/face-comparison-1.0.1/ric1.jpg";
        String targetImage = "/Users/ricardo.arcifa/Desktop/face-comparison-1.0.1/russel2020.jpg";

        ByteBuffer sourceImageBytes=null;
        ByteBuffer targetImageBytes=null; */

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        /*Load source and target images and create input parameters
        try (InputStream inputStream = new FileInputStream(new File())) {
            sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        }
        catch(Exception e)
        {
            System.out.println("Failed to load source image " + sourceImage);
            System.exit(1);
        }
        try (InputStream inputStream = new FileInputStream(new File(targetImage))) {
            targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        }
        catch(Exception e)
        {
            System.out.println("Failed to load target images: " + targetImage);
            System.exit(1);
        }*/

        Image source=new Image()
                .withBytes(sourceImageBytes);
        Image target=new Image()
                .withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(source)
                .withTargetImage(target)
                .withSimilarityThreshold(similarityThreshold);

        // Call operation
        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);


        // Display results
        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match: faceDetails){
            ComparedFace face= match.getFace();
            BoundingBox position = face.getBoundingBox();
            System.out.println("Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + match.getSimilarity().toString()
                    + "% confidence.");

        }
        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();

        System.out.println("There was " + uncompared.size()
                + " face(s) that did not match");

        return compareFacesResult;
    }
}
