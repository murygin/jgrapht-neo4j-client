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
 *     Daniel Murygin daniel.murygin@gmail.com - initial API and implementation
 ******************************************************************************/
package org.murygin.neo4j;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a Cypher on a Neo4j server and returns result as JGraphT graph object.
 * 
 * Getting started:
 * 
 * CypherToJGraphT graphLoader = new CypherToJGraphT();
 * graphLoader.setHost("NEO4J_HOST_NAME");
 * DirectedGraph graph = graphLoader.execute("MATCH n-[r]-() RETURN n,r");
 * 
 * Queries are executed by class {@link CypherToJson}.
 * 
 * See: http://neo4j.com/
 * See: http://neo4j.com/docs/stable/cypher-query-lang.html
 * See: http://jgrapht.org/
 * 
 * @author Daniel Murygin daniel.murygin@gmail.com
 */
public class CypherToJGraphT {

    final Logger LOG = LoggerFactory.getLogger(CypherToJGraphT.class);
    
    private String protocol = CypherToJson.DEFAULT_PROTOCOL;
    private String host = CypherToJson.DEFAULT_HOST;
    private String port = CypherToJson.DEFAULT_PORT;
    private String path = CypherToJson.DEFAULT_PATH;
    private String query = CypherToJson.DEFAULT_QUERY;

    private DirectedGraph<Node, Edge> graph;
    private Map<String, Node> nodeMap;
       
    public CypherToJGraphT() {
        super();
        graph = new DirectedPseudograph<Node, Edge>(Edge.class);
    }

    public static void main(String[] args) throws Exception {
        CypherToJGraphT executer = new CypherToJGraphT();
        executer.execute();
    }
    
    /**
     * Executes a Cypher query.
     * 
     * @param query a Cypher query
     * @return The result of the query as JGraphT graph
     */
    public DirectedGraph<Node, Edge> execute(String query) {
        setQuery(query);
        execute();
        return getGraph();
    }
    
    /**
     * Executes a Cypher query.
     * Call setQuery(..) before to set the query. 
     * Call getGraph() to return the resukt.
     */
    public void execute() {
        nodeMap = new Hashtable<String, Node>();
        CypherToJson executer = createCypherToJson();
        executer.execute();
        JSONObject json = executer.getJson();
        convertJsonToGraph(json);
        nodeMap.clear();
    }

    private void convertJsonToGraph(JSONObject json) {
        JsonResults jsonResults = new JsonResults(json);
        List<JSONObject> graphList = jsonResults.getGraphList();     
        for (JSONObject graphJson : graphList) {
            JSONArray nodes = JsonResults.getJsonArray(graphJson, "nodes");
            addNotes(nodes);
            JSONArray relations = JsonResults.getJsonArray(graphJson, "relationships");
            addRelations(relations);
        }
    } 

    private void addNotes(JSONArray nodes) {
        for (Object nodeJson : nodes) {
            Node node = addNode((JSONObject) nodeJson);
            if(node!=null) {
                nodeMap.put(node.getId(), node);
            }
        }
    }

    private Node addNode(JSONObject nodeJson) {
        String id = (String)nodeJson.get("id");
        Node node = new Node(id);
        if(!this.graph.containsVertex(node)) {
            addLabels(node, nodeJson);
            addProperties(node, nodeJson);
            boolean added = this.graph.addVertex(node);
            logNode(node, added);
            return node;
        }
        return null;
    }
    
    private void addRelations(JSONArray relations) {
        for (Object relationObject : relations) {
            JSONObject relation = (JSONObject) relationObject;
            addRelation(relation);
        }
    }

    private void addRelation(JSONObject relation) {
        String sourceodeId = (String) relation.get("startNode");
        String targetNodeId = (String) relation.get("endNode");
        Node sourceNode = nodeMap.get(sourceodeId);
        Node targetNode = nodeMap.get(targetNodeId);
        if(sourceNode!=null && targetNode!=null) {
            String type = (String) relation.get("type");
            Edge edge = new Edge(sourceNode, targetNode, type);
            addProperties(edge, relation);
            boolean added = this.graph.addEdge(sourceNode, targetNode, edge);  
            logEdge(edge, added);
        }
    }

    @SuppressWarnings("rawtypes")
    private void addProperties(IPropertyContainer node, JSONObject nodeJson) {
        JSONObject properties = JsonResults.getJson(nodeJson, "properties");
        Set keys = properties.keySet();
        for (Object keyObject : keys) {
            String key = (String) keyObject;
            String value = (String) properties.get(key);
            node.addProperty(key, value);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Property added: " + key + ":" + value);
            }
        }
    }

    private void addLabels(Node node, JSONObject nodeJson) {
        JSONArray labels = JsonResults.getJsonArray(nodeJson, "labels");
        for (Object labelObject : labels) {
            String label = (String) labelObject;                
            node.addLabel(label);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Label added: " + label);
            }
        }
    }

    private CypherToJson createCypherToJson() {
        CypherToJson executer = new CypherToJson();
        executer.setHost(getHost());
        executer.setPath(getPath());
        executer.setPort(getPort());
        executer.setProtocol(getProtocol());
        executer.setQuery(getQuery());
        executer.setResultDataContents(CypherToJson.RESULT_DATA_CONTENT_GRAPH);
        return executer;
    }
    
    /**
     * @return The result of a query as JGraphT graph
     */
    public DirectedGraph<Node, Edge> getGraph() {
        return graph;
    }
    
    private void logNode(Node node, boolean added) {
        if (LOG.isInfoEnabled()) {
            if(added) {
                LOG.info("Node added: " + node);
            } else {
                LOG.debug("Node exists: " + node);
            }
        }
    }
    
    private void logEdge(Edge edge, boolean added) {
        if (LOG.isInfoEnabled()) {
            if(added) {
                LOG.info("Edge added: " + edge);
            } else {
                LOG.debug("Edge exists: " + edge);
            }
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
