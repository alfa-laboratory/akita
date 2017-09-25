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
package ru.alfabank.other;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.alfabank.AkitaPageMock;
import ru.alfabank.alfatest.cucumber.api.AkitaPage;
import ru.alfabank.alfatest.cucumber.api.Pages;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by alexander on 02.08.17.
 */
public class PagesTest {
    private static Pages pages;
    private static AkitaPageMock alfaPageMock;

    @BeforeClass
    public static void init() {
        pages = new Pages();
        alfaPageMock = new AkitaPageMock();
    }

    @Test
    public void getSetCurrentPagePositive() {
        pages.setCurrentPage(alfaPageMock);
        assertThat(pages.getCurrentPage(), equalTo(alfaPageMock));
    }

    @Test(expected = IllegalStateException.class)
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
        AkitaPage nullPage = null;
        pages.put("Test", nullPage);
    }

    @Test
    public void getNegative() {
        assertThat(pages.get("WRONG_KEY"), is(nullValue()));
    }
}
