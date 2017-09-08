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

package lookout.datepicker;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;

public class MonthRender extends DefaultTableCellRenderer{
    private JTextField fld;
    
    private static final Color CAL_WHITE = Color.WHITE;
    private static final Color CAL_LIGHT_GRAY = new Color(0xe9eff8);
    private static final Color CAL_WHITE_GREEN = new Color(0xd8ffeb);
    private static final Color CAL_LIGHT_GRAY_GREEN = new Color(0xc8efdc);
    private int [] dayofweek = new int [7];
    
    public MonthRender(){
        fld = new JTextField();
        fld.setEditable(false);
        fld.setHorizontalAlignment(JTextField.CENTER);
        fld.setBorder(BorderFactory.createEmptyBorder());
        
        int fdw = Calendar.getInstance().getFirstDayOfWeek();
        int dw = 0;
        for(int i=fdw;i<=Calendar.SATURDAY;i++){
            dayofweek[dw++] = i;
        }
        if (fdw>Calendar.SUNDAY){
            int day = Calendar.SUNDAY;
            while (dw<7){
               dayofweek[dw++] = day++;
            }
        }
    }
    
    @Override 
    public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        int day = ((Number)value).intValue();
        if (day<0){
            fld.setFont(fld.getFont().deriveFont(Font.PLAIN));
            fld.setForeground(Color.GRAY);
            day *= -1;
        }else if (day>100 && day<1000){
            day -= 100;
            fld.setFont(fld.getFont().deriveFont(Font.PLAIN));
            fld.setForeground(Color.GRAY);
        }else if (day>1000){
            day -= 1000;
            fld.setFont(fld.getFont().deriveFont(Font.BOLD));
            fld.setForeground(Color.BLACK);
        }else{
            fld.setFont(fld.getFont().deriveFont(Font.PLAIN));
            fld.setForeground(Color.BLACK);
        }
        
        fld.setText(""+day);
        
          //Не выбрано и нет фокуса
        if (row%2==0){
            
            if (dayofweek[column]==Calendar.SUNDAY || 
                    dayofweek[column]==Calendar.SATURDAY){
                fld.setBackground(CAL_LIGHT_GRAY_GREEN);
            }else{
                fld.setBackground(CAL_LIGHT_GRAY);
            }
        }
        else{
            if (dayofweek[column]==Calendar.SUNDAY || 
                    dayofweek[column]==Calendar.SATURDAY){
                fld.setBackground(CAL_WHITE_GREEN);
            }else{
                
                fld.setBackground(CAL_WHITE);
            }
            
        }
     return fld;
    }
}
