<%--
/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
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

--%>


<%@page language="java"
        contentType="text/html"
        session="true"
        import="
                java.io.*,
                java.util.*,
                org.opennms.web.admin.notification.noticeWizard.*,
                org.opennms.netmgt.config.*,
                org.opennms.netmgt.config.notifications.*,
                org.opennms.core.utils.BundleLists,
                org.opennms.core.utils.ConfigFileConstants,
                org.springframework.core.io.FileSystemResource,
                org.opennms.netmgt.poller.ServiceMonitor
        "
%>

<%!
        private DefaultPollerConfigDao m_pollerConfDao;

        public void init() throws ServletException {
                
                try {
                        OpennmsServerConfigFactory.init();
                OpennmsServerConfigFactory onmsSvrConfig = OpennmsServerConfigFactory.getInstance();
                        m_pollerConfDao = new DefaultPollerConfigDao();
                        m_pollerConfDao.setConfigResource(new FileSystemResource(ConfigFileConstants.getFile(ConfigFileConstants.POLLER_CONFIG_FILE_NAME)));
                        m_pollerConfDao.setLocalServer(onmsSvrConfig.getServerName());
                        m_pollerConfDao.setVerifyServer(onmsSvrConfig.verifyServer());
                        m_pollerConfDao.afterPropertiesSet();
                } catch (Throwable e) {
                        throw new ServletException("Cannot load configuration file", e);
                }
        }
%>


<%
    String newRule = request.getParameter("newRule");
    String criticalIp = request.getParameter("criticalIp");
    String showNodes = request.getParameter("showNodes");
    String returnTo = request.getParameter("returnTo");
%>


<jsp:include page="/includes/header.jsp" flush="false" >
  <jsp:param name="title" value="Configure Path Outages" />
  <jsp:param name="headTitle" value="Configure Path Outages" />
  <jsp:param name="headTitle" value="Admin" />
  <jsp:param name="breadcrumb" value="<a href='admin/index.jsp'>Admin</a>" />
  <jsp:param name="breadcrumb" value="<a href='admin/notification/index.jsp'>Configure Notifications</a>" />
  <jsp:param name="breadcrumb" value="Configure Path Outages" />
</jsp:include>

<script type="text/javascript" >

    function next()
    {
        document.crpth.nextPage.value="<%=NotificationWizardServlet.SOURCE_PAGE_VALIDATE_PATH_OUTAGE%>";
        document.crpth.submit();
    }
    
</script>

<form method="post" name="crpth"
      action="admin/notification/noticeWizard/notificationWizard" >
      
      
    <% String mode = request.getParameter("mode");
       if (mode != null && mode.endsWith("failed")) { %>
       
        <h3 style="color:red"><%=mode%>. Please check the entry for errors and re-submit.</h3>
              
    <% } %>

    <h3>Define the Critical Path</h3>

    Enter the critical path IP address in xxx.xxx.xxx.xxx format. (Or leave blank to clear previously set paths.)

    <br/><br/>

    <input type="text" name="criticalIp" value = '<%= (criticalIp != null ? criticalIp : "") %>' size="17" maxlength="15" />

    <br/><br/>

    critical path service:

    <br/><br/>

    <select name="criticalSvc" size="1">
        <%=getAllPollerList()%>
    </select>
      <input type="hidden" name="sourcePage" value="<%=NotificationWizardServlet.SOURCE_PAGE_PATH_OUTAGE%>"/>
      <input type="hidden" name="nextPage" value=""/>
      <input type="hidden" name="returnTo" value="<%= returnTo%>"/>
    <h3>Build the rule that determines which nodes will be subject to this critical path.</h3>
            <p>Filtering on TCP/IP address uses a very flexible format, allowing you
               to separate the four octets (fields) of a TCP/IP address into specific
               searches.  An asterisk (*) in place of any octet matches any value for that
               octet. Ranges are indicated by two numbers separated by a dash (-), and
               commas are used for list demarcation.
            </p>
            <p>The following examples are all valid and yield the set of addresses from
               192.168.0.0 through 192.168.3.255.</p>
               <ul>
                  <li>192.168.0-3.*
                  <li>192.168.0-3.0-255
                  <li>192.168.0,1,2,3.*
               </ul>
            <p>To Use a rule based on TCP/IP addresses as described above, enter<br/><br/>
               IPADDR IPLIKE *.*.*.*<br/><br/>in the Current Rule box below, substituting your
               desired address fields for *.*.*.*.
               <br/>Otherwise, you may enter any valid rule.
            </p>
            Current Rule:<br/>
            <input type="text" size=100 name="newRule" value="<%=newRule%>"/>
           <br/><br/>

            Show matching node list:
            <% if (showNodes == null) { %>
            <input type="checkbox" name="showNodes" checked="true" >
            <% } else { %>
            <input type="checkbox" name="showNodes">
            <% } %>
           <br/>

           <br/>
            <input type="reset" value="Reset"/>
           <br/><br/>
           <a href="javascript:next()">Validate rule results &#155;&#155;&#155;</a>
    </form>

<jsp:include page="/includes/footer.jsp" flush="false" />

<%!
        public String getAllPollerList() throws IOException, FileNotFoundException{
                
                PollerConfig m_pollerConfig = m_pollerConfDao.getPollerConfig();
                
                Map<String, ServiceMonitor> serviceMonitors = m_pollerConfig.getServiceMonitors();
                
                StringBuffer buffer = new StringBuffer();
                
                for (Map.Entry<String, ServiceMonitor> entry : serviceMonitors.entrySet()) {
                        buffer.append("<option selected VALUE=" + entry.getKey() + ">" + entry.getKey() + "</option>");
                }

        return buffer.toString();
    }  
    
    
%>