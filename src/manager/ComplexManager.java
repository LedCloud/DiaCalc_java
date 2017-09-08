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
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Collection;
import java.sql.SQLException;
import java.sql.Statement;


import products.ComplexProduct;
import products.ProductW;


public class ComplexManager {
    private ManagementSystem manager;
    
    public ComplexManager(){
        manager = ManagementSystem.getInstance();
    }
    
    public Collection getComposition(int idProd){
        Collection products = new ArrayList();

        try {   
         
         PreparedStatement stmt = manager.getConnection().prepareStatement(
        "SELECT Name, Prot, Fat, Carb, Gi, Weight, " +
        "idProd, Owner FROM complex WHERE Owner=? ORDER BY Name;");

        stmt.setInt(1, idProd);

        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()) {
            products.add( new ComplexProduct(rs.getString("Name"),rs.getFloat("Prot"),
                rs.getFloat("Fat"),rs.getFloat("Carb"),rs.getInt("Gi"),
                rs.getFloat("Weight"),rs.getInt("idProd"),rs.getInt("Owner")));
        }
        
        rs.close();
        stmt.close();
        manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    return products;
    }
    
    public Collection addComplexProducts(Collection products,int idProd)
    {
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "INSERT INTO complex "+
                    "(Name, Prot, Fat, Carb, Gi, Weight, Owner) "+
                    "VALUES (?, ?, ?, ?, ?, ?, ?);"
                    );
            Object [] prodArr = products.toArray();
            for (int i=0; i<products.size(); i++){
                ProductW prod = (ProductW)prodArr[i];
                stmt.setString(1, prod.getName());
                stmt.setFloat(2, prod.getProt());
                stmt.setFloat(3, prod.getFat());
                stmt.setFloat(4, prod.getCarb());
                stmt.setInt(5, prod.getGi());
                stmt.setFloat(6, prod.getWeight());
                stmt.setInt(7, idProd);
                
                stmt.addBatch();
            }    
            stmt.executeBatch();
            stmt.close();

            manager.getConnection().commit();
            
        }catch (SQLException ex)     {
            ex.printStackTrace();
        }
        return getComposition(idProd);
    }
        
    public Collection addProduct(ProductW prod,int idProd)
    {
         try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "INSERT INTO complex "+
                    "(Name, Prot, Fat, Carb, Gi, Weight, Owner) "+
                    "VALUES (?, ?, ?, ?, ?, ?, ?);"
                    );
           
                stmt.setString(1, prod.getName());
                stmt.setFloat(2, prod.getProt());
                stmt.setFloat(3, prod.getFat());
                stmt.setFloat(4, prod.getCarb());
                stmt.setInt(5, prod.getGi());
                stmt.setFloat(6, prod.getWeight());
                stmt.setInt(7, idProd);
                
                stmt.executeUpdate();

                stmt.close();
                manager.getConnection().commit();

         }
         catch(SQLException ex){
             ex.printStackTrace();
         }
        return getComposition(idProd);
    }
    
    public Collection updateProduct(ComplexProduct prod){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "UPDATE complex SET Weight=? "+
                    "WHERE idProd=?;"
                    );
            stmt.setFloat(1, prod.getWeight());
            stmt.setInt(2, prod.getId());
            
            stmt.executeUpdate();
            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
                e.printStackTrace();
        }
        return getComposition(prod.getOwner());
    }
    
    public Collection deleteProduct(ComplexProduct prod){
        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "DELETE FROM complex " +
            "WHERE idProd=?;");

            stmt.setInt(1, prod.getId());

            stmt.executeUpdate();
            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getComposition(prod.getOwner());
    }
    
    public Collection flushProducts(int idOwner){
        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "DELETE FROM complex " +
            "WHERE Owner=?;");

            stmt.setInt(1, idOwner);

            stmt.executeUpdate();
            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getComposition(idOwner);
    }
}
