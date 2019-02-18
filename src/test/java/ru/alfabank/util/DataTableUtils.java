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
package ru.alfabank.util;

import cucumber.api.DataTable;
import gherkin.pickles.PickleCell;
import gherkin.pickles.PickleRow;
import gherkin.pickles.PickleTable;

import java.util.ArrayList;
import java.util.List;

public class DataTableUtils {
    public static DataTable dataTableFromLists(List<List<String>> lists) {
        List<PickleRow> rows = new ArrayList<>();
        lists.forEach(list -> {
            List<PickleCell> cells = new ArrayList<>();
            list.forEach(string -> {
                cells.add(new PickleCell(null,string));
            });
            rows.add(new PickleRow(cells));
        });
        return new DataTable(new PickleTable(rows), null);
    }
}
