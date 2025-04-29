package com.juan.img.reader.controllers;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature.Type;
import com.juan.img.reader.model.Image;
import com.juan.img.reader.model.Item;
import com.juan.img.reader.service.ImageService;
import com.juan.img.reader.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ImageReaderController {

    private final String uploadDirectory = "file:C:/Dev/uploads"; // Define your upload directory

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private CloudVisionTemplate cloudVisionTemplate;

    private final StorageService storageService;
    private final ImageService imageService;

    @Autowired public ImageReaderController(StorageService storageService, ImageService imageService) {
        this.storageService = storageService;
        this.imageService = imageService;
    }

    @GetMapping("/images")
    public Set<Image> returnImages(@RequestParam(value = "objects", defaultValue = "") String objects) {
        Set<Image> images = new HashSet<>();

        // If there are no object parameters, then simply return the list of image objects
        if (objects == null || objects.isEmpty()) {
            return this.imageService.getImages();
        } else {
            // When filtering is passed in the form of objects then we need to find by object
            String[] objectArray = objects.split(",");
            return this.imageService.getImagesFromItemArray(objectArray);
        }

    }

    @GetMapping("/images/{imageId}")
    public Image returnImagesById(@PathVariable String imageId) {
        if (imageId != null && !imageId.isEmpty()) {
            return this.imageService.getImageWithId(Integer.parseInt(imageId));
        }
        return new Image();
    }

    @PostMapping("/images")
    public Image handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.store(file);

        AnnotateImageResponse response =
                this.cloudVisionTemplate.analyzeImage(
                        this.resourceLoader.getResource(uploadDirectory + "/" + file.getOriginalFilename()), Type.LABEL_DETECTION);

        Map<String, Float> imageLabels = response.getLabelAnnotationsList().stream()
                .collect(
                        Collectors.toMap(
                                EntityAnnotation::getDescription,
                                EntityAnnotation::getScore, (u, v) -> {
                                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                                    }, LinkedHashMap::new));

        // For logging purposes
        imageLabels.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        //Construct the image object
        Image image = new Image(file.getOriginalFilename(), 100, uploadDirectory + "/" + file.getOriginalFilename());



        // Adding items found on the picture
        Set<String> itemNames = imageLabels.keySet();
        for (String itemName : itemNames) {
            Item item = new Item(itemName);
            image.getItems().add(item);
        }

        // Save the object to the database
        Image img = this.imageService.saveImage(image);
        System.out.println("Image saved to database: " + file.getOriginalFilename() );

        // Response to client
        return img;
    }
}
