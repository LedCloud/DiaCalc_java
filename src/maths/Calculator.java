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

import javax.swing.JFormattedTextField;
import java.text.ParseException;
import java.text.DecimalFormatSymbols;

public class Calculator {
    private final char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
    
    public Calculator(){
    }
    
    public String Calc(String textIn, 
                  JFormattedTextField.AbstractFormatter formatter){
        String res = textIn;
        textIn = textIn.trim();
        if (textIn.length()>1){
            if (checkCorrect(textIn)){
                textIn = checkCharOneTime(textIn,decimalSeparator);
                textIn = checkCharOneTime(textIn,'+');
                textIn = checkCharOneTime(textIn,'-');
                float sum = 0;
                String buf = "0";
                float sign = 1;
                int i = 0;
                float vl = 0;
                while (i<textIn.length())
                {
                    if ((textIn.charAt(i)!='+')&&(textIn.charAt(i)!='-'))
                        buf = buf + textIn.charAt(i);
                    else
                    {
                        vl = 0;
                        try {
                            vl = ((Number)formatter.stringToValue(buf)).floatValue();
                        } catch (ParseException pe) {}
                        //vl = Float.parseFloat(buf);

                        sum = sum + sign *  vl;
                        buf = "0";
                        switch (textIn.charAt(i))
                        {   
                            case '+': sign = 1; break;
                            case '-': sign = -1;
                        }
                    }
                    i++;
                }
                if (buf.length()>1)
                {
                    vl = 0;
                    try {
                        vl = ((Number)formatter.stringToValue(buf)).floatValue();
                    } catch (ParseException pe) {}
                    sum = sum + sign * vl;
                }
                //System.out.println("Sum:"+sum);
                try{
                    return formatter.valueToString(sum);
                } catch (ParseException pe) {}

            }
        }
        return res;//если не получается обработка, то возвращаем то, что взяли
    }
    
    /**
     * Проверяем, что символ ch встречается только один раз,
     * если не так, то сокращаем его до одного раза
     * @param InStr строка для проверки
     * @param ch символ
     * @return возвращаем обработанную строку
     */    
    public String checkCharOneTime(String InStr, char ch)
    {
        if (InStr.length()==0) return "0";
        boolean conj = false;
        int i=0;
        StringBuilder stBl = new StringBuilder(InStr);
        while (i<stBl.length() )
        { 
            if (stBl.charAt(i)==ch){
              if (conj){ 
                  stBl = stBl.deleteCharAt(i); 
                  i--; 
              }
              else conj = true;
            }else{ 
                conj=false;
            }
            i++;
        }
        if ((stBl.length()==1)&&(conj)) stBl = new StringBuilder("0");
        return stBl.toString();
    }
    
    /**
     * Проверяем, что входная строка содержит только цифры
     * знаки плюс, минус или разделитель запятой
     * @param StrToCheck строка для проверки
     * @return true норм, false не подходит
     */
    public boolean checkCorrect(String StrToCheck )
    {
        int i=0; 
        StringBuilder stBl = new StringBuilder(StrToCheck);
        while (i<stBl.length() && stBl.length()>0)
        {
            char ch = stBl.charAt(i);    
            if ( ( ch>='0' && ch<='9')||
                (ch==decimalSeparator)||
                (ch=='+')||(ch=='-'))  i++;
                else stBl = stBl.deleteCharAt(i);
        }
        return stBl.length() != 0;
    }
}
