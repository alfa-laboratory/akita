package ru.alfabank.core;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.tests.core.rest.RequestParam;
import ru.alfabank.tests.core.rest.RequestParamType;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;


/**
 * Created by alexander on 16.08.17.
 */
public class RequestParamTests {
    private static RequestParam requestParam;

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
        assertThat(requestParam.getType().equals(RequestParamType.HEADER), equalTo(true));
    }
}
