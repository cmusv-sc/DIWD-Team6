package xml.KG_DBLP;

import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.neo4j.ogm.session.SessionFactory;

public class StaxParser {

	public static void main(String[] args) throws Exception {
	    XMLInputFactory factory = XMLInputFactory.newInstance();
	    String filename = "/Users/cici/Documents/CMU/SM-3rdS/Workflow/dblp.xml";
	    XMLStreamReader reader = 
	        factory.createXMLStreamReader(filename, new FileReader(filename));
	    int level = 0;
	    Map<String, String> properties = new TreeMap<String, String>();
	    String propertyName = null;
	    String propertyValue = "";
	    Set<String> authors = new TreeSet<String>();
	    GraphBuilder graphBuilder = new GraphBuilder();
	    int n = 0;
		while (reader.hasNext() && n < 119700) {
			int event = reader.next();
			++n;
			if ((n % 10000) == 0) {
				System.out.println("progress " + n);
			}
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				++level;
				switch (level) {
				case 2:
					properties.clear();
					authors.clear();
					properties.put("type", reader.getLocalName());
					for (int i = 0; i < reader.getAttributeCount(); ++i) {
						properties.put(reader.getAttributeLocalName(i), 
								reader.getAttributeValue(i));
					}
					break;
				case 3:
					propertyName = reader.getLocalName();
					propertyValue = "";
					break;
				default:
					if (level > 3 && propertyName != null) {
						propertyValue = propertyValue.concat("<" + reader.getLocalName() + ">");
					}
					break;
				}
				break;
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
				if (propertyName != null) {
					propertyValue = propertyValue.concat(reader.getText());
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				switch (level) {
				case 2:
					graphBuilder.addPublication(properties, authors);
					properties.clear();
					authors.clear();
					break;
				case 3:
					if (propertyName.equals("author")) {
						authors.add(propertyValue);
					} else {
						properties.put(propertyName, propertyValue);
					}
					propertyName = null;
					break;
				default:
					if (level > 3 && propertyName != null) {
						propertyValue = propertyValue.concat("</" + reader.getLocalName() + ">");
					}
					break;
				}
				--level;
				break;
			default:
				break;
			}
		}
		
		SessionFactory sessionFactory = new SessionFactory("neo4j.domain");
		// Change to your own neo4j password
		graphBuilder.populateGraphDB(sessionFactory.openSession("http://localhost:7474/", "neo4j", "neo4j"));
	}
}
