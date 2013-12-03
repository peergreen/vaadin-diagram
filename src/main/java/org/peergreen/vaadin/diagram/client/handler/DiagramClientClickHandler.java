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
import org.peergreen.vaadin.diagram.client.event.IObjectClick;
import org.peergreen.vaadin.diagram.client.select.ISelectable;
import org.peergreen.vaadin.diagram.client.ui.UI;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Handler for click event.
 * @author Florent Benoit
 */
public class DiagramClientClickHandler extends DiagramMouseEventHandler implements ClickHandler {

    public DiagramClientClickHandler(DiagramConnector diagramConnector, ClientStateModel clientStateModel) {
        super(diagramConnector, clientStateModel);
    }

    @Override
    public void onClick(ClickEvent event) {
        IScaledPoint point = getModel().buildScalePoint(event);
        getModel().setMouseCoordinates(point);

        UI selectedUI = null;

        // Do we have a matching shape ?
        for (UI ui : getModel().getAllUIs()) {

            // Can we select this UI ?
            if (!(ui instanceof ISelectable)) {
                continue;
            }

            ISelectable selectableUI = (ISelectable) ui;
            selectedUI = selectableUI.getSelectedUI(point);

            // found something, stop
            if (selectedUI != null) {
                break;
            }

        }

        getModel().setSelectedUI(selectedUI);

        if (selectedUI != null) {
            if (selectedUI instanceof IObjectClick) {
                ((IObjectClick) selectedUI).click(event);
            }
        }
        // redraw
        getConnector().redraw();


    }
}
