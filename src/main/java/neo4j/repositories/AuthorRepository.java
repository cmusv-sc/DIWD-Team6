package neo4j.repositories;

import java.util.Collection;

import neo4j.domain.Author;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends GraphRepository<Author> {
	@Query("match (a:Author) RETURN a")
    Collection<Author> getAllAuthor();
}
