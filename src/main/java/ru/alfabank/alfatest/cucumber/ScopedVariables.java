package ru.alfabank.alfatest.cucumber;

import com.google.common.collect.Maps;
import groovy.lang.GroovyShell;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ruslanmikhalev on 27/01/17.
 */
public class ScopedVariables {

    private ThreadLocal<Map<String, Object>> variablesContainer = new ThreadLocal<>();//Maps.newHashMap();

    public Object evaluate(String expression) {
        GroovyShell shell = new GroovyShell();
        getVariables().entrySet().forEach(e -> {
            try {
                shell.setVariable(e.getKey(), new BigDecimal(e.getValue().toString()));
            } catch (NumberFormatException exp) {
                shell.setVariable(e.getKey(), e.getValue());
            }
        });
        return shell.evaluate(expression);
    }

    public String replaceVariables(String urlName) {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(urlName);
        StringBuffer buffer = new StringBuffer();
        while(m.find()) {
            String varName = m.group(1);
            String value = get(varName).toString();
            m.appendReplacement(buffer, value);
        }
        m.appendTail(buffer);
        return buffer.toString();
    }

    public void put(String name, Object value) {
        getVariables().put(name, value);
    }

    public Object get(String name) {
        return getVariables().get(name);
    }

    public void clear() {
        getVariables().clear();
    }

    public Object remove(String key) {
        return getVariables().remove(key);
    }

    private Map<String, Object> getVariables() {
        if (variablesContainer.get() == null) {
            variablesContainer.set(Maps.newHashMap());
        }
        return variablesContainer.get();
    }

}
