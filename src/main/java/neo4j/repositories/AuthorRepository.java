package neo4j.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import neo4j.domain.Author;
import neo4j.domain.Paper;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends GraphRepository<Author> {
	@Query("match (a:Author) RETURN a")
    Collection<Author> getAllAuthor();
	
	@Query("MATCH (a:Author)-[B:PUBLISH]->(p:Paper)<-[:PUBLISH]-(co_author) WHERE a.name = {name} return co_author")
    Collection<Author> findCoAuthorByName(@Param("name") String name);
	
	@Query("MATCH (p:Paper)<-[:PUBLISH]-(a:Author) WHERE p.title =~ ('(?i).*'+{keyword}+'.*') RETURN a LIMIT {limit}")
	Collection<Author> graphByKeyword(@Param("limit") int limit, @Param("keyword") String keyword);
}
