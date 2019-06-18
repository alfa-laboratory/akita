/**
 * Copyright 2017 Alfa Laboratory
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.alfatest.cucumber;

import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import groovy.lang.GroovyShell;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

/**
 * Реализация хранилища переменных, заданных пользователем, внутри тестовых сценариев
 */
public class ScopedVariables {

    public static final String VARIABLE_NAME_PATTERN = "[{]([\\wа-яА-Я]+[\\wа-яА-Я.-]+[\\wа-яА-Я]+)[}]";

    private Map<String, Object> variables = Maps.newHashMap();

    /**
     * Компилирует и выполняет в рантайме переданный на вход java/groovy-код.
     * Предварительно загружает в память все переменные,
     * т.е. на вход в строковом аргументе могут быть переданы переменные из "variables"
     *
     * @param expression java/groovy-код, который будет выполнен
     */
    public Object evaluate(String expression) {
        GroovyShell shell = new GroovyShell();
        variables.entrySet().forEach(e -> {
            try {
                shell.setVariable(e.getKey(), new BigDecimal(e.getValue().toString()));
            } catch (NumberFormatException exp) {
                shell.setVariable(e.getKey(), e.getValue());
            }
        });
        return shell.evaluate(expression);
    }

    /**
     * Заменяет в строке все ключи переменных из "variables" на их значения
     *
     * @param textToReplaceIn строка, в которой необходимо выполнить замену (не модифицируется)
     */
    public String replaceVariables(String textToReplaceIn) {
        Pattern p = Pattern.compile(VARIABLE_NAME_PATTERN);
        Matcher m = p.matcher(textToReplaceIn);
        StringBuffer buffer = new StringBuffer();
        while (m.find()) {
            String varName = m.group(1);
            String value = get(varName).toString();
            m.appendReplacement(buffer, value);
        }
        m.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Производит поиск в заданной строке на наличие совпадений параметров.
     * В случае нахождения параметра в строке заменяет его значение на значение из properties или хранилища переменных
     *
     * @param inputString заданная строка
     * @return новая строка
     */
    public static String resolveVars(String inputString) {
        Pattern p = Pattern.compile(VARIABLE_NAME_PATTERN);
        Matcher m = p.matcher(inputString);
        String newString = "";
        List<String> unresolvedVariables = new ArrayList<>();
        while (m.find()) {
            String varName = m.group(1);
            String value = loadProperty(varName, (String) AkitaScenario.getInstance().tryGetVar(varName));
            if (value == null) {
                unresolvedVariables.add(varName);
                value = varName;
            }
            newString = m.replaceFirst(value);
            m = p.matcher(newString);
        }
        if (!unresolvedVariables.isEmpty()) {
            throw new IllegalArgumentException(
                "Значения " + unresolvedVariables +
                " не были найдены ни в application.properties, ни в environment переменной");
        }
        if (newString.isEmpty()) {
            newString = inputString;
        }
        return newString;
    }

    /**
     * Проверяет, является ли переданная в качестве аргумента строка валидным JSON
     * @param jsonInString - строка для валидации
     * @return
     */
    public static boolean isJSONValid(String jsonInString) {
        try {
            JsonParser parser = new JsonParser();
            parser.parse(jsonInString);
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
        return true;
    }

    public void put(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(String name) {
        return variables.get(name);
    }

    public void clear() {
        variables.clear();
    }

    public Object remove(String key) {
        return variables.remove(key);
    }

}
