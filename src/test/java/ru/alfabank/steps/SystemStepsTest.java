package ru.alfabank.steps;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by onotole on 09.02.17.
 */
public class SystemStepsTest {
    ClassLoader classLoader = getClass().getClassLoader();

    @Test
    public void isFilePdfPositive() throws FileNotFoundException {
        File file = new File(classLoader.getResource("example.pdf").getFile());
        assertThat("открытый файл на самом деле pdf", SystemSteps.isFilePdf(file), equalTo(true));
    }

    @Test
    public void isFilePdfNegative() throws FileNotFoundException {
        File file = new File(classLoader.getResource("image.png").getFile());
        assertThat("этот файл не pdf", SystemSteps.isFilePdf(file), equalTo(false));
    }

}