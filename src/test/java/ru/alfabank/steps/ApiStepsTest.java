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
package ru.alfabank.steps;

import com.codeborne.selenide.WebDriverRunner;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import ru.alfabank.StubScenario;
import ru.alfabank.alfatest.cucumber.api.AkitaEnvironment;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.tests.core.rest.RequestParam;
import ru.alfabank.tests.core.rest.RequestParamType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.alfabank.tests.core.rest.RequestParamType.PARAMETER;

public class ApiStepsTest {

    private static DefaultApiSteps api;
    private static AkitaScenario akitaScenario;

    @BeforeClass
    public static void setup() {
        akitaScenario = AkitaScenario.getInstance();
        api = new DefaultApiSteps();
        akitaScenario.setEnvironment(new AkitaEnvironment(new StubScenario()));
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
        api.sendHttpRequestWithoutParams("GET", "/get/resource", "RESPONSE_GET_BODY");
        assertThat(akitaScenario.getVar("RESPONSE_GET_BODY"), equalTo("TEST_BODY"));
    }

    @Test
    public void sendHttpRequestPost() throws java.lang.Exception {
        stubFor(post(urlEqualTo("/post/resource"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("TEST_BODY")));
        api.sendHttpRequestWithoutParams("POST", "/post/resource", "RESPONSE_POST_BODY");
        assertThat(akitaScenario.getVar("RESPONSE_POST_BODY"), equalTo("TEST_BODY"));
    }

    @Test
    public void sendHttpRequestWithVarsPost() throws java.lang.Exception {
        String body = "testBodyValue";
        String bodyVarName = "testBodyName";
        akitaScenario.setVar(bodyVarName, body);

        stubFor(post(urlEqualTo("/post/resource"))
                .withRequestBody(WireMock.equalTo(body))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("TEST_BODY")));

        List<RequestParam> params = Collections.singletonList(
                RequestParam.builder()
                        .name("body")
                        .type(RequestParamType.BODY)
                        .value("{" + bodyVarName + "}")
                        .build());

        api.sendHttpRequestSaveResponse("POST", "/post/resource", "RESPONSE_POST_BODY", params);
        assertThat(akitaScenario.getVar("RESPONSE_POST_BODY"), equalTo("TEST_BODY"));
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
        assertThat(akitaScenario.getVar("TEST_HTTP"), equalTo("TEST_BODY_1"));
    }

    @Test
    public void checkResponseCodeTest() throws java.lang.Exception {
        stubFor(get(urlEqualTo("/get/responseWithTable?param=test"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "text/xml")));
        List<RequestParam> paramTable = new ArrayList<>();
        RequestParam requestParam = RequestParam.builder()
                .type(PARAMETER)
                .name("param")
                .value("test")
                .build();
        paramTable.add(requestParam);
        api.checkResponseCode("GET", "/get/responseWithTable",
                404, paramTable);
    }

    @Test
    public void shouldCreateRequestForEqualsParamNamesTest() throws Exception{
        stubFor(get(urlEqualTo("/get/responseWithTable?param=first&param=second"))
            .willReturn(aResponse()
            .withStatus(200)));
        List<RequestParam> paramTable = asList(
                new RequestParam(PARAMETER, "param", "first"),
                new RequestParam(PARAMETER, "param", "second")
        );
        api.checkResponseCode("GET", "/get/responseWithTable", 200, paramTable);
    }

    @Test
    public void shouldSendPutRequest() throws Exception {
        stubFor(put(urlEqualTo("/put/someInfo"))
            .willReturn(aResponse()
            .withStatus(205)));
        api.checkResponseCode("PUT", "/put/someInfo", 205, new ArrayList<>());
    }
}