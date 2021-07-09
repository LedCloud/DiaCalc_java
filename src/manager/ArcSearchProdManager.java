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

import products.ProductInBase;
import products.ProdGroup;
import products.Pair;

import java.util.ArrayList;
import java.sql.*;
import maths.SearchString;

public class ArcSearchProdManager {
    private final ManagementSystem manager;

    public ArcSearchProdManager(){
        //Тут надо иницировать соединение
        manager = ManagementSystem.getInstance();
    }

    public Collection doSearch(String string4search){
        Collection list = new ArrayList();
        try{
            String query = "SELECT G.idGroup, G.name, P.idProd, P.name, P.prot, P.fats, P.carb, P.gi " +
                "FROM arcgroups G, arcproducts P " +
                "WHERE (P.idGroup=G.idGroup)";
            String toadd ="";
            if (string4search.length()>0){
                toadd = SearchString.getSQLPart(string4search,SearchString.PRODUCTS,null);
                toadd = toadd.replaceAll("name", "P.name");
                toadd = toadd.replaceAll("prot", "P.prot");
                toadd = toadd.replaceAll("fats", "P.fats");
                toadd = toadd.replaceAll("carb", "P.carb");
                toadd = toadd.replaceAll("gi", "P.gi");
                toadd = " AND " + toadd;

            }
            query += toadd + " ORDER BY P.name;";

            Statement stmt = manager.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()){
                 ProdGroup gr = new ProdGroup(rs.getInt(1),
                         rs.getString(2),0);
                 ProductInBase prod = new ProductInBase(rs.getString(4),
                    rs.getFloat(5),rs.getFloat(6),rs.getFloat(7),
                    rs.getInt(8),0,rs.getInt(3),
                    false,gr.getId(),0);
                 list.add(new Pair(gr,prod));
            }

            rs.close();
            stmt.close();

            manager.getConnection().commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}

