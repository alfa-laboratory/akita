package ru.alfabank.other;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.AlfaPageMock;
import ru.alfabank.alfatest.cucumber.api.AlfaPage;
import ru.alfabank.alfatest.cucumber.api.AlfaScenario;
import ru.alfabank.alfatest.cucumber.api.Pages;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by alexander on 02.08.17.
 */
public class PagesTest {
    private static Pages pages;
    private static AlfaPageMock alfaPageMock;

    @BeforeClass
    public static void init() {
        pages = new Pages();
        alfaPageMock = new AlfaPageMock();
    }

    @Test
    public void getSetCurrentPagePositive() {
        pages.setCurrentPage(alfaPageMock);
        assertThat(pages.getCurrentPage(), equalTo(alfaPageMock));
    }

    @Test(expected = AssertionError.class)
    public void setCurrentPageNegative() {
        pages.setCurrentPage(null);
        pages.getCurrentPage();
    }

    @Test
    public void getPutPositive() {
        pages.put("Test", alfaPageMock);
        assertThat(pages.get("Test"), equalTo(alfaPageMock));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNegative() {
        AlfaPage nullPage = null;
        pages.put("Test", nullPage);
        pages.get("Test");
    }

    @Test
    public void getNegative() {
        assertThat(pages.get("WRONG_KEY"), is(nullValue()));
    }
}
