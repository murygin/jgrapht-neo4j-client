/*******************************************************************************
 * Copyright (c) 2015 Daniel Murygin.
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Daniel Murygin <dm[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package org.murygin.neo4j.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.junit.Test;
import org.murygin.neo4j.CypherToJGraphT;
import org.murygin.neo4j.CypherToJson;
import org.murygin.neo4j.Edge;
import org.murygin.neo4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
public class CypherToJGraphTTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(CypherToJGraphTTest.class);
    
    public static final String CREATE_STATION = "CREATE (c:station { name : '%s' }) RETURN c";
    public static final String CREATE_RELATION = "MATCH (a:station),(b:station) WHERE a.name = '%s' AND b.name = '%s' CREATE (a)-[r:train]->(b) RETURN r";
    public static final String DELETE_STATION = "MATCH (c:station)-[r]-() DELETE c,r";
    public static final String LOAD_GRAPH = "MATCH (n:station)-[r:train]-() RETURN n,r";
    
    @Test
    public void testCreate() {
        CypherToJson executer = null;
        try {
            executer = createGraph();           
            CypherToJGraphT graphLoader = new CypherToJGraphT();
            DirectedGraph<Node, Edge> graph = graphLoader.execute(LOAD_GRAPH);
            Set<Node> nodeSet = graph.vertexSet();
            assertTrue("Not exactly 11 nodes in graph", nodeSet.size()==11);     
            assertTrue("Not exactly 22 edges in graph", graph.edgeSet().size()==22);
        } finally {
            deleteGraph(executer);
        }
    }
    
    @Test
    public void testShortestPath() {
        CypherToJson executer = null;
        try {
            executer = createGraph();           
            CypherToJGraphT graphLoader = new CypherToJGraphT();
            DirectedGraph<Node, Edge> graph = graphLoader.execute(LOAD_GRAPH);
            Set<Node> nodeSet = graph.vertexSet(); 
            
            Node start = getNodeByName(nodeSet, "name", "Ostbahnhof");
            Node destination = getNodeByName(nodeSet, "name", "Klosterstrasse");
            Iterator<String> path = Arrays.asList(new String[]{"Ostbahnhof","Jannowitzbrücke","Alexanderplatz","Klosterstrasse"}).iterator();         
            checkShortestPath(graph, start, destination, path);
            
            start = getNodeByName(nodeSet, "name", "Hallesches Tor");
            destination = getNodeByName(nodeSet, "name", "Senefelder Platz");
            path = Arrays.asList(new String[]{"Hallesches Tor","Friedrichstrasse","Hackescher Markt","Alexanderplatz","Rosa-Luxenburg-Platz","Senefelder Platz"}).iterator();         
            checkShortestPath(graph, start, destination, path);
        } finally {
            deleteGraph(executer);
        }
    }



    private void checkShortestPath(DirectedGraph<Node, Edge> graph, Node start, Node destination, Iterator<String> path) {
        List<Edge> shortestPath = DijkstraShortestPath.findPathBetween(graph, start, destination);

        String station = path.next();
        if (LOG.isDebugEnabled()) {
            LOG.debug(station);
        }
        assertTrue("Wrong station in path: " + start.getProperty("name"),station.equals(start.getProperty("name")) );
        for (Edge edge : shortestPath) {
            station = path.next();
            String currentStation = edge.getTargetNode().getProperty("name");
            if (LOG.isDebugEnabled()) {
                LOG.debug(currentStation);
            }
            assertTrue("Wrong station in path: " + currentStation,station.equals(currentStation) );
        }
    }



    private Node getNodeByName(Set<Node> nodeSet, String key, String value) {
        Node selectedNode = null;
        for (Node node : nodeSet) {
            if(value.equals(node.getProperty(key))) {
                selectedNode = node;
                break;
            }       
        }
        return selectedNode;
    }

    private CypherToJson createGraph() {
        CypherToJson executer = new CypherToJson();
        executer.execute(String.format(CREATE_STATION, "Alexanderplatz"));
        executer.execute(String.format(CREATE_STATION, "Jannowitzbrücke"));
        executer.execute(String.format(CREATE_STATION, "Hackescher Markt"));
        executer.execute(String.format(CREATE_STATION, "Friedrichstrasse"));
        executer.execute(String.format(CREATE_STATION, "Hallesches Tor"));
        executer.execute(String.format(CREATE_STATION, "Rosa-Luxenburg-Platz"));
        executer.execute(String.format(CREATE_STATION, "Klosterstrasse"));
        executer.execute(String.format(CREATE_STATION, "Senefelder Platz"));
        executer.execute(String.format(CREATE_STATION, "Ostbahnhof"));
        executer.execute(String.format(CREATE_STATION, "Warschauer Strasse"));
        executer.execute(String.format(CREATE_STATION, "Schlesisches Tor"));
        createRelations("Alexanderplatz", "Jannowitzbrücke", executer); 
        createRelations("Alexanderplatz", "Hackescher Markt", executer); 
        createRelations("Hackescher Markt", "Friedrichstrasse", executer); 
        createRelations("Friedrichstrasse", "Hallesches Tor", executer); 
        createRelations("Hallesches Tor", "Schlesisches Tor", executer); 
        createRelations("Alexanderplatz", "Rosa-Luxenburg-Platz", executer); 
        createRelations("Alexanderplatz", "Klosterstrasse", executer); 
        createRelations("Rosa-Luxenburg-Platz", "Senefelder Platz", executer); 
        createRelations("Jannowitzbrücke", "Ostbahnhof", executer);  
        createRelations("Ostbahnhof", "Warschauer Strasse", executer); 
        createRelations("Warschauer Strasse", "Schlesisches Tor", executer);
        return executer;
    }
    
    private void deleteGraph(CypherToJson executer) {
        executer.execute(DELETE_STATION);
    }

    private void createRelations(String station1, String station2, CypherToJson executer) {
        executer.execute(String.format(CREATE_RELATION, station1, station2));  
        executer.execute(String.format(CREATE_RELATION, station2, station1));
    }
}