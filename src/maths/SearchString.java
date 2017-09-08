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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

public class SearchString {
    public final static boolean PRODUCTS = true;
    public final static boolean DIARY = false;
    static private String str_conj;
    static private String str_numb;
    
    /**
     * Возвращаем строку SQL с частью запроса
     * @param source исходная строка формата "(10<Б<20) картошка"
     * @param mode режим PRODUCTS - продукты, DIARY - дневник
     * @param user текущий пользователь
     * @return String
     */
    static public String getSQLPart(String source,boolean mode,User user){
        str_conj = "";
        str_numb = "";
        String textfield;
        if (mode==PRODUCTS) textfield = "name";
        else textfield = "comment";
        if (source==null || source.trim().length()==0){
            return "";
        }
        
        String [] arr = source.trim().split(" ");
        for(String item:arr){
            String buf = item.trim();
            if (buf.length()==0){
                continue;
            }
            //Проверяем по паттерну
            String _buf = buf.replace(",", ".");
            Pattern general = Pattern.
               compile("\\((\\d*\\.?\\d+(=|>|<|>=|<=))?(Б|Ж|У|ГИ|Д|СК1|СК2|СК)((=|>|<|>=|<=)\\d*\\.?\\d+)?\\)");
            Matcher m = general.matcher( _buf );
            if (m.matches()){
                Pattern column = Pattern.compile(".?(Б|Ж|У|ГИ|Д|СК1|СК2|СК).?");
                Matcher parM = column.matcher(_buf);
                if (parM.find()){//Нашли одно из таких полей, теперь надо его выделить
                    String col = parM.group(1);
                    //Теперь получаем левую и правую часть
                    String leftPart = _buf.substring(1, parM.start(1));
                    String rightPart = _buf.substring(parM.start(1) + 
                        parM.group(1).length() ,_buf.length()-1 );
                    String lVal="";
                    String lSign="";
                    String rVal="";
                    String rSign="";
                    if (!leftPart.isEmpty()){
                        Pattern leftPattern = Pattern.compile("(\\d*\\.?\\d+)(<=|>=|<|>|=)");
                        Matcher mm = leftPattern.matcher(leftPart);
                        if ( mm.find() && mm.groupCount()==2){//Нашли, теперь запоминаем знак и значение
                            lVal = mm.group(1);//5.6>=
                            lSign = mm.group(2);
                        }
                    }//Можно было бы развернуть правую часть и обрабатывать один раз
                    //но в этом случае разворачивается и число >3.48 => 84.3<
                    if (!rightPart.isEmpty()){
                        Pattern rightPattern = Pattern.compile("(<=|>=|<|>|=)(\\d*\\.?\\d+)");
                        Matcher mm = rightPattern.matcher(rightPart);
                        if ( mm.find() && mm.groupCount()==2){//Нашли, теперь запоминаем знак и значение
                            rSign = mm.group(1);
                            rVal =  mm.group(2);//<=5.6
                        }
                    }
                    //Здесь у нас есть параметр Б Ж ГИ... знак сравнения и число
                    //Хотя бы одна пара
                    String field="";
                    switch (col) {
                        case "Б":   field = "prot"; break;
                        case "Ж":   field = "fats"; break;
                        case "У":   field = "carb"; break;
                        case "ГИ":  field = "gi";   break;
                        case "Д":   field = "dose"; break;
                        case "СК1": field = "sh1";  break;
                        case "СК2": field = "sh2";  break;
                        case "СК":  field = "sh";   break;
                        default:
                            break;
                    }
                    if (col.equals("Б")||col.equals("Ж")||col.equals("У")){
                        if (!lVal.isEmpty()){
                            str_numb += //              3.6>=
                            (str_numb.length()>0?" AND ":"") + lVal + lSign +
                            (mode==PRODUCTS?field:(field+"*weight/100"));
                        }
                        if (!rVal.isEmpty()){
                            str_numb += //    >=5.6
                            (str_numb.length()>0?" AND ":"")+
                            (mode==PRODUCTS?field:(field+"*weight/100")) +
                                    rSign + rVal;
                        }
                    }
                    if (col.equals("ГИ") ||(mode==DIARY&&col.equals("Д"))){
                        if (!lVal.isEmpty()) str_numb +=  
                            (str_numb.length()>0?" AND ":"") + //3.6>=dose
                                lVal + lSign + field ;
                        if (!rVal.isEmpty()) str_numb +=  
                            (str_numb.length()>0?" AND ":"")+  //dose<=4
                                field + rSign + rVal;
                    }
                    if (col.contains("СК") && mode==DIARY){
                        String _lVal = replaceSugar(lVal,user);
                        String _rVal = replaceSugar(rVal,user);
                        if (col.equals("СК1") || col.equals("СК2")){
                            if (!_lVal.isEmpty()) str_numb +=
                                (str_numb.length()>0?" AND ":"") 
                                    //5.6>=sh1
                                    + _lVal + lSign + field ;
                            if (!_rVal.isEmpty()) str_numb +=
                                (str_numb.length()>0?" AND ":"") + 
                                    //sh1>=3.5
                                    field + rSign + _rVal;
                        }else{//col==СК Надо обработатьь и СК1 и СК2
                            String tmp1 = "";
                            String tmp2 = "";
                            if (!_lVal.isEmpty()){
                                tmp1 = _lVal + lSign + "sh1";
                                tmp2 = _lVal + lSign + "sh2";
                            }
                            if (!_rVal.isEmpty()){
                                tmp1 +=
                                (tmp1.length()>0?" AND ":"")+
                                        "sh1" + rSign + _rVal;
                                tmp2 +=
                                (tmp2.length()>0?" AND ":"")+
                                        "sh2" + rSign + _rVal;
                            }
                            //Если присутсвуют два сравнение, то AND
                            //Если сравнение только с одним значением, то OR
                            String or = ") OR (";
                            String lBr = "((";
                            String rBr = "))";
                            if (!_lVal.isEmpty() && !_rVal.isEmpty()){
                                or = " AND ";
                                rBr = lBr = "";
                            }
                            //Проверка не имеет смысла, т.к. если прошли
                            //сквозь regex, то одно или два условия сработает.
                            //if ((tmp1.length()+tmp2.length())>0){
                             str_numb +=
                                (str_numb.length()>0?" AND ":"") +
                                   lBr+ tmp1 + or + tmp2 + rBr;
                            //}
                        }
                    }
                    
                }
            }   
            //Если просто текст, то ищем его в поле name или comment
            else{
                //Ищем в текстовом поле
                if (str_conj.length()!=0) str_conj += " OR ";
                str_conj += textfield +" LIKE '%"+ buf +"%' OR "+textfield+" LIKE " +
                        "'%"+ getCapFirst(buf) +"%'" ;
            }
        }
        String res = "";
        if (str_conj.length()>0) res += "("+str_conj +")";
        if (str_conj.length()>0 && str_numb.length()>0) res += " AND ";
        if (str_numb.length()>0) res += ""+str_numb+"";
        
        return res;
    }
    /**
     * Возвращаем строку с первой заглавной буквой.
     * @param st входная строка
     */
    static private String getCapFirst(String st){
      if (st!=null && st.length()>0){
        String c = new String("" + st.charAt(0)).toUpperCase();
        if (st.length()>1){
            return c + st.substring(1);
        }
        else return c;
      } return null;
    }
    /**
     * Возвращаем СК в формате БД
     * @param st ск из поиска в формате пользователя
     * @param user текущий пользователь
     */
    static private String replaceSugar(String st,User user){
        if (st.length()>0){
            Sugar s = new Sugar();
            s.setSugar(Float.parseFloat(st), user.isMmol(), user.isPlasma());
            return ""+s.getValue();
        }
        return "";
    }
    
}
