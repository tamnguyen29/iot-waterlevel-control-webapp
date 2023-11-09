package com.nvt.iot.repository;

import com.nvt.iot.document.UpdateWaterLevelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UpdateWaterLevelRepository extends MongoRepository<UpdateWaterLevelDocument, String> {
}
