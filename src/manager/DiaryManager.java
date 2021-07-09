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

import java.sql.*;


import java.util.Collection;
import java.sql.SQLException;
import maths.User;
import maths.DiaryUnit;
import maths.Factors;
import products.ProductInMenu;
import tablemodels.MenuTableModel;
import products.ProductW;

public class DiaryManager {
    private final ManagementSystem manager;
    private User user;
    private int lastInsertedId = -1;

    public DiaryManager(User user){
        manager = ManagementSystem.getInstance();
        this.user = user;
    }

    public void changeUser(User user){
        this.user = user;
    }

    public Collection<DiaryUnit> getDiaryRecords(long from,long to, String filter){
        Collection<DiaryUnit> res = new ArrayList();

        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                "SELECT * " +
                "FROM diary WHERE owner=? AND (timestmp BETWEEN ? AND ?)" +
                        (filter.length()>0?(" AND "+filter):"") +
                        " ORDER BY timestmp DESC;");
            stmt.setInt(1, user.getId());
            stmt.setLong(2, from);
            stmt.setLong(3, to);
            /*String st = "SELECT * " +
                "FROM diary WHERE owner="+user.getId()+" AND timestmp>="+from+" AND timestmp<="+to +
                 (filter.length()>0?(" AND "+filter):"") +
                 " ORDER BY timestmp DESC;";
            System.out.println("q="+st);*/

            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int type = rs.getInt("type");
                DiaryUnit u = null;
                switch (type){
                    case DiaryUnit.SUGAR:
                        u = new DiaryUnit(rs.getInt("idEvent"),rs.getLong("timestmp"),
                            rs.getString("comment"),rs.getFloat("sh1"),
                            user.getId());
                        break;
                    case DiaryUnit.MENU:
                        Factors f = new Factors(rs.getFloat("k1"),rs.getFloat("k2"),
                          rs.getFloat("k3"),rs.getFloat("be"));
                        ProductW prod = new ProductW("",rs.getFloat("prot"),rs.getFloat("fats"),
                          rs.getFloat("carb"),rs.getInt("gi"),rs.getFloat("weight"));

                        u = new DiaryUnit(rs.getInt("idEvent"),rs.getLong("timestmp"),
                          rs.getString("comment"),rs.getFloat("sh1"),
                          rs.getFloat("sh2"),f,rs.getFloat("dose"),
                          user.getId(),prod);
                        break;
                    case DiaryUnit.COMMENT:
                        u = new DiaryUnit(rs.getInt("idEvent"),rs.getLong("timestmp"),
                            rs.getString("comment"), user.getId());
                        break;
                }
                res.add(u);
            }

            rs.close();
            stmt.close();
            manager.getConnection().commit();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    public void addComment(DiaryUnit u){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "INSERT INTO diary (timestmp, comment, type, owner) " +
                        "VALUES (?, ?, ?, ?);" );

            stmt.setLong(1, u.getTime());
            stmt.setString(2, u.getComment());
            stmt.setInt(3, u.getType());
            stmt.setInt(4, user.getId());

            stmt.executeUpdate();

            Statement stmt2 = manager.getConnection().createStatement();
            ResultSet rsl = stmt2.executeQuery("SELECT last_insert_rowid() FROM diary;");
            lastInsertedId = -1;
            if (rsl.next()) lastInsertedId = rsl.getInt(1);
            rsl.close();

            stmt2.close();
            stmt.close();
            manager.getConnection().commit();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void addUnit(DiaryUnit u){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                "INSERT INTO diary (timestmp, comment, sh1, type, owner) " +
                    "VALUES (?, ?, ?, ?, ?);" );

            stmt.setLong(1, u.getTime());
            stmt.setString(2, u.getComment());
            stmt.setFloat(3, u.getSh1());
            stmt.setInt(4, u.getType());
            stmt.setInt(5, user.getId());
          
            stmt.executeUpdate();

            Statement stmt2 = manager.getConnection().createStatement();
            ResultSet rsl = stmt2.executeQuery("SELECT last_insert_rowid() FROM diary;");
            lastInsertedId = -1;
            if (rsl.next()) lastInsertedId = rsl.getInt(1);
            rsl.close();

            stmt2.close();
            stmt.close();
            manager.getConnection().commit();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void updateUnit(DiaryUnit u){
        try {
            PreparedStatement stmt;
            switch (u.getType()){
                case DiaryUnit.SUGAR:
                    stmt = manager.getConnection().prepareStatement(
                      "UPDATE diary SET timestmp=?, comment=?, sh1=?, " +
                      "type=? WHERE idEvent=?;" );
                    stmt.setLong(1, u.getTime());
                    stmt.setString(2, u.getComment());
                    stmt.setFloat(3, u.getSh1());
                    stmt.setInt(4,u.getType());
                    stmt.setInt(5, u.getId());
                    break;
                case DiaryUnit.MENU:
                    stmt = manager.getConnection().prepareStatement(
                        "UPDATE diary SET timestmp=?, comment=?, sh1=?, " +
                        "sh2=?, k1=?, k2=?, k3=?, be=?, dose=?, type=?, " +
                        "prot=?, fats=?, carb=?, gi=?, weight=? " +
                        "WHERE idEvent=?;"
                        );
                    stmt.setLong(1, u.getTime());
                    stmt.setString(2, u.getComment());
                    stmt.setFloat(3, u.getSh1());
                    stmt.setFloat(4, u.getSh2());
                    stmt.setFloat(5, u.getFactors().getK1Value());
                    stmt.setFloat(6, u.getFactors().getK2());
                    stmt.setFloat(7, u.getFactors().getK3());
                    stmt.setFloat(8, u.getFactors().getBEValue());
                    stmt.setFloat(9, u.getDose());
                    stmt.setInt(10, u.getType());
                    stmt.setFloat(11, u.getProduct().getProt());
                    stmt.setFloat(12, u.getProduct().getFat());
                    stmt.setFloat(13, u.getProduct().getCarb());
                    stmt.setInt(14, u.getProduct().getGi());
                    stmt.setFloat(15, u.getProduct().getWeight());
                    stmt.setInt(16, u.getId());
                    break;
                case DiaryUnit.COMMENT:
                    stmt = manager.getConnection().prepareStatement(
                        "UPDATE diary SET timestmp=?, comment=?, " +
                        "type=? WHERE idEvent=?;" );
                    stmt.setLong(1, u.getTime());
                    stmt.setString(2, u.getComment());
                    stmt.setInt(3,u.getType());
                    stmt.setInt(4, u.getId());
                    break;
                default: return;//Никогда не должно случиться.
            }

            stmt.executeUpdate();

            stmt.close();
            manager.getConnection().commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMenu(DiaryUnit u, Collection<ProductInMenu> menu,
            Collection<ProductInMenu> snack)
    {
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "INSERT INTO diary (timestmp, comment, sh1, sh2, " +
                      "k1, k2, k3, be, dose, type, owner, " +
                      "prot, fats, carb, gi, weight) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, u.getTime());
            stmt.setString(2, u.getComment());
            stmt.setFloat(3, u.getSh1());
            stmt.setFloat(4, u.getSh2());
            stmt.setFloat(5, u.getFactors().getK1Value());
            stmt.setFloat(6, u.getFactors().getK2());
            stmt.setFloat(7, u.getFactors().getK3());
            stmt.setFloat(8, u.getFactors().getBEValue());
            stmt.setFloat(9, u.getDose());
            stmt.setInt(10, u.getType());
            stmt.setInt(11, user.getId());
            stmt.setFloat(12, u.getProduct().getProt());
            stmt.setFloat(13, u.getProduct().getFat());
            stmt.setFloat(14, u.getProduct().getCarb());
            stmt.setInt(15, u.getProduct().getGi());
            stmt.setFloat(16, u.getProduct().getWeight());

            stmt.executeUpdate();

            Statement stmt2 = manager.getConnection().createStatement();
            ResultSet rsl = stmt2.executeQuery("SELECT last_insert_rowid() FROM diary;");
            lastInsertedId = -1;
            if (rsl.next()) lastInsertedId = rsl.getInt(1);
            rsl.close();
            stmt2.close();


            //Тут сохраняем меню
            stmt = manager.getConnection().prepareStatement(
                    "INSERT INTO storedMenu " +
                    "(name, prot, fat, carb, gi, weight, snack, iduser, owner) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            if (menu!=null) {
                for(ProductInMenu item:menu){
                    stmt.setString(1, item.getName());
                    stmt.setFloat(2, item.getProt());
                    stmt.setFloat(3, item.getFat());
                    stmt.setFloat(4, item.getCarb());
                    stmt.setInt(5, item.getGi());
                    stmt.setFloat(6, item.getWeight());
                    stmt.setInt(7, MenuTableModel.MENU_TABLE);
                    stmt.setInt(8, user.getId());
                    stmt.setInt(9, lastInsertedId);

                    stmt.addBatch();
                }
            }
            if (snack!=null) {
                for(ProductInMenu item:snack){
                    stmt.setString(1, item.getName());
                    stmt.setFloat(2, item.getProt());
                    stmt.setFloat(3, item.getFat());
                    stmt.setFloat(4, item.getCarb());
                    stmt.setInt(5, item.getGi());
                    stmt.setFloat(6, item.getWeight());
                    stmt.setInt(7, MenuTableModel.SNACK_TABLE);
                    stmt.setInt(8, user.getId());
                    stmt.setInt(9, lastInsertedId);

                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
            stmt.close();

            manager.getConnection().commit();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    public void deleteUnit(DiaryUnit u){
        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "DELETE FROM diary WHERE idEvent=?;"
                    );
            stmt.setInt(1, u.getId());
            stmt.executeUpdate();

            stmt = manager.getConnection().prepareStatement(
                    "DELETE FROM storedMenu WHERE owner=?;"
                    );
            stmt.setInt(1, u.getId());
            stmt.executeUpdate();

            stmt.close();
            manager.getConnection().commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Collection<ProductInMenu> getMenu(DiaryUnit u){
        Collection<ProductInMenu> prods = new ArrayList();
        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "SELECT * FROM storedMenu WHERE owner=? AND snack=?;");
            stmt.setInt(1, u.getId());
            stmt.setInt(2, MenuTableModel.MENU_TABLE);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                prods.add(new ProductInMenu(rs.getString("name"),
                                            rs.getFloat("prot"),
                                            rs.getFloat("fat"),
                                            rs.getFloat("carb"),
                                            rs.getInt("gi"),
                                            rs.getFloat("weight"),
                                            0));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return prods;
    }
    public Collection<ProductInMenu> getSnack(DiaryUnit u){
        Collection<ProductInMenu> prods = new ArrayList();
        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "SELECT * FROM storedMenu WHERE owner=? AND snack=?;");
            stmt.setInt(1, u.getId());
            stmt.setInt(2, MenuTableModel.SNACK_TABLE);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                prods.add(new ProductInMenu(rs.getString("name"),
                                            rs.getFloat("prot"),
                                            rs.getFloat("fat"),
                                            rs.getFloat("carb"),
                                            rs.getInt("gi"),
                                            rs.getFloat("weight"),
                                            0));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return prods;
    }
}
