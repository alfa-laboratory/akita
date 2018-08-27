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
package ru.alfabank;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.support.FindBy;
import ru.alfabank.alfatest.cucumber.annotations.Name;
import ru.alfabank.alfatest.cucumber.annotations.Optional;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;

import java.util.List;

@Getter
@Name("AkitaPageMock")
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

    @FindBy(xpath = "//ul[@id=\"list2\"]/li")
    @Name("List2")
    public List<SelenideElement> list2;

    @FindBy(xpath = "//ul[@id=\"list3\"]/li")
    @Name("List3")
    public List<SelenideElement> list3;

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

    @FindBy(id = "innerText1")
    @Name("innerTextP")
    public SelenideElement innerTextP;

    @FindBy(className = "searchBlock")
    @Name("SearchBlock")
    public SearchFieldMock searchField;

    @Optional
    @FindBy(xpath = "//span[text()='Показать ещё']")
    @Name("Кнопка Показать ещё")
    public SelenideElement buttonShowMore;

    @FindBy(xpath = "//*[text()='Подписать и отправить']/parent::button")
    @Name("Кнопка Подписать и отправить")
    public SelenideElement signAndSendToBankButton;

    @FindBy(name = "mixedButton")
    @Name("Кнопка с англо-русским названием")
    public SelenideElement mixLangButton;
}