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
import java.util.*;
import maths.Plate;
import lookout.settings.ProgramSettings;
import manager.PlatesManager;

public class PlateTableModel  extends AbstractTableModel{
    private Vector<Plate> records;
    private ProgramSettings settings = ProgramSettings.getInstance();
    private PlatesManager manager;

    public PlateTableModel(){
        manager = new PlatesManager();
        records = manager.getPlates();
    }
    @Override
    public String getColumnName(int column){
        String[] colNames = { "Наименование", "Вес",};
        return colNames[column];
    }

    @Override
    public int getRowCount(){
        if (records != null) {
            return records.size();
        }
        return 0;
    }

    @Override
    public int getColumnCount()
    {
        return 2;
    }


  @Override
  public Object getValueAt(int rowIndex, int columnIndex){
      if (records != null && records.size()>0 && rowIndex<records.size() && rowIndex>=0) {
      // Получаем из вектора студента
        Plate plate = records.get(rowIndex);
        switch(columnIndex){
            case 0: return plate.getName();
            case 1: return plate.getWeight();
        }
      }
      return null;
  }
  @Override
  public Class getColumnClass(int col ) {
      switch (col){
          case 0: return String.class;
          case 1: return float.class;
      }
      return null;
  }

  @Override
  public boolean isCellEditable(int row, int col) {
        return true;
  }

    public int addPlate(Plate plate){
        int res = -1;
        res = manager.addPlate(plate);
        records = manager.getPlates();
        fireTableDataChanged();
        return res;
    }

    public void deletePlate(Plate plate){
        int row = findRow(plate.getId());
        manager.deletePlate(plate);
        records = manager.getPlates();
        this.fireTableRowsDeleted(row, row);
        //this.fireTableDataChanged();
    }

  @Override
  public void setValueAt(Object value, int row, int col){
      if (row<records.size() && row>=0 && col>=0){
        Plate plate = records.get(row);
        int id = plate.getId();
        switch (col){
            case 0: plate.setName( (String)value ); break;
            case 1: plate.setWeight( ((Number)value).floatValue() ); break;
        }
        manager.updatePlate(plate);
        records = manager.getPlates();

        this.fireTableCellUpdated(findRow(id), col);
      }
  }
    public int findRow(int id){
        for(int i=0;i<records.size();i++){
            if (id==records.get(i).getId()) return i;
        }
        return -1;
    }
    public Plate getPlate(int row){
        if (row<records.size() && row>=0){
            return records.get(row);
        }
        return null;
    }

}
