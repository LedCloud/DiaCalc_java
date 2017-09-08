/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2006-2012 Toporov Konstantin. All rights reserved.
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
 * Portions Copyrighted 2006-2017 Toporov Konstantin.
 */

package lookout;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JProgressBar;

/**
 * @author Toporov Konstantin <www.diacalc.org> 
 */
/**
 * Прогресс бар с двумя градусниками
 * @author connie
 */
public class TwoColorsProgressBar extends JProgressBar{
    private static final Color ALEX_RED = new Color(0xDA,0x25,0x1D);//da251dff
    private static final Color ALEX_RED_LIGHT = new Color(0xF8,0xA2,0x9E);//da251dff
    private static final Color ALEX_GREEN = new Color(0,0x92,0x3F);
    private static final Color ALEX_GREEN_LIGH = new Color(0x58,0xdd,0xb3);
    private static final int MAX_VALUE = 100;
    private int forcast = 0;
    
    /**
     * Инициализируем прогресс бар
     */
    public TwoColorsProgressBar(){
        super(0,MAX_VALUE-1);
        //setOpaque(false);
        setOrientation(JProgressBar.VERTICAL);
    }
    /**
     * Устанавливаем величину градусника, а заодно меняем его
     * цвет на красный при превышении.
     * @param vl 
     */
    @Override
    public void setValue(int vl){
        if (vl>(MAX_VALUE-1)) setForeground(ALEX_RED);
        else setForeground(ALEX_GREEN);
        super.setValue(vl<MAX_VALUE?vl:(MAX_VALUE-1) );
    }
    /**
     * Устанавливаем прогноз
     * @param f - значение прогноза
     */ 
    public void setForcast(int f){
        forcast = f;
        repaint();
    }
    
    /**
     * Отрисовываем прогноз поверх прогрессбара.
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);//Рисуется обычный градусник
        //Рисуем по старому
        //Тут рисуем предположение
        Graphics2D g2d = (Graphics2D) g.create();
        if (getValue()<(MAX_VALUE-1) && forcast>0){
            //Rectangle r = g2d.getClipBounds();
            float ratio = (float)MAX_VALUE / (float)getHeight();
            
            int f_height = (int)Math.round(forcast/ratio);
            int y_current = (int)Math.round(getValue()/ratio);
            //Прогноз превысит лимит
            if ((MAX_VALUE-getValue()-forcast)<=0){
                g2d.setColor(ALEX_RED_LIGHT);
                g2d.fillRect(1, 1 , 
                    getWidth()-2, getHeight()-y_current);
                
            }else {
                g2d.setColor(ALEX_GREEN_LIGH);
                g2d.fillRect(1, getHeight() - f_height - y_current , 
                    getWidth()-2, f_height);
            }
        }
        g2d.dispose();
    }
}