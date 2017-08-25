package ru.alfabank.alfatest.cucumber.utils;

import java.lang.reflect.Field;

/**
 * Created by ruslanmikhalev on 26/01/17.
 */
public final class Reflection {

    private Reflection() {

    }

    public static Object extractFieldValue(Field field, Object owner) {
        field.setAccessible(true);
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }
}
