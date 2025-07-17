package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;
    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLineLocalized("TestService.answer.the.questions");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            ioService.printLineLocalized(question.text());

            List<Answer> answers = question.answers();
            for (int i = 0; i < answers.size(); i++) {
                ioService.printFormattedLine("%d. %s", i + 1, answers.get(i).text());
            }

            ioService.printLineLocalized("TestService.enter.answer");

            int userChoice = -1;
            try {
                userChoice = Integer.parseInt(ioService.readString());
            } catch (NumberFormatException e) {
                ioService.printLineLocalized("TestService.invalid.input");
                continue;
            }

            boolean isAnswerValid = false;

            if (userChoice >= 1 && userChoice <= answers.size()) {
                Answer selectedAnswer = answers.get(userChoice - 1);
                isAnswerValid = selectedAnswer.isCorrect();
            } else {
                ioService.printLineLocalized("TestService.invalid.option");
            }

            testResult.applyAnswer(question, isAnswerValid);
        }

        return testResult;
    }
}
