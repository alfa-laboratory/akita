package ru.alfabank.core;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.tests.core.rest.FilterByParameterValue;
import ru.alfabank.tests.core.rest.OperationType;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Created by alexander on 16.08.17.
 */
public class FilterByParameterValueTests {
    private static FilterByParameterValue filterByParameterValue;

    @BeforeClass
    public static void init() throws Exception {
        //filterByParameterValue = new FilterByParameterValue("testName", "testValue", "==");
    }

    @Test
    public void getNameOfParameterTest() {
        //assertThat(filterByParameterValue.getNameOfParameter(), equalTo("testName"));
    }

    @Test
    public void getValueTest() {
        //assertThat(filterByParameterValue.getValue(), equalTo("testValue"));
    }
}
