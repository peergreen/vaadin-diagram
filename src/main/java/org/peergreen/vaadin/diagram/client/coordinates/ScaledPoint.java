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
package org.peergreen.vaadin.diagram.client.coordinates;

/**
 * Scaled point and the original point.
 * @author Florent Benoit
 */
public class ScaledPoint extends Point implements IScaledPoint {

    private final double originalX;

    private final double originalY;

    public ScaledPoint(double originalX, double originalY, double x, double y) {
        super(x, y);
        this.originalX = originalX;
        this.originalY = originalY;
    }

    @Override
    public double getOriginalX() {
        return originalX;
    }

    @Override
    public double getOriginalY() {
        return originalY;
    }

}
