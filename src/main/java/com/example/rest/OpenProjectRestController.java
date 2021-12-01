package com.example.rest;

import com.example.model.OpenProject;
import com.example.service.GetOpenProjectDataService;
import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class OpenProjectRestController {

    @Value("${project_list}")
    private String projectList;

    @Autowired
    private GetOpenProjectDataService service;

    @GetMapping(value = "/getall")
    public OpenProject getAll() throws URISyntaxException {
        String url = "https://vuhoangphuc.openproject.com/api/v3/projects/j5va/work_packages";

        OpenProject data = service.callApi(new URI(url));

        System.out.println(data.getEmbedded());
        System.out.println(projectList);

        return data;
    }

    @GetMapping(value = "/filter")
    public OpenProject filterData() throws EncoderException {
        String filters = "[{\"project\": { \"operator\": \"=\", \"values\": [%s] } },{\"spent_on\":{\"operator\":\"t\",\"values\":[]}}]";
        String url = "https://vuhoangphuc.openproject.com/api/v3/time_entries";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filter", String.format(filters, projectList));
        OpenProject data = service.callApi(builder.build().toUri());

        return data;
    }
}
