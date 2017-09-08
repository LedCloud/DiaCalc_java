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
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import tablemodels.MenuModel;
import java.net.URL;
import lookout.settings.ProgramSettings;

public class SettingsFrame extends JDialog implements ActionListener, ItemListener{
    private boolean res = false;
    private static final String OK = "ok";
    private static final String CANCEL = "cancel";
    private SpinnerModel numb;
    private JSpinner spin;
    private ProgramSettings settings;
    private JCheckBox snack;
    private JRadioButton small;
    private JRadioButton big;
    private JCheckBox use;
    private JTable tb;
    private JCheckBox calc;
    private JComboBox round;
    private JCheckBox carbRatio;
    private JCheckBox lockCoef;
    private JCheckBox product_once;
    private JCheckBox vacuum;

    public SettingsFrame(JFrame owner){
        super(owner);
        setTitle("Настройки программы");
        settings = ProgramSettings.getInstance();
        //Настройки области меню
        Box box = new Box(BoxLayout.PAGE_AXIS);
        JPanel one = new JPanel(new GridBagLayout());
        snack = new JCheckBox("<html>Использовать перекус <sup>*</sup></html>");
        snack.setSelected(settings.getIn().isUseSnack());
        snack.addItemListener(this);

        tb = new JTable(new MenuModel(settings.getIn().getMenuMask()));
        ((MenuModel)tb.getModel()).setMask(settings.getIn().getMenuMask());
        tb.getColumnModel().getColumn(0).setPreferredWidth(settings.getIn().getSizedValue(130));
        for (int i=1;i<tb.getColumnCount();i++){
            tb.getColumnModel().getColumn(i).setPreferredWidth(settings.getIn().getSizedValue(50));
        }
        tb.setFillsViewportHeight(true);
        //tb.setFont(settings.getIn().getFont(tb.getFont()));
        tb.setRowHeight(settings.getIn().getSizedValue(tb.getRowHeight()));
        
        tb.getTableHeader().setReorderingAllowed(false);
        calc = new JCheckBox("Расчитывать ЦЕИ по к1");
        calc.setSelected(settings.getIn().isCalcOUVbyK1());

        carbRatio = new JCheckBox(
                "<html>Показывать соотношение углеводов в перекусе и основной еде<sup>*</sup></html>");
        carbRatio.setSelected(settings.getIn().isCarbRatio());
        carbRatio.setEnabled(settings.getIn().isUseSnack());

        lockCoef = new JCheckBox("Блокировать возможность изменения к1, к2, ЦЕИ");
        lockCoef.setSelected(!settings.getIn().isCoefsLocked());

        product_once = new JCheckBox("Продукт один раз в меню");
        product_once.setSelected(settings.getIn().isProductOnce());

        vacuum = new JCheckBox("Оптимизировать БД при следующем запуске");
        vacuum.setSelected(settings.getIn().isVacuum());

        one.add(snack,
                new GridBagConstraints(0,0,1,1,0.5,0.1, GridBagConstraints.LINE_START,
            GridBagConstraints.HORIZONTAL, new Insets(5,5,2,2), 0, 0));
        one.add(calc,
                new GridBagConstraints(1,0,1,1,0.5,0.1, GridBagConstraints.LINE_START,
            GridBagConstraints.HORIZONTAL, new Insets(5,5,2,2), 0, 0));
        
        JScrollPane scr = new JScrollPane(tb);
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scr.setPreferredSize(new Dimension(settings.getIn().getSizedValue(500),
                settings.getIn().getSizedValue(45)));
        one.add(new JLabel("Отметьте необходимые столбцы"),
                new GridBagConstraints(0,1,2,1,1,0.1, GridBagConstraints.LAST_LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,5,2,2), 0, 0));
        one.add(scr,
                new GridBagConstraints(0,2,2,1,1,0.8, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.BOTH, new Insets(5,5,2,2), 0, 0));
        one.add(carbRatio,
                new GridBagConstraints(0,3,2,1,1,0.15, GridBagConstraints.LINE_START,
          GridBagConstraints.BOTH, new Insets(2,5,2,2), 0, 0));
        one.add(lockCoef,
                new GridBagConstraints(0,4,2,1,1,0.15, GridBagConstraints.LINE_START,
          GridBagConstraints.BOTH, new Insets(2,5,2,2), 0, 0));
        one.add(product_once,
                new GridBagConstraints(0,5,2,1,1,0.15, GridBagConstraints.LINE_START,
          GridBagConstraints.BOTH, new Insets(2,5,2,2), 0, 0));

        one.setBorder(BorderFactory.createTitledBorder("Настройки меню"));

        JPanel two = new JPanel(new GridBagLayout());
        two.setBorder(BorderFactory.createTitledBorder("Размер элементов"));

        small = new JRadioButton("<html><font size=\""+settings.getIn().getSizedValue(3)+"\">Маленький размер <sup>*</sup></html>");
        JLabel lblSmall = null;
        //Look for the image.
        String imgPlate =  "buttons/" + 
                settings.getIn().getSizedPath(false) + 
                "plates.png";
        String imgPlateSm =  "buttons/" + 
                settings.getIn().getSizedPath(false) + 
                "platessm.png";
        URL imageURL = SettingsFrame.class.getResource(imgPlateSm);
        if (imageURL != null) {                      //image found
            lblSmall = new JLabel(new ImageIcon(imageURL));
        }

        big = new JRadioButton("<html><font size=\""+
                settings.getIn().getSizedValue(4)+
                "\">Большой размер <sup>*</sup></font></html>");
        JLabel lblBig = null;
        imageURL = SettingsFrame.class.getResource(imgPlate);
        if (imageURL != null) {                      //image found
            lblBig = new JLabel(new ImageIcon(imageURL));
        }
        if (settings.getIn().getSize()==4){
            big.setSelected(true);
        }
        else{
            small.setSelected(true);
        }
        ButtonGroup size = new ButtonGroup();
        size.add(small);
        size.add(big);
        two.add(small,
                new GridBagConstraints(0,0,1,1,0.8,0.5, GridBagConstraints.LINE_START,
            GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        if (lblSmall!=null){
            two.add(lblSmall,
                new GridBagConstraints(1,0,1,1,0.2,0.5, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        }
        two.add(big,
                new GridBagConstraints(0,1,1,1,0.8,0.5, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        if (lblBig!=null){
            two.add(lblBig,
                new GridBagConstraints(1,1,1,1,0.2,0.5, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        }

        JPanel two_add = new JPanel(new GridBagLayout());
        two_add.setBorder(BorderFactory.createTitledBorder("Часто используемые продукты"));
        use = new JCheckBox("Использовать");
        
        use.addItemListener(this);
        
        numb = new SpinnerNumberModel(settings.getIn().getUsageGroupCount(), //initial value
                                       5, //min
                                       Short.MAX_VALUE, //max
                                       1);                //step


        spin = new JSpinner(numb);
        use.setSelected(settings.getIn().getUseUsageGroup()==1);
        spin.setEnabled(settings.getIn().getUseUsageGroup()==1);
        JLabel lblSpin = new JLabel("Количество продуктов в группе");
        lblSpin.setLabelFor(spin);
        two_add.add(use,
                new GridBagConstraints(0,0,1,1,1,0.5, GridBagConstraints.LINE_START,
            GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        two_add.add(lblSpin,
                new GridBagConstraints(0,1,1,1,1,0.5, GridBagConstraints.LINE_START,
            GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        two_add.add(spin,
                new GridBagConstraints(0,2,1,1,1,0.5, GridBagConstraints.LINE_START,
            GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));

        JPanel pane = new JPanel(new GridBagLayout());
        pane.add(two,
                new GridBagConstraints(0,0,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(5,5,2,2), 0, 0));


        pane.add(two_add,
                new GridBagConstraints(1,0,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(5,5,2,2), 0, 0));
        JPanel three = new JPanel(new GridBagLayout());
        
        round = new JComboBox(MainFrame.ROUNDS);
        round.setSelectedIndex(settings.getIn().getRoundLimit());
        three.setBorder(BorderFactory.createTitledBorder("Округление дозы"));
        three.add(round,
                new GridBagConstraints(0,0,1,1,1,0.5, GridBagConstraints.LINE_START,
            GridBagConstraints.HORIZONTAL, new Insets(5,5,2,2), 0, 0));

        JPanel five = new JPanel(new GridBagLayout());
        five.setBorder(BorderFactory.createTitledBorder(""));
        five.add(vacuum,
                new GridBagConstraints(0,0,1,1,1,0.5, GridBagConstraints.LINE_START,
            GridBagConstraints.HORIZONTAL, new Insets(5,5,2,2), 0, 0));

        JButton btnOk = new JButton("Сохранить");
        btnOk.setActionCommand(OK);
        btnOk.addActionListener(this);
        
        JButton btnCancel = new JButton("Отменить изменения");
        btnCancel.setActionCommand(CANCEL);
        btnCancel.addActionListener(this);
        
        JLabel lbl = new JLabel("<html><sup>*</sup> - эти изменения войдут в силу " +
                "после перезапуска программы</html>");
        JPanel four = new JPanel(new GridBagLayout());
        four.add(btnOk,
                new GridBagConstraints(0,0,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        four.add(btnCancel,
                new GridBagConstraints(1,0,1,1,0.5,1, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        four.add(lbl,
                new GridBagConstraints(0,1,2,1,0.5,1, GridBagConstraints.LINE_START,
            GridBagConstraints.HORIZONTAL, new Insets(5,5,2,2), 0, 0));

        box.add(one);
        box.add(pane);
        box.add(three);
        box.add(five);
        box.add(four);
        add(box);
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        String cmd = e.getActionCommand();
        if (OK.equals(cmd)){
            res = true;
            setVisible(false);
        } else if (CANCEL.equals(cmd)){
            setVisible(false);
        }
    }
    @Override
    public void itemStateChanged(ItemEvent e){
        if (e.getSource()==snack){
            carbRatio.setEnabled(((JCheckBox)e.getSource()).isSelected());
        }
        else spin.setEnabled(((JCheckBox)e.getSource()).isSelected());
    }
    public boolean getResult(){
        return res;
    }
    public boolean lockCoefsFields(){
        return !lockCoef.isSelected();
    }
    public boolean useSnack(){
        return snack.isSelected();
    }
    public int getProgSize(){
        if (small.isSelected()) return 3;
        else return 4;
    }
    public int useUsageGroup(){
        return use.isSelected()?1:0;
    }
    public int getUsageGroupCount(){
        return ((Number)numb.getValue()).intValue();
    }
    public int getMenuMask(){
        return ((MenuModel)tb.getModel()).getMask();
    }
    public boolean getShouldCalcOUVbyK1(){
        return calc.isSelected();
    }
    public int getRoundLimit(){
        return round.getSelectedIndex();
    }
    public boolean showCarbRatio(){
        return carbRatio.isSelected();
    }
    public boolean isProductOnce(){
        return product_once.isSelected();
    }
    public boolean shouldVacuum(){
        return vacuum.isSelected();
    }
}
