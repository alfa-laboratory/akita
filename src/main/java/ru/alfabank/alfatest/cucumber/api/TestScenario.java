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
public final class TestScenario {

    private static TestScenario instance = new TestScenario();

    /**
     * Среда прогона тестов, хранит в себе: Cucumber.Scenario,
     * переменные, объявленные пользователем в сценарии и страницы, тестирование которых будет производиться
     */
    private static TestEnvironment environment;

    private TestScenario() {
    }

    public static TestScenario getInstance() {
        return instance;
    }

    public TestEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(TestEnvironment testEnvironment) {
        environment = testEnvironment;
    }

    public static void sleep(int seconds) {
        Selenide.sleep(TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS));
    }

    /**
     * Получение страницы, тестирование которой производится в данный момент
     */
    public TestPage getCurrentPage() {
        return environment.getPages().getCurrentPage();
    }

    /**
     * Задание страницы, тестирование которой производится в данный момент
     */
    public void setCurrentPage(TestPage page) {
        if (page == null) throw new IllegalArgumentException("Происходит переход на несуществующую страницу. " +
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
    public static <T extends TestPage> void withPage(Class<T> clazz, Consumer<T> consumer) {
        withPage(clazz, true, consumer);
    }

    /**
     * Позволяет получить доступ к полям и методам конкретной страницы.
     * Пример использования: withPage(TestPage.class, page -> { some actions with TestPage methods});
     * Проверка отображения всех элементов страницы опциональна
     *
     * @param clazz класс страницы, доступ к полям и методам которой необходимо получить
     * @param checkIfElementsAppeared флаг, отвечающий за проверку отображения всех элементов страницы, не помеченных аннотацией @Optional
     */
    public static <T extends TestPage> void withPage(Class<T> clazz, boolean checkIfElementsAppeared, Consumer<T> consumer) {
        Pages.withPage(clazz, checkIfElementsAppeared, consumer);
    }

    /**
     * Получение списка страниц
     */
    public Pages getPages() {
        return this.getEnvironment().getPages();
    }

    public TestPage getPage(String name) {
        return this.getEnvironment().getPage(name);
    }

    /**
     * Выводит дополнительный информационный текст в отчет (уровень логирования INFO)
     */
    public void write(Object object) {
        this.getEnvironment().write(object);
    }

    /**
     * Получение переменной по имени, заданного пользователем, из пула переменных "variables" в TestEnvironment
     * @param name - имя переменной, для которй необходимо получить ранее сохраненное значение
     */
    public Object getVar(String name) {
        Object obj = this.getEnvironment().getVar(name);
        if (obj == null) throw new IllegalArgumentException("Переменная " + name + " не найдена");
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
     * @param checkIfElementsAppeared - флаг, определяющий проверку отображения элементов на странице
     */
    public <T extends TestPage> T getPage(Class<T> clazz, boolean checkIfElementsAppeared) {
        return Pages.getPage(clazz, checkIfElementsAppeared);
    }

    /**
     * Получение страницы по классу (проверка отображения элементов страницы не выполняется)
     * @param clazz - класс страницы, которую необходимо получить
     */
    public <T extends TestPage> T getPage(Class<T> clazz) {
        return Pages.getPage(clazz, true);
    }

    /**
     * Получение страницы по классу и имени (оба параметра должны совпадать)
     * @param clazz - класс страницы, которую необходимо получить
     * @param name - название страницы, заданное в аннотации @Name
     */
    public <T extends TestPage> T getPage(Class<T> clazz, String name) {
        return this.getEnvironment().getPage(clazz, name);
    }

    /**
     * Заменяет в строке все ключи переменных из пула переменных "variables" в классе TestEnvironment на их значения
     *
     * @param stringToReplaceIn строка, в которой необходимо выполнить замену (не модифицируется)
     */
    public String replaceVariables(String stringToReplaceIn) {
        return this.getEnvironment().replaceVariables(stringToReplaceIn);
    }

    /**
     *  Добавление переменной в пул "variables" в классе TestEnvironment
     *  @param name имя переменной заданное пользователем, для которого сохраняется значение. Является ключом в пуле variables в классе TestEnvironment
     *  @param object значение, которое нужно сохранить в переменную
     */
    public void setVar(String name, Object object) {
        this.getEnvironment().setVar(name, object);
    }

    /**
     *  Получение всех переменных из пула "variables" в классе TestEnvironment
     */
    public ScopedVariables getVars() {
        return this.getEnvironment().getVars();
    }
}
