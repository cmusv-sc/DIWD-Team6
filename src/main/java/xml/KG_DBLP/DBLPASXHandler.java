package xml.KG_DBLP;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.codehaus.stax2.XMLStreamReader2;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class DBLPASXHandler extends DefaultHandler {

	@Override
	public void error(SAXParseException e) throws SAXException {

	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {

	}

	private int level = 0;
	private Map<Integer, Set<String>> elementNames = new HashMap<Integer, Set<String>>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		++level;
		if (!elementNames.containsKey(level)) {
			elementNames.put(level, new TreeSet<String>());
		}
		elementNames.get(level).add(qName);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		--level;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
	}
	
	@Override
	public void endDocument() throws SAXException {
		for (Map.Entry<Integer, Set<String>> entry : elementNames.entrySet()) {
			System.out.printf("%d: {", entry.getKey());
			for (String s : entry.getValue()) {
				System.out.printf("%s, ", s);
			}
			System.out.printf("}%n");
		}
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(new File("/Users/cici/Documents/CMU/SM-3rdS/Workflow/dblp.xml"), 
				new DBLPASXHandler());
	}
}
