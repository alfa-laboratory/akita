package ru.alfabank.alfatest.cucumber.api;

import cucumber.api.Scenario;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import java.util.Arrays;

/**
 * Класс, связанный с TestScenario, используется для хранения страниц и переменных внутри сценария
 */
@Slf4j
public class TestEnvironment {

    /**
     * Сценарий (Cucumber.api), с которым связана среда
     */
    private Scenario scenario;
    /**
     * Переменные, объявленные пользователем внутри сценария
     * ThreadLocal обеспечивает отсутствие коллизий при многопоточном запуске
     */
    private ThreadLocal<ScopedVariables> variables = new ThreadLocal<>();
    /**
     * Список веб-страниц, заданных пользователем, доступных для использования в сценариях
     */
    private Pages pages = new Pages();

    public TestEnvironment(Scenario scenario) {
        this.scenario = scenario;
        initPages();
    }

    public TestEnvironment() {
        initPages();
    }

    /**
     * Метод ищет классы, аннотированные "TestPage.Name",
     * добавляя ссылки на эти классы в поле "pages"
     */
    @SuppressWarnings("unchecked")
    private void initPages() {
        new AnnotationScanner().getClassesAnnotatedWith(TestPage.Name.class)
                .stream()
                .map(it -> {
                    if(TestPage.class.isAssignableFrom(it)){
                        return (Class<? extends TestPage>)it;
                    }
                    else throw new IllegalStateException("Класс " + it.getName() + " должен наследоваться от TestPage");
                })
                .forEach(clazz -> pages.put(getClassAnnotationValue(clazz), clazz));
    }

    /**
     * Вспомогательный метод, получает значение аннотации "TestPage.Name" для класса
     *
     * @param c класс, который должен быть аннотирован "TestPage.Name"
     * @return значение аннотации "TestPage.Name" для класса
     */
    private String getClassAnnotationValue(Class<?> c) {
        return Arrays.stream(c.getAnnotationsByType(TestPage.Name.class))
                .findAny()
                .orElseThrow(AssertionError::new)
                .value();
    }

    /**
     * Выводит дополнительный информационный текст в отчет (уровень логирования INFO)
     */
    public void write(Object object) {
        scenario.write(String.valueOf(object));
    }

    public ScopedVariables getVars() {
        return getVariables();
    }

    public Object getVar(String name) {
        return getVariables().get(name);
    }

    public void setVar(String name, Object object) {
        getVariables().put(name, object);
    }

    public Pages getPages() {
        return pages;
    }

    public TestPage getPage(String name) {
        return pages.get(name);
    }

    public <T extends TestPage> T getPage(Class<T> clazz, String name) {
        return pages.get(clazz, name);
    }

    public String replaceVariables(String textToReplaceIn) {
        return getVariables().replaceVariables(textToReplaceIn);
    }

    private ScopedVariables getVariables() {
        if (variables.get() == null) {
            variables.set(new ScopedVariables());
        }
        return variables.get();
    }
}
