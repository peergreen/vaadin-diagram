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
package org.peergreen.vaadin.diagram.client.event;

import com.google.gwt.event.dom.client.KeyUpEvent;

/**
 * Notify the selected item (if any) with the key.
 * @author Florent Benoit
 */
public interface IKeyUp {

    /**
     * Notification when a key has been pressed (key up)
     * @param event
     */
    void keyUp(KeyUpEvent event);
}