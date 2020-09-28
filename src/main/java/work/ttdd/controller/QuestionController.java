package work.ttdd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import work.ttdd.entity.Problem;
import work.ttdd.entity.Question;
import work.ttdd.form.QuestionPostForm;
import work.ttdd.repository.ProblemRepository;
import work.ttdd.repository.QuestionRepository;

@Controller
public class QuestionController {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    QuestionRepository questionRepository;

    @PostMapping(path = "/problem/{id}/question")
    public String createQuestion(@PathVariable Long id, QuestionPostForm form) {
        Problem problem = problemRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
        questionRepository.save(
                Question.builder()
                        .statement(form.getStatement())
                        .problem(problem)
                        .build()
        );
        return "redirect:/problem/" + id;
    }
}
