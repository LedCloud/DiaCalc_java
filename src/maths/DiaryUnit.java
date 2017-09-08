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

import products.ProductW;

public class DiaryUnit {
    public final static int MENU = 0;
    public final static int SUGAR = 1;
    public final static int COMMENT = 2;
    private int id;
    private long time;
    private String comment;
    private float sh1;
    private float sh2;
    private int type;
    private int owner;
    private Factors fcs;
    private float dose;
    private ProductW prod;

    //create comment
    public DiaryUnit(int id, long time, String comment, int owner){
        this.id = id;
        this.time = time;
        this.comment = comment;
        type = COMMENT;
        this.owner = owner;
    }
    //create sugar
    public DiaryUnit(int id, long time, String comment, float sh, int owner){
        this.id = id;
        this.time = time;
        this.comment = comment;
        sh1 = sh;
        type = SUGAR;
        this.owner = owner;
    }
    //create menu
    public DiaryUnit(int id,long time, String comment, float sh1, float sh2,
            Factors fc, float dose, int owner, ProductW prod){
        this.id = id;
        this.time = time;
        this.comment = comment;
        this.sh1 = sh1;
        this.sh2 = sh2;
        fcs = fc;
        this.dose = dose;

        type = MENU;
        this.owner = owner;
        this.prod = prod;
    }

    public int getOwner(){
        return owner;
    }
    public int getId(){
        return id;
    }
    public String getComment(){
        return comment;
    }
    public int getType(){
        return type;
    }
    public ProductW getProduct(){
        return type==MENU? prod: null;
    }
    public Factors getFactors(){
        return type==MENU? fcs : null;
    }
    public float getSh1(){
        return (type==SUGAR || type==MENU ) ? sh1: null;
    }
    public float getSh2(){
        return type==MENU? sh2 : null;
    }
    public float getDose(){
        return type==MENU? dose : null;
    }
    public long getTime(){
        return time;
    }

    public void setProduct(ProductW prod){
        this.prod = prod;
    }
    public void setTime(long v){
        time = v;
    }
    public void setOwner(int v){
        owner = v;
    }
    public void setId(int v){
        id = v;
    }
    public void setComment(String v){
        comment = v;
    }
    public void setFactors(Factors v){
        fcs = v;
    }
    public void setSh1(float v){
        sh1 = v;
    }
    public void setSh2(float v){
        sh2 = v;
    }
    public void setDose(float v){
        dose = v;
    }
}
