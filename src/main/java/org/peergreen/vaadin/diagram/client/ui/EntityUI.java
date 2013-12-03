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
import org.peergreen.vaadin.diagram.client.event.IKeyUp;
import org.peergreen.vaadin.diagram.client.move.IMovable;
import org.peergreen.vaadin.diagram.client.select.ISelectable;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;


public class EntityUI extends AbstractUI implements ISelectable, IMovable, IKeyUp {


    private String name;

    private double offsetX = 0;
    private double offsetY = 0;


    private double x;

    private double y;

    private final double width = 200;
    private final double radius = 15;
    private final double headerHeight = 25;
    private final double compartmentBaseHeight = 50;

    private final String strokeStyle = "black";
    private final String fillStyle = "white";
    private final String headerFillStyle = "#157DEC";

    private final List<CompartmentUI> compartments = new ArrayList<CompartmentUI>();


    public EntityUI(ClientStateModel model, String id, int x, int y) {
        super(model, id);
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getCompartmentBaseHeight() {
        return compartmentBaseHeight;
    }

    public double getWidth() {
        return width;
    }

    @Override
    public void draw() {

        getCanvas().save();
        getCanvas().setFillStyle(fillStyle);
        getCanvas().setStrokeStyle(strokeStyle);

        drawHeader();
        drawCompartments();

        getCanvas().restore();

    }

    private void drawCompartments() {
        for (CompartmentUI compartment : compartments) {
            compartment.draw();
        }
        //drawCompartment(canvas, lastCompartment, yOffset, x, y);
    }

    private void drawHeader() {
        drawRoundedBox();
        drawTitle();
    }

    private void drawRoundedBox() {

        getCanvas().save();

        if (this.equals(getModel().getCurrentMouseOverUI())) {
            getCanvas().setShadowBlur(10);
            getCanvas().setShadowColor("#666");
        }
        if (this.equals(getModel().getSelectedUI())) {
            getCanvas().setFillStyle("#1800AB");
        } else {
            getCanvas().setFillStyle(headerFillStyle);
        }

        getCanvas().beginPath();
        // top horizontal segment
        getCanvas().moveTo(x + radius, y);
        getCanvas().lineTo(x + width - radius, y);
        // top right curve
        getCanvas().quadraticCurveTo(x + width, y, x + width, y + radius);
        // right vertical segment
        double compartmentsHeight = getCompartmentsHeight();
        getCanvas().lineTo(x + width, y + headerHeight + compartmentsHeight);
        // bottom horizontal segment
        getCanvas().lineTo(x, y + headerHeight + compartmentsHeight);
        // left vertical segment
        getCanvas().lineTo(x, y + radius);
        // top left curve
        getCanvas().quadraticCurveTo(x, y, x + radius, y);
        getCanvas().closePath();
        getCanvas().fill();
        getCanvas().stroke();
        getCanvas().restore();
    }

    private void drawTitle() {
        getCanvas().save();
        getCanvas().setFillStyle("white");
        getCanvas().setTextBaseline(Context2d.TextBaseline.MIDDLE);
        getCanvas().setFont("bold 18px arial");
        int space = 10;
        getCanvas().fillText(name, x + space, y + (headerHeight / 2), width - (2 * space));
        getCanvas().restore();
    }


    @Override
    public void moveTo(IPoint point) {
        this.x = point.getX() - offsetX;
        this.y = point.getY() - offsetY;
    }


    @Override
    public UI getSelectedUI(IScaledPoint point) {
        // Check Compartments
        for (CompartmentUI compartment : compartments) {
            UI ui = compartment.getSelectedUI(point);
            if (ui != null) {
                return ui;
            }
        }

        if (isMatching(point.getX(), point.getY())) {
            return this;
        }

        return null;
    }


    protected boolean isMatching(double mouseX, double mouseY) {
        return ((mouseX >= x && mouseX <= x + width)
                && (mouseY >= y && mouseY <= y + getHeight()));
    }


    public double getX() {
        return x;
    }


    public double getY() {
        return y;
    }


    @Override
    public void setStartMove(IPoint point) {
        this.offsetX = point.getX() - this.x;
        this.offsetY = point.getY() - this.y;
    }


    public double getHeight() {
        return headerHeight + getCompartmentsHeight();
    }

    public void addCompartment(final CompartmentUI compartment) {
        compartments.add(compartment);
    }

    public void removeCompartment(final CompartmentUI compartment) {
        compartments.remove(compartment);
    }

    public double getCompartmentOriginY(final CompartmentUI ui) {
        int target = compartments.indexOf(ui);
        double y = this.y;
        y += headerHeight;
        int i = 0;
        while (i < target) {
            CompartmentUI compartment = compartments.get(i++);
            y += compartment.getHeight();
        }
        return y;
    }

    public double getCompartmentsHeight() {
        double total = 0;
        for (CompartmentUI compartment : compartments) {
            total += compartment.getHeight();
        }
        return total;
    }

    public List<CompartmentUI> getCompartments() {
        return new ArrayList<CompartmentUI>(compartments);
    }


    @Override
    public void keyUp(KeyUpEvent event) {
        // Remove the entity
        if (KeyCodes.KEY_DELETE == event.getNativeKeyCode() || KeyCodes.KEY_BACKSPACE == event.getNativeKeyCode() ) {
            getModel().getServerRpc().deleted(getId());
        }
    }

}
