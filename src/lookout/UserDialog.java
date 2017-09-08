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

package lookout;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */


import lookout.cellroutins.IntVerifier;
import lookout.cellroutins.PositiveFloatVerifier;
import lookout.cellroutins.SpaceVerifier;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import maths.User;
import lookout.settings.ProgramSettings;
import javax.swing.*;
import maths.Sugar;
import lookout.datepicker.DateField;
import java.util.Date;

public class UserDialog extends JDialog implements 
ActionListener, ItemListener {
    private JButton buttonOk;
    private JButton buttonCancel;
    private static final String OK_PRESSED = "Ok_Pressed";
    private static final String CANCEL_PRESSED = "Cancel_Pressed";
    private boolean result = false;
    
    private JLabel lblName;
    private JLabel lblWeight;
    private JLabel lblHeight;
    private JLabel lblSex;
    private JLabel lblCalor;
    private JLabel lblTargetSh;
    private JLabel lblBlood;
    private JLabel lblLoSh;
    private JLabel lblHiSh;
    
    private JTextField fldName;
    private JFormattedTextField fldWeight;
    private JFormattedTextField fldHeight;
    private JFormattedTextField fldCalor;
    private JFormattedTextField fldTargetSh;
    
    private JRadioButton male;
    private JRadioButton female;
    private ButtonGroup sex;
    
    private JRadioButton wholeBlood;
    private JRadioButton plasmaBlood;
    private ButtonGroup blood;
    
    private ProgramSettings settings;
    private User user;

    private JRadioButton mmol;
    private JRadioButton mgdl;

    private DateField birthday;
    private JLabel lblBirthDay;

    private JFormattedTextField fldLoSh;
    private JFormattedTextField fldHiSh;
    
    public UserDialog(JFrame owner, User user){
        super(owner);
        
        settings = ProgramSettings.getInstance();
        
        
        if (user==null) this.setTitle("Создание пользователя");
        else setTitle("Редактирование пользователя");

        buttonOk = new JButton("Ok");
        if (settings.getIn().getSize()==4) buttonOk.setPreferredSize(new Dimension(165,30));
        else buttonOk.setPreferredSize(new Dimension(150,25));
        buttonOk.setActionCommand(OK_PRESSED);
        buttonOk.addActionListener(this);
        buttonOk.setDefaultCapable(true);
        
        buttonCancel = new JButton("Cancel");
        if (settings.getIn().getSize()==4) buttonCancel.setPreferredSize(new Dimension(165,30));
        else buttonCancel.setPreferredSize(new Dimension(150,25));
        buttonCancel.setActionCommand(CANCEL_PRESSED);
        buttonCancel.addActionListener(this);
        
        lblName = new JLabel("Имя пользователя");
        lblWeight = new JLabel("Вес (кг.)");
        lblHeight = new JLabel("Рост (см.)");
        lblSex = new JLabel("Пол");
        lblCalor = new JLabel("Лимит калорий");
        lblBlood = new JLabel("Как измеряется кровь");
        lblTargetSh = new JLabel("Целевой уровень сахаров");
        lblBirthDay = new JLabel("Дата рождения");
        lblLoSh = new JLabel("Нижний уровень сахаров");
        lblHiSh = new JLabel("Верхний уровень сахаров");
        
        MyFocusListener fcLst = new MyFocusListener();
        DecimalFormat frt =  new DecimalFormat("#0.0");
        
        fldName = new JTextField();
        //fldName.setPreferredSize(new Dimension(70,0));
        fldName.addFocusListener(fcLst);
        fldName.setInputVerifier(new SpaceVerifier());
        
        fldWeight = new JFormattedTextField(frt);
        fldWeight.addFocusListener(fcLst);
        fldWeight.setInputVerifier(new PositiveFloatVerifier(false));
        
        
        fldHeight = new JFormattedTextField(frt);
        fldHeight.addFocusListener(fcLst);
        fldHeight.setInputVerifier(new PositiveFloatVerifier(false));
        
        
        fldCalor = new JFormattedTextField(new DecimalFormat("0"));
        fldCalor.addFocusListener(fcLst);
        fldCalor.setInputVerifier(new IntVerifier(0,null));
        
        fldTargetSh = new JFormattedTextField(new DecimalFormat("0.0"));
        fldTargetSh.addFocusListener(fcLst);
        fldTargetSh.setInputVerifier(new PositiveFloatVerifier(false));

        fldLoSh = new JFormattedTextField(new DecimalFormat("0.0"));
        fldLoSh.addFocusListener(fcLst);
        fldLoSh.setInputVerifier(new PositiveFloatVerifier(false));

        fldHiSh = new JFormattedTextField(new DecimalFormat("0.0"));
        fldHiSh.addFocusListener(fcLst);
        fldHiSh.setInputVerifier(new PositiveFloatVerifier(false));
        
        male = new JRadioButton("Муж");
        female = new JRadioButton("Жен");
        sex = new ButtonGroup();
        sex.add(male);
        sex.add(female);
        
        wholeBlood = new JRadioButton("Цельная");
        plasmaBlood = new JRadioButton("Плазма");
        blood = new ButtonGroup();
        blood.add(wholeBlood);
        blood.add(plasmaBlood);

        
        mmol =new JRadioButton("ммоль/л");
        
        mgdl =new JRadioButton("мг/дл (%)");
        ButtonGroup gluc = new ButtonGroup();
        gluc.add(mmol);
        gluc.add(mgdl);

        if (user==null){
            this.user = new User();
        }else{//Создаем копию
            this.user = user;
        }
        
        birthday = new DateField(new Date(this.user.getBirthday()));

        fldName.setText(this.user.getName());
        fldWeight.setValue(this.user.getWeight());
        fldHeight.setValue(this.user.getHeight());
        fldCalor.setValue(this.user.getCalorLimit());
        if (this.user.isMale()) male.setSelected(true);
        else female.setSelected(true);

        if (this.user.isPlasma()) plasmaBlood.setSelected(true);
        else wholeBlood.setSelected(true);

        fldTargetSh.setValue(this.user.getTargetSh().getSugar(this.user.isMmol(),
                this.user.isPlasma()));

        fldLoSh.setValue(this.user.getLowSh().getSugar(this.user.isMmol(),
                this.user.isPlasma()));

        fldHiSh.setValue(this.user.getHiSh().getSugar(this.user.isMmol(),
                this.user.isPlasma()));

        if (this.user.isMmol()) mmol.setSelected(true);
        else mgdl.setSelected(true);
        
        mmol.addItemListener(this);
        plasmaBlood.addItemListener(this);
        
        setLayout(new GridBagLayout());
        add(lblName,
          new GridBagConstraints(0,0,1,1,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(fldName,
          new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        add(lblWeight,
          new GridBagConstraints(0,1,1,1,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(fldWeight,
          new GridBagConstraints(1,1,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,2), 0, 0));
        
        add(lblHeight,
          new GridBagConstraints(0,2,1,1,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(fldHeight,
          new GridBagConstraints(1,2,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        add(lblSex,
          new GridBagConstraints(0,3,1,2,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(male,
          new GridBagConstraints(1,3,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        add(female,
          new GridBagConstraints(1,4,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        add(lblBirthDay,
          new GridBagConstraints(0,5,1,1,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(birthday,
          new GridBagConstraints(1,5,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));


        add(lblCalor,
          new GridBagConstraints(0,6,1,1,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(fldCalor,
          new GridBagConstraints(1,6,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        add(lblBlood,
          new GridBagConstraints(0,7,1,4,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(wholeBlood,
          new GridBagConstraints(1,7,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        add(plasmaBlood,
          new GridBagConstraints(1,8,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        add(mmol,
          new GridBagConstraints(1,9,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        add(mgdl,
          new GridBagConstraints(1,10,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        add(lblTargetSh,
          new GridBagConstraints(0,11,1,1,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(fldTargetSh,
          new GridBagConstraints(1,11,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        add(lblLoSh,
          new GridBagConstraints(0,12,1,1,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(fldLoSh,
          new GridBagConstraints(1,12,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        add(lblHiSh,
          new GridBagConstraints(0,13,1,1,1,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
        add(fldHiSh,
          new GridBagConstraints(1,13,1,1,1,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        add(buttonOk,
          new GridBagConstraints(0,14,1,1,1,0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 10, 0));
        add(buttonCancel,
          new GridBagConstraints(1,14,1,1,1,0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

        //setSize(WIDTH_D,HEIGHT_D);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //Выход по нажатию Escape
        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    result = false;
                    setVisible(false);
                }
                }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        
    }
    @Override
    public void actionPerformed(ActionEvent e){
       if (e.getSource() instanceof JButton){
        if (e.getActionCommand().equals(OK_PRESSED)){
           result = true; 
        }else if (e.getActionCommand().equals(CANCEL_PRESSED)){
           result = false;
        }
        this.setVisible(false);
       }
    }
    
    

    public boolean getResult(){
         return result;
    }

    public User getUser(){
      user.setName(fldName.getText());
      user.setWeight(((Number)fldWeight.getValue()).floatValue());
      user.setHeight(((Number)fldHeight.getValue()).floatValue());
      user.setCalorLimit( ((Number)fldCalor.getValue()).intValue() );
      
      user.setMale(male.isSelected());

      user.setPlasma(plasmaBlood.isSelected());
      user.setMmol(mmol.isSelected());

      user.getTargetSh().setSugar(((Number)fldTargetSh.getValue()).floatValue(),
              user.isMmol(),user.isPlasma());

      user.getLowSh().setSugar(((Number)fldLoSh.getValue()).floatValue(),
              user.isMmol(),user.isPlasma());

      user.getHiSh().setSugar(((Number)fldHiSh.getValue()).floatValue(),
              user.isMmol(),user.isPlasma());

      user.setBirthday(birthday.getDate().getTime());
      
      return user;
    }

    @Override
    public void itemStateChanged(ItemEvent ev){
        if (ev.getSource()==mmol){
            if (ev.getStateChange()==ItemEvent.SELECTED){//Выбрали ммоль
               Sugar s = new Sugar();
                s.setSugar(((Number)fldTargetSh.getValue()).floatValue(),
                    Sugar.MGDL, plasmaBlood.isSelected());
                fldTargetSh.setValue(s.getSugar(Sugar.MMOL, plasmaBlood.isSelected()));
                s.setSugar(((Number)fldLoSh.getValue()).floatValue(),
                    Sugar.MGDL, plasmaBlood.isSelected());
                fldLoSh.setValue(s.getSugar(Sugar.MMOL, plasmaBlood.isSelected()));
                s.setSugar(((Number)fldHiSh.getValue()).floatValue(),
                    Sugar.MGDL, plasmaBlood.isSelected());
                fldHiSh.setValue(s.getSugar(Sugar.MMOL, plasmaBlood.isSelected()));
            }
            else if (ev.getStateChange()==ItemEvent.DESELECTED){//Выбрали мг/дл
                Sugar s = new Sugar();
                s.setSugar(((Number)fldTargetSh.getValue()).floatValue(),
                    Sugar.MMOL, plasmaBlood.isSelected());
                fldTargetSh.setValue(s.getSugar(Sugar.MGDL, plasmaBlood.isSelected()));
                s.setSugar(((Number)fldLoSh.getValue()).floatValue(),
                    Sugar.MMOL, plasmaBlood.isSelected());
                fldLoSh.setValue(s.getSugar(Sugar.MGDL, plasmaBlood.isSelected()));
                s.setSugar(((Number)fldHiSh.getValue()).floatValue(),
                    Sugar.MMOL, plasmaBlood.isSelected());
                fldHiSh.setValue(s.getSugar(Sugar.MGDL, plasmaBlood.isSelected()));
        }
        }else{//plasma
            if (ev.getStateChange()==ItemEvent.SELECTED){//Выбрали ммоль
                Sugar s = new Sugar();
                s.setSugar(((Number)fldTargetSh.getValue()).floatValue(),
                    mmol.isSelected(), Sugar.WHOLE);
                fldTargetSh.setValue(s.getSugar(mmol.isSelected(), Sugar.PLASMA));
                s.setSugar(((Number)fldLoSh.getValue()).floatValue(),
                    mmol.isSelected(), Sugar.WHOLE);
                fldLoSh.setValue(s.getSugar(mmol.isSelected(), Sugar.PLASMA));
                s.setSugar(((Number)fldHiSh.getValue()).floatValue(),
                    mmol.isSelected(), Sugar.WHOLE);
                fldHiSh.setValue(s.getSugar(mmol.isSelected(), Sugar.PLASMA));
            }
            else if (ev.getStateChange()==ItemEvent.DESELECTED){//Выбрали мг/дл
                Sugar s = new Sugar();
                s.setSugar(((Number)fldTargetSh.getValue()).floatValue(),
                    mmol.isSelected(), Sugar.PLASMA);
                fldTargetSh.setValue(s.getSugar(mmol.isSelected(), Sugar.WHOLE));
                s.setSugar(((Number)fldLoSh.getValue()).floatValue(),
                    mmol.isSelected(), Sugar.PLASMA);
                fldLoSh.setValue(s.getSugar(mmol.isSelected(), Sugar.WHOLE));
                s.setSugar(((Number)fldHiSh.getValue()).floatValue(),
                    mmol.isSelected(), Sugar.PLASMA);
                fldHiSh.setValue(s.getSugar(mmol.isSelected(), Sugar.WHOLE));
            }
        }
    }
}


