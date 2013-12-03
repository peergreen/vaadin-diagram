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
package org.peergreen.vaadin.diagram.client.select;

import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.ui.UI;

/**
 * UI objects implementing this interface can be selected and return the underlying selected object.
 * @author Florent Benoit
 */
public interface ISelectable {

    /**
     * @param point the point that has been hit.
     * @return the selected ui if there is a matching UI for the given point
     */
    UI getSelectedUI(IScaledPoint point);

}
