package io.reflectoring.coderadar.graph.analyzer.repository;

import io.reflectoring.coderadar.graph.analyzer.domain.CommitEntity;
import java.util.List;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaveCommitRepository extends Neo4jRepository<CommitEntity, Long> {


}
