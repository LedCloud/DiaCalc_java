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

package lookout.cellroutins;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import lookout.settings.ProgramSettings;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Component;
import javax.swing.JTable;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import javax.swing.border.Border;
import javax.swing.BorderFactory; 

import javax.swing.UIManager;

import java.text.DecimalFormat;


public class FloatRenderer extends DefaultTableCellRenderer{
   
    private JFormattedTextField fld;
    private Border selectedBorder=null;
    private Border unselectedBorder=null;
    private Border emptyBorder = BorderFactory.createEmptyBorder();
    
    public FloatRenderer(String mask) {
        super(); 
        fld = new JFormattedTextField(new DecimalFormat(mask));
        fld.setHorizontalAlignment(JTextField.RIGHT);
        fld.setEditable(false);
        //format.setMinimumFractionDigits(1);
        //format.setMaximumFractionDigits(1);
    }
    
    @Override 
    public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        fld.setValue(value);
        //fld.setText(format.format(value));
        if (isSelected){
            fld.setBackground(table.getSelectionBackground());
            if (hasFocus) {
                if (selectedBorder == null) {
                selectedBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
                
                }
                fld.setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(1,1,1,1,
                                              table.getSelectionBackground());
                }
                fld.setBorder(unselectedBorder);
            }
        }
        else { //Не выбрано и нет фокуса
            fld.setBackground(table.getBackground());
            fld.setBorder(emptyBorder);
            }
 
     return fld;
    }

}
