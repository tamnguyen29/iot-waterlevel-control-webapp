package com.nvt.iot.repository;

import com.nvt.iot.document.ConnectedDeviceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConnectedDeviceRepository extends MongoRepository<ConnectedDeviceDocument, String> {
    boolean existsBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);

    boolean existsByIdAndCurrentUsingUserId(String deviceId, String userId);

    Optional<ConnectedDeviceDocument> findByCurrentUsingUserId(String userId);

    Optional<ConnectedDeviceDocument> findBySessionId(String sessionId);
}
