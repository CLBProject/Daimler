package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import templates.GroovyHtmlParser;

public class CarServlet extends HttpServlet {
	/**
	 * @author Carlos Nobre
	 * 
	 * Responsable for receiving the Requests from server invocations for HTML Files
	 */
	
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try (PrintWriter out = response.getWriter()){
			
			GroovyHtmlParser parser = new GroovyHtmlParser(
					getServletContext().getResourceAsStream(request.getRequestURI()),request.getRequestURI());

			
			Map<String,Object> variables = new HashMap<String,Object>();
			variables.put("request",request);

			parser.renderGroovyHtml(variables);
			response.setContentType("text/html;charset=UTF-8");
			out.println(parser.getCurrentHtml());
			
		} catch (IOException e) {
			throw e;
		}
	}
}