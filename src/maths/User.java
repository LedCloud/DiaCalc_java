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

public class User {
    private int id = 0;
    private String Name = "Новый пользователь";
    private float Weight = 0;
    private float Height = 0;
    private boolean male = true;
    private int calorLimit = 2000;
    private Sugar targetSh = new Sugar();
    private Sugar lowSh = new Sugar(3.2f);
    private Sugar hiSh = new Sugar(7.8f);
    private boolean direct = false;
    private boolean mmol = true;//Используем ммоль или мг/дл
    private boolean plasma = false;
    private ProductW eatenFood = new ProductW();
    private Factors factors = new Factors();
    private Sugar sh1 = new Sugar();
    private Sugar sh2 = new Sugar();
    private long time = 0l; //время последнего добавления в счетчик
    private boolean timeSense = false;
    private float OUVcoef = 180f;
    private long birthday = 0;

    public User(){
    }
    
    public User(int idUser,String Name, float Weight, float Height, boolean male,
            int calorLimit, Sugar targetSh,boolean direct, boolean mmol,
            boolean plasma,
            ProductW eatenProd, Factors fcs, Sugar sh1, Sugar sh2,
            long time, boolean timeSense, float OUVcoef,long birthday,
            Sugar lS,Sugar hS){
        id = idUser;
        this.Name = Name;
        this.Weight = Weight;
        this.Height = Height;
        this.male = male;
        this.calorLimit = calorLimit;
        this.targetSh = new Sugar(targetSh);
        this.direct = direct;
        this.mmol = mmol;
        this.plasma = plasma;
        this.eatenFood = eatenProd;
        this.factors = fcs;
        this.sh1 = new Sugar(sh1);
        this.sh2 = new Sugar(sh2);
        this.time = time;
        this.timeSense = timeSense;
        this.OUVcoef = OUVcoef;
        this.birthday = birthday;
        lowSh = lS;
        hiSh = hS;
    }
    
    public User(User source){
        id = source.id;
        Name = source.Name;
        Weight = source.Weight;
        Height = source.Height;
        male = source.male;
        calorLimit = source.calorLimit;
        targetSh = source.targetSh;
        direct = source.direct;
        mmol = source.mmol;
        plasma = source.plasma;
        eatenFood = source.eatenFood;
        factors = source.factors;
        sh1 = source.sh1;
        sh2 = source.sh2;
        time = source.time;
        timeSense = source.timeSense;
        OUVcoef = source.OUVcoef;
        birthday = source.birthday;
        lowSh = source.lowSh;
        hiSh = source.hiSh;
    }
    
    @Override
    public String toString(){
        return Name;
    }
    
    public Sugar getHiSh(){
        return hiSh;
    }
    public Sugar getLowSh(){
        return lowSh;
    }
    public void setHiSh(Sugar s){
        hiSh = s;
    }
    public void setLoSh(Sugar s){
        lowSh = s;
    }
    public long getBirthday(){
        return birthday;
    }
    public void setBirthday(long v){
        birthday = v;
    }
    
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    
    public String getName(){
        return Name;
    }
    public void setName(String Name){
        this.Name = Name;
    }
    
    public float getWeight(){
        return Weight;
    }
    public void setWeight(float Weight){
        this.Weight = Weight;
    }
    
    public float getHeight(){
        return Height;
    }
    public void setHeight(float Height){
        this.Height = Height;
    }
    
    public boolean isMale(){
        return male;
    }
    public void setMale(boolean Male){
        this.male = Male;
    }
    
    public int getCalorLimit(){
        return calorLimit;
    }
    public void setCalorLimit(int Limit){
        calorLimit = Limit;
    }
    
    public Sugar getTargetSh(){
        return targetSh;
    }
    public void setTargetSh(Sugar targetSh){
        this.targetSh = new Sugar(targetSh);
    }
    
    public boolean isDirect(){
        return direct;
    }
    public void setDirect(boolean direct){
        this.direct = direct;
    }
   
    public boolean isMmol(){
        return mmol;
    }
    public void setMmol(boolean mmol){
        this.mmol = mmol;
    }
    
    
    public boolean isPlasma(){
        return plasma;
    }
    public void setPlasma(boolean plasma){
        this.plasma = plasma;
    }
    
    public ProductW getEatenFood(){
        return eatenFood;
    }
    public void setEatenFood(ProductW prod){
        eatenFood = prod;
    }
    
    public Factors getFactors(){
        return factors;
    }
    public void setFactors(Factors fcs){
        factors = fcs;
    }
    
    public Sugar getSh1(){
        return sh1;
    }
    public void setSh1(Sugar sh1){
        this.sh1=new Sugar(sh1);
    }
    public Sugar getSh2(){
        return sh2;
    }
    public void setSh2(Sugar sh2){
        this.sh2=new Sugar(sh2);
    }
    
    public long getEatenTime(){
        return time;
    }
    public void setEatenTime(long time){
        this.time = time;
    }
    
    public boolean isTimeSense(){
        return timeSense;
    }
    public void setTimeSense(boolean timeSense){
        this.timeSense = timeSense;
    }

    public float getOUVcoef(){
        return OUVcoef;
    }
    public void setOUVcoef(float OUVcoef){
        this.OUVcoef = OUVcoef;
    }
}
