package ru.alfabank.alfatest.cucumber.api;

import cucumber.api.Scenario;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import java.util.Arrays;

/**
 * Класс, связанный с AlfaScenario, используется для хранения страниц и переменных внутри сценария
 */
@Slf4j
public class AlfaEnvironment {

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

    public AlfaEnvironment(Scenario scenario) {
        this.scenario = scenario;
        initPages();
    }

    public AlfaEnvironment() {
        initPages();
    }

    /**
     * Метод ищет классы, аннотированные "AlfaPage.Name",
     * добавляя ссылки на эти классы в поле "pages"
     */
    @SuppressWarnings("unchecked")
    private void initPages() {
        new AnnotationScanner().getClassesAnnotatedWith(AlfaPage.Name.class)
                .stream()
                .map(it -> {
                    if(AlfaPage.class.isAssignableFrom(it)){
                        return (Class<? extends AlfaPage>)it;
                    }
                    else throw new IllegalStateException("Class " + it.getName() + " must be a subclass of AlfaPage");
                })
                .forEach(clazz -> pages.put(getClassAnnotationValue(clazz), clazz));
    }

    /**
     * Вспомогательный метод, получает значение аннотации "AlfaPage.Name" для класса
     *
     * @param c класс, который должен быть аннотирован "AlfaPage.Name"
     * @return значение аннотации "AlfaPage.Name" для класса
     */
    private String getClassAnnotationValue(Class<?> c) {
        return Arrays.stream(c.getAnnotationsByType(AlfaPage.Name.class))
                .findAny()
                .orElseThrow(AssertionError::new)
                .value();
    }

    /**
     * Выводит дополнительный информационный текст в отчет (уровень логирования INFO)
     */
    public void write(Object o) {
        scenario.write(String.valueOf(o));
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

    public AlfaPage getPage(String name) {
        return pages.get(name);
    }

    public <T extends AlfaPage> T getPage(Class<T> clazz, String name) {
        return pages.get(clazz, name);
    }

    public String replaceVariables(String address) {
        return getVariables().replaceVariables(address);
    }

    private ScopedVariables getVariables() {
        if (variables.get() == null) {
            variables.set(new ScopedVariables());
        }
        return variables.get();
    }
}
