package com.juan.img.reader.service;

import com.juan.img.reader.model.Image;
import java.util.Set;

public interface ImageService {
    Set<Image> getImages();
    Set<Image> getImagesFromItemArray(String[] itemArray);
    Image getImageWithId(Integer id);
    Image saveImage(Image image);
}
