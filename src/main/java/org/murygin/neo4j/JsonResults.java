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
package org.murygin.neo4j;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
@SuppressWarnings("rawtypes")
public class JsonResults {

    private JSONObject json;

    public JsonResults(JSONObject json) {
        super();
        this.json = json;
    }
    
    public JSONArray getDataList() {
        JSONArray results = getJsonArray(json, "results");
        JSONObject result = (JSONObject) results.get(0);
        return getJsonArray(result, "data");
    }
    
    public List<JSONObject> getNodeList() {
        return getGraphChildNodeList("nodes");
    }
    
    public List<JSONObject> getRelationList() {
        return getGraphChildNodeList("relationships");
    }
    
    public List<JSONObject> getGraphList() {
        List<JSONObject> childNodeList = new LinkedList<JSONObject>();
        List dataList = getDataList();      
        for (Object dataObject : dataList) {
            childNodeList.add(getJson((JSONObject) dataObject, "graph"));
        }
        return childNodeList;
    }
    
    public List<JSONObject> getGraphChildNodeList(String name) {
        List<JSONObject> childNodeList = new LinkedList<JSONObject>();
        List dataList = getDataList();      
        for (Object dataObject : dataList) {
            JSONObject graph =  getJson((JSONObject) dataObject, "graph");
            JSONArray relations = getJsonArray(graph, name);
            for (Object relation : relations) {
                childNodeList.add((JSONObject) relation);
            }
        }
        return childNodeList;
    }
    
    public static JSONArray getJsonArray(JSONObject node, String name) {
        return (JSONArray) node.get(name);
    }
    
    public static JSONObject getJson(JSONObject node, String name) {
        return (JSONObject) node.get(name);
    }
}
