package com.nvt.iot.repository;

import com.nvt.iot.document.WaterLevelStoreDocument;
import com.nvt.iot.model.WaterLevelStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaterLevelStoreRepository extends MongoRepository<WaterLevelStoreDocument, String> {
    @Query(value = "{ 'creator.id': ?0, 'controller.id': ?1 }")
    List<WaterLevelStore> findAllWaterLevelsByCreatorIdAndControllerId(String creatorId, String controllerId);
}
