package com.example.service;

import com.example.model.OpenProject;
import java.net.URI;
import java.util.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GetOpenProjectDataService {

  public OpenProject callApi(URI url) {
    ResponseEntity<OpenProject> response;
    RestTemplate restTemplate = new RestTemplate();

    String authStr = "apikey:b2805f1a021f4a21795b34d12297c163b3453a31c60120aee0d8b21176d67ef6";

    String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + base64Creds);

    HttpEntity request = new HttpEntity(headers);
    response = new RestTemplate().exchange(url, HttpMethod.GET, request, OpenProject.class);

    return response.getBody();
  }
}
