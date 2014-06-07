/*
 * Sentilo
 * 
 * Copyright (C) 2013 Institut Municipal d’Informàtica, Ajuntament de Barcelona.
 * 
 * This program is licensed and may be used, modified and redistributed under the terms of the
 * European Public License (EUPL), either version 1.1 or (at your option) any later version as soon
 * as they are approved by the European Commission.
 * 
 * Alternatively, you may redistribute and/or modify this program under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * 
 * See the licenses for the specific language governing permissions, limitations and more details.
 * 
 * You should have received a copy of the EUPL1.1 and the LGPLv3 licenses along with this program;
 * if not, you may find them at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl http://www.gnu.org/licenses/ and
 * https://www.gnu.org/licenses/lgpl.txt
 */
package org.sentilo.web.catalog.search.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.sentilo.web.catalog.search.SearchFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

public class DefaultSearchFilterBuilderImpl implements SearchFilterBuilder {

  public static final String ID_COLUMN = "_id";
  public static final String NAME_COLUMN = "name";
  public static final String DESC_COLUMN = "description";
  public static final String CREATED_COLUMN = "createdAt";
  public static final String TYPE_COLUMN = "type";
  public static final String SENSORID_COLUMN = "sensorId";
  public static final String PROVIDERID_COLUMN = "providerId";
  public static final String MOBILE_COLUMN = "mobile";
  public static final String USER_NAME_COLUMN = "userName";
  public static final String EMAIL_COLUMN = "email";
  public static final String TARGET_COLUMN = "target";
  
     
  public DefaultSearchFilterBuilderImpl() {
    registerDataTableColumns();
  }

  @Override
  public SearchFilter buildSearchFilter(final HttpServletRequest request, final Pageable pageable, final String wordToSearch) {
    if (StringUtils.hasText(wordToSearch)) {
      final Map<String, Object> params = buildSearchParams(request, wordToSearch);
      return new SearchFilter(params, pageable);
    }
    return new SearchFilter(pageable);
  }

  protected Map<String, Object> buildSearchParams(final HttpServletRequest request, final String wordToSearch) {
    final Map<String, Object> searchParams = new HashMap<String, Object>();
    final List<Column> listColumns = SearchFilterUtils.getListColumns(request);

    for (final Column column : listColumns) {
      if (column.isSearchable()) {
        searchParams.put(column.getName(), wordToSearch);
      }
    }

    return searchParams;
  }

  private void registerDataTableColumns() {
    registerAlertDataTableColumns();
    registerProviderDataTableColumns();
    registerApplicationDataTableColumns();
    registerSensorDataTableColumns();
    registerComponentDataTableColumns();
    registerUserDataTableColumns();
    registerPermissionsDataTableColumns();
    registerSensorTypesDataTableColumns();
    registerComponentTypesDataTableColumns();
  }

  private void registerAlertDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(ID_COLUMN, true, true));
    columns.add(new Column(NAME_COLUMN, true, true));
    columns.add(new Column(TYPE_COLUMN, true, true));
    columns.add(new Column(CREATED_COLUMN, true));

    SearchFilterUtils.addListColumns("alert", columns);
  }

  private void registerProviderDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(ID_COLUMN, true, true));
    columns.add(new Column(NAME_COLUMN, true, true));
    columns.add(new Column(DESC_COLUMN, true, true));
    columns.add(new Column(CREATED_COLUMN, true));

    SearchFilterUtils.addListColumns("provider", columns);
  }

  private void registerApplicationDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(ID_COLUMN, true, true));
    columns.add(new Column(NAME_COLUMN, true, true));
    columns.add(new Column(DESC_COLUMN, true, true));
    columns.add(new Column(CREATED_COLUMN, true));

    SearchFilterUtils.addListColumns("application", columns);
  }

  private void registerSensorDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(SENSORID_COLUMN, true, true));
    columns.add(new Column(PROVIDERID_COLUMN, true, true));
    columns.add(new Column(TYPE_COLUMN, true));
    columns.add(new Column(CREATED_COLUMN, true));

    SearchFilterUtils.addListColumns("sensor", columns);
  }

  private void registerComponentDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(NAME_COLUMN, true, true));
    columns.add(new Column(DESC_COLUMN, true, true));
    columns.add(new Column(PROVIDERID_COLUMN, true, true));
    columns.add(new Column(MOBILE_COLUMN, true, true));
    columns.add(new Column(CREATED_COLUMN, true));

    SearchFilterUtils.addListColumns("component", columns);
  }

  private void registerUserDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(USER_NAME_COLUMN, true, true));
    columns.add(new Column(NAME_COLUMN, true, true));
    columns.add(new Column(EMAIL_COLUMN, true, true));
    columns.add(new Column(CREATED_COLUMN, true));

    SearchFilterUtils.addListColumns("users", columns);
  }

  private void registerPermissionsDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(TARGET_COLUMN, true, true));
    columns.add(new Column(TYPE_COLUMN, true));

    SearchFilterUtils.addListColumns("permissions", columns);
  }

  private void registerSensorTypesDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(ID_COLUMN, true, true));
    columns.add(new Column(NAME_COLUMN, true, true));
    columns.add(new Column(DESC_COLUMN, true, true));
    columns.add(new Column(CREATED_COLUMN, true));

    SearchFilterUtils.addListColumns("sensortypes", columns);
  }

  private void registerComponentTypesDataTableColumns() {
    final List<Column> columns = new ArrayList<Column>();
    columns.add(new Column(ID_COLUMN, true, true));
    columns.add(new Column(NAME_COLUMN, true, true));
    columns.add(new Column(DESC_COLUMN, true, true));
    columns.add(new Column(CREATED_COLUMN, true));

    SearchFilterUtils.addListColumns("componenttypes", columns);
  }

}
