package ru.otus.hw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.TestService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TestService")
class TestServiceImplTest {

    @Autowired
    private TestService testService;

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Test
    @DisplayName("should correctly process answers and return the test result")
    void shouldCorrectlyProcessAnswersAndReturnResult() {
        Student student = new Student("Ivan", "Ivanov");

        Question question = new Question("2+2?", List.of(
                new Answer("3", false),
                new Answer("4", true),
                new Answer("5", false)
        ));

        when(questionDao.findAll()).thenReturn(List.of(question));
        when(ioService.readString()).thenReturn("2");

        TestResult result = testService.executeTestFor(student);

        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(result.getAnsweredQuestions()).containsExactly(question);

        verify(ioService).printLineLocalized("TestService.answer.the.questions");
        verify(ioService).printLineLocalized("2+2?");
        verify(ioService, times(3)).printFormattedLine(eq("%d. %s"), anyInt(), anyString());
        verify(ioService).printLineLocalized("TestService.enter.answer");
        verify(ioService).readString();
    }
}