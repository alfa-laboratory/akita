package ru.alfabank.alfatest.cucumber.api;

import cucumber.api.Scenario;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import java.util.Arrays;

/**
 * Класс, связанный с AkitaScenario, используется для хранения страниц и переменных внутри сценария
 */
@Slf4j
public class AkitaEnvironment {

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

    public AkitaEnvironment(Scenario scenario) {
        this.scenario = scenario;
        initPages();
    }

    public AkitaEnvironment() {
        initPages();
    }

    /**
     * Метод ищет классы, аннотированные "AkitaPage.Name",
     * добавляя ссылки на эти классы в поле "pages"
     */
    @SuppressWarnings("unchecked")
    private void initPages() {
        new AnnotationScanner().getClassesAnnotatedWith(AkitaPage.Name.class)
                .stream()
                .map(it -> {
                    if (AkitaPage.class.isAssignableFrom(it)) {
                        return (Class<? extends AkitaPage>) it;
                    } else
                        throw new IllegalStateException("Класс " + it.getName() + " должен наследоваться от AkitaPage");
                })
                .forEach(clazz -> pages.put(getClassAnnotationValue(clazz), clazz));
    }

    /**
     * Вспомогательный метод, получает значение аннотации "AkitaPage.Name" для класса
     *
     * @param c класс, который должен быть аннотирован "AkitaPage.Name"
     * @return значение аннотации "AkitaPage.Name" для класса
     */
    private String getClassAnnotationValue(Class<?> c) {
        return Arrays.stream(c.getAnnotationsByType(AkitaPage.Name.class))
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

    public AkitaPage getPage(String name) {
        return pages.get(name);
    }

    public <T extends AkitaPage> T getPage(Class<T> clazz, String name) {
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
