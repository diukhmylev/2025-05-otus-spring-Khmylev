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

            boolean validInput = false;
            while (!validInput) {
                ioService.printLineLocalized("TestService.enter.answer");
                try {
                    int userChoice = Integer.parseInt(ioService.readString());

                    if (userChoice >= 1 && userChoice <= answers.size()) {
                        Answer selectedAnswer = answers.get(userChoice - 1);
                        boolean isAnswerValid = selectedAnswer.isCorrect();
                        testResult.applyAnswer(question, isAnswerValid);
                        validInput = true;
                    } else {
                        ioService.printLineLocalized("TestService.invalid.option");
                    }
                } catch (NumberFormatException e) {
                    ioService.printLineLocalized("TestService.invalid.input");
                }
            }
        }

        return testResult;
    }
}
