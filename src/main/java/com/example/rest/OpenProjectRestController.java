package com.example.rest;

import com.example.model.OpenProject;
import com.example.model.Task;
import com.example.service.GetOpenProjectDataService;
import com.example.service.OpenProjectExportExcel;
import com.example.util.DateTimeUtil;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class OpenProjectRestController {

    @Value("${project_list}")
    private String projectList;
    @Value("${host_url}")
    private String hostUrl;

    @Autowired
    private GetOpenProjectDataService service;

    @GetMapping(value = "/getall")
    public OpenProject getAll() throws URISyntaxException {
        String url = "https://vuhoangphuc.openproject.com/api/v3/projects/j5va/work_packages";

        OpenProject data = service.callApi(new URI(url));
        return data;
    }

    @GetMapping(value = "/filter")
    public void filterData(HttpServletResponse response) throws EncoderException, IOException, URISyntaxException {

        String currentDate = DateTimeUtil.dateToString(new Date(), DateTimeUtil.YYYY_MM_DD_FORMAT);

        String filters = "[{\"project\": { \"operator\": \"*\", \"values\": [%s]}},{\"spent_on\":{\"operator\":\"=d\",\"values\":[\"%s\"]}}]";
        String url = hostUrl + "/api/v3/time_entries";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filters",
                String.format(filters, projectList, currentDate));
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

        String headerKey = "Content-Disposition";
        //String headerValue = "attachment; filename=Daily_Report_" + currentDate + ".xlsx";
        String headerValue = "attachment; filename=Daily_Report.xlsx";
        response.setHeader(headerKey, headerValue);

        OpenProjectExportExcel excelExporter = new OpenProjectExportExcel(data);
        excelExporter.export(response);
    }

}
