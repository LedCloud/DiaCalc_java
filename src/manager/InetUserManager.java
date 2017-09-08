/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manager;

/**
 *
 * @author connie
 */
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Collection;
import java.sql.SQLException;

import maths.InetUser;

public class InetUserManager {
    private ManagementSystem manager;

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
