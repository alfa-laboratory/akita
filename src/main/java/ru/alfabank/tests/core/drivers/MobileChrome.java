/**
 * Copyright 2017 Alfa Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
