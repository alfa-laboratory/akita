/*
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

public class BlackListTests {
    private static final List<BlacklistEntry> DEFAULT_BLACKLIST_ENTRIES = new ArrayList<>();

    @Test
    void testGetDefaultBlacklistEntries() {
        BlackList blackList = new BlackList();
        assertThat(DEFAULT_BLACKLIST_ENTRIES, samePropertyValuesAs(blackList.getBlacklistEntries()));
    }

    @Test
    void testAddToDefaultBlacklistEntries() {
        List<BlacklistEntry> expectedEntries = new ArrayList<>(DEFAULT_BLACKLIST_ENTRIES);

        List<BlacklistEntry> newEntries = new ArrayList<>(Collections.singletonList(new BlacklistEntry("new.entry", 404)));
        expectedEntries.addAll(newEntries);

        BlackList blackList = new BlackList();
        blackList.addToDefaultBlacklistEntries(newEntries);
        assertThat(expectedEntries, samePropertyValuesAs(blackList.getBlacklistEntries()));
    }

    @Test
    void testNewBlacklistEntries() {
        List<BlacklistEntry> newEntries = new ArrayList<>(Collections.singletonList(new BlacklistEntry("new.entry", 404)));
        BlackList blackList = new BlackList(newEntries);
        assertThat(newEntries, samePropertyValuesAs(blackList.getBlacklistEntries()));
    }

    @BeforeEach
    void initBlackList() {
        new BlackListManager("blacklist").fillBlackList(DEFAULT_BLACKLIST_ENTRIES);
    }
}
