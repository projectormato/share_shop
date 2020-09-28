package work.ttdd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import work.ttdd.entity.Choice;
import work.ttdd.form.ChoicePostForm;
import work.ttdd.repository.ChoiceRepository;
import work.ttdd.repository.ProblemRepository;
import work.ttdd.repository.QuestionRepository;

@Controller
public class ChoiceController {

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ChoiceRepository choiceRepository;

    @PostMapping(path = "/problem/{problemId}/question/{questionId}/choice")
    public String createQuestion(@PathVariable Long problemId, @PathVariable Long questionId, ChoicePostForm form) {
        final var question = questionRepository.findById(questionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem Not Found"));
        final var choiceList = choiceRepository.findChoicesByQuestion(question);
        if (!form.getCorrectFlag() && choiceList.isEmpty()) {
            return "redirect:/problem/" + problemId;
        }
        if (form.getCorrectFlag() && !choiceList.isEmpty()) {
            return "redirect:/problem/" + problemId;
        }
        choiceRepository.save(
                Choice.builder()
                        .content(form.getContent())
                        .correctFlag(form.getCorrectFlag())
                        .question(question)
                        .build()
        );
        return "redirect:/problem/" + problemId;
    }
}
