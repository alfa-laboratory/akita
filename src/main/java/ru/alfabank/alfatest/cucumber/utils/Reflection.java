package ru.alfabank.alfatest.cucumber.utils;

import java.lang.reflect.Field;

/**
 * Created by ruslanmikhalev on 26/01/17.
 */
final public class Reflection {

    private Reflection() {

    }

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
