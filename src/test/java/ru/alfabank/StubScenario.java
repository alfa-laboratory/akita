/**
 * Copyright 2017 Alfa Laboratory
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.alfabank;

import cucumber.api.Scenario;

import java.util.Collection;

public class StubScenario implements Scenario {
    @Override
    public Collection<String> getSourceTagNames() {
        return null;
    }

    @Override
    public String getStatus() {
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
        return null;
    }

    @Override
    public String getId() {
        return null;
    }
}
