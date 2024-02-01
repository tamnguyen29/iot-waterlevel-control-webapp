package com.nvt.iot.repository;

import com.nvt.iot.document.DeviceControllerUserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceControllerUserRepository extends MongoRepository<DeviceControllerUserDocument, String> {
    Optional<DeviceControllerUserDocument> findByUserId(String userId);
    void deleteByUserId(String userId);
}
