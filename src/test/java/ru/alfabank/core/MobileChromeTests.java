package ru.alfabank.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.alfabank.tests.core.drivers.MobileChrome;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by alexander on 16.08.17.
 */
public class MobileChromeTests {
    private static MobileChrome mobileChrome = new MobileChrome();
    private static WebDriver mobileDriver;

    @Test
    public void createDriverTest() {
        mobileDriver = mobileChrome.createDriver(new DesiredCapabilities());
        assertThat(mobileDriver, isA(WebDriver.class));
    }
}
