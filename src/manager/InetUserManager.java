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

//import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

//import java.util.Collection;
import java.sql.SQLException;

import maths.InetUser;

public class InetUserManager {
    private final ManagementSystem manager;

    public InetUserManager(){
        manager =  ManagementSystem.getInstance();
    }
    public InetUser getUser(){
        InetUser us = new InetUser();
        try {
            Statement stmt = manager.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM inetuser;");
            if (rs.next()){
                us = new InetUser(
                        rs.getString("login"),
                        rs.getString("pass"),
                        rs.getString("server"));
            }
            rs.close();
            stmt.close();
            manager.getConnection().commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
            us = new InetUser();
        }

        return us;
    }
    
    public void setUser(InetUser us){
        try {
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "UPDATE inetuser SET " +
                    "login=?, pass=?, server=?;"
                    );
            stmt.setString(1, us.getLogin());
            stmt.setString(2, us.getPass());
            stmt.setString(3, us.getServer());

            stmt.executeUpdate();

            stmt.close();
            manager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
