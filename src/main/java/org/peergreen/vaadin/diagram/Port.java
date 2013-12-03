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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User: guillaume
 * Date: 05/11/13
 * Time: 12:26
 */
public abstract class Port extends AbstractElement {

    private String name;

    private List<Connector> connectors = new ArrayList<Connector>();

    public Port(final String name) {
        super(UUID.randomUUID());
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void addConnector(Connector connector) {
        connectors.add(connector);
        if (isAttached()) {
            getDiagram().addConnector(connector);
        }
    }

    public void removeConnector(Connector connector) {
        connectors.remove(connector);
        if (isAttached()) {
            getDiagram().removeConnector(connector);
        }
    }

    public List<Connector> getConnectors() {
        return new ArrayList<Connector>(connectors);
    }

    protected void connect(Port target) {
        Connector connector = new Connector(this, target);
        addConnector(connector);
        target.addConnector(connector);
    }

    public void disconnect(Port extremity) {
        for (Connector connector : getConnectors()) {
            if (connector.getSource().equals(extremity) || connector.getTarget().equals(extremity)) {
                removeConnector(connector);
                extremity.removeConnector(connector);
            }
        }
    }
}
