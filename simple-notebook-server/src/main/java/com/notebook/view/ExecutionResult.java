package com.notebook.view;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExecutionResult {

	@JsonProperty("output")
	private String output;

	@JsonProperty("result")
	private String result;

	public ExecutionResult(String output) {
		this.result = "resut";
		this.output = output;
	}

	public String getOutput() {
		return output;
	}

	public String getResult() {
		return result;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
