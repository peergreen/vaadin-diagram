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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.vaadin.server.StreamResource;

/**
 * User: guillaume
 * Date: 05/11/13
 * Time: 12:26
 */
public class Compartment extends AbstractElement {

    public static final String DEFAULT_ICON_TYPE = "default-compartment.png";
    private String name;
    private URL iconUrl;
    private String iconType = DEFAULT_ICON_TYPE;
    private final List<Provide> provides = new ArrayList<Provide>();
    private final List<Require> requires = new ArrayList<Require>();

    public Compartment(final String name) {
        super(UUID.randomUUID());
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl.toString();
    }

    public void setIconUrl(final URL iconUrl) {
        this.iconUrl = iconUrl;
        if (iconUrl != null) {
            this.iconType = iconUrl.toString().substring(iconUrl.toString().lastIndexOf('/') + 1);
        }
    }

    public String getIconType() {
        return iconType;
    }

    @Override
    public void attach(final Diagram diagram) {
        // If there is an icon provided, wrap it in a StreamResource and make it available to client-side through the connector
        if (iconUrl != null) {
            diagram.addSharedResource(iconType, new StreamResource(new IconStreamSource(), iconType));
        }
        super.attach(diagram);
    }

    public void addProvide(Provide provide) {
        provides.add(provide);
        if (isAttached()) {
            getDiagram().addProvide(this, provide);
        }
    }

    public void removeProvide(Provide provide) {
        provides.remove(provide);
        if (isAttached()) {
            getDiagram().removeProvide(this, provide);
        }
    }

    public void addRequire(Require require) {
        requires.add(require);
        if (isAttached()) {
            getDiagram().addRequire(this, require);
        }
    }

    public void removeRequire(Require require) {
        requires.remove(require);
        if (isAttached()) {
            getDiagram().removeRequire(this, require);
        }
    }

    public List<Require> getRequires() {
        return new ArrayList<Require>(requires);
    }

    public List<Provide> getProvides() {
        return new ArrayList<Provide>(provides);
    }

    private class IconStreamSource implements StreamResource.StreamSource {

        /**
         * Serial Version UID.
         */
        private static final long serialVersionUID = -7005064725668793901L;

        @Override
        public InputStream getStream() {
            try {
                return iconUrl.openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
