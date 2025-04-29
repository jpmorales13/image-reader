package com.juan.img.reader.controllers;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature.Type;
import com.juan.img.reader.objects.Image;
import com.juan.img.reader.objects.Item;
import com.juan.img.reader.repository.ImageItemRepository;
import com.juan.img.reader.repository.ImageRepository;
import com.juan.img.reader.repository.ItemRepository;
import com.juan.img.reader.service.FileSystemStorageService;
import com.juan.img.reader.service.StorageFileNotFoundException;
import com.juan.img.reader.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ImageReaderController {

    private final String uploadDirectory = "file:C:/Dev/uploads"; // Define your upload directory

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private CloudVisionTemplate cloudVisionTemplate;

    private final StorageService storageService;
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;

    @Autowired public ImageReaderController(StorageService storageService, ImageRepository imageRepository, ItemRepository itemRepository) {
        this.storageService = storageService;
        this.imageRepository = imageRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/images")
    public Set<Image> returnImages(@RequestParam(value = "objects", defaultValue = "") String objects) {
        Set<Image> images = new HashSet<>();

        // If there are no object parameters, then simply return the list of image objects
        if (objects == null || objects.isEmpty()) {
            for (Image img : this.imageRepository.findAll()) {
                images.add(img);
            }
        } else {
            // When filtering is passed in the form of objects then we need to find by object
            List<Item> items = new ArrayList<>();
            String[] objectArray = objects.split(",");

            for (String objName : objectArray) {
                items.addAll(this.itemRepository.findItemsByNameEqualsIgnoreCase(objName));
            }

            for (Item item : items) {
                images.addAll(item.getImage());
            }
        }

        return images;
    }

    @GetMapping("/images/{imageId}")
    public Image returnImagesById(@PathVariable String imageId) {
        if (imageId != null && !imageId.isEmpty()) {
            Integer id = Integer.parseInt(imageId);
            Optional<Image> img = this.imageRepository.findById(id);
            if (img.isPresent()) {
                return img.get();
            }
        }
        return new Image();
    }

    @PostMapping("/images")
    public Image handleFileUpload(@RequestParam("file") MultipartFile file) {
        storageService.store(file);

        AnnotateImageResponse response =
                this.cloudVisionTemplate.analyzeImage(
                        this.resourceLoader.getResource(uploadDirectory + "/" + file.getOriginalFilename()), Type.LABEL_DETECTION);

        Map<String, Float> imageLabels =
                response.getLabelAnnotationsList().stream()
                        .collect(
                                Collectors.toMap(
                                        EntityAnnotation::getDescription,
                                        EntityAnnotation::getScore,
                                        (u, v) -> {
                                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                                        },
                                        LinkedHashMap::new));

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
        Image img =  this.imageRepository.save(image);
        System.out.println("Image saved to database: " + file.getOriginalFilename() );

        // Response to client
        return img;
    }
}
