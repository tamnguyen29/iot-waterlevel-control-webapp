package com.nvt.iot.repository;

import com.nvt.iot.document.UpdateWaterLevelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpdateWaterLevelRepository extends MongoRepository<UpdateWaterLevelDocument, String> {
    Optional<UpdateWaterLevelDocument> findByUserId(String userId);

    boolean existsByUserId(String userId);

    void deleteByUserIdAndDeviceId(String userId, String deviceId);

    void deleteByUserId(String userId);
}
