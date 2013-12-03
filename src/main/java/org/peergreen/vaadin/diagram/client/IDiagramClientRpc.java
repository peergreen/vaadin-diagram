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

import com.vaadin.shared.communication.ClientRpc;

/**
 * Vaain RPC interface proposed by the Client.
 * @author Florent Benoit
 */
public interface IDiagramClientRpc extends ClientRpc {

    void init();
    void reset();

    void left();
    void right();
    void up();
    void down();

    void zoomIn();

    void zoomOut();

    void createEntity(String uuid, String name);
    void updateEntity(String uuid, String name);
    void deleteEntity(String uuid);

    void createCompartment(String entityUuid, String uuid, String name, final String iconType);

    void createRequire(String compartmentUuid, String uuid, String name);

    void createProvide(String compartmentUuid, String uuid, String name);

    void deleteCompartment(String uuid);

    void createConnector(final String uuid, String sourceUuid, String targetUuid);

    void deleteProvide(String uuid);

    void deleteRequire(String uuid);

    void deleteConnector(String uuid);

    void handleDrop(int x, int y, String data);
}