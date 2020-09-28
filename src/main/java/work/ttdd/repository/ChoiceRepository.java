package work.ttdd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.ttdd.entity.Choice;
import work.ttdd.entity.Question;

import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findChoicesByQuestion(Question question);
}
