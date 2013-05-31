/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.rest.client;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opennms.core.test.MockLogAppender;
import org.opennms.rest.client.internal.JerseyClientImpl;
import org.opennms.rest.client.internal.JerseyDataLinkInterfaceService;
import org.opennms.rest.model.ClientDataLinkInterface;

public class ClientDataLinkInterfaceServiceTest {
    
    private JerseyDataLinkInterfaceService m_datalinkinterfaceservice;
    
    @Before
    public void setUp() throws Exception {
        MockLogAppender.setupLogging(true, "DEBUG");
        m_datalinkinterfaceservice = new JerseyDataLinkInterfaceService();
        JerseyClientImpl jerseyClient = new JerseyClientImpl(
                                                         "http://demo.opennms.org/opennms/rest/","demo","demo");
        m_datalinkinterfaceservice.setJerseyClient(jerseyClient);
    }

    @After
    public void tearDown() throws Exception {
        MockLogAppender.assertNoWarningsOrGreater();
    }
    
    @Test
    public void testLinks() throws Exception {
        
        
        
        List<ClientDataLinkInterface> datalinkinterfacelist = m_datalinkinterfaceservice.getAll();
        assertEquals(49 , datalinkinterfacelist.size());
        for (ClientDataLinkInterface datalinkinterface: datalinkinterfacelist) {
        	System.out.println(datalinkinterface);
        }
  
    }


}
