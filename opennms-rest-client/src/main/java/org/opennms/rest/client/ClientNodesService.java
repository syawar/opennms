package org.opennms.rest.client;

import javax.ws.rs.core.MultivaluedMap;

import org.opennms.rest.model.ClientOnmsNode;
import org.opennms.rest.model.ClientOnmsNodeList;

public interface ClientNodesService extends RestFilterService{


    public ClientOnmsNodeList getAll();

    public ClientOnmsNodeList getWithDefaultsQueryParams();

    public ClientOnmsNodeList find(MultivaluedMap<String, String> queryParams);

    public ClientOnmsNode get(Integer id);
    
    

}
