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
package org.sentilo.agent.alert.domain;

import java.util.Date;

import org.sentilo.agent.alert.utils.enums.AlarmTriggerType;
import org.sentilo.agent.alert.utils.enums.AlarmType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Alarm {

  @Id
  private String id;

  private String name;

  private String description;

  private AlarmType type;
  private AlarmTriggerType trigger;

  private Date createdAt;
  private Date updateAt;

  private String expression;

  /**
   * Identificador del proveedor al cual esta asociada la alarma en el caso de que sea interna.
   */
  private String providerId;

  /**
   * Identificador del componente al cual esta asociada la alarma en el caso de que sea interna.
   */
  private String componentId;

  /**
   * Identificador del sensor al cual esta asociada la alarma en el caso de que sea interna.
   */
  private String sensorId;

  public Alarm() {

  }

  public Alarm(final String id) {
    this();
    this.id = id;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Alarm) || id == null) {
      return false;
    }
    final Alarm other = (Alarm) obj;
    return id.equals(other.id);
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getSensorId() {
    return sensorId;
  }

  public void setSensorId(final String sensorId) {
    this.sensorId = sensorId;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(final String expression) {
    this.expression = expression;
  }

  public void setUpdateAt(final Date updatedAt) {
    updateAt = updatedAt;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(final String providerId) {
    this.providerId = providerId;
  }

  public String getComponentId() {
    return componentId;
  }

  public void setComponentId(final String componentId) {
    this.componentId = componentId;
  }

  public AlarmType getType() {
    return type;
  }

  public void setType(final AlarmType type) {
    this.type = type;
  }

  public AlarmTriggerType getTrigger() {
    return trigger;
  }

  public void setTrigger(final AlarmTriggerType trigger) {
    this.trigger = trigger;
  }

  @Override
  public int hashCode() {
    // Hashcode return must be consistent with the equals method
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

}
