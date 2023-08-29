package com.serkowski.productservice.repository.product.item;

import com.serkowski.productservice.model.ProductItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductItemReadRepository extends MongoRepository<ProductItem, String> {


}
