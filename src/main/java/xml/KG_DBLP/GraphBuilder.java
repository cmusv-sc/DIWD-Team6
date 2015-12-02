package xml.KG_DBLP;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.ogm.session.Session;

import neo4j.domain.Author;
import neo4j.domain.Paper;

public class GraphBuilder {
    private Map<String, Author> allAuthors = new HashMap<String, Author>();
    private Map<String, Paper> papers = new HashMap<String, Paper>();
    
    GraphBuilder() {}
    
    void addPublication(Map<String, String> properties,
    		Set<String> authors) {
    	Paper paper = new Paper();
    	for (String propertyName : properties.keySet()) {
    		String propertyValue = properties.get(propertyName);
    		switch (propertyName) {
    		case "type":
    			paper.type = propertyValue;
    			break;
    		case "key":
    			paper.key = propertyValue;
    			break;
    		case "title":
    			paper.title = propertyValue;
    			break;
    		case "year":
    			try {
    				paper.year = Integer.parseInt(propertyValue);
    			} catch (NumberFormatException e) {
    				paper.year = -1;
    			}
    			break;
    		case "month":
    			paper.month = propertyValue;
    			break;
    		case "school":
    			paper.school = propertyValue;
    			break;
    		default:
    			break;
    		}
    	}
    	for (String authorName : authors) {
    		if (!allAuthors.containsKey(authorName)) {
    			Author author = new Author();
    			author.name = authorName;
    			allAuthors.put(authorName, author);
    			// System.out.println("new author: " + author);
    		}
    	}
    	if (papers.containsKey(paper.key)) {
    		System.err.printf("Duplicate key %s for both '%s' and '%s'%n", 
    				paper.key, paper.title, 
    				papers.get(paper.key).title);
    	} else {
    		papers.put(paper.key, paper);
    		for (String authorName : authors) {
    			Author author = allAuthors.get(authorName);
    			paper.authors.add(author);
    		}
    		// System.out.println("new paper: " + paper);
    	}
    }
    
    void populateGraphDB(Session session) {
    	System.out.println("#authors = " + allAuthors.size());
    	int i = 0;
    	for (String authorName : allAuthors.keySet()) {
    		Author author = allAuthors.get(authorName);
    		session.save(author);
    		++i;
    		if ((i % 1000) == 0) {
    			System.out.println("#authors created = " + i);
    		}
    	}
    	System.out.println("#papers = " + papers.size());
    	i = 0;
    	for (String key : papers.keySet()) {
    		Paper paper = papers.get(key);
    		session.save(paper);
    		++i;
    		if ((i % 1000) == 0) {
    			System.out.println("#papers created = " + i);
    		}
    	}
    }
}
