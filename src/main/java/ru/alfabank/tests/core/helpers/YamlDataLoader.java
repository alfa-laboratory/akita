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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

public class YamlDataLoader {

    /**
     * Объединить две JsonNode.
     * При совпадении имен ключей значения newNode перезапишут значения oldNode.
     *
     * @param oldNode old node
     * @param newNode new node
     *
     * @return jsonNode
     *
     */
    public static JsonNode mergeNodes(JsonNode oldNode, JsonNode newNode) {

        Iterator<String> fieldNames = newNode.fieldNames();
        while (fieldNames.hasNext()) {

            String fieldName = fieldNames.next();
            JsonNode jsonNode = oldNode.get(fieldName);
            // if field exists and is an embedded object
            if (jsonNode != null && jsonNode.isObject()) {
                mergeNodes(jsonNode, newNode.get(fieldName));
            }
            else {
                if (oldNode instanceof ObjectNode) {
                    JsonNode value = newNode.get(fieldName);
                    ((ObjectNode) oldNode).set(fieldName, value);
                }
            }

        }

        return oldNode;
    }

    /**
     * Прочитать данные yaml-файла и десериализовать в экземпляр переданного класса.
     * Пример использования:
     *  Settings settings = DataLoader.readDataToType("data/settings.yml", Settings.class);
     *
     * @param resourcePath относительный путь к yaml-файлу ресурсу
     * @param targetClass Java класс для десериализации
     *
     * @return Pojo class instance
     *
     */
    @SneakyThrows
    static public <T> T readDataToClassType(String resourcePath, Class<T> targetClass) {
        return readDataToClassType(resourcePath, targetClass, "");
    }

    /**
     * Прочитать данные yaml-файла по ключу root и десериализовать в экземпляр переданного класса.
     * Пример использования:
     *  Settings settings = DataLoader.readDataToType("data/settings.yml", Settings.class);
     *
     * @param resourcePath относительный путь к yaml-файлу ресурсу
     * @param targetClass Java класс для десериализации
     * @param root имя корневого элемента верхнего уровня в yaml-файле
     *
     * @return Pojo class instance
     *
     */
    @SneakyThrows
    static public <T> T readDataToClassType(String resourcePath, Class<T> targetClass, String root) {
        JsonNode data = readDataToJsonNode(resourcePath, root);
        return jsonNodeToClassType(data, targetClass);
    }

    /**
     * Прочитать данные yaml файла в JsonNode
     *
     * @param resourcePath относительный путь к yaml-файлу ресурсу
     *
     * @return jsonNode
     *
     */
    static public JsonNode readDataToJsonNode(String resourcePath) {
        return readDataToJsonNode(resourcePath, "");
    }

    /**
     * Прочитать данные yaml файла по ключу root в JsonNode
     *
     * @param resourcePath относительный путь к yaml-файлу ресурсу
     * @param root имя корневого элемента верхнего уровня в yaml-файле
     *
     * @return jsonNode
     *
     */
    @SneakyThrows
    static public JsonNode readDataToJsonNode(String resourcePath, String root) {
        Yaml yaml = new Yaml();
        Object data = yaml.load(new FileReader(ResourceHelper.getFile(resourcePath)));
        if (!root.isEmpty()) {
            if (!(data instanceof Map<?,?>)) {
                throw new Exception(
                        String.format("Yaml root level file structure must be a Map to get '%s' element", root)
                );
            }
            data = ((Map) data).get(root);
        }
        return objectToJsonNode(data);
    }



    /**
     * Конвертировать JsonNode в экземпляр переданного класса.
     *
     * @param jsonNode
     * @param targetClass Java класс для десериализации
     *
     * @return Pojo class instance
     *
     */
    @SneakyThrows
    static public <T> T jsonNodeToClassType(JsonNode jsonNode, Class<T> targetClass) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.treeToValue(jsonNode, targetClass);
    }

    static protected JsonNode objectToJsonNode(Object obj) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.valueToTree(obj);
    }
}
