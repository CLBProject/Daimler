package templates;

import java.util.Collection;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * Class Responsable for evaluate Groovy expressions and return the rendered result
 * @author nobre
 *
 */
public class Groovy {

	GroovyShell shell;

	public Groovy(String groovyScript,Map<String,Object> variables) {
		Binding binding = new Binding();
		
		variables.entrySet().forEach(variable -> binding.setProperty(variable.getKey(), variable.getValue()));
		
		shell = new GroovyShell(binding);
		shell.evaluate(groovyScript);
	}
	
	/**
	 * Receives an expression of type "x.y" and returns groovy getY() from x Class current value as String;
	 * @param string
	 * @return
	 */
	public String getExpValue(String className, String methodName) {
		
		String getMethodClassGroovy = className+".get"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1)+"()";

		return "" + shell.evaluate(getMethodClassGroovy);
	}

	/**
	 * Receives an expression of type "x.y" and returns groovy isY() from x Class current value as Boolean;
	 * @param string
	 * @return
	 */
	public boolean isExpValue(String className, String methodName) {

		String isMethodClassGroovy = className+".is"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1)+"()";
		
		return (Boolean) shell.evaluate(isMethodClassGroovy);
	}

	/**
	 * Receives an expression of type "x.y" and returns groovy getY() from x Class current value as Collection;
	 * @param string
	 * @return
	 */
	public Collection<?> getExpCollection(String className, String methodName) {
		
		String getMethodClassGroovy = className+".get"+methodName.substring(0, 1).toUpperCase()+methodName.substring(1)+"()";

		return (Collection<?>)shell.evaluate(getMethodClassGroovy);

	}
}
