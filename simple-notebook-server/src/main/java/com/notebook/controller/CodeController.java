package com.notebook.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.notebook.configuration.NotebookConfiguration;
import com.notebook.model.CodeToExecute;
import com.notebook.view.ExecutionResult;

@RestController
public class CodeController {

	@Autowired
	NotebookConfiguration myNotebookConf;

	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ExecutionResult excuteCode(@RequestBody CodeToExecute code, HttpSession session) {
		ExecutionResult result;
		code.setTimeout(myNotebookConf.getTimeout());
		result = code.runCode(session);

		return result;
	}

}