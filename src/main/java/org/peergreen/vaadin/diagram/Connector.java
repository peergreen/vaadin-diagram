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

import java.util.UUID;

/**
 * User: guillaume
 * Date: 05/11/13
 * Time: 12:26
 */
public class Connector extends AbstractElement {

    private Port source;
    private Port target;

    public Connector(Port source, Port target) {
        super(UUID.randomUUID());
        this.source = source;
        this.target = target;
    }

    public Port getSource() {
        return source;
    }

    public Port getTarget() {
        return target;
    }
}
