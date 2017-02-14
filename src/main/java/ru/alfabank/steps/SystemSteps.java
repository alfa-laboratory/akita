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

    public static boolean isFilePdf(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        return isFilePdf(file);
    }

    public static boolean isFilePdf(File file) throws FileNotFoundException {
        if (! file.exists()) throw new AssertionError("File not found by path: "
                + file.getAbsolutePath());
        Scanner scanner = new Scanner(file);
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            return line.startsWith(PDF_PREFIX);
        }
        return false;
    }

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
