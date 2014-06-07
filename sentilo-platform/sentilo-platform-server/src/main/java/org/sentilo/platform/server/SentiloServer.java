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
package org.sentilo.platform.server;

import org.sentilo.common.hook.SentiloShutdownHook;
import org.sentilo.platform.server.http.RequestListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;

public final class SentiloServer {

  private SentiloServer() {
  }

  private static Logger logger = LoggerFactory.getLogger(SentiloServer.class);

  @SuppressWarnings("resource")
  public static void main(final String... args) {
    final String activeProfiles = System.getProperty("spring.profiles.active");
    logger.info("Starting server");
    logger.info("Active profile:{}", activeProfiles);

    Runtime.getRuntime().addShutdownHook(new SentiloShutdownHook("server"));
    logger.info("Sentilo shutdown hook registered");

    final GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
    ctx.getEnvironment().setActiveProfiles(activeProfiles);
    ctx.registerShutdownHook();
    ctx.load("classpath:spring/platform-server-context.xml");
    ctx.refresh();
    
    final RequestListenerThread t = (RequestListenerThread) ctx.getBean("listener");
    t.setDaemon(false);
    t.start();        
  }
}
