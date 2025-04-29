package com.juan.img.reader.repository;

import com.juan.img.reader.model.Image;
import com.juan.img.reader.model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface ImageRepository extends CrudRepository<Image, Integer> {
    List<Image> findImagesByItems(Set<Item> items);
}