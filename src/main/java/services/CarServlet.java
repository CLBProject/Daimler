package services;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import templates.Groovy;

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

		Document document = Jsoup.parse(getServletContext().getResourceAsStream(request.getRequestURI()),"UTF-8",request.getRequestURI());

		Element resultGroovy = document.select("script").first();

		if(resultGroovy.attr("type").equals("server/groovy")) {
			Groovy groovy = new Groovy(request, resultGroovy.data());

			for (Element element : document.body().select("*")) {

				if(element.nodeName().equals("div") && element.hasAttr("data-loop-model")) {
					//TODO
				}
				else {
					
					for(Attribute attr: element.attributes()) {
						renderAttribute(attr, groovy);
					}
					
					if(element.hasAttr("data-if") && !Boolean.parseBoolean(element.attr("data-if"))) {
						element.text("");
					}
					else {
						element.removeAttr("data-if");
						renderElementBody(element,groovy);
					}
				}
			}
		}
		
		System.out.println(document.html());
		
		out.println(document.html());
		out.close();

	}

	private void renderElementBody(Element element, Groovy groovy) {
		String elemText = element.ownText();

		String[] splitByExp = elemText.split("\\$\\{");

		if(!elemText.equals("")) {

			element.text(splitByExp[0]);

			for(int i=1;i<splitByExp.length;i++) {
				String[] content = splitByExp[i].split("\\}");

				if(content.length > 0) {
					element.append(groovy.getExpValue(content[0]));
				}
			}
		}
		
	}
	
	private void renderAttribute(Attribute attr, Groovy groovy) {
		String elemText = attr.getValue();

		String[] splitByExp = elemText.split("\\$\\{");

		if(!elemText.equals("")) {
			
			attr.setValue(splitByExp[0]);

			for(int i=1;i<splitByExp.length;i++) {
				String[] content = splitByExp[i].split("\\}");

				if(content.length > 0) {
					attr.setValue(attr.getValue()+groovy.getExpValue(content[0]));
				}
			}
		}
		
	}
}