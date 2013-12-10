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

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.MouseEventDetails;

/**
 * Dop Handler for the Diagram component.
 * @author Florent Benoit
 */
public class DiagramDropHandler implements DropHandler {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 7565439449126548219L;

    private final Diagram diagram;

    public DiagramDropHandler(Diagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public void drop(final DragAndDropEvent event) {
        TargetDetails targetDetails = event.getTargetDetails();
        Transferable transferable = event.getTransferable();

        MouseEventDetails mouseEventDetails = MouseEventDetails.deSerialize((String) targetDetails.getData("mouseEvent"));
        Integer absoluteLeft = (Integer) targetDetails.getData("absoluteLeft");
        Integer absoluteTop = (Integer) targetDetails.getData("absoluteTop");

        diagram.drop(
                mouseEventDetails.getClientX() - absoluteLeft,
                mouseEventDetails.getClientY() - absoluteTop,
                (String) transferable.getData("component-type")
                );
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }
}
