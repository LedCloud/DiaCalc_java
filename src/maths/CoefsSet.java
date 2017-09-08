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

package maths;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoefsSet {
    public final static boolean TIME = false;
    public final static boolean ORDER = true;

    private int     id;
    private float   k1;
    private float   k2;
    private float   k3;
    private Date    time;
    private int     row;
    private boolean mode;//true - возвращаем номер
                         //false - возвращаем время
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private DecimalFormat dcf = new DecimalFormat("0.00");

    public CoefsSet(){
        id =0;
        k1 = 1f;
        k2 = 0f;
        k3 = 10f;
        time = new Date(TimedCoefsSet.SEVEN-TimedCoefsSet.DELTA);
        row = 0;
        mode = true;
    }
    public CoefsSet(CoefsSet in){
        id = in.id;
        k1 = in.k1;
        k2 = in.k2;
        k3 = in.k3;
        time = in.time;
        row = in.row;
        mode = in.mode;
    }
    public CoefsSet(int id, float k1, float k2, float k3, Date time, int row,
            boolean mode){
        this.id = id;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.time = time;
        this.row = row;
        this.mode = mode;
    }

    public void setMode(boolean mode){
        this.mode = mode;
    }
    public void setPos(int row){
        this.row = row;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public float getK1(){
        return k1;
    }
    public void setK1(float k1){
        this.k1 = k1;
    }
    public float getK2(){
        return k2;
    }
    public void setK2(float k2){
        this.k2 = k2;
    }
    public float getK3(){
        return k3;
    }
    public void setK3(float k3){
        this.k3 = k3;
    }
    
    public Date getTime(){
        return time;
    }
    public void setTime(Date time){
        this.time = time;
    }
    
    public int getRow(){
        return row;
    }
    public void setRow(int row){
        this.row = row;
    }
    @Override
    public String toString(){
        String buf ="";
        if (mode) {
            buf ="#";
            if (row<10) buf += " ";
            buf += row;
        }
        else{
            buf += format.format(time);
        }

        buf += " : k1 "+
                dcf.format(k1)+" : k2 "+dcf.format(k2)+" : ЦЕИ "+dcf.format(k3)+" ";
        return buf;
    }
}
