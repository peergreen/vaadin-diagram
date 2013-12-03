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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.peergreen.vaadin.diagram.client.coordinates.IPoint;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.coordinates.ScaledPoint;
import org.peergreen.vaadin.diagram.client.ui.AbstractUI;
import org.peergreen.vaadin.diagram.client.ui.CompartmentUI;
import org.peergreen.vaadin.diagram.client.ui.ConnectorUI;
import org.peergreen.vaadin.diagram.client.ui.EntityUI;
import org.peergreen.vaadin.diagram.client.ui.PortUI;
import org.peergreen.vaadin.diagram.client.ui.ProvideUI;
import org.peergreen.vaadin.diagram.client.ui.RequireUI;
import org.peergreen.vaadin.diagram.client.ui.UI;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseEvent;

/**
 * The client state model is only used on the client side and contains helper methods used by UIs.
 * @author Florent Benoit
 */
public class ClientStateModel {

    private final Context2d canvas;
    private final IDiagramServerRpc rpc;

    private UI currentMouseOverUI;
    private UI currentUI;
    private UI selectedUI;
    private UI tempDrawUI;
    private UI moveComponentUI;
    private UI zoomComponentUI;

    private double translateOffset = 20;

    private  double scale = 1.0;
    private  double invertScale = 1.0;

    private final List<UI> rootUIs = new ArrayList<UI>();

    private final LinkedList<ConnectorUI> connectors = new LinkedList<ConnectorUI>();

    private IPoint mouseCoordinates;

    /**
     * Offset used to translate drawing on the X coordinate.
     */
    private double translateX = 0;


    /**
     * Offset used to translate drawing on the Y coordinate.
     */
    private double translateY = 0;

    private final List<AbstractUI> globalEventCallbacks = new ArrayList<AbstractUI>();


    public ClientStateModel(Context2d canvas, final IDiagramServerRpc serverRpc) {
        this.canvas = canvas;
        rpc = serverRpc;
    }

    public Context2d getCanvas() {
        return canvas;
    }



    public UI getCurrentMouseOverUI() {
        return currentMouseOverUI;
    }

    public void setCurrentMouseOverUI(UI currentMouseOverUI) {
        this.currentMouseOverUI = currentMouseOverUI;
    }

    public UI getCurrentUI() {
        return currentUI;
    }

    public void setCurrentUI(UI currentUI) {
        this.currentUI = currentUI;
    }

    public UI getSelectedUI() {
        return selectedUI;
    }

    public void setSelectedUI(UI selectedUI) {
        this.selectedUI = selectedUI;
        if (selectedUI != null) {
            rpc.selected(selectedUI.getId());
        }
    }



    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setInvertScale(double invertScale) {
        this.invertScale = invertScale;
    }

    public double getInvertScale() {
        return invertScale;
    }

    public UI getTempDrawUI() {
        return tempDrawUI;
    }

    public void setTempDrawUI(UI tempDrawUI) {
        this.tempDrawUI = tempDrawUI;
    }

    public List<AbstractUI> getGlobalEventCallbacks() {
        return globalEventCallbacks;
    }


    public List<UI> getRootUIs() {
        return rootUIs;
    }

    public List<UI> getAllUIs() {
        List<UI> uis = new ArrayList<UI>();
        // We store connectors before root UIs because it allows to always match a connector before a shape UI (Z axis)
        uis.addAll(connectors);
        uis.addAll(rootUIs);
        return uis;
    }



    public IPoint getMouseCoordinates() {
        return mouseCoordinates;
    }

    public void setMouseCoordinates(IPoint mouseCoordinates) {
        this.mouseCoordinates = mouseCoordinates;
    }


    /**
     * Find the scaling point hit by the mouse which is varying from the scale of the canvas.
     * @param event the mouse event
     * @return the updated coordinates
     */
    public IScaledPoint buildScalePoint(MouseEvent<?> event) {
        // Scale
        return scalePoint(event.getX(), event.getY());
    }

    public IScaledPoint scalePoint(final double x, final double y) {
        return new ScaledPoint(x, y, translateX + (x * invertScale), translateY + (y * invertScale));
    }

    public double getTranslateX() {
        return translateX;
    }

    public double getTranslateY() {
        return translateY;
    }

    public void setTranslateX(double translateX) {
        this.translateX = translateX;
    }

    public void setTranslateY(double translateY) {
        this.translateY = translateY;
    }


    public EntityUI findEntity(final String uuid) {
        for (UI ui : rootUIs) {
            if (uuid.equals(ui.getId())) {
                if (ui instanceof EntityUI) {
                    return (EntityUI) ui;
                }
            }
        }

        return null;
    }

    /**
     * @return an immutable copy of the list
     */
    public List<ConnectorUI> getConnectors() {
        //Collections.unmodifiableList(connectors);
        return new ArrayList<ConnectorUI>(connectors);
    }

    public CompartmentUI findCompartment(final String uuid) {
        for (UI rootUI : rootUIs) {
            if (rootUI instanceof EntityUI) {
                EntityUI entity = (EntityUI) rootUI;
                for (CompartmentUI compartment : entity.getCompartments()) {
                    if (uuid.equals(compartment.getId())) {
                        return compartment;
                    }
                }
            }
        }
        return null;
    }

    public PortUI findPort(final String uuid) {
        for (UI rootUI : rootUIs) {
            if (rootUI instanceof EntityUI) {
                EntityUI entity = (EntityUI) rootUI;
                for (CompartmentUI compartment : entity.getCompartments()) {
                    for (RequireUI require : compartment.getRequires()) {
                        if (uuid.equals(require.getId())) {
                            return require;
                        }
                    }
                    for (ProvideUI provide : compartment.getProvides()) {
                        if (uuid.equals(provide.getId())) {
                            return provide;
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<ConnectorUI> findConnectors(final PortUI port) {
        List<ConnectorUI> uis = new ArrayList<ConnectorUI>();
        for (ConnectorUI connector : connectors) {
            if (port.equals(connector.getSourcePort()) || port.equals(connector.getTargetPort())) {
                uis.add(connector);
            }
        }
        return uis;
    }

    public void addConnector(ConnectorUI connector) {
        for (ConnectorUI ui : connectors) {
            // If we already have the same connector
            if (ui.isSame(connector)) {
                return;
            }
        }
        connectors.add(connector);
    }

    public void removeConnector(ConnectorUI connector) {
        connectors.remove(connector);
    }

    public IDiagramServerRpc getServerRpc() {
        return rpc;
    }

    public UI getMoveComponentUI() {
        return moveComponentUI;
    }

    public void setMoveComponentUI(UI moveComponentUI) {
        this.moveComponentUI = moveComponentUI;
    }

    public UI getZoomComponentUI() {
        return zoomComponentUI;
    }

    public void setZoomComponentUI(UI zoomComponentUI) {
        this.zoomComponentUI = zoomComponentUI;
    }

    public double getTranslateOffset() {
        return translateOffset;
    }

    public void setTranslateOffset(double translateOffset) {
        this.translateOffset = translateOffset;
    }


}
