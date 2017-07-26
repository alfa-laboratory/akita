package ru.alfabank.tests.core.helpers;

import com.google.common.base.Strings;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class PropertyLoader {
    private static final String PROPERTIES_FILE = "/application.properties";
    private static final Properties properties = getPropertiesInstance();
    private static final Properties profileProperties = getProfilePropertiesInstance();

    private PropertyLoader() {

    }

    public static String loadProperty(String name) {
        String value = loadPropertySafe(name);
        if (null == value) {
            throw new IllegalArgumentException("Properties file does not contain property with key: " + name);
        }
        return value;
    }

    public static String loadProperty(String name, String defaultValue) {
        String value = loadPropertySafe(name);
        return value != null ? value: defaultValue;
    }

    public static String loadPropertySafe(String name) {
        String value = null;
        if (!Strings.isNullOrEmpty(name)) {
            value = profileProperties.getProperty(name);

            if (null == value) {
                value = properties.getProperty(name);
            }
        }
        return value;
    }

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

