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
package lookout.cellroutins;


import javax.swing.*;
//import lookout.settings.ProgramSettings;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;


/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
public class TimeEditor  extends DefaultCellEditor{
    private final static long ZERO = -10800000;
    private final static long EIGHT_O_CLOCK = 18000000;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    
    public TimeEditor(final JFormattedTextField tf){
        super(tf);
        format.setLenient(false);

        tf.setFocusLostBehavior(JFormattedTextField.COMMIT);
        tf.setHorizontalAlignment(SwingConstants.RIGHT);
        tf.setBorder(null);
        tf.setInputVerifier(new TimeVerifier());

        delegate = new EditorDelegate() {
            @Override
            public void setValue(Object param) {
                Date _value = (Date)param;
                if (_value == null) {
                    tf.setValue(format.format(new Date(EIGHT_O_CLOCK)));
                } else {
                    Date _d = _value;
                    String _format = format.format(_d);
                    tf.setText(_format);
                }
                tf.selectAll();
            }

            @Override
            public Object getCellEditorValue() {
                try {
                    String _field = tf.getText();
                    Date _date = format.parse(_field);
                    return _date;
                } catch (ParseException ex) {
                    return new Date(EIGHT_O_CLOCK);
                }
            }
        };
    }
}
