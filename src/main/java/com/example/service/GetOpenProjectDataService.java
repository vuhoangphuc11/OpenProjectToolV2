package com.example.service;

import com.example.model.OpenProject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class GetOpenProjectDataService {
    @Value("${auth_key}")
    private String authStr;

    public OpenProject callApi(URI url) {
        ResponseEntity<OpenProject> response;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + authStr);

        HttpEntity request = new HttpEntity(headers);
        response = new RestTemplate().exchange(url, HttpMethod.GET, request, OpenProject.class);

        return response.getBody();
    }
}
