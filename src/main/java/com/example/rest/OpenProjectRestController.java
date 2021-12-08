package com.example.rest;

import com.example.model.OpenProject;
import com.example.model.Task;
import com.example.service.GetOpenProjectDataService;
import com.example.service.OpenProjectExportExcel;
import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        return data;
    }

    @GetMapping(value = "/filter")
    public OpenProject filterData(HttpServletResponse response) throws EncoderException, IOException, URISyntaxException {

        String host = "https://vuhoangphuc.openproject.com";
        OpenProject output = null;

        String filters = "[{\"project\": { \"operator\": \"*\", \"values\": [%s] } },{\"spent_on\":{\"operator\":\"t\",\"values\":[]}}]";
        String url = host + "/api/v3/time_entries";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filters", String.format(filters, projectList));
        OpenProject data = service.callApi(builder.build().toUri());

        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        String currentTime = date.toString();


        for (int i = 0; i < data.getEmbedded().getTasks().size(); i++) {
            if (data.getEmbedded().getTasks().get(i).getSpentOn().equals(currentTime)) {

                String href = data.getEmbedded().getTasks().get(i).getLink().getWorkPackage().getHref();
                UriComponentsBuilder call = UriComponentsBuilder.fromUriString(host + href);
                output = service.callApi(call.build().toUri());

                Task task = data.getEmbedded().getTasks().get(i);

                task.setIdTask(output.getIdTask());
                task.setNameTask(output.getNameTask());
                task.setProgress(output.getPercentageDone());

            }
        }

        response.setContentType("application/json");
        DateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        OpenProject openProject = data;
        OpenProject progess = output;

        OpenProjectExportExcel excelExporter = new OpenProjectExportExcel(openProject, progess);
        excelExporter.export(response);

        return data;
    }

}
