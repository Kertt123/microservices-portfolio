package com.serkowski.productservice.repository.product;

import com.serkowski.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductReadRepository extends MongoRepository<Product, String> {

}
