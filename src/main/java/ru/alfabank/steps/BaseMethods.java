/**
 * Copyright 2017 Alfa Laboratory
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.steps;

import com.codeborne.selenide.SelenideElement;
import com.galenframework.api.Galen;
import com.galenframework.reports.model.LayoutReport;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import io.restassured.http.Method;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.openqa.selenium.Keys;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.tests.core.rest.RequestParam;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.readonly;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static io.restassured.RestAssured.given;
import static java.util.Objects.isNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.alfabank.alfatest.cucumber.ScopedVariables.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.*;

/**
 * Общие методы, используемые в различных шагах
 */


@Slf4j
public class BaseMethods {

    protected AkitaScenario akitaScenario = AkitaScenario.getInstance();

    protected static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 15000);

    protected static final String SPECS_DIR_PATH = loadSystemPropertyOrDefault("specsDir",
            System.getProperty("user.dir") + "/src/test/resources/specs/");
    protected static final String IMG_DIFF_PATH = loadSystemPropertyOrDefault("imgDiff",
            System.getProperty("user.dir") + "/build/results-img/");


    /**
     * Создание запроса
     *
     * @param paramsTable массив с параметрами
     * @return сформированный запрос
     */
    public RequestSender createRequest(List<RequestParam> paramsTable) {
        String body = null;
        RequestSpecification request = given();
        for (RequestParam requestParam : paramsTable) {
            String name = requestParam.getName();
            String value = requestParam.getValue();
            switch (requestParam.getType()) {
                case PARAMETER:
                    request.param(name, value);
                    break;
                case HEADER:
                    request.header(name, value);
                    break;
                case BODY:
                    value = loadValueFromFileOrPropertyOrVariableOrDefault(value);
                    body = resolveVars(value);
                    request.body(body);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Некорректно задан тип %s для параметра запроса %s ", requestParam.getType(), name));
            }
        }
        if (body != null) {
            akitaScenario.write("Тело запроса:\n" + body);
        }
        return request;
    }

    /**
     * Получает body из ответа и сохраняет в переменную
     *
     * @param variableName имя переменной, в которую будет сохранен ответ
     * @param response     ответ от http запроса
     */
    public void getBodyAndSaveToVariable(String variableName, Response response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            akitaScenario.setVar(variableName, response.getBody().asString());
            akitaScenario.write("Тело ответа : \n" + new Prettifier().getPrettifiedBodyIfPossible(response, response));
        } else {
            fail("Некорректный ответ на запрос: " + new Prettifier().getPrettifiedBodyIfPossible(response, response));
        }
    }

    /**
     * Сравнение кода http ответа с ожидаемым
     *
     * @param response           ответ от сервиса
     * @param expectedStatusCode ожидаемый http статус код
     * @return возвращает true или false в зависимости от ожидаемого и полученного http кодов
     */
    public boolean checkStatusCode(Response response, int expectedStatusCode) {
        int statusCode = response.getStatusCode();
        if (statusCode != expectedStatusCode) {
            akitaScenario.write("Получен неверный статус код ответа " + statusCode + ". Ожидаемый статус код " + expectedStatusCode);
        }
        return statusCode == expectedStatusCode;
    }

    /**
     * Отправка http запроса
     *
     * @param method      тип http запроса
     * @param address     url, на который будет направлен запроc
     * @param paramsTable список параметров для http запроса
     */
    public Response sendRequest(String method, String address,
                                List<RequestParam> paramsTable) {
        address = loadProperty(address, resolveVars(address));
        RequestSender request = createRequest(paramsTable);
        return request.request(Method.valueOf(method), address);
    }


    protected Configuration createJsonPathConfiguration() {
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(new GsonJsonProvider())
                .mappingProvider(new GsonMappingProvider())
                .build();
    }

    public String nextWindowHandle() {
        String currentWindowHandle = getWebDriver().getWindowHandle();
        Set<String> windowHandles = getWebDriver().getWindowHandles();
        windowHandles.remove(currentWindowHandle);
        return windowHandles.iterator().next();
    }

    /**
     * Возвращает значение из property файла, если отсутствует, то из пользовательских переменных,
     * если и оно отсутствует, то возвращает значение переданной на вход переменной
     *
     * @return
     */
    public String getPropertyOrStringVariableOrValue(String propertyNameOrVariableNameOrValue) {
        String propertyValue = tryLoadProperty(propertyNameOrVariableNameOrValue);
        String variableValue = (String) akitaScenario.tryGetVar(propertyNameOrVariableNameOrValue);

        boolean propertyCheck = checkResult(propertyValue, "Переменная " + propertyNameOrVariableNameOrValue + " из property файла");
        boolean variableCheck = checkResult(variableValue, "Переменная сценария " + propertyNameOrVariableNameOrValue);

        return propertyCheck ? propertyValue : (variableCheck ? variableValue : propertyNameOrVariableNameOrValue);
    }

    private boolean checkResult(String result, String message) {
        if (isNull(result)) {
            log.warn(message + " не найдена");
            return false;
        }
        log.info(message + " = " + result);
        akitaScenario.write(message + " = " + result);
        return true;
    }

    /**
     * Возвращает каталог "Downloads" в домашней директории
     *
     * @return
     */
    public File getDownloadsDir() {
        String homeDir = System.getProperty("user.home");
        return new File(homeDir + "/Downloads");
    }

    /**
     * Удаляет файлы, переданные в метод
     *
     * @param filesToDelete массив файлов
     */
    public void deleteFiles(File[] filesToDelete) {
        for (File file : filesToDelete) {
            file.delete();
        }
    }

    /**
     * Возвращает случайное число от нуля до maxValueInRange
     *
     * @param maxValueInRange максимальная граница диапазона генерации случайных чисел
     */
    public int getRandom(int maxValueInRange) {
        return (int) (Math.random() * maxValueInRange);
    }

    /**
     * Возвращает последовательность случайных символов переданных алфавита и длины
     * Принимает на вход варианты языков 'ru' и 'en'
     * Для других входных параметров возвращает латинские символы (en)
     */
    public String getRandCharSequence(int length, String lang) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char symbol = charGenerator(lang);
            builder.append(symbol);
        }
        return builder.toString();
    }

    /**
     * Возвращает случайный символ переданного алфавита
     */
    public char charGenerator(String lang) {
        Random random = new Random();
        if (lang.equals("ru")) {
            return (char) (1072 + random.nextInt(32));
        } else {
            return (char) (97 + random.nextInt(26));
        }
    }

    /**
     * Проверка на соответствие строки паттерну
     */
    public boolean isTextMatches(String str, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }

    /**
     * Возвращает локатор для поиска по нормализованному(без учета регистра) тексту
     */
    public String getTranslateNormalizeSpaceText(String expectedText) {
        StringBuilder text = new StringBuilder();
        text.append("//*[contains(translate(normalize-space(text()), ");
        text.append("'ABCDEFGHIJKLMNOPQRSTUVWXYZАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ', ");
        text.append("'abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхчшщъыьэюя'), '");
        text.append(expectedText.toLowerCase());
        text.append("')]");
        return text.toString();
    }

    /**
     * Прикрепляет файл к текущему сценарию в cucumber отчете
     * @param fileName - название файла
     * @param mimeType - тип файла
     */
    @SneakyThrows
    public static void embedFileToReport(File fileName, String mimeType) {
        AkitaScenario.getInstance().getScenario()
                .embed(FileUtils.readFileToByteArray(fileName), mimeType);
    }

    @SneakyThrows
    /**
     * Проверяет соответствие текущей страницы ее описанию в .spec файле.
     * Скриншоты с расходениями в дизайне сохраняются в /build/results-img/ и прикрепояются к cucumber отчету
     * Путь /build/results-img/ можно переопределить, задав системную переменную imgDiff
     */
    public void checkLayoutAccordingToSpec(String spec, List<String> tags) {
        LayoutReport report = Galen.checkLayout(getWebDriver(), SPECS_DIR_PATH + spec, tags);
        report.getFileStorage().copyAllFilesTo(new File(IMG_DIFF_PATH));
        if (report.errors() > 0) {
            embedScreenshotAndFail(report);
        }
    }

    private void embedScreenshotAndFail(LayoutReport report) {
        Map<String, File> screenshots = report.getFileStorage().getFiles();
        screenshots.forEach((key, value) -> {
            if (key.contains("map") || key.contains("expected") || key.contains("actual")) {
                akitaScenario.write(key);
                embedFileToReport(value, "image/png");
            }
        });
        fail(report.getValidationErrorResults().toString());
    }

    public void loadPage(String nameOfPage) {
        akitaScenario.setCurrentPage(akitaScenario.getPage(nameOfPage));
        if (isIE()) {
            akitaScenario.getCurrentPage().ieAppeared();
        } else akitaScenario.getCurrentPage().appeared();
    }

    public void cleanField(String nameOfField) {
        SelenideElement valueInput = akitaScenario.getCurrentPage().getElement(nameOfField);
        Keys removeKey = isIE() ? Keys.BACK_SPACE : Keys.DELETE;
        do {
            valueInput.shouldNotBe(readonly, disabled).doubleClick().sendKeys(removeKey);
        } while (valueInput.getValue().length() != 0);
    }

    /**
     * Выдергиваем число из строки
     */
    public int getCounterFromString(String variableName) {
        return Integer.parseInt(variableName.replaceAll("[^0-9]",""));
    }

    public void checkPageTitle(String pageTitleName) {
        pageTitleName = getPropertyOrStringVariableOrValue(pageTitleName);
        String currentTitle = getWebDriver().getTitle().trim();
        assertThat(String.format("Заголовок страницы не совпадает с ожидаемым значением. Ожидаемый результат: %s, текущий результат: %s", pageTitleName, currentTitle),
                pageTitleName, IsEqualIgnoringCase.equalToIgnoringCase(currentTitle));
    }
}