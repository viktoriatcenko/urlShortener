package com.stackfortech.urlShorteningService.controller;

import com.stackfortech.urlShorteningService.model.*;
import com.stackfortech.urlShorteningService.service.UrlService;
import com.stackfortech.urlShorteningService.service.VisitService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class UrlShorteningController
{
    private final UrlService urlService;
    private final VisitService visitService;
    @Autowired
    public UrlShorteningController(UrlService urlService, VisitService visitService) {
        this.urlService = urlService;
        this.visitService = visitService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto, @RequestHeader HttpHeaders httpHeaders)
    {
        if(!urlService.isValidRequest(urlDto, httpHeaders)){
            return new ResponseEntity<>("Not authorized", HttpStatus.FORBIDDEN);
    }
        Url urlToRet = urlService.generateShortLink(urlDto);


        if(urlToRet != null)
        {
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
            urlResponseDto.setShortLink(urlToRet.getShortLink());
            return new ResponseEntity<UrlResponseDto>(urlResponseDto, HttpStatus.OK);
        }

        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("404");
        urlErrorResponseDto.setError("There was an error processing your request. please try again.");
        return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);

    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response) throws IOException {

        if(StringUtils.isEmpty(shortLink))
        {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid Url");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
        }
        Url urlToRet = urlService.getEncodedUrl(shortLink);

        if(urlToRet == null)
        {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Url does not exist or it might have expired!");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
        }

        if(urlToRet.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            urlService.deleteShortLink(urlToRet);
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Url Expired. Please try generating a fresh one.");
            urlErrorResponseDto.setStatus("200");
            return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
        }


            Visit visit = new Visit(shortLink);
            visitService.save(visit);

            response.sendRedirect(urlToRet.getOriginalUrl());

        return null;
    }

    @GetMapping("/visits")
    public ResponseEntity<?> visits() {
        return new ResponseEntity<>(visitService.count(), HttpStatus.OK);
    }

    @GetMapping("/visits/{shortLink}")
    public ResponseEntity<?> visitsByShortLink(@PathVariable String shortLink) {
        return new ResponseEntity<>(visitService.countByUrl(shortLink), HttpStatus.OK);
    }
}
