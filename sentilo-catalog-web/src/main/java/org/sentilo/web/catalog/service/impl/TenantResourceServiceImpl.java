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
package org.sentilo.web.catalog.service.impl;

import java.util.Arrays;
import java.util.List;

import org.sentilo.web.catalog.service.TenantResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

@Service
public class TenantResourceServiceImpl implements TenantResourceService {

  @Autowired
  private MongoOperations mongoOps;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sentilo.web.catalog.service.TenantResourceService#updateProviderTenantAuth(java.lang.String
   * , java.lang.String, boolean)
   */
  @Override
  public void updateResourceTenantsAuthByProvider(final String providerId, final String tenantId, final boolean addGrant) {

    // Update the Provider tenants authorization list, adding or removing values as needed and
    // updating dependent elements too (alert, application, component, sensor)
    // If addGrant, then add grant to the provider and its dependent elements in cascade
    // Else, then remove the tenant grant from the provider and its dependent elements in cascade

    // Get all collections affected
    final List<String> collections = Arrays.asList("alert", "component", "sensor", "user");

    // Update the tenantsAuth values
    if (addGrant) {
      mongoOps.getCollection("provider").update(new BasicDBObject("_id", providerId),
          new BasicDBObject("$push", new BasicDBObject("tenantsAuth", tenantId)));
      for (final String collection : collections) {
        mongoOps.getCollection(collection).updateMulti(new BasicDBObject("providerId", providerId),
            new BasicDBObject("$push", new BasicDBObject("tenantsAuth", tenantId)));
      }
    } else {
      mongoOps.getCollection("provider").update(new BasicDBObject("_id", providerId),
          new BasicDBObject("$pull", new BasicDBObject("tenantsAuth", tenantId)));
      for (final String collection : collections) {
        mongoOps.getCollection(collection).updateMulti(new BasicDBObject("providerId", providerId),
            new BasicDBObject("$pull", new BasicDBObject("tenantsAuth", tenantId)));
      }
    }
  }

}
