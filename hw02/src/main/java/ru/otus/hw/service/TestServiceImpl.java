package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            ioService.printLine("");
            ioService.printLine("Question: " + question.text());

            List<Answer> answers = question.answers();
            for (int i = 0; i < answers.size(); i++) {
                ioService.printFormattedLine("%d. %s", i + 1, answers.get(i).text());
            }

            ioService.printFormattedLine("Your answer (number):");

            int userChoice = -1;
            try {
                userChoice = Integer.parseInt(ioService.readString());
            } catch (NumberFormatException e) {
                ioService.printLine("Invalid input. Please enter a number.");
            }

            boolean isAnswerValid = false;

            if (userChoice >= 1 && userChoice <= answers.size()) {
                Answer selectedAnswer = answers.get(userChoice - 1);
                isAnswerValid = selectedAnswer.isCorrect();
            } else {
                ioService.printLine("Invalid option selected.");
            }

            testResult.applyAnswer(question, isAnswerValid);
        }

        return testResult;
    }
}
