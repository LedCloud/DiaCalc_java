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

import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;


import java.util.Collection;
import java.sql.SQLException;


import products.ProductInBase;
import lookout.settings.ProgramSettings;


public class ProductManager {
    private ManagementSystem manager;
    private int lastInsertedId = -1;
    private ProgramSettings settings = ProgramSettings.getInstance();
    
    public ProductManager(){
        //Тут надо иницировать соединение
        manager = ManagementSystem.getInstance();
    }
    
    
    public Collection getAllProducts(){
           Collection products = new ArrayList();

     try {   
         
        // PreparedStatement 
         Statement stmt = manager.getConnection().createStatement();
         ResultSet rs = stmt.executeQuery( "SELECT name, prot, fats, carb, gi, weight, " +
        "idProd, compl, idGroup, usage FROM products ORDER BY idGroup, name;");
                 
          while(rs.next()) {
        products.add(new ProductInBase(rs.getString("name"),rs.getFloat("prot"),
          rs.getFloat("fats"),rs.getFloat("carb"),rs.getInt("gi"),
          rs.getFloat("weight"),rs.getInt("idProd"),rs.getInt("compl")!=0,
          rs.getInt("idGroup"),rs.getInt("usage")));
          }

        rs.close();
        stmt.close();
        manager.getConnection().commit();
  
    }
      catch (SQLException e) {
      e.printStackTrace();
    }

    return products;
        
    }
    
    public Collection getProductsFromGroup(int idGroup){
         
        Collection products = new ArrayList();
     
     try {   
         PreparedStatement stmt;
         if (idGroup==0){
             stmt = manager.getConnection().prepareStatement(
                "SELECT name, prot, fats, carb, gi, weight, " +
                "idProd, compl, idGroup, usage FROM products " +
                "WHERE usage>0 ORDER BY usage DESC LIMIT 0, ?;");
             stmt.setInt(1, settings.getIn().getUsageGroupCount());
         }
         else{
             stmt = manager.getConnection().prepareStatement(
                "SELECT name, prot, fats, carb, gi, weight, " +
                "idProd, compl, idGroup, usage FROM products " +
                "WHERE idGroup=? ORDER BY name;");
             stmt.setInt(1, idGroup);
         }
        
         ResultSet rs = stmt.executeQuery();
        
    
        int i=0;
        while(rs.next()){
            if (idGroup==0) i++;
            products.add( new ProductInBase(rs.getString("name"),
                    rs.getFloat("prot"),rs.getFloat("fats"),rs.getFloat("carb"),
                    rs.getInt("gi"),rs.getFloat("weight"),rs.getInt("idProd"),
                    rs.getInt("compl")!=0,rs.getInt("idGroup"),rs.getInt("usage")));
        }

        rs.close();
        stmt.close();
       
        manager.getConnection().commit();
    
      } catch (SQLException e) {
      e.printStackTrace();
    }

    return products;
    }
            
  
   
   public Collection addProdInBase(ProductInBase prod){
       try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "INSERT INTO products "+
            "(name, prot, fats, carb, gi, weight, compl, idGroup, usage) "+
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            
            stmt.setString(1, prod.getName());
            stmt.setFloat(2, prod.getProt());
            stmt.setFloat(3, prod.getFat());
            stmt.setFloat(4, prod.getCarb());
            stmt.setInt(5, prod.getGi());
            stmt.setFloat(6, prod.getWeight());
            stmt.setInt(7, prod.isComplex()?1:0);
            stmt.setInt(8, prod.getOwner());
            stmt.setInt(9, prod.getUsage());
        
            stmt.executeUpdate();

            Statement stmt2 = manager.getConnection().createStatement();
            ResultSet rsl = stmt2.executeQuery("SELECT last_insert_rowid() FROM products;");
            lastInsertedId = -1;
            if (rsl.next()) lastInsertedId = rsl.getInt(1);
            rsl.close();
            stmt.close();
            stmt2.close();
            manager.getConnection().commit();

       }catch (SQLException ex){
           ex.printStackTrace();
       }
       
        return getProductsFromGroup(prod.getOwner());
   }
   public Collection addCollectionProducts(Collection prods, int grId){
       try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "INSERT INTO products "+
            "(name, prot, fats, carb, gi, weight, compl, idGroup, usage) "+
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            
            for (Object item:prods){
                
                stmt.setString(1, ((ProductInBase)item).getName());
                stmt.setFloat(2, ((ProductInBase)item).getProt());
                stmt.setFloat(3, ((ProductInBase)item).getFat());
                stmt.setFloat(4, ((ProductInBase)item).getCarb());
                stmt.setInt(5, ((ProductInBase)item).getGi());
                stmt.setFloat(6, ((ProductInBase)item).getWeight());
                stmt.setInt(7, ((ProductInBase)item).isComplex()?1:0);
                stmt.setInt(8, grId);
                stmt.setInt(9, ((ProductInBase)item).getUsage());
        

                stmt.executeUpdate();
            }
       
            Statement stmt2 = manager.getConnection().createStatement();
            ResultSet rsl = stmt2.executeQuery("SELECT changes() FROM products;");
            int count = 0;
            if (rsl.next()) count = rsl.getInt(1);
            rsl.close();
            stmt.close();
            stmt2.close();
            manager.getConnection().commit();

       }catch (SQLException ex){
           ex.printStackTrace();
       }
        return getProductsFromGroup(grId);
   }
   public Collection updateProductInBase(ProductInBase prod){
        try {
        PreparedStatement 
           stmt = manager.getConnection().prepareStatement(
        "UPDATE products SET name=?, prot=?, fats=?, carb=?, gi=?, weight=?, " +
        "compl=?, idGroup=?, usage=?" +
        "WHERE idProd=?;");

        stmt.setString(1, prod.getName());
        stmt.setFloat(2, prod.getProt());
        stmt.setFloat(3, prod.getFat());
        stmt.setFloat(4, prod.getCarb());
        stmt.setInt(5, prod.getGi());
        stmt.setFloat(6, prod.getWeight());
        stmt.setInt(7, prod.isComplex()?1:0);
        stmt.setInt(8, prod.getOwner());
        stmt.setInt(9, prod.getUsage());
        
        stmt.setInt(10, prod.getId());

        stmt.executeUpdate();
        
        stmt.close();
        manager.getConnection().commit();
       
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getProductsFromGroup(prod.getOwner());
   }
   
   public Collection deleteProductFromBase(ProductInBase prod){
       try {
            PreparedStatement stmt;
            if (prod.isComplex()) {
                stmt =  manager.getConnection().prepareStatement(
                    "DELETE FROM complex " +
                    "WHERE Owner=?;");

                stmt.setInt(1, prod.getId());
                stmt.executeUpdate();
            }

            stmt = manager.getConnection().prepareStatement(
            "DELETE FROM products " +
            "WHERE idProd=?;");

            stmt.setInt(1, prod.getId());

            stmt.executeUpdate();
        
            stmt.close();
            manager.getConnection().commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return getProductsFromGroup(prod.getOwner());
   }
   public int getLastInsertedId(){
       return lastInsertedId;
   }
}
