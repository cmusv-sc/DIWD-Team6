package neo4j.services;

import neo4j.domain.Author;
import neo4j.domain.Paper;
import neo4j.repositories.AuthorRepository;
import neo4j.repositories.PaperRepository;
import neo4j.utils.Remap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;

@Service
@Transactional
public class PaperService {

    @Autowired PaperRepository paperRepository;
    private Map<String, Object> toD3Format(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String,Object>>();
        int i = 0;
        int target = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(Remap.map("name",row.get("paper"),"label","paper"));
            i++;
            target = i;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = Remap.map("name", name,"label","author");
                int source = nodes.indexOf(author);
                if (source == -1) {
                    nodes.add(author);
                    source = i++;
                }
                rels.add(Remap.map("source",source,"target",target));
            }
        }
        return Remap.map("nodes", nodes, "links", rels);
    }
    
    private Map<String, Object> toAlcFormat(Iterator<Map<String, Object>> result) {
        List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String, Object>>();
        int i = 1;
        int target = 0;
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            nodes.add(Remap.map("id", i, "title",row.get("paper"),"label", "paper", "cluster", "1", "value", 2, "group", "paper"));
            target = i++;
            for (Object name : (Collection) row.get("cast")) {
                Map<String, Object> author = Remap.map("title", 
                		name,"label", "author", "cluster", "2", "value", 1, "group", "author");
                int source = 0;
                for (int j = 0; j < nodes.size(); j++) {
                	if (nodes.get(j).get("title").equals(name)) {
                		source = (int) nodes.get(j).get("id");
                		break;
                	} 
                }
                if (source == 0) {
                	author.put("id", i);
                    source = i;
                    i++;
                    nodes.add(author);
                }

                rels.add(Remap.map("from", source, "to", target, "title", "PUBLISH"));
            }
        }
        return Remap.map("nodes", nodes, "edges", rels);
    }

    

    public Map<String, Object> graph(int limit) {
        Iterator<Map<String, Object>> result = paperRepository.graph(limit).iterator();
        return toD3Format(result);
    }
    
    public Map<String, Object> graphAlc(int limit) {
        Iterator<Map<String, Object>> result = paperRepository.graph(limit).iterator();
        return toAlcFormat(result);
    }
    
    public Map<String, Object> graphPaper2Paper(int limit) {
    	List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> rels = new ArrayList<Map<String, Object>>();
        int i = 0;
        int target = 0;
        Iterator<Paper> result = paperRepository.getPaper(limit).iterator();
        while (result.hasNext()) {
        	Paper row = result.next();
        	String [] cite = row.getCite().split("/");
        	nodes.add(Remap.map("id", i, "title",row.getTitle(),"label", "paper", "cite", cite[2], "value", 2, "group", "Basedpaper"));
        	target = i++;
        	Map<String, Object> paper = Remap.map("title", 
					cite[2],"label", "paper", "cluster", "2", "value", 1, "group", "CoPaper");
        	int source = 0;
            if (source == 0) {
            	paper.put("id", i);
                source = i;
                i++;
                nodes.add(paper);
            }
            rels.add(Remap.map("from", source, "to", target, "title", "CoPaper"));
//        	Iterator<Paper> tempResult = paperRepository.findByTitleContaining(cite[2]).iterator();
//        	while (tempResult.hasNext()) {
//				Paper pa = tempResult.next();
//				if (!pa.getTitle().equals(row.getTitle())) {
//					Map<String, Object> paper = Remap.map("title", 
//							pa.getTitle(),"label", "paper", "cluster", "2", "value", 1, "group", "CoPaper");
//	                int source = 0;
//	                if (source == 0) {
//	                	paper.put("id", i);
//	                    source = i;
//	                    i++;
//	                    nodes.add(paper);
//	                }
//
//	                rels.add(Remap.map("from", source, "to", target, "title", "CoPaper"));
//				}
//			}
        }
        return Remap.map("nodes", nodes, "edges", rels);
    }
    
    public Map<String, Object> graphAlcByKeyword(int limit, String name) {
        Iterator<Map<String, Object>> result = paperRepository.graphTopKByKeyword(limit, name).iterator();
        return toAlcFormat(result);
    }
    
    public Map<String, Object> getTopKCitedPaper(int year, String name) {
        Iterator<Paper> result = paperRepository.getPaperByChannel(year + "", name).iterator();
        List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        int max = 1;
        while (result.hasNext() && max < 11) {
        	Paper row = result.next();
        	if (row.getCite() != null) {
        		nodes.add(Remap.map("Name", row.getCite(), "rank", max + ""));
            	max++;
        	}
        }
        return Remap.map("Result", nodes);
    }
    
    public Map<String, Object> getPaperInfo(String name) {
        Iterator<Paper> result = paperRepository.getPaperByName(name + ".").iterator();
        List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        while(result.hasNext()) {
        	Paper row = result.next();
        	Map<String, Object> paper = Remap.map("title", 
            		row.getTitle(),"year", row.getYear(), "category", row.getCategory(),
            		"booktitle", row.getBookTitle(), "channel", row.getChannel(), "cite", row.getCite());
        	nodes.add(paper);
        }
        return Remap.map("Information", nodes); 
    }
    
    public Map<String, Object> categorizeByTime(int startYear, int endYear) {
        Iterator<Paper> result = paperRepository.categorizeByTime(startYear + "", endYear + "").iterator();
        return toCategorizeFormat(result);
    }
    
    public Map<String, Object> categorizeByTimeAndOther(int startYear, int endYear, String channel, String[] keywordList) {
        Iterator<Paper> result = paperRepository.categorizeByTimeAndOther(startYear + "", endYear + "", channel).iterator();
        return toCategorizeFormatByKeywords(result, keywordList);
    }
    
    public Map<String, Object> getJournalEvolution(int startYear, int endYear, String name) {
        //Iterator<Paper> result = paperRepository.getJournalEvolution(startYear + "", endYear + "").iterator();
    	List<Map<String,Object>> result = new ArrayList<Map<String, Object>>();
		while (startYear <= endYear) {
			Iterator<Paper> tempResult = paperRepository.getJournalEvolution(startYear + "", name).iterator();
			int[] record = new int[5];
			while (tempResult.hasNext()) {
				Paper row = tempResult.next();
				switch (row.getCategory()) {
	    		case "Database" :
	    			record[0]++;
	    			break;
	    		case "Web" :
	    			record[1]++;
	    			break;
	    		case "Software" :
	    			record[2]++;
	    			break;
	    		case "Operating System" :
	    			record[3]++;
	    			break;
	    		case "Other" :
	    			record[4]++;
	    			break;
	    		}
			}
			int maxIndex = 0;
			int max = record[0];
			for (int i = 1; i < 5; i++) {
				if (record[i] > max) {
					max = record[i];
					maxIndex = i;
				}
			}
			String topic = "";
			switch (maxIndex) {
    		case 0 :
    			topic = "Database";
    			break;
    		case 1 :
    			topic = "Web";
    			break;
    		case 2 :
    			topic = "Software";
    			break;
    		case 3 :
    			topic = "Operating System";
    			break;
    		case 4 :
    			record[4]++;
    			topic = "Other";
    			break;
    		}
			result.add(Remap.map("Year", startYear + "", "topic", topic));
			startYear++;
		}
		return Remap.map("Evolution", result);
    }
    
    private Map<String, Object> toCategorizeFormat(Iterator<Paper> result) {
		// Database, Web, Software, Operating System, Other
    	List<Map<String,Object>> OSNodes = new ArrayList<Map<String, Object>>();
    	List<Map<String,Object>> DBNodes = new ArrayList<Map<String, Object>>();
    	List<Map<String,Object>> WebNodes = new ArrayList<Map<String, Object>>();
    	List<Map<String,Object>> SoftwareNodes = new ArrayList<Map<String, Object>>();
    	List<Map<String,Object>> OtherNodes = new ArrayList<Map<String, Object>>();
    	while (result.hasNext()) {
    		Paper row = result.next();
    		switch (row.getCategory()) {
    		case "Database" :
    			Map<String, Object> nodeDB = Remap.map("title", 
                		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
    			DBNodes.add(nodeDB);
    			break;
    		case "Web" :
    			Map<String, Object> nodeWeb = Remap.map("title", 
                		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
    			WebNodes.add(nodeWeb);
    			break;
    		case "Software" :
    			Map<String, Object> nodeSoftware = Remap.map("title", 
                		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
    			SoftwareNodes.add(nodeSoftware);
    			break;
    		case "Operating System" :
    			Map<String, Object> nodeOS = Remap.map("title", 
                		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
    			OSNodes.add(nodeOS);
    			break;
    		case "Other" :
    			Map<String, Object> nodeOther = Remap.map("title", 
                		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
    			OtherNodes.add(nodeOther);
    			break;
    		}
    	}
    	return Remap.map("Databases", DBNodes, "Web", WebNodes, "Software", SoftwareNodes, "Operating System", OSNodes, "Other", OtherNodes);
    }
    	
    	private Map<String, Object> toCategorizeFormatByKeywords(Iterator<Paper> result, String[] keywordList) {
    		// Database, Web, Software, Operating System, Other
        	List<Map<String,Object>> OSNodes = new ArrayList<Map<String, Object>>();
        	List<Map<String,Object>> DBNodes = new ArrayList<Map<String, Object>>();
        	List<Map<String,Object>> WebNodes = new ArrayList<Map<String, Object>>();
        	List<Map<String,Object>> SoftwareNodes = new ArrayList<Map<String, Object>>();
        	List<Map<String,Object>> OtherNodes = new ArrayList<Map<String, Object>>();
        	while (result.hasNext()) {
        		Paper row = result.next();
        		if (containsKeyword(keywordList, row.getTitle())) {
        			switch (row.getCategory()) {
            		case "Database" :
            			Map<String, Object> nodeDB = Remap.map("title", 
                        		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
            			DBNodes.add(nodeDB);
            			break;
            		case "Web" :
            			Map<String, Object> nodeWeb = Remap.map("title", 
                        		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
            			WebNodes.add(nodeWeb);
            			break;
            		case "Software" :
            			Map<String, Object> nodeSoftware = Remap.map("title", 
                        		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
            			SoftwareNodes.add(nodeSoftware);
            			break;
            		case "Operating System" :
            			Map<String, Object> nodeOS = Remap.map("title", 
                        		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
            			OSNodes.add(nodeOS);
            			break;
            		case "Other" :
            			Map<String, Object> nodeOther = Remap.map("title", 
                        		row.getTitle(),"category", row.getCategory(), "cluster", "2", "value", 1, "group", "paper");
            			OtherNodes.add(nodeOther);
            			break;
            		}
        		}
        	}
    	return Remap.map("Databases", DBNodes, "Web", WebNodes, "Software", SoftwareNodes, "Operating System", OSNodes, "Other", OtherNodes);
    }
    	private boolean containsKeyword(String[] keywordList, String name) {
    		for (String each : keywordList) {
    			if (name.contains(each)) {
    				return true;
    			}
    		}
    		return false;
    	}
    
   
    

}

