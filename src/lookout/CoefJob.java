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
import lookout.cellroutins.PositiveFloatVerifier;
import lookout.cellroutins.TimeRenderer;
import lookout.cellroutins.TimeEditor;
import lookout.cellroutins.FloatRenderer;
import lookout.cellroutins.FloatEditor;
import lookout.cellroutins.MyActionListener;
import tablemodels.CoefTableModel;

import javax.swing.*;
import java.awt.event.*;

import maths.User;
import maths.CoefsSet;
import java.util.Date;
import maths.Factors;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collection;
import maths.Sugar;
import java.util.ArrayList;
import javax.swing.text.*;
import java.text.*;
import lookout.settings.ProgramSettings;

public class CoefJob extends JDialog implements ActionListener,
        TableModelListener, PropertyChangeListener, ItemListener {
    private final static String ADD_ROW = "addRow";
    private final static String REMOVE_ROW ="removeRow";
    private final static String CALC_OUV = "calculate one unit value";
    private final static String VALUE = "value";
    private final static String PLAY_AS_ENTER = "play as enter";

    private ProgramSettings settings;
    private User user;
    private JRadioButton indirect;
    private JRadioButton direct;
    private ButtonGroup direction;
    
    private JButton addRowBtn;
    private JButton removeRowBtn;
    
    private JTable tableCs;
    private JCheckBox timeSense;

    private JLabel lblBE;
    private JFormattedTextField fldBE;

    private JLabel lblCoefOUV;
    private JFormattedTextField fldCoefOUV;
    private JButton calcBtn;

    private MainFrame owner;
    private boolean flag=false;
    private boolean startini=false;
    

    public CoefJob(MainFrame owner, User user){
        setIconImage(new ImageIcon(CoefJob.class
                .getResource("images/CoefIcon.png")).getImage());
        setTitle("Работа с коэффициентами");
        this.owner = owner;
        this.user = user;
        settings = ProgramSettings.getInstance();
        
        BoxLayout layout = new BoxLayout(getContentPane(),BoxLayout.Y_AXIS);
        getContentPane().setLayout(layout);
        
        JPanel top = new JPanel(new BorderLayout());
                
        tableCs = new JTable(new CoefTableModel(this.user));
        tableCs.setRowHeight(settings.getIn().getSizedValue(tableCs.getRowHeight()));
        tableCs.setFillsViewportHeight(true);
        tableCs.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableCs.setDefaultRenderer(Date.class, new TimeRenderer("HH:mm"));
        tableCs.setDefaultRenderer(float.class, new FloatRenderer("0.00"));
        //////////
        DecimalFormat df0 = new DecimalFormat("0.00");
        NumberFormatter formatter0 = new NumberFormatter(df0);
        JFormattedTextField field0 = new JFormattedTextField(formatter0);
        FloatEditor cellEditor0 = new FloatEditor(field0, df0);
        /////////
        tableCs.setDefaultEditor(float.class, cellEditor0);

        ////////////////
        MaskFormatter formatter;
        try{
            formatter = new MaskFormatter("##:##");

        } catch (Exception e){formatter = new MaskFormatter();}

        formatter.setValidCharacters("0123456789");
        formatter.setPlaceholderCharacter('_');
        JFormattedTextField fld = new JFormattedTextField(formatter);
        ///////////////
        tableCs.getColumnModel().getColumn(1).setCellEditor(new TimeEditor(fld));
        
        tableCs.getModel().addTableModelListener(this);

        //Actions//
        AbstractAction playASenter = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ev){
                JTable tb = (JTable)ev.getSource();
                int rec_id = ((CoefTableModel)tb.getModel())
                        .getCoefs(
                        tb.convertRowIndexToModel(tb.getSelectedRow())
                        ).getId();
                int col = tb.getSelectedColumn();

                if (tb.isEditing()){
                    tb.getCellEditor().stopCellEditing();
                }
                int row_now = ((CoefTableModel)tb.getModel()).findRow(rec_id);
                if (col<(tb.getColumnCount()-1)){
                    tb.setRowSelectionInterval(row_now, row_now);
                    tb.setColumnSelectionInterval(col+1, col+1);
                }
                else if (row_now<(tb.getRowCount()-1)){
                    tb.setRowSelectionInterval(row_now+1, row_now+1);
                    tb.setColumnSelectionInterval(1, 1);
                }
            }
        };
        tableCs.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
        tableCs.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
        tableCs.getActionMap().put(PLAY_AS_ENTER, playASenter);
        ////////
        
        JToolBar bar = new JToolBar();
        addRowBtn = makeButton("New","Add",ADD_ROW,
              "Добавить коэффициенты");
        bar.add(addRowBtn);
        
        
        bar.add(Box.createHorizontalGlue());
        removeRowBtn  = makeButton("Delete","Del",REMOVE_ROW,
              "Удалить коэффициенты");
        bar.add(removeRowBtn);
        bar.setFloatable(false);
        
        top.add(new JScrollPane(tableCs),BorderLayout.CENTER);
        top.add(bar,BorderLayout.SOUTH);
       
        MyFocusListener fls = new MyFocusListener();
        

        lblBE = new JLabel("Вес ХЕ");
        fldBE = new JFormattedTextField(new DecimalFormat("0.0"));
        fldBE.setColumns(4);
        fldBE.addPropertyChangeListener(VALUE, this);
        fldBE.setInputVerifier(new PositiveFloatVerifier(false));
        fldBE.addActionListener(new MyActionListener(false));
        fldBE.addFocusListener(fls);
        fldBE.setToolTipText("Вес ХЕ в граммах");

        
        indirect = new JRadioButton(/*settings.getIn().StrSize(*/"Прямые коэффициенты (ХЕ)");//);
        
        direct  = new JRadioButton("Обратные коэффициенты");
        direction = new ButtonGroup();
        direction.add(indirect);
        direction.add(direct);
        
        timeSense = new JCheckBox("Расчет коэффициентов по времени");
        timeSense.addItemListener(this);
        
        calcBtn = new JButton("Расчет ЦЕИ");
        calcBtn.setActionCommand(CALC_OUV);
        calcBtn.addActionListener(this);
        calcBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblCoefOUV = new JLabel("Коэффициент расчета ЦЕИ");
        fldCoefOUV = new JFormattedTextField(new DecimalFormat("0.0"));
        
        
        fldCoefOUV.setColumns(5);
        fldCoefOUV.addPropertyChangeListener(VALUE, this);
        fldCoefOUV.setInputVerifier(new PositiveFloatVerifier(false));
        fldCoefOUV.addFocusListener(fls);
        fldCoefOUV.setToolTipText("Коэффициент расчета ЦЕИ (по умолчанию 167)");

        
        JPanel mid = new JPanel(new GridBagLayout());
        mid.add(indirect,
          new GridBagConstraints(0,0,1,1,0.8,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,3,3), 0, 0));
        mid.add(direct,
          new GridBagConstraints(0,1,1,1,0.8,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(2,5,5,3), 0, 0));
        mid.add(lblBE,
          new GridBagConstraints(1,0,1,1,0.2,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,2,3,5), 0, 0));
        mid.add(fldBE,
          new GridBagConstraints(1,1,1,1,0.2,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,2,5,5), 0, 0));
        mid.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel submid = new JPanel(new GridBagLayout());
        submid.add(timeSense,
          new GridBagConstraints(0,0,1,1,0.5,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

        submid.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel bot = new JPanel(new GridBagLayout());
        bot.add(lblCoefOUV,
          new GridBagConstraints(0,0,1,1,0.8,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,3,3), 0, 0));
        bot.add(fldCoefOUV,
          new GridBagConstraints(0,1,1,1,0.2,0, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,5,5,3), 0, 0));
        bot.add(calcBtn,
          new GridBagConstraints(1,0,1,2,0.2,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,2,5,5), 0, 0));
        bot.setBorder(BorderFactory.createLineBorder(Color.GRAY));

       
        getContentPane().add(top);
        getContentPane().add(mid);
        getContentPane().add(submid);
        getContentPane().add(bot);

        startini=false;
        userChanged(user);
        startini=true;
        
        if (settings.getIn().getSize()==4){
            setSize(new Dimension(settings.getIn().getSizedValue(400),
                    settings.getIn().getSizedValue(375)));
        }
        else{
            setSize(new Dimension(settings.getIn().getSizedValue(385),
                    settings.getIn().getSizedValue(350)));
        }
        setVisible(true);
        setModal(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        
        indirect.addItemListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ev){
        String cmd = ev.getActionCommand();
        if (ADD_ROW.equals(cmd)){
            addRow();
        } else if (REMOVE_ROW.equals(cmd)){
            removeRow();
        } else if (CALC_OUV.equals(cmd)){
            calcOUV();
        }
    }

    private void addRow(){
        CoefsSet cf = new CoefsSet();
        ((CoefTableModel)tableCs.getModel()).addRow(cf);
    }

    private void removeRow(){
        if (!tableCs.getSelectionModel().isSelectionEmpty() && !tableCs.isEditing()){
            if (tableCs.getRowCount()==1 && user.isTimeSense()){
                timeSense.setSelected(false);
            }
            int row = tableCs.getSelectedRow();
            ((CoefTableModel)tableCs.getModel()).deleteRow(row);
        }
    }

    private void calcOUV(){
        JOptionPane.showMessageDialog(this,
                "ЦЕИ будет рассчитана исходя из веса "+user.getName() + " " +
                (new DecimalFormat("0")).format(user.getWeight()) + "кг.\n" +
                "и коэффициента расчета " + fldCoefOUV.getValue() );
        if (tableCs.getRowCount()>0){
            for (int i=0;i<tableCs.getRowCount();i++){
                CoefsSet cf = ((CoefTableModel)tableCs.getModel()).getCoefs(i);
                float k1_10;
                if (user.isDirect()){
                    k1_10 = 10f/cf.getK1();
                } else{
                    k1_10 = cf.getK1() * 10f /
                                user.getFactors().getBE(user.isDirect());
                }
                float k3 = new Sugar(user.getOUVcoef() / (user.getWeight()*k1_10))
                                .getSugar(user.isMmol(), user.isPlasma());
                ((CoefTableModel)tableCs.getModel()).setValueAt(k3, i, 4);
            }
        }
        owner.calcOUV();
    }
    public void userChanged(User user){
        tableCs.getDefaultEditor(float.class).stopCellEditing();
        tableCs.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
        flag=false;
        this.user = user;

        ((CoefTableModel)tableCs.getModel()).changeUser(this.user);
        fldBE.setValue(this.user.getFactors().getBE(this.user.isDirect()));
        if (this.user.isDirect()){
            direct.setSelected(true);
            lblBE.setText("<html>Кол. инс.<br>на k1 гр.</html>");
        }
        else{
            indirect.setSelected(true);
            lblBE.setText("Вес ХЕ");
        }
        fldBE.setEditable(!user.isDirect());
        
        if (this.user.isTimeSense()) timeSense.setSelected(true);
        else timeSense.setSelected(false);

        fldCoefOUV.setValue(this.user.getOUVcoef());

        //if (startini) sendCoefs2Owner(((CoefTableModel)tableCs.getModel()).getAllCoefs());

        flag=true;
    }

    @Override
    public void tableChanged(TableModelEvent e){
     if (flag)
     {  //передаем не данные, а их копию
         sendCoefs2Owner(((CoefTableModel)tableCs.getModel()).getAllCoefs());
     }
    }
    @Override
    public void propertyChange(PropertyChangeEvent ev){
        String prop_name = ev.getPropertyName();
        if (MainFrame.USER_CHANGED.equals(prop_name)){
            userChanged((User)ev.getNewValue());
        }
        if (flag){
         if (VALUE.equals(prop_name)){
            if (ev.getSource()==fldBE){
                //System.out.println(ev.getNewValue() + " "+ev.getOldValue()+" "+ev.getPropertyName());
                changeBE();
                
            } else if (ev.getSource()==fldCoefOUV){
                user.setOUVcoef( ((Number)fldCoefOUV.getValue()).floatValue() );
                //owner.set тут сохраняем пользователя
                owner.updateOUVcoef(user);
            }
         }
        }
    }

    private void changeBE(){
        boolean shouldReCalc = false;
        float newBE = ((Number)fldBE.getValue()).floatValue();
        Object[] options = {"Да","Нет"};
        int n = JOptionPane.showOptionDialog(this,
                                    "Вы изменили вес ХЕ\n\n" +
                                    "пересчитать коэффициенты k1?",
                                    "Изменение коэффициентов",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[0]);
                    if (n == JOptionPane.YES_OPTION) {
                        shouldReCalc = true;
                        //System.out.println("Пересчитываем k1");
                        for (int i=0;i<tableCs.getRowCount();i++){
                            CoefsSet cf = ((CoefTableModel)tableCs.getModel()).getCoefs(i);
                            float newK1 = cf.getK1()*newBE / user.getFactors().getBE(user.isDirect());

                            ((CoefTableModel)tableCs.getModel()).setValueAt(newK1, i, 2);
                        }
                    }
        user.getFactors().setK1XE(
              user.getFactors().getK1(user.isDirect()),
              newBE,
              user.isDirect());
        user = owner.setBE(user,shouldReCalc);
    }
    private JButton makeButton(String imageName, String altText,
            String actionCommand, String toolTipText) {
        //Look for the image.
        String imgLocation =  "buttons/" + 
                settings.getIn().getSizedPath(true) + 
                imageName.toLowerCase() +
                ".png";
        URL imageURL = MainFrame.class.getResource(imgLocation);

        //Create and initialize the button.
        JButton button = new JButton();
        button.setToolTipText(toolTipText);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);

        if (imageURL != null) {                      //image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {                                     //no image found
            button.setText(altText);
            System.err.println("Resource not found: "
                               + imgLocation);
        }

        return button;
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
      if (flag){
        if (e.getSource()==timeSense){
            if (timeSense.isSelected() && tableCs.getRowCount()==0){
                flag = false;
                timeSense.setSelected(false);
                flag = true;
            }
            user.setTimeSense(timeSense.isSelected());
            user = owner.updateOUVcoef(user);
            sendCoefs2Owner(((CoefTableModel)tableCs.getModel()).getAllCoefs());
        }
        else
        if (e.getStateChange()==ItemEvent.SELECTED ||
              e.getStateChange()==ItemEvent.DESELECTED){
              
                boolean dir = !user.isDirect();
                flag=false;
                user.setDirect(dir);
                fldBE.setValue(user.getFactors().getBE(user.isDirect()));
                flag=true;

                user = owner.changeDirection(user);
                if (user.isDirect()) lblBE.setText("<html>Кол. инс.<br>на k1 гр.</html>");
                else lblBE.setText("Вес ХЕ");
                ((CoefTableModel)tableCs.getModel()).changeUser(user);

                fldBE.setEditable(!user.isDirect());
              
        }
        
      }
   }
   private void sendCoefs2Owner(Collection col){//Этот метод передает коэф-ты в основную форму
       boolean mode = !timeSense.isSelected();
       Collection ex = new ArrayList();
       for (Object item:col){
           ex.add(new CoefsSet( (CoefsSet)item ));
       }
       if (ex.size()>0){
           for (Object item:ex){
               ((CoefsSet)item).setK3(new Sugar(((CoefsSet)item).getK3())
                       .getSugar(user.isMmol(),user.isPlasma()));
               Factors f = new Factors(
                            ((CoefsSet)item).getK1(),
                            ((CoefsSet)item).getK2(),
                            ((CoefsSet)item).getK3(),
                            user.getFactors().getBEValue() );
               ((CoefsSet)item).setK1(f.getK1(user.isDirect()));
               ((CoefsSet)item).setMode(mode);
               //System.out.println(item);
           }
       }
       owner.setCoefSets( ex );//Тут уже данные пересчитаны к ммоль и плазме*/
   }
}
