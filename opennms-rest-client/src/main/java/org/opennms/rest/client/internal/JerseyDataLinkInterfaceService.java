package org.opennms.rest.client.internal;

import javax.ws.rs.core.MultivaluedMap;

import org.opennms.netmgt.model.DataLinkInterface;
import org.opennms.netmgt.model.DataLinkInterfaceList;
import org.opennms.rest.client.DataLinkInterfaceService;

public class JerseyDataLinkInterfaceService extends JerseyAbstractService implements DataLinkInterfaceService {

    private final static String LINK_REST_PATH = "links/";

    private JerseyClientImpl m_jerseyClient;
        
    public JerseyClientImpl getJerseyClient() {
        return m_jerseyClient;
    }

    public void setJerseyClient(JerseyClientImpl jerseyClient) {
        m_jerseyClient = jerseyClient;
    }

    public DataLinkInterfaceList getAll() {
    	MultivaluedMap<String, String> queryParams = setLimit(0);
        return getJerseyClient().get(DataLinkInterfaceList.class, LINK_REST_PATH,queryParams);                
    }
 
    public DataLinkInterfaceList find(MultivaluedMap<String, String> queryParams) {
        return getJerseyClient().get(DataLinkInterfaceList.class, LINK_REST_PATH,queryParams);                
    }

    public DataLinkInterface get(Integer id) {
        return getJerseyClient().get(DataLinkInterface.class, LINK_REST_PATH+id);
    }
 
	public int countAll() {
		return Integer.parseInt(getJerseyClient().get(LINK_REST_PATH+"count"));
	}

	public DataLinkInterfaceList getWithDefaultsQueryParams() {
        return getJerseyClient().get(DataLinkInterfaceList.class, LINK_REST_PATH);                
	}
}
