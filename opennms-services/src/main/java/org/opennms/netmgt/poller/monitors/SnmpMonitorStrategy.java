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

import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Pattern;

import org.opennms.netmgt.model.PollStatus;
import org.opennms.netmgt.poller.MonitoredService;
import org.opennms.netmgt.snmp.SnmpValue;

/**
 * <p>Abstract SnmpMonitorStrategy class.</p>
 *
 * @author david
 * @version $Id: $
 */
abstract public class SnmpMonitorStrategy extends AbstractServiceMonitor {
	
	/**
	 * This pattern to use assets.Token replaced when polling.
	 * 
	 * @see #checkAssetMatch(String)
	 */
	protected static final String ASSET_PATTERN="USE_ASSET";
    /**
     * Constant for less-than operand
     */
    protected static final String LESS_THAN = "<";
    /** Constant <code>GREATER_THAN=">"</code> */
    protected static final String GREATER_THAN = ">";
    /** Constant <code>LESS_THAN_EQUALS="<="</code> */
    protected static final String LESS_THAN_EQUALS = "<=";
    /** Constant <code>GREATER_THAN_EQUALS=">="</code> */
    protected static final String GREATER_THAN_EQUALS = ">=";
    /** Constant <code>EQUALS="="</code> */
    protected static final String EQUALS = "=";
    /** Constant <code>NOT_EQUAL="!="</code> */
    protected static final String NOT_EQUAL = "!=";
    /** Constant <code>MATCHES="~"</code> */
    protected static final String MATCHES = "~";
    
    boolean hex = false;
    /*
     * TODO: Use it or loose it.
     * Commented out because it is not currently used in this monitor
     */
    //private Category log = ThreadCategory.getInstance(getClass());

    /** {@inheritDoc} */
    abstract public PollStatus poll(MonitoredService svc, Map<String, Object> parameters) ;
    

    public String getStringValue(SnmpValue result) {
    	if (hex){
    		log().debug(this+"::syawar::The result is hex...converting to string::"+result.toHexString());
    		return result.toHexString();
    	}
    	return result.toString();
    }
    /**
     * Verifies that the result of the SNMP query meets the criteria specified
     * by the operator and the operand from the configuration file.
     *
     * @param result a {@link org.opennms.netmgt.snmp.SnmpValue} object.
     * @param operator a {@link java.lang.String} object.
     * @param operand a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean meetsCriteria(SnmpValue result, String operator, String operand) {

        Boolean retVal = null;
        log().debug(this+"::syawar::the result::"+getStringValue(result)+"::comparator::"+operator+"::operand::"+operand);
        
        retVal = isCriteriaNull(result, operator, operand);
        log().debug(this+"::syawar::is criteria null::"+retVal);
        if (retVal == null) {
        	String value = getStringValue(result);
            retVal = checkStringCriteria(operator, operand, value);
            log().debug(this+"::syawar::the retval::"+retVal);
            if (retVal == null) {
                
                BigInteger val = BigInteger.valueOf(result.toLong());
                
                BigInteger intOperand = new BigInteger(operand);
                if (LESS_THAN.equals(operator)) {
                    return val.compareTo(intOperand) < 0;
                } else if (LESS_THAN_EQUALS.equals(operator)) {
                    return val.compareTo(intOperand) <= 0;
                } else if (GREATER_THAN.equals(operator)) {
                    return val.compareTo(intOperand) > 0;
                } else if (GREATER_THAN_EQUALS.equals(operator)) {
                    return val.compareTo(intOperand) >= 0;
                } else {
                	log().debug(this+"::syawar::illegal argument exception");
                    throw new IllegalArgumentException("operator " + operator + " is unknown");
                }
            }
        } else if (retVal.booleanValue()) {
            return true;
        }
        log().debug(this+"::syawar::final::"+retVal);
        return retVal.booleanValue();
    }

    /**
     * @param operator
     * @param operand
     * @param retVal
     * @param value
     * @return
     */
    private Boolean checkStringCriteria(final String operator, String operand, String value) {
        Boolean retVal = null;
        
        if (value == null) {
            value = "";
        } else if (value.startsWith(".")) {
            value = value.substring(1);
        }
        
        // Bug 2178 -- if this is a regex match, a leading "." in the operand
        // should not be stripped
        if (operand.startsWith(".") && !MATCHES.equals(operator)) {
            operand = operand.substring(1);
        }
        
        if (EQUALS.equals(operator))
            retVal = Boolean.valueOf(operand.equals(value));
        else if (NOT_EQUAL.equals(operator))
            retVal = Boolean.valueOf(!operand.equals(value));
        else if (MATCHES.equals(operator))
            retVal = Boolean.valueOf(Pattern.compile(operand).matcher(value).find());
        return retVal;
    }

    /**
     * @param result
     * @param operator
     * @param operand
     * @param retVal
     * @return
     */
    private Boolean isCriteriaNull(Object result, String operator, String operand) {
        
        if (result == null)
            return Boolean.FALSE;
        if (operator == null || operand == null) {
            return Boolean.TRUE;
        } else {
            return null;
        }
    }
    
    /**
     * check to see if the string is to be taken from the assets
     * @param theValue
     * @return true if config matches asset pattern
     */
    private boolean checkAssetMatch(String theValue){
    	if (theValue == null){
    		log().debug(this+"::asset value null...");
    		return false;
    	}
    	if(theValue.equalsIgnoreCase(ASSET_PATTERN)){
    		log().debug(this+"::Using Asset for value...");
    		return true;
    	}
    	else{
    		return false;
    	}
    	
    }
    
    /**
     * check and assign asset value
     * @param currentValue the value from the config
     * @param assetValue the value from the assets
     * @return the resolved value
     */
    public String getStringAsset(String currentValue, String assetValue){
    	log().debug(this+"::Checking asset value...");
    	if(currentValue != null && assetValue !=null){
    		if(checkAssetMatch(currentValue) && !"".equals(assetValue) && !assetValue.isEmpty()){
        		currentValue = assetValue.trim();
        		log().debug(this+"::asset value::"+currentValue+"::");
        		return assetValue;
        	}
    	}
    	else{
    		//if asset field not declared
    		if(currentValue != null && checkAssetMatch(currentValue)){
    			log().debug(this+"::Asset value not defined...will result in improper monitoring");
    			currentValue = null;
    		}
    	}
    	
    	log().debug(this+"::asset value::"+currentValue);
    	return currentValue;
    }
    
    
    /**
     * convert given hex mac address to mib oid
     * @param macAddress hex mac address
     * @return a {java.lang.String} object
     */
    public String convertMacToOid(String macAddress){
    	String oid = "";
    	if(macAddress != null && !"".equals(macAddress) && !macAddress.isEmpty()){
    		log().debug("Converting macAddress::"+macAddress+"to OID");
    		for(String hex: macAddress.split(":")){
    			int dec = Integer.parseInt(hex,16);
    			oid = oid +"."+ Integer.toString(dec);
    		}
    	}else{
    		log().debug(this+"::No Mac Address provided for conversion to oid...");
    	}
    	
    	
    	return oid;
    }

}
