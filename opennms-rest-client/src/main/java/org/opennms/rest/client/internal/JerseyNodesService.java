package org.opennms.rest.client.internal;

import javax.ws.rs.core.MultivaluedMap;

import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsNodeList;
import org.opennms.rest.client.NodesService;

public class JerseyNodesService extends JerseyAbstractService implements NodesService {

    private final static String NODES_REST_PATH = "nodes/";

    public OnmsNodeList getAll() {
    	MultivaluedMap<String, String> queryParams = setLimit(0);
        return getJerseyClient().get(OnmsNodeList.class, NODES_REST_PATH,queryParams);                
    }
 
    public OnmsNodeList find(MultivaluedMap<String, String> queryParams) {
        return getJerseyClient().get(OnmsNodeList.class, NODES_REST_PATH,queryParams);                
    }

    public OnmsNode get(Integer id) {
        return getJerseyClient().get(OnmsNode.class, NODES_REST_PATH+id);
    }
 

	public OnmsNodeList getWithDefaultsQueryParams() {
        return getJerseyClient().get(OnmsNodeList.class, NODES_REST_PATH);                
	}
}