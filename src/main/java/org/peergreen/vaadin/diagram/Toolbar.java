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

import static java.lang.String.format;

import java.util.Map;

import com.vaadin.event.Transferable;
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

    private final GridLayout gridLayout;

    public Toolbar(int columns, int rows) {
        super("Toolbar");
        setModal(false);
        setClosable(false);
        setResizable(false);
        setDraggable(true);
        this.gridLayout = new GridLayout(columns, rows);
        setContent(gridLayout);
    }


    public void addEntry(int columnId, int rowId, final ToolbarItem toolbarItem) {
        Entry entry = new Entry(toolbarItem);
        DragAndDropWrapper drag = new DragAndDropWrapper(entry) {
            @Override
            public Transferable getTransferable(final Map<String, Object> variables) {
                variables.put("component-type", toolbarItem.getName());
                return super.getTransferable(variables);
            }
        };
        drag.setSizeUndefined();
        drag.setDragStartMode(DragAndDropWrapper.DragStartMode.COMPONENT);
        gridLayout.addComponent(drag, columnId, rowId);
    }


    private static class Entry extends Image {
        private Entry(ToolbarItem toolbarItem) {
            setSource(toolbarItem.getIconResource());
            setWidth("45px");
            setHeight("45px");
            setDescription(format("%s (%s Wrapper)", toolbarItem.getName(), toolbarItem.getDescription()));
        }
    }

}
