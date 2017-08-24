package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Selenide;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Главный класс-синглтон TODO: что сюда писать?
 */
@Slf4j
public final class AlfaScenario {

    private static AlfaScenario instance = new AlfaScenario();

    /**
     * Среда прогона тестов, хранит в себе: сценарий, переменные, объявленные в сценарии
     * и страницы, тестирование которых будет производится
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
     * TODO: что делает метод withPage?
     */
    public static <T extends AlfaPage> void withPage(Class<T> clazz, Consumer<T> consumer) {
        withPage(clazz, true, consumer);
    }
    /**
     * TODO: что делает метод withPage?
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
     * Получение перменной, заданной пользователем, из инстанса AlfaEnvironment по имени
     */
    public Object getVar(String name) {
        Object obj = this.getEnvironment().getVar(name);
        if (obj == null) throw new NullPointerException("Переменная " + name + " не найдена");
        return obj;
    }

    /**
     * Получение перменной без проверки на NULL
     */
    public Object tryGetVar(String name) {
        return this.getEnvironment().getVar(name);
    }

    public <T extends AlfaPage> T getPage(Class<T> clazz, boolean checkIsAppeared) {
        return Pages.getPage(clazz, checkIsAppeared);
    }

    public <T extends AlfaPage> T getPage(Class<T> clazz) {
        return Pages.getPage(clazz, true);
    }

    public <T extends AlfaPage> T getPage(Class<T> clazz, String name) {
        return this.getEnvironment().getPage(clazz, name);
    }

    public String replaceVariables(String address) {
        return this.getEnvironment().replaceVariables(address);
    }

    public void setVar(String name, Object object) {
        this.getEnvironment().setVar(name, object);
    }

    public ScopedVariables getVars() {
        return this.getEnvironment().getVars();
    }
}
