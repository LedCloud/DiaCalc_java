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

package lookout.cellroutins;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import java.text.ParseException;
import java.text.DecimalFormatSymbols;
import maths.Calculator;


public class PositiveFloatVerifier extends InputVerifier {
    private final boolean calc;
    private DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    private final Calculator calculator;
    
    public PositiveFloatVerifier(boolean calc){
        this.calc = calc;
        symbols = new DecimalFormatSymbols();
        calculator = new Calculator();
    }
    
    @Override
    public boolean verify(JComponent input) {
             if (input instanceof JFormattedTextField) {
             JFormattedTextField ftf = (JFormattedTextField)input;
             JFormattedTextField.AbstractFormatter formatter = ftf.getFormatter();
             if (formatter != null) {
                 boolean should_replace = false;
                 String text = ftf.getText().replace(',', symbols.getDecimalSeparator())
                         .replace('.', symbols.getDecimalSeparator());
                 if (!text.equals(ftf.getText())){
                     should_replace = true;
                 }
                 if (calc){
                    String text_calced = calculator.Calc(text,formatter);
                    if (!text_calced.equals(text)){
                        try{
                            //Проверяем проходит ли парсинг, но value не меняем
                            ((Number)formatter.stringToValue(text)).floatValue();
                        }catch (ParseException pe){
                        return false;
                        }
                        text = text_calced;
                        should_replace = true;
                    }
                 }
                 if (should_replace){
                    try {
                      float vl = ((Number)formatter.stringToValue(text)).floatValue();
                      if (vl<0.0f) ftf.setValue(0f);
                      else ftf.setValue(vl);
                      ftf.setCaretPosition(ftf.getText().length());
                    } catch (ParseException pe) {
                      return false;
                    }
                 }
              }
          }
            
          return true;
  }
    
  @Override
  public boolean shouldYieldFocus(JComponent input) {
          return verify(input);
  }
}
