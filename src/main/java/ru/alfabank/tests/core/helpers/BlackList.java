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
package ru.alfabank.tests.core.helpers;

import net.lightbody.bmp.proxy.BlacklistEntry;

import java.util.ArrayList;
import java.util.List;

public class BlackList {
    private List<BlacklistEntry> blacklistEntries = new ArrayList<>();

    private BlackListManager blackListManager = new BlackListManager("blacklist");

    public BlackList() {
        setDefaultBlacklistEntries();
    }

    public BlackList(List<BlacklistEntry> newEntries) {
        setNewBlacklistEntries(newEntries);
    }

    /**
     * Список регулярных выражений соответствующих URL, которые добавляются в Blacklist по умолчанию
     */
    private void setDefaultBlacklistEntries() {
        blackListManager.fillBlackList(blacklistEntries);
    }

    /**
     * очищает дефолтный сисок URL из Blacklist и задает новый Blacklist
     * @param newEntries - новый список URL для доавления в Blacklist
     */
    private void setNewBlacklistEntries(List<BlacklistEntry> newEntries) {
        blacklistEntries.clear();
        blacklistEntries.addAll(newEntries);
    }

    /**
     *
     * @return - спискок URL, которые находятся в Blacklist
     */
    public List<BlacklistEntry> getBlacklistEntries() {
        return blacklistEntries;
    }


    /**
     * добавляет новые URL к дефолтному Blacklist
     *
     * @param newEntries - новые URL, которые будут добавлены  в Blacklist
     */
    public void addToDefaultBlacklistEntries(List<BlacklistEntry> newEntries) {
        blacklistEntries.addAll(newEntries);
    }
}
