package ru.alfabank.tests.core.drivers;

import com.codeborne.selenide.WebDriverProvider;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

import static ru.alfabank.tests.core.helpers.PropertyLoader.loadSystemPropertyOrDefault;

@Slf4j
public class MobileChrome implements WebDriverProvider {

    @Override
    public WebDriver createDriver(DesiredCapabilities capabilities) {
        log.info("---------------run CustomMobileDriver---------------------");
        String mobileDeviceName = loadSystemPropertyOrDefault("device", "Google Nexus 5");
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", mobileDeviceName);

        Map<String, Object> chromeOptions = new HashMap<>();
        chromeOptions.put("mobileEmulation", mobileEmulation);

        DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        desiredCapabilities.setBrowserName(desiredCapabilities.chrome().getBrowserName());
        return new ChromeDriver(desiredCapabilities);
    }


}
