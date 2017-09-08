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

package tablemodels;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import maths.DiaryUnit;
import maths.User;
import manager.DiaryManager;
import javax.swing.table.AbstractTableModel;
import lookout.settings.ProgramSettings;
import java.util.*;
import maths.Sugar;

public class DiaryTableModel extends AbstractTableModel{
    private final ProgramSettings settings = ProgramSettings.getInstance();
    private Vector<DiaryUnit> records = null;
    private User user;
    private final DiaryManager manager;
    private Date from;
    private Date to;

    public DiaryTableModel(User user,Date from, Date to){
        this.user = user;
        manager = new DiaryManager(user);
        this.from = from;
        this.to = to;
        fetchData(from,to,"");
    }
    private void fetchData(Date from,Date to,String search){
        records = new Vector(manager.getDiaryRecords(from.getTime(), to.getTime(),search));
    }
    public void refresh(String search){
        //System.out.println( search );
        fetchData(from,to,search);
        this.fireTableDataChanged();
    }
    public DiaryUnit getUnit(int row){
        if (records!=null && records.size()>0 && row>=0){
            return records.get(row);
        }
        return null;
    }
    /**
     * Добавляем событие в дневник
     * @param u - событие DiaryUnit
     * после добавления нужно дернуть refresh
     */
    public void addUnit(DiaryUnit u){//, String search){
        if (u.getType()==DiaryUnit.COMMENT){
            manager.addComment(u);
        }else{
            manager.addUnit(u);
        }
        //refresh(search);
    }
    public void deleteUnit(DiaryUnit u, String search){
        if (records!=null && records.size()>0){
            int row= records.indexOf(u);
           
            manager.deleteUnit(u);
          if (row>=0) {
              this.fireTableRowsDeleted(row, row);
          }
          //this.fireTableDataChanged();
          refresh(search);
        }
    }
    public void changeDates(Date from,Date to,String search){
        this.from = from;
        this.to = to;
        fetchData(from,to,search);
        this.fireTableDataChanged();
    }
    /**
     * Меняем пользователя и вытягиваем новые данные
     * @param user
     * @param search 
     */
    public void changeUser(User user,String search){
        this.user = user;
        manager.changeUser(user);
        fetchData(from,to,search);
        this.fireTableDataChanged();
    }
    @Override
    public String getColumnName(int column)
    {
        String[] colNames = { "Дата и время", "Описание", "СК1", "СК2","Доза",
            "Б","Ж","У","ГИ"};
        //return colNames[column];
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
        return 9;
    }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex)
  {
    if (records != null && records.size()>0 
            && rowIndex<records.size() && rowIndex>=0) {
      DiaryUnit u = records.get(rowIndex);

      switch (columnIndex) {
        case 0:
            return new Date(u.getTime());
        case 1:
            return u.getComment();
      }
      if (u.getType()==DiaryUnit.COMMENT){
          return null;//Далее возвращать можно null
      }
      if (u.getType()==DiaryUnit.SUGAR){
          if (columnIndex==2){
              return new Sugar(u.getSh1()).getSugar(user.isMmol(), user.isPlasma());
          }else{
              return null;
          }
      }
      //Если дошли до сюда, то значит отдаем меню
      switch (columnIndex){
        case 2:
          return new Sugar(u.getSh1()).getSugar(user.isMmol(), user.isPlasma());
        case 3:
          return new Sugar(u.getSh2()).getSugar(user.isMmol(), user.isPlasma());
        case 4:
          return u.getDose();
        case 5:
            return u.getProduct().getAllProt();
        case 6:
            return u.getProduct().getAllFat();
        case 7:
            return u.getProduct().getAllCarb();
        case 8:
            return u.getProduct().getGi();
        }
     }
    return null;
  }

  @Override
  public Class getColumnClass(int col) {
      switch (col){
          case 0: return Date.class;
          case 1: return String.class;
          case 2:
          case 3:
          case 4:
          case 5:
          case 6:
          case 7: return Float.class;
          case 8: return Integer.class;
          
      }
      return null;
  }

  @Override
  public boolean isCellEditable(int row, int col) {
        return false;
  }
}
