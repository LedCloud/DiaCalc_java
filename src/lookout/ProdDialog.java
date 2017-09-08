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
import lookout.cellroutins.IntVerifier;
import lookout.cellroutins.PositiveFloatVerifier;
import lookout.cellroutins.SpaceVerifier;
import lookout.cellroutins.MyActionListener;
import lookout.settings.ProgramSettings;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Vector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import products.ProductInBase;
import java.awt.event.KeyEvent;
import products.ProdGroup;
import javax.swing.*;



public class ProdDialog extends JDialog implements PropertyChangeListener,
ActionListener {
    public static final int NEW_PRODUCT = 1;
    public static final int EDIT_PRODUCT = 2;
    public static final int NEW_COMPLEX = 3;
    public static final int EDIT_COMPLEX = 4;

    private static final String [] titles = {
                    "Новый продукт",
                    "Редактирование продукта",
                    "Новый сложный",
                    "Редактирование сложного"};
    private final ProductInBase prod;
    private final JLabel lblName;
    private final JTextField fldName;
    private final JLabel lblWeight;
    private final JLabel lblWeightMeasure;
    private final JFormattedTextField fldWeight;
    private final JLabel lblProt;
    private final JFormattedTextField fldProt;
    private final JLabel lblFat;
    private final JFormattedTextField fldFat;
    private final JLabel lblCarb;
    private final JFormattedTextField fldCarb;
    private final JLabel lblGi;
    private final JFormattedTextField fldGi;
    private final JButton buttonOk;
    private final JButton buttonCancel;
    
    private final JComboBox groupsCombo;
    
    private final JLabel percentProt;
    private final JLabel percentFat;
    private final JLabel percentCarb;
    private final JLabel percentProtTitle;
    private final JLabel percentFatTitle;
    private final JLabel percentCarbTitle;
    private final DecimalFormat frt;
    private boolean start = false;
    private boolean result = false;
    
    private static final String OK_PRESSED = "Ok_Pressed";
    private static final String CANCEL_PRESSED = "Cancel_Pressed";
    
    private final ProgramSettings settings;
    private float prot100 = 0;
    private float fat100 = 0;
    private float carb100 = 0;
    
    public ProdDialog(MainFrame owner,ProductInBase prod, Collection groups,
            int selection,int mode){
        super(owner);
        settings =  ProgramSettings.getInstance();
        frt =  new DecimalFormat("0.0");
        MyFocusListener fcLst = new MyFocusListener();
                
        lblName = new JLabel("Наименование");
        fldName = new JTextField(10);
        fldName.addFocusListener(fcLst);
        fldName.setInputVerifier(new SpaceVerifier());
        
        lblWeight = new JLabel("Вес");
        lblWeightMeasure = new JLabel("г.");
        
        PositiveFloatVerifier fv = new PositiveFloatVerifier(true);
        MyActionListener mal = new MyActionListener(true);
        fldWeight = new JFormattedTextField(frt);
        fldWeight.setPreferredSize(new Dimension(70,0));
        fldWeight.addPropertyChangeListener("value", this);
        fldWeight.addFocusListener(fcLst);
        fldWeight.setInputVerifier(fv);
        fldWeight.addActionListener(mal);
        
        
        lblProt = new JLabel("Б");
        fv = new PositiveFloatVerifier(false);
        mal = new MyActionListener(false);
        fldProt = new JFormattedTextField(frt);
        fldProt.addPropertyChangeListener("value", this);
        fldProt.addFocusListener(fcLst);
        fldProt.setInputVerifier(fv);
        fldProt.addActionListener(mal);


        lblFat = new JLabel("Ж");
        fldFat = new JFormattedTextField(frt);
        fldFat.addPropertyChangeListener("value", this);
        fldFat.addFocusListener(fcLst);
        fldFat.setInputVerifier(fv);
        fldFat.addActionListener(mal);
        
        lblCarb = new JLabel("У");
        fldCarb = new JFormattedTextField(frt);
        fldCarb.addPropertyChangeListener("value", this);
        fldCarb.addFocusListener(fcLst);
        fldCarb.setInputVerifier(fv);
        fldCarb.addActionListener(mal);
        
        lblGi = new JLabel("ГИ");
        fldGi = new JFormattedTextField(new DecimalFormat("0"));
        fldGi.addFocusListener(fcLst);
        fldGi.setInputVerifier(new IntVerifier(0,100));
        
                
        groupsCombo = new JComboBox(new Vector(groups));
        groupsCombo.setSelectedIndex(selection-settings.getIn().getUseUsageGroup());
        
        buttonOk = new JButton("Ok");
        buttonOk.setPreferredSize(new Dimension(150,25));
        buttonOk.setActionCommand(OK_PRESSED);
        buttonOk.addActionListener(this);
        buttonOk.setDefaultCapable(true);
        
        buttonCancel = new JButton("Cancel");
        
        buttonCancel.setPreferredSize(new Dimension(150,25));
        buttonCancel.setActionCommand(CANCEL_PRESSED);
        buttonCancel.addActionListener(this);
        
        if (mode>0 && mode<5) setTitle(titles[mode-1]);
        else setTitle("Редактирование продукта");
        if (prod!=null){//Значит редактирование продукта
            this.prod = new ProductInBase(prod);//создаем копию, чтобы после изменения
            //обязательно внести изменения в базу и модель
            if (prod.isComplex()){
                fldProt.setEditable(false);
                fldFat.setEditable(false);
                fldCarb.setEditable(false);
            }
        }
        else{//Создаем новый продукт
            this.prod = new ProductInBase();
            this.prod.setName("Новый продукт");
            this.prod.setWeight(100f);
            this.prod.setGi(50);
            //setTitle("Создание нового продукта");
        }
        
        fldName.setText(this.prod.getName());
        fldWeight.setValue(this.prod.getWeight());
        fldProt.setValue(this.prod.getAllProt());
        fldFat.setValue(this.prod.getAllFat());
        fldCarb.setValue(this.prod.getAllCarb());
        fldGi.setValue(this.prod.getGi());
        
        fldName.selectAll();
        fldWeight.selectAll();
        
        percentProt = new JLabel("==");
        percentProt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        percentProt.setHorizontalAlignment(SwingConstants.CENTER);
        percentProtTitle = new JLabel("Б");
        percentProtTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        
        percentFat = new JLabel("==");
        percentFat.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        percentFat.setHorizontalAlignment(SwingConstants.CENTER);
        percentFatTitle = new JLabel("Ж");
        percentFatTitle.setHorizontalAlignment(SwingConstants.RIGHT);

        percentCarb = new JLabel("==");
        percentCarb.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        percentCarb.setHorizontalAlignment(SwingConstants.CENTER);
        percentCarbTitle = new JLabel("У");
        percentCarbTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel lbl100 = new JLabel("В пересчете на 100 гр.");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        getContentPane().setLayout(layout);
        
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup()
            .addComponent(lblName)
            .addComponent(fldName)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblWeight,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
          GroupLayout.PREFERRED_SIZE)
                .addComponent(fldWeight,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
          GroupLayout.PREFERRED_SIZE)
                .addComponent(lblWeightMeasure,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
          GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(lblProt)
                    .addComponent(fldProt))
                .addGroup(layout.createParallelGroup()
                    .addComponent(lblFat)
                    .addComponent(fldFat))
                .addGroup(layout.createParallelGroup()
                    .addComponent(lblCarb)
                    .addComponent(fldCarb))
                .addGroup(layout.createParallelGroup()
                    .addComponent(lblGi)
                    .addComponent(fldGi)))
            .addComponent(groupsCombo)
            .addComponent(lbl100)
            .addGroup(layout.createSequentialGroup()

                .addComponent(percentProtTitle)
                .addComponent(percentProt, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(percentFatTitle)
                .addComponent(percentFat, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(percentCarbTitle)
                .addComponent(percentCarb, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(buttonOk, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(100)
                .addComponent(buttonCancel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        
        layout.setVerticalGroup(
             layout.createSequentialGroup()
             .addComponent(lblName)
             .addComponent(fldName)
             .addGroup(layout.createParallelGroup()
                .addComponent(lblWeight)
                .addComponent(fldWeight)
                .addComponent(lblWeightMeasure))
             .addGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblProt)
                    .addComponent(fldProt))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblFat)
                    .addComponent(fldFat))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblCarb)
                    .addComponent(fldCarb))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(lblGi)
                    .addComponent(fldGi)))
             .addComponent(groupsCombo)
             .addComponent(lbl100)
             .addGroup(layout.createParallelGroup()
                 .addComponent(percentProtTitle)
                .addComponent(percentProt)
                .addComponent(percentFatTitle)
                .addComponent(percentFat)
                .addComponent(percentCarbTitle)
                .addComponent(percentCarb))
             .addGroup(layout.createParallelGroup()
                .addComponent(buttonOk)
                .addGap(25)
                .addComponent(buttonCancel))
             );
                
        
        
        // Устанавливаем поведение формы при закрытии
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        start = true;
        calcPercent();
        //Выход по Esc
        this.getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    result = false;
                    setVisible(false);
                }
                }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        pack();
        this.setResizable(false);
        this.setLocationRelativeTo(owner);
    }
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        calcPercent();
        //System.out.println("property changed");
    }
     
   public void calcPercent(){
       if (start){
           float w = ((Number)fldWeight.getValue()).floatValue();
           if (w!=0){
               prot100 = 100f * ((Number)fldProt.getValue()).floatValue()/w;
               fat100 = 100f * ((Number)fldFat.getValue()).floatValue()/w;
               carb100 = 100f * ((Number)fldCarb.getValue()).floatValue()/w;
           }
           else{
               prot100 = 0;
               fat100 = 0;
               carb100 = 0;
           }
           percentProt.setText(frt.format(prot100));
           percentFat.setText(frt.format(fat100));
           percentCarb.setText(frt.format(carb100));
       
       }
   }
   @Override
   public void actionPerformed(ActionEvent e){
       if (e.getActionCommand().equals(OK_PRESSED)){
           if (fldName.getText().trim().length()<2) return;
           if ((prot100+fat100+carb100)>100f){
               JOptionPane.showMessageDialog(this, 
                       "Ошибка ввода\n" +
                       "проверьте верность харктеристик\n" +
                       "и веса\n\nСумма БЖУ в пересчете на 100 гр.\n" +
                       "не может быть более 100", "Ошибка",
                       JOptionPane.ERROR_MESSAGE);
               int f = findMax(prot100,fat100,carb100);
               switch (f){
                   case 1: fldProt.requestFocusInWindow(); break;
                   case 2: fldFat.requestFocusInWindow(); break;
                   case 3: fldCarb.requestFocusInWindow(); break;
                   default: fldWeight.requestFocusInWindow();
               }
               return;
           }
           result = true; 
       }else if (e.getActionCommand().equals(CANCEL_PRESSED)){
           result = false;
       }
       this.setVisible(false);
   }
   
   public boolean getResult(){
         return result;
   }
    private int findMax(float v1,float v2,float v3){
        if (v1>=v2 && v1>=v3) return 1;
        if (v2>=v1 && v2>=v3) return 2;
        return 3;
    }
   public ProductInBase getProduct(){
       prod.setName(fldName.getText());
       float w = ((Number)fldWeight.getValue()).floatValue();
       prod.setWeight(w);
       if (w==0){
         prod.setProt(0f);
         prod.setFat(0f);
         prod.setCarb(0f);
       }else{
        prod.setProt((100 * ((Number)fldProt.getValue()).floatValue()/
               w));
        prod.setFat((100 * ((Number)fldFat.getValue()).floatValue()/
               w));
       
        prod.setCarb((100 * ((Number)fldCarb.getValue()).floatValue()/
               w));
       }
       prod.setGi(((Number)fldGi.getValue()).intValue());
       prod.setOwner(((ProdGroup)groupsCombo.getSelectedItem()).getId());
       return prod;
   }
   public int getSelectedGroupIndex(){
       return groupsCombo.getSelectedIndex();
   }
 
 


}
