/*******************************************************************************
 * Copyright (c) 2015 DanSiel Murygin.
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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Murygin <dm[at]sernet[dot]de>
 */
public class Node implements IPropertyContainer {

    private String id;
    private List<String> labels;
    private Map<String, String> properties;
    
    public Node(String id) {
        super();
        this.id = id;
        labels = new LinkedList<String>();
        properties = new Hashtable<String, String>();
    }
    
    public void addLabel(String label) {
        labels.add(label);
    }
    
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<String> getLabels() {
        return labels;
    }
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
    public Map<String, String> getProperties() {
        return properties;
    }
    public String getProperty(String key) {
        return getProperties().get(key);
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getId());
        if(!labels.isEmpty()) {
            sb.append(":");
        }
        boolean first = true;
        for (String label : labels) {
            if(!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(label);  
        }
        return sb.toString();
    }
}
