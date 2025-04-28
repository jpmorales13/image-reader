package com.juan.img.reader.repository;

import com.juan.img.reader.objects.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Integer> {
    List<Item> findItemsByNameEqualsIgnoreCase(String name);
}
