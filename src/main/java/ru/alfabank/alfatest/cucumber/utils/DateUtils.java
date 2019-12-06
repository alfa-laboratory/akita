package ru.alfabank.alfatest.cucumber.utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum  DateUtils {

    YESTERDAY("вчера", LocalDate.now().minusDays(1)),
    TOMORROW("завтра", LocalDate.now().plusDays(1)),
    TODAY("сегодня", LocalDate.now()),
    MONTH_AGO("месяц назад", LocalDate.now().minusMonths(1)),
    THREE_MONTH_AGO("3 месяца назад", LocalDate.now().minusMonths(3)),
    YEAR_AGO("год назад", LocalDate.now().minusYears(1)),
    MONTH_AHEAD("месяц вперед", LocalDate.now().plusMonths(1)),
    THREE_MONTH_AHEAD("3 месяца вперед", LocalDate.now().plusMonths(3)),
    YEAR_AHEAD("год вперед", LocalDate.now().plusMonths(3));

    private final String textDate;
    private final LocalDate date;

    private static final Map<String, LocalDate> DATES = new HashMap<>();

    static {
        for (DateUtils date: values()) {
            DATES.put(date.getTextDate(), date.getDate());
        }
    }

        public static LocalDate convertStringDateToValues(String expectedTextDate) throws IllegalArgumentException{
        LocalDate date = DATES.get(expectedTextDate);

        if (Objects.isNull(date)) {
            throw new IllegalArgumentException(String.format("Некорректнное значение даты: %s", expectedTextDate));
        }
            return date;
        }
    }
