package ru.alfabank.tests.core.helpers;

import com.google.common.base.Strings;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Класс для загрузки свойств
 */
public class PropertyLoader {
    private static final String PROPERTIES_FILE = "/application.properties";
    private static final Properties PROPERTIES = getPropertiesInstance();
    private static final Properties PROFILE_PROPERTIES = getProfilePropertiesInstance();

    private PropertyLoader() {

    }

    /**
     * Загрузка системного свойства по ключу,
     * в случае, если оно не найдено, вернется значение по умолчанию
     *
     * @param propertyName название параметра
     * @param defaultValue значение по умолчанию
     * @return значение параметра системы по названию или значение по умолчанию
     */
    public static String loadSystemPropertyOrDefault(String propertyName, String defaultValue) {
        String propValue = System.getProperty(propertyName);
        return propValue != null ? propValue : defaultValue;
    }

    /**
     * Возвращает свойство по ключу из property-файла
     *
     * @param name ключ
     * @return значение параметра, в случае, если значение не найдено,
     * будет выброшено исключение
     */
    public static String loadProperty(String name) {
        String value = tryLoadProperty(name);
        if (null == value) {
            throw new IllegalArgumentException("Properties file does not contain property with key: " + name);
        }
        return value;
    }

    /**
     * Возвращает свойство из property-файла по ключу,
     * если значение не найдено, возвращает это же значение в качестве значения по умолчанию
     *
     * @param value ключ или значение по умолчанию
     * @return значение по ключу value, если значение не найдено,
     * вернется value
     */
    public static String getPropertyOrValue(String value) {
        return loadProperty(value, value);
    }

    /**
     * Загружет значение из файла property-файла по ключу,
     * Если ничего не найдено, возвращает значение по умолчанию
     *
     * @param name название свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства
     */
    public static String loadProperty(String name, String defaultValue) {
        String value = tryLoadProperty(name);
        return value != null ? value: defaultValue;
    }

    /**
     * Загружет значение типа Integer из property-файла по ключу,
     * Сначала идёт попытка поиска, если указано системное свойство "profile"
     * Если ничего не найдено, поиск в файле /application.properties
     *
     * @param varName название свойста
     * @return значение свойства типа Integer
     */
    public static Integer loadPropertyInt(String varName) {
        String value = tryLoadProperty(varName);
        return Integer.parseInt(value);
    }

    /**
     * Загружет значение типа Integer из файла property-файла по ключу,
     * если ничего не найдено, возвращает значение по умолчанию
     *
     * @param varName название свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства типа Integer или значение по умолчанию
     */
    public static Integer loadPropertyInt(String varName, Integer defaultValue) {
        String value = tryLoadProperty(varName);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * Вспомогательный метод загрузки свойства по имени.
     * Сначала идёт попытка поиска, если указано системное свойство "profile"
     * Если ничего не найдено, поиск в файле /application.properties
     *
     * @param name название свойства
     * @return значение свойства
     */
    public static String tryLoadProperty(String name) {
        String value = null;
        if (!Strings.isNullOrEmpty(name)) {
            value = PROFILE_PROPERTIES.getProperty(name);

            if (null == value) {
                value = PROPERTIES.getProperty(name);
            }
        }
        return value;
    }

    /**
     * Вспомогательный метод чтения свойств из файла /application.properties
     *
     * @return прочитанные свойства
     */
    @SneakyThrows(IOException.class)
    private static Properties getPropertiesInstance() {
        Properties instance = new Properties();
        try(
                InputStream resourceStream = PropertyLoader.class.getResourceAsStream(PROPERTIES_FILE);
                InputStreamReader inputStream = new InputStreamReader(resourceStream, Charset.forName("UTF-8"))
        ) {
            instance.load(inputStream);
        }
        return instance;
    }

    /**
     * Вспомогательный метод чтения кастомного application.properties по пути
     * из системного свойства "profile"
     *
     * @return прочитанные свойства из кастомного файла application.properties,
     * если свойство "profile" указано,
     * иначе пустой объект
     */
    @SneakyThrows(IOException.class)
    private static Properties getProfilePropertiesInstance() {
        Properties instance = new Properties();
        String profile = System.getProperty("profile", "");
        if (!Strings.isNullOrEmpty(profile)) {
            try(
                    InputStream resourceStream = PropertyLoader.class.getResourceAsStream("/" + profile + PROPERTIES_FILE);
                    InputStreamReader inputStream = new InputStreamReader(resourceStream, Charset.forName("UTF-8"))
            ) {
                instance.load(inputStream);
            }
        }
        return instance;
    }
}

