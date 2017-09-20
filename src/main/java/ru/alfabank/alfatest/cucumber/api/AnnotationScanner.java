package ru.alfabank.alfatest.cucumber.api;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Для поиска классов с заданной аннотацией среди всех классов в проекте на основе механизма рефлексии
 */
public class AnnotationScanner {

    private static Reflections reflection = new Reflections();

    public Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotation) {
        return reflection.getTypesAnnotatedWith(annotation);
    }

}