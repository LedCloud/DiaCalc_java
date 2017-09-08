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

package tablemodels;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import javax.swing.table.AbstractTableModel;

public class MenuModel extends AbstractTableModel{
    //калории, доза, белки, жиры, угл, ГИ, ГН;
    private boolean calor = true;
    private boolean dose = true;
    private boolean prot = false;
    private boolean fat = false;
    private boolean carb = false;
    private boolean Gi = false;
    private boolean GL = false;

    public MenuModel(int mask){
        calor = (mask&MenuTableModel.CALOR)>0;
        dose = (mask&MenuTableModel.DOSE)>0;
        prot = (mask&MenuTableModel.PROT)>0;
        fat = (mask&MenuTableModel.FAT)>0;
        carb = (mask&MenuTableModel.CARB)>0;
        Gi = (mask&MenuTableModel.Gi)>0;
        GL = (mask&MenuTableModel.GL)>0;
    }

    @Override
    public Object getValueAt(int row,int col){
        switch (col){
             case 0: return true;
             case 1: return true;
             case 2: return calor;
            case 3: return dose;
            case 4: return prot;
            case 5: return fat;
            case 6: return carb;
            case 7: return Gi;
            case 8: return GL;
        }
        return null;
    }
    @Override
    public int getColumnCount(){
        return 9;
    }
    @Override
    public int getRowCount(){
        return 1;
    }

    @Override
    public boolean isCellEditable(int row, int col){
        return col>1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int col){
        
        switch (col){
            case 2: calor = (Boolean)aValue; break;
            case 3: dose = (Boolean)aValue; break;
            case 4: prot = (Boolean)aValue; break;
            case 5: fat = (Boolean)aValue; break;
            case 6: carb = (Boolean)aValue; break;
            case 7: Gi = (Boolean)aValue; break;
            case 8: GL = (Boolean)aValue;
        }
        
        this.fireTableCellUpdated(rowIndex, col);
    }

    @Override
    public Class getColumnClass(int c) {
        return Boolean.class;
    }

    @Override
    public String getColumnName(int col) {
        switch (col){
            case 0: return "Наименование";
            case 1: return "Вес";
            case 2: return "Калор";
            case 3: return "Доза";
            case 4: return "Б";
            case 5: return "Ж";
            case 6: return "У";
            case 7: return "ГИ";
            case 8: return "ГН";
        }
        return "";
    }

    public int getMask(){
        int mask = 0;
        if (calor) mask = MenuTableModel.CALOR;
        if (dose) mask += MenuTableModel.DOSE;
        if (prot) mask += MenuTableModel.PROT;
        if (fat) mask += MenuTableModel.FAT;
        if (carb) mask += MenuTableModel.CARB;
        if (Gi) mask += MenuTableModel.Gi;
        if (GL) mask += MenuTableModel.GL;
        return mask;
    }

    public void setMask(int mask){
        calor = (mask&MenuTableModel.CALOR)>0;
        //System.out.println(mask+" "+(mask&MenuTableModel.CALOR));
        //System.out.println((mask&MenuTableModel.CALOR).class);
        dose = (mask&MenuTableModel.DOSE)>0;
        //System.out.println(mask+" "+(mask&MenuTableModel.DOSE));
        prot = (mask&MenuTableModel.PROT)>0;
        //System.out.println(mask+" "+(mask&MenuTableModel.PROT));
        fat = (mask&MenuTableModel.FAT)>0;
        //System.out.println(mask+" "+(mask&MenuTableModel.FAT));
        carb = (mask&MenuTableModel.CARB)>0;
        //System.out.println(mask+" "+(mask&MenuTableModel.CARB));
        Gi = (mask&MenuTableModel.Gi)>0;

        GL = (mask&MenuTableModel.GL)>0;
        //System.out.println(mask+" "+(mask&MenuTableModel.GL));
    }


}
