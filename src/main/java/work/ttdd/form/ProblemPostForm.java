package work.ttdd.form;

import lombok.Data;

@Data
public class ProblemPostForm {
    private String title;
    private String description;

    public ProblemPostForm(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public ProblemPostForm() {
    }
}
