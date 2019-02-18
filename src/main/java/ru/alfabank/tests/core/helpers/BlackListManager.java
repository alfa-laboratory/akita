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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.proxy.BlacklistEntry;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class BlackListManager {
    /**
     * Производится парсинг строки из файла blacklist на наличие ссылок типа:
     * .*ru.fp.kaspersky-labs.com.*
     * http://google.com/ 200
     * При необходимости можно указывать статус код, по умолчанию будет присвоен 404
     * @param blacklistEntries - список ссылок и статус кодов
     */
    private String fileName;

    public BlackListManager(String blacklist) {
        this.fileName = blacklist;
    }

    public void fillBlackList(List<BlacklistEntry> blacklistEntries) {
        String file = getResource();
        Pattern pattern = Pattern.compile("((https?:\\/\\/)?([\\da-z\\.\\*-]+)\\.([a-z\\.]{2,6})([\\/\\w\\.\\*-]*)*\\/?)\\s?(\\d{3})*");
        Matcher matcher = pattern.matcher(file);
        while (matcher.find()) {
            if (matcher.group(6) == null)
                blacklistEntries.add(new BlacklistEntry(matcher.group(1), 404));
            else blacklistEntries.add(new BlacklistEntry(matcher.group(1), Integer.parseInt(matcher.group(6))));
        }
    }

    @SneakyThrows({IOException.class, URISyntaxException.class})
    private String getResource() {
        ClassLoader classLoader = getClass().getClassLoader();
        byte[] file = new byte[0];
        try {
            Path path = Paths.get(classLoader.getResource(fileName).toURI());
            if(Files.exists(path)) {
                file = Files.readAllBytes(path);
                return new String(file, "UTF-8");
            }
            else log.warn("Файла '" + fileName + "' - не существует\n");
        } catch (NullPointerException ne) {
            log.warn("Файла '" + fileName + "' - не существует\n");
        }
        return new String(file);
    }
}
