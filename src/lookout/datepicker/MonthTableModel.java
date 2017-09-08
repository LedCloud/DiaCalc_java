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

package lookout.datepicker;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.text.*;

public class MonthTableModel extends AbstractTableModel {
    private GregorianCalendar date;
    private int rows;
    private int [] dayofweek = new int [7];
    private int [] indexofdayinweek = new int [8];
    private String [] columnNames = new String[7];
    private GregorianCalendar c;
    
    private int firstdaypos;
    private int daysinmonth;
    private int daysinpriormonth;
    private int selectedday;
    public MonthTableModel(GregorianCalendar date){
        this.date = date;
        initCalendar();
    }
    public void changeDate(GregorianCalendar date){
        this.date = date;
        initCalendar();
        this.fireTableStructureChanged();
    }
    private void initCalendar(){
        String [] daynames = new DateFormatSymbols().getShortWeekdays();
        int fdw = Calendar.getInstance().getFirstDayOfWeek();
        int dw = 0;
        for(int i=fdw;i<=Calendar.SATURDAY;i++){
            dayofweek[dw] = i;
            indexofdayinweek[i] = dw;
            columnNames[dw++] = daynames[i];
        }
        if (fdw>Calendar.SUNDAY){
            int day = Calendar.SUNDAY;
            while (dw<7){
               indexofdayinweek[day] = dw;
               dayofweek[dw] = day;
               columnNames[dw++] = daynames[day++];
            }
        }
        daysinmonth = calculateDaysInMonth(date);
        c = new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                1);
        int dofw = indexofdayinweek[c.get(Calendar.DAY_OF_WEEK)];
        c.set(Calendar.DATE, daysinmonth);
        if (dofw==0){
            dofw = 7;
        }
        firstdaypos = dofw;
        rows =  (dofw+daysinmonth)/7+1;
        c.add(Calendar.DATE, -daysinmonth-1);
        daysinpriormonth = calculateDaysInMonth(c);
        selectedday = date.get(Calendar.DATE);
    }
    public GregorianCalendar getDate(int row,int col){
        GregorianCalendar res = new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                1);
        int v = row * 7 + col - firstdaypos;
        res.add(Calendar.DATE, v);
        return res;
    }
  @Override
  public Class getColumnClass(int col) {
      return Integer.class;
  }
    @Override
    public Object getValueAt(int row,int col){
        int v = (row * 7 + col - firstdaypos)+1;
        if (v<=0) v = -1*(daysinpriormonth + v);
        else if (v>daysinmonth) v = v - daysinmonth + 100;
        else if (v==selectedday) v += 1000;
        return v;
    }
    @Override
    public int getColumnCount(){
        return 7;
    }
    @Override
    public int getRowCount(){
        return rows;
    }
    @Override
    public String getColumnName(int col){
        return columnNames[col];
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
}
