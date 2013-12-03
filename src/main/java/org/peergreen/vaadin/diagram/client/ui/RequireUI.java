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

public class RequireUI extends PortUI {

    private final float radius = 12.5f;

    public RequireUI(ClientStateModel clientStateModel, String id, CompartmentUI parent) {
        super(clientStateModel, id, parent);
    }

    @Override
    public double getConnectorX() {
        return getX() + radius;
    }

    @Override
    public double getConnectorY() {
        return getY() + getCompartment().getEntity().getCompartmentBaseHeight() / 2;
    }

    public double getX() {
        return getCompartment().getX() + getCompartment().getWidth();
    }

    public double getY() {
        return getCompartment().getPortOriginY(this);
    }

    @Override
    protected double getTextX() {
        return getX() + 5;
    }

    @Override
    protected double getTextY() {
        return getY() + getCompartment().getEntity().getCompartmentBaseHeight() / 2 - radius;
    }

    @Override
    public void draw() {
        getCanvas().save();
        getCanvas().setFillStyle("#64E986");
        if (this.equals(getModel().getCurrentMouseOverUI())) {
            getCanvas().setShadowBlur(5);
            getCanvas().setShadowColor("#666");
        }

        getCanvas().beginPath();
        double x = getX();
        double y = getY();
        double half = getCompartment().getEntity().getCompartmentBaseHeight() / 2;
        getCanvas().arc(x, y + half,
                        radius,
                        Math.PI / 2, 3 * Math.PI / 2,
                        true);
        getCanvas().closePath();

        getCanvas().stroke();
        getCanvas().fill();

        getCanvas().restore();

        // Draw something when there are hidden connectors
        if (isInternallyConnected()) {
            getCanvas().save();
            getCanvas().setFillStyle("black");
            getCanvas().setFont("18px arial");
            getCanvas().fillText("...", x, y + half + radius + 2);
            getCanvas().restore();
        }

        // draw text
        if ((getName() != null) && (!"".equals(getName()))) {
            super.draw();
        }

    }
    @Override
    public UI getSelectedUI(final IScaledPoint point) {
        return isInside(point) ? this : null;

    }

    private boolean isInside(final IPoint point) {
        double x = getX();
        double y = getY() + (getCompartment().getEntity().getCompartmentBaseHeight() / 2) - radius;
        double height = 2 * radius;
        double width = radius;
        return ((point.getX() >= x && point.getX() <= x + width)
                && (point.getY() >= y && point.getY() <= y + height));
    }


    @Override
    protected boolean isValid(final IntermediateConnectorUI connector) {
        return connector.getSourcePort() instanceof ProvideUI;
    }

}
