package com.nvt.iot.repository;

import com.nvt.iot.document.DeviceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends MongoRepository<DeviceDocument, String> {
    boolean existsByName(String name);

    @Query("{ 'name' : ?0, '_id' : { '$ne' : ?1 } }")
    boolean existsByNameAndNotEqualId(String name, String id);

    boolean existsById(String id);

    DeviceDocument findByName(String name);

}
