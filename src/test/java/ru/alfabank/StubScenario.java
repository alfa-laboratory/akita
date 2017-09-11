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
