package neo4j.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import neo4j.domain.Author;
import neo4j.repositories.AuthorRepository;
import neo4j.utils.Remap;

@Service
@Transactional
public class AuthorService {
	@Autowired AuthorRepository authorRepository;
	
	public Map<String, Object> graphAlc() {
        Iterator<Author> result = authorRepository.getAllAuthor().iterator();
        return toAlcFormat(result);
    }
    private Map<String, Object> toAlcFormat(Iterator<Author> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        while(result.hasNext()) {
        	Author row = result.next();
        	Map<String, Object> author = Remap.map("title", 
            		row.getName(),"label", "author", "cluster", "2", "value", 1, "group", "author");
        	nodes.add(author);
        }
        return Remap.map("nodes", nodes);
    }
}
