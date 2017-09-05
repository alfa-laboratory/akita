package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.*;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.tests.core.rest.RequestParam;
import ru.alfabank.tests.core.rest.RequestParamType;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static ru.alfabank.tests.core.helpers.PropertyLoader.loadProperty;

public class ApiStepsTest {

    private static DefaultApiSteps api;
    private static AlfaScenario alfaScenario;

    @BeforeClass
    public static void setup() {
        alfaScenario = AlfaScenario.getInstance();
        api = new DefaultApiSteps();
        alfaScenario.setEnvironment(new AlfaEnvironment(new StubScenario()));
    }

    @AfterClass
    public static void close() {
        WebDriverRunner.closeWebDriver();
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void getURLwithPathParamsCalculatedSimple() {
        assertThat(DefaultApiSteps.resolveVars("alfabank.ru"),
                equalTo("alfabank.ru"));
    }

    @Test
    public void sendHttpRequestGET() throws java.lang.Exception {
        stubFor(get(urlEqualTo("/get/resource"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("TEST_BODY")));
        api.sendHttpRequest("GET", "/get/resource", "RESPONSE_GET_BODY");
        assertThat(alfaScenario.getVar("RESPONSE_GET_BODY"), equalTo("TEST_BODY"));
    }

    @Test
    public void sendHttpRequestPost() throws java.lang.Exception {
        stubFor(post(urlEqualTo("/post/resource"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("TEST_BODY")));
        api.sendHttpRequest("POST", "/post/resource", "RESPONSE_POST_BODY");
        assertThat(alfaScenario.getVar("RESPONSE_POST_BODY"), equalTo("TEST_BODY"));
    }

    @Test
    public void sendHttpRequestSaveResponseTest() throws java.lang.Exception {
        stubFor(post(urlEqualTo("/post/saveWithTable"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("TEST_BODY_1")));
        List<RequestParam> paramTable = new ArrayList<>();
        RequestParam requestParamHeader = RequestParam.builder()
                .type(RequestParamType.HEADER)
                .name("Accept")
                .value("text/plain")
                .build();
        RequestParam requestParamBody = RequestParam.builder()
                .type(RequestParamType.BODY)
                .name("TEST_BODY_PARAM")
                .value("TEST")
                .build();
        paramTable.add(requestParamHeader);
        paramTable.add(requestParamBody);
        api.sendHttpRequestSaveResponse("POST", "/post/saveWithTable",
                "TEST_HTTP", paramTable);
        assertThat(alfaScenario.getVar("TEST_HTTP"), equalTo("TEST_BODY_1"));
    }

    @Test
    public void checkResponseCodeTest() throws java.lang.Exception {
        stubFor(get(urlEqualTo("/get/responseWithTable?param=test"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "text/xml")));
        List<RequestParam> paramTable = new ArrayList<>();
        RequestParam requestParam = RequestParam.builder()
                .type(RequestParamType.PARAMETER)
                .name("param")
                .value("test")
                .build();
        paramTable.add(requestParam);
        api.checkResponseCode("GET", "/get/responseWithTable",
                404, paramTable);
    }

}