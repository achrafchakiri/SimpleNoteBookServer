package com.notebook.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpSession;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.notebook.view.ExecutionResult;

@Component
public class CodeToExecute {

	private String language;

	@JsonProperty("code")
	private String statement;

	private int timeout;

	public CodeToExecute() {
	}

	public CodeToExecute(String statement) {
		this.statement = statement;
	}

	public String getStatement() {
		return statement;
	}

	public void handleSession(HttpSession session) {
		if (session.isNew()) {
			System.out.println("New session: " + session.getId());
			if (this.language.equalsIgnoreCase("python")) {
				session.setAttribute("python", new PythonInterpreter());
			} else {
				// To do for other languages interpreters
				return;
			}
		} else {
			System.out.println(
					"Reused session: " + session.getId() + " Parm is set to: " + session.getAttribute("python"));

		}
	}

	public ExecutionResult runCode(HttpSession session) {
		handleSession(session);
		String output = "";

		if (language.equalsIgnoreCase("python")) {
			if (statement.equalsIgnoreCase("")) {
				return new ExecutionResult("Please fill the code");
			}
			PythonInterpreter interpreter = (PythonInterpreter) session.getAttribute("python");
			try {
				boolean getresult = false;
				if (statement.contains("print")) {
					getresult = true;
				}
				int occurence = StringUtils.countOccurrencesOf(statement, "print");
				String codeToExecute = statement;
				ArrayList<String> results = new ArrayList<String>();
				while (occurence > 0) {
					results.add("result" + occurence);
					codeToExecute = codeToExecute.replaceFirst("print", "result" + occurence + "=");
					occurence--;
				}
				try {
					if (codeToExecute.equals(session.getAttribute("codeToExecute"))) {
						return new ExecutionResult("Code Already executed!");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				ExecutorService executor = Executors.newCachedThreadPool();
				Callable<PythonInterpreter> task = new MyPythonInterpreter(codeToExecute, interpreter);
				Future<PythonInterpreter> future = executor.submit(task);
				try {
					interpreter = future.get(timeout, TimeUnit.SECONDS);
					session.setAttribute("codeToExecute",codeToExecute);
					Iterator<String> result = results.iterator();
					while (result.hasNext()) {
						String res = result.next();
						PyObject pyResult = interpreter.get(res);
						output += " " + pyResult.toString();
					}

					if (!getresult) {
						output = "";
					}
				} catch (TimeoutException e) {
					output = "Execution took too much time!";
				} catch (Exception e) {
					throw e;
				} finally {
					future.cancel(true); // may or may not desire this
				}

			} catch (Exception exc) {
				exc.printStackTrace();
				output = "Oops ! The statement had raised an exception\\n" + exc.getMessage();
			}
			session.setAttribute("python", interpreter);
		} else {
			output = "The language demanded is not yet implemented";
		}

		return new ExecutionResult(output);
	}

	public void setStatement(String statement) {
		String arr[] = statement.split(" ", 2);
		String language = arr[0];
		String codeToExec = arr[1];
		this.statement = codeToExec;
		this.language = language;

	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "CodeToExecute [statement=  " + this.statement + "]";
	}
}
