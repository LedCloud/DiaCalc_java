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

package lookout.datepicker;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import lookout.settings.ProgramSettings;

public class DateField extends JPanel implements PropertyChangeListener{
    private JFormattedTextField fld;
    private MaskFormatter mask;
    private final JButton dropbtn;
    private Date value;
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    private CalendarPane calPane = null;
    private JPopupMenu menu;
    private final ProgramSettings settings = ProgramSettings.getInstance();
    private final Dimension size = new Dimension(settings.getIn().getSizedValue(177),
                settings.getIn().getSizedValue(22));
    
    public DateField(Date date){
        super();
        
        initMask();
        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
        
        fld = new JFormattedTextField(mask);
        fld.addPropertyChangeListener("value", this);
        fld.setColumns(14);
        if (date==null) value = cutDate(new Date());
        else value = cutDate(date);

        format.setLenient(false);
        
        fld.setValue(format.format(value));
        fld.setInputVerifier(new DateVerifier());

        calPane = new CalendarPane(this,value);

        dropbtn = new JButton( createImageIcon("images/" + 
                settings.getIn().getSizedPath(false) + 
                "down.png") );
        dropbtn.setMargin(new Insets(0,0,0,0));
        menu = new JPopupMenu();
        menu.add(calPane);

        dropbtn.addMouseListener(new PopupListener(menu));
        dropbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calPane.setDate(value);
                menu.show(fld,
                           0, fld.getHeight()+1);
            }
        });
        
        add(fld);
        add(dropbtn);
    }
    
    @Override
    public Dimension getPreferredSize(){
        return size;
    }
    @Override
    public Dimension getMinimumSize(){
        return size;
    }
    @Override
    public Dimension getMaximumSize(){
        return size;
    }


    private void initMask(){
        try{
            mask = new MaskFormatter("##.##.####");
            mask.setPlaceholderCharacter('_');
        }catch(Exception e){}
    }

    

    @Override
    public void propertyChange(PropertyChangeEvent ev){
        Date old = value;
        try{
            value = format.parse((String)ev.getNewValue());
        } catch (ParseException ex){
                return;
        }
        firePropertyChange("value", old, value);
    }

    public Date getDate(){
        return value;
    }

    public void setDate(Date date){
        firePropertyChange("value", value, value = cutDate(date));
        fld.setValue(format.format(value));
        if (menu.isVisible()) menu.setVisible(false);
    }

    protected Date cutDate(Date date){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                calPane.setDate(value);
                popup.show(e.getComponent().getParent(),
                           0, e.getComponent().getParent().getHeight()+1);
            }
        }
    }
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DateField.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
