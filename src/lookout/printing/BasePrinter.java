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
import java.awt.print.*;
import java.util.*;
import maths.User;
import java.text.*;
import products.ProductInBase;
import products.ProdGroup;
import maths.Dose;
import maths.DPS;
import products.ProductW;

public class BasePrinter  implements Printable{
    public final static int DOSE = 1;
    public final static int BE = 2;
    public final static int CALOR = 3;
    
    private ArrayList<ArrayList<String>> rows = new ArrayList();
    private ArrayList<String> row = new ArrayList();
    private User user;
    Rectangle [] recs;
    int cellheight;
    DecimalFormat df0 = new DecimalFormat("0.0");
    DecimalFormat df00 = new DecimalFormat("0.00");
    private int head_height;
    private int mode;
    
    private int w;
    private int firstw;
    private int bzhu;
    private int gr;
    private int gr10;
    
    public BasePrinter(int mode,ArrayList<ProdGroup> groups, 
            ArrayList<ProductInBase> prods,User user){
        this.user = user;
        this.mode = mode;
        for(ProdGroup group:groups){
            row = new ArrayList();
            row.add(group.getName());
            rows.add(row);
            for(ProductInBase prod:prods){
                if (group.getId()==prod.getOwner()){
                    row = new ArrayList();
                    row.add(prod.getName());
                    row.add(df0.format(prod.getProt()));
                    row.add(df0.format(prod.getFat()));
                    row.add(df0.format(prod.getCarb()));
                    row.add(""+prod.getGi());
                    ProductW [] prodw = new ProductW[10];
                    for(int i=0;i<10;i++){
                        prodw[i] = new ProductW(
                                prod.getName(),
                                prod.getAllProt()*100f/prod.getWeight(),
                                prod.getAllFat()*100f/prod.getWeight(),
                                prod.getAllCarb()*100f/prod.getWeight(),
                                prod.getGi(),
                                100f);
                        prodw[i].setWeight( (i+1)*10 );
                    }
                    for(ProductW p:prodw){
                        switch (mode){
                            case DOSE: row.add(
                                    df0.format(
                                       new Dose(p,user.getFactors(),new DPS()).getWholeDose())); 
                                        break;
                            case BE:   row.add(
                                    df0.format(
                                    p.getAllCarb()/user.getFactors().getBE(user.isDirect())));
                                    break;
                            case CALOR: row.add(df0.format(p.getCalories()));
                                    break;
                        }
                    }
                    rows.add(row);
                }
            }
            
        }
    }
    
    @Override
    public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException {
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        head_height = drawHead(g2d);
        g2d.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,8));
        int st_height = g2d.getFontMetrics().getHeight()+2;
        int page_left_height = g2d.getClipBounds().height - head_height;
        int st_count = page_left_height / st_height;
        int pages_possible = rows.size()/st_count + 
                ((rows.size()%st_count>0)?1:0);
        if (page>(pages_possible-1)) return NO_SUCH_PAGE;
        int first_line = page * st_count;
        int i=0;
        while (i<st_count && (i+first_line)<rows.size()){
            String name;
            if (rows.get(i+first_line).size()==1){
                name = rows.get(i+first_line).get(0);
                Rectangle r = new Rectangle(0,i*st_height+head_height,
                        firstw,st_height);
                drawCenteredCell(g2d,r,name,false);
            }else{
                name = rows.get(i+first_line).get(0);
                Rectangle r = new Rectangle(0,i*st_height+head_height,
                        firstw,st_height);
                drawLeftCell(g2d,r,name,true);
                drawCenteredCell(g2d,
                        new Rectangle(firstw,i*st_height+head_height,
                        bzhu,st_height)
                        ,rows.get(i+first_line).get(1),true);//Б
                drawCenteredCell(g2d,
                        new Rectangle(firstw+bzhu,i*st_height+head_height,
                        bzhu,st_height)
                        ,rows.get(i+first_line).get(2),true);//Ж
                drawCenteredCell(g2d,
                        new Rectangle(firstw+bzhu*2,i*st_height+head_height,
                        bzhu,st_height)
                        ,rows.get(i+first_line).get(3),true);//У
                drawCenteredCell(g2d,
                        new Rectangle(firstw+bzhu*3,i*st_height+head_height,
                        bzhu,st_height)
                        ,rows.get(i+first_line).get(4),true);//ГИ
                for(int j=0;j<10;j++){
                    drawCenteredCell(g2d,
                        new Rectangle(firstw+bzhu*4+j*gr10,
                        i*st_height+head_height,
                        gr10,st_height)
                        ,rows.get(i+first_line).get(5+j),true);//характреристики
                }
            }
            i++;
        }
        return PAGE_EXISTS;
    }
    
    private int drawHead(Graphics g) throws PrinterException{
        int res;
        int heightHead = 30;
        Point start;
        if (mode!=CALOR){
            int st_height = g.getFontMetrics().getHeight();
            if (g.getClipBounds().height<(heightHead+st_height)){
                throw new PrinterException();
            }
            res = heightHead+st_height + 3;
            start = new Point(0,st_height);
            String out = "База продуктов ";
            switch(mode){
                case DOSE: out += "k1=" + df00.format(user.getFactors().getK1(user.isDirect()))+
                " k2=" + df00.format(user.getFactors().getK2()) + 
                       (user.isDirect()?(
                        ""
                        )
                        :
                         (" XE="+df0.format(user.getFactors().getBE(user.isDirect())))
                           ); break;
                case BE:  out += "ХЕ="+(user.isDirect()?"":df0.format(user.getFactors().getBE(user.isDirect())));
                   break;
            }

            g.drawString(out, start.x, start.y);
            start.y = start.y + 3;
        }
        else{
            if (g.getClipBounds().height<heightHead){
                throw new PrinterException();
            }
            start  = new Point(0,0);
            res = heightHead;
        }
        w = g.getClipBounds().width;
        firstw = w * 35/100;//30
        bzhu = w * 5 /100;//30+15
        gr = w - bzhu*4 - firstw;
        firstw  += gr%10 - 1;
        gr = w - bzhu*4 - firstw;
        gr10 = gr/10;
        
        g.setColor(Color.GRAY);
        g.drawRect(start.x, start.y, firstw, heightHead);//Наименование
        g.drawRect(start.x+firstw, start.y, bzhu, heightHead);//Б
        g.drawRect(start.x+firstw+bzhu, start.y, bzhu, heightHead);//Ж
        g.drawRect(start.x+firstw+bzhu*2, start.y, bzhu, heightHead);//У
        g.drawRect(start.x+firstw+bzhu*3, start.y, bzhu, heightHead);//ГИ
        g.drawRect(start.x+firstw+bzhu*4, start.y, gr10*10, heightHead/2);//Дозы для разного кол-ва продуктов
        for(int i=0;i<10;i++){
            g.drawRect(start.x+firstw+i*gr10+bzhu*4, start.y+heightHead/2, gr10, heightHead/2);
            //10 гр 20 гр и т.д.
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
        int fh = g.getFontMetrics().getHeight();
        String name  ="Наименование";
        int fw = g.getFontMetrics().stringWidth(name);
        if (fw>firstw) throw new PrinterException();
        g.drawString(name, start.x+firstw/2-fw/2,
                start.y+heightHead/2+fh/2);
        g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,7));
        fw = g.getFontMetrics().stringWidth("Б");
        if (fw>bzhu) throw new PrinterException();
        g.drawString("Б", start.x+firstw+bzhu/2-fw/2,
                start.y+heightHead/2+fh/2);
        fw = g.getFontMetrics().stringWidth("Ж");
        if (fw>bzhu) throw new PrinterException();
        g.drawString("Ж", start.x+firstw+bzhu + bzhu/2-fw/2,
                start.y+heightHead/2+fh/2);
        fw = g.getFontMetrics().stringWidth("У");
        if (fw>bzhu) throw new PrinterException();
        g.drawString("У", start.x+firstw+bzhu*2 + bzhu/2-fw/2,
                start.y+heightHead/2+fh/2);
        fw = g.getFontMetrics().stringWidth("ГИ");
        if (fw>bzhu) throw new PrinterException();
        g.drawString("ГИ", start.x+firstw+bzhu*3 + bzhu/2-fw/2,
                start.y+heightHead/2+fh/2);
        switch(mode){
            case DOSE: name = "Дозы для разного количества продуктов";break;
            case BE : name = "Количество ХЕ для разного количества продуков";break;
            case CALOR: name = "Количество калорий для разного количества продуктов";break;
        }
        
        fh = g.getFontMetrics().getHeight();
        fw = g.getFontMetrics().stringWidth(name);
        if (fw>gr) throw new PrinterException();
        g.drawString(name, start.x+firstw + bzhu*4 + gr/2 -fw/2,
                start.y+heightHead/4+fh/2);
        for(int i=0;i<10;i++){
            name = ""+(10+i*10)+" г";
            fw = g.getFontMetrics().stringWidth(name);
            if (fw>gr10) throw new PrinterException();
            g.drawString(name, start.x+firstw + bzhu*4 + gr10/2 - fw/2 + gr10*i,
                start.y+heightHead*3/4+fh/2);
        }
        return res;
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
        /*if (g.getFontMetrics().stringWidth(st)>r.width) 
            throw new PrinterException();*/
        if (drawrect){
            g.setColor(Color.GRAY);
            g.drawRect(r.x, r.y, r.width, r.height);
        }
        int y =  r.y + r.height/2 + g.getFontMetrics().getHeight()/2-2;
        int x = r.x  + 2;
        g.setColor(Color.BLACK);
        g.drawString(st, x, y);
    }
}
