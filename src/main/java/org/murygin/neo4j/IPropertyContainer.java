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
 * Interface for a data model class which contains
 * property such as nodes and edges.
 * 
 * @author Daniel Murygin daniel.murygin@gmail.com
 */
public interface IPropertyContainer {

    void addProperty(String key, String value);
    Map<String, String> getProperties();
    void setProperties(Map<String, String> properties);
}
