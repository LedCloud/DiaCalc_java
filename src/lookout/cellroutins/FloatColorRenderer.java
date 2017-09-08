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
 * Portions Copyrighted 2009-2017 Toporov Konstantin.
 */

package lookout.cellroutins;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Component;
import javax.swing.JTable;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import javax.swing.border.Border;
import javax.swing.BorderFactory; 

import javax.swing.UIManager;

import java.text.DecimalFormat;
import maths.User;
import maths.Sugar;


public class FloatColorRenderer extends DefaultTableCellRenderer{
   
    private final JFormattedTextField fld;
    private Border selectedBorder=null;
    private Border unselectedBorder=null;
    private final Border emptyBorder = BorderFactory.createEmptyBorder();
    private User user;
    private final static Color LO_COLOR = new Color(0xB1FFFA);
    private final static Color HI_COLOR = new Color(0xFFD5BE);
    private final static Color NORMAL_COLOR = new Color(0xCCFFC9);
    private final Sugar s = new Sugar();

    public FloatColorRenderer(String mask,User us) {
        super(); 
        fld = new JFormattedTextField(new DecimalFormat(mask));
        fld.setHorizontalAlignment(JTextField.RIGHT);
        fld.setEditable(false);
        
        user = us;
        //format.setMinimumFractionDigits(1);
        //format.setMaximumFractionDigits(1);
    }
    public void changeUser(User us){
        user = us;
    }
    @Override 
    public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        fld.setValue(value);
        if (value!=null){
            float v = (Float)value;
            s.setSugar(v, user.isMmol(), user.isPlasma());
            if (s.getValue()<user.getLowSh().getValue()){
                fld.setBackground(LO_COLOR);
            }else if (s.getValue()>user.getHiSh().getValue()){
                fld.setBackground(HI_COLOR);
            }else{
                fld.setBackground(NORMAL_COLOR);
            }
        }
        //fld.setText(format.format(value));
        if (isSelected){
            if (value==null) fld.setBackground(table.getSelectionBackground());
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
            if (value==null) fld.setBackground(table.getBackground());
            fld.setBorder(emptyBorder);
            }
 
     return fld;
    }

}
