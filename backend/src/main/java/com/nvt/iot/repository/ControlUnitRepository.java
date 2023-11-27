package com.nvt.iot.repository;

import com.nvt.iot.document.ControlUnitDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlUnitRepository extends MongoRepository<ControlUnitDocument, String> {
    void deleteAllByCreatorId(String creatorId);

    ControlUnitDocument getControlUnitDocumentById(String id);
}
