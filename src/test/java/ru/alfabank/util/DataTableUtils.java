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
package ru.alfabank.util;

import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.DataTableTypeRegistry;
import io.cucumber.datatable.DataTableTypeRegistryTableConverter;

import java.util.List;
import java.util.Locale;


public class DataTableUtils {

    private final DataTableTypeRegistry registry = new DataTableTypeRegistry(Locale.ENGLISH);
    private final DataTable.TableConverter tableConverter = new DataTableTypeRegistryTableConverter(registry);

    public DataTable dataTableFromLists(List<List<String>> lists) {
        return DataTable.create(lists, tableConverter);
    }

}
