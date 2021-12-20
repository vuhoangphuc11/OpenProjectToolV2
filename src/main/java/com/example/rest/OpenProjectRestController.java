package com.example.rest;

import com.example.model.Links;
import com.example.model.OpenProject;
import com.example.model.Task;
import com.example.service.GetOpenProjectDataService;
import com.example.service.OpenProjectExportExcel;
import com.example.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

@RestController
public class OpenProjectRestController {

    @Value("${project_list}")
    private String projectList;
    @Value("${host_url}")
    private String hostUrl;
//    @Value("${file_path}")
//    private String filePath;

    @Autowired
    private GetOpenProjectDataService service;

    @GetMapping(value = "/getall")
    public OpenProject getAll() throws URISyntaxException {
        String url = hostUrl + "/api/v3/projects/j5va/work_packages";
        OpenProject data = service.callApi(new URI(url));
        return data;
    }

    @GetMapping(value = "/export")
    public String filterData(HttpServletResponse response,
                             @RequestParam(name = "date", required = false) String date,
//                             @RequestParam(name = "projectId", required = false) String projectId,
                             @RequestParam(name = "path", required = false) String path) throws IOException, ParseException {
        String filters = "[{\"project\": { \"operator\": \"*\", \"values\": [%s]}},{\"spent_on\":{\"operator\":\"=d\",\"values\":[\"%s\"]}}]";
        String url = hostUrl + "/api/v3/time_entries";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filters",
                String.format(filters, projectList, date));
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

        OpenProjectExportExcel excelExporter = new OpenProjectExportExcel(data,date);
        excelExporter.export(path);

        return "Success update daily report!";
    }

}
