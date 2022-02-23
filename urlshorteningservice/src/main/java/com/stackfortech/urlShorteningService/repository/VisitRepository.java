package com.stackfortech.urlShorteningService.repository;

import com.stackfortech.urlShorteningService.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    public long countByUrl(String url);

}
