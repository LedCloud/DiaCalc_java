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

import lookout.settings.ProgramSettings;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

import maths.*;

import manager.CoefsManager;
import java.util.Date;
import maths.Sugar;


public class CoefTableModel  extends AbstractTableModel {
    //private CoefManager mgr;
    private Vector coefs;
    private ProgramSettings settings;
    private User user;
    private CoefsManager mgr;
    
    
    public CoefTableModel(User user){
        settings = ProgramSettings.getInstance();
        this.user = user;
        mgr = new CoefsManager(user);
        coefs = new Vector(mgr.getCoefs());
    }
    public void changeUser(User user){
        
        this.user = user;
        mgr.changeUser(user);
        coefs = new Vector(mgr.getCoefs());
        this.fireTableDataChanged();
        
    }
    @Override
    public int getRowCount(){
        if (coefs != null) {
            return coefs.size();
        }
        return 0;
    }
    // Количество столбцов - 6
    @Override
    public int getColumnCount()
    {
        return 5;
    }
    // Вернем наименование колонки
    @Override
    public String getColumnName(int column)
    {
        String[] colNames = { "#", "Время", "к1", "к2","ЦЕИ" };
        return colNames[column];
    }
    // Возвращаем данные для определенной строки и столбца
  @Override
  public Object getValueAt(int rowIndex, int columnIndex){
    if (coefs != null && coefs.size()>0
            && rowIndex<coefs.size() && rowIndex>=0) {
      CoefsSet cfSet = (CoefsSet) coefs.get(rowIndex);
      
      // В зависимости от колонки возвращаем имя, фамилия и т.д.
      switch (columnIndex) {
      case 0:
        return cfSet.getRow();

      case 1:
        return cfSet.getTime();//frt.format(prod.getWeight());

      case 2:
          Factors f = new Factors(cfSet.getK1(),
                  user.getFactors().getK2(),
                  user.getFactors().getK3(),
                  user.getFactors().getBEValue());

        return f.getK1(user.isDirect());//frt.format(ds.getCalories());

      case 3:
        return cfSet.getK2();//frt.format(ds.getWholeDose());
      
      case 4:
          return new Sugar(cfSet.getK3()).getSugar(user.isMmol(), user.isPlasma());
      }
    }
    return null;
  }
  
  @Override
  public void setValueAt(Object value, int row, int col){
      if (col>0){
        CoefsSet cf = (CoefsSet)coefs.get(row);
        switch (col){
            case 1: cf.setTime( (Date)value ); break;
            case 2: Factors f = new Factors( ((Number)value).floatValue(),
                        user.getFactors().getK2(),
                        user.getFactors().getK3(),
                        user.getFactors().getBE(user.isDirect()),
                        user.isDirect());
                    cf.setK1( f.getK1Value() ); break;
            case 3: cf.setK2( ((Number)value).floatValue() ); break;
            case 4: Sugar s = new Sugar();
                    s.setSugar(((Number)value).floatValue(), user.isMmol(), user.isPlasma());
                    cf.setK3( s.getValue() );
                    //System.out.println("Value="+cf.getK3());
        }
        int id = cf.getId();

        coefs = new Vector(mgr.updateCoef(cf));

        this.fireTableCellUpdated(findRow(id), col);

      }
  }
  @Override
  public Class getColumnClass(int col) {
      switch (col){
          case 0: return Integer.class;
          case 1: return Date.class;
          case 2:
          case 3: 
          case 4: 
          default: return float.class;
      }
  }  
  @Override  
  public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return col>0;
  }

  public void addRow(CoefsSet cf){
        coefs = new Vector(mgr.addCoef(cf));
        fireTableDataChanged();
  }
  
  public void deleteRow(int row){
      int sz = coefs.size();
      if (sz>0){
        CoefsSet cf = (CoefsSet)coefs.get(row);
        coefs = new Vector(mgr.deleteCoef(cf));
        this.fireTableRowsDeleted(row, row);
      }
  }
  public CoefsSet getCoefs(int row){
      if (coefs.size()>0){
          return (CoefsSet)coefs.get(row);
      }else return null;
  }
  public Vector getAllCoefs(){
      return coefs;
  }
  public int findRow(int id){
      int row = -1;
      if (coefs.size()>0){
         Object [] pr = coefs.toArray();
         for (int i=0;i<coefs.size();i++){
             if ( ((CoefsSet)pr[i]).getId()==id ){
                 row = i;
                 break;
             }
         }
      }
      return row;
  }
}
