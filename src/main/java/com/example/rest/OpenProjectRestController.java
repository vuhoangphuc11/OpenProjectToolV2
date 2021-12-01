package com.example.rest;

import com.example.model.OpenProject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class OpenProjectRestController {

    @Value("${project_list}")
    private String projectList;

    @GetMapping(value = "/getall")
    public OpenProject getAll() throws EncoderException {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://vuhoangphuc.openproject.com/api/v3/projects/j5va/work_packages";

        String authStr = "YXBpa2V5OjBmNDc4NTg2YWJmYjJhN2M4ODM3N2VlYjdjZmJmNWM3MWVlYzY4M2YxMDM3ZjA1MGQ1ZjA2MmM5ODMxNDBkNWU=";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + authStr);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<OpenProject> response = new RestTemplate().exchange(url, HttpMethod.GET, request, OpenProject.class);

        OpenProject data = response.getBody();

        System.out.println(data.getEmbedded());
        System.out.println(projectList);

        return data;
    }

    @GetMapping(value = "/fillter")
    public OpenProject fillterData() throws EncoderException {

        String filters = "[{\"project\": { \"operator\": \"=\", \"values\": [%s] } },{\"spent_on\":{\"operator\":\"t\",\"values\":[]}}]";
        String url = "https://vuhoangphuc.openproject.com/api/v3/time_entries";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filter", String.format(filters, projectList));

        String authStr = "YXBpa2V5OjBmNDc4NTg2YWJmYjJhN2M4ODM3N2VlYjdjZmJmNWM3MWVlYzY4M2YxMDM3ZjA1MGQ1ZjA2MmM5ODMxNDBkNWU=";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + authStr);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<OpenProject> response = new RestTemplate().exchange(builder.build().toUri(), HttpMethod.GET, request, OpenProject.class);

        OpenProject data = response.getBody();

        return data;
    }
}
