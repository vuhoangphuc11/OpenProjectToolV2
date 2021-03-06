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
public class OpenProject {
	@JsonProperty("_embedded")
	Embedded embedded;
	@JsonProperty("percentageDone")
	Double percentageDone;
	@JsonProperty("id")
	int idTask;
	@JsonProperty("subject")
	String nameTask;
}
