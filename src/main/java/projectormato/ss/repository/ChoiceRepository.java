package projectormato.ss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectormato.ss.entity.Choice;
import projectormato.ss.entity.Question;

import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findChoicesByQuestion(Question question);
}
