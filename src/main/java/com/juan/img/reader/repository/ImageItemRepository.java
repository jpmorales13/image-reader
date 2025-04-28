package com.juan.img.reader.repository;

import com.juan.img.reader.objects.ImageItem;
import com.juan.img.reader.objects.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageItemRepository extends CrudRepository<ImageItem, Integer> {
    List<ImageItem> findImageItemByItemEquals(Item item);
}
