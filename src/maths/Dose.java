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

import products.ProductW;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

public class Dose {
    public final static float PROT = 4.0f;
    public final static float FAT = 9.0f;
    public final static float CARB = 4.0f;
    private ProductW    prod;
    private Factors     fact;
    private DPS         dps;
    private boolean     NeedReCalc;
    private float      Calories;
    private float      SlowDose; //медленная доза
    private float      CarbSlowDose;
    private float      CarbFastDose;
    
    public Dose(ProductW newProd, Factors newFs, DPS newDps){
        prod = new ProductW( newProd );
        fact = new Factors(newFs);
        dps = new DPS(newDps);
        NeedReCalc = true;
    }
    
    public Dose(Dose newDose){
        prod = new ProductW( newDose.prod );
        fact = new Factors( newDose.fact );
        dps = new DPS( newDose.dps );
        NeedReCalc = true;
    }
    
    public Dose(){
        prod = new ProductW();
        fact = new Factors();
        dps = new DPS();
        NeedReCalc = true;
    }
    
    private void calcDoses(){
        float kWH = PROT * prod.getAllProt();
        float kFAT = FAT * prod.getAllFat();
        float kUG = CARB * prod.getAllCarb();
         
        Calories = kWH + kFAT +kUG;

        SlowDose = fact.getK2() * kWH/100f + fact.getK2() * kFAT/100f;
        //    (    WH Doze   )   (   Fat Doze     )
                
        //здесь 1 надо заменить на коэффициент позволяющий учитывать углеводы
        //по количеству
        CarbSlowDose = fact.getK1(Factors.DIRECT) / fact.getBE(Factors.DIRECT) *
                    ( prod.getAllCarb() * (100f - (float)prod.getGi()) / 100f );
        CarbFastDose = fact.getK1(Factors.DIRECT) / fact.getBE(Factors.DIRECT) *
                ( prod.getAllCarb() *  (float)prod.getGi() / 100f );
        
        NeedReCalc = false;
    }
    
    public void setProduct(ProductW newProduct){
        prod = new ProductW( newProduct );
        NeedReCalc = true;
    }
    
    public void setFactors(Factors newFs){
        fact = new Factors( newFs );
        NeedReCalc = true;
    }
    
    public void setDPS(DPS newDps){
        dps = new DPS(newDps);
        NeedReCalc = true;
    }
    
    public float getSlowDose(){
        if (NeedReCalc) calcDoses();
        return SlowDose;
    }
    
    public float getCarbSlowDose(){
        if (NeedReCalc) calcDoses();
        return CarbSlowDose;
        
    }
    
    public float getCarbFastDose(){
        if (NeedReCalc) calcDoses();
        return CarbFastDose;
    }
    
    public float getCalories(){
        if (NeedReCalc) calcDoses();
        return Calories;
    }
    
    public float getWholeDose(){
        if (NeedReCalc) calcDoses();
        return SlowDose+CarbSlowDose+CarbFastDose+dps.getDPSDose();
    }

    public float getDPSDose(){
        return dps.getDPSDose();
    }
}
