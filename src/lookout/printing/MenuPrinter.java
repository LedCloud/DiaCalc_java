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

package lookout.printing;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JTable;
import maths.DPS;
import maths.Dose;
import maths.User;
import products.ProductW;
import tablemodels.MenuTableModel;

public class MenuPrinter implements Printable{
    private MenuTableModel menu = null;
    private MenuTableModel snack = null;
    private final ArrayList<ArrayList<String>> rows = new ArrayList();
    private ArrayList<String> row;
    private int columns = 0;
    private Rectangle [] recs;
    int cellheight;
    private final DecimalFormat df0 = new DecimalFormat("0.0");
    private final DecimalFormat df00 = new DecimalFormat("0.00");
    private final ProductW sum;
    
    
    public MenuPrinter(User user, JTable menuT,
            JTable snackT,DecimalFormat df_prec){
        menu = (MenuTableModel)menuT.getModel();
        sum = menu.getSumProd();
        
        if (snackT!=null){
            snack = (MenuTableModel)snackT.getModel();
            sum.plusProd(snack.getSumProd());
        }
        
        row = new ArrayList();
        row.add("Меню");
        rows.add(row);
        //Делаем заголовок таблицы
        columns = menu.getColumnCount();
        
        recs = new Rectangle[columns];
        
        row = new ArrayList();
        for(int j=0;j<columns;j++){
                row.add(menu.getPureColumnName(j));
        }
        rows.add(row);
        //заполняем таблицу
        for(int i=0;i<menu.getRowCount();i++){
            row = new ArrayList();
            for(int j=0;j<columns;j++){
                Object ob = menu.getValueAt(i, j);
                if (ob instanceof String){
                    row.add((String)ob);
                }else if (ob instanceof Integer){
                    row.add(((Number)ob).toString());
                }else if (ob instanceof Float){
                    row.add(df0.format(((Number)ob).floatValue()));
                }
            }
            rows.add(row);
        }
        
        if (snack!=null){
            row = new ArrayList();
            row.add("Перекус");
            rows.add(row);
            for(int i=0;i<snack.getRowCount();i++){
                row = new ArrayList();
                for(int j=0;j<columns;j++){
                    Object ob = snack.getValueAt(i, j);
                    if (ob instanceof String){
                        row.add((String)ob);
                    }else if (ob instanceof Integer){
                        row.add(((Number)ob).toString());
                    }else if (ob instanceof Float){
                        row.add(df0.format(((Number)ob).floatValue()));
                    } 
                }
                rows.add(row);
            }
        }
        row = new ArrayList();
        row.add(
                "k1=" + df00.format(user.getFactors().getK1(user.isDirect()))+
                " k2=" + df00.format(user.getFactors().getK2())+
                " ЦЕИ=" + df00.format(user.getFactors().getK3())+
                (user.isDirect()?(" ХЕ="+df0.format(user.getFactors().getBE(user.isDirect()))):"")
                );
        rows.add(row);
        row = new ArrayList();
        row.add(
                "СКстарт=" + df0.format(user.getSh1().getSugar(user.isMmol(), user.isPlasma())) + 
                " СКцель=" + df0.format(user.getSh2().getSugar(user.isMmol(), user.isPlasma()))
                );
        rows.add(row);
        Dose ds = new Dose(sum,user.getFactors(),new DPS(user.getSh1(),user.getSh2(),user.getFactors()));
        row = new ArrayList();
        row.add(
                "ДПС+БД=" + df_prec.format(ds.getDPSDose()+ds.getCarbFastDose())
                 + " МДугл+МДбел/ж=" +
                df_prec.format(ds.getCarbSlowDose()+ds.getSlowDose()) + 
                " Вся доза =" + df_prec.format(ds.getWholeDose())
                );
        rows.add(row);
    }
    
    @Override
    public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException {
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        g2d.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
        
        int [] cellwidth = new int[columns];
        
        for(int i=0;i<columns;i++){
            cellwidth[i]=0;
        }
        //Определяем необходимую ширину столбцов
        for(int i=0;i<rows.size();i++){
            if (rows.get(i).size()>1){
                for(int j=0;j<columns;j++){
                    cellwidth[j] = 
                            g2d.getFontMetrics().stringWidth(rows.get(i).get(j))>
                            cellwidth[j]?
                              g2d.getFontMetrics().stringWidth(rows.get(i).get(j)):
                                cellwidth[j];
                }
            }
        }
        int w=0;
        for(int i=0;i<columns;i++){
            cellwidth[i] += 10;
            w += cellwidth[i];
        }
        if (w>g2d.getClipBounds().width) return NO_SUCH_PAGE;
                
        /* Now we perform our rendering */
        Point startcorner = new Point(0,0);
        
        cellheight = g.getFontMetrics().getHeight() + 1;//Два пиксела пустота и один на линию
        
        recs = new Rectangle[columns];
        for(int i=0;i<columns;i++){
            if (i==0){
                recs[i] = 
                  new Rectangle(startcorner.x,
                                startcorner.y,
                                cellwidth[i],
                                cellheight);
            }else{
                recs[i] = 
                  new Rectangle(recs[i-1].x + recs[i-1].width,
                                startcorner.y,
                                cellwidth[i],
                                cellheight);
            }
        }
        int rows_possible = g2d.getClipBounds().height/cellheight;
        int pages_possible = rows.size()/rows_possible + 
                ((rows.size()%rows_possible>0)?1:0);
        
        if (page>(pages_possible-1)) return NO_SUCH_PAGE;
        int start_row = rows_possible * page;
        int maxrow = rows.size()<start_row+rows_possible?rows.size():start_row+rows_possible - 1;
        //System.out.println(start_row + " "+maxrow+" "+rows.size());
        
        for(int i=start_row;i<maxrow;i++){
            if (rows.get(i).size()==1){
                drawLeftCell(g2d,recs[0],rows.get(i).get(0),false);
                step();
            }else{
                for(int j=0;j<rows.get(i).size();j++){
                    if (j==0){
                        drawLeftCell(g2d,recs[j],rows.get(i).get(j),true);
                    }else{
                        drawCenteredCell(g2d,recs[j],rows.get(i).get(j),true);
                    }
                }
                step();
            }
        }
        
        return PAGE_EXISTS;
        
        /* tell the caller that this page is part of the printed document */
        //return PAGE_EXISTS;
    }
    
    private void step(){
        for(Rectangle r:recs){
                r.y += cellheight;
        }
    }
    
    private void drawCenteredCell(Graphics g,Rectangle r,Object ob,boolean drawrect){
        String st = "";
        
        if (ob instanceof String){
            st = (String)ob;
        }else if (ob instanceof Integer){
            st = ((Number)ob).toString();
        }else if (ob instanceof Float){
            st = df0.format(((Number)ob).floatValue());
        }
        if (drawrect){
            g.setColor(Color.GRAY);
            g.drawRect(r.x, r.y, r.width, r.height);
        }
        int y =  r.y + r.height/2 + g.getFontMetrics().getHeight()/2-2;
        int x = r.x  + (r.width - g.getFontMetrics().stringWidth(st))/2;
        g.setColor(Color.BLACK);
        g.drawString(st, x, y);
        //g.drawLine(x, y, x+10, y);
    }
    
    private void drawLeftCell(Graphics g,Rectangle r,Object ob,boolean drawrect){
        String st = "";
        
        if (ob instanceof String){
            st = (String)ob;
        }else if (ob instanceof Integer){
            st = ((Number)ob).toString();
        }else if (ob instanceof Float){
            st = df0.format(((Number)ob).floatValue());
        }
        if (drawrect){
            g.setColor(Color.GRAY);
            g.drawRect(r.x, r.y, r.width, r.height);
        }
        int y =  r.y + r.height/2 + g.getFontMetrics().getHeight()/2-2;
        int x = r.x  + 5;
        g.setColor(Color.BLACK);
        g.drawString(st, x, y);
    }
}
