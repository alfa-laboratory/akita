package ru.alfabank.alfatest.cucumber.api;

import cucumber.api.Scenario;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by ruslanmikhalev on 27/01/17.
 */
@Slf4j
public class AlfaEnvironment {

    private final Scenario scenario;
    private ScopedVariables variables = new ScopedVariables();
    private Pages pages = new Pages();

    public AlfaEnvironment(Scenario scenario) {
        this.scenario = scenario;
        initPages();
    }

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

    private String getClassAnnotationValue(Class<?> c) {
        return Arrays.stream(c.getAnnotationsByType(AlfaPage.Name.class))
                .findAny()
                .orElseThrow(AssertionError::new)
                .value();
    }

    public void write(Object o) {
        scenario.write(String.valueOf(o));
    }

    public ScopedVariables getVars() {
        return variables;
    }

    public Object getVar(String name) {
        Object result = variables.get(name);
        if (result == null) throw new AssertionError("Элемент " + name + " не найден в хранилище");
        return result;
    }

    public void setVar(String name, Object object) {
        variables.put(name, object);
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
        return variables.replaceVariables(address);
    }
}
