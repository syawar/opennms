package org.opennms.rest.client;

import javax.ws.rs.core.MultivaluedMap;

import org.opennms.netmgt.model.DataLinkInterface;
import org.opennms.netmgt.model.DataLinkInterfaceList;

public interface DataLinkInterfaceService extends RestFilterService{

    public int countAll();

    public DataLinkInterfaceList getAll();

    public DataLinkInterfaceList getWithDefaultsQueryParams();

    public DataLinkInterfaceList find(MultivaluedMap<String, String> queryParams);

    public DataLinkInterface get(Integer id);
    
    

}
