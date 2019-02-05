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
package ru.alfabank.alfatest.cucumber.api;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.Selenide;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Предназначен для хранения страниц, используемых при прогоне тестов
 */
public final class Pages {

    /**
     * Страницы, на которых будет производится тестирование < Имя, Страница >
     */
    private Map<String, AkitaPage> pages;

    /**
     * Страница, на которой в текущий момент производится тестирование
     */
    private AkitaPage currentPage;

    public Pages() {
        pages = Maps.newHashMap();
    }


    /**
     * Возвращает текущую страницу, на которой в текущий момент производится тестирование
     */
    public AkitaPage getCurrentPage() {
        if (currentPage == null) throw new IllegalStateException("Текущая страница не задана");
        return currentPage.initialize();
    }

    /**
     * Задает текущую страницу по ее имени
     */
    public void setCurrentPage(AkitaPage page) {
        this.currentPage = page;
    }

    /**
     * Реализация анонимных методов со страницей в качестве аргумента
     *
     * @param clazz                   класс страницы
     * @param checkIfElementsAppeared проверка всех не помеченных "@Optional" элементов
     */
    public static <T extends AkitaPage> void withPage(Class<T> clazz, boolean checkIfElementsAppeared, Consumer<T> consumer) {
        T page = getPage(clazz, checkIfElementsAppeared);
        consumer.accept(page);
    }

    /**
     * Получение страницы из "pages" по имени
     */
    public AkitaPage get(String pageName) {
        return Selenide.page(getPageFromPagesByName(pageName)).initialize();
    }

    /**
     * Получение страницы по классу
     */
    @SuppressWarnings("unchecked")
    public <T extends AkitaPage> T get(Class<T> clazz, String name) {
        AkitaPage page = Selenide.page(getPageFromPagesByName(name)).initialize();

        if (!clazz.isInstance(page)) {
            throw new IllegalStateException(name + " page is not a instance of " + clazz + ". Named page is a " + page);
        }
        return (T) page;
    }

    private Map<String, AkitaPage> getPageMapInstanceInternal() {
        return pages;
    }

    private AkitaPage getPageFromPagesByName(String pageName) throws IllegalArgumentException {
        AkitaPage page = getPageMapInstanceInternal().get(pageName);
        if (page == null)
            throw new IllegalArgumentException(pageName + " page is not declared in a list of available pages");
        return page;
    }

    /**
     * Добавление инстанциированной страницы в "pages" с проверкой на NULL
     */
    public <T extends AkitaPage> void put(String pageName, T page) throws IllegalArgumentException {
        if (page == null)
            throw new IllegalArgumentException("Была передана пустая страница");
        pages.put(pageName, page);
    }

    /**
     * Получение страницы по классу с возможностью выполнить проверку элементов страницы
     */
    public static <T extends AkitaPage> T getPage(Class<T> clazz, boolean checkIfElementsAppeared) {
        T page = Selenide.page(clazz);
        if (checkIfElementsAppeared) {
            page.initialize().isAppeared();
        }
        return page;
    }

    /**
     * Добавление страницы в "pages" по классу
     */
    @SneakyThrows
    public void put(String pageName, Class<? extends AkitaPage> page) {
        if (page == null)
            throw new IllegalArgumentException("Была передана пустая страница");
        Constructor<? extends AkitaPage> constructor = page.getDeclaredConstructor();
        constructor.setAccessible(true);
        AkitaPage p = page.newInstance();
        pages.put(pageName, p);
    }
}
