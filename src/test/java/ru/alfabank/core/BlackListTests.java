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
package ru.alfabank.core;


import net.lightbody.bmp.proxy.BlacklistEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alfabank.tests.core.helpers.BlackList;
import ru.alfabank.tests.core.helpers.BlackListManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

public class BlackListTests {
    private static List<BlacklistEntry> defaultBlacklistEntries = new ArrayList<>();

    @Test
    void testGetDefaultBlacklistEntries() {
        BlackList blackList = new BlackList();
        assertThat(defaultBlacklistEntries, samePropertyValuesAs(blackList.getBlacklistEntries()));
    }

    @Test
    void testAddToDefaultBlacklistEntries() {
        List<BlacklistEntry> expectedEntries = new ArrayList<>();
        expectedEntries.addAll(defaultBlacklistEntries);

        List<BlacklistEntry> newEntries = new ArrayList<>(Arrays.asList(new BlacklistEntry("new.entry", 404)));
        expectedEntries.addAll(newEntries);

        BlackList blackList = new BlackList();
        blackList.addToDefaultBlacklistEntries(newEntries);
        assertThat(expectedEntries, samePropertyValuesAs(blackList.getBlacklistEntries()));
    }

    @Test
    void testNewBlacklistEntries() {
        List<BlacklistEntry> newEntries = new ArrayList<>(Arrays.asList(new BlacklistEntry("new.entry", 404)));
        BlackList blackList = new BlackList(newEntries);
        assertThat(newEntries, samePropertyValuesAs(blackList.getBlacklistEntries()));
    }

    @BeforeEach
    void initBlackList() {
        new BlackListManager("blacklist").fillBlackList(defaultBlacklistEntries);
    }
}
