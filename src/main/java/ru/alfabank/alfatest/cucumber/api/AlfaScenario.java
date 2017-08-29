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
     * переменные, объявленные пользователем в сценарии и страницы, тестирование которых будет производиться
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
     * Задание страницы, тестирование которой производится в данный момент
     */
    public void setCurrentPage(AlfaPage page) {
        if (page == null) throw new IllegalArgumentException("Ты пытаешься установить null в качестве current page." +
                "Проверь аннотации @Name у используемых страниц");
        environment.getPages().setCurrentPage(page);
    }

    /**
     * Позволяет получить доступ к полям и методам конкретной страницы, которая передается в метод в качестве аргумента.
     * Пример использования: withPage(TestPage.class, page -> { some actions with TestPage methods});
     * Проверка отображения всех элементов страницы выполняется всегда
     *
     * @param clazz класс страницы, доступ к полям и методам которой необходимо получить
     */
    public static <T extends AlfaPage> void withPage(Class<T> clazz, Consumer<T> consumer) {
        withPage(clazz, true, consumer);
    }

    /**
     * Позволяет получить доступ к полям и методам конкретной страницы.
     * Пример использования: withPage(TestPage.class, page -> { some actions with TestPage methods});
     * Проверка отображения всех элементов страницы опциональна
     *
     * @param clazz класс страницы, доступ к полям и методам которой необходимо получить
     * @param checkIsAppeared флаг, отвечающий за проверку отображения всех элементов страницы, не помеченных аннотацией @Optional
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
     * Получение переменной по имени, заданного пользователем, из пула переменных "variables" в AlfaEnvironment
     * @param name - имя переменной, для которй необходимо получить ранее сохраненное значение
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
     * Получение страницы по классу с возможностью выполнить проверку отображения элементов страницы
     * @param clazz - класс страницы, которую необходимо получить
     * @param checkIsAppeared - флаг, определяющий проверку отображения элементов на странице
     */
    public <T extends AlfaPage> T getPage(Class<T> clazz, boolean checkIsAppeared) {
        return Pages.getPage(clazz, checkIsAppeared);
    }

    /**
     * Получение страницы по классу (проверка отображения элементов страницы не выполняется)
     * @param clazz - класс страницы, которую необходимо получить
     */
    public <T extends AlfaPage> T getPage(Class<T> clazz) {
        return Pages.getPage(clazz, true);
    }

    /**
     * Получение страницы по классу и имени (оба параметра должны совпадать)
     * @param clazz - класс страницы, которую необходимо получить
     * @param name - название страницы, заданное в аннотации @Name
     */
    public <T extends AlfaPage> T getPage(Class<T> clazz, String name) {
        return this.getEnvironment().getPage(clazz, name);
    }

    /**
     * Заменяет в строке все ключи переменных из пула переменных "variables" в классе AlfaEnvironment на их значения
     *
     * @param stringToReplaceIn строка, в которой необходимо выполнить замену (не модифицируется)
     */
    public String replaceVariables(String stringToReplaceIn) {
        return this.getEnvironment().replaceVariables(stringToReplaceIn);
    }

    /**
     *  Добавление переменной в пул "variables" в классе AlfaEnvironment
     *  @param name имя переменной заданное пользователем, для которого сохраняется значение. Является ключом в пуле variables в классе AlfaEnvironment
     *  @param object значение, которое нужно сохранить в переменную
     */
    public void setVar(String name, Object object) {
        this.getEnvironment().setVar(name, object);
    }

    /**
     *  Получение всех переменных из пула "variables" в классе AlfaEnvironment
     */
    public ScopedVariables getVars() {
        return this.getEnvironment().getVars();
    }
}
