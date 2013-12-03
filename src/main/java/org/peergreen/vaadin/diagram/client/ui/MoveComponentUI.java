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
import org.peergreen.vaadin.diagram.client.DiagramConnector;
import org.peergreen.vaadin.diagram.client.coordinates.IPoint;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.coordinates.Point;
import org.peergreen.vaadin.diagram.client.event.IObjectClick;
import org.peergreen.vaadin.diagram.client.select.ISelectable;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * UI used to show arrows allowing to move to left, right and up and down
 * @author Florent Benoit
 */
public class MoveComponentUI extends AbstractUI implements ISelectable, IObjectClick {

    private final DiagramConnector diagramConnector;
    private double circleX;
    private double circleY;
    private double radius;


    private List<IPoint> leftArrowPoints;
    private List<IPoint> upArrowPoints;
    private List<IPoint> rightArrowPoints;
    private List<IPoint> downArrowPoints;


    public MoveComponentUI(DiagramConnector diagramConnector, ClientStateModel clientStateModel, String id) {
        super(clientStateModel, id);
        this.diagramConnector = diagramConnector;

        compute();
    }


    public void compute() {

        // needs to be in the left upper corner
        this.circleX = 35 * getModel().getInvertScale() + getModel().getTranslateX();
        this.circleY = 35 * getModel().getInvertScale() + getModel().getTranslateY();
        double width = 60 * getModel().getInvertScale();

        this.radius = width /2;

        // space between the border of the circle and the point
        double fromBorder = 4 * getModel().getInvertScale();

        // Width of the arrow
        double arrowLength = 10 * getModel().getInvertScale();


        // Now build first arrow
        // compute points
        IPoint leftPoint = new Point(circleX - radius + fromBorder, circleY);
        IPoint rightPoint = new Point(circleX + radius - fromBorder, circleY);
        IPoint upPoint = new Point(circleX, circleY - radius + fromBorder);
        IPoint bottomPoint = new Point(circleX, circleY + radius - fromBorder);

        // left
        this.leftArrowPoints = getArrowPoints(leftPoint, upPoint, bottomPoint, rightPoint, arrowLength);

        // up
        this.upArrowPoints = getArrowPoints(upPoint, rightPoint, leftPoint, bottomPoint, arrowLength);

        // right
        this.rightArrowPoints = getArrowPoints(rightPoint, bottomPoint, upPoint, leftPoint, arrowLength);

        // down
        this.downArrowPoints = getArrowPoints(bottomPoint, leftPoint, rightPoint, upPoint, arrowLength);

    }

    @Override
    public void draw() {
        compute();

        // Start the circle
        getCanvas().save();
        getCanvas().beginPath();
        getCanvas().arc(circleX, circleY, radius , 0, 2 * Math.PI, false);
        getCanvas().setFillStyle("#FFF");
        getCanvas().fill();
        getCanvas().setLineWidth(1 * getModel().getInvertScale());
        getCanvas().setStrokeStyle("#AAA");
        getCanvas().stroke();
        getCanvas().restore();

        drawPath(leftArrowPoints);
        drawPath(upArrowPoints);
        drawPath(rightArrowPoints);
        drawPath(downArrowPoints);

    }


    protected void drawPath(List<IPoint> points) {
        getCanvas().beginPath();
        getCanvas().moveTo(points.get(0).getX() , points.get(0).getY() );
        for (IPoint rectPoint : points) {
            getCanvas().lineTo(rectPoint.getX(), rectPoint.getY());
        }
        getCanvas().setFillStyle("#EEE");
        getCanvas().fill();
        getCanvas().closePath();
    }

    protected boolean isInPath(IPoint point, List<IPoint> points) {
        getCanvas().beginPath();
        getCanvas().moveTo(points.get(0).getX() , points.get(0).getY() );
        for (IPoint rectPoint : points) {
            getCanvas().lineTo(rectPoint.getX(), rectPoint.getY());
        }
        return getCanvas().isPointInPath(point.getX(), point.getY());
    }




    protected List<IPoint> getArrowPoints(IPoint startPoint, IPoint firstEndPoint, IPoint secondEndPoint, IPoint oppositePoint, double arrowLength) {
        List<IPoint> points = new ArrayList<IPoint>();

        IPoint firstMidPoint = getPointOnPath(startPoint, firstEndPoint, arrowLength);
        IPoint secondMidPoint = getPointOnPath(startPoint, secondEndPoint, arrowLength);
        IPoint oppositePointOnPath = getPointOnPath(startPoint, oppositePoint, 8 * getModel().getInvertScale());

        // first points of rectangle
        List<IPoint> lst1 = getRectPoints(startPoint, firstMidPoint);
        // other points of rectangle
        List<IPoint> lst2 = getRectPoints(startPoint, secondMidPoint);

        points.add(lst1.get(1));
        points.add(lst1.get(2));
        points.add(lst1.get(3));
        points.add(oppositePointOnPath);
        points.add(lst2.get(2));
        points.add(lst2.get(3));
        points.add(lst2.get(0));

        return points;
    }


    protected IPoint getPointOnPath(IPoint start, IPoint end, double arrowLength) {

        double sqrt = Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));

        double x = start.getX() + (arrowLength * (end.getX() - start.getX())) / sqrt;

        double deltaY =  (arrowLength * (end.getY() - start.getY())) / sqrt;
        double y = start.getY() + deltaY;

        return new Point(x, y);
    }


    protected List<IPoint> getRectPoints(IPoint source, IPoint target) {
        List<IPoint> points = new ArrayList<IPoint>();
        double width = target.getX() - source.getX();
        double height = target.getY() - source.getY();

        double length = Math.sqrt(width * width + height * height);
        double thickness = 3 * getModel().getInvertScale();
        double xS = (thickness * height / length ) / 2;
        double yS = (thickness * width / length) / 2;

        points.add(new Point(source.getX() - xS, source.getY() + yS));
        points.add(new Point(source.getX() + xS, source.getY() - yS));
        points.add(new Point(target.getX() + xS, target.getY() - yS));
        points.add(new Point(target.getX() - xS, target.getY() + yS));
        return points;
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
        if (isInPath(mouseClick, leftArrowPoints)) {
            diagramConnector.moveLeft();
        } else if (isInPath(mouseClick, rightArrowPoints)) {
            diagramConnector.moveRight();
        } else if (isInPath(mouseClick, upArrowPoints)) {
            diagramConnector.moveUp();
        } else if (isInPath(mouseClick, downArrowPoints)) {
            diagramConnector.moveDown();
        }

    }

}
