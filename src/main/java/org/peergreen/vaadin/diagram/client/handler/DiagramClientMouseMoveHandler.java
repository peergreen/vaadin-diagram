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
package org.peergreen.vaadin.diagram.client.handler;

import static com.google.gwt.dom.client.Style.Cursor.MOVE;
import static com.google.gwt.dom.client.Style.Cursor.POINTER;

import org.peergreen.vaadin.diagram.client.ClientStateModel;
import org.peergreen.vaadin.diagram.client.DiagramConnector;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.move.IMovable;
import org.peergreen.vaadin.diagram.client.select.ISelectable;
import org.peergreen.vaadin.diagram.client.ui.UI;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

/**
 * Handler for mouse move event.
 * @author Florent Benoit
 */
public class DiagramClientMouseMoveHandler extends DiagramMouseEventHandler implements MouseMoveHandler {

    public DiagramClientMouseMoveHandler(DiagramConnector diagramConnector, ClientStateModel clientStateModel) {
        super(diagramConnector, clientStateModel);
    }


    /**
     * Called when MouseMoveEvent is fired.
     *
     * @param event the {@link MouseMoveEvent} that was fired
     */
    @Override
    public void onMouseMove(MouseMoveEvent event) {

        IScaledPoint point = getModel().buildScalePoint(event);
        getModel().setMouseCoordinates(point);

        UI currentUI = getModel().getCurrentUI();

        if (currentUI != null) {
            if (currentUI instanceof IMovable) {
                IMovable movable = (IMovable) getModel().getCurrentUI();
                getConnector().setCursor(MOVE);
                movable.moveTo(point);
                getConnector().redraw();
                return;
            }
        }

        boolean onAShape = false;

        UI mouseOverUI = getModel().getCurrentMouseOverUI();

        for (UI ui : getModel().getAllUIs()) {
            if (ui instanceof ISelectable) {
                UI selectedUI = ((ISelectable) ui).getSelectedUI(point);
                if (selectedUI != null) {
                    onAShape = true;
                    mouseOverUI = selectedUI;
                    break;
                }
            }
        }
        if (onAShape) {
            getModel().setCurrentMouseOverUI(mouseOverUI);
            getConnector().setCursor(POINTER);
        } else {
            getModel().setCurrentMouseOverUI(null);
            getConnector().unsetCursor();
        }

        // redraw
        getConnector().redraw();

    }
}
