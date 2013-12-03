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

import org.peergreen.vaadin.diagram.client.ClientStateModel;
import org.peergreen.vaadin.diagram.client.DiagramConnector;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.event.IObjectMouseDown;
import org.peergreen.vaadin.diagram.client.move.IMovable;
import org.peergreen.vaadin.diagram.client.select.ISelectable;
import org.peergreen.vaadin.diagram.client.ui.UI;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

/**
 * Mouse down handler.
 * @author Florent Benoit
 */
public class DiagramClientMouseDownHandler extends DiagramMouseEventHandler implements MouseDownHandler {

    public DiagramClientMouseDownHandler(DiagramConnector diagramConnector, ClientStateModel clientStateModel) {
        super(diagramConnector, clientStateModel);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {

        // Compute real coordinate
        IScaledPoint point = getModel().buildScalePoint(event);

        // Do we have a matching shape ?
        for (UI ui : getModel().getRootUIs()) {

            // Can we select this UI ?
            if (!(ui instanceof ISelectable)) {
                continue;
            }

            ISelectable selectableUI = (ISelectable) ui;
            UI selectedUI = selectableUI.getSelectedUI(point);
            if (selectedUI != null) {
                if (selectedUI instanceof IObjectMouseDown) {
                    IObjectMouseDown objectMouseDown = (IObjectMouseDown) selectedUI;
                    objectMouseDown.mouseDown(event);
                }

                if (selectedUI instanceof IMovable) {
                    ((IMovable) selectedUI).setStartMove(point);
                    getModel().setCurrentUI(selectedUI);
                }
            }
        }
    }

}
