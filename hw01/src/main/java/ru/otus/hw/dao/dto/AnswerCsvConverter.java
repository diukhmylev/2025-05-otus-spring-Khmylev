package ru.otus.hw.dao.dto;

import com.opencsv.bean.AbstractCsvConverter;
import ru.otus.hw.domain.Answer;

public class AnswerCsvConverter extends AbstractCsvConverter {

    @Override
    public Object convertToRead(String value) {
        if (value == null || !value.contains("%")) {
            return null;
        }
        var valueArr = value.split("%");
        return new Answer(valueArr[0].trim(), Boolean.parseBoolean(valueArr[1].trim()));
    }
}
