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
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;

import com.google.gwt.canvas.dom.client.Context2d;

public class ProvideUI extends PortUI {

    private final String fillStyle = "#FFFFCC";
    private double length;
    private final double tail = 10;
    private double hypotenuse;


    public ProvideUI(ClientStateModel clientStateModel, String id, CompartmentUI parent) {
        super(clientStateModel, id, parent);
        setLength(20);
    }

    public double getLength() {
        return length;
    }

    public void setLength(final double length) {
        this.length = length;
        double half = length / 2;
        this.hypotenuse = Math.sqrt((length * length) + (half * half));
    }

    @Override
    public double getConnectorX() {
        return getX() - tail - hypotenuse;
    }

    @Override
    public double getConnectorY() {
        return getY() + getCompartment().getEntity().getCompartmentBaseHeight() / 2;
    }

    public double getX() {
        return getCompartment().getX();
    }

    public double getY() {
        return getCompartment().getPortOriginY(this);
    }

    @Override
    protected double getTextX() {
        return getX() - tail;
    }

    @Override
    protected double getTextY() {
        double half = length / 2;
        return getY() + getCompartment().getEntity().getCompartmentBaseHeight() / 2 - half;
    }

    @Override
    public void draw() {
        double x = getX();
        double y = getY() + getCompartment().getEntity().getCompartmentBaseHeight() / 2;

        getCanvas().save();
        getCanvas().setFillStyle(fillStyle);
        if (this.equals(getModel().getCurrentMouseOverUI())) {
            getCanvas().setShadowBlur(5);
            getCanvas().setShadowColor("#666");
        }

        getCanvas().beginPath();
        getCanvas().moveTo(x, y);
        getCanvas().lineTo(x - tail, y);
        getCanvas().closePath();
        getCanvas().stroke();

        getCanvas().beginPath();
        double half = length / 2;
        getCanvas().moveTo(x - tail, y - half);
        getCanvas().lineTo(x - tail, y + half);
        getCanvas().lineTo(x - tail - hypotenuse, y);
        getCanvas().closePath();

        getCanvas().fill();
        getCanvas().stroke();

        getCanvas().restore();

        // Draw something when there are hidden connectors
        if (isInternallyConnected()) {
            getCanvas().save();
            getCanvas().setFillStyle("black");
            getCanvas().setTextAlign(Context2d.TextAlign.RIGHT);
            getCanvas().setFont("18px arial");
            getCanvas().fillText("...", x - tail, y + half + 2);
            getCanvas().restore();
        }

        // draw text
        if ((getName() != null) && (!"".equals(getName()))) {
            getCanvas().save();
            getCanvas().setTextAlign(Context2d.TextAlign.RIGHT);
            super.draw();
            getCanvas().restore();
        }


    }

    @Override
    public UI getSelectedUI(final IScaledPoint point) {
        return isInside(point) ? this : null;

    }

    private boolean isInside(final IPoint point) {
        double x = getX() - hypotenuse - tail;
        double y = getY() + (getCompartment().getEntity().getCompartmentBaseHeight() / 2) - length / 2;
        double height = length;
        double width = hypotenuse;
        return ((point.getX() >= x && point.getX() <= x + width)
                && (point.getY() >= y && point.getY() <= y + height));
    }

    @Override
    protected boolean isValid(final IntermediateConnectorUI connector) {
        return connector.getSourcePort() instanceof RequireUI;
    }
}
