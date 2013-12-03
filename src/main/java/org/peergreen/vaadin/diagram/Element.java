/**
 * Copyright 2013 Peergreen S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.peergreen.vaadin.diagram;

/**
 * User: guillaume
 * Date: 06/11/13
 * Time: 09:56
 */
public interface Element {

    /**
     * @return {@literal true} if the component is attached to a {@link org.peergreen.vaadin.diagram.Diagram}.
     */
    boolean isAttached();

    /**
     * This is called by the Diagram when the component is being transferred into the diagram.
     * @param diagram attached diagram
     */
    void attach(Diagram diagram);

    /**
     * This is called by the Diagram when the component is being removed from the diagram.
     * When detached, Element's changes are not synchronized anymore.
     */
    void detach();

    /**
     * @return the attached diagram (may be {@literal null} if not attached).
     */
    Diagram getDiagram();

    /**
     * @return The element's unique identifier.
     */
    String getUuid();
}
