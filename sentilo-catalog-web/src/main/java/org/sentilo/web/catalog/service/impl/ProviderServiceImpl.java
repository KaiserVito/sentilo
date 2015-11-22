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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.sentilo.web.catalog.context.TenantContextHolder;
import org.sentilo.web.catalog.domain.Alert;
import org.sentilo.web.catalog.domain.Component;
import org.sentilo.web.catalog.domain.Provider;
import org.sentilo.web.catalog.domain.Sensor;
import org.sentilo.web.catalog.event.DeletePlatformResourcesEvent;
import org.sentilo.web.catalog.repository.ProviderRepository;
import org.sentilo.web.catalog.search.SearchFilter;
import org.sentilo.web.catalog.service.PermissionService;
import org.sentilo.web.catalog.service.ProviderService;
import org.sentilo.web.catalog.service.TenantPermissionService;
import org.sentilo.web.catalog.utils.IdentityKeyGenerator;
import org.sentilo.web.catalog.utils.TenantUtils;
import org.sentilo.web.catalog.validator.EntityKeyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProviderServiceImpl extends AbstractBaseCrudServiceImpl<Provider> implements ProviderService {

  @Autowired
  private ProviderRepository repository;

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private TenantPermissionService tenantPermissionService;

  @Autowired
  @Qualifier("appsAndProvidersKeyValidator")
  private EntityKeyValidator customEntityValidator;

  public ProviderServiceImpl() {
    super(Provider.class);
  }

  @Override
  protected void doAfterInit() {
    setEntityKeyValidator(customEntityValidator);
    super.doAfterInit();
  }

  @Override
  public ProviderRepository getRepository() {
    return repository;
  }

  @Override
  public String getEntityId(final Provider entity) {
    return entity.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.web.catalog.service.ProviderService#findAllowed()
   */
  @Override
  public List<Provider> findAllowed() {
    final SearchFilter filter = new SearchFilter();
    if (TenantContextHolder.isEnabled()) {
      filter.addParam("tenantsAuth", TenantUtils.getCurrentTenant());
    }
    final Query query = buildQuery(filter);
    return getMongoOps().find(query, Provider.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sentilo.web.catalog.service.ProviderService#deleteChildDocuments(org.sentilo.web.catalog
   * .domain.Provider)
   */
  public void deleteChilds(final Provider provider) {
    notifyProviderToDelete(provider);
    deleteRelatedResources(provider);
    tenantPermissionService.deleteRelatedEntity(provider);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.web.catalog.service.ProviderService#addGrantedTenant(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void addGrantedTenant(final String providerId, final String tenantId) {
    final Provider provider = findAndThrowErrorIfNotExist(new Provider(providerId));
    provider.getTenantsAuth().add(tenantId);
    getMongoOps().save(provider);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.web.catalog.service.ProviderService#removeGrantedTenant(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void removeGrantedTenant(final String providerId, final String tenantId) {
    final Provider provider = findAndThrowErrorIfNotExist(new Provider(providerId));
    provider.getTenantsAuth().remove(tenantId);
    getMongoOps().save(provider);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.web.catalog.service.ProviderService#getGrantedTenantsIds(java.lang.String)
   */
  @Override
  public Set<String> getGrantedTenantsIds(final String providerId) {
    return findAndThrowErrorIfNotExist(new Provider(providerId)).getTenantsAuth();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.web.catalog.service.ProviderService#deleteFromTenant(java.lang.String)
   */
  @Override
  public void deleteFromTenant(final String tenantId) {
    final SearchFilter filter = new SearchFilter();
    filter.addAndParam("tenantId", tenantId);
    final Query query = buildQuery(filter);
    final List<Provider> providers = getMongoOps().find(query, Provider.class);
    delete(providers);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.sentilo.web.catalog.service.ProviderService#isProviderFromTenant(java.lang.String,
   * java.lang.String)
   */
  @Override
  public boolean isProviderFromTenant(final String providerId, final String tenantId) {
    final Provider provider = findAndThrowErrorIfNotExist(new Provider(providerId));
    return tenantId.equals(provider.getTenantId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sentilo.web.catalog.service.impl.AbstractBaseCrudServiceImpl#doBeforeCreate(org.sentilo
   * .web.catalog.domain.CatalogDocument)
   */
  protected void doBeforeCreate(final Provider provider) {
    // El identificador se informa por pantalla (es obligatorio). El nombre, en caso de no estar
    // informado, se rellena con el valor del identificador.
    if (!StringUtils.hasText(provider.getName())) {
      provider.setName(provider.getId());
    }

    // Validamos la unicidad del identificador: no puede sexistir otra entidad (app o provider) con
    // el mismo identificador.
    checkIntegrityKey(provider.getId());

    provider.setToken(IdentityKeyGenerator.generateNewToken(provider.getId()));

    permissionService.createRelated(provider);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sentilo.web.catalog.service.impl.AbstractBaseCrudServiceImpl#doAfterDelete(org.sentilo.
   * web.catalog.domain.CatalogDocument)
   */
  protected void doAfterDelete(final Provider provider) {
    permissionService.deleteRelated(provider);
    deleteChilds(provider);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.sentilo.web.catalog.service.impl.AbstractBaseCrudServiceImpl#delete(java.util.Collection)
   */
  protected void doAfterDelete(final Collection<Provider> providers) {
    notifyProvidersToDelete(providers);
    for (final Provider provider : providers) {
      permissionService.deleteRelated(provider);
      deleteRelatedResources(provider);
    }
  }

  private void deleteRelatedResources(final Provider provider) {
    // Must be remove all resources related to the provider: alerts, sensors and components
    final SearchFilter childFilter = new SearchFilter();
    childFilter.addAndParam("providerId", provider.getId());
    final Query childQuery = buildQuery(childFilter);

    getMongoOps().remove(childQuery, Alert.class);
    getMongoOps().remove(childQuery, Sensor.class);
    getMongoOps().remove(childQuery, Component.class);

    // Also delete all tenant permissions related with it
    tenantPermissionService.deleteRelatedEntity(provider);
  }

  private void notifyProviderToDelete(final Provider provider) {
    notifyProvidersToDelete(Arrays.asList(provider));
  }

  private void notifyProvidersToDelete(final Collection<Provider> providers) {
    // Create new DeletePlatformResourceEvent to notify what providers will be deleted
    getContext().publishEvent(new DeletePlatformResourcesEvent<Provider>(this, providers, Provider.class));
  }

}
