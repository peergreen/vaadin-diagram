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

import java.util.ArrayList;
import java.util.List;

import org.peergreen.vaadin.diagram.client.ClientStateModel;
import org.peergreen.vaadin.diagram.client.coordinates.IPoint;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.coordinates.Point;
import org.peergreen.vaadin.diagram.client.event.IKeyUp;
import org.peergreen.vaadin.diagram.client.select.ISelectable;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;

/**
 * Connector allowing the connect two ports.
 * @author Florent Benoit
 */
public class ConnectorUI extends AbstractUI implements IKeyUp, ISelectable {

    private final PortUI sourcePort;

    private final PortUI targetPort;

    public ConnectorUI(ClientStateModel clientStateModel, String id, PortUI sourcePort, final PortUI targetPort) {
        super(clientStateModel, id);
        this.sourcePort = sourcePort;
        this.targetPort = targetPort;
    }

    public PortUI getSourcePort() {
        return sourcePort;
    }

    public PortUI getTargetPort() {
        return targetPort;
    }

    public boolean isInternal() {
        EntityUI sourceEntity = sourcePort.getCompartment().getEntity();
        EntityUI targetEntity = targetPort.getCompartment().getEntity();
        return sourceEntity.equals(targetEntity);
    }

    @Override
    public void draw() {

        // The connector is connected at both ends

        // General rule:
        //  -> Do not draw the connector if it joins 2 ports that are in the same entity
        if (isInternal()) {

            // Exception
            // -> If connector is itself selected, draw it
            if (this.equals(getModel().getSelectedUI())) {
                drawConnector();
            }

            // Exception
            // -> if either source or target compartment of this connector are selected
            CompartmentUI sourceCompartment = sourcePort.getCompartment();
            CompartmentUI targetCompartment = targetPort.getCompartment();
            if (sourceCompartment.equals(getModel().getSelectedUI()) ||
                    targetCompartment.equals(getModel().getSelectedUI())) {
                drawConnector();
            }
        } else {
            // Normal draw
            drawConnector();
        }
    }

    private void drawConnector() {
        getCanvas().save();
        getCanvas().beginPath();

        // Highlight if we're over this connector
        if (this.equals(getModel().getCurrentMouseOverUI())) {
            getCanvas().setShadowBlur(5);
            getCanvas().setShadowColor("#666");
        }

        if (this.equals(getModel().getSelectedUI())) {
            getCanvas().setStrokeStyle("#4A8EE6");
        } else {
            getCanvas().setStrokeStyle("#000");
        }
        getCanvas().moveTo(sourcePort.getConnectorX(), sourcePort.getConnectorY());
        getCanvas().lineTo(targetPort.getConnectorX(), targetPort.getConnectorY());
        getCanvas().closePath();
        getCanvas().stroke();
        getCanvas().restore();
    }

    @Override
    public void keyUp(KeyUpEvent event) {
        // Remove the connector
        if (KeyCodes.KEY_DELETE == event.getNativeKeyCode() || KeyCodes.KEY_BACKSPACE == event.getNativeKeyCode() ) {
            getModel().getServerRpc().deleted(getId());
        }
    }

    @Override
    public UI getSelectedUI(IScaledPoint point) {

        // No end, exit
        if (targetPort == null) {
                return null;
        }

        // Create a rectangle path around the source and the target and check if it's included
        getCanvas().beginPath();

        List<IPoint> points = getRectPoints(
                getModel().scalePoint(sourcePort.getConnectorX(), sourcePort.getConnectorY()),
                getModel().scalePoint(targetPort.getConnectorX(), targetPort.getConnectorY()));

        // start point
        getCanvas().moveTo(points.get(0).getX() , points.get(0).getY() );
        // now connect the points
        for (IPoint rectPoint : points) {
            getCanvas().lineTo(rectPoint.getX(), rectPoint.getY());
        }

        if (getCanvas().isPointInPath(point.getX(), point.getY())) {
            return this;
        }
        // not found
        return null;
    }


    /**
     * Compute 4 points for building a rectangle around the two given points.
     * @param source the source point
     * @param target the target point
     * @return the 4 points.
     */
    protected List<IPoint> getRectPoints(IPoint source, IPoint target) {
        List<IPoint> points = new ArrayList<IPoint>();
        double width = target.getX() - source.getX();
        double height = target.getY() - source.getY();

        double length = Math.sqrt(width * width + height * height);
        int thickness = 10;
        double xS = (thickness * height / length ) / 2;
        double yS = (thickness * width / length) / 2;

        points.add(new Point(source.getX() - xS, source.getY() + yS));
        points.add(new Point(source.getX() + xS, source.getY() - yS));
        points.add(new Point(target.getX() + xS, target.getY() - yS));
        points.add(new Point(target.getX() - xS, target.getY() + yS));
        return points;
    }

    public boolean isSame(ConnectorUI other) {
        PortUI otherSource = other.getSourcePort();
        PortUI otherTarget = other.getTargetPort();

        // this and other are representing the same connector if they have the same extremities
        return ((sourcePort.equals(otherSource) && targetPort.equals(otherTarget))
                || (sourcePort.equals(otherTarget) && targetPort.equals(sourcePort)));
    }

}
