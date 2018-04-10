package templates;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class Groovy {

	GroovyShell shell;

	public Groovy(HttpServletRequest req, String groovyScript) {
		Binding binding = new Binding();
		binding.setVariable("request", req);
		shell = new GroovyShell(binding);
		shell.evaluate(groovyScript);
	}

	public String getExpValue(String string) {

		String[] expSplited = string.split("\\.");

		return "" + shell.evaluate(expSplited[0]+".get"+expSplited[1].substring(0, 1).toUpperCase()+expSplited[1].substring(1)+"()");
	}

	public boolean validateDataIf(String attr) {

		String[] expSplited = attr.split("\\.");

		return (Boolean) shell.evaluate(expSplited[0]+".is"+expSplited[1].substring(0, 1).toUpperCase()+expSplited[1].substring(1)+"()");
	}

	public Collection evaluateCollection(String groovyExp) {
		String[] expSplited = groovyExp.split("\\.");

		return (Collection)shell.evaluate(expSplited[0]+".get"+expSplited[1].substring(0, 1).toUpperCase()+expSplited[1].substring(1)+"()");

	}
}
