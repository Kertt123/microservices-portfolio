package com.serkowski.productservice.repository;

import com.serkowski.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ProductWriteRepository extends MongoRepository<Product, String> {
}
