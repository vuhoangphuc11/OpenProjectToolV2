package com.example.service;

import com.example.model.OpenProject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;

@Service
public class GetOpenProjectDataService {

    public OpenProject callApi(URI url) {
        ResponseEntity<OpenProject> response;
        RestTemplate restTemplate = new RestTemplate();

        String authStr = "apikey:ab2c6d5889ad269322cfe583c53e2f03f1469d2b8f297aa0be25e93a89a8701c";
        //Anh Quang Bstar 8f24ee3239e31dbf9007df2b990e5e895e6947d796f8bce63738ed3e0347ea1c
        //Ngan Bstar 84ce57c342d650e27df67e877ab8186d0fcb259309cf8648f1a3eefaef3fbdfc
        //Phuc Bstar 42f3853620613bc60b8dc12b49849cc0e0b542b815bab01066b1126b7b4dd135
        //Anh Hong Bstar fd92ab7caf28ee6723b1c1f2929802bc34d706149467147200f3f4e17bc42e6d
        //Phuc ca nhan ab2c6d5889ad269322cfe583c53e2f03f1469d2b8f297aa0be25e93a89a8701c
        
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        HttpEntity request = new HttpEntity(headers);
        response = new RestTemplate().exchange(url, HttpMethod.GET, request, OpenProject.class);

        return response.getBody();
    }
}
