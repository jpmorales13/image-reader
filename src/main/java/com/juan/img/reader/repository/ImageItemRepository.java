package com.juan.img.reader.repository;

import com.juan.img.reader.model.ImageItem;
import com.juan.img.reader.model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageItemRepository extends CrudRepository<ImageItem, Integer> {
    List<ImageItem> findImageItemByItemEquals(Item item);
}
