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
import products.ProductW;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

import maths.*;


import products.ProductInMenu;
import manager.MenuManager;

public class MenuTableModel extends AbstractTableModel {
    public final static int MENU_TABLE = 0;
    public final static int SNACK_TABLE = 1;

    public final static int CALOR = 1;
    public final static int DOSE = 2;
    public final static int PROT = 4;
    public final static int FAT = 8;
    public final static int CARB = 16;
    public final static int Gi = 32;
    public final static int GL = 64;
    
    private Vector products;
    private ProgramSettings settings = ProgramSettings.getInstance();
   
    private Dose ds;
    private Factors factors;
    private MenuManager mgr;
    private int table;

    private String [] colNames = { "Наименование", "Вес", "Калор", "Доза",
                "Б","Ж","У","ГИ","ГН"};
    private int colCount;
    private int [] colReplace = new int[9];
    
    public MenuTableModel(int idUser,Factors factors,
            int table){
         
         initModelStructure();
         this.factors = factors;
         this.table = table;
         
         mgr = new MenuManager(idUser,table);
         products = new Vector(mgr.getMenu());
         
         
    }
    public int getTable(){
        return table;
    }
    public void changeStructure(){
        initModelStructure();
        fireTableStructureChanged();
    }
    private void initModelStructure(){
        int mask = settings.getIn().getMenuMask();
        colCount = 2 + (mask&CALOR) + ((mask&DOSE)>>1) + ((mask&PROT)>>2) +
                ((mask&FAT)>>3) + ((mask&CARB)>>4) + ((mask&Gi)>>5) + ((mask&GL)>>6);
        //System.out.println("columns="+colCount + " " + mask);
        colReplace[0] = 0;
        colReplace[1] = 1;
        //int bit = 1;
        int j = 2;
        int i = 2;
        for (int bit=1;bit<=GL;bit *=2){
            if ( (mask&bit)!=0 ){
                colReplace[j] = i;
                j++;
            }
            i++;
        }
    }
    public void userChanged(int idUser){
        mgr = new MenuManager(idUser,table);
        products = new Vector(mgr.getMenu());
        fireTableDataChanged();
    }
  
  @Override
  public int getRowCount()
  {
    if (products != null) {
      return products.size();
    }
    return 0;
  }
  
  @Override
  public int getColumnCount()
  {
    return colCount;
  }

  // Вернем наименование колонки
  @Override
  public String getColumnName(int column)
  {
     if (table==1) return null;
     else return colNames[colReplace[column]];
  }
  public String getPureColumnName(int column)
  {
     if (table==1) return null;
     else return colNames[colReplace[column]];
  }
  // Возвращаем данные для определенной строки и столбца
  @Override
  public Object getValueAt(int rowIndex, int columnIndex){
    if (products != null && products.size()>0
            && rowIndex<products.size() && rowIndex>=0) {
      ProductW prod = (ProductW) products.get(rowIndex);
      ds = new Dose(prod,factors,new DPS());
      int c = colReplace[columnIndex];
      switch (c) {
          case 0: return prod.getName();

          case 1: return prod.getWeight();

          case 2: return ds.getCalories();

          case 3: return ds.getWholeDose();

          case 4: return prod.getAllProt();

          case 5: return prod.getAllFat();
      
          case 6: return prod.getAllCarb();

          case 7: return prod.getGi();
          
          case 8: return prod.getGL();
      }
    }
    return null;
  }
  
  @Override
  public void setValueAt(Object value, int row, int col){
      if (col==1){
        //System.out.println("table "+value);
        ProductInMenu prod = (ProductInMenu)products.get(row);
        prod.setWeight(((Number)value).floatValue());
        mgr.updateProd(prod);
      
        fireTableRowsUpdated(row,row);
        //fireTableCellUpdated(row, col);
      }
  }
  
  
  
  public ProductW getProductAtRow(int Row){
      if ((products!=null)&&(Row<products.size())){
          return (ProductW)products.get(Row);
      }
      return null;
  }
   public ProductW getSumProd(){
      if (products!=null){
          ProductW res = new ProductW();
          for (Object item : products) {
             res.plusProd((ProductW)item);
          }
          return res;
      }
      return null;
  }
   
  public void setFactors(Factors fcs){
      this.factors = fcs;
      if (products!=null && products.size()>0) this.fireTableDataChanged();
  }
 
  /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
  @Override
  public Class getColumnClass(int c) {
      if (products!=null && products.size()>0)
        return getValueAt(0, c).getClass();
      else return null;
  }    
  
  /*
     * Don't need to implement this method unless your table's
     * editable.
     */
  @Override  
  public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return col==1;
  }
    
   public ProductInMenu getProduct(int rowIndex)
   {
    if(products!=null) {
      if(rowIndex<products.size() && rowIndex>=0) {
          
        return (ProductInMenu)products.get(rowIndex);
      }
    }
     return null;
   }   
    
    public void addProduct(ProductW prod){
        boolean found = false;
        if (settings.getIn().isProductOnce()){
            for (Object item:products.toArray() ){
                if (((ProductW)item).getName().equals(prod.getName())){
                    found=true;
                    break;
                }
            }
        }
        if (!found){
            prod.setWeight(0f);
            products = new Vector(mgr.addProd(prod));
            fireTableDataChanged();
        }
    }

    public void insertProduct(ProductW prod){
        boolean found = false;
        if (settings.getIn().isProductOnce()){
            for (Object item:products.toArray() ){
                if (((ProductW)item).getName().equals(prod.getName())){
                    found=true;
                    break;
                }
            }
        }
        if (!found){
            products = new Vector(mgr.addProd(prod));
            fireTableDataChanged();
        }
    }
    public void flushMenu(){
        if (products.size()>0){
            int n = products.size();
            products = new Vector(mgr.flush());
            fireTableRowsDeleted(0,n-1);
         }
    }
    
    public void deleteProduct(ProductInMenu prod){
        if (products.size()>0){
            products = new Vector(mgr.deleteProd(prod));
            if (products.size()>0) fireTableDataChanged();
            else fireTableRowsDeleted(0,0);
        }
    }
    public void updateProduct(ProductInMenu prod){
        if (products.size()>0){
            products = new Vector(mgr.updateProd(prod));
            if (products.size()>0) fireTableDataChanged();
        }
    }
}
