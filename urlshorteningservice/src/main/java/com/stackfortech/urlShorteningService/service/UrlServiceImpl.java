package com.stackfortech.urlShorteningService.service;

import com.google.common.hash.Hashing;
import com.stackfortech.urlShorteningService.model.Url;
import com.stackfortech.urlShorteningService.model.UrlDto;
import com.stackfortech.urlShorteningService.repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class UrlServiceImpl implements UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlServiceImpl.class);
    private final UrlRepository urlRepository;
    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public Url generateShortLink(UrlDto urlDto) {

        if(StringUtils.isNotEmpty(urlDto.getUrl()))
        {
            String encodedUrl = encodeUrl(urlDto.getUrl());
            Url urlToPersist = new Url();
            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(urlDto.getUrl());
            urlToPersist.setShortLink(encodedUrl);
            urlToPersist.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(),urlToPersist.getCreationDate()));
            Url urlToRet = persistShortLink(urlToPersist);

            if(urlToRet != null)
                return urlToRet;

            return null;
        }
        return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate)
    {
        if(StringUtils.isBlank(expirationDate))
        {
            return creationDate.plusSeconds(6000);
        }
        LocalDateTime expirationDateToRet = LocalDateTime.parse(expirationDate);
        return expirationDateToRet;
    }

    private String encodeUrl(String url)
    {
        String encodedUrl = "";
        LocalDateTime time = LocalDateTime.now();
        encodedUrl = Hashing.murmur3_32()
                .hashString(url.concat(time.toString()), StandardCharsets.UTF_8)
                .toString();
        return  encodedUrl;
    }

    @Override
    public Url persistShortLink(Url url) {
        Url urlToRet = urlRepository.save(url);
        return urlToRet;
    }

    @Override
    public Url getEncodedUrl(String url) {
        Url urlToRet = urlRepository.findByShortLink(url);
        return urlToRet;
    }

    @Override
    public void deleteShortLink(Url url) {
        urlRepository.delete(url);
    }

    @Override
    public boolean isValidRequest(UrlDto urlDto, HttpHeaders httpHeaders) {
        if (StringUtils.isBlank(urlDto.getUserId())) {
            return false;
        }
        ArrayList<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(urlDto.getExpirationDate())) {
            list.add("expirationDate=" + (urlDto.getExpirationDate()));
        }
        if (!urlDto.getUrl().isEmpty()) {
            list.add("url=" + (urlDto.getUrl()));
        }
        if (!urlDto.getUserId().isEmpty()) {
            list.add("userId=" + (urlDto.getUserId()));
        }
        String joined = String.join("&", list);

        String secretKey = "qwerty"; // условно секретный ключ
        joined+=secretKey;

        String result = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.reset();
            messageDigest.update(joined.getBytes(StandardCharsets.UTF_8));
            result = String.format("%040x", new BigInteger(1, messageDigest.digest()));
            System.out.println(result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (result.equals(httpHeaders.getFirst("Authorization"))) {
            return true;
        }

        return false;
    }
}
