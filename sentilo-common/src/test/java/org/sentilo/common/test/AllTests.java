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
package org.sentilo.common.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.sentilo.common.test.domain.CommonDomainTest;
import org.sentilo.common.test.exception.SentiloExceptionsTest;
import org.sentilo.common.test.hook.SentiloShutdownHookTest;
import org.sentilo.common.test.parser.EventMessageConverterTest;
import org.sentilo.common.test.rest.HMACBuilderTest;
import org.sentilo.common.test.rest.IdentityKeyInterceptorTest;
import org.sentilo.common.test.rest.RESTClientImplTest;
import org.sentilo.common.test.rest.RequestParametersTest;
import org.sentilo.common.test.utils.DateUtilsTest;
import org.sentilo.common.test.utils.RESTUtilsTest;
import org.sentilo.common.test.utils.SentiloUtilsTest;
import org.sentilo.common.test.utils.URIUtilsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({DateUtilsTest.class, EventMessageConverterTest.class, URIUtilsTest.class, RESTUtilsTest.class, 
 RequestParametersTest.class,
    SentiloUtilsTest.class, RESTClientImplTest.class, HMACBuilderTest.class, IdentityKeyInterceptorTest.class, SentiloShutdownHookTest.class,
    SentiloExceptionsTest.class, CommonDomainTest.class})
public class AllTests {

}
