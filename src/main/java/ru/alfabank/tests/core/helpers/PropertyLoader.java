package ru.alfabank.tests.core.helpers;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class PropertyLoader {
    private static final String PROPERTIES_FILE = "/application.properties";
    private static final Properties properties = new Properties();
    private static final Properties profileProperties = new Properties();

    static {
        try {
            properties.load(new InputStreamReader(
                    PropertyLoader.class.getResourceAsStream(PROPERTIES_FILE),
                    Charset.forName("UTF-8")
            ));

            String profile = System.getProperty("profile", "");

            if (!Strings.isNullOrEmpty(profile)) {
                profileProperties.load(new InputStreamReader(
                        PropertyLoader.class.getResourceAsStream("/" + profile +PROPERTIES_FILE),
                        Charset.forName("UTF-8")
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private PropertyLoader() {

    }

    public static String loadProperty(String name) {
        String value = null;
        if (!Strings.isNullOrEmpty(name)) {
            value = profileProperties.getProperty(name);

            if (null == value) {
                value = properties.getProperty(name);
            }
        }

        if (null == value) {
            throw new IllegalArgumentException("Properties file does not contain property with key: " + name);
        }
        return value;
    }

    public static String getCus(String username){
        String acus = loadProperty(username+".cus");
        return acus;
    }
}

