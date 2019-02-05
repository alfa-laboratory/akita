/**
 * Copyright 2017 Alfa Laboratory
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank.tests.core.formatters;

import cucumber.api.TestStep;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestStepFinished;
import cucumber.api.formatter.Formatter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import ru.alfabank.alfatest.cucumber.annotations.Screenshot;
import ru.alfabank.alfatest.cucumber.api.AkitaScenario;
import ru.alfabank.alfatest.cucumber.api.AnnotationScanner;
import ru.alfabank.alfatest.cucumber.utils.Reflection;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * При подключении StepFormatter к проеку с тестами, становится достуна опция снятия скриншотов
 * после каждого шага. Для этого необходимо задать системную переменную takeScreenshotAfterSteps=true
 * Скриншот так же будет сниматься после каждого метода, помеченного аннотацией @Screenshot
 */
@Slf4j
public class StepFormatter implements Formatter {
    public final String SCREENSHOT_AFTER_STEPS = "takeScreenshotAfterSteps";

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepFinished.class, getTestStepFinishedHandler());
    }

    private EventHandler<TestStepFinished> getTestStepFinishedHandler() {
        return this::handleTestStepFinished;
    }

    private void handleTestStepFinished(TestStepFinished event) {
        if (!event.testStep.isHook()) {
            afterStep(event.testStep);
        }
    }

    /**
     * Метод осуществляет снятие скришота и прикрепление его к cucumber отчету.
     * Скриншот снимается после шагов, помеченных аннотацией @Screenshot,
     * либо после каждого шага, если задана системная переменная takeScreenshotAfterSteps=true
     * @param testStep - текущий шаг
     *
     */
    private void afterStep(TestStep testStep) {
        String fullMethodLocation = testStep.getCodeLocation();
        String currentMethodName = fullMethodLocation.substring(fullMethodLocation.indexOf('.') + 1, fullMethodLocation.indexOf('('));

        List<Method> methodsWithScreenshotAnnotation = new Reflections(new MethodAnnotationsScanner())
            .getMethodsAnnotatedWith(Screenshot.class)
            .stream()
            .filter(m -> m.getName().contains(currentMethodName))
            .collect(Collectors.toList());

        boolean isScreenshotAnnotationPresent = methodsWithScreenshotAnnotation.size() > 0;

        boolean isTakeScreenshotAfterStepsProperty = System.getProperty(SCREENSHOT_AFTER_STEPS) != null
            ? Boolean.valueOf(System.getProperty(SCREENSHOT_AFTER_STEPS)) : false;

        if (isScreenshotAnnotationPresent || isTakeScreenshotAfterStepsProperty) {
            final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
            AkitaScenario.getInstance().getScenario().embed(screenshot, "image/png");
        }
    }
}
