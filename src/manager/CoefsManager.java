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
import java.sql.Statement;

import java.util.Collection;
import java.sql.SQLException;
import maths.User;
import maths.CoefsSet;
import java.util.Date;


public class CoefsManager {
    private ManagementSystem manager;
    private Collection<CoefsSet> coefs;
    private User user;
    
    public CoefsManager(User user){
        manager =  ManagementSystem.getInstance();
        this.user = user;
    }
    
    public Collection<CoefsSet> getCoefs(){
        coefs = new ArrayList();
        try {   
         
         PreparedStatement stmt = manager.getConnection().prepareStatement(
         "SELECT idCoef, k1, k2, k3, time " +
         "FROM coefSets WHERE idUser=? ORDER BY time;");
         stmt.setInt(1, user.getId());

         ResultSet rs = stmt.executeQuery();
          int i = 0;
          while(rs.next()) {
              coefs.add(new CoefsSet(rs.getInt("idCoef"),rs.getFloat("k1"),
                    rs.getFloat("k2"),rs.getFloat("k3"),new Date(rs.getLong("time")),++i,
                    CoefsSet.ORDER));
          }

        rs.close();
        stmt.close();
        manager.getConnection().commit();
     }
        catch (SQLException e) {
        e.printStackTrace();
     }
     return coefs;
    }

    public Collection<CoefsSet> changeUser(User user){
        this.user = user;
        return getCoefs();
    }

    public CoefsSet getCoef(int row){
        if (coefs.size()>0) return (CoefsSet) coefs.toArray()[row];
        else return null;

    }

    public Collection<CoefsSet> updateCoef(CoefsSet coef){
        try {
         PreparedStatement stmt = manager.getConnection().prepareStatement(
                 "UPDATE coefSets SET k1=?, k2=?, k3=?, time=? " +
                 "WHERE idCoef=?;");
         stmt.setFloat(1, coef.getK1());
         stmt.setFloat(2, coef.getK2());
         stmt.setFloat(3, coef.getK3());
         stmt.setLong(4, coef.getTime().getTime());
         stmt.setInt(5, coef.getId());

         stmt.executeUpdate();
         stmt.close();
         manager.getConnection().commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return getCoefs();
    }

    public Collection<CoefsSet> addCoef(CoefsSet coef){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                 "INSERT INTO coefSets " +
                 "(k1, k2, k3, time, idUser) " +
                 "VALUES (?, ?, ?, ?, ?);");
            stmt.setFloat(1, coef.getK1());
            stmt.setFloat(2, coef.getK2());
            stmt.setFloat(3, coef.getK3());
            stmt.setLong(4, coef.getTime().getTime());
            stmt.setInt(5, user.getId());

            stmt.executeUpdate();
            stmt.close();
            manager.getConnection().commit();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return getCoefs();
    }
    public Collection<CoefsSet> deleteCoef(CoefsSet coef){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                 "DELETE FROM coefSets " +
                 "WHERE idCoef=?;");
            stmt.setInt(1, coef.getId());

            stmt.executeUpdate();
            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
                e.printStackTrace();
        }
        return getCoefs();
    }
}
