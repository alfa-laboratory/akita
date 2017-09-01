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

<<<<<<< HEAD
    public static Object extractFieldValue(Field field, Object owner) {
        field.setAccessible(true);
=======
    /**
     * Получение поля класса с помощью механизма рефлексии
     */
    public static Object extractFieldValue(Field f, Object owner) {
        f.setAccessible(true);
>>>>>>> e356702bb47ab4a4f1aebacda8a1bb92f5fc9531
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }
}
