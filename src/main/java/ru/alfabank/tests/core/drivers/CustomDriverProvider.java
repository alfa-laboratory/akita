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
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.proxy.BlacklistEntry;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.alfabank.tests.core.helpers.BlackList;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.WebDriverRunner.*;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadSystemPropertyOrDefault;

/**
 * Провайдер драйверов, который позволяет запускать тесты локально или удаленно, используя Selenoid
 * Параметры запуска можно задавать, как системные переменные.
 *
 * Например, можно указать браузер, версию браузера, remote Url(где будут запущены тесты), ширину и высоту окна браузера:
 * -Dbrowser=chrome -DbrowserVersion=63.0 -DremoteUrl=http://some/url -Dwidth=1000 -Dheight=500
 * Если параметр remoteUrl не указан - тесты будут запущены локально в заданном браузере последней версии
 * Если указан параметр remoteUrl и browser, но версия браузера не указана,
 * по умолчанию для chrome будет установлена версия 60.0 и для firefox версия 57.0
 * Если браузер не указан - по умолчанию будет запущен chrome
 * По умолчанию размер окна браузера при remote запуске равен 1920x1080
 * Предусмотрена возможность запуска в режиме мобильного браузера (-Dbrowser=mobile)
 * С указанием устройства, на котором будем эмулироваться запуск мобильного chrome браузера (-Ddevice=iPhone 6)
 */
@Slf4j
public class CustomDriverProvider implements WebDriverProvider {
    public final static String MOBILE_DRIVER = "mobile";
    public final static String BROWSER = "browser";
    public final static String BROWSER_VERSION = "browserVersion";
    public final static String REMOTE_URL = "remoteUrl";
    public final static String WINDOW_WIDTH = "width";
    public final static String WINDOW_HEIGHT = "height";
    private BrowserMobProxy proxy = new BrowserMobProxyServer();

    @Override
    public WebDriver createDriver(DesiredCapabilities capabilities) {
        String expectedBrowser = loadSystemPropertyOrDefault(BROWSER, CHROME);
        String remoteUrl = loadSystemPropertyOrDefault(REMOTE_URL, "local");
        BlackList blackList = new BlackList();

        if (FIREFOX.equalsIgnoreCase(expectedBrowser)) {
            capabilities = getFirefoxDriverCapabilities();
            return "local".equalsIgnoreCase(remoteUrl) ? new FirefoxDriver() : getRemoteDriver(capabilities, remoteUrl, blackList.getBlacklistEntries());
        }

        if (MOBILE_DRIVER.equalsIgnoreCase(expectedBrowser)) {
            capabilities.setCapability(ChromeOptions.CAPABILITY, getMobileChromeOptions());
            return "local".equalsIgnoreCase(remoteUrl) ? new ChromeDriver(getMobileChromeOptions()) : getRemoteDriver(capabilities, remoteUrl, blackList.getBlacklistEntries());
        }

        if (OPERA.equalsIgnoreCase(expectedBrowser)) {
            capabilities = getOperaDriverCapabilities();
            return "local".equalsIgnoreCase(remoteUrl) ? new OperaDriver() : getRemoteDriver(capabilities, remoteUrl, blackList.getBlacklistEntries());
        }

        log.info("remoteUrl=" + remoteUrl + " expectedBrowser= " + expectedBrowser + " BROWSER_VERSION=" + System.getProperty(BROWSER_VERSION));
        capabilities = getChromeDriverCapabilities();
        return "local".equalsIgnoreCase(remoteUrl) ? new ChromeDriver() : getRemoteDriver(capabilities, remoteUrl, blackList.getBlacklistEntries());
    }

    /**
     * Задает capabilities для запуска Remote драйвера для Selenoid
     *
     * @param capabilities - capabilities для установленного браузера
     * @param remoteUrl    - url для запуска тестов, например http://remoteIP:4444/wd/hub
     * @return
     */
    private WebDriver getRemoteDriver(DesiredCapabilities capabilities, String remoteUrl) {
        log.info("---------------run Selenoid Remote Driver---------------------");
        Integer browserWidth = loadSystemPropertyOrDefault(WINDOW_WIDTH, 1920);
        Integer browserHeight = loadSystemPropertyOrDefault(WINDOW_HEIGHT, 1080);
        capabilities.setCapability("enableVNC", true);
        try {
            RemoteWebDriver driver = new RemoteWebDriver(
                URI.create(remoteUrl).toURL(),
                capabilities
            );
            driver.manage().window().setSize(new Dimension(browserWidth, browserHeight));
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Задает capabilities для запуска Remote драйвера для Selenoid
     * со списком регулярных выражений соответствующих URL, которые добавляются в Blacklist
     *
     * @param capabilities
     * @param remoteUrl
     * @param blacklistEntries - список url для добавления в Blacklist
     * @return
     */
    private WebDriver getRemoteDriver(DesiredCapabilities capabilities, String remoteUrl, List<BlacklistEntry> blacklistEntries) {
        proxy.setBlacklist(blacklistEntries);
        return getRemoteDriver(capabilities, remoteUrl);
    }

    /**
     * Устанавливает ChromeOptions для запуска google chrome эмулирующего работу мобильного устройства (по умолчанию nexus 5)
     * Название мобильного устройства (device) может быть задано через системные переменные
     *
     *@return ChromeOptions
     */
    private ChromeOptions getMobileChromeOptions() {
        log.info("---------------run CustomMobileDriver---------------------");
        String mobileDeviceName = loadSystemPropertyOrDefault("device", "Nexus 5");
        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("disable-extensions",
            "test-type", "no-default-browser-check", "ignore-certificate-errors");

        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", mobileDeviceName);
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

        return chromeOptions;
    }

    /**
     * Задает capabilities для запуска Chrome драйвера
     *
     * @return
     */
    private DesiredCapabilities getChromeDriverCapabilities() {
        log.info("---------------Chrome Driver---------------------");
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities = getCapabilitiesWithCustomFileDownloadFolder(capabilities);
        capabilities.setBrowserName(CHROME);
        capabilities.setVersion(loadSystemPropertyOrDefault(BROWSER_VERSION, "60.0"));
        return capabilities;
    }

    /**
     * Задает capabilities для запуска Firefox драйвера
     *
     * @return
     */
    private DesiredCapabilities getFirefoxDriverCapabilities() {
        log.info("---------------Firefox Driver---------------------");
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setBrowserName(FIREFOX);
        capabilities.setVersion(loadSystemPropertyOrDefault(BROWSER_VERSION, "57.0"));
        return capabilities;
    }

    /**
     * Задает capabilities для запуска Opera драйвера
     *
     * @return
     */
    private DesiredCapabilities getOperaDriverCapabilities() {
        log.info("---------------Opera Driver---------------------");
        DesiredCapabilities capabilities = DesiredCapabilities.operaBlink();
        capabilities.setBrowserName(OPERA);
        capabilities.setVersion(loadSystemPropertyOrDefault(BROWSER_VERSION, "46.0"));
        return capabilities;
    }

    /**
     * Используется для создания хром-браузера с пользовательскими настройками
     * ("profile.default_content_settings.popups", 0) - блокирует всплывающие окна
     * ("download.prompt_for_download", "false") - выключает подтверждение (и выбор пути) для загрузки файла
     * ("download.default_directory", ...) - устанавливает стандартную папку загрузки файлов
     * "plugins.plugins_disabled", new String[]{
     * "Adobe Flash Player", "Chrome PDF Viewer" - выключает плагины
     * <p>
     * Чтобы задать папку для загрузки файлов пропишите абсолютный путь
     * в fileDownloadPath в application.properties
     * <p>
     * Полный список параметров, которые можно установить таким способом:
     * https://sites.google.com/a/chromium.org/chromedriver/capabilities
     */
    private DesiredCapabilities getCapabilitiesWithCustomFileDownloadFolder(DesiredCapabilities capabilities) {
        Map<String, Object> preferences = new Hashtable<>();
        preferences.put("profile.default_content_settings.popups", 0);
        preferences.put("download.prompt_for_download", "false");
        String downloadsPath = System.getProperty("user.home") + "/Downloads";
        preferences.put("download.default_directory", loadSystemPropertyOrDefault("fileDownloadPath", downloadsPath));
        preferences.put("plugins.plugins_disabled", new String[]{
            "Adobe Flash Player", "Chrome PDF Viewer"});
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", preferences);

        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        return capabilities;
    }
}
