package ru.alfabank.steps;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by onotole on 09.02.17.
 */
@Slf4j
public class SystemSteps {
    private static final String PDF_PREFIX = "%PDF";


    private SystemSteps() {
    }

    /**
     * Проверка, что переданный файл имеет тип PDF
     */
    public static boolean isFilePdf(String filePath) throws FileNotFoundException {
        return isFilePdf(new File(filePath));
    }

    public static boolean isFilePdf(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new AssertionError("File not found by path: " + file.getAbsolutePath());
        }
        try (Scanner scanner = new Scanner(file)) {
            return scanner.hasNextLine() && scanner.nextLine().startsWith(PDF_PREFIX);
        }
    }

    /**
     * В переданном Enum'e ищется элемент, переданный строкой
     */
    public static <E extends Enum<E>> E enumElementLookup(Class<E> e, String id) {
        try {
            return Enum.valueOf(e, id);
        } catch (IllegalArgumentException ex) {
            log.error("В enum не найден запрошенный элемент: " + id);
            throw new RuntimeException(
                    "Invalid value for enum " + e.getSimpleName() + ": " + id);
        }
    }
}