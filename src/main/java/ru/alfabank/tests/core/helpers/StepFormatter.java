package ru.alfabank.tests.core.helpers;

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

@Slf4j
public class StepFormatter implements Formatter {
    public final String SCREENSHOT_AFTER_STEPS = "akita.stepScreenshot";

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


    private void afterStep(TestStep testStep) {
        String method = testStep.getCodeLocation();
        String currentMethodName = method.substring(method.indexOf('.') + 1, method.indexOf('('));
        log.info("methodName = " + currentMethodName);

        Reflections reflections = new Reflections(new MethodAnnotationsScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Screenshot.class);
        log.info("methods = " + methods.toString());

        List<Method> methodsWithAnnotation = methods.stream()
            .filter(m -> m.getName().contains(currentMethodName))
            .collect(Collectors.toList());

        log.info("methodsWithAnnotation = " + methodsWithAnnotation.toString());
        boolean isScreenshotAnnotationPresent = methodsWithAnnotation.size() > 0;

        log.info("isScreenshotAnnotationPresent " + isScreenshotAnnotationPresent);

        boolean stepScreenshots = System.getProperty(SCREENSHOT_AFTER_STEPS) == null
            ? Boolean.valueOf(System.getProperty(SCREENSHOT_AFTER_STEPS)) : false;

        log.info("stepScreenshots " + stepScreenshots);

        if (isScreenshotAnnotationPresent || stepScreenshots) {
            final byte[] screenshot = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
            AkitaScenario.getInstance().getScenario().embed(screenshot, "image/png");
        }
    }
}
