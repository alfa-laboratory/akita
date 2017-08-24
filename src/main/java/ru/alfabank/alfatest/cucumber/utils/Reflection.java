package ru.alfabank.alfatest.cucumber.utils;

import java.lang.reflect.Field;

/**
 * Реализация механизма рефлексии для доступа к аннотациям классов
 * Необходимо для сбора списка страниц, на которых будет производиться тестирование
 * и для сбора элементов с этих страниц
 */
public final class Reflection {

    private Reflection() {

    }

    /**
     * Получение поля класса с помощью механизма рефлексии
     */
    public static Object extractFieldValue(Field f, Object owner) {
        f.setAccessible(true);
        try {
            return f.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            f.setAccessible(false);
        }
    }
}
