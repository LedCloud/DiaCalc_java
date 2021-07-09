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

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import products.ProdGroup;
import manager.ArcGroupManager;
//import lookout.settings.ProgramSettings;

public class ArcGroupTableModel extends AbstractTableModel {
    private ArrayList<ProdGroup> groups;
    private final ArcGroupManager mgr = new ArcGroupManager();
    //private final ProgramSettings settings = ProgramSettings.getInstance();

    // Модель при создании получает список групп
    public ArcGroupTableModel(){
        groups = new ArrayList(mgr.getGroups());
    }

    public void updateGroup(ProdGroup gr){
        groups = new ArrayList(mgr.updateGroup(gr));
        //fireTableRowsUpdated(0,groups.size()-1);
        fireTableDataChanged();
    }

    public void addGroup(ProdGroup gr){
        if ( (groups.size()>0)&&(gr.getSortInd()==0) ){

            gr.setSortInd(   ((ProdGroup) groups.get(groups.size()-1)).getSortInd()+1
                    );
        }
        groups = new ArrayList(mgr.addGroup(gr));
        //fireTableRowsUpdated(0,groups.size()-1);
        fireTableDataChanged();
    }

    public void deleteGroup(ProdGroup gr){
        if (groups.size()>0){
            int row= groups.indexOf(gr);

            groups = new ArrayList(mgr.deleteGroup(gr));
            if (row>=0) {
                this.fireTableRowsDeleted(row, row);
            }
            this.fireTableDataChanged();
        }
    }

    // Количество строк равно числу записей
    @Override
    public int getRowCount()
    {

      if (groups != null) {
        return groups.size();
      }
      return 0;
    }

    // Количество столбцов - 4. Фамилия, Имя, Отчество, Дата рождения
    @Override
    public int getColumnCount()
    {
      return 1;
    }

    // Вернем наименование колонки
    @Override
    public String getColumnName(int column)
    {
       return "Наименование";
    }

    // Возвращаем данные для определенной строки и столбца
    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
      if ( groups != null && groups.size()>0
              && rowIndex<groups.size() && rowIndex>=0 ){

        ProdGroup gr = (ProdGroup) groups.get(rowIndex);

        switch (columnIndex) {
        case 0:
          return gr.getName();
        }
      }
      return null;
    }


    public ProdGroup getGroup(int rowIndex)
    {
      if(groups!=null) {
        if(rowIndex<groups.size() && rowIndex>=0) {
          return (ProdGroup)groups.get(rowIndex);
        }
      }
      return null;
    }
    
    public void reloadGroups(){
        groups = new ArrayList(mgr.getGroups());
        if (groups.size()>0)
            fireTableDataChanged();
    }
    
    public int findRow(int id){
        int row = -1;
        if (groups.size()>0){
           Object [] pr = groups.toArray();
           for (int i=0;i<groups.size();i++){
               if ( ((ProdGroup)pr[i]).getId()==id ){
                   row = i;
                   break;
               }
           }
        }
        return row;
    }
}
