/**
 * Copyright 2013 Orange S.A. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.peergreen.vaadin.diagram;

import static com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vaadin.event.Transferable;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Window;

/**
 * Toolbar will allow to drag and drop images on the diagram area and then
 *
 * @author Florent Benoit
 */
public class Toolbar extends Window {

    public static final int DEFAULT_NUMBER_OF_COLUMNS = 1;
    public static final int DEFAULT_DISPLAYED_ROWS = 6;

    public static final Integer IMAGE_LENGTH = 45;
    public static final int PADDING = 4;

    private final GridLayout layout;
    private final int columns;
    private final int rows;
    private int imageLength;

    private final List<ToolbarItem> items = new ArrayList<ToolbarItem>();

    public Toolbar() {
        this(DEFAULT_NUMBER_OF_COLUMNS, DEFAULT_DISPLAYED_ROWS);
    }

    public Toolbar(int columns, final int rows) {
        super("Tools");
        setModal(false);
        setClosable(false);
        setResizable(false);
        setDraggable(true);
        addStyleName("diagram-toolbar");

        this.columns = columns;
        this.rows = rows;

        this.layout = new GridLayout(this.columns, 1);
        layout.setSizeFull();
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(layout);
        setImageLength(IMAGE_LENGTH);

    }

    public void setImageLength(final int length) {
        imageLength = length;
        setWidth(columns * (imageLength + 10), Unit.PIXELS);
        redraw();
    }

    public void addEntry(final ToolbarItem item) {
        items.add(item);
        addItemInLayout(item);
    }

    public void removeEntry(ToolbarItem item) {
        items.remove(item);
        redraw();
    }

    private void redraw() {
        layout.removeAllComponents();
        for (ToolbarItem item : items) {
            addItemInLayout(item);
        }
    }

    private void addItemInLayout(final ToolbarItem item) {
        // Append the item as last entry
        layout.addComponent(createDragEntry(item));
        layout.setHeight(Math.min(rows, layout.getRows()) * (imageLength + PADDING), Unit.PIXELS);
    }

    private Component createDragEntry(final ToolbarItem item) {
        Entry entry = new Entry(item);
        DragAndDropWrapper drag = new DragAndDropWrapper(entry) {
            @Override
            public Transferable getTransferable(final Map<String, Object> variables) {
                variables.put("component-type", item.getName());
                return super.getTransferable(variables);
            }
        };
        drag.setSizeUndefined();
        drag.setDragStartMode(DragStartMode.COMPONENT);
        return drag;
    }


    private class Entry extends Image {
        private Entry(ToolbarItem item) {
            setSource(item.getIconResource());
            setWidth(imageLength, Unit.PIXELS);
            setHeight(imageLength, Unit.PIXELS);
            setDescription(format("%s (%s Wrapper)", item.getName(), item.getDescription()));
        }
    }

}
