package ru.alfabank.alfatest.cucumber.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для элементов страницы,
 * служащая для отключения проверки появления элемента после загрузки страницы
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
}
