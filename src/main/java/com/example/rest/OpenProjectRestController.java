package com.example.rest;

import com.example.model.OpenProject;
import com.example.model.Task;
import com.example.service.GetOpenProjectDataService;
import com.example.service.OpenProjectExportExcel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import static com.example.common.CommonUtils.isNullOrEmpty;

@Controller
public class OpenProjectRestController {

  @Value("${host_url}")
  private String hostUrl;

  @Value("${default_path}")
  private String defaultPath;

  @Autowired
  private GetOpenProjectDataService service;

  @GetMapping(value = "/getall")
  public OpenProject getAll() throws URISyntaxException {
    String url = hostUrl + "/api/v3/projects/j5va/work_packages";
    OpenProject data = service.callApi(new URI(url));
    return data;
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  public String exportData(HttpServletResponse response,
                           @RequestParam(name = "dateStart", required = false) String dateStart,
                           @RequestParam(name = "dateEnd", required = false) String dateEnd,
                           @RequestParam(name = "projectId", required = false) String projectId,
                           @RequestParam(name = "path", required = false) String path,
                           Model model) throws IOException, ParseException {
    if(isNullOrEmpty(path)){
      path = defaultPath;
    }
    OpenProject data;
    System.out.println("Param date start = "+dateStart);
    System.out.println("Param date end = "+dateEnd);

    if (dateEnd.equalsIgnoreCase("") && isNullOrEmpty(dateEnd)) {

      //select of the day
      String filters =
          "[{\"project\": { \"operator\": \"=\", \"values\": [%s]}},{\"spentOn\": {\"operator\": \"=d\",\"values\":[\"%s\"]}}]";
      String url = hostUrl + "/api/v3/time_entries";

      UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filters",
          String.format(filters, projectId, dateStart));
      data = service.callApi(builder.build().toUri());
    } else {

      //select in date range
      String filters =
          "[{\"project\": { \"operator\": \"=\", \"values\": [%s]}},{\"spentOn\": {\"operator\": \"<>d\",\"values\":[\"%s\",\"%s\"]}}]";
      String url = hostUrl + "/api/v3/time_entries";

      UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("filters",
          String.format(filters, projectId, dateStart, dateEnd));
      data = service.callApi(builder.build().toUri());
    }

    data.getEmbedded().getTasks().parallelStream().forEach(this::process);

    response.setContentType("application/json");

    try {
      OpenProjectExportExcel excelExporter = new OpenProjectExportExcel(data, dateStart);
      excelExporter.export(path);
      String success = "Export successfully!";
      model.addAttribute("message_success", success);

    } catch (FileNotFoundException ex) {
      String fail = "Please close the file before exporting!";
      model.addAttribute("message_fail", fail);
      return "forward:/home";
    } catch (Exception e) {
      String fail = "Export fail!";
      model.addAttribute("message_fail", fail);
      return "forward:/home";
    }


    return "forward:/home";
  }

  @Async
  void process(Task task){
    String href =task.getLink().getWorkPackage().getHref();
    UriComponentsBuilder call = UriComponentsBuilder.fromUriString(hostUrl + href);
    OpenProject output = service.callApi(call.build().toUri());//TODO:

    System.out.println("ID Task :: "+task.getIdTask()+" | Date logtime :: "+task.getSpentOn());

    task.setIdTask(output.getIdTask());
    task.setNameTask(output.getNameTask());
    task.setProgress(output.getPercentageDone());
  }
}
