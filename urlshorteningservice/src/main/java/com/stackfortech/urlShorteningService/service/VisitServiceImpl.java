package com.stackfortech.urlShorteningService.service;

import com.stackfortech.urlShorteningService.model.Visit;
import com.stackfortech.urlShorteningService.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitServiceImpl implements VisitService{

    private VisitRepository visitRepository;

    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    public long count() {
        return visitRepository.count();
    }

    @Override
    public long countByUrl(String url) {
        return visitRepository.countByUrl(url);
    }

    @Override
    public void save(Visit visit) {
        visitRepository.save(visit);
    }
}
