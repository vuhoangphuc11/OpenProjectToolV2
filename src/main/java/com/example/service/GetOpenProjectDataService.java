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

    String authStr = "apikey:8f24ee3239e31dbf9007df2b990e5e895e6947d796f8bce63738ed3e0347ea1c";

    String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + base64Creds);

    HttpEntity request = new HttpEntity(headers);
    response = new RestTemplate().exchange(url, HttpMethod.GET, request, OpenProject.class);

    return response.getBody();
  }
}
