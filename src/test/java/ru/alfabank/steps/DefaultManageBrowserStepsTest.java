package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.*;
import org.openqa.selenium.Cookie;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.containsString;


public class DefaultManageBrowserStepsTest {

    private static DefaultApiSteps api;
    private static DefaultSteps ds;
    private static AkitaScenario akitaScenario;
    private static DefaultManageBrowserSteps dmbs;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        api = new DefaultApiSteps();
        ds = new DefaultSteps();
        akitaScenario.setEnvironment(new AkitaEnvironment(new StubScenario()));
        dmbs = new DefaultManageBrowserSteps();
        ds.goToUrl("https://www.google.ru/");
    }

//    @Before
//    public void prepare() {
//        ds.goToSelectedPageByLinkFromPropertyFile("AkitaPageMock", akitaScenario.getVar("Page").toString());
//    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void deleteCookiesTest() {
        assertThat(WebDriverRunner.getWebDriver().manage().getCookies().isEmpty(), equalTo(false));
        dmbs.deleteCookies();
        assertThat(WebDriverRunner.getWebDriver().manage().getCookies().isEmpty(), equalTo(true));
    }

    @Test
    public void saveCookieToVarTest() {
        WebDriverRunner.getWebDriver().manage().addCookie(new Cookie("cookieName", "123"));
        dmbs.saveCookieToVar("cookieName", "varName");
        assertThat(akitaScenario.getVar("varName").toString(), containsString("123"));
    }

    @Test
    public void saveAllCookiesTest(){
        dmbs.saveAllCookies("var2");
        assertThat(akitaScenario.getVar("var2").toString().isEmpty(), equalTo(false));
    }

    @Test
    public void replaceCookieTest() {
        WebDriverRunner.getWebDriver().manage().deleteAllCookies();
        dmbs.replaceCookie("testName", "12qwe");
        assertThat(WebDriverRunner.getWebDriver().manage().getCookieNamed("testName").getValue(), equalTo("12qwe"));
    }
}
