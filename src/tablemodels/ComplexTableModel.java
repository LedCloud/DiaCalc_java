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
//import lookout.settings.ProgramSettings;
import javax.swing.table.AbstractTableModel;

import products.ComplexProduct;
import products.ProductW;
import manager.ComplexManager;
import products.ProductInBase;

public class ComplexTableModel extends AbstractTableModel {

    private ArrayList composition;
    //private final ProgramSettings settings;
    private final ComplexManager mgr;
    private boolean weightEditable;
    private int Owner;
    private boolean editable=false;
    private final String[] colNames = { "Наименование","Вес", "Б", "Ж", "У","ГИ" };
    
    public ComplexTableModel(ComplexManager mgr,int idOwner){
        this.mgr = mgr;
        composition = new ArrayList(mgr.getComposition(idOwner));
        //settings = ProgramSettings.getInstance();
        weightEditable = false;
        this.Owner = idOwner;
    }
  public void setWeightEditable(boolean vl){
      weightEditable = vl;
  }
  
  @Override  
  public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return col==1&&weightEditable;
  }
  
  @Override
  public void setValueAt(Object value, int row, int col){
      if (col==1){
        ComplexProduct prod = (ComplexProduct)composition.get(row);
        prod.setWeight(((Number)value).floatValue());
        mgr.updateProduct(prod);
        //fireTableRowsUpdated(row,row);
        fireTableCellUpdated(row, col);
      }
  }
  
  @Override
  public int getRowCount()
  {
    if (composition != null) {
      return composition.size();
    }
    return 0;
  }
  
  // Количество столбцов - 6
    @Override
  public int getColumnCount()
  {
    return 6;
  }

  // Вернем наименование колонки
  public @Override String getColumnName(int column)
  {
    return colNames[column];
  }
  
  // Возвращаем данные для определенной строки и столбца
    @Override
  public Object getValueAt(int rowIndex, int columnIndex){
    if (composition != null && composition.size()>0 
            && rowIndex<composition.size() && rowIndex>=0)  {
      // Получаем из вектора студента
      ComplexProduct prod = (ComplexProduct) composition.get(rowIndex);

      switch (columnIndex) {
      case 0://Наименование
        return prod.getName();

      case 1://Вес
        return prod.getWeight();

      case 2://Б
        return prod.getProt();
        
      case 3://Ж
        return prod.getFat();

      case 4://У
        return prod.getCarb();
        
      case 5:
        return prod.getGi();

      }
    }
    return null;

  }
  
  public ComplexProduct getProduct(int rowIndex)
  {
    if (composition!=null) {
      if(rowIndex<composition.size() && rowIndex>=0) {
        return (ComplexProduct)composition.get(rowIndex);
      }
    }
    return null;
  }   
  
  public ProductW getSumProd(){
      if (composition!=null){
          ProductW res = new ProductW();
          for (Object item : composition) {
             res.plusProd((ProductW)item);
          }
          return res;
      }
      return null;
  }
  
  public @Override Class getColumnClass(int c){
      if (composition!=null && composition.size()>0 )
            return getValueAt(0, c).getClass();
      else return null;
  }
  
  public void flushProducts(int idOwner){
      Owner = idOwner;
      int sz = composition.size();
      composition =  new ArrayList(mgr.flushProducts(Owner));
      fireTableRowsDeleted(0,sz-1);

  }
  
  public void reloadProducts(int idOwner){
      int sz =  composition.size();
      Owner = idOwner;
      composition = new ArrayList(mgr.getComposition(Owner));
      if (composition.size()>0) fireTableDataChanged();
      else if (sz>0) fireTableRowsDeleted(0,sz-1);
  }
  
   public void deleteProduct(ComplexProduct prod){
        if (composition.size()>0){
            composition = new ArrayList(mgr.deleteProduct(prod));
            //fireTableRowsDeleted(pos-1,pos-1);
            if (composition.size()>0) fireTableDataChanged();
            else fireTableRowsDeleted(0,0);
        }
   }
   
   public void addProduct(ProductInBase prod){
        boolean found = false;
        for (Object item:composition.toArray() ){
            if (((ProductW)item).getName().equals(prod.getName())){
                found=true;
                break;
            }
        }
        if (!found){
            prod.setWeight(0f);
            composition = new ArrayList(mgr.addProduct((ProductW)prod,Owner));
            fireTableDataChanged();
        }
   }
   public boolean isEditable(){
       return editable;
   }
   
   public void setEditable(boolean ed){
       editable = ed;
   }
}
