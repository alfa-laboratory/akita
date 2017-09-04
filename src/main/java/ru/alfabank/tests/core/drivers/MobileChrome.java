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


/**
 *  Эмуляция мобильной версии браузера Google Chrome
 */


@Slf4j
public class MobileChrome implements WebDriverProvider {

    /**
     * Создание instance google chrome эмулирующего работу на мобильном устройстве (по умолчанию nexus 5)
     * Мобильное устройство может быть задано через системные переменные
     * @param capabilities настройки Chrome браузера
     * @return возвращает новый instance Chrome драйера
     */

    @Override
    public WebDriver createDriver(DesiredCapabilities capabilities) {
        log.info("---------------run CustomMobileDriver---------------------");
        String mobileDeviceName = loadSystemPropertyOrDefault("device", "Nexus 5");
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
