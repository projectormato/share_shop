package projectormato.ss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import projectormato.ss.entity.Question;
import projectormato.ss.repository.ChoiceRepository;
import projectormato.ss.repository.ProblemRepository;
import projectormato.ss.repository.QuestionRepository;
import projectormato.ss.entity.Problem;

import java.util.List;

@Service
public class ProblemService {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ChoiceRepository choiceRepository;

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
