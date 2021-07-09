package com.saltlux.khnp.searcher.search.repository;

import com.saltlux.khnp.searcher.search.model.PlantInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantInformationRepository extends JpaRepository<PlantInformation, String> {

    public List<PlantInformation> findByPrefixStartsWith(String prefix);
}
