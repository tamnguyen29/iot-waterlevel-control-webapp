package com.nvt.iot.repository;

import com.nvt.iot.document.ConnectedUserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConnectedUserRepository extends MongoRepository<ConnectedUserDocument, String> {
    void deleteBySessionId(String sessionId);

    Optional<ConnectedUserDocument> findBySessionId(String sessionId);
}
