package ru.alfabank.core;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.tests.core.rest.RequestParam;
import ru.alfabank.tests.core.rest.RequestParamType;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertTrue;


/**
 * Created by alexander on 16.08.17.
 */
public class RequestParamTests {
    private static RequestParam requestParam;
    private static RequestParam requestParamForCompare;

    @BeforeClass
    public static void init() {
        requestParam = RequestParam.builder()
                .name("test")
                .value("testValue")
                .type(RequestParamType.HEADER)
                .build();
    }

    @Test
    public void getNameTest() {
        requestParam.setType("hEaDer");
        assertThat(requestParam.getType(), equalTo(RequestParamType.HEADER));
    }

    @Test
    public void equalsTest() {
        RequestParam requestParamForCompare = RequestParam.builder()
                .name("test")
                .value("testValue")
                .type(RequestParamType.HEADER)
                .build();
        assertTrue(requestParam.equals(requestParamForCompare));
    }

    @Test
    public void hashTest() {
        RequestParam requestParamForCompare = RequestParam.builder()
                .name("test")
                .value("testValue")
                .type(RequestParamType.HEADER)
                .build();
        int hashToCompare = requestParamForCompare.hashCode();
        assertThat(requestParam.hashCode(), equalTo(hashToCompare));
    }

    @Test
    public void toStringTest() {
        RequestParam requestParamForCompare = RequestParam.builder()
                .name("test")
                .value("testValue")
                .type(RequestParamType.HEADER)
                .build();
        String stringToCompare = requestParamForCompare.toString();
        assertThat(requestParam.toString(), equalTo(stringToCompare));
    }
}
