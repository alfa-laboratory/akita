package ru.alfabank.steps;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by onotole on 09.02.17.
 */
class SystemStepsTest {
    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    void isFilePdfPositive() throws FileNotFoundException {
        File file = new File(classLoader.getResource("example.pdf").getFile());
        assertTrue(SystemSteps.isFilePdf(file), "открытый файл на самом деле pdf");
    }

    @Test
    void isFilePdfNegative() throws FileNotFoundException {
        File file = new File(classLoader.getResource("image.png").getFile());
        assertFalse(SystemSteps.isFilePdf(file), "этот файл не pdf");
    }

}