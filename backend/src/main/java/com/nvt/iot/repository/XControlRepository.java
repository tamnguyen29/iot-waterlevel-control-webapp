package com.nvt.iot.repository;

import com.nvt.iot.document.XControlDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XControlRepository extends MongoRepository<XControlDocument, String> {
    XControlDocument findFirstById(String id);
}
