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
package ru.alfabank;

import cucumber.api.Result;
import cucumber.api.Scenario;

import java.util.Collection;
import java.util.List;

public class StubScenario implements Scenario {
    @Override
    public Collection<String> getSourceTagNames() {
        return null;
    }

    @Override
    public Result.Type getStatus() {
        return null;
    }


    @Override
    public boolean isFailed() {
        return false;
    }

    @Override
    public void embed(byte[] data, String mimeType) {

    }

    @Override
    public void write(String text) {

    }

    @Override
    public String getName() {
        return "My scenario";
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public List<Integer> getLines() {
        return null;
    }
}
