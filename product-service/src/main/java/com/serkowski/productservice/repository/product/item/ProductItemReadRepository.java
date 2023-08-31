package com.serkowski.productservice.repository.product.item;

import com.serkowski.productservice.model.ProductItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductItemReadRepository extends MongoRepository<ProductItem, String> {

    Optional<ProductItem> findBySerialNumber(String serialNumber);

    @Query("select p from ProductItem p where p.id in :ids")
    List<ProductItem> findByIds(@Param("ids") List<String> ids);

    @Query("select p from ProductItem p where p.serialNumber in :serialNumbers")
    List<ProductItem> findBySerialNumbers(@Param("serialNumbers") List<String> serialNumbers);

}
