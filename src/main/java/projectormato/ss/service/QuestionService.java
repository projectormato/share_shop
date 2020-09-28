package projectormato.ss.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import projectormato.ss.entity.Problem;
import projectormato.ss.entity.Question;
import projectormato.ss.repository.QuestionRepository;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;

    public List<Question> findByProblem(Problem problem) {
        return questionRepository.findQuestionsByProblem(problem);
    }

    public Question findById(Long id) {
        return questionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question Not Found"));
    }
}
