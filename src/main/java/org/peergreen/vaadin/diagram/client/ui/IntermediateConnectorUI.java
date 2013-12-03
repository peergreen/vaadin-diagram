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
import org.peergreen.vaadin.diagram.client.coordinates.IPoint;
import org.peergreen.vaadin.diagram.client.event.IGlobalMouseUp;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseUpEvent;

public class IntermediateConnectorUI extends AbstractUI implements IGlobalMouseUp {

    private final PortUI sourcePort;

    public IntermediateConnectorUI(ClientStateModel model, PortUI sourcePort) {
        super(model, "un-used");
        this.sourcePort = sourcePort;
    }

    public PortUI getSourcePort() {
        return sourcePort;
    }

    @Override
    public void draw() {
        // Draw a line from the source port to the current mouse coordinates
        Context2d canvas = getCanvas();
        IPoint coordinates = getModel().getMouseCoordinates();

        canvas.save();
        canvas.beginPath();
        canvas.setStrokeStyle("#000");
        canvas.moveTo(sourcePort.getConnectorX(), sourcePort.getConnectorY());
        canvas.lineTo(coordinates.getX(), coordinates.getY());
        canvas.closePath();
        canvas.stroke();
        canvas.restore();
    }

    @Override
    public void globalMouseUp(MouseUpEvent event) {
        dispose();
    }

    private void dispose() {
        // Remove mouse-up listener
        getModel().getGlobalEventCallbacks().remove(this);

        // Remove from temporal drawing space
        if (this.equals(getModel().getTempDrawUI())) {
            getModel().setTempDrawUI(null);
        }
    }

    public void start() {
        // Place ourselves in the temporary drawing space
        getModel().setTempDrawUI(this);

        // Adds a global event callback to be notified on mouse-up
        // events (regardless what is the currently selected ui element)
        getModel().getGlobalEventCallbacks().add(this);

    }
}
