package com.nvt.iot.repository;

import com.nvt.iot.document.WaterLevelStoreDocument;
import com.nvt.iot.model.WaterLevelData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaterLevelStoreRepository extends MongoRepository<WaterLevelStoreDocument, String> {
    boolean existsByUserIdAndDeviceIdAndControllerId(String userId, String deviceId, String controllerId);
    void deleteAllByUserId(String userId);
    Optional<WaterLevelStoreDocument> findByUserIdAndDeviceIdAndControllerId(String userId, String deviceId, String controllerId);
    void deleteByUserIdAndControllerIdAndDeviceId(String userId, String controllerId, String deviceId);
}
