package ru.alfabank.other;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.Class;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.alfabank.alfatest.cucumber.utils.Reflection.extractFieldValue;

/**
 * Created by alexander on 03.08.17.
 */
public class ReflectionTest {

    public class MockClass {
        public String mockField;

        MockClass() {
            mockField = "123";
        }

        public String getMockField() {
            return mockField;
        }
    }

    @Test(expected = NullPointerException.class)
    public void extractFieldValueNegative() {
        MockClass mockClass = new MockClass();
        Field field = null;
        extractFieldValue(field, mockClass);
    }

    @Test
    public void extractFieldValuePositive() throws NoSuchFieldException {
        MockClass mockClass = new MockClass();
        Class reflectClass = mockClass.getClass();
        Field field = reflectClass.getField("mockField");
        assertThat(extractFieldValue(field, mockClass), equalTo(mockClass.getMockField()) );
    }
}
