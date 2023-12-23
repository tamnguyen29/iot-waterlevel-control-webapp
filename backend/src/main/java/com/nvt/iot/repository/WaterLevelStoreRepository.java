package com.nvt.iot.repository;

import com.nvt.iot.document.WaterLevelStoreDocument;
import com.nvt.iot.model.WaterLevelData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterLevelStoreRepository extends MongoRepository<WaterLevelStoreDocument, String> {
    @Query("{ 'userID': ?0, 'controllerID': ?1, 'deviceID': ?2 }")
    @Update("{ '$push': { 'water_level': ?3 } }")
    void addWaterLevelData(String userId, String controllerId, String deviceId, WaterLevelData waterLevelData);

    boolean existsByUserIdAndDeviceIdAndControllerId(String userId, String deviceId, String controllerId);

    void deleteAllByUserId(String userId);
}
