package ru.alfabank.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.ru.*;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import ru.alfabank.alfatest.cucumber.api.TestScenario;

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
import static ru.alfabank.steps.DefaultApiSteps.resolveVars;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadPropertyInt;

/**
 * В testScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
 * Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
 * наследующем TestPage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
 * можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
 * не в степах, в степах - взаимодействие по русскому названию элемента.
 */
@Slf4j
public class DefaultSteps {

    @Delegate
    TestScenario testScenario = TestScenario.getInstance();

    private static final int DEFAULT_TIMEOUT = loadPropertyInt("waitingCustomElementsTimeout", 10000);

    /**
     * Значение заданной переменной из application.properties сохраняется в переменную в testScenario
     * для дальнейшего использования
     */
    @И("^сохранено значение \"([^\"]*)\" из property файла в переменную \"([^\"]*)\"$")
    public void saveValueToVar(String propertyVariableName, String variableName) {
        testScenario.setVar(variableName, loadProperty(propertyVariableName));
    }

    /**
     * Выполняется обновление страницы
     */
    @И("^выполнено обновление текущей страницы$")
    public void refreshPage() {
        getWebDriver().navigate().refresh();
    }

    /**
     * Выполняется переход по заданной ссылке,
     * при этом все ключи переменных в фигурных скобках
     * меняются на их значения из хранилища testScenario
     */
    @Когда("^совершен переход по ссылке \"([^\"]*)\"$")
    public void goToUrl(String address) {
        String url = replaceVariables(address);
        getWebDriver().get(url);
        testScenario.write("Url = " + url);
    }

    /**
     * Проверка, что текущий URL совпадает с ожидаемым
     */
    @Тогда("^текущий URL равен \"([^\"]*)\"$")
    public void checkCurrentURL(String url) {
        String currentUrl = getWebDriver().getCurrentUrl();
        String expectedUrl = replaceVariables(url);
        assertThat("Текущий URL не совпадает с ожидаемым", currentUrl, is(expectedUrl));
    }

    /**
     * На странице происходит клик по заданному элементу
     */
    @И("^выполнено нажатие на (?:кнопку|поле|блок) \"([^\"]*)\"$")
    public void clickOnElement(String elementName) {
        testScenario.getCurrentPage().getElement(elementName).click();
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     */
    @Тогда("^элемент \"([^\"]*)\" отображается на странице$")
    public void elemIsPresentedOnPage(String elementName) {
        testScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, testScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка появления элемента(не списка) на странице в течение
     * заданного количества секунд
     */
    @Тогда("^элемент \"([^\"]*)\" отобразился на странице в течение (\\d+) (?:секунд|секунды)")
    public void testElementAppeared(String elementName, int seconds) {
        testScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, seconds * 1000, testScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка появления списка на странице в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     */
    @Тогда("^список \"([^\"]*)\" отображается на странице$")
    public void listIsPresentedOnPage(String elementName) {
        testScenario.getCurrentPage().waitElementsUntil(
                Condition.appear, DEFAULT_TIMEOUT, testScenario.getCurrentPage().getElementsList(elementName)
        );
    }

    /**
     * Проверка того, что элемент исчезнет со страницы (станет невидимым) в течение DEFAULT_TIMEOUT.
     * В случае, если свойство "waitingCustomElementsTimeout" в application.properties не задано,
     * таймаут равен 10 секундам
     */
    @Тогда("^ожидается исчезновение элемента \"([^\"]*)\"")
    public void elemDisappered(String elementName) {
        testScenario.getCurrentPage().waitElementsUntil(
                Condition.disappears, DEFAULT_TIMEOUT, testScenario.getCurrentPage().getElement(elementName));
    }

    /**
     * Проверка того, что все элементы, которые описаны в классе страницы с аннотацией @Name,
     * но без аннотации @Optional появились на странице
     * в течение WAITING_APPEAR_TIMEOUT, которое равно значению свойства "waitingAppearTimeout"
     * из application.properties. Если свойство не найдено, время таймаута равно 8 секундам
     */
    @Тогда("^(?:страница|блок|форма|вкладка) \"([^\"]*)\" (?:загрузилась|загрузился)$")
    public void loadPage(String nameOfPage) {
        testScenario.setCurrentPage(testScenario.getPage(nameOfPage));
        testScenario.getCurrentPage().appeared();
    }

    /**
     * Устанавливается значение переменной в хранилище переменных. Один из кейсов: установка login пользователя
     */
    @И("^установлено значение переменной \"([^\"]*)\" равным \"(.*)\"$")
    public void setVariable(String variableName, String value) {
        testScenario.setVar(variableName, value);
    }

    /**
     * Проверка равенства двух переменных из хранилища
     */
    @Тогда("^значения в переменных \"([^\"]*)\" и \"([^\"]*)\" совпадают$")
    public void compareTwoVariables(String firstVariableName, String secondVariableName) {
        String firstValueToCompare = getVar(firstVariableName).toString();
        String secondValueToCompare = getVar(secondVariableName).toString();
        assertThat(String.format("Значения в переменных [%s] и [%s] не совпадают", firstVariableName, secondVariableName),
                firstValueToCompare, equalTo(secondValueToCompare));
    }

    /**
     * Проверка того, что значение из поля совпадает со значением заданной переменной из хранилища
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" совпадает со значением из переменной \"([^\"]*)\"$")
    public void compareFieldAndVariable(String elementName, String variableName) {
        String actualValue = testScenario.getCurrentPage().getAnyElementText(elementName);
        String expectedValue = testScenario.getVar(variableName).toString();
        assertThat(String.format("Значение поля [%s] не совпадает со значением из переменной [%s]", elementName, variableName),
                expectedValue, equalTo(actualValue));
    }

    /**
     * Проверка того, что значение из поля содержится в списке,
     * полученном из хранилища переменных по заданному ключу
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список из переменной \"([^\"]*)\" содержит значение (?:поля|элемента) \"([^\"]*)\" $")
    public void checkIfListContainsValueFromField(String elementName, String variableListName) {
        String actualValue = testScenario.getCurrentPage().getAnyElementText(elementName);
        List<String> listFromVariable = ((List<String>) testScenario.getVar(variableListName));
        assertTrue(String.format("Список из переменной [%s] не содержит значение поля [%s]", elementName, variableListName),
                listFromVariable.contains(actualValue));
    }

    /**
     * Выполняется переход по заданной ссылке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @Deprecated
    @И("^совершен переход на страницу \"([^\"]*)\" по (?:ссылке|ссылке из property файла) = \"([^\"]*)\"$")
    public void goToSelectedPageByLinkFromProperty(String pageName, String urlName) {
        urlName = loadProperty(urlName, resolveVars(urlName));
        testScenario.write(" url = " + urlName);
        WebDriverRunner.getWebDriver().get(urlName);
        loadPage(pageName);
    }

    /**
     * Выполняется переход по заданной ссылке.
     * Шаг содержит проверку, что после перехода загружена заданная страница.
     * Ссылка может передаваться как строка, так и как ключ из application.properties
     */
    @И("^совершен переход на страницу \"([^\"]*)\" по (?:ссылке|ссылке из property файла) \"([^\"]*)\"$")
    public void goToSelectedPageByLinkFromPropertyFile(String pageName, String urlOrName) {
        String address = loadProperty(urlOrName, resolveVars(urlOrName));
        testScenario.write(" url = " + address);
        WebDriverRunner.getWebDriver().get(address);
        loadPage(pageName);
    }

    /**
     * Ожидание в течение заданного количества секунд
     */
    @Когда("^выполнено ожидание в течение (\\d+) (?:секунд|секунды)")
    public void waitForSeconds(long seconds) {
        sleep(1000 * seconds);
    }

    /**
     * Проверка того, что блок исчез/стал невидимым
     */
    @Тогда("^(?:страница|блок|форма) \"([^\"]*)\" (?:скрыт|скрыта)")
    public void blockDisappeared(String nameOfPage) {
        testScenario.getPage(nameOfPage).disappeared();
    }

    /**
     * Эмулирует нажатие клавиш на клавиатуре
     */
    @И("^выполнено нажатие на клавиатуре \"([^\"]*)\"$")
    public void pushButtonOnKeyboard(String buttonName) {
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        WebDriverRunner.getWebDriver().switchTo().activeElement().sendKeys(key);
    }

    /**
     * Устанавливается значение в заданное поле. Перед использованием поле нужно очистить
     */
    @Когда("^в поле \"([^\"]*)\" введено значение \"(.*)\"$")
    public void setFieldValue(String elementName, String value) {
        SelenideElement valueInput = testScenario.getCurrentPage().getElement(elementName);
        valueInput.setValue(String.valueOf(value));
    }

    /**
     * Очищается заданное поле
     */
    @Когда("^очищено поле \"([^\"]*)\"$")
    public void cleanField(String fieldName) {
        SelenideElement valueInput = testScenario.getCurrentPage().getElement(fieldName);
        valueInput.click();
        valueInput.clear();
        valueInput.setValue("");
        valueInput.doubleClick().sendKeys(Keys.DELETE);
    }

    /**
     * Проверка, что поле для ввода пусто
     */
    @Тогда("^поле \"([^\"]*)\" пусто$")
    public void fieldInputIsEmpty(String fieldName) {
        assertThat(String.format("Поле [%s] не пусто", fieldName),
                testScenario.getCurrentPage().getAnyElementText(fieldName),
                isEmptyOrNullString());
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
     * Проверка, что список со страницы состоит только из элементов,
     * перечисленных в таблице
     */
    @Тогда("^список \"([^\"]*)\" состоит из элементов из таблицы$")
    public void checkIfListConsistsOfTableElements(String listName, List<String> textTable) {
        List<String> actualValues = testScenario.getCurrentPage().getAnyElementsListTexts(listName);
        int numberOfTypes = actualValues.size();
        assertThat(String.format("Количество элементов в списке [%s] не соответсвует ожиданию", listName), textTable, hasSize(numberOfTypes));
        assertTrue(String.format("Значения элементов в списке [%s] не совпадают с ожидаемыми значениями из таблицы", listName), actualValues.containsAll(textTable));
    }

    /**
     * Выбор из списка со страницы элемента с заданным значением
     */
    @Тогда("^в списке \"([^\"]*)\" выбран элемент с (?:текстом|значением) \"(.*)\"$")
    public void checkIfSelectedListElementMatchesValue(String listName, String value) {
        List<SelenideElement> listOfTypeFromPage = testScenario.getCurrentPage().getElementsList(listName);
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
        testScenario.setVar(variableName, testScenario.getCurrentPage().getAnyElementText(elementName));
    }

    /**
     * Проверка выражения на истинность
     * Например, string1.equals(string2)
     * OR string.equals("string")
     * Любое Java-выражение, возвращающие boolean
     */
    @Тогда("^верно, что \"([^\"]*)\"$")
    public void expressionExpression(String expression) {
        testScenario.getVars().evaluate("assert(" + expression + ")");
    }

    /**
     * Переход на страницу по клику и проверка, что страница загружена
     */
    @И("^выполнен переход на страницу \"([^\"]*)\" после нажатия на (?:ссылку|кнопку) \"([^\"]*)\"$")
    public void urlClickAndCheckRedirection(String pageName, String elementName) {
        testScenario.getCurrentPage().getElement(elementName).click();
        loadPage(pageName);
        testScenario.write(" url = " + WebDriverRunner.getWebDriver().getCurrentUrl());
    }

    /**
     * Шаг авторизации.
     * Для того, чтобы шаг работал, на текущей странице должны быть указаны элементы
     * со значениями аннотации @Name:
     * "Логин" - для поля ввода логина,
     * "Пароль" - для поля ввода пароля и
     * "Войти" - для кнопки входа.
     * Также должны быть указаны логин и пароль в файле application.properties.
     * Например для шага: "Пусть пользователь user ввел логин и пароль"
     * логин и пароль должны быть указаны со следующими ключами:
     * user.login - для логина и
     * user.password - для пароля
     */
    @Пусть("^пользователь \"([^\"]*)\" ввел логин и пароль$")
    public void loginByUserData(String userCode) {
        String login = loadProperty(userCode + ".login");
        String password = loadProperty(userCode + ".password");
        cleanField("Логин");
        testScenario.getCurrentPage().getElement("Логин").sendKeys(login);
        cleanField("Пароль");
        testScenario.getCurrentPage().getElement("Пароль").sendKeys(password);
        testScenario.getCurrentPage().getElement("Войти").click();
    }

    /**
     * Выполняется наведение курсора на элемент
     */
    @Когда("^выполнен ховер на (?:поле|элемент) \"([^\"]*)\"$")
    public void elementHover(String elementName) {
        SelenideElement field = testScenario.getCurrentPage().getElement(elementName);
        field.hover();
    }

    /**
     * Проверка того, что элемент не отображается на странице
     */
    @Тогда("^(?:поле|блок|форма|выпадающий список|элемент) \"([^\"]*)\" не отображается на странице$")
    public void elementIsNotVisible(String elementName) {
        testScenario.getCurrentPage().waitElementsUntil(
                not(Condition.appear), DEFAULT_TIMEOUT, testScenario.getCurrentPage().getElement(elementName)
        );
    }

    /**
     * Проверка, что элемент на странице кликабелен
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" кликабельно$")
    public void clickableField(String elementName) {
        SelenideElement element = testScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] не кликабелен", elementName), element.isEnabled());
    }

    /**
     * Проверка, что у элемента есть атрибут с ожидаемым значением
     */
    @Тогда("^элемент \"([^\"]*)\" содержит атрибут \"([^\"]*)\" со значением \"(.*)\"$")
    public void checkElemContainsAtrWithValue(String elementName, String attribute, String expectedAttributeValue) {
        SelenideElement currentElement = testScenario.getCurrentPage().getElement(elementName);
        String currentAtrValue = currentElement.attr(attribute);
        assertThat(String.format("Элемент [%s] не содержит атрибут [%s] со значением [%s]", elementName, attribute, expectedAttributeValue)
                , currentAtrValue, equalToIgnoringCase(expectedAttributeValue));
    }

    /**
     * Выполняется переход в конец страницы
     */
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
        String actualValue = testScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Поле [%s] не содержит значение [%s]", elementName, expectedValue), actualValue, containsString(expectedValue));
    }

    /**
     * Проверка, что значение в поле равно значению, указанному в шаге
     */
    @Тогда("^значение (?:поля|элемента) \"([^\"]*)\" равно \"(.*)\"$")
    public void compareValInFieldAndFromStep(String elementName, String expectedValue) {
        String actualValue = testScenario.getCurrentPage().getAnyElementText(elementName);
        assertThat(String.format("Значение поля [%s] не равно ожидаемому [%s]", elementName, expectedValue), expectedValue, equalTo(actualValue));
    }

    /**
     * Проверка, что кнопка/ссылка недоступна для нажатия
     */
    @Тогда("^(?:ссылка|кнопка) \"([^\"]*)\" недоступна для нажатия$")
    public void buttonIsNotActive(String elementName) {
        SelenideElement element = testScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] кликабелен", elementName), element.is(Condition.disabled));
    }

    /**
     * Проверка, что поле нередактируемо
     */
    @Тогда("^(?:поле|элемент) \"([^\"]*)\" (?:недоступно|недоступен) для редактирования$")
    public void fieldIsDisable(String elementName) {
        SelenideElement element = testScenario.getCurrentPage().getElement(elementName);
        assertTrue(String.format("Элемент [%s] доступен для редактирования", elementName), element.is(Condition.disabled));
    }

    /**
     * Проверка, что список со страницы совпадает со списком из переменной
     * без учёта порядка элементов
     */
    @SuppressWarnings("unchecked")
    @Тогда("^список \"([^\"]*)\" со страницы совпадает со списком \"([^\"]*)\"$")
    public void compareListFromUIAndFromVariable(String listName, String listVariable) {
        HashSet<String> expectedList = new HashSet<>((List<String>) testScenario.getVar(listVariable));
        HashSet<String> actualList = new HashSet<>(testScenario.getCurrentPage().getAnyElementsListTexts(listName));
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
        SelenideElement field = testScenario.getCurrentPage().getElement(elementName);
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
     * Ввод в поле текущей даты в заданном формате
     * При неверном формате, используется dd.MM.yyyy
     */
    @Когда("^элемент \"([^\"]*)\" заполняется текущей датой в формате \"([^\"]*)\"$")
    public void currentDate(String fieldName, String dateFormat) {
        long date = System.currentTimeMillis();
        String currentStringDate;
        try {
            currentStringDate = new SimpleDateFormat(dateFormat).format(date);
        } catch (IllegalArgumentException ex) {
            currentStringDate = new SimpleDateFormat("dd.MM.yyyy").format(date);
            log.error("Неверный формат даты. Будет использоваться значание по умолчанию в формате dd.MM.yyyy");
        }
        SelenideElement valueInput = testScenario.getCurrentPage().getElement(fieldName);
        valueInput.setValue("");
        valueInput.setValue(currentStringDate);
    }

    /**
     * Ввод в поле указанного текста, используя буфер обмена и клавиши SHIFT + INSERT
     */
    @Когда("^вставлено значение \"([^\"]*)\" в элемент \"([^\"]*)\" с помощью горячих клавиш$")
    public void pasteValueToTextField(String value, String fieldName) {
        ClipboardOwner clipboardOwner = (clipboard, contents) -> {
        };
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(value);
        clipboard.setContents(stringSelection, clipboardOwner);
        testScenario.getCurrentPage().getElement(fieldName).sendKeys(Keys.chord(Keys.SHIFT, Keys.INSERT));
    }

    /**
     * Выполняется поиск нужного файла в папке /Downloads
     * Поиск осуществляется по содержанию ожидаемого текста в названии файла. Можно передавать регулярное выражение.
     * После выполнения проверки файл удаляется
     */
    @Тогда("^файл \"(.*)\" загрузился в паку /Downloads$")
    public void testFileDownloaded(String fileName) {
        File downloads = getDownloadsDir();
        File[] expectedFiles = downloads.listFiles((files, file) -> file.contains(fileName));
        assertNotNull("Ошибка поиска файла", expectedFiles);
        assertFalse("Файл не загрузился", expectedFiles.length == 0);
        assertTrue(String.format("В папке присутствуют более одного файла с одинаковым назанием, содержащим текст [%s]", fileName),
                expectedFiles.length == 1);
        deleteFiles(expectedFiles);
    }


    /**
     * Возвращает каталог "Downloads" в домашней директории
     *
     * @return
     */
    private File getDownloadsDir() {
        String homeDir = System.getProperty("user.home");
        return new File(homeDir + "/Downloads");
    }

    /**
     * Удаляет файлы, переданные в метод
     *
     * @param filesToDelete массив файлов
     */
    private void deleteFiles(File[] filesToDelete) {
        for (File file : filesToDelete) {
            file.delete();
        }
    }

}
