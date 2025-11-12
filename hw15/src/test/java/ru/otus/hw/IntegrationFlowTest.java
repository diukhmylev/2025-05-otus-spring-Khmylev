package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import ru.otus.hw.domain.Board;
import ru.otus.hw.domain.Chair;
import ru.otus.hw.gateway.WorkshopGateway;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.shell.interactive.enabled=false",
        "spring.shell.script.enabled=false"
})
@ExtendWith(OutputCaptureExtension.class)
class IntegrationFlowTest {

    @Autowired
    private WorkshopGateway gateway;

    @Test
    void shouldBuildFinishedChairAndLogCutMessage(CapturedOutput output) {

        Chair chair = gateway.processBoard(new Board("oak"));
        assertThat(chair).isNotNull();
        assertThat(chair.isFinished()).isTrue();

        assertThat(output.getOut())
                .contains("Доска для изготовления стула");
    }
}
