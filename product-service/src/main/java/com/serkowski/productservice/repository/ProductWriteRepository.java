package com.serkowski.productservice.repository;

import com.serkowski.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductWriteRepository extends MongoRepository<Product, String> {
}
