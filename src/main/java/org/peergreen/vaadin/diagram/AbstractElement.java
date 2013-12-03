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

import java.util.UUID;

/**
 * User: guillaume
 * Date: 06/11/13
 * Time: 10:29
 */
public abstract class AbstractElement implements Element {

    /**
     * Element(s identifier.
     */
    private final String uuid;
    /**
     * Attached diagram (if any)
     */
    private Diagram diagram;

    protected AbstractElement(final UUID uuid) {
        this.uuid = uuid.toString();
    }

    @Override
    public boolean isAttached() {
        return diagram != null;
    }

    @Override
    public void attach(final Diagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public void detach() {
        this.diagram = null;
    }

    @Override
    public Diagram getDiagram() {
        return diagram;
    }

    @Override
    public String getUuid() {
        return uuid;
    }
}
