/*
 * Sentilo
 *  
 * Original version 1.4 Copyright (C) 2013 Institut Municipal d’Informàtica, Ajuntament de Barcelona.
 * Modified by Opentrends adding support for multitenant deployments and SaaS. Modifications on version 1.5 Copyright (C) 2015 Opentrends Solucions i Sistemes, S.L.
 * 
 *   
 * This program is licensed and may be used, modified and redistributed under the
 * terms  of the European Public License (EUPL), either version 1.1 or (at your 
 * option) any later version as soon as they are approved by the European 
 * Commission.
 *   
 * Alternatively, you may redistribute and/or modify this program under the terms
 * of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either  version 3 of the License, or (at your option) any later 
 * version. 
 *   
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. 
 *   
 * See the licenses for the specific language governing permissions, limitations 
 * and more details.
 *   
 * You should have received a copy of the EUPL1.1 and the LGPLv3 licenses along 
 * with this program; if not, you may find them at: 
 *   
 *   https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *   http://www.gnu.org/licenses/ 
 *   and 
 *   https://www.gnu.org/licenses/lgpl.txt
 */
package org.sentilo.agent.relational.repository.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.sentilo.agent.relational.domain.Alarm;
import org.sentilo.agent.relational.domain.Data;
import org.sentilo.agent.relational.domain.Observation;
import org.sentilo.agent.relational.domain.Order;
import org.sentilo.agent.relational.repository.AgentRelationalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Deprecated
/**
 * This class is deprecated in favour of AgentRelationalBatchRepositoryImpl which is more efficient. 
 */
public class AgentRelationalRepositoryImpl implements AgentRelationalRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(AgentRelationalRepositoryImpl.class);

  private Map<String, JdbcTemplate> jdbcTemplates;

  @Value("${relational.tables.prefix}")
  private String tables_prefix;

  private String observationPs;
  private String orderPs;
  private String alarmPs;

  @PostConstruct
  public void init() throws Exception {
    if (StringUtils.hasText(tables_prefix) && !tables_prefix.startsWith("$")) {
      observationPs = "insert into " + tables_prefix + "_observations (provider, sensor, value, timestamp, location) values (?,?,?,?,?)";
      orderPs = "insert into " + tables_prefix + "_orders (provider, sensor, message, timestamp) values (?,?,?,?)";
      alarmPs = "insert into " + tables_prefix + "_alarms (alarm, message, timestamp) values (?,?,?)";
    } else {
      throw new IllegalStateException(
          "Field tables_prefix is not initialized. Review your properties configuration and confirm that property relational.tables.prefix is defined");
    }
  }

  @Resource
  public void setDataSources(final Map<String, DataSource> dataSources) {
    // For every Ds this method creates and initializes a new JdbcTemplate and associates it with
    // the dataSource key.
    if (!CollectionUtils.isEmpty(dataSources)) {
      LOGGER.debug("Number of dataSources to register: {}", dataSources.size());
      jdbcTemplates = new HashMap<String, JdbcTemplate>();
      for (final String key : dataSources.keySet()) {
        LOGGER.debug("Registering dataSource {}", key);
        jdbcTemplates.put(key, new JdbcTemplate(dataSources.get(key)));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.agent.relational.repository.AgentRelationalRepository#save(org.sentilo
   * .agent.relational.common.domain.Observation)
   */
  public void save(final Observation observation) {
    final JdbcTemplate jdbcTemplate = getJdbcTemplate(observation);
    if (jdbcTemplate != null) {
      jdbcTemplate.update(observationPs, observation.getProvider(), observation.getSensor(), observation.getValue(), observation.getTimestamp(),
          observation.getLocation());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.agent.relational.repository.AgentRelationalRepository#save(org.sentilo
   * .agent.relational.common.domain.Alarm)
   */
  public void save(final Alarm alarm) {
    final JdbcTemplate jdbcTemplate = getJdbcTemplate(alarm);
    if (jdbcTemplate != null) {
      jdbcTemplate.update(alarmPs, alarm.getAlarm(), alarm.getMessage(), alarm.getTimestamp());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.agent.relational.repository.AgentRelationalRepository#save(org.sentilo
   * .agent.relational.common.domain.Order)
   */
  public void save(final Order order) {
    final JdbcTemplate jdbcTemplate = getJdbcTemplate(order);
    if (jdbcTemplate != null) {
      jdbcTemplate.update(orderPs, order.getProvider(), order.getSensor(), order.getMessage(), order.getTimestamp());
    }
  }

  private JdbcTemplate getJdbcTemplate(final Data data) {
    JdbcTemplate jdbcTemplate = null;
    LOGGER.debug("Will retrieve jdbcTemplate for targetDs {}:", data.getTargetDs());
    if (StringUtils.hasText(data.getTargetDs()) && !CollectionUtils.isEmpty(jdbcTemplates)) {
      LOGGER.debug("Retrieving jdbcTemplate associated to dataSource : {}", data.getTargetDs());
      jdbcTemplate = jdbcTemplates.get(data.getTargetDs());
    } else {
      LOGGER.warn("Not found jdbcTemplate for this targetDs {}. Data will not be persist.", data.getTargetDs());
    }

    return jdbcTemplate;
  }
}
