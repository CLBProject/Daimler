package templates;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * This Class is responsable to load an HTML file and render it with Groovy
 * @author nobre
 *
 */
public class GroovyHtmlParser {
	
	private Document document;
	
	private static final String EXPRESSION_PATTERN = "[${]\\w*\\.\\w*\\}";
	private static final String CHARS_TO_REMOVE_FROM_EXPRESSION = "[${}]";
	
	public GroovyHtmlParser(InputStream inDoc, String fileName) throws IOException {
		document = Jsoup.parse(inDoc,"UTF-8",fileName);
	}

	/**
	 * Responsable for creating a final dom tree already rendered with groovy
	 * @param requestForGroovy
	 */
	public void renderGroovyHtml(Map<String,Object> variables) {
		
		Element resultGroovy = document.select("script").first();

		Groovy groovy = null;

		if(resultGroovy.attr("type").equals("server/groovy")) {
			groovy = new Groovy(resultGroovy.data(),variables);
		}
		//Remove Groovy Element from final result
		resultGroovy.remove();
		
		Element body = document.select("body").first();

		for (Element element : document.select("*")) {
			if(element.nodeName().equals("div")) {
				for(Attribute attr : element.attributes()) {
					if (attr.getKey().startsWith("data-loop-")) {
						String groovyVar = attr.getKey().split("data-loop-")[1];
						String groovyExp = attr.getValue();

						String elementText = element.ownText();
						element.remove();

						String[] expSplited = groovyExp.split("\\.");
						
						for(Object obj:  groovy.getExpCollection(expSplited[0],expSplited[1])) {
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
					
					String[] expSplited = element.attr("data-if").split("\\.");
					
					if(!groovy.isExpValue(expSplited[0],expSplited[1])) {
						element.text("");
					}
					else {
						element.removeAttr("data-if");
					}
				}
				renderElementBody(element,groovy);
			}
		}
		
	}
	
	/**
	 * Looks for an element in the DOM that contains an $-Expression and replaces it by groovy value
	 * @param element
	 * @param groovy
	 */
	private void renderElementBody(Element element, Groovy groovy) {

		Pattern pattern = Pattern.compile(EXPRESSION_PATTERN);
		Matcher m = pattern.matcher(element.ownText());

		if(m.find()) {

			String result = m.group(0);
			String groovyVar = result.replaceAll(CHARS_TO_REMOVE_FROM_EXPRESSION, "");
			
			String[] expSplited = groovyVar.split("\\.");

			element.html(element.ownText().replaceAll("\\$\\{"+groovyVar+"\\}",groovy.getExpValue(expSplited[0],expSplited[1])));
		}

	}

	/**
	 * Looks for an attribute in the DOM that contains an $-Expression and replaces it by groovy value
	 * @param element
	 * @param groovy
	 */
	private void renderAttribute(Attribute attr, Groovy groovy) {
		Pattern pattern = Pattern.compile(EXPRESSION_PATTERN);
		Matcher m = pattern.matcher(attr.getValue());

		if(m.find()) {

			String result = m.group(0);
			String groovyVar = result.replaceAll(CHARS_TO_REMOVE_FROM_EXPRESSION, "");
			
			String[] expSplited = groovyVar.split("\\.");

			attr.setValue(attr.getValue().replaceAll("\\$\\{"+groovyVar+"\\}",groovy.getExpValue(expSplited[0],expSplited[1])));
		}

	}

	/**
	 * Returns the current HTML String from dom
	 * @return
	 */
	public String getCurrentHtml() {
		return document.toString();
	}
}
