package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static  org.mockito.Mockito.atLeastOnce;
import static  org.mockito.Mockito.any;

import static org.assertj.core.api.Assertions.assertThat;

class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void shouldCorrectlyProcessAnswersAndReturnResult() {
        Student student = new Student("Ivan", "Ivanov");
        Question question1 = new Question("2+2?", List.of(
                new Answer("3", false),
                new Answer("4", true),
                new Answer("5", false)
        ));

        when(questionDao.findAll()).thenReturn(List.of(question1));
        when(ioService.readString()).thenReturn("2");

        TestResult result = testService.executeTestFor(student);

        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(result.getAnsweredQuestions()).containsExactly(question1);
        verify(ioService, atLeastOnce()).printLine(any());
    }
}