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

/**
 * Super class of mouse event handler.
 * @author Florent Benoit
 */
public abstract class DiagramMouseEventHandler {

    private final DiagramConnector diagramConnector;
    private final ClientStateModel clientStateModel;


    public DiagramMouseEventHandler(DiagramConnector diagramConnector, ClientStateModel clientStateModel) {
        this.diagramConnector = diagramConnector;
        this.clientStateModel = clientStateModel;
    }


    public DiagramConnector getConnector() {
        return diagramConnector;
    }

    public ClientStateModel getModel() {
        return clientStateModel;
    }
}
