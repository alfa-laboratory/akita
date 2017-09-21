package ru.alfabank.tests.core.helpers;

import com.google.common.base.Strings;
import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Класс для получения свойств
 */
public class PropertyLoader {
    private static final String PROPERTIES_FILE = "/application.properties";
    private static final Properties PROPERTIES = getPropertiesInstance();
    private static final Properties PROFILE_PROPERTIES = getProfilePropertiesInstance();

    private PropertyLoader() {

    }

    /**
     * Возвращает значение системного свойства
     * (из доступных для данной JVM) по его названию,
     * в случае, если оно не найдено, вернется значение по умолчанию
     *
     * @param propertyName название свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства по названию или значение по умолчанию
     */
    public static String loadSystemPropertyOrDefault(String propertyName, String defaultValue) {
        String propValue = System.getProperty(propertyName);
        return propValue != null ? propValue : defaultValue;
    }

    /**
     * Возвращает свойство по его названию из property-файла
     *
     * @param propertyName название свойства
     * @return значение свойства, в случае, если значение не найдено,
     * будет выброшено исключение
     */
    public static String loadProperty(String propertyName) {
        String value = tryLoadProperty(propertyName);
        if (null == value) {
            throw new IllegalArgumentException("В файле application.properties не найдено значение по ключу: " + propertyName);
        }
        return value;
    }

    /**
     * Возвращает значение свойства из property-файла по его названию,
     * если значение не найдено, возвращает это же значение в качестве значения по умолчанию
     *
     * @param propertyNameOrValue название свойства/значение по умолчанию
     * @return значение по ключу value, если значение не найдено,
     * вернется value
     */
    public static String getPropertyOrValue(String propertyNameOrValue) {
        return loadProperty(propertyNameOrValue, propertyNameOrValue);
    }

    /**
     * Возвращает значение свойства из property-файла по его названию,
     * Если ничего не найдено, возвращает значение по умолчанию
     *
     * @param propertyName название свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства
     */
    public static String loadProperty(String propertyName, String defaultValue) {
        String value = tryLoadProperty(propertyName);
        return value != null ? value : defaultValue;
    }

    /**
     * Возвращает значение свойства типа Integer из property-файла по названию
     *
     * @param propertyName название свойста
     * @return значение свойства типа Integer
     */
    public static Integer loadPropertyInt(String propertyName) {
        String value = tryLoadProperty(propertyName);
        return Integer.parseInt(value);
    }

    /**
     * Возвращает значение свойства типа Integer из property-файла по названию,
     * если ничего не найдено, возвращает значение по умолчанию
     *
     * @param propertyName название свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства типа Integer или значение по умолчанию
     */
    public static Integer loadPropertyInt(String propertyName, Integer defaultValue) {
        String value = tryLoadProperty(propertyName);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * Вспомогательный метод, возвращает значение свойства по имени.
     * Сначала поиск в property-файле, если указано системное свойство "profile"
     * Если ничего не найдено, поиск в /application.properties
     *
     * @param propertyName название свойства
     * @return значение свойства
     */
    public static String tryLoadProperty(String propertyName) {
        String value = null;
        if (!Strings.isNullOrEmpty(propertyName)) {
            value = PROFILE_PROPERTIES.getProperty(propertyName);

            if (null == value) {
                value = PROPERTIES.getProperty(propertyName);
            }
        }
        return value;
    }

    /**
     * Вспомогательный метод, возвращает свойства из файла /application.properties
     *
     * @return свойства из файла /application.properties
     */
    @SneakyThrows(IOException.class)
    private static Properties getPropertiesInstance() {
        Properties instance = new Properties();
        try (
                InputStream resourceStream = PropertyLoader.class.getResourceAsStream(PROPERTIES_FILE);
                InputStreamReader inputStream = new InputStreamReader(resourceStream, Charset.forName("UTF-8"))
        ) {
            instance.load(inputStream);
        }
        return instance;
    }

    /**
     * Вспомогательный метод, возвращает свойства из кастомного application.properties по пути
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
            String path = Paths.get(profile, PROPERTIES_FILE).toString();
            URL url = PropertyLoader.class.getClassLoader().getResource(path);
            try(
                    InputStream resourceStream = url.openStream();
                    InputStreamReader inputStream = new InputStreamReader(resourceStream, Charset.forName("UTF-8"))
            ) {
                instance.load(inputStream);
            }
        }
        return instance;
    }
}

