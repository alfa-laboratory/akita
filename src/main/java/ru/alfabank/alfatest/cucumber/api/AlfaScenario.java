package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.Selenide;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by ruslanmikhalev on 27/01/17.
 */
@Slf4j
final public class AlfaScenario {

    private static AlfaScenario instance = new AlfaScenario();

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

    public AlfaPage getCurrentPage() {
        AlfaPage currentPage = environment.getPages().getCurrentPage();
        if (currentPage == null) throw new AssertionError("Current Page пустой! " +
                "Проверь аннотации @Name у используемых страниц");
        return currentPage;
    }

    public void setCurrentPage(AlfaPage page) {
        if (page == null) throw new AssertionError("Ты пытаешься установить null в качестве current page." +
                "Проверь аннотации @Name у используемых страниц");
        environment.getPages().setCurrentPage(page);
    }

    public static <T extends AlfaPage> void withPage(Class<T> clazz, Consumer<T> consumer) {
        withPage(clazz, true, consumer);
    }

    public static <T extends AlfaPage> void withPage(Class<T> clazz, boolean checkIsAppeared, Consumer<T> consumer) {
        Pages.withPage(clazz, checkIsAppeared, consumer);
    }

    public Pages getPages() {
        return this.getEnvironment().getPages();
    }

    public AlfaPage getPage(String name) {
        return this.getEnvironment().getPage(name);
    }

    public void write(Object o) {
        this.getEnvironment().write(o);
    }

    public Object getVar(String name) {
        Object obj = this.getEnvironment().getVar(name);
        if (obj == null) throw new NullPointerException("Переменная " + name + " не найдена");
        return obj;
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
