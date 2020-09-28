package work.ttdd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.ttdd.entity.Choice;
import work.ttdd.entity.Question;
import work.ttdd.repository.ChoiceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChoiceService {

    @Autowired
    ChoiceRepository choiceRepository;

    public List<Choice> findByQuestion(Question question) {
        return choiceRepository.findChoicesByQuestion(question);
    }

    public Map<Long, List<Choice>> getChoiceMap(List<Question> questionList) {
        final Map<Long, List<Choice>> choiceMap = new HashMap<>();
        for (Question question : questionList) {
            choiceMap.put(question.getId(), choiceRepository.findChoicesByQuestion(question));
        }
        return choiceMap;
    }
}
