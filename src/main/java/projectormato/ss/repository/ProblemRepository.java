package projectormato.ss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectormato.ss.entity.Problem;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
