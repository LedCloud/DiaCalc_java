/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tablemodels;

/**
 *
 * @author connie
 */
import java.util.ArrayList;
//import lookout.settings.ProgramSettings;
import javax.swing.table.AbstractTableModel;

import products.ProductInBase;

import manager.ProductManager;

public class ProductTableModel extends AbstractTableModel {
    // Сделаем хранилище для нашего списка
    private ArrayList products; //Vector
    private final ProductManager mgr;
    private int groupid = -1;
    //private final ProgramSettings settings;
    private String[] columnNames = { "Наименование", "Б", "Ж", "У","ГИ","Тип" };
 
    // Модель при создании получает список студентов
    public ProductTableModel(ProductManager mgr,int idGroup){
        //settings = ProgramSettings.getInstance();
        this.mgr = mgr;
        products = new ArrayList(this.mgr.getProductsFromGroup(idGroup));
        groupid = idGroup;
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
        //String[] colNames = { "Наименование", "Б", "Ж", "У","ГИ","Тип" };
        return columnNames[column];
    }

    // Возвращаем данные для определенной строки и столбца
    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
      if (products != null && products.size()>0 
              && rowIndex<products.size() && rowIndex>=0) {
        // Получаем из вектора студента
        ProductInBase prod = (ProductInBase) products.get(rowIndex);

        // В зависимости от колонки возвращаем имя, фамилия и т.д.
        switch (columnIndex) {
            case 0: return prod.getName();
            case 1: return prod.getProt();
            case 2: return prod.getFat();
            case 3: return prod.getCarb();
            case 4: return prod.getGi();
            case 5: return prod.isComplex();//()?imgCmpl.getImage():imgSimple;
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
        products = new ArrayList(mgr.addProdInBase(prod));
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
        products = new ArrayList(this.mgr.getProductsFromGroup(idGroup));

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
