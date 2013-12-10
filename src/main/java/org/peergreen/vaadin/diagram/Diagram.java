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

package org.peergreen.vaadin.diagram;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import org.peergreen.vaadin.diagram.client.DiagramComponentState;
import org.peergreen.vaadin.diagram.client.IDiagramClientRpc;
import org.peergreen.vaadin.diagram.client.IDiagramServerRpc;

import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.TargetDetailsImpl;
import com.vaadin.server.ClassResource;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.ui.LegacyComponent;
import com.vaadin.util.ReflectTools;

/**
 * Diagram component that has to be embedded by applications wanted to use Diagram addon.
 * @author Florent Benoit
 */

public class Diagram extends com.vaadin.ui.AbstractComponent implements DropTarget, LegacyComponent {

    /**
     * Attached elements.
     */
    private final Map<String, Element> elements = new HashMap<String, Element>();

    /**
     *
     */
    private static final long serialVersionUID = 56997080379191390L;

    /**
     * Drop Handler.
     */
    private final DropHandler dropHandler;


    private final IDiagramClientRpc clientRPC = getRpcProxy(IDiagramClientRpc.class);

	public Diagram() {
	    // Drop handler
        this.dropHandler = new DiagramDropHandler(this);
        markAsDirty();
	    // register server RPC
		registerRpc(new IDiagramServerRpc() {

            @Override
            public void selected(final String uuid) {
                fireSelected(uuid);
            }

            @Override
            public void deleted(final String uuid) {
                fireDeleted(uuid);
            }

            @Override
            public void createConnector(final String sourceUuid, final String targetUuid) {
                fireConnectorCreation(sourceUuid, targetUuid);
            }

            @Override
            public void dropTarget(final String uuid, final String data) {
                fireDropped(uuid, data);
            }

            @Override
            public void log(final String message) {
                fireLog(message);
            }

        });
        addSharedResource(Compartment.DEFAULT_ICON_TYPE, new ClassResource("ovf-icon.png"));
	}

    private void fireLog(final String message) {
        fireEvent(new LogEvent(message));
    }

    public void addSharedResource(String name, Resource resource) {
        setResource(name, resource);
    }

    private void fireDropped(final String uuid, final String data) {
        Element e = null;
        if (elements.containsKey(uuid)) {
            e = elements.get(uuid);
        }
        fireEvent(new DroppedEvent(this, e, data));
    }

    private void fireDeleted(final String uuid) {
        Element e = elements.get(uuid);
        if (e != null) {
            fireEvent(new DeletedEvent(e));
        }
    }

    private void fireConnectorCreation(final String sourceUuid, final String targetUuid) {
        Element source = elements.get(sourceUuid);
        Element target = elements.get(targetUuid);

        // Need valid uuid/elements
        if ((source != null) && (target != null)) {
            // Need also Port
            if ((source instanceof Port) && (target instanceof Port)) {
                fireEvent(new ConnectorCreationEvent((Port) source, (Port) target));
            }
        }
    }

    private void fireSelected(final String uuid) {
        Element e = elements.get(uuid);
        if (e != null) {
            fireEvent(new SelectedEvent(e));
        }
    }

    @Override
	public DiagramComponentState getState() {
		return (DiagramComponentState) super.getState();
	}

    public void addEntity(Entity entity) {
        attach(entity);
        String uuid = entity.getUuid();
        clientRPC.createEntity(uuid, entity.getName());
        for (Compartment compartment : entity.getCompartments()) {
            addCompartment(entity, compartment);
        }
    }

    public void removeEntity(Entity entity) {
        for (Compartment compartment : entity.getCompartments()) {
            entity.removeCompartment(compartment);
        }
        clientRPC.deleteEntity(entity.getUuid());
        detach(entity);
    }

    public void updateEntity(Entity entity, String name) {
        clientRPC.updateEntity(entity.getUuid(), name);
    }


    private void attach(final Element element) {
        element.attach(this);
        elements.put(element.getUuid(), element);
    }

    private void detach(final Element element) {
        element.detach();
        elements.remove(element.getUuid());
    }

    public void addCompartment(final Entity entity, final Compartment compartment) {
        if (!entity.isAttached()) {
            addEntity(entity);
        } else {
            attach(compartment);
            String cId = compartment.getUuid();
            clientRPC.createCompartment(entity.getUuid(), cId, compartment.getName(), compartment.getIconType());
            for (Require require : compartment.getRequires()) {
                addRequire(compartment, require);
            }
            for (Provide provide : compartment.getProvides()) {
                addProvide(compartment, provide);
            }
        }
    }

    public void removeCompartment(final Entity entity, final Compartment compartment) {
        for (Provide provide : compartment.getProvides()) {
            compartment.removeProvide(provide);
        }
        for (Require require : compartment.getRequires()) {
            compartment.removeRequire(require);
        }
        clientRPC.deleteCompartment(compartment.getUuid());
        detach(compartment);
    }

    public void addProvide(final Compartment compartment, final Provide provide) {
        if (compartment.isAttached()) {
            attach(provide);
            clientRPC.createProvide(
                    compartment.getUuid(),
                    provide.getUuid(),
                    provide.getName()
            );
            for (Connector connector : provide.getConnectors()) {
                addConnector(connector);
            }
        }
    }

    public void removeProvide(final Compartment compartment, final Provide provide) {
        // Disconnect all connectors to this port
        provide.disconnect(provide);
        clientRPC.deleteProvide(provide.getUuid());
        detach(provide);
    }

    public void addRequire(final Compartment compartment, final Require require) {
        if (compartment.isAttached()) {
            attach(require);
            clientRPC.createRequire(
                    compartment.getUuid(),
                    require.getUuid(),
                    require.getName()
            );
            for (Connector connector : require.getConnectors()) {
                addConnector(connector);
            }

        }
    }

    public void removeRequire(final Compartment compartment, final Require require) {
        // Disconnect all connectors to this port
        require.disconnect(require);
        clientRPC.deleteRequire(require.getUuid());
        detach(require);
    }

    public void addConnector(final Connector connector) {
        attach(connector);
        clientRPC.createConnector(
                connector.getUuid(),
                connector.getSource().getUuid(),
                connector.getTarget().getUuid()
        );
    }

    public void removeConnector(final Connector connector) {
        clientRPC.deleteConnector(connector.getUuid());
        detach(connector);
    }

    public void reset() {
        clientRPC.reset();
    }

    public void init() {
        clientRPC.init();
	}

    public void zoomIn() {
        clientRPC.zoomIn();
    }

    public void zoomOut() {
        clientRPC.zoomOut();
    }

    public void left() {
        clientRPC.left();
    }

    public void right() {
        clientRPC.right();
    }

    public void up() {
        clientRPC.up();
    }

    public void down() {
        clientRPC.down();
    }

    public void drop(int x, int y, String data) {
        clientRPC.handleDrop(x, y, data);
    }

    public interface ElementSelectionListener extends Serializable {
        public static final Method ELEMENT_SELECTED_METHOD = ReflectTools
                .findMethod(ElementSelectionListener.class, "selected",
                        SelectedEvent.class);

        void selected(SelectedEvent event);
    }

    public interface DroppedListener extends Serializable {
        public static final Method DROPPED_METHOD = ReflectTools
                .findMethod(DroppedListener.class, "dropped",
                        DroppedEvent.class);

        void dropped(DroppedEvent event);
    }

    public interface DeletedListener extends Serializable {
        public static final Method DELETED_METHOD = ReflectTools
                .findMethod(DeletedListener.class, "deleted",
                        DeletedEvent.class);

        void deleted(DeletedEvent event);
    }

    public interface ConnectorListener extends Serializable {
        public static final Method CONNECTOR_CREATED_METHOD = ReflectTools
                .findMethod(ConnectorListener.class, "created",
                        ConnectorCreationEvent.class);

        void created(ConnectorCreationEvent event);
    }

    public interface LogListener extends Serializable {
        public static final Method LOG_METHOD = ReflectTools
                .findMethod(LogListener.class, "log",
                        LogEvent.class);

        void log(LogEvent event);
    }

    public static class SelectedEvent extends EventObject {

        private SelectedEvent(final Element element) {
            super(element);
        }

        public Element getElement() {
            return (Element) getSource();
        }
    }

    public static class DeletedEvent extends EventObject {

        private DeletedEvent(final Element element) {
            super(element);
        }

        public Element getElement() {
            return (Element) getSource();
        }
    }

    public static class DroppedEvent extends EventObject {

        private final Element element;
        private final String data;

        private DroppedEvent(final Diagram diagram, final Element element, String data) {
            super(diagram);
            this.element = element;
            this.data = data;
        }

        public Element getElement() {
            return element;
        }

        public String getData() {
            return data;
        }
    }

    public static class ConnectorCreationEvent extends EventObject {

        private final Port target;

        private ConnectorCreationEvent(final Port source, final Port target) {
            super(source);
            this.target = target;
        }

        @Override
        public Port getSource() {
            return (Port) super.getSource();
        }

        public Port getTarget() {
            return target;
        }
    }


    public static class LogEvent extends EventObject {

        private LogEvent(final String message) {
            super(message);
        }

        public String getMessage() {
            return (String) getSource();
        }
    }
    public void addSelectionListener(ElementSelectionListener listener) {
        this.addListener(SelectedEvent.class, listener, ElementSelectionListener.ELEMENT_SELECTED_METHOD);
    }

    public void removeSelectionListener(ElementSelectionListener listener) {
        this.removeListener(SelectedEvent.class, listener);
    }

    public void addConnectorListener(ConnectorListener listener) {
        this.addListener(ConnectorCreationEvent.class, listener, ConnectorListener.CONNECTOR_CREATED_METHOD);
    }

    public void removeConnectorListener(ConnectorListener listener) {
        this.removeListener(ConnectorCreationEvent.class, listener);
    }

    public void addDroppedListener(DroppedListener listener) {
        this.addListener(DroppedEvent.class, listener, DroppedListener.DROPPED_METHOD);
    }

    public void removeDroppedListener(DroppedListener listener) {
        this.removeListener(DroppedEvent.class, listener);
    }

    public void addDeletedListener(DeletedListener listener) {
        this.addListener(DeletedEvent.class, listener, DeletedListener.DELETED_METHOD);
    }

    public void removeDeletedListener(DeletedListener listener) {
        this.removeListener(DeletedEvent.class, listener);
    }

    public void addLogListener(LogListener listener) {
        this.addListener(LogEvent.class, listener, LogListener.LOG_METHOD);
    }

    public void removeLogListener(LogListener listener) {
        this.removeListener(LogEvent.class, listener);
    }

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return new TargetDetailsImpl(clientVariables, this);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (getDropHandler() != null) {
            getDropHandler().getAcceptCriterion().paint(target);
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

    }
}
