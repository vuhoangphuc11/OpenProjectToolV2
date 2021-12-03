package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Links {
    @JsonProperty("project")
    Project project;
    @JsonProperty("assignee")
    Assignee assignee;
    @JsonProperty("user")
    User user;
    @JsonProperty("workPackage")
    WorkPackage workPackage;
}
