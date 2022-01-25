package ru.alfabank.tests.core.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class ResourceHelper {

    /**
     * Получить файл по относительному пути ресурса
     *
     * @param resourcePath относительный путь к ресурсу, например "data/test.yml"
     *
     * @return File
     *
     */
    static public File getFile(String resourcePath) {
        try {
            URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
            if (resourceUrl == null) {
                throw new FileNotFoundException(
                        String.format("Could not find the data file: %s, specify a valid path.", resourcePath)
                );
            }
            return new File(resourceUrl.getFile());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
