package ru.alfabank;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import ru.alfabank.alfatest.cucumber.annotations.Name;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;

@Name("SearchBlock")
public class SearchFieldMock extends AkitaPage {

    @FindBy(name = "searchInput")
    @Name("SearchInput")
    private SelenideElement searchInput;

    @FindBy(name = "searchButton")
    @Name("SearchButton")
    private SelenideElement submitButton;
}
