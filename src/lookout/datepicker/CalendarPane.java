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
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;
import lookout.settings.ProgramSettings;


public class CalendarPane extends JPanel implements ActionListener{
    private static final String PRIOR_YEAR = "prior year";
    private static final String NEXT_YEAR = "next year";
    private static final String PRIOR_MONTH = "prior month";
    private static final String NEXT_MONTH = "next month";
    private static final String TODAY = "today";
    
    private GregorianCalendar selectedDate = null;
    private final JLabel lblMonth;
    private final JLabel lblYear;
    private final DateField infield_owner;
    private JTable month;
    private final ProgramSettings settings = ProgramSettings.getInstance();
            

    public CalendarPane(final DateField owner,Date initDate){
        super();
        setLayout(new BorderLayout());
        
        infield_owner = owner;
        
        selectedDate = getDay(initDate);

        JPanel top = new JPanel(new BorderLayout());
        JPanel bottom = new JPanel(new BorderLayout());

        lblMonth = new JLabel("");
        lblYear = new JLabel("");
        
        top.add(createButton("<",PRIOR_MONTH,"prior"),BorderLayout.WEST);
        
        
        lblMonth.setHorizontalAlignment(SwingConstants.CENTER);
        
        top.add(lblMonth,BorderLayout.CENTER);

        top.add(createButton(">",NEXT_MONTH,"next"), BorderLayout.EAST);

        bottom.add(createButton("<<",PRIOR_YEAR,"prior"),BorderLayout.WEST);
        lblYear.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(lblYear,BorderLayout.CENTER);
        bottom.add(createButton(">>",NEXT_YEAR,"next"),BorderLayout.EAST);
        bottom.add(createButton("()",TODAY,"today"), BorderLayout.SOUTH);

        
        
        month = new JTable(new MonthTableModel(getDay(initDate)));
        month.setFillsViewportHeight(true);
        month.setDefaultRenderer(Integer.class, new MonthRender());
        month.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
        month.getTableHeader().setResizingAllowed(false);
        month.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent ev){
                if (ev.getButton()==MouseEvent.BUTTON1){
                     Point p = ev.getPoint();
                     int row = month.rowAtPoint(p);
                     int col = month.columnAtPoint(p);
                     if (row>=0 && col>=0){
                         GregorianCalendar dt = ((MonthTableModel)month.getModel()).getDate(row,col);
                         selectedDate = dt;
                         fillNames();
                         ((MonthTableModel)month.getModel()).changeDate(dt);

                         if (selectedDate.get(Calendar.MONTH)==
                                 dt.get(Calendar.MONTH)){
                             //выбрали дату
                             owner.setDate(dt.getTime());
                         }
                     }
                }
            }
        });
        month.setRowHeight(settings.getIn().getSizedValue(month.getRowHeight()));
        
        JScrollPane scr = new JScrollPane(month);
        scr.setPreferredSize(new Dimension(settings.getIn().getSizedValue(174),settings.getIn().getSizedValue(120)));
        add(top,BorderLayout.NORTH);
        add(scr,BorderLayout.CENTER);
        add(bottom,BorderLayout.SOUTH);

        fillNames();
    }

    private JButton createButton(String title,String actionName,String file){
        JButton button = new JButton();
        button.addActionListener(this);
        button.setActionCommand(actionName);
        button.setMargin(new Insets(0,0,0,0));
        button.setPreferredSize(new Dimension(settings.getIn().getSizedValue(25),
                settings.getIn().getSizedValue(20)));

        button.setOpaque(false);
        button.setBackground(Color.LIGHT_GRAY);
        button.setBorderPainted(false);

        String imgLocation =  "images/" + 
                settings.getIn().getSizedPath(false) + 
                file.toLowerCase() +
                ".png";
        URL imageURL = CalendarPane.class.getResource(imgLocation);

        if (imageURL != null) {                      //image found
            button.setIcon(new ImageIcon(imageURL));
        } else {                                     //no image found
            button.setText(title);
            System.err.println("Resource not found: "
                               + imgLocation);
        }
        return button;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev){
        String cmd = ev.getActionCommand();
        if (NEXT_MONTH.equals(cmd)){
            selectedDate = getNextMonth(selectedDate);
            ((MonthTableModel)month.getModel()).changeDate(selectedDate);
           
        }
        else if (PRIOR_MONTH.equals(cmd)){
            selectedDate = getPriorMonth(selectedDate);
            ((MonthTableModel)month.getModel()).changeDate(selectedDate);
           
        }
        if (NEXT_YEAR.equals(cmd)){
            selectedDate = getNextYear(selectedDate);
            ((MonthTableModel)month.getModel()).changeDate(selectedDate);
            
        }
        else if (PRIOR_YEAR.equals(cmd)){
            selectedDate = getPriorYear(selectedDate);
            ((MonthTableModel)month.getModel()).changeDate(selectedDate);
            
        }
        else if (TODAY.equals(cmd)){
            selectedDate = getDay(new Date());
            infield_owner.setDate(selectedDate.getTime());
           
        }
        fillNames();
    }
    private void fillNames(){
        lblMonth.setText(
                selectedDate.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.getDefault()));
        lblYear.setText(
                ""+selectedDate.get(Calendar.YEAR));
    }
    private static GregorianCalendar getDay(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        if (date!=null) gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc;
    }

    private static int calculateDaysInMonth(final Calendar c) {
        int daysInMonth = 0;
        switch (c.get(Calendar.MONTH)) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                daysInMonth = 31;
                break;
            case 3:
            case 5:
            case 8:
            case 10:
                daysInMonth = 30;
                break;
            case 1:
                final int year = c.get(Calendar.YEAR);
                daysInMonth =
                        (0 == year % 1000) ? 29 :
                        (0 == year % 100) ? 28 :
                        (0 == year % 4) ? 29 : 28;
                break;
        }
        return daysInMonth;
    }

    

    private GregorianCalendar getNextMonth(GregorianCalendar c){
        final int day = c.get(Calendar.DATE);
        GregorianCalendar next = new GregorianCalendar(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                1);

        next.add(Calendar.MONTH, +1);
        next.set(Calendar.DATE,
                Math.min(day, calculateDaysInMonth(next)));


        return next;
    }

    private GregorianCalendar getPriorMonth(GregorianCalendar c){
        final int day = c.get(Calendar.DATE);
        GregorianCalendar prior = new GregorianCalendar(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                1);

        
        prior.add(Calendar.MONTH, -1);
        prior.set(Calendar.DATE,
                Math.min(day, calculateDaysInMonth(prior)));

        return prior;
    }

    private GregorianCalendar getNextYear(GregorianCalendar c){
        GregorianCalendar next = new GregorianCalendar(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                1);
        final int day = c.get(Calendar.DATE);
        next.add(Calendar.YEAR, +1);
        next.set(Calendar.DATE,
                Math.min(day, calculateDaysInMonth(next)));


        return next;
    }

    private GregorianCalendar getPriorYear(GregorianCalendar c){
        GregorianCalendar prior = new GregorianCalendar(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                1);

        final int day = c.get(Calendar.DATE);
        prior.add(Calendar.YEAR, -1);
        prior.set(Calendar.DATE,
                Math.min(day, calculateDaysInMonth(prior)));

        return prior;
    }

    public void setDate(Date date){
        selectedDate = getDay(date);
        ((MonthTableModel)month.getModel()).changeDate(getDay(date));
        fillNames();
        //calculateCalendar();
    }
}
