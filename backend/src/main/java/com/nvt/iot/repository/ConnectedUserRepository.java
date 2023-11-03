package com.nvt.iot.repository;

import com.nvt.iot.document.ConnectedUserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectedUserRepository extends MongoRepository<ConnectedUserDocument, String> {
    void deleteBySessionId(String sessionId);
}
