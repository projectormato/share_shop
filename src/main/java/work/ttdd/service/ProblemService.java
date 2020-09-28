package work.ttdd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import work.ttdd.entity.Problem;
import work.ttdd.entity.Question;
import work.ttdd.repository.ChoiceRepository;
import work.ttdd.repository.ProblemRepository;
import work.ttdd.repository.QuestionRepository;

import java.util.List;

@Service
public class ProblemService {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ChoiceRepository choiceRepository;

    public List<Problem> findAll() {
        return problemRepository.findAll();
    }

    public Problem save(Problem problem) {
        return problemRepository.save(problem);
    }

    public Problem findById(Long id) {
        return problemRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
    }

    public void delete(Problem problem) {
        final var questionList = questionRepository.findQuestionsByProblem(problem);
        for (Question question : questionList) {
            choiceRepository.deleteAll(choiceRepository.findChoicesByQuestion(question));
        }
        questionRepository.deleteAll(questionList);
        problemRepository.delete(problem);
    }
}
