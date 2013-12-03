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

import static com.google.gwt.dom.client.Style.Cursor.AUTO;

import org.peergreen.vaadin.diagram.Diagram;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.handler.DiagramClientClickHandler;
import org.peergreen.vaadin.diagram.client.handler.DiagramClientKeyUpHandler;
import org.peergreen.vaadin.diagram.client.handler.DiagramClientMouseDownHandler;
import org.peergreen.vaadin.diagram.client.handler.DiagramClientMouseMoveHandler;
import org.peergreen.vaadin.diagram.client.handler.DiagramClientMouseOutHandler;
import org.peergreen.vaadin.diagram.client.handler.DiagramClientMouseUpHandler;
import org.peergreen.vaadin.diagram.client.handler.DiagramClientMouseWheelHandler;
import org.peergreen.vaadin.diagram.client.select.ISelectable;
import org.peergreen.vaadin.diagram.client.ui.CompartmentUI;
import org.peergreen.vaadin.diagram.client.ui.ConnectorUI;
import org.peergreen.vaadin.diagram.client.ui.EntityUI;
import org.peergreen.vaadin.diagram.client.ui.MoveComponentUI;
import org.peergreen.vaadin.diagram.client.ui.PortUI;
import org.peergreen.vaadin.diagram.client.ui.ProvideUI;
import org.peergreen.vaadin.diagram.client.ui.RequireUI;
import org.peergreen.vaadin.diagram.client.ui.UI;
import org.peergreen.vaadin.diagram.client.ui.ZoomComponentUI;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.shared.ui.Connect;

/**
 * Vaadin Connector linked to the Diagram component
 * @author Florent Benoit
 */
@Connect(Diagram.class)
public class DiagramConnector extends AbstractComponentConnector implements SimpleManagedLayout, PostLayoutListener  {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2185981138007699576L;

    /**
     * Server side RPC object.
     */
    private final IDiagramServerRpc serverRpc = RpcProxy.create(IDiagramServerRpc.class, this);

    /**
     * State on the client side.
     */
    private final transient ClientStateModel clientStateModel;

    /**
     * Temporary index for switched entity creation
     */
    private int created = 0;

    /**
     * Build a new connector
     */
    public DiagramConnector() {

        this.clientStateModel = new ClientStateModel(getWidget().getContext2d(), serverRpc);

        registerRpc(IDiagramClientRpc.class, new IDiagramClientRpc() {

            /**
             * Serial version UID.
             */
            private static final long serialVersionUID = 1622915989830982425L;

            @Override
            public void init() {
            }

            @Override
            public void createEntity(final String uuid, final String name) {
                created++;
                EntityUI entity = new EntityUI(clientStateModel, uuid, 80 + (created * 40), created * 40);
                entity.setName(name);
                clientStateModel.getRootUIs().add(entity);
                redraw();
            }

            @Override
            public void updateEntity(String uuid, String name) {
                // Search the entity UI
                EntityUI entityUI = clientStateModel.findEntity(uuid);

                // Update the name
                if (entityUI != null) {
                    entityUI.setName(name);
                    redraw();
                }
            }

            @Override
            public void deleteEntity(final String uuid) {
                EntityUI entity = clientStateModel.findEntity(uuid);
                if (entity != null) {
                    cleanDeletedUiReferences(entity);
                    for (CompartmentUI compartment : entity.getCompartments()) {
                        deleteCompartment(compartment.getId());
                    }
                    clientStateModel.getRootUIs().remove(entity);
                }
                redraw();
            }

            @Override
            public void createCompartment(final String entityUuid, final String uuid, final String name, final String iconType) {
                EntityUI entity = clientStateModel.findEntity(entityUuid);
                if (entity != null) {
                    CompartmentUI compartment = new CompartmentUI(clientStateModel, uuid, entity);
                    compartment.setName(name);
                    String resourceUrl = getResourceUrl(iconType);
                    serverRpc.log(iconType + " -> " + resourceUrl);
                    if (resourceUrl != null) {
                        compartment.setIconUrl(resourceUrl);
                    }
                    entity.addCompartment(compartment);
                }
                redraw();
            }

            @Override
            public void deleteCompartment(final String uuid) {
                CompartmentUI compartment = clientStateModel.findCompartment(uuid);
                if (compartment != null) {
                    cleanDeletedUiReferences(compartment);
                    for (ProvideUI provide : compartment.getProvides()) {
                        deleteProvide(provide.getId());
                    }
                    for (RequireUI require : compartment.getRequires()) {
                        deleteRequire(require.getId());
                    }
                    compartment.getEntity().removeCompartment(compartment);
                }
                redraw();
            }

            @Override
            public void createRequire(final String compartmentUuid, final String uuid, final String name) {
                CompartmentUI compartment = clientStateModel.findCompartment(compartmentUuid);
                if (compartment != null) {
                    RequireUI require = new RequireUI(clientStateModel, uuid, compartment);
                    require.setName(name);
                    compartment.addRequire(require);
                }
                redraw();
            }

            @Override
            public void createProvide(final String compartmentUuid, final String uuid, final String name) {
                CompartmentUI compartment = clientStateModel.findCompartment(compartmentUuid);
                if (compartment != null) {
                    ProvideUI provide = new ProvideUI(clientStateModel, uuid, compartment);
                    provide.setName(name);
                    compartment.addProvide(provide);
                }
                redraw();
            }

            @Override
            public void createConnector(final String uuid, final String sourceUuid, final String targetUuid) {
                PortUI source = clientStateModel.findPort(sourceUuid);
                PortUI target = clientStateModel.findPort(targetUuid);
                if ((source != null) && (target != null)) {
                    // Only creates the ConnectorUI when source and target ports are known
                    clientStateModel.addConnector(new ConnectorUI(clientStateModel, uuid, source, target));
                }
                redraw();
            }

            @Override
            public void deleteProvide(final String uuid) {
                PortUI port = clientStateModel.findPort(uuid);
                if (port != null) {
                    cleanDeletedUiReferences(port);
                    for (ConnectorUI connector : clientStateModel.findConnectors(port)) {
                        clientStateModel.removeConnector(connector);
                    }
                    port.getCompartment().removeProvide((ProvideUI) port);
                }
                redraw();
            }

            @Override
            public void deleteRequire(final String uuid) {
                PortUI port = clientStateModel.findPort(uuid);
                if (port != null) {
                    cleanDeletedUiReferences(port);
                    for (ConnectorUI connector : clientStateModel.findConnectors(port)) {
                        clientStateModel.removeConnector(connector);
                    }
                    port.getCompartment().removeRequire((RequireUI) port);
                }
                redraw();
            }

            @Override
            public void deleteConnector(final String uuid) {
                for (ConnectorUI connector : clientStateModel.getConnectors()) {
                    if (uuid.equals(connector.getId())) {
                        cleanDeletedUiReferences(connector);
                        clientStateModel.removeConnector(connector);
                    }
                }
                redraw();
            }

            @Override
            public void handleDrop(final int x, final int y, final String data) {

                IScaledPoint point = clientStateModel.scalePoint(x, y);
                UI selected = null;
                for (UI ui : clientStateModel.getAllUIs()) {

                    // Can we select this UI ?
                    if (!(ui instanceof ISelectable)) {
                        continue;
                    }

                    ISelectable selectableUI = (ISelectable) ui;

                    // Always send back a dropTarget event
                    selected = selectableUI.getSelectedUI(point);
                    if (selected != null) {
                        // Exit loop if we hit something
                        break;
                    }

                }

                String id = null;
                if (selected != null) {
                    id = selected.getId();
                }
                serverRpc.dropTarget(id, data);
            }

            @Override
            public void zoomIn() {
                doZoomIn();
            }

            @Override
            public void zoomOut() {
                doZoomOut();
            }

            @Override
            public void left() {
                moveLeft();
            }

            @Override
            public void right() {
                moveRight();
            }

            @Override
            public void up() {
                moveUp();
            }

            @Override
            public void down() {
                moveDown();
            }

            @Override
            public void reset() {
                clientStateModel.getRootUIs().clear();
                for (ConnectorUI connector : clientStateModel.getConnectors()) {
                    clientStateModel.removeConnector(connector);
                }
                clientStateModel.getRootUIs().add(clientStateModel.getMoveComponentUI());
                clientStateModel.getRootUIs().add(clientStateModel.getZoomComponentUI());

            }

        });
    }

    public void doZoomOut() {
        clientStateModel.setInvertScale(clientStateModel.getInvertScale() * 0.8);
        clientStateModel.setScale(clientStateModel.getScale() * 1.25);

        getWidget().getContext2d().scale(1.25, 1.25);
        // needs to change the shift
        double translateX = clientStateModel.getTranslateX();
        if (translateX != 0) {
            clientStateModel.setTranslateX(translateX * 0.8);
        }
        double translateY = clientStateModel.getTranslateY();
        if (translateY != 0) {
            clientStateModel.setTranslateY(translateY * 0.8);
        }

        redraw();

    }

    public void doZoomIn() {
        clientStateModel.setScale(clientStateModel.getScale() * 0.8);
        clientStateModel.setInvertScale(clientStateModel.getInvertScale() * 1.25);
        getWidget().getContext2d().scale(0.8, 0.8);
        double translateX = clientStateModel.getTranslateX();
        if (translateX != 0) {
            clientStateModel.setTranslateX(translateX * 1.25);
        }
        double translateY = clientStateModel.getTranslateY();
        if (translateY != 0) {
            clientStateModel.setTranslateY(translateY * 1.25);
        }
        redraw();
    }

    public void moveLeft() {
        translate(clientStateModel.getTranslateOffset(),0);
    }

    public void moveRight() {
        translate(-clientStateModel.getTranslateOffset(), 0);
    }

    public void moveUp() {
        translate(0,clientStateModel.getTranslateOffset());
    }

    public void moveDown() {
        translate(0,-clientStateModel.getTranslateOffset());
    }

    protected void translate(double x, double y) {
        clientStateModel.setTranslateX(clientStateModel.getTranslateX() - x);
        clientStateModel.setTranslateY(clientStateModel.getTranslateY() - y);
        getWidget().getContext2d().translate(x, y);
        redraw();
    }

    /**
     * Clean the selectedUi field if the given ui is the one currently selected
     * @param ui potentially selected Ui
     */
    protected void cleanDeletedUiReferences(UI ui) {
        if (ui.equals(clientStateModel.getSelectedUI())) {
            clientStateModel.setSelectedUI(null);
        }
        if (ui.equals(clientStateModel.getCurrentMouseOverUI())) {
            clientStateModel.setCurrentMouseOverUI(null);
        }
        if (ui.equals(clientStateModel.getCurrentUI())) {
            clientStateModel.setCurrentUI(null);
        }
    }

    @Override
    protected void init() {
        super.init();

        // Register all the mouse events.
        getWidget().addMouseDownHandler(new DiagramClientMouseDownHandler(this, clientStateModel));
        getWidget().addMouseMoveHandler(new DiagramClientMouseMoveHandler(this, clientStateModel));
        getWidget().addMouseOutHandler(new DiagramClientMouseOutHandler(this, clientStateModel));
        getWidget().addMouseUpHandler(new DiagramClientMouseUpHandler(this, clientStateModel));
        getWidget().addClickHandler(new DiagramClientClickHandler(this, clientStateModel));
        getWidget().addMouseWheelHandler(new DiagramClientMouseWheelHandler(this, clientStateModel));

        // Key events
        getWidget().addKeyUpHandler(new DiagramClientKeyUpHandler(this, clientStateModel));

        MoveComponentUI moveComponentUI = new MoveComponentUI(this, clientStateModel, "moveComponent");
        clientStateModel.getRootUIs().add(moveComponentUI);
        clientStateModel.setMoveComponentUI(moveComponentUI);

        ZoomComponentUI zoomComponentUI = new ZoomComponentUI(this, clientStateModel, "zoomComponent");
        clientStateModel.getRootUIs().add(zoomComponentUI);
        clientStateModel.setZoomComponentUI(zoomComponentUI);

    }

    /**
     * Draw the component.
     */
    public void redraw() {

        // Perform the shift
        getWidget().getContext2d().clearRect(clientStateModel.getTranslateX() - 100, clientStateModel.getTranslateY() - 100, 5000, 5000);

        // Redraw root UIs
        for (UI ui : clientStateModel.getRootUIs()) {
            ui.draw();
        }

        // Notice that we're redrawin a UI that has already been draw (directly or indirectly) from the root UIs.
        // That leads to weird impressions: text bolder than they should be, ...
        // Not super hyper important, but deserves a note :)

        // Redraw the selected UI
        if (clientStateModel.getCurrentMouseOverUI() != null) {
            clientStateModel.getCurrentMouseOverUI().draw();
        }

        // Draw connectors
        for (ConnectorUI connector : clientStateModel.getConnectors()) {
            connector.draw();
        }

        // Draw temp ui if any
        if (clientStateModel.getTempDrawUI() != null) {
            clientStateModel.getTempDrawUI().draw();
        }

        UI moveComponentUI = clientStateModel.getMoveComponentUI();
        if (moveComponentUI != null) {
            moveComponentUI.draw();
        }

        UI zoomComponentUI = clientStateModel.getZoomComponentUI();
        if (zoomComponentUI != null) {
            zoomComponentUI.draw();
        }

    }


    /**
     * Unset the cursor
     */
    public void unsetCursor() {
        getWidget().getElement().getStyle().setCursor(AUTO);
    }

    /**
     * Defines the cursor.
     * @param cursor
     */
    public void setCursor(Cursor cursor) {
        getWidget().getElement().getStyle().setCursor(cursor);
    }


    /**
     * Creates the widget which is the HTML5 canvas.
     */
    @Override
    protected Widget createWidget() {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setStyleName("canvas-widget");
        return canvas;
    }

    /**
     * Gets the widget.
     */
    @Override
    public Canvas getWidget() {
        return (Canvas) super.getWidget();
    }

    /**
     * Gets the state shared between the client and the server.
     */
    @Override
    public DiagramComponentState getState() {
        return (DiagramComponentState) super.getState();
    }

    /**
     * Notification when the state is being changed.
     */
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
    }

    @Override
    public void layout() {

        // Update the height/width
        int newHeight = getWidget().getElement().getOffsetHeight();
        int newWidth = getWidget().getElement().getOffsetWidth();
        if (getWidget().getCoordinateSpaceHeight() != newHeight || getWidget().getCoordinateSpaceWidth() != newWidth) {
            getWidget().setCoordinateSpaceHeight(newHeight);
            getWidget().setCoordinateSpaceWidth(newWidth);
            getWidget().getContext2d().scale(clientStateModel.getScale(), clientStateModel.getScale());
            getWidget().getContext2d().translate(-clientStateModel.getTranslateX(), -clientStateModel.getTranslateY());
        }
    }

    @Override
    public void postLayout() {
        redraw();
    }

}

