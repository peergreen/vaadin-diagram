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

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Super class of UIs.
 * @author Florent Benoit
 */
public abstract class AbstractUI implements UI {

    private final ClientStateModel clientStateModel;

    private final String id;

    public AbstractUI(ClientStateModel clientStateModel, String id) {
        this.clientStateModel = clientStateModel;
        this.id = id;
    }

    @Override
    public Context2d getCanvas() {
        return clientStateModel.getCanvas();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ClientStateModel getModel() {
        return clientStateModel;
    }


}
