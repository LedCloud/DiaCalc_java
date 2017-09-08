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


public class ArcProductManager {
    private ManagementSystem manager;
    private int lastInsertedId = -1;
    
    public ArcProductManager(){
        //Тут надо иницировать соединение
        manager = ManagementSystem.getInstance();
    }


    public Collection<ProductInBase> getAllProducts(){
           Collection products = new ArrayList();

     try {

        // PreparedStatement
         Statement stmt = manager.getConnection().createStatement();
         ResultSet rs = stmt.executeQuery( "SELECT name, prot, fats, carb, gi, weight, " +
        "idProd, compl, idGroup FROM arcproducts ORDER BY idGroup, name;");

          while(rs.next()) {
        products.add(new ProductInBase(rs.getString("name"),rs.getFloat("prot"),
          rs.getFloat("fats"),rs.getFloat("carb"),rs.getInt("gi"),
          rs.getFloat("weight"),rs.getInt("idProd"),rs.getInt("compl")!=0,
          rs.getInt("idGroup"),0));
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

        Collection<ProductInBase> products = new ArrayList();

     try {
         PreparedStatement stmt = manager.getConnection().prepareStatement(
                "SELECT name, prot, fats, carb, gi, weight, " +
                "idProd, compl, idGroup FROM arcproducts " +
                "WHERE idGroup=? ORDER BY name;");
             stmt.setInt(1, idGroup);


         ResultSet rs = stmt.executeQuery();


        int i=0;
        while(rs.next()) {
            if (idGroup==0) i++;
            products.add( new ProductInBase(rs.getString("name"),
                    rs.getFloat("prot"),rs.getFloat("fats"),rs.getFloat("carb"),
                    rs.getInt("gi"),rs.getFloat("weight"),rs.getInt("idProd"),
                    rs.getInt("compl")!=0,rs.getInt("idGroup"),0));
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
       try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "INSERT INTO arcproducts "+
            "(name, prot, fats, carb, gi, weight, compl, idGroup) "+
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

            stmt.setString(1, prod.getName());
            stmt.setFloat(2, prod.getProt());
            stmt.setFloat(3, prod.getFat());
            stmt.setFloat(4, prod.getCarb());
            stmt.setInt(5, prod.getGi());
            stmt.setFloat(6, prod.getWeight());
            stmt.setInt(7, prod.isComplex()?1:0);
            stmt.setInt(8, prod.getOwner());


            stmt.executeUpdate();

            Statement stmt2 = manager.getConnection().createStatement();
            ResultSet rsl = stmt2.executeQuery("SELECT last_insert_rowid() FROM arcproducts;");
            lastInsertedId = -1;
            if (rsl.next()) lastInsertedId = rsl.getInt(1);
            rsl.close();
            stmt.close();
            stmt2.close();
            manager.getConnection().commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getProductsFromGroup(prod.getOwner());
   }
   public Collection addCollectionProducts(Collection<ProductInBase> prods, int grId){
       try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "INSERT INTO arcproducts "+
            "(name, prot, fats, carb, gi, weight, compl, idGroup) "+
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

            for (ProductInBase item:prods){

                stmt.setString(1, item.getName());
                stmt.setFloat(2, item.getProt());
                stmt.setFloat(3, item.getFat());
                stmt.setFloat(4, item.getCarb());
                stmt.setInt(5, item.getGi());
                stmt.setFloat(6, item.getWeight());
                stmt.setInt(7, item.isComplex()?1:0);
                stmt.setInt(8, grId);

                stmt.executeUpdate();
            }

            stmt.close();
            manager.getConnection().commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getProductsFromGroup(grId);

   }
   public Collection updateProductInBase(ProductInBase prod){
        try {
        PreparedStatement
           stmt = manager.getConnection().prepareStatement(
        "UPDATE arcproducts SET name=?, prot=?, fats=?, carb=?, gi=?, weight=?, " +
        "compl=?, idGroup=? " +
        "WHERE idProd=?;");

        stmt.setString(1, prod.getName());
        stmt.setFloat(2, prod.getProt());
        stmt.setFloat(3, prod.getFat());
        stmt.setFloat(4, prod.getCarb());
        stmt.setInt(5, prod.getGi());
        stmt.setFloat(6, prod.getWeight());
        stmt.setInt(7, prod.isComplex()?1:0);
        stmt.setInt(8, prod.getOwner());

        stmt.setInt(9, prod.getId());

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
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "DELETE FROM arcproducts " +
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

   public void clearArchive(){
       try {
           Statement stmt = manager.getConnection().createStatement();

            stmt.execute("DELETE FROM arcproducts " +
            "WHERE compl=1;");
            stmt.close();

            Statement stmt2 = manager.getConnection().createStatement();
            stmt2.execute("SELECT G.idGroup, "+
                "(SELECT COUNT(P.idProd) AS CNT FROM arcproducts P " +
                "WHERE p.idGroup=G.idGroup AND P.compl=0) " +
                "AS prodt FROM arcgroups G WHERE prodt=0;");
            ResultSet rs = stmt2.getResultSet();
            PreparedStatement stmt3 = manager.getConnection().prepareStatement(
                "DELETE FROM arcgroups " +
                    "WHERE idGroup=?;");
            while (rs.next()){
                //System.out.println(rs.getString("G.Name"));
                stmt3.setInt(1, rs.getInt("idGroup"));
                stmt3.executeUpdate();
            }
            stmt3.close();
            stmt2.close();
            manager.getConnection().commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
   }
}
