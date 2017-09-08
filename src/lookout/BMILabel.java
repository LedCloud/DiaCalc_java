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

package lookout;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import javax.swing.*;
import java.awt.*;
import lookout.settings.ProgramSettings;


public class BMILabel extends JLabel{
    ProgramSettings settings = ProgramSettings.getInstance();
    
    //public final static Color STARVING = new Color();
    private final int lwidth = settings.getIn().getSizedValue(15);//240,100
    private final Dimension minimum = new Dimension(settings.getIn().getSizedValue(95),
            settings.getIn().getSizedValue(100));
    //Category 	BMI range - kg/m2
    //Starvation 	less than 14.9
    //Underweight 	from 15 to 18.4
    //Normal 	from 18.5 to 22.9
    //Overweight 	from 23 to 27.5
    //Obese 	from 27.6 to 40
    //Morbidly Obese 	greater than 40
    //Рисуем от 45 до 10
    private final float interval = settings.getIn().getSizeFactor()*31;
    private final float [] zones     = {
                    settings.getIn().getSizeFactor()*3.0f/interval,
                    settings.getIn().getSizeFactor()*3.5f/interval,
                    settings.getIn().getSizeFactor()*4.5f/interval,
                    settings.getIn().getSizeFactor()*4.5f/interval,
                    settings.getIn().getSizeFactor()*12.5f/interval,
                    settings.getIn().getSizeFactor()*3.0f/interval};
    public final static Color  [] colors    = {new Color(0x91fff8),
                                   new Color(0x3eff98),
                                   new Color(0x0d8232),
                                   new Color(0xffd042),
                                   new Color(0xc9381f),
                                   new Color(0x822178)};
    private final String [] labels = {"Голодание",
                                "Недостаток",
                                "Норма",
                                "Излишек",
                                "Ожирение",
                                "Патология"};

    public BMILabel(){
        super();
    }
    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g.create();
        //Rectangle r = g2d.getClipBounds();
        //System.out.println(r.x+" "+r.y+" "+r.width+" "+r.height);
        //
        g2d.setFont(new Font(Font.SANS_SERIF,
                Font.BOLD,settings.getIn().getSizedValue(11)));
        Dimension d = this.getSize();
        //int lowY = d.height;
        //System.out.println("height="+lowY);
        float deltaY = 0;
        float height = (float)d.height;
        float lowY = height;
        for(int i=0;i<zones.length;i++){
            deltaY = height * zones[i];
            
            g2d.setColor(colors[i]);
            g2d.fillRect(0, Math.round(lowY - deltaY) ,
                    lwidth,Math.round(deltaY) );
            g2d.setColor(Color.BLACK);
            g2d.drawString(labels[i], lwidth+2,
                    Math.round(lowY - deltaY/2) + 5   );

            lowY -= deltaY;
        }
    }
    @Override
    public Dimension getMinimumSize(){
        return minimum;
    }
    @Override
    public Dimension getPreferredSize(){
        return minimum;
    }
}