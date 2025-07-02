package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        greetStudent();

        String fullName = ioService.readLine();
        ioService.printFormattedLine("Hello, %s! Let's start the test.", fullName);

        List<Question> questions = questionDao.findAll();
        int correctAnswers = conductTest(questions);

        printResult(correctAnswers, questions.size());
    }

    private void greetStudent() {
        ioService.printFormattedLine("Welcome to the student test!");
        ioService.printFormattedLine("Please enter your first and last name:");
    }

    private int conductTest(List<Question> questions) {
        int correct = 0;

        for (Question question : questions) {
            ioService.printLine("");
            ioService.printLine("Question: " + question.text());

            List<Answer> answers = question.answers();
            for (int i = 0; i < answers.size(); i++) {
                ioService.printFormattedLine("%d. %s", i + 1, answers.get(i).text());
            }

            ioService.printFormattedLine("Your answer (number):");
            int userChoice = Integer.parseInt(ioService.readLine());

            if (userChoice >= 1 && userChoice <= answers.size()) {
                if (answers.get(userChoice - 1).isCorrect()) {
                    correct++;
                }
            } else {
                ioService.printLine("Invalid option selected.");
            }
        }
        return correct;
    }

    private void printResult(int correctAnswers, int totalQuestions) {
        ioService.printLine("");
        ioService.printFormattedLine("Test completed. Correct answers: %d out of %d",
                correctAnswers, totalQuestions);
    }
}
