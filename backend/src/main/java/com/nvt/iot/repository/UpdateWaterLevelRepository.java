package com.nvt.iot.repository;

import com.nvt.iot.document.UpdateWaterLevelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpdateWaterLevelRepository extends MongoRepository<UpdateWaterLevelDocument, String> {
    Optional<UpdateWaterLevelDocument> findByDeviceId(String userId);

    boolean existsByDeviceId(String deviceId);

    void deleteByDeviceId(String deviceId);
}
