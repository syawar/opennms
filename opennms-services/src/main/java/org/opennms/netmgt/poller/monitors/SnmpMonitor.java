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

package org.opennms.netmgt.poller.monitors;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Level;
import org.jfree.util.Log;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.core.utils.ParameterMap;
import org.opennms.core.utils.PropertiesUtils;
import org.opennms.netmgt.config.SnmpPeerFactory;
import org.opennms.netmgt.model.OnmsAssetRecord;
import org.opennms.netmgt.model.PollStatus;
import org.opennms.netmgt.poller.Distributable;
import org.opennms.netmgt.poller.DistributionContext;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.poller.NetworkInterface;
import org.opennms.netmgt.snmp.SnmpAgentConfig;
import org.opennms.netmgt.snmp.SnmpInstId;
import org.opennms.netmgt.snmp.SnmpObjId;
import org.opennms.netmgt.snmp.SnmpUtils;
import org.opennms.netmgt.snmp.SnmpValue;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.dao.AssetRecordDao;
import org.opennms.core.utils.BeanUtils;
/**
 * <P>
 * This class is designed to be used by the service poller framework to test the
 * availability of the SNMP service on remote interfaces. The class implements
 * the ServiceMonitor interface that allows it to be used along with other
 * plug-ins by the service poller framework.
 * </P>
 * <p>
 * This does SNMP and therefore relies on the SNMP configuration so it is not distributable.
 * </p>
 *
 * @author <A HREF="mailto:tarus@opennms.org">Tarus Balog </A>
 * @author <A HREF="mailto:mike@opennms.org">Mike Davidson </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 */
@Distributable(DistributionContext.DAEMON)
public class SnmpMonitor extends SnmpMonitorStrategy {
    /**
     * Name of monitored service.
     */
    private static final String SERVICE_NAME = "SNMP";
    
    /**
     * the node dao object for retrieving assets
     */
    private NodeDao m_nodeDao = null; 
    
    /**
     * the asset records
     */
    private AssetRecordDao m_assetDao = null;

    /**
     * Default object to collect if "oid" property not available.
     */
    private static final String DEFAULT_OBJECT_IDENTIFIER = ".1.3.6.1.2.1.1.2.0"; // MIB-II
                                                                                // System
                                                                                // Object
                                                                                // Id

    private static final String DEFAULT_REASON_TEMPLATE = "Observed value '${observedValue}' does not meet criteria '${operator} ${operand}'";

    /**
     * <P>
     * Returns the name of the service that the plug-in monitors ("SNMP").
     * </P>
     *
     * @return The service that the plug-in monitors.
     */
    public String serviceName() {
        return SERVICE_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * <P>
     * Initialize the service monitor.
     * </P>
     * @exception RuntimeException
     *                Thrown if an unrecoverable error occurs that prevents the
     *                plug-in from functioning.
     */
    public void initialize(Map<String, Object> parameters) {
        // Initialize the SnmpPeerFactory
        //
    	 m_nodeDao = BeanUtils.getBean("daoContext", "nodeDao", NodeDao.class);
    	 m_assetDao = BeanUtils.getBean("daoContext", "assetRecordDao", AssetRecordDao.class);

         if (m_nodeDao == null || m_assetDao == null) {
         	log().debug("::Node dao/ asset Dao should be a non-null value.");
         }
         
        try {
            SnmpPeerFactory.init();
        } catch (IOException ex) {
        	log().fatal("initialize: Failed to load SNMP configuration", ex);
            throw new UndeclaredThrowableException(ex);
        }

        return;
    }

    /**
     * <P>
     * Called by the poller framework when an interface is being added to the
     * scheduler. Here we perform any necessary initialization to prepare the
     * NetworkInterface object for polling.
     * </P>
     *
     * @exception RuntimeException
     *                Thrown if an unrecoverable error occurs that prevents the
     *                interface from being monitored.
     * @param svc a {@link org.opennms.netmgt.poller.MonitoredService} object.
     */
    public void initialize(MonitoredService svc) {
        super.initialize(svc);
        return;
    }

    /**
     * {@inheritDoc}
     *
     * <P>
     * The poll() method is responsible for polling the specified address for
     * SNMP service availability.
     * </P>
     * @exception RuntimeException
     *                Thrown for any unrecoverable errors.
     */
    public PollStatus poll(MonitoredService svc, Map<String, Object> parameters) {
    	//the onms node
    	OnmsNode onmsNode = m_nodeDao.get(svc.getNodeId());
    	//OnmsAssetRecord modifAssets = onmsNode.getAssetRecord();
        NetworkInterface<InetAddress> iface = svc.getNetInterface();

        PollStatus status = PollStatus.unavailable();
        InetAddress ipaddr = iface.getAddress();

        // Retrieve this interface's SNMP peer object
        //
        SnmpAgentConfig agentConfig = SnmpPeerFactory.getInstance().getAgentConfig(ipaddr);
        if (agentConfig == null) throw new RuntimeException("SnmpAgentConfig object not available for interface " + ipaddr);
        final String hostAddress = InetAddressUtils.str(ipaddr);
		log().debug("poll: setting SNMP peer attribute for interface " + hostAddress);
		
        // Get configuration parameters
        //
        String oid = ParameterMap.getKeyedString(parameters, "oid", DEFAULT_OBJECT_IDENTIFIER);
        String operator = ParameterMap.getKeyedString(parameters, "operator", null);
        String operand = ParameterMap.getKeyedString(parameters, "operand", null);
        
        //get passive snmp poller stats
        boolean isPassive = ParameterMap.getKeyedBoolean(parameters, "isPassive", false);
        String hostList = ParameterMap.getKeyedString(parameters, "hostList", null);
        
        //TODO support mac2oid conversion and mib aggregation
        boolean mac2oid = ParameterMap.getKeyedBoolean(parameters, "mac2oid", false);
        boolean aggregation = ParameterMap.getKeyedBoolean(parameters, "aggregation", false);
        int strategy = ParameterMap.getKeyedInteger(parameters,"strategy", 0);
        boolean reverse = ParameterMap.getKeyedBoolean(parameters, "reverse", false);
        
        //mac to oid
        String assetMacToOid =onmsNode.getAssetRecord().getMacAddress();
        if(mac2oid){
        	if(assetMacToOid != null && !"".equals(assetMacToOid)){
        		assetMacToOid = convertMacToOid(assetMacToOid);
        	}      	
        }
        //aggregation
        String assetOid = getStringAsset(oid, onmsNode.getAssetRecord().getSnmpMib() );
        if(aggregation){
        	oid = OidAggregation(strategy, reverse, assetMacToOid, assetOid, oid);
        }
        else{
        	// check if asset fields need to be used
            oid = getStringAsset(oid, onmsNode.getAssetRecord().getSnmpMib() );
        }
        
        // check if asset fields need to be used
        operator = getStringAsset(operator,onmsNode.getAssetRecord().getSnmpComparator());
        log().debug("the operator::" + operator);
        operand = getStringAsset(operand,onmsNode.getAssetRecord().getCompareValue());
        log().debug("the operand::" + operand);
        hostList = getStringAsset(hostList, onmsNode.getAssetRecord().getHostList());
        
        
        operator = getStringAsset(operator,onmsNode.getAssetRecord().getSnmpComparator());
        operand = getStringAsset(operand,onmsNode.getAssetRecord().getCompareValue());
        hostList = getStringAsset(hostList, onmsNode.getAssetRecord().getHostList());
        
        log().debug("The value of isPassive::"+isPassive);
        //Check if passive SNMP polling
        if(isPassive){
        	String lastParentController = onmsNode.getAssetRecord().getLastParentController();
        	String lastPolledMib = onmsNode.getAssetRecord().getLastPolledMib();
        	//check if asset field has changed and/or matches the last polling record
        	if(lastParentController != null && !"".equals(lastParentController) && lastPolledMib != null && !"".equals(lastPolledMib) && (lastPolledMib.equals(oid) || lastPolledMib.contains(oid))){
        		//check if hostlist still contains the selected IP from the previous poll
        		if(hostList != null && hostList.contains(lastParentController)){
        			List<String> theHostList=Arrays.asList(hostList.split("\\s*:\\s*"));
        			Integer theDynamicPort = null;
        			boolean previousCheck = true;
        			for(String host : theHostList){
        				if(host.contains(":")){
                			List<String> hostDiv=Arrays.asList(host.split("\\s*:\\s*"));
                			if(hostDiv.size() == 2){
                				if(hostDiv.get(0).trim().contains(lastParentController)){
                        			theDynamicPort = Integer.parseInt(hostDiv.get(1).trim());
                				}
                			}
                		}
        			}
        			
            		lastParentController= lastParentController.trim();
            		log().debug("Passive polling last Parent Controller::"+lastParentController);
            		InetAddress passiveHost = InetAddressUtils.addr(lastParentController);
            		SnmpAgentConfig passiveAgentConfig = SnmpPeerFactory.getInstance().getAgentConfig(passiveHost);
                    agentConfig.setAddress(passiveHost);
                    if (agentConfig == null) throw new RuntimeException("Reverting to local config failed::SnmpAgentConfig object not available for interface " + ipaddr );
                    status =  doPolling(operator,operand,lastPolledMib,parameters,status,InetAddressUtils.str(passiveHost),agentConfig, previousCheck,svc.getNodeId(),theDynamicPort);
                    if(status.isUp()){
                    	Date theCurrentTime = new Date();
                    	log().debug("updating asset records..status up from last polled mib..");
                    	m_assetDao.updateAssetRecord(svc.getNodeId(),"snmpCheckDate",theCurrentTime.toString());
                    	return status;
                    }
        		}
        		else{
        			log().debug("Previous Optimized Value is outdated...Ignoring");
        		}
        		
        	}
        	log().debug("is Passive is true");
            List<String> theHostList;
            Integer theDynamicPort = null;
            if(hostList != null){
            	log().debug("Passive polling hostlist is not empty::");
            	theHostList=Arrays.asList(hostList.split("\\s*,\\s*"));
            	for(String host : theHostList){
            		
            		if(host.contains(":")){
            			List<String> hostDiv=Arrays.asList(host.split("\\s*:\\s*"));
            			if(hostDiv.size() == 2){
            				host=hostDiv.get(0).trim();
                			theDynamicPort = Integer.parseInt(hostDiv.get(1).trim());
            			}
            			else{
            				log().error("Improper fomat of host::"+host+"::compensating and using default port");
            				host = host.replace(":", "");
            				host = host.trim();
            			}
            		}
            		log().debug("Passive polling host::"+host);
            		InetAddress passiveHost = InetAddressUtils.addr(host);
            		SnmpAgentConfig passiveAgentConfig = SnmpPeerFactory.getInstance().getAgentConfig(passiveHost);
                    agentConfig.setAddress(passiveHost);
                    if (agentConfig == null) throw new RuntimeException("Reverting to local config failed::SnmpAgentConfig object not available for interface " + ipaddr );
                    status =  doPolling(operator,operand,oid,parameters,status,InetAddressUtils.str(passiveHost),agentConfig,false,svc.getNodeId(),theDynamicPort);
                    //if the status is up change the required asset fields for optimization steps
                    if(status.isUp()){
                    	Date theCurrentTime = new Date();
                    	log().debug("updating asset records..for node up..");
                    	m_assetDao.updateAssetRecord(svc.getNodeId(),"snmpCheckDate",theCurrentTime.toString());
                    	m_assetDao.updateAssetRecord(svc.getNodeId(),"lastParentController",host.trim());
                    	return status;
                    }
                    log().debug("Passive poll on host::"+host+"::result::"+status.toString());
            	}
            	if(!status.isUp()){
            		//Date theCurrentTime = new Date();
            		log().debug("updating asset records..for down or unavailable node..");
            		//m_assetDao.updateAssetRecord(svc.getNodeId(),"snmpCheckDate",theCurrentTime.toString());
            		status= PollStatus.down();
            	}
            }
            else{
            	//Date theCurrentTime = new Date();
            	log().debug("updating asset records..no host list found..");
            	//m_assetDao.updateAssetRecord(svc.getNodeId(),"snmpCheckDate",theCurrentTime.toString());
            	log().error("Passive poller found no host list::value recieved::"+hostList+"::setting state to down");
            	status = PollStatus.down();
            }
            //Date theCurrentTime = new Date();
            log().debug("updating asset records..normal update..");
            //m_assetDao.updateAssetRecord(svc.getNodeId(),"snmpCheckDate",theCurrentTime.toString());
    		
        	log().debug("::The status to return::"+status.toString());
            return status;
        }
        else{
        	log().debug("doing normal polling::");
        	status = doPolling(operator,operand,oid,parameters,status,hostAddress,agentConfig,false,svc.getNodeId(),null);
        }
        
        
        return status;
        
    }
    /**
     * {@inheritDoc}
     *
     * <P>
     * The doPoll() method is responsible for polling the specified address for
     * SNMP service availability based on the configuration passed from the poll() method
     * </P>
     * @param operator
     * @param operand
     * @param oid
     * @param parameters
     * @param status
     * @param hostAddress
     * @param agentConfig 
     * @param prevCheck 
     * @param nodeId 
     * @return PollStatus from the current poll
     * 
     * @author <A HREF="mailto:syawar@datavalet.com">Saqib Yawar </A>
     */
    public PollStatus doPolling(String operator, String operand, String oid, Map<String, Object> parameters ,PollStatus status, String hostAddress , SnmpAgentConfig agentConfig, boolean prevCheck, Integer nodeId, Integer dynamicPort){
    	//TODO:CLEANUP THE FUNCTION TO USE MAP STRING INSTEAD OF SO MANY VARIABLES
    	//to compensate for previous mib checks
    	String walkstr = "";
    	if(prevCheck){
    		walkstr = "false";
    	}
    	else{
    		walkstr = ParameterMap.getKeyedString(parameters, "walk", "false");
    	}
    	
        String matchstr = ParameterMap.getKeyedString(parameters, "match-all", "true");
        int countMin = ParameterMap.getKeyedInteger(parameters, "minimum", 0);
        int countMax = ParameterMap.getKeyedInteger(parameters, "maximum", 0);
        String reasonTemplate = ParameterMap.getKeyedString(parameters, "reason-template", DEFAULT_REASON_TEMPLATE);
        String hexstr = ParameterMap.getKeyedString(parameters, "hex", "false");

        hex = "true".equalsIgnoreCase(hexstr);
        // set timeout and retries on SNMP peer object
        agentConfig.setTimeout(ParameterMap.getKeyedInteger(parameters, "timeout", agentConfig.getTimeout()));
        agentConfig.setRetries(ParameterMap.getKeyedInteger(parameters, "retry", ParameterMap.getKeyedInteger(parameters, "retries", agentConfig.getRetries())));
        if(dynamicPort != null){
        	agentConfig.setPort(dynamicPort);
        }
        else{
        	agentConfig.setPort(ParameterMap.getKeyedInteger(parameters, "port", agentConfig.getPort()));
        }

        // Squirrel the configuration parameters away in a Properties for later expansion if service is down
        Properties svcParams = new Properties();
        svcParams.setProperty("oid", oid);
        svcParams.setProperty("operator", String.valueOf(operator));
        svcParams.setProperty("operand", String.valueOf(operand));
        svcParams.setProperty("walk", walkstr);
        svcParams.setProperty("matchAll", matchstr);
        svcParams.setProperty("minimum", String.valueOf(countMin));
        svcParams.setProperty("maximum", String.valueOf(countMax));
        svcParams.setProperty("timeout", String.valueOf(agentConfig.getTimeout()));
        svcParams.setProperty("retry", String.valueOf(agentConfig.getRetries()));
        svcParams.setProperty("retries", svcParams.getProperty("retry"));
        svcParams.setProperty("ipaddr", hostAddress);
        svcParams.setProperty("port", String.valueOf(agentConfig.getPort()));
        svcParams.setProperty("hex", hexstr);

        if (log().isDebugEnabled()) log().debug("poll: service= SNMP address= " + agentConfig);

        // Establish SNMP session with interface
        //
        try {
            if (log().isDebugEnabled()) {
                log().debug("SnmpMonitor.poll: SnmpAgentConfig address: " +agentConfig);
            }
            SnmpObjId snmpObjectId = SnmpObjId.get(oid);
            log().debug("snmp object for oid::"+oid+"::is::"+snmpObjectId);
            // This if block will count the number of matches within a walk and mark the service
            // as up if it is between the minimum and maximum number, down if otherwise. Setting
            // the parameter "matchall" to "count" will act as if "walk" has been set to "true".
            if ("count".equals(matchstr)) {
            	log().debug("::SNMP::level 1::Count Match");
                if (DEFAULT_REASON_TEMPLATE.equals(reasonTemplate)) {
                    reasonTemplate = "Value: ${matchCount} outside of range Min: ${minimum} to Max: ${maximum}";
                }
                int matchCount = 0;
                List<SnmpValue> results = SnmpUtils.getColumns(agentConfig, "snmpPoller", snmpObjectId);
                for(SnmpValue result : results) {

                    if (result != null) {
                        log().debug("poll: SNMPwalk poll succeeded, addr=" + hostAddress + " oid=" + oid + " value=" + result);
                        if (meetsCriteria(result, operator, operand)) {
                            matchCount++;
                        }
                    }
                }
                svcParams.setProperty("matchCount", String.valueOf(matchCount));
                log().debug("poll: SNMPwalk count succeeded, total=" + matchCount + " min=" + countMin + " max=" + countMax);
                if ((countMin <= matchCount) && (matchCount <= countMax)) {
                    status = PollStatus.available();
                } else {
                    status = logDown(Level.DEBUG, PropertiesUtils.substitute(reasonTemplate, svcParams));
                    return status;
                }
            } else if ("true".equals(walkstr)) {
            	log().debug("::SNMP::level 2::WALK");
                if (DEFAULT_REASON_TEMPLATE.equals(reasonTemplate)) {
                    reasonTemplate = "SNMP poll failed, addr=${ipaddr} oid=${oid}";
                }
                
                //List<SnmpValue> results = SnmpUtils.getColumns(agentConfig, "snmpPoller", snmpObjectId);
                Map<SnmpInstId, SnmpValue> results = SnmpUtils.getOidValues(agentConfig, "snmpPoller", snmpObjectId);
                log().debug("the snmp walk resultsed in list of size::"+results.size());
                for(Map.Entry<SnmpInstId, SnmpValue> entry : results.entrySet()) {
                	SnmpValue result= entry.getValue();
                	log().debug("iterative snmp walk result::"+result.toHexString());
                    if (result != null) {
                    	log().debug("results are NOT null");
                        svcParams.setProperty("observedValue", getStringValue(result));
                        log().debug("poll: SNMPwalk poll succeeded, addr=" + hostAddress + " oid=" + oid + " value=" + getStringValue(result));
                        if (meetsCriteria(result, operator, operand)) {
                            status = PollStatus.available();
                        	m_assetDao.updateAssetRecord(nodeId,"lastPolledMib", addMibObject(oid,entry.getKey().toString()));
                            if ("false".equals(matchstr)) {
                                return status;
                            }
                        } else if ("true".equals(matchstr)) {
                            status = logDown(Level.DEBUG, PropertiesUtils.substitute(reasonTemplate, svcParams));
                            return status;
                        }
                    }
                    else{
                    	log().debug("results are null");
                    }
                }

            } else {
            	log().debug("::SNMP::level 3::DEFAULT");
                if (DEFAULT_REASON_TEMPLATE.equals(reasonTemplate)) {
                    if (operator != null) {
                        reasonTemplate = "Observed value '${observedValue}' does not meet criteria '${operator} ${operand}'";
                    } else {
                        reasonTemplate = "Observed value '${observedValue}' was null";
                    }
                }

                SnmpValue result = SnmpUtils.get(agentConfig, snmpObjectId);

                if (result != null) {
                    svcParams.setProperty("observedValue", getStringValue(result));
                    log().debug("poll: SNMP poll succeeded, addr=" + hostAddress + " oid=" + oid + " value=" + result);
                    
                    if (meetsCriteria(result, operator, operand)) {
                        status = PollStatus.available();
                        m_assetDao.updateAssetRecord(nodeId,"lastPolledMib", oid);
                    } else {
                        status = PollStatus.unavailable(PropertiesUtils.substitute(reasonTemplate, svcParams));
                    }
                } else {
                    status = logDown(Level.DEBUG, "SNMP poll failed, addr=" + hostAddress + " oid=" + oid);
                }
            }

        } catch (NumberFormatException e) {
            status = logDown(Level.ERROR, "Number operator used on a non-number " + e.getMessage());
        } catch (IllegalArgumentException e) {
            status = logDown(Level.ERROR, "Invalid SNMP Criteria: " + e.getMessage());
        } catch (Throwable t) {
            status = logDown(Level.WARN, "Unexpected exception during SNMP poll of interface " + hostAddress, t);
        }

        return status;
    }

}
