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

package manager;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import java.util.Collection;
import java.util.ArrayList;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import products.ProductInMenu;
import products.ProductW;
import java.sql.Statement;


public class MenuManager {
 private ManagementSystem manager;
 private int idUser;
 private String tableName;
 public final static int MENU_TABLE = 0;
 public final static int SNACK_TABLE = 1;
 private final static String menu_table = "menu";
 private final static String snack_table = "snack";
 
    public MenuManager(int idUser,int table){
        //Тут надо иницировать соединение
        manager = ManagementSystem.getInstance();
        this.idUser = idUser;
        switch (table){
            case 0: tableName = menu_table; break;
            case 1: tableName = snack_table; break;
        }
    }
    
    public Collection getMenu(){
        Collection products = new ArrayList();
        
       try {   
         
         PreparedStatement stmt = manager.getConnection().prepareStatement(
            "SELECT Name, Prot, Fat, Carb, Gi, Weight, " +
            "idProd FROM " + tableName + " WHERE idUser=? ORDER BY Name;");
         stmt.setInt(1, idUser);
         ResultSet rs = stmt.executeQuery();
         
         while(rs.next()) {
              products.add( new ProductInMenu(rs.getString("Name"),
                      rs.getFloat("Prot"),rs.getFloat("Fat"),rs.getFloat("Carb"),
                      rs.getInt("Gi"),rs.getFloat("Weight"), rs.getInt("idProd")) );
         }

        rs.close();
        stmt.close();
        manager.getConnection().commit();
       
     } catch (SQLException e) {
      e.printStackTrace();
    }
        return products;
    }
    
    public Collection updateProd(ProductInMenu prod){
        try {
        PreparedStatement 
           stmt = manager.getConnection().prepareStatement(
        "UPDATE " + tableName + " SET Weight=? " +
        "WHERE idProd=?;");

        stmt.setFloat(1, prod.getWeight());
        stmt.setInt(2, prod.getId());

        stmt.executeUpdate();
        stmt.close();
        manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getMenu();
    }
    
    public Collection deleteProd(ProductInMenu prod){
        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "DELETE FROM " + tableName + 
            " WHERE idProd=?;");

            stmt.setInt(1, prod.getId());

            stmt.executeUpdate();
            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getMenu();
    }
    
    public Collection flush(){
        try {
        PreparedStatement stmt = manager.getConnection().prepareStatement(
            "DELETE FROM " + tableName + " WHERE idUser=?;");
        stmt.setInt(1, idUser);
        stmt.executeUpdate();
        stmt.close();
        manager.getConnection().commit();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getMenu();
    }
    
    public Collection addProd(ProductW prod){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "INSERT INTO " + tableName + 
            " (Name, Prot, Fat, Carb, Gi, Weight, idUser) "+
            "VALUES (?, ?, ?, ?, ?, ?, ?);");

            stmt.setString(1, prod.getName());
            stmt.setFloat(2, prod.getProt());
            stmt.setFloat(3, prod.getFat());
            stmt.setFloat(4, prod.getCarb());
            stmt.setInt(5, prod.getGi());
            stmt.setFloat(6, prod.getWeight());
            stmt.setInt(7, idUser);
            

            stmt.executeUpdate();

            stmt.close();
            manager.getConnection().commit();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return getMenu();
    }
}
