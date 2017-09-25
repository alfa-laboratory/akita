package ru.alfabank.loadPropertyTests;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

public class CustomPropertyLoaderTest {
    private static AkitaScenario akitaScenario = AkitaScenario.getInstance();

   @Before
    public void prepare() {
        akitaScenario.setEnvironment(new AkitaEnvironment());
    }

    @AfterClass
    public static void close() { WebDriverRunner.closeWebDriver(); }

    @Test
    public void customPropertyFile() {
        System.setProperty("profile", "customProperties");
        System.out.println(System.getProperty("profile"));
        assertThat(loadProperty("testVar"), equalTo("customPropertiesTestValue"));
    }
}
