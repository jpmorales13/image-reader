package com.juan.img.reader.service;

import com.juan.img.reader.model.Image;
import com.juan.img.reader.model.Item;
import com.juan.img.reader.repository.ImageRepository;
import com.juan.img.reader.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DatabaseImageService implements ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired private ItemRepository itemRepository;

    @Override
    public Set<Image> getImages() {
        Set<Image> images = new HashSet<>();
        for (Image img : this.imageRepository.findAll()) {
            images.add(img);
        }

        return images;
    }

    @Override
    public Set<Image> getImagesFromItemArray(String[] objectArray) {
        List<Item> items = new ArrayList<>();
        Set<Image> images = new HashSet<>();

        for (String objName : objectArray) {
            items.addAll(this.itemRepository.findItemsByNameEqualsIgnoreCase(objName));
        }

        for (Item item : items) {
            images.addAll(item.getImage());
        }

        return images;
    }

    @Override
    public Image getImageWithId(Integer id) {
        Optional<Image> img = this.imageRepository.findById(id);
        return img.orElse(null);
    }

    @Override
    public Image saveImage(Image image) {
        return this.imageRepository.save(image);
    }
}
