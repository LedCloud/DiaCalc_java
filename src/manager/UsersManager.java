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

import maths.User;
import maths.Factors;
import products.ProductW;
import maths.Sugar;

public class UsersManager {
    private ManagementSystem manager;
    private Collection users;
    
    public UsersManager(){
        manager =  ManagementSystem.getInstance();
        loadUsers();
        if (users.isEmpty()) users.add(new User());//Должен существовать хотя бы один
        //пользователь
    }
    
    private void loadUsers(){
        users = new ArrayList();
        try {   
         Statement stmt = manager.getConnection().createStatement();
         ResultSet rs = stmt.executeQuery( "SELECT * FROM users ORDER BY idUser;");
                 
          while(rs.next()) {
            Factors fcs = new Factors(rs.getFloat("k1"),rs.getFloat("k2"),
                      rs.getFloat("k3"),rs.getFloat("BE"));
            ProductW prod = new ProductW("",rs.getFloat("prot"),rs.getFloat("fat"),
                    rs.getFloat("carb"),rs.getInt("gi"),rs.getFloat("FoodWeight") );
            
            users.add(new User(rs.getInt("idUser"),rs.getString("name"),
                    rs.getFloat("weight"),rs.getFloat("height"),
                    rs.getInt("male")!=0,rs.getInt("calorLimit"),
                    new Sugar(rs.getFloat("targetSh")),rs.getInt("direct")==1,
                    rs.getInt("mmol")==1,rs.getInt("plasma")==1,
                    prod, fcs, new Sugar(rs.getFloat("sh1")),
                    new Sugar(rs.getFloat("sh2")),rs.getLong("time"),
                    rs.getInt("timeSense")==1, rs.getFloat("OUVcoef"),
                    rs.getLong("birthday"),
                    new Sugar(rs.getFloat("lowSugar")),
                    new Sugar(rs.getFloat("hiSugar"))));
          }

        rs.close();
        stmt.close();
        manager.getConnection().commit();
     }
        catch (SQLException e) {
        e.printStackTrace();
     }
   }
    
   public int addUser(User updateUser){
       int newOneId = -1;
       try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "INSERT INTO users "+
            "(name, weight, height, male, calorLimit, mmol, plasma, targetSh, " +
                    "direct, BE, k1, k2, k3, sh1, sh2, prot, fat, carb, gi, " +
                    "FoodWeight, time, timeSense, OUVcoef, birthday, lowSugar, hiSugar) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?);");
            
            stmt.setString(1, updateUser.getName());
            stmt.setFloat(2, updateUser.getWeight());
            stmt.setFloat(3, updateUser.getHeight());
            stmt.setInt(4, updateUser.isMale()?1:0);
            stmt.setInt(5, updateUser.getCalorLimit());
            stmt.setInt(6, updateUser.isMmol()?1:0);
            stmt.setInt(7, updateUser.isPlasma()?1:0);
            stmt.setFloat(8, updateUser.getTargetSh().getValue());
            stmt.setInt(9, updateUser.isDirect()?1:0);
            stmt.setFloat(10, updateUser.getFactors().getBEValue());
            stmt.setFloat(11, updateUser.getFactors().getK1Value());
            stmt.setFloat(12, updateUser.getFactors().getK2());
            stmt.setFloat(13, updateUser.getFactors().getK3());
            stmt.setFloat(14, updateUser.getTargetSh().getValue());//sh1
            stmt.setFloat(15, updateUser.getTargetSh().getValue());//sh2
            stmt.setFloat(16, updateUser.getEatenFood().getProt());
            stmt.setFloat(17, updateUser.getEatenFood().getFat());
            stmt.setFloat(18, updateUser.getEatenFood().getCarb());
            stmt.setInt(19, updateUser.getEatenFood().getGi());
            stmt.setFloat(20, updateUser.getEatenFood().getWeight());
            stmt.setLong(21, updateUser.getEatenTime());
            stmt.setInt(22, updateUser.isTimeSense()?1:0);
            stmt.setFloat(23, updateUser.getOUVcoef());
            stmt.setLong(24, updateUser.getBirthday());
            stmt.setFloat(25, updateUser.getLowSh().getValue());
            stmt.setFloat(26, updateUser.getHiSh().getValue());
            
            
            stmt.executeUpdate();
            stmt.close();
           
         Statement stmt2 = manager.getConnection().createStatement();
         ResultSet rs = stmt2.executeQuery("SELECT last_insert_rowid() FROM users;");
            
         if (rs.next()) newOneId = rs.getInt(1);
         stmt2.close();

         manager.getConnection().commit();
       }catch(SQLException ex) {
           ex.printStackTrace();
       }
       loadUsers();
       return newOneId;
   }
   
   public void updateUser(User updateUser){
       try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "UPDATE users SET name=?, weight=?, height=?, male=?, " +
                    "calorLimit=?, mmol=?, plasma=?, targetSh=?, direct=?, " +
                    "BE=?, k1=?, k2=?, k3=?, sh1=?, sh2=?, " +
                    "prot=?, fat=?, carb=?, gi=?, FoodWeight=?, " +
                    "time=?, timeSense=?, OUVcoef=?, birthday=?, " +
                    "lowSugar=?, hiSugar=? " +
                    "WHERE idUser=?;");
            
            stmt.setString(1, updateUser.getName());
            stmt.setFloat(2, updateUser.getWeight());
            stmt.setFloat(3, updateUser.getHeight());
            stmt.setInt(4, updateUser.isMale()?1:0);
            stmt.setInt(5, updateUser.getCalorLimit());
            stmt.setInt(6, updateUser.isMmol()?1:0);
            stmt.setInt(7, updateUser.isPlasma()?1:0);
            stmt.setFloat(8, updateUser.getTargetSh().getValue());
            stmt.setInt(9, updateUser.isDirect()?1:0);
            stmt.setFloat(10, updateUser.getFactors().getBEValue());
            stmt.setFloat(11, updateUser.getFactors().getK1Value());
            stmt.setFloat(12, updateUser.getFactors().getK2());
            stmt.setFloat(13, updateUser.getFactors().getK3());
            stmt.setFloat(14, updateUser.getTargetSh().getValue());//sh1
            stmt.setFloat(15, updateUser.getTargetSh().getValue());//sh2
            stmt.setFloat(16, updateUser.getEatenFood().getProt());
            stmt.setFloat(17, updateUser.getEatenFood().getFat());
            stmt.setFloat(18, updateUser.getEatenFood().getCarb());
            stmt.setInt(19, updateUser.getEatenFood().getGi());
            stmt.setFloat(20, updateUser.getEatenFood().getWeight());
            stmt.setLong(21, updateUser.getEatenTime());
            stmt.setInt(22, updateUser.isTimeSense()?1:0);
            stmt.setFloat(23, updateUser.getOUVcoef());
            stmt.setLong(24, updateUser.getBirthday());
            stmt.setFloat(25, updateUser.getLowSh().getValue());
            stmt.setFloat(26, updateUser.getHiSh().getValue());
            
            stmt.setInt(27, updateUser.getId());
            
            stmt.executeUpdate();
       
            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       loadUsers();
   }
   
   public void deleteUser(User user){
       if (users.size()>1){
           try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "DELETE FROM diary WHERE owner=?;");
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();

            stmt = manager.getConnection().prepareStatement(
                    "DELETE FROM storedMenu WHERE idUser=?;");
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();

            stmt = manager.getConnection().prepareStatement(
            "DELETE FROM coefSets " +
            "WHERE idUser=?;");

            stmt.setInt(1, user.getId());

            stmt.executeUpdate();
        
            stmt = manager.getConnection().prepareStatement(
            "DELETE FROM menu " +
            "WHERE idUser=?;");

            stmt.setInt(1, user.getId());

            stmt.executeUpdate();
        
            stmt = manager.getConnection().prepareStatement(
            "DELETE FROM snack " +
            "WHERE idUser=?;");

            stmt.setInt(1, user.getId());

            stmt.executeUpdate();
        
            stmt = manager.getConnection().prepareStatement(
                "DELETE FROM users " +
                "WHERE idUser=?;");

                stmt.setInt(1, user.getId());

             stmt.executeUpdate();
             stmt.close();
             manager.getConnection().commit();
          } catch (SQLException e) {
            e.printStackTrace();
          }   
        loadUsers();
       }
   }
   public void updateFood(User user){
        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "UPDATE users SET " +
                    "prot=?, fat=?, carb=?, gi=?, FoodWeight=?, time=? " +
                    "WHERE idUser=?;");
            
            stmt.setFloat(1, user.getEatenFood().getProt());
            stmt.setFloat(2, user.getEatenFood().getFat());
            stmt.setFloat(3, user.getEatenFood().getCarb());
            stmt.setInt(4, user.getEatenFood().getGi());
            stmt.setFloat(5, user.getEatenFood().getWeight());
            stmt.setLong(6, user.getEatenTime());
            
            stmt.setInt(7, user.getId());
            
            stmt.executeUpdate();
       
            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       loadUsers();
   }
   public void updateFactors(User user){
       try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
            "UPDATE users SET direct=?, " +
                    "BE=?, k1=?, k2=?, k3=?, sh1=?, sh2=? " +
                    "WHERE idUser=?;");
            
            
            stmt.setInt(1, user.isDirect()?1:0);
            stmt.setFloat(2, user.getFactors().getBEValue());
            stmt.setFloat(3, user.getFactors().getK1Value());
            stmt.setFloat(4, user.getFactors().getK2());
            stmt.setFloat(5, user.getFactors().getK3());
            stmt.setFloat(6, user.getSh1().getValue());//sh1
            stmt.setFloat(7, user.getSh2().getValue());//sh2
            
            
            
            stmt.setInt(8, user.getId());
            
            stmt.executeUpdate();
       
            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       loadUsers();
   }
   public User getUser(int pos){
       loadUsers();
       if (pos<0)                       return (User) users.toArray()[0];
       else if (pos>(users.size()-1))   return (User) users.toArray()[users.size() - 1];

       return (User) users.toArray()[pos];
   }
   public int getAmount(){
       return users.size();
   }

   public void initSugars(){
       try{
           Statement stmt = manager.getConnection().createStatement();
           stmt.executeUpdate("UPDATE users SET sh1=targetSh, sh2=targetSh;");

           stmt.close();
           manager.getConnection().commit();
       }
       catch (SQLException e) {
            e.printStackTrace();
       }
       loadUsers();
   }
}
