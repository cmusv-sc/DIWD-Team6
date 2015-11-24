package startingPoint.KG_DBLP;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import neo4j.domain.*;
import neo4j.repositories.*;
import neo4j.services.AuthorService;
import neo4j.services.DatasetService;
import neo4j.services.PaperService;

@Configuration
@Import(App.class)
@RestController("/")
public class KnowledgeGraph extends WebMvcConfigurerAdapter {
	//private String test = "'nodes':[{"cluster":"1","id":1,"label":"paper","title":"Integration of Fuzzy ERD Modeling to the Management of Global Contextual Data.","value":2,"group":"paper"}"; 
    public static void main(String[] args) throws IOException {
        SpringApplication.run(KnowledgeGraph.class, args);
    }
    
    @Autowired
    PaperService paperService;
    @Autowired
    DatasetService datasetService;
    @Autowired
    AuthorService authorService;

    @Autowired PaperRepository paperRepository;
    @Autowired DatasetRepository datasetRepository;

    @RequestMapping("/graph")
    public Map<String, Object> graph(@RequestParam(value = "limit",required = false) Integer limit) {
    	return paperService.graph(limit == null ? 100 : limit);
    }
    
    @RequestMapping("/graphTest")
    public String graphTest(@RequestParam(value = "limit",required = false) Integer limit) {
    	Map<String, Object> map = paperService.graphAlc(limit == null ? 200 : limit);
    	String json = "";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	System.out.println("================================================");
    	System.out.println(json);
    	return json;
    }
    
    @RequestMapping("/graphUserDataset")
    public String graphUserDataset(@RequestParam(value = "limit",required = false) Integer limit) {
    	Map<String, Object> map = authorService.graphAlc();
    	String json = "";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		//convert map to JSON string
    		json = mapper.writeValueAsString(map);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return json;
    }
    
    @RequestMapping("/getPapers")
    public Collection<Paper> getPapers(String title) {
    	return paperRepository.findByTitleContaining(title);
    	//return paperRepository.findByTitleLike(title);
    }
    
    @RequestMapping("/getPaper")
    public Paper getPaper(String title) {
    	//return movieRepository.findByTitleContaining(title);
    	return paperRepository.findByTitle(title);
    }

}