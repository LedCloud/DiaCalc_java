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
import maths.User;
import javax.swing.event.*;
import java.text.DecimalFormat;
import java.beans.*;
import java.awt.event.*;
import maths.Sugar;
import lookout.cellroutins.PositiveFloatVerifier;
import lookout.cellroutins.MyActionListener;
import java.net.URI;
import java.util.*;

public class CalcsDialog extends JDialog implements ActionListener,
PropertyChangeListener, ChangeListener, ItemListener{
    private final static String BMI_CHILDREN_ADDR = 
            "http://www.diacalc.org/BMIchildren.html";
    private final static float CALOR_DOWN = 7716f;
    private final static float CALOR_UP = CALOR_DOWN/0.7f;
    private final static String CALOR_PANE = "calor panel";
    private final static String WARN_PANE = "warning panel";
    private final ProgramSettings settings = ProgramSettings.getInstance();
    private final User user;

    private final float [] zones     = {
                    15.0f,
                    18.5f,
                    23.0f,
                    27.5f,
                    40.0f,
                    1000.0f};

    private JProgressBar barBMI;
    private JFormattedTextField fldBMI;
    private JFormattedTextField fldWeight;
    private JFormattedTextField fldHeight;
    private JRadioButton btnMale;
    private JRadioButton btnFemale;
    private JFormattedTextField fldAge;
    private JFormattedTextField fldCalor;
    private JFormattedTextField fldCalorTopNorm;
    private JFormattedTextField fldCalorNorm;
    private JFormattedTextField fldTargetWeight;
    private JFormattedTextField fldCalorTarget;
    private JFormattedTextField fldWeightNorm;
    private JFormattedTextField fldWeightTopNorm;
    private JSlider activity;
    private JComboBox periodList;
    private JPanel cardPane;

    private final DecimalFormat df00 = new DecimalFormat("0.00");
    private final DecimalFormat df0 = new DecimalFormat("0.0");
    private final DecimalFormat df = new DecimalFormat("0");
    private final MyFocusListener mfl = new MyFocusListener();

    private boolean cancalc = true;

    private final JLabel [] lblOUV = {new JLabel(),new JLabel(),new JLabel(),new JLabel()};
    private final JLabel [] lblCarb = {new JLabel(),new JLabel(),new JLabel(),new JLabel()};
    private final float [] sugarstep = {0.1f,0.2f,0.25f,0.5f};
    private final float [] carbstep = {1f,2f,5f,10f};

    private final JTabbedPane tabbedPane;
    private final PositiveFloatVerifier pfv = new PositiveFloatVerifier(false);
    private final MyActionListener mal = new MyActionListener(false);

    public CalcsDialog(User user){
        setIconImage(new ImageIcon(CoefJob.class
                .getResource("images/CalcsIcon.png")).getImage());
        setTitle("Различные пересчеты");
        this.user = user;
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        JComponent c1= createWeightCalc();
        tabbedPane.addTab("ИМТ и калорийность",c1);
        JComponent c2 = createBloodCalc();
        tabbedPane.addTab("Пересчет крови",c2);
        
        
        add(tabbedPane,BorderLayout.CENTER);

        JButton btnClose = new JButton("Закрыть");
        btnClose.addActionListener(this);
        add(btnClose,BorderLayout.SOUTH);
        

        setModal(false);
        pack();
        
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    @Override
    public void actionPerformed(ActionEvent ev){
        setVisible(false);
    }
    private JComponent createWeightCalc(){
        JPanel pane = new JPanel(new GridBagLayout());
        ////////////////////////
        fldBMI = new JFormattedTextField(df0);
        fldBMI.setValue(22f);
        fldBMI.setEditable(false);
        fldBMI.setHorizontalAlignment(JTextField.CENTER);
        fldBMI.setFocusable(false);
        
        fldWeight = new JFormattedTextField(df0);
        fldWeight.setValue(user.getWeight());
        fldWeight.addFocusListener(mfl);
        
        fldHeight = new JFormattedTextField(df0);
        fldHeight.setValue(user.getHeight());
        fldHeight.addFocusListener(mfl);
        
        btnMale = new JRadioButton("муж");
        btnFemale = new JRadioButton("жен");
        btnMale.setSelected(user.isMale());
        btnFemale.setSelected(!user.isMale());

        ButtonGroup group = new ButtonGroup();
        group.add(btnMale);
        group.add(btnFemale);

        fldAge = new JFormattedTextField(df);
        GregorianCalendar birthday = new GregorianCalendar();
        birthday.setTime(new Date(user.getBirthday()) );
        GregorianCalendar today = new GregorianCalendar();
        int age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        if((birthday.get(Calendar.MONTH) > today.get(Calendar.MONTH))
            || (birthday.get(Calendar.MONTH) == today.get(Calendar.MONTH)
            && birthday.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH)))
        {
            age--;
        }
        fldAge.setValue( age );
        fldAge.addFocusListener(mfl);
        
        fldTargetWeight = new JFormattedTextField(df0);
        float h = ((Number)fldHeight.getValue()).floatValue()/100;
        fldTargetWeight.setValue( 20.75f * h * h );
        fldTargetWeight.addFocusListener(mfl);
        
        fldCalorTarget = new JFormattedTextField(df);
        fldCalorTarget.setEditable(false);
        fldCalorTarget.setHorizontalAlignment(JTextField.CENTER);
        fldCalorTarget.setFocusable(false);
        
        periodList = new JComboBox();
        periodList.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int sel = periodList.getSelectedIndex();
                if (sel>=0){
                    float calorneed = calcCalorByWeight(
                            ((Number)fldTargetWeight.getValue()).floatValue(),
                            ((Number)fldHeight.getValue()).floatValue(),
                            ((Number)fldAge.getValue()).floatValue(),
                            btnMale.isSelected());
                    float calorPerMonth = getCalor2Loose(((Number)fldWeight.getValue()).floatValue(),
                            ((Number)fldTargetWeight.getValue()).floatValue()) /
                            (((Number)periodList.getSelectedItem()).intValue() * 30);
                    fldCalorTarget.setValue(calorneed - calorPerMonth);
                }
            }
        });
       
        barBMI = new JProgressBar(JProgressBar.VERTICAL,
                120,430);
        barBMI.setBorder(BorderFactory.createEmptyBorder());

        BMILabel bmiL = new BMILabel();

        fldCalor = new JFormattedTextField(df);
        fldCalor.setEditable(false);
        fldCalor.setHorizontalAlignment(JTextField.CENTER);
        fldCalor.setFocusable(false);
        fldCalor.setColumns(6);
        
        fldCalorTopNorm = new JFormattedTextField(df);
        fldCalorTopNorm.setEditable(false);
        fldCalorTopNorm.setHorizontalAlignment(JTextField.CENTER);
        fldCalorTopNorm.setFocusable(false);
        fldCalorTopNorm.setColumns(6);
        
        fldCalorNorm = new JFormattedTextField(df);
        fldCalorNorm.setEditable(false);
        fldCalorNorm.setHorizontalAlignment(JTextField.CENTER);
        fldCalorNorm.setFocusable(false);
        fldCalorNorm.setColumns(6);
        
        activity = new JSlider(JSlider.VERTICAL,
                110, 177, 115);
        activity.setMajorTickSpacing(5);
        activity.setPaintTicks(true);
        activity.setFocusable(false);

        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put(110, new JLabel("Сидячая раб.") );//11
        labelTable.put(115, new JLabel("Легкая акт") );//11.5
        labelTable.put(135, new JLabel("Средняя" ));//13.5
        labelTable.put(150, new JLabel("Высокая"));//15 активная (ходячая работа)") );
        labelTable.put(177, new JLabel("Экстремальная"));//18 активность (активная работа с большим кол-м спорта") );

        activity.setLabelTable( labelTable );

        activity.setPaintLabels(true);
        activity.addChangeListener(this);


        fldWeight.setInputVerifier(pfv);
        fldHeight.setInputVerifier(pfv);
        fldAge.setInputVerifier(pfv);
        fldTargetWeight.setInputVerifier(pfv);

        fldWeightTopNorm = new JFormattedTextField(df);
        fldWeightTopNorm.setEditable(false);
        fldWeightTopNorm.setHorizontalAlignment(JTextField.CENTER);
        fldWeightTopNorm.setFocusable(false);
        
        fldWeightNorm = new JFormattedTextField(df);
        fldWeightNorm.setEditable(false);
        fldWeightNorm.setHorizontalAlignment(JTextField.CENTER);
        fldWeightNorm.setFocusable(false);
        
       //первый столбец
        pane.add(new JLabel("ИМТ"),
          new GridBagConstraints(0,0,1,1,0.1,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(fldBMI,
          new GridBagConstraints(1,0,1,1,0.15,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("Вес кг."),
          new GridBagConstraints(0,1,1,1,0.1,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(fldWeight,
          new GridBagConstraints(1,1,1,1,0.15,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("Рост см."),
          new GridBagConstraints(0,2,1,1,0.1,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(fldHeight,
          new GridBagConstraints(1,2,1,1,0.15,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("Возраст"),
          new GridBagConstraints(0,3,1,1,0.1,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(fldAge,
          new GridBagConstraints(1,3,1,1,0.15,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("Пол"),
          new GridBagConstraints(0,4,1,2,0.1,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(btnMale,
          new GridBagConstraints(1,4,1,1,0.15,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
        pane.add(btnFemale,
          new GridBagConstraints(1,5,1,1,0.15,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("Целевой вес"),
          new GridBagConstraints(0,6,1,1,0.1,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(fldTargetWeight,
          new GridBagConstraints(1,6,1,1,0.15,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("Период коррекции веса мес."),
          new GridBagConstraints(0,7,2,1,0.25,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
        pane.add(periodList,
          new GridBagConstraints(0,8,2,1,0.25,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(barBMI,
          new GridBagConstraints(2,0,1,9,0.1,1, GridBagConstraints.LINE_END,
          GridBagConstraints.VERTICAL, new Insets(2,2,2,0), 0, 0));

        pane.add(bmiL,
          new GridBagConstraints(3,0,1,9,1.4,1, GridBagConstraints.LINE_START,
          GridBagConstraints.BOTH, new Insets(2,0,2,2), 0, 0));

        pane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel calorPane = new JPanel(new GridBagLayout());

        calorPane.add(new JLabel("Активность"),
          new GridBagConstraints(0,0,1,1,0.3,1, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

        calorPane.add(activity,
          new GridBagConstraints(0,1,1,6,0.3,1, GridBagConstraints.CENTER,
          GridBagConstraints.VERTICAL, new Insets(2,2,2,2), 0, 0));

        calorPane.add(new JLabel("Верх. гр. нормы веса"),
          new GridBagConstraints(1,0,1,1,0.4,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        calorPane.add(fldWeightTopNorm,
          new GridBagConstraints(2,0,1,1,0.2,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
        
        calorPane.add(new JLabel("Вес норма"),
          new GridBagConstraints(1,1,1,1,0.4,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        calorPane.add(fldWeightNorm,
          new GridBagConstraints(2,1,1,1,0.2,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        calorPane.add(new JLabel("Норма потребления (кКал/сут)"),
          new GridBagConstraints(1,2,2,1,0.6,1, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

        calorPane.add(new JLabel("Для текущ. веса"),
          new GridBagConstraints(1,3,1,1,0.4,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        calorPane.add(fldCalor,
          new GridBagConstraints(2,3,1,1,0.2,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        calorPane.add(new JLabel("Для верх. гр. нормы"),
          new GridBagConstraints(1,4,1,1,0.4,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        calorPane.add(fldCalorTopNorm,
          new GridBagConstraints(2,4,1,1,0.2,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        calorPane.add(new JLabel("Для нормы"),
          new GridBagConstraints(1,5,1,1,0.4,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        calorPane.add(fldCalorNorm,
          new GridBagConstraints(2,5,1,1,0.2,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        calorPane.add(new JLabel("Для достижения\nцелевого веса"),
          new GridBagConstraints(1,6,1,1,0.4,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        calorPane.add(fldCalorTarget,
          new GridBagConstraints(2,6,1,1,0.2,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        calorPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel warnPane = new JPanel(new BorderLayout());
        JLabel warn = new JLabel("<html><center>При паталогических состояниях необходима<br>" +
                "очная консультация врача</center></html>");
        warn.setHorizontalAlignment(SwingConstants.CENTER);
        warnPane.add(warn);
        
        cardPane = new JPanel(new CardLayout());
        cardPane.add(calorPane,CALOR_PANE);
        cardPane.add(warnPane,WARN_PANE);

        JPanel resPane = new JPanel(new GridBagLayout());

        resPane.add(pane,
          new GridBagConstraints(0,0,1,1,0.8,1, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
        resPane.add(cardPane,
          new GridBagConstraints(1,0,1,1,0.4,1, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
        JLabel lblBMIchildren = new JLabel("<html>ИМТ рассчитанный для детей, должен быть интерпретирован особым " +
                "образом,<br>подробности по адресу <font color=blue>http://www.diacalc.org/BMIchildren.html</font>");
        lblBMIchildren.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblBMIchildren.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent ev){
                if (SwingUtilities.isLeftMouseButton(ev) ){
                    if (Desktop.isDesktopSupported()){
                        if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
                            try {
                                URI uri = new URI(BMI_CHILDREN_ADDR);
                                Desktop.getDesktop().browse(uri);
                            } catch(Exception exc){}
                        }
                    }
                }
            }
        });
        resPane.add(lblBMIchildren,
          new GridBagConstraints(0,1,2,1,1,0.05, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

        fldWeight.addPropertyChangeListener(this);
        fldHeight.addPropertyChangeListener(this);
        btnMale.addItemListener(this);
        fldAge.addPropertyChangeListener(this);
        fldTargetWeight.addPropertyChangeListener(this);

        calcCalories();
        
        return resPane;
    }
    private void setBMIScale(float v){
        Color c = Color.BLACK;
        for(int i=0;i<zones.length;i++){
            if (v<zones[i]){
                //System.out.println(v+" "+zones[i]);
                c = BMILabel.colors[i];
                break;
            }
        }
        barBMI.setForeground(c);
        barBMI.setValue( (int)(10*v) );
    }
    private void correctPeriodList(){
        int months = findMinMonths();
            //Получаем минимальное количество месяцев
            int minmonth = 0;
            int selectedmonth = -1;
            //Получаем выделенный месяц
            if (periodList.getItemCount()>0){
                minmonth = (Integer)periodList.getItemAt(0);
                selectedmonth = minmonth + periodList.getSelectedIndex();
            }
            periodList.removeAllItems();
            //если months == 0, то значит коррекция не нужна
            //А если month>24 то и список будет пустой
            if (months>0 && months<25){
                for(int i=months;i<=24;i++){
                    periodList.addItem(i);
                }

                int pos;
                if (selectedmonth<0){
                    pos = periodList.getItemCount()/2;
                }else{
                    minmonth = (Integer)periodList.getItemAt(0);
                    pos = selectedmonth - minmonth;
                    pos = pos<0 ? 0 : (pos>23 ? 23 : pos);
                }
                periodList.setSelectedIndex(pos);
            }
            else{
                fldCalorTarget.setText("===");
            }
    }
    @Override
    public void propertyChange(PropertyChangeEvent e){
        correctPeriodList();
        calcCalories();
    }

    @Override
    public void itemStateChanged(ItemEvent ev){
        correctPeriodList();
        calcCalories();
    }
    @Override
    public void stateChanged(ChangeEvent e){
        correctPeriodList();
        calcCalories();
    }
    private void calcCalories(){
        float m = ((Number)fldWeight.getValue()).floatValue();
        float h = ((Number)fldHeight.getValue()).floatValue();
        float a = ((Number)fldAge.getValue()).floatValue();
        float v = calcBMI(m,h);
        CardLayout cl = (CardLayout)cardPane.getLayout();
        if (v>15 && v<40){
            cl.show(cardPane, CALOR_PANE);
        }
        else{
            cl.show(cardPane, WARN_PANE);
        }
        fldBMI.setValue(v);
        setBMIScale(v);

        fldCalor.setValue( calcCalorByWeight(m,h,a,btnMale.isSelected()) );

        float wTop = 23f * h * h / 10000f;//вес верхней границы нормы
        fldWeightTopNorm.setValue(wTop);

        fldCalorTopNorm.setValue( calcCalorByWeight(wTop,h,a,btnMale.isSelected()) );

        float wNorm = 20.75f * h * h / 10000f;//вес середины нормы
        fldWeightNorm.setValue(wNorm);

        fldCalorNorm.setValue(  calcCalorByWeight(wNorm,h,a,btnMale.isSelected())  );

        int sel = periodList.getSelectedIndex();
                if (sel>=0){
                    int month = (Integer)periodList.getSelectedItem();
                    float target = ((Number)fldTargetWeight.getValue()).floatValue();
                    float source = ((Number)fldWeight.getValue()).floatValue();
                    float calorneed = calcCalorByWeight(
                            target,
                            ((Number)fldHeight.getValue()).floatValue(),
                            ((Number)fldAge.getValue()).floatValue(),
                            btnMale.isSelected());
                    float calorPerMonth = getCalor2Loose(source,target) / (month * 30);
                    fldCalorTarget.setValue(calorneed - calorPerMonth);
                }
    }
    private float getCalor2Loose(float wSt,float wTrg){
        float weightchange = wSt - wTrg;
        float calor2loose;
        if (weightchange>=0) calor2loose = weightchange * CALOR_DOWN;
        else calor2loose = weightchange * CALOR_UP;
        return calor2loose;
    }
    private int findMinMonths(){
       //тут ищем минимальное количество месяцев необходимое для
       //коррекции веса.
       float wTarget = ((Number)fldTargetWeight.getValue()).floatValue();
       float w = ((Number)fldWeight.getValue()).floatValue();
       float calorneed = calcCalorByWeight(
                    wTarget,
                    ((Number)fldHeight.getValue()).floatValue(),
                    ((Number)fldAge.getValue()).floatValue(),
                    btnMale.isSelected());
       if (calorneed<1200) calorneed = 1200;
       
       float calor2loose = getCalor2Loose(w,
               wTarget);
       int months;
       if (calor2loose>0){//сбрасываем вес
            if (calorneed==1200) calorneed += 1;
            months = (int)Math.ceil(calor2loose/( (calorneed - 1200)*30 ));
       }
       else{//набираем вес
           float addon = 0;
           if (calorneed<5500){
               addon = 5500 - calorneed;
               months = (int)Math.ceil(-calor2loose/(addon*30));
           }
           else{
               months = 0;
           }
        }
       return months;
    }
    private float calcBMI(float m,float h){
        if (h==0f) return 0f;
        return 10000f * m/(h*h);
    }
    private float calcCalorByWeight(float m,float h,float a,boolean male){
        float s;
        if (male) s = 5f;
        else s = -161f;
        float basal = 9.99f * m + 6.25f * h - 4.92f * a + s;
        //System.out.println("m="+m+" h="+h+" a="+a+" basal="+basal);
        return basal * (float)activity.getValue() / 100f;
    }
    ////blood
    private JFormattedTextField createField(int mode){
        JFormattedTextField fld;
        if (mode==1) fld = new JFormattedTextField(df);
        else if (mode==0) fld = new JFormattedTextField(df0);
        else fld = new JFormattedTextField(df00);
        fld.setColumns(8);
        fld.addFocusListener(mfl);
        fld.setValue(0f);
        fld.setInputVerifier(pfv);
        fld.addActionListener(mal);
        return fld;
    }
    private JComponent createBloodCalc(){
        JPanel blood = new JPanel();
        blood.setLayout(new BoxLayout(blood, BoxLayout.PAGE_AXIS));
        ///

        final JFormattedTextField fldWhMM = createField(0);
        final JFormattedTextField fldWhMD = createField(1);
        final JFormattedTextField fldPLMM= createField(0);
        final JFormattedTextField fldPLMD = createField(1);
        final JFormattedTextField fldGG = createField(0);

        JPanel pane = new JPanel(new GridBagLayout());
        pane.add(new JLabel("Цельная кровь"),
          new GridBagConstraints(0,0,1,1,0.5,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(new JLabel("Плазма кровь"),
          new GridBagConstraints(0,1,1,1,0.5,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("Цельная кровь"),
          new GridBagConstraints(0,2,1,1,0.5,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(new JLabel("Плазма кровь"),
          new GridBagConstraints(0,3,1,1,0.5,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("ГГ"),
          new GridBagConstraints(0,4,1,1,0.5,1, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));


        pane.add(fldWhMM,
          new GridBagConstraints(1,0,1,1,0.9,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
        pane.add(fldPLMM,
          new GridBagConstraints(1,1,1,1,0.9,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(fldWhMD,
          new GridBagConstraints(1,2,1,1,0.9,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
        pane.add(fldPLMD,
          new GridBagConstraints(1,3,1,1,0.9,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(fldGG,
          new GridBagConstraints(1,4,1,1,0.9,1, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("ммоль/л"),
          new GridBagConstraints(2,0,1,1,0.5,1, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(new JLabel("ммоль/л"),
          new GridBagConstraints(2,1,1,1,0.5,1, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("мг/дл (%)"),
          new GridBagConstraints(2,2,1,1,0.5,1, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
        pane.add(new JLabel("мг/дл (%)"),
          new GridBagConstraints(2,3,1,1,0.5,1, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

        pane.add(new JLabel("%"),
          new GridBagConstraints(2,4,1,1,0.5,1, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

        pane.setBorder(BorderFactory.createTitledBorder("Различные системы измерения"));

        fldWhMM.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent ev){
                if (cancalc){
                    cancalc = false;
                float v = ((Number)fldWhMM.getValue()).floatValue();
                Sugar s = new Sugar(v);
                fldPLMM.setValue(s.getSugar(Sugar.MMOL, Sugar.PLASMA));
                fldWhMD.setValue(s.getSugar(Sugar.MGDL, Sugar.WHOLE));
                fldPLMD.setValue(s.getSugar(Sugar.MGDL, Sugar.PLASMA));
                fldGG.setValue(getGGBySugar(s));
                    cancalc = true;
                }
            }
        });

        fldWhMD.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent ev){
                if (cancalc){
                    cancalc = false;
                float v = ((Number)fldWhMD.getValue()).floatValue();
                Sugar s = new Sugar();
                s.setSugar(v, Sugar.MGDL, Sugar.WHOLE);
                fldPLMM.setValue(s.getSugar(Sugar.MMOL, Sugar.PLASMA));
                fldWhMM.setValue(s.getSugar(Sugar.MMOL, Sugar.WHOLE));
                fldPLMD.setValue(s.getSugar(Sugar.MGDL, Sugar.PLASMA));
                fldGG.setValue(getGGBySugar(s));
                    cancalc = true;
                }
            }
        });

        fldPLMM.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent ev){
                if (cancalc){
                    cancalc = false;
                float v = ((Number)fldPLMM.getValue()).floatValue();
                Sugar s = new Sugar();
                s.setSugar(v, Sugar.MMOL, Sugar.PLASMA);
                fldWhMM.setValue(s.getSugar(Sugar.MMOL, Sugar.WHOLE));
                fldWhMD.setValue(s.getSugar(Sugar.MGDL, Sugar.WHOLE));
                fldPLMD.setValue(s.getSugar(Sugar.MGDL, Sugar.PLASMA));
                fldGG.setValue(getGGBySugar(s));
                    cancalc = true;
                }
            }
        });

        fldPLMD.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent ev){
                if (cancalc){
                    cancalc = false;
                float v = ((Number)fldPLMD.getValue()).floatValue();
                Sugar s = new Sugar();
                s.setSugar(v, Sugar.MGDL, Sugar.PLASMA);
                fldWhMM.setValue(s.getSugar(Sugar.MMOL, Sugar.WHOLE));
                fldWhMD.setValue(s.getSugar(Sugar.MGDL, Sugar.WHOLE));
                fldPLMM.setValue(s.getSugar(Sugar.MMOL, Sugar.PLASMA));
                fldGG.setValue(getGGBySugar(s));
                    cancalc = true;
                }
            }
        });

        fldGG.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent ev){
                if (cancalc){
                    cancalc = false;
                float v = ((Number)fldGG.getValue()).floatValue();
                Sugar s = getSugarByGG(v);
                fldWhMM.setValue(s.getSugar(Sugar.MMOL, Sugar.WHOLE));
                fldWhMD.setValue(s.getSugar(Sugar.MGDL, Sugar.WHOLE));
                fldPLMM.setValue(s.getSugar(Sugar.MMOL, Sugar.PLASMA));
                fldPLMD.setValue(s.getSugar(Sugar.MGDL, Sugar.PLASMA));
                    cancalc = true;
                }
            }
        });
        fldWhMM.setValue(user.getTargetSh().getValue());
          
        pane.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        blood.add(pane);

        pane = new JPanel(new GridBagLayout());
          

        final JFormattedTextField fldOUV = createField(2);
        final JFormattedTextField fldk1 = createField(2);

        final JLabel lblMeasureIns = new JLabel("ммоль/л цельная");
        final JLabel lblMeasureCarb = new JLabel("ммоль/л цельная");

        final JRadioButton mmol = new JRadioButton("ммоль/л");
        final JRadioButton mgdl = new JRadioButton("мг/дл (%)");
        ButtonGroup gr1 = new ButtonGroup();
        gr1.add(mmol);
        gr1.add(mgdl);
        final JRadioButton whole = new JRadioButton("цельная");
        final JRadioButton plasma = new JRadioButton("плазма");
        ButtonGroup gr2 = new ButtonGroup();
        gr2.add(whole);
        gr2.add(plasma);
        
        mmol.setSelected(user.isMmol());
        mgdl.setSelected(!user.isMmol());
        plasma.setSelected(user.isPlasma());
        whole.setSelected(!user.isPlasma());

        ItemListener lsnr = new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e){
                boolean vmmol = true;
                boolean vplasma = false;
                if (e.getItem()==mmol){
                    if (e.getStateChange()==ItemEvent.SELECTED){
                        vmmol = false;
                    }else{
                        vmmol = true;
                    }
                    vplasma = plasma.isSelected();
                }else if (e.getItem()==plasma){
                    if (e.getStateChange()==ItemEvent.SELECTED){
                        vplasma = false;
                    }else{
                        vplasma = true;
                    }
                    vmmol = mmol.isSelected();
                }

                Sugar s = new Sugar();
                s.setSugar(((Number)fldOUV.getValue()).floatValue(),
                        vmmol,
                        vplasma);
                fldOUV.setValue(s.getSugar(mmol.isSelected(),
                        plasma.isSelected()));
                String st;
                if (mmol.isSelected()) st = "ммоль/л";
                else st = "мг/дл (%)";
                if (plasma.isSelected()) st += " плазма";
                else st += " цельная";
                lblMeasureIns.setText(st);
                lblMeasureCarb.setText(st);
            }
        };
        mmol.addItemListener(lsnr);
        plasma.addItemListener(lsnr);
        fldOUV.setValue(new Sugar(user.getFactors().getK3())
                .getSugar(user.isMmol(), user.isPlasma()));

        fldk1.setValue(user.getFactors().getK1(user.isDirect()));

          pane.add(addBorder(new JLabel("ЦЕИ")),
            new GridBagConstraints(0,0,1,2,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));

          pane.add(fldOUV,
            new GridBagConstraints(1,0,1,2,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));

          pane.add(mmol,
            new GridBagConstraints(2,0,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(mgdl,
            new GridBagConstraints(3,0,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(whole,
            new GridBagConstraints(2,1,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(plasma,
            new GridBagConstraints(3,1,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));

          pane.add(addBorder(new JLabel("k1")),
            new GridBagConstraints(4,0,1,2,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));

          pane.add(fldk1,
            new GridBagConstraints(5,0,1,2,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));

          ////////////////////////////////////////
          pane.add(addBorder(new JLabel("инс. ед.")),
            new GridBagConstraints(0,2,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(10,4,2,4), 0, 0));
          pane.add(addBorder(lblMeasureIns),
            new GridBagConstraints(1,2,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(10,4,2,4), 0, 0));
          
          
          pane.add(addBorder(new JLabel("гр. угл.")),
            new GridBagConstraints(2,2,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(10,4,2,4), 0, 0));
          pane.add(addBorder(lblMeasureCarb),
            new GridBagConstraints(3,2,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(10,4,2,4), 0, 0));
          
          pane.add(addBorder(new JLabel("0,1")),
            new GridBagConstraints(0,3,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(lblOUV[0]),
            new GridBagConstraints(1,3,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(new JLabel("1")),
            new GridBagConstraints(2,3,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(lblCarb[0]),
            new GridBagConstraints(3,3,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));

          
          pane.add(addBorder(new JLabel("0,2")),
            new GridBagConstraints(0,4,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(lblOUV[1]),
            new GridBagConstraints(1,4,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(new JLabel("2")),
            new GridBagConstraints(2,4,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(lblCarb[1]),
            new GridBagConstraints(3,4,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          
          pane.add(addBorder(new JLabel("0,25")),
            new GridBagConstraints(0,5,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(lblOUV[2]),
            new GridBagConstraints(1,5,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(new JLabel("5")),
            new GridBagConstraints(2,5,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(lblCarb[2]),
            new GridBagConstraints(3,5,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          
          pane.add(addBorder(new JLabel("0,5")),
            new GridBagConstraints(0,6,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(lblOUV[3]),
            new GridBagConstraints(1,6,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(new JLabel("10")),
            new GridBagConstraints(2,6,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          pane.add(addBorder(lblCarb[3]),
            new GridBagConstraints(3,6,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2,4,2,4), 0, 0));
          
         pane.setBorder(BorderFactory.createTitledBorder("Влияние на гликемию"));

          PropertyChangeListener proplsn = new PropertyChangeListener(){
             @Override
            public void propertyChange(PropertyChangeEvent ev){
                     float v = ((Number)fldOUV.getValue()).floatValue();
                     Sugar s = new Sugar();
                     s.setSugar(v, mmol.isSelected(), plasma.isSelected());
                     for(int i=0;i<sugarstep.length;i++){
                        lblOUV[i].setText(
                           
                            df00.format(s.getSugar(mmol.isSelected(), 
                                    plasma.isSelected())*sugarstep[i]));
                     }

                v = ((Number)fldk1.getValue()).floatValue();
                float gr;
                if (user.isDirect()){
                    gr = v;
                }else{
                    gr = v==0?0:user.getFactors().getBE(user.isDirect())/v;
                }
                float ouv = ((Number)fldOUV.getValue()).floatValue();
                float onegramm = gr==0?0:ouv/gr;
                //System.out.println(onegramm);
                for(int i=0;i<carbstep.length;i++){
                        lblCarb[i].setText(
                           df00.format(onegramm*carbstep[i]));
                }
            }
          };
          
         fldOUV.addPropertyChangeListener(proplsn);

         fldk1.addPropertyChangeListener(proplsn);
         
         pane.setAlignmentX(JComponent.LEFT_ALIGNMENT);
         blood.add(pane);
        
        return blood;
    }
    
    private JComponent addBorder(JComponent c){
        if (c instanceof JLabel) ((JLabel)c).setHorizontalAlignment(JLabel.CENTER);
        return c;
    }
    private Sugar getSugarByGG(float gg){
        //Conversion: HbA1c = 2 + (Whole Blood Glucose / 1.67);
        //Whole Blood Glucose = (HbA1c - 2) * 1.67
        //33*gg - 80
        return new Sugar((gg - 2f) * 1.67f);
    }

    private float getGGBySugar(Sugar s){
        return 2f + (s.getValue()/1.67f);
    }
    public void selectTab(int tab){
       tabbedPane.setSelectedIndex(tab-1);
    }
}

