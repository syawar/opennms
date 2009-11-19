/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2009 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Modifications:
 * 
 * Created: Oct 26, 2009
 * 
 * TODO Remove this class when we're totally happy about the QuartzReportReportService
 *
 * Copyright (C) 2009 The OpenNMS Group, Inc.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 *      OpenNMS Licensing       <license@opennms.org>
 *      http://www.opennms.org/
 *      http://www.opennms.com/
 */
package org.opennms.web.svclayer.support;

import java.util.List;

import org.opennms.netmgt.model.DatabaseReportCategoryParm;
import org.opennms.netmgt.model.DatabaseReportCriteria;
import org.opennms.netmgt.model.DatabaseReportDateParm;
import org.opennms.report.availability.svclayer.OnmsDatabaseReportService;
import org.opennms.web.svclayer.DatabaseReportService;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.execution.RequestContext;

/**
 * @author user
 *
 */
public class DefaultDatabaseReportService implements DatabaseReportService {
    
    private static final String DATE_NAME = "endDate";
    private static final String CATEGORY_NAME = "reportCategory";

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String CATEGORY_ERROR = 
        "Report definition must have only one category parameter, with name " + CATEGORY_NAME;
    private static final String DATE_ERROR = 
        "Report definition must have only one date parameter, with name " + DATE_NAME;
    
    OnmsDatabaseReportService m_reportRunner;

    /* (non-Javadoc)
     * @see org.opennms.web.svclayer.DatabaseReportService#execute(org.opennms.web.svclayer.support.DatabaseReportCriteria)
     */
    public String execute(DatabaseReportCriteria criteria, RequestContext context) {
        
        List <DatabaseReportCategoryParm> categories = criteria.getCategories();
        if ((categories.size() != 1) || (!categories.get(0).getName().equals(CATEGORY_NAME))) {
            context.getMessageContext().addMessage(new MessageBuilder().error()
                                                   .defaultText(CATEGORY_ERROR).build());
            return ERROR;
        } else {
            List <DatabaseReportDateParm> dates = criteria.getDates();
            if ((dates.size() != 1) || (!dates.get(0).getName().equals(DATE_NAME))) {
                context.getMessageContext().addMessage(new MessageBuilder().error()
                                                       .defaultText(DATE_ERROR).build());
                return ERROR; 
            } else {
              
                m_reportRunner.setCriteria(criteria);
                new Thread(m_reportRunner).start();
                return SUCCESS;
            }
        }
        
    }

    public void setReportRunner(OnmsDatabaseReportService reportRunner) {
        m_reportRunner = reportRunner;
    }    

}
