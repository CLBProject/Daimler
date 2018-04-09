package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import templates.Groovy;
import templates.exceptions.MethodNotExistsOnClassException;
import templates.exceptions.VariableNotFoundException;

public class CarServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		// Set the response message's MIME type
		response.setContentType("text/html;charset=UTF-8");
		// Allocate a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Scanner in = new Scanner(getServletContext().getResourceAsStream(request.getRequestURI()));

		boolean groovyScript = false;
		
		Groovy groovy = new Groovy(request);
		
		while(in.hasNext()) {
			String line = in.nextLine();
			
			if(line.contains("type=\"server/groovy\">")) {
				groovyScript = true;
			}
			
			else if(line.contains("</script>")){
				groovyScript = false;
			}
			
			if(groovyScript) {
				try {
					groovy.processGroovyToJava(line);
				} catch (ClassNotFoundException | VariableNotFoundException | MethodNotExistsOnClassException e) {
					out.println("<h1>" + e.getMessage() + "</h1>");
				}
			}
		}
		
		in.close();
		out.close();

	}
}