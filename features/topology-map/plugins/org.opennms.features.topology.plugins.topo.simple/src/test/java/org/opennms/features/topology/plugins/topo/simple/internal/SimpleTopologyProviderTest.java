/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
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

package org.opennms.features.topology.plugins.topo.simple.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opennms.features.topology.api.Constants;
import org.opennms.features.topology.api.Graph;
import org.opennms.features.topology.api.GraphContainer;
import org.opennms.features.topology.api.LayoutAlgorithm;
import org.opennms.features.topology.api.OperationContext;
import org.opennms.features.topology.api.SelectionManager;
import org.opennms.features.topology.api.TopologyProvider;
import org.opennms.features.topology.api.VertexContainer;
import org.opennms.features.topology.api.topo.GraphProvider;
import org.opennms.features.topology.plugins.topo.simple.internal.operations.AddVertexOperation;
import org.opennms.features.topology.plugins.topo.simple.internal.operations.ConnectOperation;
import org.opennms.features.topology.plugins.topo.simple.internal.operations.RemoveVertexOperation;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Window;

public class SimpleTopologyProviderTest {
    
    public class TestVertex {

    }

    
    private class TestGraphContainer implements GraphContainer{
        
        
        private SimpleVertexContainer m_vertContainer;

        public TestGraphContainer(SimpleVertexContainer vertContainer) {
            m_vertContainer = vertContainer;
        }
        
        @Override
        public Integer getSemanticZoomLevel() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setSemanticZoomLevel(Integer level) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setLayoutAlgorithm(LayoutAlgorithm layoutAlgorithm) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public LayoutAlgorithm getLayoutAlgorithm() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void redoLayout() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Property getProperty(String property) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VertexContainer<?, ?> getVertexContainer() {
            return m_vertContainer;
        }

        @Override
        public BeanContainer<?, ?> getEdgeContainer() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Item getVertexItem(Object vertexId) {
            return m_vertContainer.getItem(vertexId);
        }

        @Override
        public Item getEdgeItem(Object edgeId) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Collection<?> getEndPointIdsForEdge(Object edgeId) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Collection<?> getEdgeIdsForVertex(Object vertexId) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getVertexItemIdForVertexKey(Object key) {
            Item vertexItem = getVertexItem(key);
            return vertexItem == null ? null : vertexItem.getItemProperty("itemId").getValue();
        }

	@Override
	public TopologyProvider getDataSource() {
		throw new UnsupportedOperationException("GraphContainer.getDataSource is not yet implemented.");
	}

	@Override
	public void setDataSource(TopologyProvider topologyProvider) {
		throw new UnsupportedOperationException("GraphContainer.setDataSource is not yet implemented.");
	}

	@Override
	public GraphProvider getBaseTopology() {
		throw new UnsupportedOperationException("GraphContainer.getBaseTopology is not yet implemented.");
	}

	@Override
	public void setBaseTopology(GraphProvider graphProvider) {
		throw new UnsupportedOperationException("GraphContainer.setBaseTopology is not yet implemented.");
	}

	@Override
	public int getX(Object vertexId) {
		throw new UnsupportedOperationException("GraphContainer.getX is not yet implemented.");
	}

	@Override
	public void setX(Object vertexId, int x) {
		throw new UnsupportedOperationException("GraphContainer.setX is not yet implemented.");
	}

	@Override
	public int getY(Object vertexId) {
		throw new UnsupportedOperationException("GraphContainer.getY is not yet implemented.");
	}

	@Override
	public void setY(Object vertexId, int y) {
		throw new UnsupportedOperationException("GraphContainer.setY is not yet implemented.");
	}

	@Override
	public int getSemanticZoomLevel(Object vertexId) {
		throw new UnsupportedOperationException("GraphContainer.getSemanticZoomLevel is not yet implemented.");
	}

	@Override
	public Object getGroupId(Object vertexId) {
		throw new UnsupportedOperationException("GraphContainer.getGroupId is not yet implemented.");
	}

	@Override
	public Object getDisplayVertexId(Object vertexId, int semanticZoomLevel) {
		throw new UnsupportedOperationException("GraphContainer.getDisplayVertexId is not yet implemented.");
	}

	@Override
	public Collection<Object> getDisplayVertexIds(int semanticZoomLevel) {
		throw new UnsupportedOperationException("GraphContainer.getDisplayVertices is not yet implemented.");
	}

	@Override
	public Collection<?> getVertexIds() {
		throw new UnsupportedOperationException("GraphContainer.getVertexIds is not yet implemented.");
	}

	@Override
	public boolean hasChildren(Object itemId) {
		throw new UnsupportedOperationException("GraphContainer.hasChildren is not yet implemented.");
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
		throw new UnsupportedOperationException("GraphContainer.getChildren is not yet implemented.");
	}

	@Override
	public Object getParentId(Object itemId) {
		throw new UnsupportedOperationException("GraphContainer.getParentId is not yet implemented.");
	}

	@Override
	public boolean containsVertexId(Object vertexId) {
		return getVertexContainer().containsId(vertexId);
	}

	@Override
	public boolean containsEdgeId(Object edgeId) {
		throw new UnsupportedOperationException("GraphContainer.containsEdgeId is not yet implemented.");
	}

	@Override
	public SelectionManager getSelectionManager() {
		throw new UnsupportedOperationException("GraphContainer.getSelectionManager is not yet implemented.");
	}

	@Override
	public Collection<?> getVertexForest(Collection<?> vertexIds) {
		throw new UnsupportedOperationException("GraphContainer.getVertexForest is not yet implemented.");
	}

	@Override
	public Graph getGraph() {
		throw new UnsupportedOperationException("GraphContainer.getGraph is not yet implemented.");
	}

	@Override
	public Graph getCompleteGraph() {
		throw new UnsupportedOperationException("GraphContainer.getCompleteGraph is not yet implemented.");
	}
        
    }
    
    private class TestOperationContext implements OperationContext{
        
        private GraphContainer m_graphContainer;

        public TestOperationContext(GraphContainer graphContainer) {
            m_graphContainer = graphContainer;
        }
        
        @Override
        public Window getMainWindow() {
            return EasyMock.createMock(Window.class);
        }

        @Override
        public GraphContainer getGraphContainer() {
            return m_graphContainer;
        }

        @Override
        public boolean isChecked() {
            // TODO Auto-generated method stub
            return false;
        }
        
    }
    
    private SimpleTopologyProvider m_topologyProvider;
    
    @Before
    public void setUp() {
        if(m_topologyProvider == null) {
            m_topologyProvider = new SimpleTopologyProvider();
        }
        
		m_topologyProvider.resetContainer();
		

    }
    
    @After
    public void tearDown() {
        if(m_topologyProvider != null) {
            m_topologyProvider.resetContainer();
        }
    }
    
	@Test
	public void test() {
		SimpleTopologyProvider topologyProvider = new SimpleTopologyProvider();
		topologyProvider.resetContainer();
		
		String vertexA = (String) topologyProvider.addVertex(50, 100);
		String vertexB = (String) topologyProvider.addVertex(100, 50);
		String vertexC = (String) topologyProvider.addVertex(100, 150);
		String vertexD = (String) topologyProvider.addVertex(150, 100);
		String vertexE = (String) topologyProvider.addVertex(200, 200);
		String group1 = (String) topologyProvider.addGroup("Group 1", Constants.GROUP_ICON_KEY);
		String group2 = (String) topologyProvider.addGroup("Group 2", Constants.GROUP_ICON_KEY);
		topologyProvider.getVertexContainer().setParent(vertexA, group1);
		topologyProvider.getVertexContainer().setParent(vertexB, group1);
		topologyProvider.getVertexContainer().setParent(vertexC, group2);
		topologyProvider.getVertexContainer().setParent(vertexD, group2);
		
		topologyProvider.connectVertices(vertexA, vertexB);
		topologyProvider.connectVertices(vertexA, vertexC);
		topologyProvider.connectVertices(vertexB, vertexC);
		topologyProvider.connectVertices(vertexB, vertexD);
		topologyProvider.connectVertices(vertexC, vertexD);
		topologyProvider.connectVertices(vertexA, vertexE);
		topologyProvider.connectVertices(vertexD, vertexE);
		
		topologyProvider.save("target/test-classes/test-graph.xml");
		
		topologyProvider.load("target/test-classes/test-graph.xml");
		
	}
	
	@Test
	public void loadSampleGraph() {
		SimpleTopologyProvider topologyProvider = new SimpleTopologyProvider();
		topologyProvider.load("saved-vmware-graph.xml");
		
		System.err.println("Vertex Count: " + topologyProvider.getVertexIds().size());
		System.err.println("Edge Count: " + topologyProvider.getEdgeIds().size());
	}
	
	@Test
	public void testAddVertexWithOperation() {
	    
	    List<Object> targets = Collections.emptyList();
	    OperationContext operationContext = getOperationContext(new TestGraphContainer(new SimpleVertexContainer()));
	    
	    AddVertexOperation addOperation = new AddVertexOperation(Constants.GROUP_ICON_KEY, m_topologyProvider);
	    addOperation.execute(targets, operationContext);
	    
	    Collection<?> vertIds =  m_topologyProvider.getVertexIds();
	    assertEquals(1, vertIds.size());
	    assertTrue(vertIds.contains("v0"));
	}

	@Test
	public void testAddVertexToAnotherVertexOperation() {

		m_topologyProvider.resetContainer();

		//Add existing vertex
        Object vertexId = addVertexToTopr();
	    
	    GraphContainer graphContainer = EasyMock.createMock(GraphContainer.class);
	    
	    EasyMock.expect(graphContainer.getVertexItemIdForVertexKey("1")).andReturn(vertexId);
	    graphContainer.redoLayout();
	    
	    EasyMock.replay(graphContainer);
	    
	    
	    List<Object> targets = new ArrayList<Object>();
	    targets.add("1");
	    
	    OperationContext operationContext = getOperationContext(graphContainer);
	    AddVertexOperation addOperation = new AddVertexOperation(Constants.SERVER_ICON_KEY, m_topologyProvider);
        addOperation.execute(targets, operationContext);
	    
        Collection<?> vertIds = m_topologyProvider.getVertexIds();
        assertEquals(2, vertIds.size());
        
        Collection<?> edgeIds = m_topologyProvider.getEdgeIds();
        assertEquals(1, edgeIds.size());
        
        EasyMock.verify(graphContainer);
	    
	}
	
    @Test
	public void testConnectVertices() {
		m_topologyProvider.resetContainer();

		Object vertexId = m_topologyProvider.addVertex(0, 0);
        
        assertEquals(1, m_topologyProvider.getVertexIds().size());
        Object vertId = m_topologyProvider.getVertexIds().iterator().next();
        assertEquals("v0", vertId);
        
        m_topologyProvider.addVertex(0, 0);
        assertEquals(2, m_topologyProvider.getVertexIds().size());
        
        Object edgeId = m_topologyProvider.connectVertices("v0", "v1");
        assertEquals(1, m_topologyProvider.getEdgeIds().size());
        SimpleLeafVertex sourceLeafVert = (SimpleLeafVertex) m_topologyProvider.getEdgeItem(edgeId).getItemProperty("source").getValue();
        SimpleLeafVertex targetLeafVert = (SimpleLeafVertex) m_topologyProvider.getEdgeItem(edgeId).getItemProperty("target").getValue();
        
        assertEquals("v0", sourceLeafVert.getId());
        assertEquals("v1", targetLeafVert.getId());
        
        Collection<?> edgeIds = m_topologyProvider.getEdgeIdsForVertex(vertexId);
        assertEquals(1, edgeIds.size());
        assertEquals(edgeId, edgeIds.iterator().next());
        
	}
    
    @Test
    public void testRemoveVertexOperation() {
		m_topologyProvider.resetContainer();

    	
        GraphContainer graphContainer = EasyMock.createMock(GraphContainer.class);
        
        Object vertexId = addVertexToTopr();
        
        OperationContext operationContext = getOperationContext(graphContainer);
        RemoveVertexOperation removeOperation = new RemoveVertexOperation(m_topologyProvider);
        removeOperation.execute(Arrays.asList(vertexId), operationContext);
        
        assertEquals(0, m_topologyProvider.getVertexIds().size());
        
    }
    
    /**
     * TODO Refactor this test into the app bundle.
     */
    @Test
    @Ignore("Since this operation is now interactive, we need to change this unit test")
    public void testCreateGroupOperation() {
        Object vertexId = addVertexToTopr();
        Object vertexId2 = addVertexToTopr();
        
        GraphContainer graphContainer = EasyMock.createMock(GraphContainer.class);
        
        EasyMock.expect(graphContainer.getVertexItemIdForVertexKey("1")).andReturn(vertexId);
        EasyMock.expect(graphContainer.getVertexItemIdForVertexKey("2")).andReturn(vertexId2);
        
        EasyMock.replay(graphContainer);
        
        /*
        CreateGroupOperation groupOperation = new CreateGroupOperation(m_topologyProvider);
        groupOperation.execute(Arrays.asList((Object)"1", (Object)"2"), getOperationContext(graphContainer));
        
        Item vertexItem1 = m_topologyProvider.getVertexItem(vertexId);
        SimpleGroup parent = (SimpleGroup) vertexItem1.getItemProperty("parent").getValue();
        assertEquals(2, parent.getMembers().size());
        
        m_topologyProvider.addGroup("Test Group", Constants.GROUP_ICON_KEY);
        
        EasyMock.verify(graphContainer);
        */
    }
    
    @Test
    public void testTopoProviderSetParent() {
        Object vertexId1 = addVertexToTopr();
        Object vertexId2 = addVertexToTopr();
        
        final AtomicInteger eventsReceived = new AtomicInteger(0);
        
        m_topologyProvider.getVertexContainer().addListener(new ItemSetChangeListener() {
            
            @Override
            public void containerItemSetChange(ItemSetChangeEvent event) {
                eventsReceived.incrementAndGet();
            }
        });
        
        Object groupId = m_topologyProvider.addGroup("Test Group", "groupIcon.jpg");
        assertEquals(1, eventsReceived.get());
        eventsReceived.set(0);
        
        m_topologyProvider.setParent(vertexId1, groupId);
        m_topologyProvider.setParent(vertexId2, groupId);
        
        assertEquals(2, eventsReceived.get());
    }
    
    @Test
    public void testConnectVerticesOperation() {
    	
		m_topologyProvider.resetContainer();

        Object vertexId1 = addVertexToTopr();
        Object vertexId2 = addVertexToTopr();
        
        GraphContainer graphContainer = EasyMock.createMock(GraphContainer.class);
        
        EasyMock.expect(graphContainer.getVertexItemIdForVertexKey("1")).andReturn(vertexId1);
        EasyMock.expect(graphContainer.getVertexItemIdForVertexKey("2")).andReturn(vertexId2);
        
        EasyMock.replay(graphContainer);
        
        List<Object> targets = new ArrayList<Object>();
        targets.add("1");
        targets.add("2");
        
        ConnectOperation connectOperation = new ConnectOperation(m_topologyProvider);
        connectOperation.execute(targets, getOperationContext(graphContainer));
        
        Collection<?> edgeIds = m_topologyProvider.getEdgeIds();
        assertEquals(1, edgeIds.size());
        
        for(Object edgeId : edgeIds) {
            Item edgeItem = m_topologyProvider.getEdgeItem(edgeId);
            SimpleLeafVertex source = (SimpleLeafVertex) edgeItem.getItemProperty("source").getValue();
            SimpleLeafVertex target = (SimpleLeafVertex) edgeItem.getItemProperty("target").getValue();
            assertNotNull(source);
            assertNotNull(target);
            assertEquals(vertexId1, source.getId());
            assertEquals(vertexId2, target.getId());
        }
        
        EasyMock.verify(graphContainer);
    }
	
	
	private TestOperationContext getOperationContext(GraphContainer mockedContainer) {
        return new TestOperationContext(mockedContainer);
    }
	
	private Object addVertexToTopr() {
	    return m_topologyProvider.addVertex(0, 0);
    }

}
