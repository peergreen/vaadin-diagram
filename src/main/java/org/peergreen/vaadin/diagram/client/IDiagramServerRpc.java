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

import com.vaadin.shared.communication.ServerRpc;

/**
 * Vaadin RPC interface proposed on the Server side.
 * @author Florent Benoit
 */
public interface IDiagramServerRpc extends ServerRpc {

    /**
     * Notifies the server that a UI element (identified by the given UUID) has been selected.
     * @param uuid Selected UI element identifier
     */
    void selected(String uuid);

    /**
     * Notifies the server that a UI element (identified by the given UUID) should be deleted.
     * @param uuid Deleted UI element identifier
     */
    void deleted(String uuid);

    /**
     * Notifies the server that a new connector should be build between the given ports.
     * The server should verify parameters, if the connection looks good, propagate it back to the client side.
     * @param sourceUuid Identifier of the source port
     * @param targetUuid Identifier of the target port
     */
    void createConnector(String sourceUuid, String targetUuid);

    /**
     * Notifies the server that something was dropped into the given UI Element.
     * @param uuid Drop target
     * @param data transferred data from the drag source
     */
    void dropTarget(String uuid, final String data);

    void log(String message);
}
