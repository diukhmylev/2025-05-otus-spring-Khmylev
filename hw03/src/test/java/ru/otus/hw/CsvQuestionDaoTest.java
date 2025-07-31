package ru.otus.hw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CsvQuestionDao")
class CsvQuestionDaoTest {

    @Test
    @DisplayName("should correctly load questions from CSV file based on locale")
    void shouldLoadQuestionsFromCsvFile() {

        AppProperties properties = new AppProperties();
        properties.setRightAnswersCountToPass(1);
        properties.setLocale("en-US");

        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("en-US", "questions.csv");
        properties.setFileNameByLocaleTag(fileMap);

        CsvQuestionDao dao = new CsvQuestionDao(properties);

        List<Question> questions = dao.findAll();

        assertThat(questions).hasSize(3);

        Question first = questions.get(0);
        assertThat(first.text()).isEqualTo("Is there life on Mars?");
        assertThat(first.answers()).hasSize(3);
        assertThat(first.answers().get(0).text()).isEqualTo("Science doesn't know this yet");
        assertThat(first.answers().get(0).isCorrect()).isTrue();
    }
}
