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

import java.util.Map;

/**
 * @author Daniel Murygin daniel.murygin@gmail.com
 */
public class Edge implements IPropertyContainer {
    
    private Node sourceNode;
    private Node targetNode; 
    private String type;
    private Map<String, String> properties;
    
    public Edge(Node sourceNode, Node targetNode, String type) {
        super();
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.type = type;
    }
    
    public Node getSourceNode() {
        return sourceNode;
    }
    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }
    
    public Node getTargetNode() {
        return targetNode;
    }
    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }
    public Map<String, String> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sourceNode == null) ? 0 : sourceNode.hashCode());
        result = prime * result + ((targetNode == null) ? 0 : targetNode.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Edge other = (Edge) obj;
        if (sourceNode == null) {
            if (other.sourceNode != null)
                return false;
        } else if (!sourceNode.equals(other.sourceNode))
            return false;
        if (targetNode == null) {
            if (other.targetNode != null)
                return false;
        } else if (!targetNode.equals(other.targetNode))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getSourceNode() + "->" + getTargetNode() + ", " + getType();
    }
}
