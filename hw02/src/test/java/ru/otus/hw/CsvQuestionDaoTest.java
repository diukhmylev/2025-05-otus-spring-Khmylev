package ru.otus.hw;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvQuestionDaoTest {

    @Test
    void shouldLoadQuestionsFromCsvFile() {
        AppProperties properties = new AppProperties(1, "questions.csv");
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
