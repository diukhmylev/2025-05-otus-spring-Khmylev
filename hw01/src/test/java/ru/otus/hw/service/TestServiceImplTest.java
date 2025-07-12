package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static  org.mockito.Mockito.atLeastOnce;
import static  org.mockito.Mockito.anyString;
import static  org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void shouldExecuteTestWithCorrectAnswer() {
        Question question = new Question("Is this test?",
                List.of(
                        new Answer("Yes", true),
                        new Answer("No", false)
                ));
        when(questionDao.findAll()).thenReturn(List.of(question));

        when(ioService.readLine())
                .thenReturn("Name")
                .thenReturn("1");

        testService.executeTest();

        verify(ioService, atLeastOnce()).printFormattedLine(anyString(), any());
        verify(ioService).printLine("Question: Is this test?");
        verify(ioService).printFormattedLine("%d. %s", 1, "Yes");
        verify(ioService).printFormattedLine("%d. %s", 2, "No");

        verify(ioService).printFormattedLine("Test completed. Correct answers: %d out of %d", 1, 1);
    }

    @Test
    void shouldHandleIncorrectAnswerInputGracefully() {
        Question question = new Question("2 + 2 = ?",
                List.of(
                        new Answer("3", false),
                        new Answer("4", true)
                ));
        when(questionDao.findAll()).thenReturn(List.of(question));

        when(ioService.readLine())
                .thenReturn("Name")
                .thenReturn("5");

        testService.executeTest();

        verify(ioService).printLine("Invalid option selected.");
        verify(ioService).printFormattedLine("Test completed. Correct answers: %d out of %d", 0, 1);
    }
}