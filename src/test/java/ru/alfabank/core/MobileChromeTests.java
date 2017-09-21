package ru.alfabank.core;

import org.junit.AfterClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.alfabank.tests.core.drivers.MobileChrome;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class MobileChromeTests {
    private static MobileChrome mobileChrome = new MobileChrome();
    private static WebDriver mobileDriver;

    @Test
    public void createDriverTest() {
        mobileDriver = mobileChrome.createDriver(new DesiredCapabilities());
        assertThat(mobileDriver, isA(WebDriver.class));
    }

    @AfterClass
    public static void close() {
        mobileDriver.close();
    }
}
