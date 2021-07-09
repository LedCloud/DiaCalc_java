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

//import lookout.settings.ProgramSettings;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

import products.ProductInBase;


import manager.ArcProductManager;

public class ArcProductTableModel extends AbstractTableModel {
    // Сделаем хранилище для нашего списка
    private ArrayList<ProductInBase> products;
    private final ArcProductManager mgr = new ArcProductManager();

    //private final ProgramSettings settings = ProgramSettings.getInstance();
    private int groupid = -1;

    // Модель при создании получает список студентов
    public ArcProductTableModel(int idGroup){
      groupid = idGroup;
      products = new ArrayList(mgr.getProductsFromGroup(idGroup));
   }


    // Количество строк равно числу записей
    @Override
    public int getRowCount()
    {
      if (products != null) {
        return products.size();
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
    @Override
    public String getColumnName(int column)
    {
      String[] colNames = { "Наименование", "Б", "Ж", "У","ГИ","Тип" };
      return colNames[column];
    }

    // Возвращаем данные для определенной строки и столбца
    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
      if (products != null && products.size()>0
              && rowIndex<products.size() && rowIndex>=0) {
        ProductInBase prod = (ProductInBase) products.get(rowIndex);

        switch (columnIndex) {
          case 0:
              return prod.getName();

          case 1:
              return prod.getProt();

          case 2:
              return prod.getFat();

          case 3:
              return prod.getCarb();

          case 4:
              return prod.getGi();

          case 5:
              return prod.isComplex();
        }
      }
      return null;
    }


    public ProductInBase getProduct(int rowIndex)
    {
      if(products!=null) {
        if(rowIndex<products.size() && rowIndex>=0) {

          return (ProductInBase)products.get(rowIndex);
        }
      }
      return null;
    }

    /*
           * JTable uses this method to determine the default renderer/
           * editor for each cell.  If we didn't implement this method,
           * then the last column would contain text ("true"/"false"),
           * rather than a check box.
           */
    @Override
    public Class getColumnClass(int c) {
        if (products!=null && products.size()>0 )
            return getValueAt(0, c).getClass();
        else return null;
    }

    public void addProduct(ProductInBase prod){
        prod.setOwner(groupid);
        products = new ArrayList(mgr.addProdInBase(prod));
        //fireTableRowsInserted(0,products.size()-1);
        fireTableDataChanged();
    }

    public void updateProduct(ProductInBase prod){
        products = new ArrayList(mgr.updateProductInBase(prod));
        //fireTableRowsUpdated(0,products.size()-1);
        fireTableDataChanged();

    }

    public void deleteProduct(ProductInBase prod){
        if (products.size()>0){
          products = new ArrayList(mgr.deleteProductFromBase(prod));
          if (products.size()>0) fireTableDataChanged();
          else fireTableRowsDeleted(0,0);
        }

    }

    public void reloadProducts(int idGroup){
        groupid = idGroup;
        int sz = products.size();
        products = new ArrayList(mgr.getProductsFromGroup(idGroup));

        if (products.size()>0) fireTableDataChanged();
        else if (sz>0) fireTableRowsDeleted(0,sz-1);
    }
    
    public int findRow(int id){
        int row = -1;
        if (products.size()>0){
           Object [] pr = products.toArray();
           for (int i=0;i<products.size();i++){
               if ( ((ProductInBase)pr[i]).getId()==id ){
                   row = i;
                   break;
               }
           }
        }
        return row;
    }
    
    public int getGrId(){
        return groupid;
    }
}
