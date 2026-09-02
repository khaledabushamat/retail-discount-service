package io.github.khaledabushamat.discount.catalog.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {}
