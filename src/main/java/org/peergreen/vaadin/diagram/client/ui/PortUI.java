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

package org.peergreen.vaadin.diagram.client.ui;

import org.peergreen.vaadin.diagram.client.ClientStateModel;
import org.peergreen.vaadin.diagram.client.event.IObjectMouseDown;
import org.peergreen.vaadin.diagram.client.event.IObjectMouseUp;
import org.peergreen.vaadin.diagram.client.select.ISelectable;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * User: guillaume
 * Date: 05/11/13
 * Time: 16:31
 */
public abstract class PortUI extends AbstractUI implements ISelectable, IObjectMouseDown, IObjectMouseUp {
    private final CompartmentUI compartment;
    private String name = "";

    public PortUI(final ClientStateModel model, final String id, CompartmentUI compartment) {
        super(model, id);
        this.compartment = compartment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void draw() {
        getCanvas().save();
        getCanvas().setFillStyle("black");

        getCanvas().setTextBaseline(Context2d.TextBaseline.BOTTOM);
        getCanvas().fillText(name, getTextX(), getTextY());

        getCanvas().restore();

    }

    @Override
    public void mouseDown(MouseDownEvent event) {
        // Click on a port, start a new fake connector
        new IntermediateConnectorUI(getModel(), this).start();
    }

    @Override
    public void mouseUp(MouseUpEvent event) {
        // Check if there is a current connector
        UI ui = getModel().getTempDrawUI();
        if (ui != null && ui instanceof IntermediateConnectorUI) {
            IntermediateConnectorUI connector = (IntermediateConnectorUI) ui;

            // First level of verification:
            // Do not handle 0 length connectors (source == target)
            if (this.equals(connector.getSourcePort())) {
                return;
            }

            // Let a chance to the target port to validate the connection
            if (isValid(connector)) {
                // The server-side is responsible for maintaining the real state
                getModel().getServerRpc().createConnector(
                        connector.getSourcePort().getId(),
                        this.getId()
                );
            }
        }

    }

    protected abstract boolean isValid(final IntermediateConnectorUI connector);

    protected abstract double getTextX();

    protected abstract double getTextY();

    public abstract double getConnectorX();

    public abstract double getConnectorY();

    public CompartmentUI getCompartment() {
        return compartment;
    }

    protected boolean isInternallyConnected() {
        for (ConnectorUI connector : getModel().findConnectors(this)) {
            if (connector.isInternal()) {
                return true;
            }
        }
        return false;
    }
}
