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
 * Portions Copyrighted 2009-2017 Toporov Konstantin.
 */

package manager;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import java.sql.*;
import java.sql.SQLException;
import javax.swing.*;


public class DBVersionCorrect {
    private final static int VERSION = 7;
    private final ManagementSystem manager;
    private int version = 0;

    public DBVersionCorrect(JWindow owner){
        manager = ManagementSystem.getInstance();
        int oldver = version = getVersion();

        if (version<1) step1();
        if (version<2) step2();
        if (version<3) step3();
        if (version<4) step4();
        if (version<5) step5();
        if (version<6) step6();
        if (version<7) step7();

        if (oldver<VERSION){
            owner.toBack();
            JOptionPane.showMessageDialog(owner,
                "Формат БД изменен\nтекущий формат:"+VERSION,
                "Обновление БД",
                JOptionPane.INFORMATION_MESSAGE);
            owner.toFront();
        }
    }

    private int getVersion(){
        int v = 0;
        try{
            Statement stmt = manager.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("select * from sqlite_master where name='ver';");
            if (rs.next()){//значит таблица существует
                ResultSet rs_ver = stmt.executeQuery("select * from ver;");
                if (rs_ver.next()){
                    v = rs_ver.getInt("ver");
                }
                rs_ver.close();
            }

            rs.close();
            stmt.close();
            manager.getConnection().commit();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return v;
    }

    private void step1(){
        try{
            Statement stmt = manager.getConnection().createStatement();
            stmt.execute("CREATE TABLE coefSets_tmp ("+
                "idCoef INTEGER, "+
                "k1 REAL, " +
                "k2 REAL, " +
                "k3 REAL, " +
                "time INTEGER, " +
                "idUser INTEGER);" );
            stmt.execute("insert into coefSets_tmp select * from coefSets;");
            stmt.execute("drop table coefSets;");
            stmt.execute("CREATE TABLE coefSets (" +
                         "idCoef INTEGER PRIMARY KEY NOT NULL, " +
                         "k1 REAL NOT NULL, " +
                         "k2 REAL NOT NULL, " +
                         "k3 REAL NOT NULL, " +
                         "time INTEGER NOT NULL, " +
                         "idUser INTEGER NOT NULL);");
            stmt.execute("insert into coefSets select * from coefSets_tmp;");
            stmt.execute("drop table coefSets_tmp;");
            
            stmt.execute("CREATE TABLE storedMenu ( " +
                        "idProd INTEGER PRIMARY KEY NOT NULL, "+
                        "name TEXT NOT NULL, " +
                        "prot REAL NOT NULL, "+
                        "fat REAL NOT NULL, " +
                        "carb REAL NOT NULL, " +
                        "gi INTEGER NOT NULL, " +
                        "weight REAL NOT NULL , "+
                        "snack INTEGER  NOT NULL, "+
                        "idUser INTEGER  NOT NULL, " +
                        "owner INTEGER  NOT NULL);");
            
            stmt.execute("CREATE TABLE diary ("+
                        "idEvent INTEGER PRIMARY KEY NOT NULL, "+
                        "time INTEGER, "+
                        "comment TEXT, " +
                        "sh1 REAL, " +
                        "sh2 REAL, "+
                        "k1 REAL, "+
                        "k2 REAL, "+
                        "k3 REAL, " +
                        "be REAL, "+
                        "dose REAL, "+
                        "type INTEGER, "+
                        "owner INTEGER);");
            stmt.execute("create table ver (" +
                         "ver INTEGER);");
            stmt.execute("INSERT INTO ver (ver) VALUES (1);");

            stmt.close();
            manager.getConnection().commit();

            version = 1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void step2(){
        try{
            Statement stmt = manager.getConnection().createStatement();
            stmt.execute("ALTER TABLE diary ADD prot REAL;");
            stmt.execute("ALTER TABLE diary ADD fats REAL;");
            stmt.execute("ALTER TABLE diary ADD carb REAL;");
            stmt.execute("ALTER TABLE diary ADD gi INTEGER;");
            stmt.execute("ALTER TABLE diary ADD weight REAL;");
            stmt.execute("UPDATE diary "+
                    "SET prot=0, " +
                    "fats=0, " +
                    "carb=0, " +
                    "gi=0, " +
                    "weight=0;");
            stmt.execute("CREATE TABLE plates ("+
                         "idPlate INTEGER PRIMARY KEY, "+
                         "name TEXT NOT NULL," +
                         "weight REAL, " +
                         "pict BLOB);");

            stmt.execute("UPDATE ver SET ver=2;");
            stmt.close();
            manager.getConnection().commit();
            version = 2;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void step3(){
        try{
            Statement stmt = manager.getConnection().createStatement();
            stmt.execute("CREATE TABLE arcgroups " +
                        "(idGroup INTEGER PRIMARY KEY NOT NULL," +
                         "name TEXT," +
                         "mainInd INTEGER);");
            stmt.execute("CREATE TABLE arcproducts (" +
                         "idProd INTEGER PRIMARY KEY NOT NULL," +
                         "name TEXT NOT NULL," +
                         "prot REAL NOT NULL," +
                         "fats REAL NOT NULL," +
                         "carb REAL NOT NULL," +
                         "gi INTEGER NOT NULL," +
                         "weight REAL NOT NULL," +
                         "compl INTEGER NOT NULL," +
                         "idGroup INTEGER NOT NULL);");

            stmt.execute("UPDATE ver SET ver=3;");
            stmt.close();
            manager.getConnection().commit();
            version = 3;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void step4(){
        try{
            Statement stmt = manager.getConnection().createStatement();
            stmt.execute("ALTER TABLE users ADD birthday INTEGER;");
            stmt.execute("UPDATE users SET birthday=0;");
            stmt.execute("UPDATE ver SET ver=4;");
            stmt.close();
            manager.getConnection().commit();
            version = 4;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void step5(){
        try{
            Statement stmt = manager.getConnection().createStatement();
            stmt.execute("CREATE TABLE inetuser (" +
                    "login TEXT," +
                    "pass TEXT," +
                    "server TEXT" +
                    ");");
            stmt.execute("INSERT INTO inetuser (login, pass, server) " +
                    "VALUES ('', '', 'http://diacalc.org/dbwork/');");

            stmt.execute("UPDATE ver SET ver=5;");
            stmt.close();
            manager.getConnection().commit();
            version = 5;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void step6(){
        try{
            Statement stmt = manager.getConnection().createStatement();
            stmt.addBatch("ALTER TABLE users ADD lowSugar REAL;");
            stmt.addBatch("ALTER TABLE users ADD hiSugar REAL;");
            stmt.addBatch("UPDATE users SET lowSugar=3.2;");
            stmt.addBatch("UPDATE users SET hiSugar=7.8;");
            stmt.addBatch("UPDATE ver SET ver=6;");
            stmt.executeBatch();
            stmt.close();
            manager.getConnection().commit();
            version = 6;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void step7(){
        try{
            Statement stmt = manager.getConnection().createStatement();
            stmt.addBatch("ALTER TABLE diary RENAME to tmp_diary;");
            stmt.addBatch("CREATE TABLE diary (\n" +
                "idEvent INTEGER PRIMARY KEY NOT NULL, \n" +
                "timestmp INTEGER, \n" +
                "comment TEXT,\n" +
                "sh1 REAL, \n" +
                "sh2 REAL, \n" +
                "k1 REAL, \n" +
                "k2 REAL, \n" +
                "k3 REAL, \n" +
                "be REAL, \n" +
                "dose REAL, \n" +
                "type INTEGER, \n" +
                "owner INTEGER, \n" +
                "prot REAL, \n" +
                "fats REAL, \n" +
                "carb REAL, \n" +
                "gi INTEGER, \n" +
                "weight REAL);");
            stmt.addBatch("INSERT INTO diary(timestmp,comment,sh1,sh2,k1,k2,k3,be,dose,type,owner,prot,fats,carb,gi,weight)\n" +
                "SELECT time,comment,sh1,sh2,k1,k2,k3,be,dose,type,owner,prot,fats,carb,gi,weight\n" +
                "FROM tmp_diary;");
            stmt.addBatch("DROP TABLE tmp_diary;");
            stmt.addBatch("UPDATE ver SET ver=7;");
            stmt.executeBatch();
            stmt.close();
            manager.getConnection().commit();
            version = 7;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
