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

package products;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import java.io.Serializable;
import maths.Dose;

public class ProductW implements Serializable{
    protected String  Name = new String();
    protected float   Prot = 0f;
    protected float   Fat = 0f;
    protected float   Carb = 0f;
    protected int Gi = 50;
    protected float   Weight = 0f;
    
    public ProductW(){
        
    }
    
    public ProductW(String newName, float newProt, float newFat, float newCarb,
            int newGi, float newWeight){
        Name = newName;
        Prot = newProt;
        Fat = newFat;
        Carb = newCarb;
        setGi(newGi);
        setWeight(newWeight);
    }
    
    public ProductW(ProductW newProd){
        Name = newProd.getName();
        Prot = newProd.getProt();
        Fat = newProd.getFat();
        Carb = newProd.getCarb();
        Gi = newProd.getGi();
        Weight = newProd.getWeight();
    }
    
    /*public void copyProduct(ProductW newProd){
        Name = newProd.getName();
        Prot = newProd.getProt();
        Fat = newProd.getFat();
        Carb = newProd.getCarb();
        Gi = newProd.getGi();
        Weight = newProd.getWeight();
    }*/
    
    public String getName(){
        return Name;
    }
    
    public float getProt(){
        return Prot;
    }
    
    public float getFat(){
        return Fat;
    }
    
    public float getCarb(){
        return Carb;
    }
    
    public int getGi(){
        return Gi;
    }
    
    public float getWeight(){
        return Weight;
    }
    
    public float getCalories(){
        return Dose.PROT * getAllProt() + Dose.FAT * getAllFat() +
                Dose.CARB * getAllCarb();
    }
    public float getGL(){
        return Carb * Weight * Gi /10000f;
    }
    public void flush(){
            Name = "";
            Prot = 0f;
            Fat = 0f;
            Carb = 0f;
            Gi = 0;
            Weight = 0f;
    }
    
    public void setName(String newName){
        Name = newName;
    }
    public void setProt(float newProt){
        Prot = newProt;
    }
    public void setFat(float newFat){
        Fat = newFat;
    }
    public void setCarb(float newCarb){
        Carb = newCarb;
    }
    public void setGi(int newGi){
        if (newGi<0) Gi = 50;
        else if (newGi>100) Gi = 100;
        else if (newGi==0) Gi = 50;
        else Gi = newGi;
    }
    public void setWeight(float newWeight){
        if (newWeight<0) Weight = 0f;
        else Weight = newWeight;
    }
    
    public float getAllProt(){
        return Prot*Weight/100f;
    }
    
    public float getAllFat(){
        return Fat*Weight/100f;
    }
    
    public float getAllCarb(){
        return Carb*Weight/100f;
    }
    
    public void changeWeight(float newWeight){
        if (newWeight>0 && Weight!=0f){
                float kW = Weight / newWeight;
                
                if ((Prot = Prot * kW)>100f) Prot = 100f;
                if ((Fat = Fat * kW)>100f) Fat = 100f;
                if ((Carb = Carb * kW)>100f) Carb = 100f;
        }
        else{
            Prot = 0f;
            Fat = 0f;
            Carb = 0f;
            Gi = 0;
        }
        setWeight(newWeight);
    }
    
    public void plusProd(ProductW ProdToAdd){
        float AllProt = Prot * Weight /100f + ProdToAdd.getAllProt();
        float AllFat = Fat * Weight /100f + ProdToAdd.getAllFat();
        float AllCarb = Carb * Weight /100f + ProdToAdd.getAllCarb();
        float AllCarbQuick = (Gi/100f) * Carb * Weight /100f +
                          (ProdToAdd.getGi()/100f) * ProdToAdd.getAllCarb();
        int newGi;
        if (AllCarb==0) newGi = 50;
        else newGi = Math.round(100f * AllCarbQuick / AllCarb); //RoundTo(100 * AllCarbQuick / AllUg , 0 );
        
        float newWeight = Weight + ProdToAdd.getWeight();

        
        if (newWeight!=0){
            if (Name.length()==0) Name = ProdToAdd.getName();
            else Name += " " + ProdToAdd.getName();
            Prot = 100f*AllProt/newWeight;
            Fat =  100f*AllFat/newWeight;
            Carb = 100f*AllCarb/newWeight;
            setGi(newGi);
            Weight = newWeight;
            
        } else flush();
            
    }
    public boolean equals(ProductW pr){
      return Name.equals(pr.Name) && Prot==pr.Prot && Fat==pr.Fat &&
                Carb==pr.Carb && Gi==pr.Gi && Weight==pr.Weight;
    }
    
    @Override
    public String toString(){
        return Name+" "+Prot+" "+Fat+" "+Carb+" "+Gi;
    }
}

