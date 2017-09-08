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

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */


import javax.swing.*;
import lookout.settings.ProgramSettings;


public class StringEditor extends DefaultCellEditor{

    protected JTextField fld;
    
    

    public StringEditor(final JTextField tf){
        super(tf);
        tf.setBorder(null);
        
        delegate = new EditorDelegate() {
            @Override
            public void setValue(Object param) {
              String _value = (String)param;
              if (_value == null) {
                tf.setText("");
              } else {
                tf.setText(_value);
              }
              tf.selectAll();
            }

            @Override
            public Object getCellEditorValue() {
                return tf.getText();
            }
        };


    }

    

 }