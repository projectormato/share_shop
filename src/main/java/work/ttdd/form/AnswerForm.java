package work.ttdd.form;

import lombok.Data;

@Data
public class AnswerForm {
    private Long questionId;
    private String questionStatement;
    private int answeredChoiceId;
    private int numberOfCorrectAnswers;

    public AnswerForm(Long questionId, String questionStatement, int numberOfCorrectAnswers) {
        this.questionId = questionId;
        this.questionStatement = questionStatement;
        this.numberOfCorrectAnswers = numberOfCorrectAnswers;
    }

    public AnswerForm() {
    }
}
