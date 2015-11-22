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
package org.sentilo.web.catalog.aop.aspect;

import java.util.Collection;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.sentilo.web.catalog.context.TenantContextHolder;
import org.sentilo.web.catalog.domain.Alert;
import org.sentilo.web.catalog.domain.Application;
import org.sentilo.web.catalog.domain.Component;
import org.sentilo.web.catalog.domain.EntityResource;
import org.sentilo.web.catalog.domain.Provider;
import org.sentilo.web.catalog.domain.TenantResource;
import org.sentilo.web.catalog.security.audit.Auditable;
import org.sentilo.web.catalog.utils.TenantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

/**
 * It aspect allows to fill in the tenantId and tenantAuth fields in each TenantResource created.
 */
@org.springframework.stereotype.Component
@Aspect
public class TenantManagerAspect implements Ordered {

  private static final int ORDER = 2;

  @Autowired
  private MongoOperations mongoOps;

  @Pointcut("execution(* org.sentilo.web.catalog.service.impl.AbstractBaseCrudServiceImpl.insertAll(..)) && args(entityResources)")
  public void insertAll(final Collection<EntityResource> entityResources) {
    // Do nothing. Pointcut definition
  }

  @Pointcut("execution(* org.sentilo.web.catalog.service.impl.AbstractBaseCrudServiceImpl.updateAll(..)) && args(entityResources)")
  public void updateAll(final Collection<EntityResource> entityResources) {
    // Do nothing. Pointcut definition
  }

  @Pointcut("execution(* org.sentilo.web.catalog.service.impl.*.create(..)) && args(tenantResource)")
  public void create(final TenantResource tenantResource) {
    // Do nothing. Pointcut definition
  }

  @Pointcut("execution(* org.sentilo.web.catalog.service.impl.*.update(..)) && args(tenantResource)")
  public void update(final TenantResource tenantResource) {
    // Do nothing. Pointcut definition
  }

  @Before("((update(tenantResource) || create(tenantResource)) && @annotation(auditable) )")
  public void doSimpleAdvice(final JoinPoint jp, final TenantResource tenantResource, final Auditable auditable) {
    if (TenantContextHolder.isEnabled()) {
      final String currentTenant = TenantUtils.getCurrentTenant();
      switch (auditable.actionType()) {
        case CREATE:
          addTenantFields(tenantResource, currentTenant);
          break;
        case UPDATE:
          keepTenantFields(tenantResource);
          break;
        default:
          break;
      }
    }
    return;
  }

  @Before("((updateAll(entityResources) || insertAll(entityResources)) && @annotation(auditable) )")
  public void doCollectionAdvice(final JoinPoint jp, final Collection<EntityResource> entityResources, final Auditable auditable) {
    if (TenantContextHolder.isEnabled()) {
      switch (auditable.actionType()) {
        case CREATE:
          addTenantFields(entityResources);
          break;
        case UPDATE:
          keepTenantFields(entityResources);
          break;
        default:
          break;
      }
    }

    return;
  }

  private void keepTenantFields(final Collection<EntityResource> entityResources) {
    for (final EntityResource entityResource : entityResources) {
      keepTenantFields((TenantResource) entityResource);
    }
  }

  private void keepTenantFields(final TenantResource entityResource) {
    final Criteria criteria = Criteria.where("_id").is(entityResource.getId());
    final TenantResource resource = mongoOps.findOne(new Query(criteria), entityResource.getClass());
    if (resource != null) {
      entityResource.setTenantsAuth(resource.getTenantsAuth());
      entityResource.setTenantId(resource.getTenantId());
      // And finally, if tenantResource is Component, keep its tenantsMapVisible members
      if (entityResource instanceof Component) {
        ((Component) entityResource).getTenantsMapVisible().addAll(((Component) resource).getTenantsMapVisible());
      }
    }
  }

  private void addTenantFields(final Collection<EntityResource> entityResources) {
    String entityOwnerTenant = null;
    for (final EntityResource entityResource : entityResources) {
      // For each tenantResource, its tenant owner must be equals to its entity tenant owner, so
      // the first step is to get the entity tenant owner (it could be a Provider or an Application)
      // And all the resources into collection belongs to the same entity
      if (entityOwnerTenant == null) {
        entityOwnerTenant = getEntityOwnerTenant(entityResource);
      }

      addTenantFields((TenantResource) entityResource, entityOwnerTenant);
    }
  }

  private void addTenantFields(final TenantResource tenantResource, final String currentTenant) {
    // set the current tenant as tenantId owner if it is not null
    if (StringUtils.hasText(currentTenant)) {
      tenantResource.setTenantId(currentTenant);
    }

    // Add tenantId owner as authorized
    tenantResource.getTenantsAuth().add(tenantResource.getTenantId());
    // And finally, if tenantResource is Component, add currentTenant to tenantsMapVisible map
    if (tenantResource instanceof Component) {
      ((Component) tenantResource).getTenantsMapVisible().add(currentTenant);
    }
  }

  private boolean entityBelongsToProvider(final EntityResource entityResource) {
    if (entityResource instanceof Alert) {
      return (StringUtils.hasText(((Alert) entityResource).getProviderId()) ? true : false);
    } else {
      return true;
    }
  }

  private String getEntityOwnerTenant(final EntityResource entityResource) {
    final Class<?> collectionClass = (entityBelongsToProvider(entityResource) ? Provider.class : Application.class);
    final Criteria criteria = Criteria.where("_id").is(entityResource.getEntityOwner());
    final TenantResource entityOwner = (TenantResource) mongoOps.find(new Query(criteria), collectionClass).get(0);
    return entityOwner.getTenantId();
  }

  @Override
  public int getOrder() {
    return ORDER;
  }

}
