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


import manager.ArcSearchProdManager;
import products.Pair;
import products.ProdGroup;
import products.ProductInBase;

public class ArcSearchTableModel  extends AbstractTableModel {
    private ArcSearchProdManager mgr = new ArcSearchProdManager();
    private Vector<Pair> searchResults;
    private ProgramSettings settings = ProgramSettings.getInstance();

    public ArcSearchTableModel(){
        searchResults = new Vector(mgr.doSearch(""));
    }

    @Override
  public int getRowCount()
  {
    if (searchResults!= null) {
      return searchResults.size();
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
    String[] colNames = {"Группа","Наименование", "Б", "Ж", "У","ГИ" };
    return colNames[column];
  }
   // Возвращаем данные для определенной строки и столбца
    @Override
  public Object getValueAt(int rowIndex, int columnIndex){
    if (searchResults != null && searchResults.size()>0
            && rowIndex<searchResults.size() && rowIndex>=0)  {
      Pair pair = searchResults.get(rowIndex);
      
      switch (columnIndex) {
      case 0://Наименование группы
        return pair.getGroup().getName();

      case 1://Наименование продукта
        return pair.getProduct().getName();

      case 2://Б
        return pair.getProduct().getProt();

      case 3://Ж
        return pair.getProduct().getFat();

      case 4://У
        return pair.getProduct().getCarb();

      case 5:
        return pair.getProduct().getGi();

      }
    }
    return null;

  }
  @Override
  public Class getColumnClass(int c) {
      if (searchResults!=null && searchResults.size()>0 )
          return getValueAt(0, c).getClass();
      else return null;
  }
  public void doSearch(String st){
      int sz = searchResults.size();
      searchResults = new Vector(mgr.doSearch(st));
      if (searchResults.size()>0) fireTableDataChanged();
      else if (sz>0) fireTableRowsDeleted(0,sz-1);
  }

  public ProdGroup getGroup(int rowInx){
      if (searchResults.size()>0){
        Pair pr = searchResults.get(rowInx);
        return pr.getGroup();
      } else return null;
  }

  public ProductInBase getProduct(int rowInx){
      if (searchResults.size()>0){
        Pair pr = searchResults.get(rowInx);
        return pr.getProduct();
      } else return null;
  }
}