package com.nvt.iot.repository;

import com.nvt.iot.document.ControlUnitDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControlUnitRepository extends MongoRepository<ControlUnitDocument, String> {
    boolean existsByNameAndUserId(String name, String userId);

    ControlUnitDocument getControlUnitDocumentById(String id);

    List<ControlUnitDocument> findAllByUserId(String userId);

    void deleteAllByUserId(String userId);
}
