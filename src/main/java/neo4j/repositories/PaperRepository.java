package neo4j.repositories;

import neo4j.domain.Author;
import neo4j.domain.Paper;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public interface PaperRepository extends GraphRepository<Paper> {
    Paper findByTitle(@Param("title") String title);

    @Query("MATCH (p:Paper) WHERE p.title =~ ('(?i).*'+{title}+'.*') RETURN p")
    Collection<Paper> findByTitleContaining(@Param("title") String title);
    
    @Query("MATCH (p:Paper) WHERE p.title = {title} RETURN p")
    Collection<Paper> getPaperByName(@Param("title") String title);
    
    @Query("MATCH (p:Paper) RETURN p")
    Collection<Paper> getAllPaper();
    
    @Query("MATCH (p:Paper) WHERE p.cite IS NOT NULL RETURN p LIMIT {limit}")
    Collection<Paper> getPaper(@Param("limit") int limit);

    @Query("MATCH (p:Paper)<-[:PUBLISH]-(a:Author) RETURN p.title as paper, collect(a.name) as cast LIMIT {limit}")
    List<Map<String, Object>> graph(@Param("limit") int limit);
    
    @Query("MATCH (p:Paper)<-[:PUBLISH]-(a:Author) WHERE p.title =~ ('(?i).*'+{keyword}+'.*') RETURN p.title as paper, collect(a.name) as cast LIMIT {limit}")
    List<Map<String, Object>> graphTopKByKeyword(@Param("limit") int limit, @Param("keyword") String keyword);
    
    @Query("MATCH (p:Paper) WHERE p.year >= {startYear} and p.year <= {endYear}  RETURN p")
    Collection<Paper> categorizeByTime(@Param("startYear") String startYear, @Param("endYear") String endYear);
    
    @Query("MATCH (p:Paper) WHERE p.year = {year} and p.booktitle = {name}  RETURN p")
    Collection<Paper> getJournalEvolution(@Param("year") String year, @Param("name") String name);
    
    @Query("MATCH (p:Paper) WHERE p.year = {year} and p.channel = {name}  RETURN p")
    Collection<Paper> getPaperByChannel(@Param("year") String year, @Param("name") String name);
    
    @Query("MATCH (p:Paper) WHERE p.year >= {startYear} and p.year <= {endYear} and p.channel = {channel} RETURN p")
    Collection<Paper> categorizeByTimeAndOther(@Param("startYear") String startYear, @Param("endYear") String endYear, @Param("channel") String channel);
    
    
}


