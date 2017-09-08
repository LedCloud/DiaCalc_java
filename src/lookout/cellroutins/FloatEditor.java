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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import java.text.*;
//import lookout.settings.ProgramSettings;


public class FloatEditor extends DefaultCellEditor{
  private NumberFormat floatFormat;
  private PositiveFloatVerifier pfv = new PositiveFloatVerifier(true);

  public FloatEditor(final JFormattedTextField tf, NumberFormat nf) {
   super(tf);
   floatFormat = nf;
   tf.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
   tf.setHorizontalAlignment(SwingConstants.RIGHT);
   tf.setBorder(null);
   tf.addFocusListener(new FocusAdapter(){
       @Override
       public void focusLost(FocusEvent e) {
           //Сообщаем слушателяем, что надо заканчивать
           FloatEditor.this.stopCellEditing();
       }
   });
   
   delegate = new EditorDelegate() {
     @Override
    public void setValue(Object param) {
     Float _value = (Float)param;
     if (_value == null) {
      tf.setValue(floatFormat.format(0.0f));
     } else {
      Float _d = _value.floatValue();
      String _format = floatFormat.format(_d);
      tf.setText(_format);
     }
     tf.selectAll();
    }

     @Override
    public Object getCellEditorValue() {
     try {
      pfv.verify(tf);
      String _field = tf.getText();
      Number _number = floatFormat.parse(_field);
      Float _parsed = _number.floatValue();
      Float d = new Float(_parsed);
      return d;
     } catch (ParseException e) {
        return new Float(0.0f);
     }
    }
   };
  }
}