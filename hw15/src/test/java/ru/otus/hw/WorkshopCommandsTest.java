package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.command.WorkshopCommands;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.shell.interactive.enabled=false",
        "spring.shell.script.enabled=false"
})
class WorkshopCommandsTest {

    @Autowired
    private WorkshopCommands commands;

    @Test
    void buildChairCommandShouldReturnHumanReadableResult() {
        String result = commands.buildChair("oak");

        assertThat(result)
                .contains("Готовый стул")
                .contains("finished=true");
    }
}
