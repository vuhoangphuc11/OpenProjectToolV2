package com.example.controller;

import com.example.model.OpenProject;
import com.example.model.Task;
import com.example.service.GetOpenProjectDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Controller
public class HomeController {

    @Value("${host_url}")
    private String hostUrl;

    @Autowired
    private GetOpenProjectDataService service;

    @RequestMapping(value = {"/", "home"}, method = RequestMethod.GET)
    public String home(Model model) throws URISyntaxException {

        String url = hostUrl + "/api/v3/projects";
        OpenProject data = service.callApi(new URI(url));
        ArrayList<Task> arr = new ArrayList<>();
        for (Task task : data.getEmbedded().getTasks()) {
            arr.add(task);
        }
        model.addAttribute("arr", arr);
        return "index";
    }
}
