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

package maths;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

public class DPS {
    private Factors fs;
    private Sugar sh1,sh2;
    
    public DPS(){
        sh1 = new Sugar();
        sh2 = new Sugar();
        fs = new Factors();
    }
    
    public DPS(Sugar newSh1, Sugar newSh2, Factors newFs){
        if (newSh1.getValue()>0) sh1 = new Sugar(newSh1);
        else sh1 = new Sugar();
        if (newSh2.getValue()>0) sh2 = new Sugar(newSh2);
        else sh2 = new Sugar();
        fs = new Factors(newFs);
    }
    
    public DPS(DPS newDps){
        sh1 = new Sugar(newDps.sh1);
        sh2 = new Sugar(newDps.sh2);
        fs = new Factors(newDps.getFs());
    }
    
    public void setSh1(Sugar newSh1){
        if (newSh1.getValue()>0) sh1 = new Sugar(newSh1);
        else sh1 = new Sugar();
    }
    
    public void setSh2(Sugar newSh2){
        if (newSh2.getValue()>0) sh2 = new Sugar(newSh2);
        else sh2 = new Sugar();
        
    }
    
    public void setFs(Factors newFs){
        fs = new Factors(newFs);
    }
    
    public Sugar getSh1(){
        return sh1;
    }
    
    public Sugar getSh2(){
        return sh2;
    }
    
    public Factors getFs(){
        return fs;
    }
    
    public float getDPSDose(){
        if (fs.getK3()<0.01f) return 0f;
        return (sh1.getValue()-sh2.getValue())/fs.getK3();
    }
            
}
