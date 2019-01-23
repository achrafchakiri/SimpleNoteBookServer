package com.notebook.model;

import java.util.concurrent.Callable;
import org.python.util.PythonInterpreter;

public class MyPythonInterpreter implements Callable<PythonInterpreter> {

	public MyPythonInterpreter(String codeToExecute, PythonInterpreter interpreter) {
		this.codeToExecute = codeToExecute;
		this.interpreter = interpreter;
	}

	private String codeToExecute;
	private PythonInterpreter interpreter;

	@Override
	public PythonInterpreter call() throws Exception {
		interpreter.exec(codeToExecute);
		return interpreter;
	}

	public PythonInterpreter getInterpreter() {
		return interpreter;
	}

	public void setInterpreter(PythonInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	public void setCodeToExecute(String codeToExecute) {
		this.codeToExecute = codeToExecute;
	}
}
