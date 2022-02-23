package com.stackfortech.urlShorteningService.service;

import com.stackfortech.urlShorteningService.model.Visit;
import org.springframework.stereotype.Service;

@Service
public interface VisitService {

    public long count();
    public long countByUrl(String url);
    public void save(Visit visit);

}
