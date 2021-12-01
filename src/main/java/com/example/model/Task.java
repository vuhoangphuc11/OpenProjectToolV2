package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    @JsonProperty("id")
    int IdTask;
    @JsonProperty("subject")
    String NameTask;
    @JsonProperty("percentageDone")
    Double Progess;
    @JsonProperty("_links")
    Links link;
    @JsonProperty("spentOn")
    String SpentOn;
}
