package ru.alfabank.steps;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Тогда;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.tests.core.rest.RequestParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

@Slf4j
public class DefaultApiSteps {

    @Delegate
    AlfaScenario alfaScenario = AlfaScenario.getInstance();

    @И("^вызван \"([^\"]*)\" c URL \"([^\"]*)\", headers и parameters из таблицы. Полученный ответ сохранен в переменную \"([^\"]*)\"$")
    public void sendRequest(String typeOfRequest, String urlName, String variableName, List<RequestParam> table) throws Exception {
        urlName = getURLwithPathParamsCalculated(urlName);
        RequestSender request = createRequestByParamsTable(table);
        Response response = request.request(Method.valueOf(typeOfRequest), urlName);
        getResponseAndSaveToVariable(request, variableName, response);
    }

    @И("^вызван \"([^\"]*)\" c URL \"([^\"]*)\", headers и parameters из таблицы. Ожидается код ответа: (\\d+)$")
    public void checkStatusCode(String typeOfRequest, String urlName, int expectedStatusCode, List<RequestParam> table) throws Exception {
        urlName = getURLwithPathParamsCalculated(urlName);
        RequestSender request = createRequestByParamsTable(table);
        Response response = request.request(Method.valueOf(typeOfRequest), urlName);
        assertThat("статус код совпал с ожидаемым", expectedStatusCode, equalTo(response.getStatusCode()));
    }


    @Тогда("^поле \"([^\"]*)\" ответа \"([^\"]*)\" совпадает с$")
    public void checkExpectedFieldApi(String field, String apiResponse, String expectedFieldValue) throws Throwable {
        JsonParser parser = new JsonParser();
        String jsonResponse = getVar(apiResponse).toString();
        String realFieldValue = parser.parse(jsonResponse).getAsJsonObject().get(field).toString();
        assertThat("Значение поля API совпадает с ожидаемым",
                realFieldValue,
                equalTo(expectedFieldValue));
    }

    private RequestSender createRequestByParamsTable(List<RequestParam> table) {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        String body = null;
        Gson gson = new Gson();
        for (RequestParam requestParam : table) {
            switch (requestParam.getType()) {
                case PARAMETER:
                    parameters.put(requestParam.getName(), requestParam.getValue());
                    break;
                case HEADER:
                    headers.put(requestParam.getName(), requestParam.getValue());
                    break;
                case BODY:
                    String path = String.join(File.separator, "src", "main", "java", "restBodies", requestParam.getValue());
                    try(FileReader fileReader = new FileReader(path)) {
                        JsonElement json = gson.fromJson(fileReader, JsonElement.class);
                        body = gson.toJson(json);
                    } catch (FileNotFoundException e) {
                        body = requestParam.getValue();
                    }
                    break;
                default:
                    throw new RuntimeException("Некорректно задан элемент таблицы : " + requestParam.getType());
            }
        }
        RequestSender request;
        if (body != null) {
            alfaScenario.write("Тело запроса:\n" + body);
            request = given()
                    .contentType(ContentType.JSON)
                    .headers(headers)
                    .params(parameters)
                    .body(body)
                    .when();
        } else {
            request = given()
                    .headers(headers)
                    .params(parameters)
                    .when();
        }
        return request;
    }

    private void getResponseAndSaveToVariable(RequestSender request, String variableName, Response response) {
        if (response.statusCode() == 200) {
            alfaScenario.setVar(variableName, response.getBody().asString());
            alfaScenario.write("Тело ответа : \n" + response.getBody().asString());
        } else {
            fail("Некорректный ответ на запрос: " + response.getBody().asString());
        }
    }

    private Response makePostRequestWithBody(Map<String, String> headers, String jsonBody, Method methodType, String apiUrl) {
        RequestSpecification requestSender = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when();

        if (headers != null) requestSender = requestSender.headers(headers);
        return requestSender.request(methodType, apiUrl);
    }

    static String getURLwithPathParamsCalculated(String urlName) {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(urlName);
        String newString = "";
        while (m.find()) {
            String varName = m.group(1);
            String value = AlfaScenario.getInstance().getVar(varName).toString();
            newString = m.replaceFirst(value);
            m = p.matcher(newString);
        }
        if (newString.isEmpty()) {
            newString = urlName;
        }
        return newString;
    }

}