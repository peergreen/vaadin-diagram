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

import java.util.List;

import org.peergreen.vaadin.diagram.client.ClientStateModel;
import org.peergreen.vaadin.diagram.client.DiagramConnector;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.event.IGlobalMouseUp;
import org.peergreen.vaadin.diagram.client.event.IObjectMouseUp;
import org.peergreen.vaadin.diagram.client.select.ISelectable;
import org.peergreen.vaadin.diagram.client.ui.AbstractUI;
import org.peergreen.vaadin.diagram.client.ui.UI;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

/**
 * Handler for mouse up event.
 * @author Florent Benoit
 */
public class DiagramClientMouseUpHandler extends DiagramMouseEventHandler implements MouseUpHandler {

    public DiagramClientMouseUpHandler(DiagramConnector diagramConnector, ClientStateModel clientStateModel) {
        super(diagramConnector, clientStateModel);
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {

        IScaledPoint point = getModel().buildScalePoint(event);
        for (UI ui : getModel().getRootUIs()) {
            if (ui instanceof ISelectable) {
                UI selectedUI = ((ISelectable) ui).getSelectedUI(point);
                if (selectedUI != null) {

                    if (selectedUI instanceof IObjectMouseUp) {
                        IObjectMouseUp objectMouseUp = (IObjectMouseUp) selectedUI;
                        objectMouseUp.mouseUp(event);
                    }
                }
            }
        }
        getConnector().setCursor(Cursor.POINTER);
        getModel().setCurrentUI(null);

        // Notify global listeners
        List<AbstractUI> globalEventsCallbacks = getModel().getGlobalEventCallbacks();
        for (AbstractUI abstractUI : globalEventsCallbacks) {
            if (abstractUI instanceof IGlobalMouseUp) {
                ((IGlobalMouseUp) abstractUI).globalMouseUp(event);
            }
        }

        getConnector().redraw();
    }

}