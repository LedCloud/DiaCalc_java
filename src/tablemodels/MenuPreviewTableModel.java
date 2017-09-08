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

import java.util.*;
import javax.swing.table.AbstractTableModel;
import lookout.settings.ProgramSettings;
import products.ProductInMenu;

public class MenuPreviewTableModel extends AbstractTableModel{
    private ProgramSettings settings = ProgramSettings.getInstance();
    private Vector<ProductInMenu> prods;
    private boolean mode;
    private String[] colNames = { "Наименование", "Вес"};

    public MenuPreviewTableModel(boolean mode){

        prods = new Vector();
        this.mode = mode;

    }
    public void setProducts(Collection<ProductInMenu> products){
        prods = new Vector(products);
        this.fireTableDataChanged();
    }

  @Override
  public int getRowCount()
  {
    if (prods != null) {
      return prods.size();
    }
    return 0;
  }

  // Количество столбцов - 6
  @Override
  public int getColumnCount()
  {

    return 2;
  }
  @Override
  public String getColumnName(int column)
  {
    if (mode) return null;
    return colNames[column];
  }
  @Override
  public Object getValueAt(int rowIndex, int columnIndex){
    if (prods != null && prods.size()>0
            && rowIndex<prods.size() && rowIndex>=0) {
      ProductInMenu prod = prods.get(rowIndex);
      switch (columnIndex) {
          case 0: return prod.getName();

          case 1: return prod.getWeight();

      }
    }
    return null;
  }
  @Override
  public Class getColumnClass(int c) {
      if (prods!=null && prods.size()>0 )
          return getValueAt(0, c).getClass();
      else return null;
  }
}
