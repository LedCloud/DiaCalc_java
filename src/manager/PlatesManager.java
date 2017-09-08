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

import java.util.*;
import maths.Plate;

import java.sql.*;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class PlatesManager {
    private ManagementSystem manager = ManagementSystem.getInstance();

    private Vector<Plate> plates;

    public PlatesManager(){

    }

    public Vector<Plate> getPlates(){
        plates = new Vector();
        try{
            Statement stmt = manager.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idPlate, name, weight " +
                    "FROM plates ORDER BY name;");
            while (rs.next()){
                plates.add(new Plate(rs.getInt("idPlate"),rs.getString("name"),
                        rs.getFloat("weight")));
            }
            rs.close();
            stmt.close();

            manager.getConnection().commit();

        } catch (SQLException e) {
        e.printStackTrace();
        }
        return plates;
    }
    public int addPlate(Plate plate){
        int idadded = -1;
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "INSERT INTO plates " +
                    "(name, weight) " +
                    "VALUES (?, ?);"
                    );
            stmt.setString(1, plate.getName());
            stmt.setFloat(2, plate.getWeight());

            stmt.executeUpdate();

            Statement stmt2 = manager.getConnection().createStatement();
            ResultSet rsl = stmt2.executeQuery("SELECT last_insert_rowid() FROM plates;");

            if (rsl.next()) idadded = rsl.getInt(1);
            rsl.close();
            stmt2.close();
            stmt.close();

            manager.getConnection().commit();
            
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return idadded;
    }
    public void deletePlate(Plate plate){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "DELETE FROM plates WHERE idPlate=?;");
            stmt.setInt(1, plate.getId());
            stmt.executeUpdate();

            stmt.close();

            manager.getConnection().commit();
        } catch (SQLException e) {
        e.printStackTrace();
        }
    }
    public void updatePlate(Plate plate){
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "UPDATE plates SET name=?, weight=? WHERE idPlate=?;");

            stmt.setString(1, plate.getName());
            stmt.setFloat(2, plate.getWeight());
            stmt.setInt(3, plate.getId());

            stmt.executeUpdate();
            stmt.close();

            manager.getConnection().commit();
        } catch (SQLException e) {
        e.printStackTrace();
        }

    }
    public BufferedImage getPict(int id){
        BufferedImage image = null;
        try{
            PreparedStatement stmt = manager.getConnection().prepareStatement(
                    "SELECT pict FROM plates WHERE idPlate=?;"
                    );
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                byte[] b = rs.getBytes("pict");
                if (b!=null){
                    try{
                        ByteArrayInputStream bais = new ByteArrayInputStream(b);
                        image = ImageIO.read(bais);
                        bais.close();
                    }catch(Exception e){}
                }
                
            }
            rs.close();
            stmt.close();

            manager.getConnection().commit();
        }
         catch (SQLException e) {
            e.printStackTrace();
        }
        return image;
    }
    public void setPict(int id,BufferedImage image){
          if(image != null) {
            try{
              ByteArrayOutputStream baos = new ByteArrayOutputStream(50000);
              ImageIO.write(image, "jpg", baos);
              byte[] buffer = baos.toByteArray();
              baos.close();

              ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                    PreparedStatement stmt = manager.getConnection().prepareStatement(
                        "UPDATE plates SET pict=? WHERE idPlate=?;"
                        );

                    stmt.setBytes(1, buffer);
                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                    stmt.close();

                    manager.getConnection().commit();
                

              bais.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
          }else{
              try{
                    PreparedStatement stmt = manager.getConnection().prepareStatement(
                        "UPDATE plates SET pict=? WHERE idPlate=?;"
                        );

                    stmt.setBytes(1, null);

                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                    stmt.close();

                    manager.getConnection().commit();
              }catch (SQLException ex){
                  ex.printStackTrace();
              }
                
          }

          
    }
}
