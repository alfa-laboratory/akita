package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Selenide;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Главный класс, отвечающий за сопровождение тестовых шагов
 */
@Slf4j
public final class AlfaScenario {

    private static AlfaScenario instance = new AlfaScenario();

    /**
     * Среда прогона тестов, хранит в себе: Cucumber.Scenario,
     * переменные, объявленные в сценарии и страницы, тестирование которых будет производиться
     */
    private static AlfaEnvironment environment;

    private AlfaScenario() {
    }

    public static AlfaScenario getInstance() {
        return instance;
    }

    public AlfaEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(AlfaEnvironment alfaEnvironment) {
        environment = alfaEnvironment;
    }

    public static void sleep(int seconds) {
        Selenide.sleep(TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS));
    }

    /**
     * Получение страницы, тестирование которой производится в данный момент
     */
    public AlfaPage getCurrentPage() {
        AlfaPage currentPage = environment.getPages().getCurrentPage();
        if (currentPage == null) throw new AssertionError("Current Page пустой! " +
                "Проверь аннотации @Name у используемых страниц");
        return currentPage;
    }

    /**
     * Установка страницы, тестирование которой производится в данный момент
     */
    public void setCurrentPage(AlfaPage page) {
        if (page == null) throw new IllegalArgumentException("Ты пытаешься установить null в качестве current page." +
                "Проверь аннотации @Name у используемых страниц");
        environment.getPages().setCurrentPage(page);
    }

    /**
     * Реализация анонимных методов со страницей в качестве аргумента,
     * проверка всех элементов страницы выполняется всегда
     *
     * @param clazz класс страницы
     */
    public static <T extends AlfaPage> void withPage(Class<T> clazz, Consumer<T> consumer) {
        withPage(clazz, true, consumer);
    }

    /**
     * Реализация анонимных методов со страницей в качестве аргумента
     * проверка всех элементов страницы опциональна
     *
     * @param clazz класс страницы
     * @param checkIsAppeared проверка всех не помеченных "@Optional" элементов
     */
    public static <T extends AlfaPage> void withPage(Class<T> clazz, boolean checkIsAppeared, Consumer<T> consumer) {
        Pages.withPage(clazz, checkIsAppeared, consumer);
    }

    /**
     * Получение списка страниц
     */
    public Pages getPages() {
        return this.getEnvironment().getPages();
    }

    public AlfaPage getPage(String name) {
        return this.getEnvironment().getPage(name);
    }

    /**
     * Выводит информативно-отладочный текст в отчет
     */
    public void write(Object o) {
        this.getEnvironment().write(o);
    }

    /**
     * Получение переменной, заданной пользователем, из инстанса AlfaEnvironment по имени
     */
    public Object getVar(String name) {
        Object obj = this.getEnvironment().getVar(name);
        if (obj == null) throw new NullPointerException("Переменная " + name + " не найдена");
        return obj;
    }

    /**
     * Получение переменной без проверки на NULL
     */
    public Object tryGetVar(String name) {
        return this.getEnvironment().getVar(name);
    }

    /**
     * Получение страницы по классу с возможностью выполнить проверку элементов страницы
     */
    public <T extends AlfaPage> T getPage(Class<T> clazz, boolean checkIsAppeared) {
        return Pages.getPage(clazz, checkIsAppeared);
    }

    /**
     * Получение страницы по классу (проверка не выполняется)
     */
    public <T extends AlfaPage> T getPage(Class<T> clazz) {
        return Pages.getPage(clazz, true);
    }

    /**
     * Получение страницы по классу и имени (должно совпадать и то, и другое)
     */
    public <T extends AlfaPage> T getPage(Class<T> clazz, String name) {
        return this.getEnvironment().getPage(clazz, name);
    }

    /**
     * Заменяет в строке все ключи переменных из "variables" на их значения
     *
     * @param stringToReplaceIn строка, в которой необходимо выполнить замену (не модифицируется)
     */
    public String replaceVariables(String stringToReplaceIn) {
        return this.getEnvironment().replaceVariables(stringToReplaceIn);
    }

    /**
     *  Добавление переменной
     */
    public void setVar(String name, Object object) {
        this.getEnvironment().setVar(name, object);
    }

    /**
     *  Получение всех переменных
     */
    public ScopedVariables getVars() {
        return this.getEnvironment().getVars();
    }
}
