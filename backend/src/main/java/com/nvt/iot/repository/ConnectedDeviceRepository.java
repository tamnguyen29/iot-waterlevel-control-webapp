package com.nvt.iot.repository;

import com.nvt.iot.document.ConnectedDeviceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectedDeviceRepository extends MongoRepository<ConnectedDeviceDocument, String> {
    boolean existsBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);

    boolean existsByCurrentUsingUserSessionId(String sessionId);

    ConnectedDeviceDocument findByCurrentUsingUserSessionId(String sessionId);

    boolean existsByIdAndCurrentUsingUserId(String deviceId, String userId);

    ConnectedDeviceDocument findByCurrentUsingUserId(String userId);
}
