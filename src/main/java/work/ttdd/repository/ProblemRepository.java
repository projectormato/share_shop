package work.ttdd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.ttdd.entity.Problem;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
