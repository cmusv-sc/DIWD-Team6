package neo4j.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import neo4j.domain.Author;
import neo4j.domain.Paper;
import neo4j.repositories.AuthorRepository;
import neo4j.utils.Remap;

@Service
@Transactional
public class AuthorService {
	@Autowired AuthorRepository authorRepository;
	
//	public Map<String, Object> graphAlc() {
//        Iterator<Author> result = authorRepository.getAllAuthor().iterator();
//        return toAlcFormatSingle(result);
//    }
	
	public Map<String, Object> getCoAuthor(String name) {
		Iterator<Author> result = authorRepository.findCoAuthorByName(name).iterator();

        return toAlcFormatSingle(result);

	}
	
	public Map<String, Object> getExpertByKeyword(int limit, String keyword) {
		Iterator<Author> result = authorRepository.graphExpertsByKeyword(limit, keyword).iterator();
        return toAlcFormatSingle(result);

	}
	
	public Map<String, Object> getCollaboratorsByKeyword(String keyword) {
		Iterator<Author> result = authorRepository.graphCollaboratorsByKeyword(keyword).iterator();
        return toAlcFormatSingle(result);

	}
	
	public Map<String, Object> getTimelineOfAuthors(int startYear, int endYear, String[] author) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String each : author) {
			List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
			for (int start = startYear; start <= endYear; start++) {
				Iterator<Paper> paper = authorRepository.getPaperByAuthorAndByYear(each, start + "").iterator();
				while(paper.hasNext()) {
		        	Paper row = paper.next();
		        	Map<String, Object> tempPaper = Remap.map("title", 
		            		row.getTitle(),"year", start + "", "cluster", "2", "value", 1, "group", "paper");
		        	nodes.add(tempPaper);
		        }
			}
			result.put(each, nodes);
	    }
		return Remap.map("author", result);
	} 
	
	public Map<String, Object> getCoCoAuthor(String name) {
		List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String, Object>>();
        int i = 0;
        int target = 0;
		Iterator<Author> result = authorRepository.findCoAuthorByName(name).iterator();
		while(result.hasNext()) {
			Author row = result.next();
			System.out.println(row.getName());
			nodes.add(Remap.map("id", i, "title",row.getName(),"label", "author", "cluster", "1", "value", 2, "group", "coAuthor"));
			target = i++;
			Iterator<Author> tempResult = authorRepository.findCoAuthorByName(row.getName()).iterator();
			while (tempResult.hasNext()) {
				Author au = tempResult.next();
				if (!au.getName().equals(name)) {
					Map<String, Object> author = Remap.map("title", 
							au.getName(),"label", "author", "cluster", "2", "value", 1, "group", "coCoAuthor");
	                int source = 0;
//	                for (int j = 0; j < nodes.size(); j++) {
//	                	if (nodes.get(j).get("title").equals(name)) {
//	                		source = (int) nodes.get(j).get("id");
//	                		break;
//	                	} 
//	                }
	                if (source == 0) {
	                	author.put("id", i);
	                    source = i;
	                    i++;
	                    nodes.add(author);
	                }

	                rels.add(Remap.map("from", source, "to", target, "title", "CO_AUTHOR"));
				}
			}
		}
        return Remap.map("nodes", nodes, "edges", rels);
	}
	
	public Map<String, Object> graphPerson2Person(int limit) {
		List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String, Object>>();
        int i = 0;
        int target = 0;
        Iterator<Author> auth = authorRepository.getAllAuthor(limit).iterator();
        List<String> authorList = new ArrayList<String>();
        while (auth.hasNext()) {
        	Author temp = auth.next();
        	authorList.add(temp.getName());
        }
		Iterator<Author> result = authorRepository.getAllAuthor(limit).iterator();
		while (result.hasNext()) {
			Author row = result.next();
			System.out.println(row.getName());
			nodes.add(Remap.map("id", i, "title",row.getName(),"label", "author", "cluster", "1", "value", 2, "group", "coAuthor"));
			target = i++;
			Iterator<Author> tempResult = authorRepository.findCoAuthorByName(row.getName()).iterator();
			while (tempResult.hasNext()) {
				Author au = tempResult.next();
				if (!authorList.contains(au.getName())) {
					Map<String, Object> author = Remap.map("title", 
							au.getName(),"label", "author", "cluster", "2", "value", 1, "group", "coCoAuthor");
	                int source = 0;
//	                for (int j = 0; j < nodes.size(); j++) {
//	                	if (nodes.get(j).get("title").equals(name)) {
//	                		source = (int) nodes.get(j).get("id");
//	                		break;
//	                	} 
//	                }
	                if (source == 0) {
	                	author.put("id", i);
	                    source = i;
	                    i++;
	                    nodes.add(author);
	                }

	                rels.add(Remap.map("from", source, "to", target, "title", "CO_AUTHOR"));
				}
			}
		}
        return Remap.map("nodes", nodes, "edges", rels);
	}
    private Map<String, Object> toAlcFormatSingle(Iterator<Author> result) {
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
