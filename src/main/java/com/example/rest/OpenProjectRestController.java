package com.example.rest;

import com.example.model.OpenProject;
import com.example.model.Task;
import com.example.service.GetOpenProjectDataService;
import com.example.service.OpenProjectExportExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

@RestController
public class OpenProjectRestController {

    @Value("${host_url}")
    private String hostUrl;

    @Autowired
    private GetOpenProjectDataService service;

    @GetMapping(value = "/getall")
    public OpenProject getAll() throws URISyntaxException {
        String url = hostUrl + "/api/v3/projects/j5va/work_packages";
        OpenProject data = service.callApi(new URI(url));
        return data;
    }

    @GetMapping(value = "/export")
    public String exportData(HttpServletResponse response,
                             @RequestParam(name = "date", required = false) String date,
                             @RequestParam(name = "projectId", required = false) String projectId,
                             @RequestParam(name = "path", required = false) String path) throws IOException, ParseException {
        String filters = "[{\"project\": { \"operator\": \"=\", \"values\": [%s]}},{\"spent_on\":{\"operator\":\"=d\",\"values\":[\"%s\"]}}]";
        String url = hostUrl + "/api/v3/time_entries";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filters",
                String.format(filters, projectId, date));
        OpenProject data = service.callApi(builder.build().toUri());

        for (int i = 0; i < data.getEmbedded().getTasks().size(); i++) {

            String href = data.getEmbedded().getTasks().get(i).getLink().getWorkPackage().getHref();
            UriComponentsBuilder call = UriComponentsBuilder.fromUriString(hostUrl + href);
            OpenProject output = service.callApi(call.build().toUri());

            Task task = data.getEmbedded().getTasks().get(i);

            task.setIdTask(output.getIdTask());
            task.setNameTask(output.getNameTask());
            task.setProgress(output.getPercentageDone());
        }

        response.setContentType("application/json");

        try {
            OpenProjectExportExcel excelExporter = new OpenProjectExportExcel(data, date);
            excelExporter.export(path);
        } catch (FileNotFoundException e) {
            return "<center><h2>It looks like you have a report file open please close and try again!</h2></center>";
        }


        return "<center><h2>Success update daily report!</h2></center>";
    }
}
