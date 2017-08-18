package ru.alfabank.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.alfabank.tests.core.rest.JsonMapper.*;
/**
 * Created by alexander on 16.08.17.
 */
public class JsonMapperTests {
    private static JsonObject jsonObject = new JsonObject();

    @BeforeClass
    public static void init() {

    }

    @Test
    public void getStringPositive()
    {
        jsonObject.addProperty("testString", "testValue");
        assertThat(getString(jsonObject, "testString"), equalTo("testValue"));
    }

    @Test
    public void getStringObjectNull() {
        String nullString = null;
        jsonObject.addProperty("testNull", nullString);
        assertThat(getString(jsonObject, "testNull"), equalTo(""));
    }

    @Test
    public void getStringJsonNull() {
        jsonObject.add("jsonNull", JsonNull.INSTANCE);
        assertThat(getString(jsonObject, "jsonNull"), equalTo(""));
    }

    @Test
    public void getBigDecimalPositive()
    {
        BigDecimal bigDecimal = new BigDecimal(1223243434.23);
        jsonObject.addProperty("jsonBigDecimal", bigDecimal);
        assertThat(getBigDecimal(jsonObject, "jsonBigDecimal"), equalTo(bigDecimal));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getBigDecimalNegative() {
        String nullString = null;
        jsonObject.addProperty("testNull", nullString);
        getBigDecimal(jsonObject, "testNull");
    }

    @Test
    public void getBooleanPositive()
    {
        jsonObject.addProperty("testBool", true);
        assertThat(getBoolean(jsonObject, "testBool"), equalTo(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getBooleanNegative() {
        String nullString = null;
        jsonObject.addProperty("testNull", nullString);
        getBoolean(jsonObject, "testNull");
    }

    @Test
    public void getIntPositive()
    {
        jsonObject.addProperty("testInt", 101);
        assertThat(getInt(jsonObject, "testInt"), equalTo(101));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getIntNegative() {
        String nullString = null;
        jsonObject.addProperty("testNull", nullString);
        getInt(jsonObject, "testNull");
    }

    @Test
    public void getJsonObjectPositive()
    {
        JsonObject node = new JsonObject();
        node.addProperty("field1", "test1");
        node.addProperty("field2", "test2");
        jsonObject.add("node", node);
        assertThat(getJsonObject(jsonObject, "node"), equalTo(node));
    }

    @Test(expected = IllegalStateException.class)
    public void getJsonObjectNegative() {
        JsonObject node = null;
        jsonObject.add("node", node);
        getJsonObject(jsonObject, "node");
    }

    @Test
    public void getJsonArrayFromStringPositive()
    {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add("testValue");
        jsonArray.add(1123);
        jsonArray.add(false);
        jsonObject.add("array", jsonArray);
        assertThat(getJsonArrayFromString(jsonObject.toString(), "array"), equalTo(jsonArray));
    }
}
