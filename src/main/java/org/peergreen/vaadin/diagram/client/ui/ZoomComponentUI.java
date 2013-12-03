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
import org.peergreen.vaadin.diagram.client.DiagramConnector;
import org.peergreen.vaadin.diagram.client.coordinates.IPoint;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.coordinates.Point;
import org.peergreen.vaadin.diagram.client.event.IObjectClick;
import org.peergreen.vaadin.diagram.client.select.ISelectable;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * Component UI used to perform zoom in and zoom out
 * @author Florent Benoit
 */
public class ZoomComponentUI extends AbstractUI implements ISelectable, IObjectClick {

    private final DiagramConnector diagramConnector;
    private double circleX;
    private double circleY;

    private IPoint zoomIn;
    private IPoint zoomOut;

    private double radius;
    private double radiusSign;

    private IPoint leftPlus;
    private IPoint rigthPlus;
    private IPoint upPlus;
    private IPoint downPlus;
    private IPoint leftMinus;
    private IPoint rigthMinus;



    public ZoomComponentUI(DiagramConnector diagramConnector, ClientStateModel clientStateModel, String id) {
        super(clientStateModel, id);
        this.diagramConnector = diagramConnector;

        compute();
    }


    public void compute() {

        // needs to be in the left upper corner
        this.circleX = 35 * getModel().getInvertScale() + getModel().getTranslateX();
        this.circleY = (35 + 70) * getModel().getInvertScale() + getModel().getTranslateY();
        double width = 60 * getModel().getInvertScale();

        this.radius = width / 2;

        double signWidth = width / 4;
        this.radiusSign = signWidth / 2;


        // Compute plus (for zoomIn)
        this.zoomIn = new Point(circleX, circleY - (radius /2));
        this.leftPlus = new Point(zoomIn.getX() - radiusSign, zoomIn.getY());
        this.rigthPlus = new Point(zoomIn.getX() + radiusSign, zoomIn.getY());
        this.upPlus = new Point(zoomIn.getX(), zoomIn.getY() - radiusSign);
        this.downPlus = new Point(zoomIn.getX() , zoomIn.getY() + radiusSign);

        // Compute minus (for zoomOut)
        this.zoomOut = new Point(circleX, circleY + (radius /2));
        this.leftMinus = new Point(zoomOut.getX() - radiusSign, zoomOut.getY());
        this.rigthMinus = new Point(zoomOut.getX() + radiusSign, zoomOut.getY());
    }

    @Override
    public void draw() {
        compute();

        // Draw the circle
        getCanvas().save();
        getCanvas().beginPath();
        getCanvas().arc(circleX, circleY, radius , 0, 2 * Math.PI, false);
        getCanvas().setFillStyle("#FFF");
        getCanvas().fill();
        getCanvas().setLineWidth(1 * getModel().getInvertScale());
        getCanvas().setStrokeStyle("#AAA");
        getCanvas().stroke();
        getCanvas().closePath();
        getCanvas().restore();


        // And split the circle
        getCanvas().save();
        getCanvas().setStrokeStyle("#AAA");
        getCanvas().setLineWidth(1 * getModel().getInvertScale());
        getCanvas().beginPath();
        getCanvas().moveTo(circleX - radius, circleY);
        getCanvas().lineTo(circleX + radius, circleY);
        getCanvas().stroke();
        getCanvas().closePath();


        // plus and minus style
        getCanvas().save();
        getCanvas().setStrokeStyle("#AAA");
        getCanvas().setLineWidth(1 * getModel().getInvertScale());

        // Plus
        getCanvas().beginPath();
        getCanvas().moveTo(leftPlus.getX(), leftPlus.getY());
        getCanvas().lineTo(rigthPlus.getX(), rigthPlus.getY());
        getCanvas().closePath();
        getCanvas().stroke();
        getCanvas().beginPath();
        getCanvas().moveTo(upPlus.getX(), upPlus.getY());
        getCanvas().lineTo(downPlus.getX(), downPlus.getY());
        getCanvas().closePath();
        getCanvas().stroke();

        // minus
        getCanvas().beginPath();
        getCanvas().moveTo(leftMinus.getX(), leftMinus.getY());
        getCanvas().lineTo(rigthMinus.getX(), rigthMinus.getY());
        getCanvas().closePath();
        getCanvas().stroke();


        getCanvas().restore();
    }


    protected boolean isInZoomInPath(IPoint point) {
        getCanvas().beginPath();
        getCanvas().arc(circleX, circleY, radius , 0, Math.PI, false);
        return getCanvas().isPointInPath(point.getX(), point.getY());
    }

    protected boolean isInZoomOutPath(IPoint point) {
        getCanvas().beginPath();
        getCanvas().arc(circleX, circleY, radius , Math.PI, 2 * Math.PI, false);
        return getCanvas().isPointInPath(point.getX(), point.getY());
    }



    @Override
    public UI getSelectedUI(IScaledPoint point) {
        getCanvas().beginPath();
        getCanvas().arc(circleX, circleY, radius , 0, 2 * Math.PI, false);
        if (getCanvas().isPointInPath(point.getOriginalX(), point.getOriginalY())) {
            return this;
        }
        return null;
    }


    @Override
    public void click(ClickEvent event) {
        compute();
        IPoint mouseClick = new Point(event.getX(), event.getY());
        if (isInZoomInPath(mouseClick)) {
            diagramConnector.doZoomIn();
        } else if (isInZoomOutPath(mouseClick)) {
            diagramConnector.doZoomOut();
        }
    }

}
