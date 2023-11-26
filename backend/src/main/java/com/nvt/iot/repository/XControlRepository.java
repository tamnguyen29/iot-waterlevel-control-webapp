package com.nvt.iot.repository;

import com.nvt.iot.document.XControlDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface XControlRepository extends MongoRepository<XControlDocument, String> {
    Optional<XControlDocument> findByDeviceId(String deviceId);

    void deleteByDeviceId(String deviceId);

    boolean existsByDeviceId(String deviceId);
}
