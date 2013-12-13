/*
 * Sentilo
 *   
 * Copyright (C) 2013 Institut Municipal d’Informàtica, Ajuntament de  Barcelona.
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
package org.sentilo.web.catalog.test.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sentilo.web.catalog.utils.CompoundKeyBuilder;
import org.sentilo.web.catalog.utils.Constants;

public class CompoundKeyBuilderTest {

	@Test
	public void buildKeyWithTwoTokens(){
		String token1 = "token1";
		String token2 = "token2"; 
		String compoundKey = CompoundKeyBuilder.buildCompoundKey(token1,token2);
		String expectedValue = token1.concat(Constants.DEFAULT_KEY_TOKEN_SPLITTER).concat(token2);
		
		assertEquals(expectedValue, compoundKey);
	}
	
	@Test
	public void buildKeyWithMoreThanTwoTokens(){
		String token1 = "token1";
		String token2 = "token2"; 
		String token3 = "token3";
		String compoundKey = CompoundKeyBuilder.buildCompoundKey(token1,token2,token3);
		String expectedValue = token1.concat(Constants.DEFAULT_KEY_TOKEN_SPLITTER).concat(token2).concat(Constants.DEFAULT_KEY_TOKEN_SPLITTER).concat(token3);
		
		assertEquals(expectedValue, compoundKey);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void buildKeyWithoutTokens(){
		String[] tokens = {};
		 
		CompoundKeyBuilder.buildCompoundKey(tokens);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void buildKeyWithNullToken(){
		String token1 = "token1";
		String token2 = "token2"; 
		String token3 = null;
		
		CompoundKeyBuilder.buildCompoundKey(token1,token2,token3);		 			
	}
}