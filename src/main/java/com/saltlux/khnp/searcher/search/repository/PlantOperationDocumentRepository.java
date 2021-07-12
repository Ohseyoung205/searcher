package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.PlantOperationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantOperationDocumentRepository extends JpaRepository<PlantOperationDocument, Integer> {

//    @Query("select d from PlantOperationDocument d where d.documentId is not null")
    public List<PlantOperationDocument> findByDomainTableNotNull();

    public Optional<PlantOperationDocument> findByDocumentId(Integer documentId);
}
