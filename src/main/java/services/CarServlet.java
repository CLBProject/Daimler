package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

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

		Groovy groovy = null;
		
		if(resultGroovy.attr("type").equals("server/groovy")) {
			groovy = new Groovy(request, resultGroovy.data());
		}

		resultGroovy.remove();
		
		Element body = document.select("body").first();

		for (Element element : document.select("*")) {
			if(element.nodeName().equals("div")) {
				for(Attribute attr : element.attributes()) {
					if (attr.getKey().startsWith("data-loop-")) {
						String groovyVar = attr.getKey().split("data-loop-")[1];
						String groovyExp = attr.getValue();
						
						String elementText = element.ownText();
						
						for(Object obj:  groovy.evaluateCollection(groovyExp)) {
							Element elem = new Element(Tag.valueOf("div"),"");
							elem.html(elementText.replaceAll("\\$\\{"+groovyVar+"\\}",obj+""));
							body.appendChild(elem);
						}
						
					}
				}
			}
			else {

				for(Attribute attr: element.attributes()) {
					renderAttribute(attr, groovy);
				}

				if(element.hasAttr("data-if")) {
					if(!groovy.validateDataIf(element.attr("data-if"))) {
						element.text("");
					}
					else {
						element.removeAttr("data-if");
					}
				}
				renderElementBody(element,groovy);
			}
		}

		out.println(document.html());
		out.close();

	}

	private void renderElementBody(Element element, Groovy groovy) {

		Pattern pattern = Pattern.compile("\\$\\{\\w*\\.\\w*\\}");
		Matcher m = pattern.matcher(element.text());
		
		if(m.find()) {
			String grovvyVar = m.group(0).replaceAll("[${}]", "");
			element.html(element.ownText().replaceAll("\\$\\{"+grovvyVar+"\\}",groovy.getExpValue(grovvyVar)));
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