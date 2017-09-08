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
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import products.ProdGroup;
import products.ProductInBase;

public class GroupManager {
    private ManagementSystem manager;
    public static final int ONLY_EXISTS_GROUPS = 0;
    public static final int VIRTUAL_GROUP_ALSO = 1;
    
    public GroupManager(){
        manager = ManagementSystem.getInstance();
    }
    
    public Collection getGroups(int mode){
        Collection groups = new ArrayList();

        try {
            Statement stmt = manager.getConnection().createStatement();

            ResultSet rs = stmt.executeQuery("SELECT idGroup, Name, sortInd " +
                    "FROM groups ORDER BY sortInd;");
            
            boolean ch=true;
            while(rs.next()) {
                if (ch&&(mode==1)){
                    ch=false;
                    groups.add(new ProdGroup(0,"Часто используемые",0));
                }
                groups.add(new ProdGroup(rs.getInt("idGroup"),
                        rs.getString("Name"),rs.getInt("sortInd")));
            }

        rs.close();
        stmt.close();
        manager.getConnection().commit();
        
        } catch (SQLException e) {
        e.printStackTrace();
        }
        return groups; 
    }
    
    public Collection addGroup(ProdGroup gr, int mode){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "INSERT INTO groups "+
            "(Name, sortInd) VALUES (?, ?);");

            stmt.setString(1, gr.getName());
            stmt.setInt(2, gr.getSortInd());
            
            stmt.executeUpdate();
            stmt.close();
            manager.getConnection().commit();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return getGroups(mode);
    }
    
    public Collection deleteGroup(ProdGroup gr, int mode){
        Collection cmplProds = new ArrayList();
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "SELECT idProd FROM products "+
            "WHERE idGroup=? AND compl=1;");
            stmt.setInt(1, gr.getId());
            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                cmplProds.add(new ProductInBase("",0,0,0,0,100,
                        rs.getInt("idProd"),true,0,0));
            }
            
            
            if (cmplProds.size()>0){
            
                stmt = manager.getConnection().prepareStatement(
                    "DELETE FROM complex " +
                    "WHERE Owner=?;");

                for (Object item:cmplProds ){
                    stmt.setInt(1, ((ProductInBase)item).getId());
                    stmt.executeUpdate();
                }
        
            }

            stmt = manager.getConnection().prepareStatement(
                "DELETE FROM products " +
                "WHERE idGroup=?;");

            stmt.setInt(1, gr.getId());

            stmt.executeUpdate();

            stmt = manager.getConnection().prepareStatement(
                "DELETE FROM groups " +
                "WHERE idGroup=?;");

            stmt.setInt(1, gr.getId());

            stmt.executeUpdate();
        
            manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       
        
        return getGroups(mode);
    }
    
    public Collection updateGroup(ProdGroup gr, int mode){
        try {
        PreparedStatement 
           stmt = manager.getConnection().prepareStatement(
        "UPDATE groups SET Name=?, sortInd=? WHERE idGroup=?;");

        stmt.setString(1, gr.getName());
        stmt.setInt(2, gr.getSortInd());
        stmt.setInt(3, gr.getId());

        
        stmt.execute();
        
        stmt.close();
        manager.getConnection().commit();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getGroups(mode);
    }
   
}
