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
package org.jgrapht.neo4j;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Executes a Cypher on a Neo4j server and returns result as JSON string or 
 * json-simple object.
 * 
 * @see http://neo4j.com/
 * @see http://neo4j.com/docs/stable/cypher-query-lang.html
 * @see https://code.google.com/p/json-simple/
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
public class CypherToJson {

    final Logger LOG = LoggerFactory.getLogger(CypherToJson.class);
    
    public static String HTTPS_PROTOCOL = "http";
    public static String HTTP_PROTOCOL = "http";
    public static String DEFAULT_PROTOCOL = HTTP_PROTOCOL;
    public static String DEFAULT_HOST = "localhost";
    public static String DEFAULT_PORT = "7474";
    public static String DEFAULT_PATH = "/db/data/transaction/commit";
    public static String DEFAULT_QUERY = "MATCH n-[r]-() RETURN n,r";

    public String protocol = DEFAULT_PROTOCOL;
    public String host = DEFAULT_HOST;
    public String port = DEFAULT_PORT;
    public String path = DEFAULT_PATH;

    public String query = DEFAULT_QUERY;
    
    private String jsonString;
    private JSONObject json;

    public static void main(String[] args) throws Exception {
        CypherToJson executer = new CypherToJson();
        executer.execute();
    }
    
    public void execute(String query) {
        setQuery(query);
        execute();
    }

    public void execute() {
        ClientResponse response = excecuteRequest();
        setJsonString(response.getEntity(String.class));     
        response.close();
        if (LOG.isDebugEnabled()) {
            LOG.debug(getJsonString());
        }      
        JSONParser parser = new JSONParser();
        try {
            setJson((JSONObject) parser.parse(getJsonString()));
        } catch (ParseException e) {
            throw new RuntimeException("Error while parsing json: " + getJsonString(), e);
        }
    }

    private ClientResponse excecuteRequest() {
        Client client = Client.create();
        WebResource webResource = client.resource(createUri());
        String request = createJsonString();
        ClientResponse response = webResource
                .accept(MediaType.APPLICATION_JSON)
                .type( MediaType.APPLICATION_JSON )
                .post(ClientResponse.class, request);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }
        return response;
    }

    /**
     * Creates a JSON string with the cypher query:
     * 
     * {"statements" : [
     *   {
     *     "statement" : "<query>",
     *     "resultDataContents" : [ "graph" ]
     *   } 
     * ]}
     * 
     * @return JSON string with cypher query
     */
    @SuppressWarnings("unchecked")
    private String createJsonString() {
        JSONObject statement = new JSONObject();
        statement.put("statement", getQuery());
        JSONArray resultDataContents = new JSONArray();
        resultDataContents.add("graph");
        statement.put("resultDataContents", resultDataContents);
        JSONArray statements = new JSONArray();
        statements.add(statement);
        JSONObject json = new JSONObject();
        json.put("statements", statements);
        String request = json.toJSONString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("JSON request: " + request);
        }
        return request;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    private String createUri() {
        StringBuilder sb = new StringBuilder();
        sb.append(getProtocol()).append("://");
        sb.append(getHost()).append(":").append(getPort());
        sb.append(getPath());
        String uri = sb.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("URI: " + uri);
        }
        return uri;
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

    public void setPath(String rootPath) {
        this.path = rootPath;
    }
}
