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
package org.peergreen.vaadin.diagram.client;

import java.util.Map;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FocusWidget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ui.dd.VDragEvent;
import com.vaadin.client.ui.dd.VDropHandler;
import com.vaadin.client.ui.dd.VHasDropHandler;

/**
 * Widget that is implementing Canvas element and Drop Handler.
 * @author Florent Benoit
 */
public class DiagramWidget extends FocusWidget implements VHasDropHandler {

    private final VDropHandler dropHandler;
    private ApplicationConnection currentClient;

    public DiagramWidget(final ComponentConnector connector, Element element) {
        setElement(element);
        this.dropHandler = new VDropHandler() {

            @Override
            public ComponentConnector getConnector() {
                return connector;
            }

            @Override
            public ApplicationConnection getApplicationConnection() {
                return currentClient;
            }

            @Override
            public boolean drop(VDragEvent drag) {
                Map<String, Object> dropDetails = drag.getDropDetails();
                dropDetails.put("absoluteLeft", getAbsoluteLeft());
                dropDetails.put("absoluteTop", getAbsoluteTop());
                return true;
            }

            @Override
            public void dragOver(VDragEvent currentDrag) {

            }

            @Override
            public void dragLeave(VDragEvent dragEvent) {

            }

            @Override
            public void dragEnter(VDragEvent dragEvent) {

            }
        };
    }

    @Override
    public VDropHandler getDropHandler() {
        return dropHandler;
    }


    /**
     * Returns the attached Canvas Element.
     *
     * @return the Canvas Element
     */
    public CanvasElement getCanvasElement() {
      return this.getElement().cast();
    }

    /**
     * Returns a 2D rendering context.
     *
     * This is a convenience method, see {@link #getContext(String)}.
     *
     * @return a 2D canvas rendering context
     */
    public Context2d getContext2d() {
      return getCanvasElement().getContext2d();
    }

    public void setCurrentClient(ApplicationConnection currentClient) {
        this.currentClient = currentClient;
    }


}
