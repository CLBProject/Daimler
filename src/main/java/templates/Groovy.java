package templates;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import templates.exceptions.MethodNotExistsOnClassException;
import templates.exceptions.VariableNotFoundException;

public class Groovy {

	Map<String,Class> classes;
	Map<String,Object> localVariables;
	Map<String,Object> stateVariables;

	public Groovy(HttpServletRequest req) {
		classes = new HashMap<String,Class>();
		localVariables = new HashMap<String,Object>();
		localVariables.put("request", req);
		stateVariables = new HashMap<String,Object>();
	}

	public void processGroovyToJava(String line) throws VariableNotFoundException, ClassNotFoundException, MethodNotExistsOnClassException {
		if(line.startsWith("import ")) {
			try {
				String[] classSplited = line.split(" ");
				String className = classSplited[1].split("\\.")[classSplited[1].split("\\.").length-1];
				classes.put(className,Class.forName(classSplited[1]));
			} catch (ClassNotFoundException e) {
				throw new ClassNotFoundException();
			}
		}
		else if(line.startsWith("def ")) {
			String[] lineSplited = line.split(" ");

			String localVariable = lineSplited[1];
			Object value = evaluateExpression(lineSplited[3]);

			localVariables.put(localVariable, value);
		}
		else {
			String[] lineSplited = line.split(" ");

			String stateVariable = lineSplited[0];
			Object value = evaluateExpression(lineSplited[2]);

			stateVariables.put(stateVariable, value);
		}
	}

	private Object evaluateExpression(String expression) throws VariableNotFoundException, MethodNotExistsOnClassException {
		if(expression.contains("\\.")) {
			String[] expSplited = expression.split("\\.");

			Class classFind = classes.get(expSplited[0]);
			Object localVariable = localVariables.get(expSplited[0]);
			Object stateVariable = stateVariables.get(expSplited[0]);

			String method = expSplited[1];

			if(classFind != null) {
				Method methodToBeCalled;
				try {
					methodToBeCalled = classFind.getMethod(method);
					methodToBeCalled.invoke(classFind);
				} catch (Exception e) {
					throw new MethodNotExistsOnClassException(e.getMessage());
				}

			}
			else if (localVariable != null) {
				//call method
			}
			else if(stateVariable != null) {
				//call method
			}
			else {
				throw new VariableNotFoundException("Variable not Found!");
			}
		}

		return expression;
	}
}
