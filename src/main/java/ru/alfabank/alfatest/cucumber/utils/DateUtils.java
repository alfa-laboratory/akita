package ru.alfabank.alfatest.cucumber.utils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

        public static LocalDate yesterday() {
            return LocalDate.now().minusDays(1);
        }
        public static LocalDate tomorrow() {
            return LocalDate.now().plusDays(1);
        }
        public static LocalDate today() {
            return LocalDate.now();
        }
        public static LocalDate monthAgo() {
            return LocalDate.now().minusMonths(1);
        }
        public static LocalDate threeMonthAgo() {
            return LocalDate.now().minusMonths(3);
        }
        public static LocalDate yearAgo() {
            return LocalDate.now().minusYears(1);
        }
        public static LocalDate monthAhead() {
            return LocalDate.now().plusMonths(1);
        }
        public static LocalDate threeMonthAhead() {
            return LocalDate.now().plusMonths(3);
        }
        public static LocalDate yearAhead() {
            return LocalDate.now().plusYears(1);
        }
        public static LocalDate convertStringDateToValues(String expectedDate) {
            switch (expectedDate) {
           case "сегодня":
                return today();
           case "вчера":
                return yesterday();
           case "завтра":
                return tomorrow();
           case "месяц назад":
                return monthAgo();
           case "3 месяца назад":
                return threeMonthAgo();
           case "год назад":
                return yearAgo();
           case "месяц вперед":
                return monthAhead();
           case "3 месяца вперед":
                return threeMonthAhead();
           case "год вперед":
                return yearAhead();
                default:
                    return LocalDate.parse(expectedDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }
        }
    }
