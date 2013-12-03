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

package org.peergreen.vaadin.diagram.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.peergreen.vaadin.diagram.client.ClientStateModel;
import org.peergreen.vaadin.diagram.client.coordinates.IPoint;
import org.peergreen.vaadin.diagram.client.coordinates.IScaledPoint;
import org.peergreen.vaadin.diagram.client.event.IKeyUp;
import org.peergreen.vaadin.diagram.client.select.ISelectable;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.Image;

/**
 * User: guillaume
 * Date: 05/11/13
 * Time: 13:01
 */
public class CompartmentUI extends AbstractUI implements ISelectable, IKeyUp {

    private final EntityUI parent;
    private String name = "Anonymous";
    private final String fillStyle = "white";

    private final List<ProvideUI> provides = new ArrayList<ProvideUI>();
    private final List<RequireUI> requires = new ArrayList<RequireUI>();
    private final double innerBorderWidth = 5;
    private ImageElement iconImage;

    public CompartmentUI(final ClientStateModel model, final String id, EntityUI parent) {
        super(model, id);
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setIconUrl(final String iconUrl) {
        if (iconUrl != null) {
            this.iconImage = ImageElement.as(new Image(iconUrl).getElement());
        } else {
            this.iconImage = null;
        }
    }

    public double getHeight() {
        int units = Math.max(provides.size(), requires.size());
        if (units == 0) {
            units = 1;
        }
        return units * parent.getCompartmentBaseHeight();
    }

    private boolean isInside(IPoint point) {
        double width = parent.getWidth();
        double height = getHeight();
        double y = getY();
        double x = getX();

        return ((point.getX() >= x && point.getX() <= x + width)
                && (point.getY() >= y && point.getY() <= y + height));
    }

    public double getX() {
        return parent.getX();
    }

    public double getY() {
        return parent.getCompartmentOriginY(this);
    }

    @Override
    public void draw() {
        double width = parent.getWidth();
        double height = getHeight();
        double y = getY();
        double x = getX();

        getCanvas().save();
        getCanvas().setFillStyle(fillStyle);
        if (this.equals(getModel().getCurrentMouseOverUI())) {
            getCanvas().setShadowBlur(10);
            getCanvas().setShadowColor("#666");
            getCanvas().setFillStyle("#E8F4FF");
        }
        if (this.equals(getModel().getSelectedUI())) {
            getCanvas().setFillStyle("#E8F4FF");
        }
        getCanvas().strokeRect(x, y, width, height);
        getCanvas().fillRect(x, y, width, height);
        getCanvas().restore();

        drawContent(x, y, width, height);

        // Draw Ports
        for (RequireUI require : requires) {
            require.draw();
        }
        for (ProvideUI provide : provides) {
            provide.draw();
        }
    }

    private void drawContent(final double x, final double y, final double width, final double height) {

        double iconWidth;
        double iconHeight;
        double maxIconWidth = width / 5;
        double availHeight = height - 2 * innerBorderWidth;
        double availWidth = width - 2 * innerBorderWidth;
        // Select smallest of the width / height values
        if (availHeight < availWidth) {
            // constrained by the height
            if (maxIconWidth < availHeight) {
                // constrained by us
                iconHeight = maxIconWidth;
                iconWidth = maxIconWidth;
            } else {
                iconHeight = availHeight;
                iconWidth = availHeight;
            }
        } else {
            // constrained by the width
            if (maxIconWidth < availWidth) {
                // constrained by us
                iconHeight = maxIconWidth;
                iconWidth = maxIconWidth;
            } else {
                iconHeight = availWidth;
                iconWidth = availWidth;
            }
        }

        if (iconImage != null) {
            getCanvas().drawImage(
                    iconImage,
                    x + innerBorderWidth,
                    y + (height / 2) - (iconWidth / 2),
                    iconWidth,
                    iconHeight
            );
        }

        getCanvas().save();
        getCanvas().setFillStyle("black");
        getCanvas().setFont("14px arial");
        getCanvas().setTextBaseline(Context2d.TextBaseline.MIDDLE);
        getCanvas().fillText(name, x + 2 * innerBorderWidth + iconWidth, y + (height / 2), width - 3 * innerBorderWidth - iconWidth);
        getCanvas().restore();
    }

    @Override
    public UI getSelectedUI(final IScaledPoint point) {
        // Check ports
        for (RequireUI require : requires) {
            UI ui = require.getSelectedUI(point);
            if (ui != null) {
                return ui;
            }
        }
        for (ProvideUI provide : provides) {
            UI ui = provide.getSelectedUI(point);
            if (ui != null) {
                return ui;
            }
        }

        return isInside(point) ? this : null;
    }

    @Override
    public void keyUp(final KeyUpEvent event) {
        if (KeyCodes.KEY_DELETE == event.getNativeKeyCode() || KeyCodes.KEY_BACKSPACE == event.getNativeKeyCode() ) {
            getModel().getServerRpc().deleted(getId());
        }
    }

    public EntityUI getEntity() {
        return parent;
    }

    public double getPortOriginY(final RequireUI ui) {
        return getPortOriginY(requires, ui);
    }

    private double getPortOriginY(final List<? extends PortUI> ports, final PortUI ui) {
        int target = ports.indexOf(ui);
        double y = this.getY();
        int i = 0;
        while (i++ < target) {
            y += parent.getCompartmentBaseHeight();
        }
        return y;

    }

    public double getPortOriginY(final ProvideUI ui) {
        return getPortOriginY(provides, ui);
    }

    public double getWidth() {
        return parent.getWidth();
    }

    public void addRequire(RequireUI require) {
        requires.add(require);
    }

    public void removeRequire(RequireUI require) {
        requires.remove(require);
    }

    public void addProvide(ProvideUI provide) {
        provides.add(provide);
    }

    public void removeProvide(ProvideUI provide) {
        provides.remove(provide);
    }

    public List<RequireUI> getRequires() {
        return new ArrayList<RequireUI>(requires);
    }

    public List<ProvideUI> getProvides() {
        return new ArrayList<ProvideUI>(provides);
    }
}
