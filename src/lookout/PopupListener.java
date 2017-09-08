/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Toporov Konstantin. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 3 only ("GPL")  (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.gnu.org/copyleft/gpl.html  See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * You should include file containing license in each project.
 * If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by
 * the GPL Version 3, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [GPL Version 3] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under the GPL Version 3 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 3 code and therefore, elected the GPL
 * Version 3 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Toporov Konstantin.
 */

package lookout;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import java.awt.event.*;
import javax.swing.*;

public class PopupListener extends MouseAdapter {
        private JPopupMenu popup;

        public PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

    @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

    @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if (e.getSource() instanceof JTable){
                    int row = ((JTable)e.getSource()).rowAtPoint(e.getPoint());
                    if (row>=0) ((JTable)e.getSource()).getSelectionModel()
                            .setSelectionInterval(row, row);
                }
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
    }
