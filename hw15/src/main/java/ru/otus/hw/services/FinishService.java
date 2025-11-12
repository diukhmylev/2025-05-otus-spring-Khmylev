package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Chair;

@Service
public class FinishService {

    public Chair applyFinish(Chair chair) {
        chair.applyFinish();
        System.out.println("Применена финишная отделка: " + chair);
        return chair;
    }

}
