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

import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class TimedCoefsSet {//3600000
    private static long BASE = 0;
    static {
     try {
         BASE = new SimpleDateFormat("HH:mm").parse("00:00").getTime();
     }catch(ParseException e){
         BASE = -3600000*3;//Для Москвы
     }
    }
    public final static long DELTA = 10800000;
    public final static long HOUR = 3600000;
    public final static long ZERO = BASE  + DELTA;
    public final static long ONE = BASE + HOUR + DELTA;
    public final static long TWO = BASE + HOUR*2 + DELTA;
    public final static long TREE = BASE + HOUR*3 + DELTA;
    public final static long FOUR = BASE + HOUR*4 + DELTA;
    public final static long FIVE = BASE + HOUR*5 + DELTA;
    public final static long SIX = BASE + HOUR*6 + DELTA;
    public final static long SEVEN = BASE + HOUR*7 + DELTA;
    public final static long EIGHT = BASE + HOUR*8 + DELTA;
    public final static long NINE = BASE + HOUR*9 + DELTA;
    public final static long TEN = BASE + HOUR*10 + DELTA;
    public final static long ELEVEN = BASE + HOUR*11 + DELTA;
    public final static long TWELVE = BASE + HOUR*12 + DELTA;
    public final static long ONE_PM = BASE + HOUR*13 + DELTA;
    public final static long TWO_PM = BASE + HOUR*14 + DELTA;
    public final static long TREE_PM = BASE + HOUR*15 + DELTA;
    public final static long FOUR_PM = BASE + HOUR*16 + DELTA;
    public final static long FIVE_PM = BASE + HOUR*17 + DELTA;
    public final static long SIX_PM = BASE + HOUR*18 + DELTA;
    public final static long SEVEN_PM = BASE + HOUR*19 + DELTA;
    public final static long EIGHT_PM = BASE + HOUR*20 + DELTA;
    public final static long NINE_PM = BASE + HOUR*21 + DELTA;
    public final static long TEN_PM = BASE + HOUR*22 + DELTA;
    public final static long ELEVEN_PM = BASE + HOUR*23 + DELTA;
   

    private final long [] hours = {
        ZERO,ONE,TWO,TREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,
        ELEVEN,TWELVE,ONE_PM,TWO_PM,TREE_PM,FOUR_PM,FIVE_PM,SIX_PM,SEVEN_PM,
        EIGHT_PM,NINE_PM,TEN_PM,ELEVEN_PM};
   
    private ArrayList<CoefsSet>    cfs;
    
    public TimedCoefsSet(Collection<CoefsSet> col){
        cfs = new ArrayList(col);
    }

    public Collection<CoefsSet> getTimedCoefs(){
        Collection<CoefsSet> result=new ArrayList();
        if (cfs.isEmpty()) return null;
        for (int i=0;i<24;i++){
            CoefsSet lo = findLo(new Date(hours[i]-DELTA));
            CoefsSet hi = findHi(new Date(hours[i]-DELTA));
            long line = hi.getTime().getTime() - lo.getTime().getTime();
            if (line==0){ result.add(new CoefsSet(0,
                    lo.getK1(),lo.getK2(),lo.getK3(),new Date(hours[i]-DELTA),i,
                    CoefsSet.TIME));
            }
            else{
                if (line<0) line += hours[23];
                long increase = hours[i] - (lo.getTime().getTime()+DELTA);
                if (increase<0) increase += hours[23];
                float newk1 = (hi.getK1() - lo.getK1()) * increase / line + lo.getK1();
                float newk2 = (hi.getK2() - lo.getK2()) * increase / line + lo.getK2();
                float newk3 = (hi.getK3() - lo.getK3()) * increase / line + lo.getK3();
                result.add(new CoefsSet(0,
                    newk1,newk2,newk3,new Date(hours[i]-DELTA),i,
                    CoefsSet.TIME) );
            }
        }
        ArrayList<CoefsSet> arr = new ArrayList(result);
        Collection<CoefsSet> res = new ArrayList();
        for (int i=6;i<24;i++){
            res.add(arr.get(i));
        }
        for (int i=0;i<6;i++){
            res.add(arr.get(i));
        }

        /*for (CoefsSet item:res){
            System.out.println(item);
        }*/
        return res;
    }

    private CoefsSet findLo(Date time){
        CoefsSet res=null;
        for (int i=cfs.size(); i>0; i--){
            if (cfs.get(i-1).getTime().getTime()<=time.getTime()){
                res = cfs.get(i-1);
                break;
            }
        }
        if (res==null) res = cfs.get(cfs.size()-1);//.lastElement();
        return res;
    }

    private CoefsSet findHi(Date time){
        CoefsSet res=null;
        for (int i=0;i<cfs.size();i++){
            if (cfs.get(i).getTime().getTime()>time.getTime()){
                res = cfs.get(i);
                break;
            }
        }
        if (res==null) res = cfs.get(0);//.firstElement();
        return res;
    }
}

