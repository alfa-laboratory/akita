package ru.alfabank.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.*;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ru.alfabank.steps.DefaultApiSteps.getURLwithPathParamsCalculated;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadPropertyInt;

/**
 * В alfaScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем AlfaPage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */
@Slf4j
public class DefaultSteps {

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);

    /**
     * Читаем значение переменной из application.properties и сохраняем в переменную в alfaScenario,
     * для дальнейшего переиспользования
     */
    @И("^сохранено значение \"([^\"]*)\" из property файла в переменную \"([^\"]*)\"$")
    public void saveValueToVar(String propertyVariableName, String variableName) {
        alfaScenario.setVar(variableName, loadProperty(propertyVariableName));
    }

    /**
     * Обновляем страницу страницы
     */
    @И("^выполнено обновление текущей страницы$")
    public void refreshPage() {
        getWebDriver().navigate().refresh();
    }

    /**
     * Переходим по ссылке, разрезолвливая переменные из хранилища alfaScenario
     */
    @Когда("^совершен переход по ссылке \"([^\"]*)\"$")
    public void goToUrl(String address) {
        String url = replaceVariables(address);
        getWebDriver().get(url);
        alfaScenario.write("Url = " + url);
    }

    /**
     * Проверка, что текущий URL совпадает с ожидаемым
     */
    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = getWebDriver().getCurrentUrl();
        String expectedUrl = replaceVariables(url);
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, Matchers.is(expectedUrl));
    }

    /**
     * На странице ищется элемент и по нему кликается
     */
    @И("^выполнено нажатие на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        alfaScenario.getCurrentPage().getElement(elementName).click();
    }

    /**
     * Проверка, что в течении 10 секунд ожидается появление элемента(не списка) на странице
     */
    @И("^элемент \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elementName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, alfaScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка. В течение заданного количества секунд ожидается появление элемента(не списка) на странице
     */
    @И("^элемент \"([^\"]*)\" отобразился на странице в течение (\\d+) (?:секунд|секунды)")
    public void testElementAppeared(String elementName, int seconds) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, seconds * 1000, alfaScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Время задается в application.properties как "waitingCustomElementsTimeout" или по дефолту 10 секунд
     * Проверка, что в течении нескольких секунд ожидается появление списка на странице
     */
    @И("^список \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String elementName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, alfaScenario.getCurrentPage().getElementsList(elementName)
        );
    }

    /**
     * Проверка. В течении 10 секунд ожидаем пока элемент исчезнет (станет невидимым)
     */
    @И("^ожидается исчезновение элемента \"([^\"]*)\"")
    public void elemDisappered(String elementName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, DEFAULT_TIMEOUT, alfaScenario.getCurrentPage().getElement(elementName));
    }

    /**
     * Проверяем, что все элементы, которые описаны в классе страницы с аннотацией @Name, но без аннотации @Optional,
     * видны на странице. При необходимости ждем.
     */
    @Когда("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        alfaScenario.setCurrentPage(alfaScenario.getPage(nameOfPage));
        alfaScenario.getCurrentPage().appeared();
    }

    /**
     * Задать значение переменной в хранилище переменных. Один из кейсов: установка userCus для степов, использующих его.
     */
    @И("^установлено значение переменной \"([^\"]*)\" равным \"(.*)\"$")
    public void setVariable(String variableName, String value) {
        alfaScenario.setVar(variableName, value);
    }

    /**
     * Проверка. Из хранилища достаются значения двух перменных, и сравниваются на равенство. (для строк)
     */
    @Когда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compareTwoVariables(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = getVar(firstVariableName).toString();
        String secondValueToCompare = getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] не совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, equalTo(secondValueToCompare));
    }

    /**
     * Проверка. Текстовое значение из поля совпадает со значением заданной переменной из хранилища.
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String variableName) {
        String actualValue = alfaScenario.getCurrentPage().getAnyElementText(elementName);
        String expectedValue = alfaScenario.getVar(variableName).toString();
        assertThat(String.format("Значение поля [%s] не совпадает со значением из переменной [%s]", elementName, variableName),
                expectedValue, equalTo(actualValue));
    }

    /**
     * Проверка. Из хранилища достаём список по заданному ключу. Проверяем, что текстовое значение из поля содержится в списке.
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список из переменной \"([^\"]*)\" содердит значение (?:поля|элемента) \"([^\"]*)\" $")
    public void checkIfListContainsValueFromField(String elementName, String variableListName) {
        String actualValue = alfaScenario.getCurrentPage().getAnyElementText(elementName);
        List<String> listFromVariable = ((List<String>) alfaScenario.getVar(variableListName));
        assertTrue(String.format("Список из переменной [%s] не содержит значение поля [%s]", elementName, variableListName),
                listFromVariable.contains(actualValue));
    }

    /**
     * Совершается переход по заданной ссылке.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @Deprecated
    @И("^совершен переход на страницу \"([^\"]*)\" по (?:ссылке|ссылке из property файла) = \"([^\"]*)\"$")
    public void goToSelectedPageByLinkFromProperty(String pageName, String urlName) {
        urlName = loadProperty(urlName, getURLwithPathParamsCalculated(urlName));
        alfaScenario.write(" url = " + urlName);
        WebDriverRunner.getWebDriver().get(urlName);
        loadPage(pageName);
    }

    /**
     * Совершается переход по заданной ссылке.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по (?:ссылке|ссылке из property файла) \"([^\"]*)\"$")
    public void goToSelectedPageByLinkFromPropertyFile(String pageName, String urlOrName) {
        String address = loadProperty(urlOrName, getURLwithPathParamsCalculated(urlOrName));
        alfaScenario.write(" url = " + address);
        WebDriverRunner.getWebDriver().get(address);
        loadPage(pageName);
    }

    /**
     * Ожидание заданное количество секунд
     */
    @Когда("^выполнено ожидание в течение (\\d+) (?:секунд|секунды)")
    public void waitForSeconds(long seconds) {
        sleep(1000 * seconds);
    }

    /**
     * проверка, что блок исчез/стал невидимым
     */
    @Тогда("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        alfaScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * Эмулирует нажатие на клавиатуре клавиш.
     */
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    public void pushButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        WebDriverRunner.getWebDriver().switchTo().activeElement().sendKeys(key);
    }

    /**
     * Ищется указанное текстовое поле и устанавливается в него заданное значение. Перед использованием поле нужно очистить
     */
    @Когда("^в поле \"([^\"]*)\" введено значение \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(elementName);
        valueInput.setValue(String.valueOf(value));
    }

    /**
     * Ищется поле и очишается
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String fieldName) {
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(fieldName);
        valueInput.click();
        valueInput.clear();
        valueInput.setValue("");
        valueInput.doubleClick().sendKeys(Keys.DELETE);
    }

    /**
     * Проверка, что поле для ввода пустое
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String fieldName) {
        SelenideElement field = alfaScenario.getCurrentPage().getElement(fieldName);
        assertThat(String.format("Поле [%s] не пусто", fieldName),
                alfaScenario.getCurrentPage().getAnyElementText(fieldName),
                Matchers.isEmptyOrNullString());
    }

    /**
     * Устанавливает размеры окна браузера
     */
    @И("^установить разрешение экрана \"([^\"]*)\" ширина и \"([^\"]*)\" высота$")
    public void setWindowSize(String widthRaw, String heightRaw) {
        int width = Integer.valueOf(widthRaw);
        int height = Integer.valueOf(heightRaw);
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(width, height));
    }

    /**
     * Разворачивает окно с браузером на весь экран
     */
    @Если("^окно развернуто на весь экран$")
    public void expandWindowToFullScreen() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }

    /**
     * Проверка, что в списке со страницы есть перечисленные в таблице элементы
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов из таблицы$")
    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = alfaScenario.getCurrentPage().getAnyElementsListTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), numberOfTypes, Matchers.is(textTable.size()));
        assertTrue(String.format("Значения элементов в списке [%s] не совпадают с ожидаемыми значениями из таблицы", listName), actualValues.containsAll(textTable));
    }

    /**
     * В списке со страницу кликаем по элементу, содержащим заданное значение
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент с (?:текстом|значением) \"(.*)\"$")
    public void checkIfSelectedListElementMatchesValue(String listName, String value) {
        List<SelenideElement> listOfTypeFromPage = alfaScenario.getCurrentPage().getElementsList(listName);
        Optional<SelenideElement> itemFound = listOfTypeFromPage.stream().filter(type -> type.innerText().equals(value)).findFirst();
        if (itemFound.isPresent()) {
            itemFound.get().click();
        } else {
            throw new IllegalArgumentException(String.format("Элемент [%s] не найден в списке [%s] ", value, listName));
        }
    }

    /**
     * Сохранение значения элемента в переменную
     */
    @Когда("^значение (?:элемента|поля) \"([^\"]*)\" сохранено в переменную \"([^\"]*)\"")
    public void storeElementValueInVariable(String elementName, String variableName) {
        alfaScenario.setVar(variableName, alfaScenario.getCurrentPage().getAnyElementText(elementName));
    }

    /**
     * Проверка выражения на истинность
     * Например, string1.equals(string2)
     * OR string.equals("string")
     * Любое Java-выражение, возвращающие boolean
     */
    @Тогда("^верно, что \"([^\"]*)\"$")
    public void expressionExpression(String expression) {
        alfaScenario.getVars().evaluate("assert(" + expression + ")");
    }

    /**
     * Переход на страницу по клику и проверка, что страница загружена
     */
    @И("^выполнен переход на страницу \"([^\"]*)\" после нажатия на (?:ссылку|кнопку) \"([^\"]*)\"$")
    public void urlClickAndCheckRedirection(String pageName, String elementName) {
        alfaScenario.getCurrentPage().getElement(elementName).click();
        loadPage(pageName);
        alfaScenario.write(" url = " + WebDriverRunner.getWebDriver().getCurrentUrl());
    }

    /**
     * Ввод логин/пароля
     */
    @Пусть("^пользователь \"([^\"]*)\" ввел логин и пароль$")
    public void loginByUserData(String userCode) {
        String login = loadProperty(userCode + ".login");
        String password = loadProperty(userCode + ".password");
        cleanField("Логин");
        alfaScenario.getCurrentPage().getElement("Логин").sendKeys(login);
        cleanField("Пароль");
        alfaScenario.getCurrentPage().getElement("Пароль").sendKeys(password);
        alfaScenario.getCurrentPage().getElement("Войти").click();
    }

    /**
     * Выполнено наведение курсора на элемент
     */
    @Когда("^выполнен ховер на (?:поле|элемент) \"([^\"]*)\"$")
    public void elementHover(String elementName) {
        SelenideElement field = alfaScenario.getCurrentPage().getElement(elementName);
        field.hover();
    }

    /**
     * Проверка, что элемент не отображается на странице
     */
    @Тогда("^(?:поле|блок|форма|выпадающий список|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementIsNotVisible(String elementName) {
        alfaScenario.getCurrentPage().waitElementsUntil(
                not(Condition.appear), DEFAULT_TIMEOUT, alfaScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        SelenideElement element = alfaScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] не кликабелен", elementName), element.isEnabled());
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением
     */
    @Тогда("^элемент \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        SelenideElement currentElement = alfaScenario.getCurrentPage().getElement(elementName);
        String currentAtrValue = currentElement.attr(attribute);
        assertThat(String.format("Элемент [%s] не содержит атрибут [%s] со значением [%s]", elementName, attribute, expectedAttributeValue)
                , currentAtrValue, equalToIgnoringCase(expectedAttributeValue));
    }

    @И("^совершен переход в конец страницы$")
    public void scrollDown() {
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).build().perform();
        actions.keyUp(Keys.CONTROL).perform();
    }

    /**
     * Проверка, что значение в поле содержит значение, указанное в шаге
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" содержит значение \"(.*)\"$")
    public void testActualValueContainsSubstring(String elementName, String expectedValue) {
        String actualValue = alfaScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Поле [%s] не содержит значение [%s]", elementName, expectedValue), actualValue, containsString(expectedValue));
    }

    /**
     * Проверка, что значение в поле равно значению, указанному в шаге
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" равно \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        String actualValue = alfaScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Значения поля [%s] не равно ожидаемому [%s]", elementName, expectedValue), expectedValue, equalTo(actualValue));
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @Тогда("^(?:ссылка|кнопка) \"([^\"]*)\" недоступна для нажатия$")
    public void buttonIsNotActive(String elementName) {
        SelenideElement element = alfaScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] кликабелен", elementName), element.is(Condition.disabled));
    }

    /**
     * Проверка, что поле нередактируемо
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        SelenideElement element = alfaScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] доступен для редактирования", elementName), element.is(Condition.disabled));
    }

    /**
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" со страницы совпадает со списком \"([^\"]*)\"$")
    public void compareListFromUIAndFromVariable(String listName, String listVariable) {
        HashSet<String> expectedList = new HashSet<>((List<String>) alfaScenario.getVar(listVariable));
        HashSet<String> actualList = new HashSet<>(alfaScenario.getCurrentPage().getAnyElementsListTexts(listName));
        assertThat(String.format("Список со страницы [%s] не совпадает с ожидаемым списком из переменной [%s]", listName, listVariable), expectedList, equalTo(actualList));
    }

    /**
     * Проверка, что на странице не отображаются редактируемые элементы, такие как:
     * -input
     * -textarea
     */
    @Тогда("^открыта read-only форма$")
    public void openReadOnlyForm() {
        int inputsCount = getDisplayedElementsByCss("input").size();
        assertTrue("Форма не read-only. Количество input-полей: " + inputsCount, inputsCount == 0);
        int textareasCount = getDisplayedElementsByCss("textarea").size();
        assertTrue("Форма не read-only. Количество элементов textarea: " + textareasCount, textareasCount == 0);
    }

    private List<SelenideElement> getDisplayedElementsByCss(String cssSelector) {
        return $$(cssSelector).stream()
                .filter(SelenideElement::isDisplayed)
                .collect(Collectors.toList());
    }

    /**
     * Добавление строки в поле к уже заполненой строке
     */
    @Когда("^в элемент \"([^\"]*)\" дописывается значение \"(.*)\"$")
    public void addValue(String elementName, String value) {
        SelenideElement field = alfaScenario.getCurrentPage().getElement(elementName);
        String oldValue = field.getValue();
        if (oldValue.isEmpty()) {
            oldValue = field.getText();
        }
        field.setValue("");
        field.setValue(oldValue + value);
    }

    /**
     * Нажатие на элемент по его тексту
     */
    @И("^выполнено нажатие на элемент с текстом \"(.*)\"$")
    public void findElement(String text) {
        $(By.xpath("//*[text()='" + text + "']")).click();
    }

    /**
     * Ввод в поле текущую дату в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     */
    @Когда("^элемент \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"&")
    public void currentDate(String fieldName, String dateFormat) {
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            log.error("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }
        SelenideElement valueInput = alfaScenario.getCurrentPage().getElement(fieldName);
        valueInput.setValue("");
        valueInput.setValue(currentStringDate);
    }

    /**
     * Ввод в поле указанного текста используя буфер обмена и клавиши SHIFT + INSERT
     */
    @Когда("^вставлено значение \"([^\"]*)\" в элемент \"([^\"]*)\" с помощью горячих клавиш$")
    public void pasteValueToTextField(String value, String fieldName) {
        ClipboardOwner clipboardOwner = (clipboard, contents) -> {
        };
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(value);
        clipboard.setContents(stringSelection, clipboardOwner);
        alfaScenario.getCurrentPage().getElement(fieldName).sendKeys(Keys.chord(Keys.SHIFT, Keys.INSERT));
    }

    /**
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содежранию ожидаемого текста в названияя файла. Можно передавать регулярки
     * После выполнения проверки файл удаляется
     */
    @Тогда("^файл \"(.*)\" загрузился в паку /Downloads$")
    public void testFileDownloaded(String fileName) {
        File downloads = getDownloadsDir();
        File[] expectedFiles = downloads.listFiles((files, file) -> file.contains(fileName));
        assertNotNull("Ошибка поиска файла", expectedFiles);
        assertFalse("Файл не загрузился", expectedFiles.length == 0);
        assertTrue(String.format("В папке присутствуют более одного файла с одинаковым назанием содержащим текст [%s]", fileName),
                expectedFiles.length == 1);
        deleteFiles(expectedFiles);
    }

    private File getDownloadsDir() {
        String homeDir = System.getProperty("user.home");
        return new File(homeDir + "/Downloads");
    }

    private void deleteFiles(File[] filesToDelete) {
        for (File file : filesToDelete) {
            file.delete();
        }
    }

}
