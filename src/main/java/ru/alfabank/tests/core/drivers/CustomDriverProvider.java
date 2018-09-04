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
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import ru.alfabank.tests.core.helpers.BlackList;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.WebDriverRunner.*;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadSystemPropertyOrDefault;

/**
 * Провайдер драйверов, который позволяет запускать тесты локально или удаленно, используя Selenoid
 * Параметры запуска можно задавать, как системные переменные.
 *
 * Например, можно указать браузер, версию браузера, remote Url(где будут запущены тесты), ширину и высоту окна браузера:
 * -Dbrowser=chrome -DbrowserVersion=63.0 -DremoteUrl=http://some/url -Dwidth=1200 -Dheight=800 -Doptions=--load-extension=my-custom-extension
 * Если параметр remoteUrl не указан - тесты будут запущены локально в заданном браузере последней версии.
 * Все необходимые опции можно прописывать в переменную options, разделяя их пробелом.
 * Если указан параметр remoteUrl и browser, но версия браузера не указана,
 * по умолчанию будет установлена версия latest
 * Если браузер не указан - по умолчанию будет запущен chrome
 * По умолчанию размер окна браузера при remote запуске равен 1920x1080
 * Предусмотрена возможность запуска в режиме мобильного браузера (-Dbrowser=mobile)
 * С указанием устройства, на котором будем эмулироваться запуск мобильного chrome браузера (-Ddevice=iPhone 6)
 * Если указан параметр headless, то браузеры firefox и chrome будут запускаться без GUI (-Dheadless=true)
 */
@Slf4j
public class CustomDriverProvider implements WebDriverProvider {
    public final static String MOBILE_DRIVER = "mobile";
    public final static String BROWSER = "browser";
    public final static String REMOTE_URL = "remoteUrl";
    public final static String HEADLESS = "headless";
    public final static String WINDOW_WIDTH = "width";
    public final static String WINDOW_HEIGHT = "height";
    public final static String VERSION_LATEST = "latest";
    public final static String LOCAL = "local";
    public final static int DEFAULT_WIDTH = 1920;
    public final static int DEFAULT_HEIGHT = 1080;

    private BrowserMobProxy proxy = new BrowserMobProxyServer();
    private String[] options = loadSystemPropertyOrDefault("options", "").split(" ");

    @Override
    public WebDriver createDriver(DesiredCapabilities capabilities) {
        String expectedBrowser = loadSystemPropertyOrDefault(BROWSER, CHROME);
        String remoteUrl = loadSystemPropertyOrDefault(REMOTE_URL, LOCAL);
        BlackList blackList = new BlackList();

        if (FIREFOX.equalsIgnoreCase(expectedBrowser)) {
            return LOCAL.equalsIgnoreCase(remoteUrl) ? createFirefoxDriver(capabilities) : getRemoteDriver(getFirefoxDriverOptions(capabilities), remoteUrl, blackList.getBlacklistEntries());
        }

        if (MOBILE_DRIVER.equalsIgnoreCase(expectedBrowser)) {
            return LOCAL.equalsIgnoreCase(remoteUrl) ? new ChromeDriver(getMobileChromeOptions(capabilities)) : getRemoteDriver(getMobileChromeOptions(capabilities), remoteUrl, blackList.getBlacklistEntries());
        }

        if (OPERA.equalsIgnoreCase(expectedBrowser)) {
            return LOCAL.equalsIgnoreCase(remoteUrl) ? createOperaDriver(capabilities) : getRemoteDriver(getOperaDriverOptions(capabilities), remoteUrl, blackList.getBlacklistEntries());
        }

        if (SAFARI.equalsIgnoreCase(expectedBrowser)) {
            return LOCAL.equalsIgnoreCase(remoteUrl) ? createSafariDriver(capabilities) : getRemoteDriver(getSafariDriverOptions(capabilities), remoteUrl, blackList.getBlacklistEntries());
        }

        if (INTERNET_EXPLORER.equalsIgnoreCase(expectedBrowser)) {
            return createIEDriver(capabilities);
        }

        log.info("remoteUrl=" + remoteUrl + " expectedBrowser= " + expectedBrowser + " BROWSER_VERSION=" + System.getProperty(CapabilityType.BROWSER_VERSION));
        return LOCAL.equalsIgnoreCase(remoteUrl) ? createChromeDriver(capabilities) : getRemoteDriver(getChromeDriverOptions(capabilities), remoteUrl, blackList.getBlacklistEntries());
    }

    /**
     * Задает capabilities для запуска Remote драйвера для Selenoid
     *
     * @param capabilities - capabilities для установленного браузера
     * @param remoteUrl    - url для запуска тестов, например http://remoteIP:4444/wd/hub
     * @return WebDriver
     */
    private WebDriver getRemoteDriver(MutableCapabilities capabilities, String remoteUrl) {
        log.info("---------------run Selenoid Remote Driver---------------------");
        capabilities.setCapability("enableVNC", true);
        capabilities.setCapability("screenResolution", String.format("%sx%s", loadSystemPropertyOrDefault(WINDOW_WIDTH, DEFAULT_WIDTH),
                loadSystemPropertyOrDefault(WINDOW_HEIGHT, DEFAULT_HEIGHT)));
        try {
            return new RemoteWebDriver(
                URI.create(remoteUrl).toURL(),
                capabilities
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Задает capabilities для запуска Remote драйвера для Selenoid
     * со списком соответствующих URL, которые добавляются в Blacklist
     * URL для добавления в Blacklist могут быть указаны в формате регулярных выражений
     *
     * @param capabilities
     * @param remoteUrl
     * @param blacklistEntries - список url для добавления в Blacklist
     * @return WebDriver
     */
    private WebDriver getRemoteDriver(MutableCapabilities capabilities, String remoteUrl, List<BlacklistEntry> blacklistEntries) {
        proxy.setBlacklist(blacklistEntries);
        return getRemoteDriver(capabilities, remoteUrl);
    }

    /**
     * Устанавливает ChromeOptions для запуска google chrome эмулирующего работу мобильного устройства (по умолчанию nexus 5)
     * Название мобильного устройства (device) может быть задано через системные переменные
     *
     *@return ChromeOptions
     */
    private ChromeOptions getMobileChromeOptions(DesiredCapabilities capabilities) {
        log.info("---------------run CustomMobileDriver---------------------");
        String mobileDeviceName = loadSystemPropertyOrDefault("device", "Nexus 5");
        ChromeOptions chromeOptions = new ChromeOptions().addArguments("disable-extensions",
            "test-type", "no-default-browser-check", "ignore-certificate-errors");

        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", mobileDeviceName);
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        chromeOptions.merge(capabilities);
        return chromeOptions;
    }

    /**
     * Задает options для запуска Chrome драйвера
     * options можно передавать, как системную переменную, например -Doptions=--load-extension=my-custom-extension
     * @return ChromeOptions
     */
    private ChromeOptions getChromeDriverOptions(DesiredCapabilities capabilities) {
        log.info("---------------Chrome Driver---------------------");
        ChromeOptions chromeOptions = !options[0].equals("") ? new ChromeOptions().addArguments(options) : new ChromeOptions();
        chromeOptions.setCapability(CapabilityType.BROWSER_VERSION, loadSystemPropertyOrDefault(CapabilityType.BROWSER_VERSION, VERSION_LATEST));
        chromeOptions.setHeadless(loadSystemPropertyOrDefault(HEADLESS, false));
        chromeOptions.merge(capabilities);
        return chromeOptions;
    }

    /**
     * Задает options для запуска Firefox драйвера
     * options можно передавать, как системную переменную, например -Doptions=--load-extension=my-custom-extension
     * @return FirefoxOptions
     */
    private FirefoxOptions getFirefoxDriverOptions(DesiredCapabilities capabilities) {
        log.info("---------------Firefox Driver---------------------");
        FirefoxOptions firefoxOptions = !options[0].equals("") ? new FirefoxOptions().addArguments(options) : new FirefoxOptions();
        firefoxOptions.setCapability(CapabilityType.BROWSER_VERSION, loadSystemPropertyOrDefault(CapabilityType.BROWSER_VERSION, VERSION_LATEST));
        firefoxOptions.setHeadless(loadSystemPropertyOrDefault(HEADLESS, false));
        firefoxOptions.merge(capabilities);
        return firefoxOptions;
    }

    /**
     * Задает options для запуска Opera драйвера
     * options можно передавать, как системную переменную, например -Doptions=--load-extension=my-custom-extension
     * @return operaOptions
     */
    private OperaOptions getOperaDriverOptions(DesiredCapabilities capabilities) {
        log.info("---------------Opera Driver---------------------");
        OperaOptions operaOptions = !options[0].equals("") ? new OperaOptions().addArguments(options) : new OperaOptions();
        operaOptions.setCapability(CapabilityType.BROWSER_VERSION, loadSystemPropertyOrDefault(CapabilityType.BROWSER_VERSION, VERSION_LATEST));
        operaOptions.merge(capabilities);
        return operaOptions;
    }

    /**
     * Задает options для запуска IE драйвера
     * options можно передавать, как системную переменную, например -Doptions=--load-extension=my-custom-extension
     * @return internetExplorerOptions
     */
    private InternetExplorerOptions getIEDriverOptions(DesiredCapabilities capabilities){
        log.info("---------------IE Driver---------------------");
        InternetExplorerOptions internetExplorerOptions = !options[0].equals("") ? new InternetExplorerOptions().addCommandSwitches(options) : new InternetExplorerOptions();
        internetExplorerOptions.setCapability(CapabilityType.BROWSER_VERSION, loadSystemPropertyOrDefault(CapabilityType.BROWSER_VERSION, VERSION_LATEST));
        internetExplorerOptions.merge(capabilities);
        return internetExplorerOptions;
    }

    /**
     * Задает options для запуска Safari драйвера
     * options можно передавать, как системную переменную, например -Doptions=--load-extension=my-custom-extension
     * @return SafariOptions
     */
    private SafariOptions getSafariDriverOptions(DesiredCapabilities capabilities){
        log.info("---------------Safari Driver---------------------");
        SafariOptions safariOptions = new SafariOptions();
        safariOptions.setCapability(CapabilityType.BROWSER_VERSION, loadSystemPropertyOrDefault(CapabilityType.BROWSER_VERSION, VERSION_LATEST));
        safariOptions.merge(capabilities);
        return safariOptions;
    }

    /**
     * Создает экземпляр ChromeDriver с переданными capabilities и window dimensions
     *
     * @return WebDriver
     */
    private WebDriver createChromeDriver(DesiredCapabilities capabilities){
        ChromeDriver chromeDriver = new ChromeDriver(getChromeDriverOptions(capabilities));
        chromeDriver.manage().window().setSize(setDimension());
        return chromeDriver;
    }

    /**
     * Создает экземпляр FirefoxDriver с переданными capabilities и window dimensions
     *
     * @return WebDriver
     */
    private WebDriver createFirefoxDriver(DesiredCapabilities capabilities){
        FirefoxDriver firefoxDriver = new FirefoxDriver(getFirefoxDriverOptions(capabilities));
        firefoxDriver.manage().window().setSize(setDimension());
        return firefoxDriver;
    }

    /**
     * Создает экземпляр OperaDriver с переданными capabilities и window dimensions
     *
     * @return WebDriver
     */
    private WebDriver createOperaDriver(DesiredCapabilities capabilities){
        OperaDriver operaDriver = new OperaDriver(getOperaDriverOptions(capabilities));
        operaDriver.manage().window().setSize(setDimension());
        return operaDriver;
    }

    /**
     * Создает экземпляр InternetExplorerDriver с переданными capabilities и window dimensions
     *
     * @return WebDriver
     */
    private WebDriver createIEDriver(DesiredCapabilities capabilities){
        InternetExplorerDriver internetExplorerDriver = new InternetExplorerDriver(getIEDriverOptions(capabilities));
        internetExplorerDriver.manage().window().setSize(setDimension());
        return internetExplorerDriver;
    }

    /**
     * Создает экземпляр SafariDriver с переданными capabilities и window dimensions
     *
     * @return WebDriver
     */
    private WebDriver createSafariDriver(DesiredCapabilities capabilities){
        SafariDriver safariDriver = new SafariDriver(getSafariDriverOptions(capabilities));
        safariDriver.manage().window().setSize(setDimension());
        return safariDriver;
    }

    /**
     * Задает настройки разрешения окна браузера
     * Пользовательские значения ширины и высоты окна браузера можно задать, обозначив параметры
     * -Dwidth и -Dheight при старте тестов.
     * Например: ./gradlew test -Dbrowser=chrome -Dwidth=1200 -Dheight=800
     * Если пользовательские значения ширины и высоты окна браузера не указаны, используются дефолтные 1920x1080
     */
    private Dimension setDimension(){
        return new Dimension(loadSystemPropertyOrDefault(WINDOW_WIDTH, DEFAULT_WIDTH),
                loadSystemPropertyOrDefault(WINDOW_HEIGHT, DEFAULT_HEIGHT));
    }
}
