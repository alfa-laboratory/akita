package ru.alfabank.other;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import ru.alfabank.entities.BankInfo;
import ru.alfabank.tests.core.helpers.YamlDataLoader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlDataLoaderTest {

    @Test
    void readDataToJsonNodePositive() {
        JsonNode data = YamlDataLoader.readDataToJsonNode("data/testData.yml", "измененный запрос");
        String inn = data.at("/clientInfo/inn").asText();
        assertEquals(inn, "1234567890", "Итоговый INN не равен '1234567890'");
    }

    @Test
    void readDataToClassTypePositive() {
        BankInfo bankInfo = YamlDataLoader.readDataToClassType("data/testData.yml", BankInfo.class, "alfabank");
        assertEquals(bankInfo.getDetails().get("bik"), "044525593", "Итоговый BIK не равен '044525593'");
    }

    @Test
    void margeJsonNodesPositive() {
        JsonNode commonSettings = YamlDataLoader.readDataToJsonNode("data/testData.yml", "settings");
        JsonNode devSettings = YamlDataLoader.readDataToJsonNode("data/testData_dev.yml", "settings");
        devSettings = YamlDataLoader.mergeNodes(commonSettings, devSettings);
        String name = devSettings.at("/environment/name").asText();
        String user = devSettings.at("/environment/user").asText();

        assertAll(
            () -> assertEquals(name, "dev", "/environment/name не равен 'dev'"),
            () -> assertEquals(user, "tester", "/environment/user не равен 'tester'")
        );

    }
}
