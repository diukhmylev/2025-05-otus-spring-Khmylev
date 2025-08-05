package ru.otus.hw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CsvQuestionDao")
@SpringBootTest
@ActiveProfiles("test")
class CsvQuestionDaoTest {

    @Autowired
    private CsvQuestionDao dao;

    @Autowired
    private AppProperties properties;

    @Test
    @DisplayName("should correctly load questions from CSV file based on locale")
    void shouldLoadQuestionsFromCsvFile() {

        properties.setLocale("en-US");

        List<Question> questions = dao.findAll();

        assertThat(questions).hasSize(3);

        Question first = questions.get(0);
        assertThat(first.text()).isEqualTo("Is there life on Mars?");
        assertThat(first.answers()).hasSize(3);
        assertThat(first.answers().get(0).text()).isEqualTo("Science doesn't know this yet");
        assertThat(first.answers().get(0).isCorrect()).isTrue();
    }
}