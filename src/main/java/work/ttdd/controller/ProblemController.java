package work.ttdd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import work.ttdd.entity.Choice;
import work.ttdd.entity.Problem;
import work.ttdd.entity.Question;
import work.ttdd.form.AnswerForm;
import work.ttdd.form.ChoicePostForm;
import work.ttdd.form.ProblemPostForm;
import work.ttdd.form.QuestionPostForm;
import work.ttdd.service.ChoiceService;
import work.ttdd.service.ProblemService;
import work.ttdd.service.QuestionService;

import java.util.List;
import java.util.Map;

@Controller
public class ProblemController {
    @Autowired
    ProblemService problemService;

    @Autowired
    QuestionService questionService;

    @Autowired
    ChoiceService choiceService;

    @GetMapping(path = "/")
    public String problemList(Model model) {
        model.addAttribute("problemList", problemService.findAll());
        model.addAttribute("problemPostForm", new ProblemPostForm());
        return "index";
    }

    @PostMapping(path = "/problem")
    public String createProblem(ProblemPostForm form) {
        problemService.save(
                Problem.builder()
                        .title(form.getTitle())
                        .description(form.getDescription())
                        .build()
        );
        return "redirect:/";
    }

    @GetMapping("/problem/{id}")
    public String showProblem(@PathVariable Long id, Model model) {
        final var problem = problemService.findById(id);
        final var questionList = questionService.findByProblem(problem);
        final Map<Long, List<Choice>> choiceMap = choiceService.getChoiceMap(questionList);

        model.addAttribute("problem", problem);
        model.addAttribute("questionList", questionList);
        model.addAttribute("choiceMap", choiceMap);
        model.addAttribute("questionPostForm", new QuestionPostForm());
        model.addAttribute("choicePostFrom", new ChoicePostForm());
        return "problem";
    }

    @DeleteMapping(path = "/problem/{id}")
    public String deleteProblem(@PathVariable Long id) {
        problemService.delete(problemService.findById(id));
        return "redirect:/";
    }

    @GetMapping("/problem/{id}/edit")
    public String editProblem(@PathVariable Long id, Model model) {
        final var problem = problemService.findById(id);
        model.addAttribute("problem", problem);
        model.addAttribute("problemPostForm", new ProblemPostForm(problem.getTitle(), problem.getDescription()));
        return "edit";
    }

    @PutMapping(path = "/problem/{id}")
    public String updateProblem(@PathVariable Long id, ProblemPostForm form) {
        final var problem = problemService.findById(id);
        problemService.save(
                Problem.builder()
                        .id(problem.getId())
                        .title(form.getTitle())
                        .description(form.getDescription())
                        .build()
        );
        return "redirect:/problem/" + problem.getId();
    }

    @GetMapping("/problem/{id}/answer")
    public String answerProblem(@PathVariable Long id, Model model, AnswerForm form) {
        final var problem = problemService.findById(id);
        final var nowQuestion = findNowQuestion(form);
        final var nextQuestion = findNextQuestion(problem, nowQuestion);
        final var choiceList = choiceService.findByQuestion(nextQuestion);
        final var answerForm = generateAnswerForm(form, nowQuestion, nextQuestion);

        model.addAttribute("problem", problem);
        model.addAttribute("answerForm", answerForm);
        model.addAttribute("choiceList", choiceList);
        model.addAttribute("finishFlag", nextQuestion == null);
        return "answer";
    }

    private AnswerForm generateAnswerForm(AnswerForm form, Question nowQuestion, Question nextQuestion) {
        if (nextQuestion == null) {
            return new AnswerForm(null, null, calcNumberOfCorrectAnswers(nowQuestion, form));
        } else {
            return new AnswerForm(nextQuestion.getId(), nextQuestion.getStatement(), calcNumberOfCorrectAnswers(nowQuestion, form));
        }
    }

    private int calcNumberOfCorrectAnswers(Question question, AnswerForm form) {
        if (question == null) {
            return 0;
        }
        final var choiceList = choiceService.findByQuestion(question);
        final var correctChoice = choiceList.stream().filter(Choice::getCorrectFlag).findFirst();
        if (correctChoice.isPresent() && correctChoice.get().getId() == form.getAnsweredChoiceId()) {
            return form.getNumberOfCorrectAnswers() + 1;
        }
        return form.getNumberOfCorrectAnswers();
    }

    private Question findNowQuestion(AnswerForm form) {
        if (form.getQuestionId() != null) {
            return questionService.findById(form.getQuestionId());
        }
        return null;
    }

    private Question findNextQuestion(Problem problem, Question question) {
        final var questionList = questionService.findByProblem(problem);
        if (question != null) {
            int index = questionList.indexOf(question);
            if (index + 1 == questionList.size()) {
                return null;
            }
            return questionList.get(index + 1);
        }
        return questionList.get(0);
    }
}
