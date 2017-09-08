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
import javax.swing.border.BevelBorder;
import maths.User;
import java.text.DecimalFormat;
import lookout.settings.ProgramSettings;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import maths.Dose;

public class CaloriesWindow  extends JWindow
        implements PropertyChangeListener{
     //private final static int WIDTH_D = 230;
     //private final static int HEIGHT_D = 180;

     private final JLabel lblEaten;
     private final JLabel lblLimit;
     private final JLabel lblLeft;
     private final JLabel lblP;
     private final JLabel lblF;
     private final JLabel lblC;
     private final JLabel lblPp;
     private final JLabel lblFp;
     private final JLabel lblCp;
     private final JLabel lblLeftText;
     private final JLabel lblDate;

     private final ProgramSettings settings;

  public CaloriesWindow(){
   settings = ProgramSettings.getInstance();
   JPanel pane = new JPanel(new GridBagLayout());
   pane.add(new JLabel("Съедено:"),
          new GridBagConstraints(0,0,2,1,0.5,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
   lblEaten = new JLabel();
   lblEaten.setBorder(BorderFactory.createEtchedBorder());
   lblEaten.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblEaten,
          new GridBagConstraints(2,0,2,1,0.5,1, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,2,2,5), 0, 0));

   pane.add(new JLabel("Лимит:"),
          new GridBagConstraints(0,1,2,1,0.5,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,5,2,2), 0, 0));
   lblLimit = new JLabel();
   lblLimit.setBorder(BorderFactory.createEtchedBorder());
   lblLimit.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblLimit,
          new GridBagConstraints(2,1,2,1,0.5,1, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(2,5,2,5), 0, 0));
   lblLeftText = new JLabel("Осталось:");
   pane.add(lblLeftText,
          new GridBagConstraints(0,2,2,1,0.5,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,5,2,2), 0, 0));
   lblLeft = new JLabel();
   lblLeft.setBorder(BorderFactory.createEtchedBorder());
   lblLeft.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblLeft,
          new GridBagConstraints(2,2,2,1,0.5,1, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,5), 0, 0));
   pane.add(new JLabel("Б"),
          new GridBagConstraints(1,3,1,1,0.5,1, GridBagConstraints.PAGE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
   pane.add(new JLabel("Ж"),
          new GridBagConstraints(2,3,1,1,0.5,1, GridBagConstraints.PAGE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
   pane.add(new JLabel("У"),
          new GridBagConstraints(3,3,1,1,0.5,1, GridBagConstraints.PAGE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
   pane.add(new JLabel("Всего:"),
          new GridBagConstraints(0,4,1,1,0.5,1, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(2,5,2,2), 0, 0));
   lblP = new JLabel();
   lblP.setBorder(BorderFactory.createEtchedBorder());
   lblP.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblP,
          new GridBagConstraints(1,4,1,1,0.5,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
   lblF = new JLabel();
   lblF.setBorder(BorderFactory.createEtchedBorder());
   lblF.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblF,
          new GridBagConstraints(2,4,1,1,0.5,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
   lblC = new JLabel();
   lblC.setBorder(BorderFactory.createEtchedBorder());
   lblC.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblC,
          new GridBagConstraints(3,4,1,1,0.5,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,5), 0, 0));
   ///////////////////////////////////////
   pane.add(new JLabel("100% ="),
          new GridBagConstraints(0,5,1,1,0.3,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,5,2,2), 0, 0));
   lblPp = new JLabel();
   lblPp.setBorder(BorderFactory.createEtchedBorder());
   lblPp.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblPp,
          new GridBagConstraints(1,5,1,1,0.23,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
   lblFp = new JLabel();
   lblFp.setBorder(BorderFactory.createEtchedBorder());
   lblFp.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblFp,
          new GridBagConstraints(2,5,1,1,0.23,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
   lblCp = new JLabel();
   lblCp.setBorder(BorderFactory.createEtchedBorder());
   lblCp.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblCp,
          new GridBagConstraints(3,5,1,1,0.23,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,5), 0, 0));
   lblDate = new JLabel();
   lblDate.setHorizontalAlignment(SwingConstants.CENTER);
   pane.add(lblDate,
          new GridBagConstraints(0,6,4,1,0.5,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,5,5,5), 0, 0));
   add(pane);
   pane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
   setAlwaysOnTop(true);
  }
  @Override
  public void propertyChange(PropertyChangeEvent e) {
    String prop_name = e.getPropertyName();
    if (MainFrame.USER_CHANGED.equals(prop_name)){
        setData((User)e.getNewValue());
    }
  }
  public void setData(User user){
      DecimalFormat f1 = new DecimalFormat("0");
      DecimalFormat f2 = new DecimalFormat("0.0");
      lblEaten.setText(f1.format(user.getEatenFood().getCalories()));
      lblLimit.setText(f1.format(user.getCalorLimit()));
      if (user.getEatenFood().getCalories()>user.getCalorLimit()){
            lblLeftText.setText("Превышено на:");
            lblLeft.setText(f1
                    .format(user.getEatenFood().getCalories()-user.getCalorLimit()));
      }else{
          lblLeftText.setText("Осталось:");
          lblLeft.setText(f1
                    .format(user.getCalorLimit() - user.getEatenFood().getCalories()));
      }
      lblP.setText(f2.format(user.getEatenFood().getAllProt()));
      lblF.setText(f2.format(user.getEatenFood().getAllFat()));
      lblC.setText(f2.format(user.getEatenFood().getAllCarb()));
      //float all = user.getEatenFood().getAllProt() + user.getEatenFood().getAllFat() +
        //      user.getEatenFood().getAllCarb();
      
      if (user.getEatenFood().getCalories()>0){
          lblPp.setText(f1.format(100*user.getEatenFood().getAllProt()*
                  Dose.PROT/user.getEatenFood().getCalories())+"%");
          lblFp.setText(f1.format(100*user.getEatenFood().getAllFat()*
                  Dose.FAT/user.getEatenFood().getCalories())+"%");
          lblCp.setText(f1.format(100*user.getEatenFood().getAllCarb()*
                  Dose.CARB/user.getEatenFood().getCalories())+"%");
          
          SimpleDateFormat f3 = new SimpleDateFormat("HH:mm dd.MM.yyyy");
          lblDate.setText(f3.format(new Date(user.getEatenTime())));
      }
      else {
          lblPp.setText("0%");
          lblFp.setText("0%");
          lblCp.setText("0%");
          lblDate.setText("===========");
      }
  }
 }