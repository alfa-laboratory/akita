package ru.alfabank;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;

import java.util.List;

@Getter
@AkitaPage.Name("AkitaPageMock")
public class AkitaPageMock extends AkitaPage {
    @FindBy(id = "mockId")
    @Name("mockId")
    public SelenideElement mockId;

    @FindBy(css = ".mockCss")
    @Name("mockCss")
    public SelenideElement mockCss;

    @FindBy(xpath = "//div[@class='mockXpath']")
    @Name("mockXpath")
    public SelenideElement mockXpath;

    @FindBy(name = "mockName")
    @Name("mockName")
    public SelenideElement mockName;

    @FindBy(tagName = "p")
    @Name("mockTagName")
    public SelenideElement mockTagName;

    @FindBy(xpath = "//p[text()='Serious testing page']")
    @Name("mockXpathText")
    public SelenideElement mockXpathText;

    @FindBy(xpath = "//ul[@id=\"list\"]/li")
    @Name("List")
    public List<SelenideElement> list;

    @FindBy(name = "goodButton")
    @Name("GoodButton")
    public SelenideElement goodButton;

    @FindBy(name = "SUPERBUTTON")
    @Name("SUPERBUTTON")
    public SelenideElement SUPERBUTTON;

    @Optional
    @FindBy(id = "permDisabledBtn")
    @Name("DisabledButton")
    public SelenideElement disabledButton;

    @Optional
    @FindBy(id = "hiddenDiv")
    @Name("HiddenDiv")
    public SelenideElement hiddenDiv;

    @Optional
    @FindBy(name = "disabledField")
    @Name("DisabledField")
    public SelenideElement disabledField;

    @FindBy(name = "normalField")
    @Name("NormalField")
    public SelenideElement normalField;

    @FindBy(name = "fieldWithText")
    @Name("TextField")
    public SelenideElement textField;

    @FindBy(id = "link")
    @Name("Link")
    public SelenideElement link;

    @FindBy(id = "login")
    @Name("Логин")
    public SelenideElement login;

    @FindBy(id = "psw")
    @Name("Пароль")
    public SelenideElement psw;

    @FindBy(id = "submit")
    @Name("Войти")
    public SelenideElement submit;

    @FindBy(id = "ul")
    @Name("ul")
    public SelenideElement ul;
}
