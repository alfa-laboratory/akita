package ru.alfabank.alfatest.cucumber.api;

import cucumber.api.Scenario;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.ScopedVariables;

import java.util.Arrays;

/**
 * Created by ruslanmikhalev on 27/01/17.
 */
@Slf4j
public class AlfaEnvironment {

    private Scenario scenario;
    private ThreadLocal<ScopedVariables> variables = new ThreadLocal<>();
    private Pages pages = new Pages();

    public AlfaEnvironment(Scenario scenario) {
        this.scenario = scenario;
        initPages();
    }

    public AlfaEnvironment() {
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

    public AlfaPage getPage(String name) {
        return pages.get(name);
    }

    public <T extends AlfaPage> T getPage(Class<T> clazz, String name) {
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
