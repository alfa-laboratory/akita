package ru.alfabank.steps;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;
import ru.alfabank.alfatest.cucumber.api.AlfaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.tests.core.rest.RequestParam;

import java.util.ArrayList;

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
        alfaScenario.setEnvironment(new AlfaEnvironment());
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void getURLwithPathParamsCalculatedSimple() {
        assertThat(DefaultApiSteps.getURLwithPathParamsCalculated("alfabank.ru"),
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
}