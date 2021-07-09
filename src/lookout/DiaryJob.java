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
 * Portions Copyrighted 2009-2018 Toporov Konstantin.
 */

package lookout;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import javax.swing.*;
import java.awt.*;
import lookout.settings.ProgramSettings;
import java.net.URL;
import java.awt.event.*;
import lookout.datepicker.*;
import java.util.*;
import java.beans.*;
import tablemodels.DiaryTableModel;
import maths.*;
import javax.swing.event.*;
import javax.swing.text.MaskFormatter;
import maths.DiaryUnit;
import manager.DiaryManager;
import java.text.DecimalFormat;
import maths.Sugar;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.swing.border .*;
import tablemodels.MenuPreviewTableModel;
import products.ProductInMenu;
import products.ProductW;
import lookout.cellroutins.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import manager.InetUserManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class DiaryJob extends JDialog implements ActionListener, PropertyChangeListener{
    private static final int DATETIME = 16;
    private static final int COMMENT = 50;
    private static final int SH1 = 10;
    private static final int SH2 = 10;
    private static final int DOSE = 7;
    private static final int BZHU = 30;
    private static SimpleDateFormat fr = new SimpleDateFormat("HH:mm");
    public static long BASE = 0;
    static {
     try {
         BASE = fr.parse("00:00").getTime();
     }catch(ParseException e){
         BASE = -3600000*3;//Для Москвы
     }
    }
    private final static String ADD_EVENT = "add event";
    private final static String ADD_COMMENT = "add comment";
    private final static String DELETE_EVENT = "delete event";
    private final static String EDIT_EVENT = "edit event";
    private final static String RECALL_EVENT = "recall event";
    private final static String FILTER = "filter";
    private final static String EXPORT_DIARY = "export diary";
    private final static String EXPORT_TO_SERVER_DIARY = "export to server diary";

    private static final String MENU_PANE = "menu panel";
    private static final String EMPTY_PANE = "empty panel";

    private final ProgramSettings settings = ProgramSettings.getInstance();
    private final static long WEEK =  604800000;
    private final static long DAY = 86400000;
    private final DateField fldPriorDate;
    private final DateField fldNextDate;
    private boolean flag = true;
    private User user;
    private JTable diaryList;
    private JTextArea comment;
    private final MainFrame owner;
    private final DecimalFormat df00 = new DecimalFormat("0.00");
    private final DecimalFormat df0 = new DecimalFormat("0.0");
    private final DecimalFormat df = new DecimalFormat("0");
    //coefs zone
    private JLabel lblK1;
    private JLabel lblK2;
    private JLabel lblK3;
    private JLabel lblSh1;
    private JLabel lblSh2;
    private JLabel lblBE;
    //result zone
    private JLabel lblDPS;
    private JLabel lblQuickDose;
    private JLabel lblSlowCarbDose;
    private JLabel lblProtFatDose;
    private JLabel lblDPSplusQD;
    private JLabel lblCarbDose;
    private JLabel lblSlowDose;
    private JLabel lblWholeDose;

    private JTable menuList;
    private JTable snackList;

    private final DiaryManager manager;
    private JPanel cardpane;
    private JPanel menuPanel;
    private JPanel emptyPanel;

    private JLabel lblProt;
    private JLabel lblFat;
    private JLabel lblCarb;
    private JLabel lblGi;
    private JLabel lblGn;
    private JLabel lblCalor;
    private final JTextField fldFilter;
    private final JButton btnClearFilter;
    private final JSplitPane mainSplit;
    private JSplitPane splitMenu;
    private final FloatColorRenderer fcr;
    
    
    private ExportEventsTask task;
    private JDialog dialogWaitForExport;
    private final JButton exportToServerBtn;
    private JProgressBar progressBar;
    
    private void doSearch(){
            ((DiaryTableModel)diaryList.getModel()).refresh(
                SearchString.getSQLPart(fldFilter.getText(),SearchString.DIARY,user)
            );
    }
    public DiaryJob(User user,MainFrame owner){
        //super();
        setIconImage(new ImageIcon(DiaryJob.class
                .getResource("images/Diary.png")).getImage());
        setTitle("Обработка сохраненных меню");
        this.user = user;
        this.owner = owner;
        manager = new DiaryManager(user);

        setLayout(new BorderLayout());
        
        JToolBar top = new JToolBar();

        Date now = new Date();
        Date week_ago = new Date(now.getTime()-WEEK);


        top.setFloatable(false);

        top.setMargin(new Insets(0,10,0,10));
        
        
        fldPriorDate = new DateField(week_ago);
        fldPriorDate.addPropertyChangeListener("value", this);
        
        top.add(fldPriorDate);
        
        JLabel lbl2 = new JLabel(createImageIcon("buttons/" + 
                settings.getIn().getSizedPath(false) + 
                "interval.png"));
        top.addSeparator();
        top.add(lbl2);
        top.addSeparator();
        fldNextDate = new DateField(now);
        fldNextDate.addPropertyChangeListener("value", this);
        top.add(fldNextDate);
        top.addSeparator();

        top.add(new JLabel("Фильтр:"));
        
        fldFilter = new JTextField();
        //fldFilter.addActionListener(this);
        //fldFilter.setActionCommand(FILTER);
        fldFilter.setPreferredSize(new Dimension(Short.MAX_VALUE,0));
        fldFilter.addKeyListener(new KeyAdapter(){
            @Override
          public void keyReleased(KeyEvent e){
              if (e.getModifiersEx()==0){
                  if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
                      fldFilter.setText("");
                      doSearch();
                  }else if (e.getKeyCode()!=KeyEvent.VK_ENTER){
                        doSearch();
                  }
              }
          }
        });
        
        top.add(fldFilter);
        btnClearFilter = new JButton(createImageIcon("buttons/" + 
                settings.getIn().getSizedPath(true) + 
                "calorclear.png"));
        btnClearFilter.addActionListener(this);
        btnClearFilter.setActionCommand(FILTER);
        btnClearFilter.setToolTipText("Очистить");

        btnClearFilter.setMargin(new Insets(0,0,0,0));
        top.add(btnClearFilter);

        JPanel center = new JPanel(new BorderLayout());
        comment = new JTextArea();
        comment.setEditable(false);
        comment.setLineWrap(true);
        comment.setWrapStyleWord(true);
        
        JScrollPane scr = new JScrollPane(comment);
        scr.setPreferredSize(new Dimension(400,50));

        diaryList = new JTable(new DiaryTableModel(user,fldPriorDate.getDate(),
                new Date(fldNextDate.getDate().getTime()+DAY)));
        diaryList.setFillsViewportHeight(true);
        diaryList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        diaryList.setDefaultRenderer(Date.class, new TimeRenderer("dd.MM.yyyy HH:mm"));
        diaryList.setDefaultRenderer(Float.class, new FloatRenderer("0.0"));
        
        fcr = new FloatColorRenderer("0.0",user);
        diaryList.getColumnModel().getColumn(2).setCellRenderer(fcr);

        diaryList.setAutoCreateRowSorter(true);//дополняем сортировку
        diaryList.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
        diaryList.setRowHeight(settings.getIn().getSizedValue(diaryList.getRowHeight()));


        diaryList.getColumnModel().getColumn(0)
                .setPreferredWidth(settings.getIn().getDiaryDateSize());
        diaryList.getColumnModel().getColumn(1)
                .setPreferredWidth(settings.getIn().getDiaryCommSize());
        for(int i=2;i<9;i++){
            diaryList.getColumnModel().getColumn(i)
                    .setPreferredWidth(settings.getIn().getDiaryRestSize());
        }

        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Добавить СК");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_INSERT, 0));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(ADD_EVENT);
        popup.add(menuItem);

        menuItem = new JMenuItem("Добавить коммент");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(ADD_COMMENT);
        popup.add(menuItem);

        menuItem = new JMenuItem("Отредактировать запись");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, 0));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(EDIT_EVENT);
        popup.add(menuItem);

        popup.addSeparator();

        menuItem = new JMenuItem("Вспомнить меню");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(RECALL_EVENT);
        popup.add(menuItem);

        popup.addSeparator();

        /*menuItem = new JMenuItem("Экспортировать в файл");//Убираем потому, что при использовании контекстного меню
        menuItem.setAccelerator(KeyStroke.getKeyStroke(     //Можно выделить только одну запись
                KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        menuItem.setFont(settings.getIn().getFont(menuItem.getFont()));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(EXPORT_DIARY);
        popup.add(menuItem);
        
        menuItem = new JMenuItem("Экспортировать на сервер");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        menuItem.setFont(settings.getIn().getFont(menuItem.getFont()));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(EXPORT_TO_SERVER_DIARY);
        popup.add(menuItem);

        popup.addSeparator();*/

        menuItem = new JMenuItem("Удалить запись");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(DELETE_EVENT);
        popup.add(menuItem);

        diaryList.addMouseListener(new PopupListener(popup));
        diaryList.addKeyListener(
                new KeyAdapter(){
         @Override
         public void keyReleased(KeyEvent e){
             if (e.getKeyCode()==KeyEvent.VK_INSERT){
                 if (e.getModifiersEx()==0){
                     addEvent();
                 }else if ((e.getModifiersEx()&InputEvent.CTRL_DOWN_MASK)==
                         InputEvent.CTRL_DOWN_MASK){
                     addComment();
                 }
             }else if (e.getKeyCode()==KeyEvent.VK_DELETE){
                 if (e.getModifiersEx()==0){
                     deleteEvents();
                 }
             }else if (e.getKeyCode()==KeyEvent.VK_R &&
                     (e.getModifiersEx()&InputEvent.CTRL_DOWN_MASK)==
                         InputEvent.CTRL_DOWN_MASK){
                 recallEvent();
             }else if (e.getKeyCode()==KeyEvent.VK_F4){
                 if (e.getModifiersEx()==0){
                     editEvent();
                 }
             }/*else if (e.getKeyCode()==KeyEvent.VK_E &&
                     (e.getModifiersEx()&InputEvent.CTRL_DOWN_MASK)==
                         InputEvent.CTRL_DOWN_MASK){
                     exportDiary();
             }else if (e.getKeyCode()==KeyEvent.VK_I &&
                     (e.getModifiersEx()&InputEvent.CTRL_DOWN_MASK)==
                         InputEvent.CTRL_DOWN_MASK){
                     exportEventsToInternet();
             }*/
             
             
         }
     }
                );
 
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setOrientation(JToolBar.VERTICAL);
        JButton btn = makeButton("AddEvent","Add","nothing",
                "Добавить запись");

         popup = new JPopupMenu();
         menuItem = new JMenuItem("Добавить СК");
         menuItem.addActionListener(this);
         menuItem.setActionCommand(ADD_EVENT);
         popup.add(menuItem);
         menuItem = new JMenuItem("Добавить коммент");
         menuItem.addActionListener(this);
         menuItem.setActionCommand(ADD_COMMENT);
         popup.add(menuItem);
         
     
         class PrintPopupListener extends MouseAdapter {
            JPopupMenu popup;

            PrintPopupListener(JPopupMenu popupMenu) {
                popup = popupMenu;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    popup.show(e.getComponent(),
                               0, e.getComponent().getHeight()+1);
                }
            }
        }
        btn.addMouseListener(new PrintPopupListener(popup));
        
        bar.add(btn);

        //bar.add(makeButton("AddEvent","Add",ADD_EVENT,"Добавить запись"));
        bar.add(makeButton("EditEvent","Edit",EDIT_EVENT,
                "Редактировать запись"));
        bar.addSeparator();
        bar.add(makeButton("Export","Recall",RECALL_EVENT,
                "Восстановить меню"));
        bar.addSeparator();
        bar.add(makeButton("DiarExport","Export",EXPORT_DIARY,
                "Сохранить дневник в текстовой файл"));
        exportToServerBtn = makeButton("ExportDiary","BKP",
                EXPORT_TO_SERVER_DIARY,"Сохранить дневник на сервер в интернет");
        bar.add(exportToServerBtn);
        bar.addSeparator();
        bar.add(makeButton("Delete","Del",DELETE_EVENT,"Удалить запись(и)"));

        center.add(new JScrollPane(diaryList),BorderLayout.CENTER);
        center.add(scr,BorderLayout.SOUTH);
        center.add(bar,BorderLayout.WEST);

        

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                center,
                createPreviewPane());
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setDividerLocation(settings.getIn().getDiarySplit());
        //box.add(center);
        //box.add(createPreviewPane());

        add(top,BorderLayout.NORTH);
        add(mainSplit,BorderLayout.CENTER);

        diaryList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    if (!diaryList.getSelectionModel().isSelectionEmpty()){
                        DiaryUnit un = ((DiaryTableModel)diaryList.getModel())
                            .getUnit(
                            diaryList.convertRowIndexToModel(
                                diaryList.getSelectedRow()));
                        comment.setText(un.getComment());
                        comment.setCaretPosition(0);
                    }else{
                        comment.setText("");
                    }
                    fillMenuPreview();
                }
            }
            });
        fillMenuPreview();

        Rectangle rec = settings.getIn().getDiaryBounds();

        if (rec.getHeight()==0){
            setSize(settings.getIn().getSizedValue(750),settings.getIn().getSizedValue(500));
        }
        else{
            setBounds(rec);
        }

        setModal(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }
    private void fillMenuPreview(){
        if (!diaryList.getSelectionModel().isSelectionEmpty() &&
                ((DiaryTableModel)diaryList.getModel())
                    .getUnit(diaryList
                        .convertRowIndexToModel(diaryList
                            .getSelectedRow())).getType()==DiaryUnit.MENU
                    ){
            DiaryUnit un = ((DiaryTableModel)diaryList.getModel())
                   .getUnit(
                   diaryList.convertRowIndexToModel(
                    diaryList.getSelectedRow()));
            Collection<ProductInMenu> menu = manager.getMenu(un);
            Collection<ProductInMenu> snack = manager.getSnack(un);
            ((MenuPreviewTableModel)menuList.getModel()).setProducts(menu);
            ((MenuPreviewTableModel)snackList.getModel()).setProducts(snack);

            lblK1.setText("k1="+df00.format(un.getFactors().getK1(user.isDirect())));
            lblK2.setText("k2="+df00.format(un.getFactors().getK2()));
            lblK3.setText("ЦЕИ="+df00.format(un.getFactors().getK3()));
            lblSh1.setText("<html>СК<sub>ст</sub>="+df0.format(
                        new Sugar(un.getSh1()).getSugar(user.isMmol(), user.isPlasma()) )+
                    "</html>"
                    );
            lblSh2.setText("<html>СК<sub>кон</sub>="+df0.format(
                        new Sugar(un.getSh2()).getSugar(user.isMmol(), user.isPlasma()) ) +
                        "</html>"
                    );
            if (!user.isDirect()){
                lblBE.setText("ХЕ="+df0.format(
                        un.getFactors().getBE(user.isDirect())
                    ));
            }else{
                lblBE.setText("");
            }
            ProductW prod = new ProductW();
            for(ProductInMenu item:menu){
                prod.plusProd(item);
            }
            for(ProductInMenu item:snack){
                prod.plusProd(item);
            }
            DPS dps = new DPS(new Sugar(un.getSh1()),
                            new Sugar(un.getSh2()),
                            un.getFactors());
            Dose ds = new Dose(prod, un.getFactors(),
                    dps);
            lblDPS.setText(df0.format(ds.getDPSDose()));
            lblQuickDose.setText(df0.format(ds.getCarbFastDose()));
            lblSlowCarbDose.setText(df0.format(ds.getCarbSlowDose()));
            lblProtFatDose.setText(df0.format(ds.getSlowDose()));
            //ДПС и быстрая доза
            lblDPSplusQD.setText(df0.format(ds.getDPSDose()+ds.getCarbFastDose()));
            //вся доза углеводов
            lblCarbDose.setText(df0.format(ds.getCarbFastDose()+ds.getCarbSlowDose()
             +dps.getDPSDose()));
            lblSlowDose.setText(df0.format(ds.getCarbSlowDose()+ds.getSlowDose()));
            lblWholeDose.setText(df0.format(ds.getWholeDose()));

            lblProt.setText("Б: "+df0.format(prod.getAllProt()));
            lblFat.setText("Ж: "+df0.format(prod.getAllFat()));
            lblCarb.setText("У: "+df0.format(prod.getAllCarb()));
            lblGi.setText("ГИ: "+prod.getGi());
            lblGn.setText("ГН: "+df0.format(prod.getGL()));
            lblCalor.setText("Кал: "+df.format(prod.getCalories()));

            ((CardLayout)cardpane.getLayout()).show(cardpane,MENU_PANE);
        }
        else{
            //Скрыть зону предпросмотра
            ((CardLayout)cardpane.getLayout()).show(cardpane,EMPTY_PANE);
        }
    }
    private JComponent createPreviewPane(){
        cardpane = new JPanel(new CardLayout());

        menuPanel = new JPanel(new BorderLayout());
        menuPanel.add(createCoefsPane(),BorderLayout.NORTH);
        menuPanel.add(createMenuPane(),BorderLayout.CENTER);
        
        Box box = new Box(BoxLayout.PAGE_AXIS);
        box.add(createDosesPane());
        box.add(createCharPane());
        
        menuPanel.add(box,BorderLayout.SOUTH);

        emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.add(new JLabel(createImageIcon("images/progIcon.png")),
                BorderLayout.CENTER);

        cardpane.add(menuPanel,MENU_PANE);
        cardpane.add(emptyPanel,EMPTY_PANE);

        return cardpane;
    }
    private JComponent createMenuPane(){
        menuList = new JTable(new MenuPreviewTableModel(false));
        snackList = new JTable(new MenuPreviewTableModel(true));
        menuList.setFillsViewportHeight(true);
        snackList.setFillsViewportHeight(true);
        menuList.setDefaultRenderer(Float.class, new FloatRenderer("0.0"));
        snackList.setDefaultRenderer(Float.class, new FloatRenderer("0.0"));

        menuList.getColumnModel().getColumn(0).setPreferredWidth(settings.getIn().getSizedValue(200));
        menuList.getColumnModel().getColumn(1).setPreferredWidth(settings.getIn().getSizedValue(40));
        menuList.setRowHeight(settings.getIn().getSizedValue(menuList.getRowHeight()));

        snackList.getColumnModel().getColumn(0).setPreferredWidth(settings.getIn().getSizedValue(200));
        snackList.getColumnModel().getColumn(1).setPreferredWidth(settings.getIn().getSizedValue(40));
        snackList.setRowHeight(settings.getIn().getSizedValue(snackList.getRowHeight()));

        splitMenu =  new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(menuList),
                new JScrollPane(snackList));
        splitMenu.setOneTouchExpandable(false);
        splitMenu.setDividerSize(settings.getIn().getSizedValue(3));
        //splitMenu.setd.setDividerLocation(0.7);
        splitMenu.setDividerLocation(settings.getIn().getDiaryMenuSplit());

        menuList.getColumnModel().addColumnModelListener(new TableColumnModelListener(){
                @Override
            public void columnAdded(TableColumnModelEvent e){}
                @Override
            public void columnRemoved(TableColumnModelEvent e){}
                @Override
            public void columnMoved(TableColumnModelEvent e){}
                @Override
            public void columnMarginChanged(ChangeEvent e){
                //System.out.println("Изменили ширину столбцов в меню");
                for (int i=0;i<snackList.getColumnCount();i++){
                    snackList.getColumnModel().getColumn(i).setPreferredWidth(
                            menuList.getColumnModel().getColumn(i).getWidth());
                }
            }
                @Override
            public void columnSelectionChanged(ListSelectionEvent e){}
        });

        return splitMenu;
    }
    private JComponent createCoefsPane(){
        JPanel top = new JPanel(new GridLayout(2,3));
        lblK1 = new JLabel("k1=1.48");
        lblK2 = new JLabel("k2=3.14");
        lblK3 = new JLabel("ЦЕИ=2.63");
        lblSh1 = new JLabel("<html>СК<sub>старт</sub>=4.7</html>");
        lblSh2 = new JLabel("<html>СК<sub>конеч</sub>=4.7</html>");
        lblBE = new JLabel("ХЕ=10");
        top.add(lblK1);
        top.add(lblK2);
        top.add(lblK3);
        top.add(lblSh1);
        top.add(lblSh2);
        top.add(lblBE);

        return top;
    }
    private JComponent createCharPane(){
        JPanel pane = new JPanel(new GridLayout(1,5));

        Border b = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

        lblProt = new JLabel();
        lblProt.setBorder(b);
        //lblProt.setHorizontalAlignment(SwingConstants.CENTER);
        lblFat = new JLabel();
        lblFat.setBorder(b);
        lblCarb = new JLabel();
        lblCarb.setBorder(b);
        lblGi = new JLabel();
        lblGi.setBorder(b);
        lblGn = new JLabel();
        lblGn.setBorder(b);
        lblCalor = new JLabel();
        lblCalor.setBorder(b);

        pane.add(lblProt);
        pane.add(lblFat);
        pane.add(lblCarb);
        pane.add(lblGi);
        pane.add(lblGn);
        pane.add(lblCalor);

        return pane;
    }
    private JPanel createDosesPane(){
     JPanel DosesPane = new JPanel(new GridBagLayout());
     Border brdr = BorderFactory.createLineBorder(Color.GRAY);
     Border brdrRsd = BorderFactory.createBevelBorder(0);

     Insets in = new Insets(2,2,2,2);

     JLabel lbl = new JLabel("ДПС");
     lbl.setHorizontalAlignment(SwingConstants.CENTER);
     DosesPane.add(lbl,
          new GridBagConstraints(0,0,1,1,0.8,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     lbl = new JLabel("БД");
     lbl.setHorizontalAlignment(SwingConstants.CENTER);
     DosesPane.add(lbl,
          new GridBagConstraints(1,0,1,1,0.8,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));
     lbl = new JLabel("МД угл.");
     lbl.setHorizontalAlignment(SwingConstants.CENTER);
     DosesPane.add(lbl,
          new GridBagConstraints(2,0,1,1,0.4,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));
     lbl = new JLabel("МД б/ж.");
     lbl.setHorizontalAlignment(SwingConstants.CENTER);
     DosesPane.add(lbl,
          new GridBagConstraints(3,0,1,1,0.4,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     Color Olive = new Color(0x45928B);
     lblDPS = new JLabel("");
     lblDPS.setHorizontalAlignment(SwingConstants.CENTER);
     lblDPS.setBorder(brdr);
     lblDPS.setForeground(Olive);
     lblDPS.setToolTipText("Доза на снижение сахара крови");
     DosesPane.add(lblDPS,
          new GridBagConstraints(0,1,1,1,0.8,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     lblQuickDose = new JLabel("");
     lblQuickDose.setHorizontalAlignment(SwingConstants.CENTER);
     lblQuickDose.setBorder(brdr);
     lblQuickDose.setForeground(Olive);
     lblQuickDose.setToolTipText("Доза на быструю часть углеводов");
     DosesPane.add(lblQuickDose,
          new GridBagConstraints(1,1,1,1,0.5,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     lblSlowCarbDose = new JLabel("");
     lblSlowCarbDose.setHorizontalAlignment(SwingConstants.CENTER);
     lblSlowCarbDose.setBorder(brdr);
     lblSlowCarbDose.setForeground(Olive);
     lblSlowCarbDose.setToolTipText("Доза на медленную часть углеводов");
     DosesPane.add(lblSlowCarbDose,
          new GridBagConstraints(2,1,1,1,0.5,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     lblProtFatDose = new JLabel("");
     lblProtFatDose.setHorizontalAlignment(SwingConstants.CENTER);
     lblProtFatDose.setBorder(brdr);
     lblProtFatDose.setForeground(Olive);
     lblProtFatDose.setToolTipText("Доза на белки и жиры");
     DosesPane.add(lblProtFatDose,
          new GridBagConstraints(3,1,1,1,0.5,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     lblDPSplusQD = new JLabel("");
     lblDPSplusQD.setHorizontalAlignment(SwingConstants.CENTER);
     lblDPSplusQD.setBorder(brdrRsd);
     lblDPSplusQD.setToolTipText("Доза на снижение сахара крови " +
             "и быстрая часть дозы");
     DosesPane.add(lblDPSplusQD,
          new GridBagConstraints(0,2,2,1,0.5,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     lblSlowDose = new JLabel("");
     lblSlowDose.setHorizontalAlignment(SwingConstants.CENTER);
     lblSlowDose.setBorder(brdrRsd);
     lblSlowDose.setToolTipText("Медленная часть дозы");
     DosesPane.add(lblSlowDose,
          new GridBagConstraints(2,2,2,1,0.5,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     lblCarbDose = new JLabel("");
     lblCarbDose.setHorizontalAlignment(SwingConstants.CENTER);
     lblCarbDose.setBorder(brdr);
     Color DarkGreen = new Color(0x228614);
     lblCarbDose.setForeground(DarkGreen);
     lblCarbDose.setToolTipText("Доза на углеводы и на снижение сахара крови");
     DosesPane.add(lblCarbDose,
          new GridBagConstraints(0,3,3,1,0.5,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     TitledBorder titled = BorderFactory.createTitledBorder(brdrRsd,"вся доза");
     //titled.setBorder(brdr);
     lblWholeDose = new JLabel("");
     lblWholeDose.setHorizontalAlignment(SwingConstants.CENTER);
     lblWholeDose.setBorder(titled);
     lblWholeDose.setToolTipText("Полная доза");
     DosesPane.add(lblWholeDose,
          new GridBagConstraints(2,4,2,1,0.5,0, GridBagConstraints.BELOW_BASELINE,
          GridBagConstraints.HORIZONTAL, in , 0, 0));

     //brdr = BorderFactory.createLineBorder(Color.GRAY);
     //DosesPane.setBorder(brdr);
     return DosesPane;
 }

    @Override
    public void actionPerformed(ActionEvent ev){
        String cmd = ev.getActionCommand();

        if (ADD_EVENT.equals(cmd)){
            addEvent();
        }
        else if (DELETE_EVENT.equals(cmd)){
            deleteEvents();
        }
        else if (EDIT_EVENT.equals(cmd)){
            editEvent();
        }
        else if (RECALL_EVENT.equals(cmd)){
            recallEvent();
        }
        else if (FILTER.equals(cmd)){
            //Тут надо заменить на очистку и потом поиск
            fldFilter.setText("");
            doSearch();
        }
        else if (EXPORT_DIARY.equals(cmd)){
            exportDiary();
        }
        else if (ADD_COMMENT.equals(cmd)){
            addComment();
        }
        else if (EXPORT_TO_SERVER_DIARY.equals(cmd)){
            exportEventsToInternet();
        }
    

    }
    
    private void exportDiary(){
        if (diaryList.getRowCount()==0) return;
        JRadioButton shortformat = new JRadioButton(
                "Краткий формат");
        shortformat.setSelected(true);
        JRadioButton fullformat = new JRadioButton(
                "Полный формат");
        ButtonGroup gr = new ButtonGroup();
        gr.add(shortformat);
        gr.add(fullformat);
        SimpleDateFormat frdate = new SimpleDateFormat("dd-MM-yyyy");
        String interval = frdate.format(fldPriorDate.getDate())+ "_" +
                frdate.format(fldNextDate.getDate());
        JLabel lbl = new JLabel("Интервал дат: "+interval);
        
        Object[] radioButtonArray = {shortformat,fullformat,lbl};
        String [] var = {"Да","Нет"};
     
        if (JOptionPane.showOptionDialog(
                                        this,
                                        radioButtonArray,
                                        "Выбор режима экспорта дневника",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        var,
                                        var[0])==0)
        {
            File file = new File("diary_"+interval+".txt");
            JFileChooser fileSelector = new JFileChooser();
            fileSelector.setDialogTitle("Экспорт дневника");
            fileSelector.setSelectedFile(file);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            "Текстовой файл экспорта: *.txt", "txt");
            fileSelector.setMultiSelectionEnabled(false);
            fileSelector.setFileFilter(filter);
            fileSelector.setAcceptAllFileFilterUsed(false);
                        
            int returnVal = fileSelector.showSaveDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                try{
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter
                                 (new FileOutputStream(
                                 fileSelector.getSelectedFile()
                                 ),"UTF8"));
                Vector<String> text = getLines(fullformat.isSelected());
                for(String item:text){
                    out.write(item);
                    out.newLine();
                }
                
                out.close();
                JOptionPane.showMessageDialog(this, "Экспорт удачно завершен");
                }catch(UnsupportedEncodingException e_enc){
                    System.err.println(e_enc);
                }
                catch(IOException io_exc){
                    System.err.println(io_exc);
                }
                
            }
        }
        
    }
    private Vector<String> getLines(boolean fullformat){
        SimpleDateFormat frdate = new SimpleDateFormat("dd-MM-yyyy");
        Vector<String> res = new Vector();
        res.add("Дневник "+user.getName()+" за период с "+
                        frdate.format(fldPriorDate.getDate())+" по "+
                        frdate.format(fldNextDate.getDate()));
        res.add(centerString("Время Дата",DATETIME)+ "\t" +
                        centerString("Описание",COMMENT)+ "\t" +
                        centerString("СК старт",SH1)+ "\t" +
                        centerString("СК цель",SH2)+ "\t" +
                        centerString("Доза",DOSE) + "\t" +
                        centerString("(Б-Ж-У-ГИ-ГН)",BZHU)
                        );
        SimpleDateFormat exfr = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        for(int i=diaryList.getRowCount();i>0;i--){
            DiaryUnit du = ((DiaryTableModel)diaryList.getModel()).getUnit(
                    diaryList.convertRowIndexToModel(i-1));

            String st = exfr.format(new Date(du.getTime()))+"\t";
            String [] lines = split2lines(du.getComment(),COMMENT);
            st += lines[0] + stOfChar(' ',COMMENT-lines[0].length())+ "\t";

            if (du.getType()==DiaryUnit.SUGAR || du.getType()==DiaryUnit.MENU){
                st += centerString(df0.format(new Sugar(du.getSh1()).getSugar(user.isMmol(), user.isPlasma())),SH1)+ "\t";
            }
            if (du.getType()==DiaryUnit.MENU){
                st += centerString(df0.format(new Sugar(du.getSh2()).getSugar(user.isMmol(), user.isPlasma())),SH2)+ "\t" +
                    centerString(df0.format(du.getDose()),DOSE)+ "\t";
                st += centerString("(" + df0.format(du.getProduct().getAllProt())+"-"+
                    df0.format(du.getProduct().getAllFat())+"-"+
                    df0.format(du.getProduct().getAllCarb())+"-" +
                    du.getProduct().getGi()+"-"+
                    df.format(du.getProduct().getGL())+")",BZHU);
                res.add(st);
                //Теперь проверяем обрабатывать ли далее
                if (fullformat){
                    Vector<ProductInMenu> menu = new Vector(manager.getMenu(du));
                    Vector<ProductInMenu> snack = new Vector(manager.getSnack(du));
                    int menusize = (menu.size()>0?(menu.size()+1):0) +
                         (snack.size()>0?(snack.size()+1):0);
                    int menuline = menu.size()>0?1:0;
                    int snackline = (snack.size()>0? (menuline + menu.size()+1) :0);
                    int line=1;
                    String buf;
                    //4 строки минимальный вывод
                    while (line<lines.length || line<(menusize+1) || line<5){
                        buf = stOfChar(' ',DATETIME)+"\t";
                        if (line<lines.length){
                           buf += lines[line] + stOfChar(' ',COMMENT-lines[line].length())+"\t";
                        }
                        else{
                           buf += stOfChar(' ',COMMENT)+"\t";
                        }
                        switch(line){
                            case 1:buf += centerString("k1",SH1)+"\t"+
                                        centerString("k2",SH2)+"\t"+
                                        centerString("ЦЕИ",DOSE)+"\t";
                                 break;
                            case 2:buf += centerString(df0.format(du.getFactors().getK1Value()),SH1)+"\t"+
                                    centerString(df0.format(du.getFactors().getK2()),SH2)+"\t"+
                                    centerString(df0.format(new Sugar(du.getFactors().getK3()).getSugar(user.isMmol(), user.isPlasma())),DOSE)+"\t";
                                 break;
                            case 3:buf += centerString("БД",SH1)+"\t"+
                                    centerString("МД",SH2)+"\t"+
                                    centerString("ВСЕГО",DOSE)+"\t";
                                 break;
                            case 4:ProductW sum = new ProductW();
                                    for (ProductW item:menu){
                                        sum.plusProd(item);
                                    }
                                    for (ProductW item:snack){
                                        sum.plusProd(item);
                                    }
                                    DPS dps = new DPS(new Sugar(du.getSh1()),
                                                  new Sugar(du.getSh2()),
                                                    du.getFactors());
                                    Dose dose = new Dose(sum,du.getFactors(),dps);
                                    buf += centerString(df0.format(dose.getDPSDose()+dose.getCarbFastDose()),SH1)+"\t"+
                                        centerString(df0.format(dose.getCarbSlowDose()+dose.getSlowDose()),SH2)+"\t"+
                                        centerString(df0.format(dose.getWholeDose()),DOSE)+"\t";
                                    break;
                            default:buf += stOfChar(' ',SH1)+"\t"+stOfChar(' ',SH2)+"\t"+
                                    stOfChar(' ',DOSE)+"\t";
                        }

                        if (line<(menusize+1)){
                            if (line==menuline){
                                buf += centerString("Меню",BZHU);
                            }else if (line==snackline){
                                buf += centerString("Перекус",BZHU);
                            }else if (snackline==0 || (line<snackline && menu.size()>0)){
                                buf += menu.get(line-menuline-1).getName() + " "+
                                        df0.format(menu.get(line-menuline-1).getWeight()) +
                                        " г.";
                            }else if (snack.size()>0){
                                buf += snack.get(line-snackline-1).getName() + " "+
                                        df0.format(snack.get(line-snackline-1).getWeight()) +
                                        " г.";
                            }
                        }
                        //System.out.println(buf);
                        res.add(buf);
                        line++;
                     }

                }else if (lines.length>1){
                  for(int j=1;j<lines.length;j++){
                     res.add(stOfChar(' ',DATETIME)+"\t"+
                                    lines[j]);

                  }
                }
            }
            if ( (du.getType()==DiaryUnit.SUGAR) || (du.getType()==DiaryUnit.COMMENT)){
                //Не меню, значит закончили обработку
                res.add(st);
                if (lines.length>1){
                  for(int j=1;j<lines.length;j++){
                     res.add(stOfChar(' ',DATETIME)+"\t"+
                                    lines[j]);

                  }
                }
            }
          }
        return res;
    }
    private String [] split2lines(String s,int width){
        s = s.trim();
        if (s.length()<=width) return new String[] {s};
        Vector<String> lines = new Vector<>();
        int i;
        int len;
        while (s.length()>0){
           i = len = width<s.length()?(width-1):(s.length()-1);
           while ((s.charAt(i)!=' ') && (i>0) ){
                i--;
           }
           if (i==0){//space not found
               lines.add(s.substring(0, len+1));
               s = s.substring(len+1);
           }
           else{//space found
               lines.add(s.substring(0, i));
               s = s.substring(i+1);
           }
        }
        String [] ar = new String[] {};
        return lines.toArray(ar);
    }
    private String centerString(String in,int width){
        if (in.length()>width) return in.substring(0, width-1);
        int chars2add = width - in.length();
        int side = chars2add/2;
        return stOfChar(' ',side) + in + 
                stOfChar(' ',chars2add%2==0?side:(side+1));
    }
    private String stOfChar(char c,int count){
        String st = "";
        for(int i=0;i<count;i++){
            st += c;
        }
        return st;
    }
    private void recallEvent(){
        if (!diaryList.getSelectionModel().isSelectionEmpty()){
            DiaryUnit un = ((DiaryTableModel)diaryList.getModel())
                    .getUnit(
                        diaryList.convertRowIndexToModel(
                            diaryList.getSelectedRow()));
            if (un.getType()==DiaryUnit.MENU)
                owner.setMenu(un);
        }
    }
    private void addComment(){
        MyFocusListener fls = new MyFocusListener();
        JLabel lblTime = new JLabel("Время");
        JFormattedTextField fldTime;
        MaskFormatter mask;
        try{
            mask = new MaskFormatter("##:##");
        }catch (Exception e){ mask = null; }
        mask.setPlaceholderCharacter('_');

        fldTime = new JFormattedTextField(mask);
        Date dt = new Date();
        fldTime.setValue(fr.format(dt));
        fldTime.setInputVerifier(new TimeVerifier());
        //fldTime.setFont(settings.getIn().getFont(fldTime.getFont()));
        fldTime.addFocusListener(fls);

        JLabel lblDate = new JLabel("Дата");
        DateField fldDt = new DateField(dt);
        
        JLabel lblCom = new JLabel("Коментарий");

        JTextArea commArea = new JTextArea();
        commArea.setLineWrap(true);
        commArea.setWrapStyleWord(true);
        commArea.setText("Коментарий");
        commArea.addFocusListener(fls);

        JScrollPane sc = new JScrollPane(commArea);
        sc.setPreferredSize(new Dimension(250,50));
        

        Object [] arr = {lblTime,fldTime,lblDate,fldDt,lblCom,sc};
        String [] var = {"Сохранить","Нет"};

        if(JOptionPane.showOptionDialog(
                                        this,
                                        arr,
                                        "Ввод коментария",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        var,
                                        var[0])==JOptionPane.OK_OPTION){
            //Ответили да
            Date time;
            try{
                time = fr.parse((String)fldTime.getValue());
            }catch (ParseException e){
                time = new Date();
            }

            long datetime = ( time.getTime() - BASE)  +
                    fldDt.getDate().getTime();
            DiaryUnit u = new DiaryUnit(0,datetime,commArea.getText().trim().isEmpty()?"-*-":commArea.getText().trim(),user.getId());
            
            ((DiaryTableModel)diaryList.getModel()).addUnit(u);
            doSearch();//,SearchString.getSQLPart(fldFilter.getText(),SearchString.DIARY,user));
        }

    }
    private void addEvent(){
        MyFocusListener fls = new MyFocusListener();
        JLabel lblTime = new JLabel("Время");
        JFormattedTextField fldTime;
        MaskFormatter mask;
        try{
            mask = new MaskFormatter("##:##");
        }catch (Exception e){ mask = null; }
        mask.setPlaceholderCharacter('_');

        fldTime = new JFormattedTextField(mask);
        Date dt = new Date();
        fldTime.setValue(fr.format(dt));
        fldTime.setInputVerifier(new TimeVerifier());
        fldTime.addFocusListener(fls);
        

        JLabel lblDate = new JLabel("Дата");
        DateField fldDt = new DateField(dt);
        
        JLabel lblCom = new JLabel("Коментарий");

        JTextArea commArea = new JTextArea();
        commArea.setLineWrap(true);
        commArea.setWrapStyleWord(true);
        commArea.setText("Коментарий");
        //commArea.setFont(settings.getIn().getFont(commArea.getFont()));
        commArea.addFocusListener(fls);

        JScrollPane sc = new JScrollPane(commArea);
        sc.setPreferredSize(new Dimension(250,50));
        

        JLabel lblSh = new JLabel("Введите СК");
        JFormattedTextField fldSh = new JFormattedTextField(new DecimalFormat("0.0"));
        fldSh.setValue(user.getTargetSh().getSugar(user.isMmol(), user.isPlasma()));
        fldSh.addFocusListener(fls);
        fldSh.setInputVerifier(new PositiveFloatVerifier(false));

        Object [] arr = {lblTime,fldTime,lblDate,fldDt,lblCom,sc,lblSh,fldSh};
        String [] var = {"Сохранить","Нет"};

        if(JOptionPane.showOptionDialog(
                                        this,
                                        arr,
                                        "Ввод СК",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        var,
                                        var[0])==JOptionPane.OK_OPTION){
            //Ответили да
            Date time;
            try{
                time = fr.parse((String)fldTime.getValue());
            }catch (ParseException e){
                time = new Date();
            }

            long datetime = ( time.getTime() - BASE)  +
                    fldDt.getDate().getTime();
            Sugar s = new Sugar();
            s.setSugar(((Number)fldSh.getValue()).floatValue(), user.isMmol()
                    , user.isPlasma());
            DiaryUnit u = new DiaryUnit(0,datetime,commArea.getText().trim().isEmpty()?"-*-":commArea.getText().trim(),
                    s.getValue(),user.getId());
            
            ((DiaryTableModel)diaryList.getModel()).addUnit(u);
            doSearch();
            //,SearchString.getSQLPart(fldFilter.getText(),SearchString.DIARY,user));
        }

    }
    public void deleteEvents(){
        if (diaryList.getSelectionModel().isSelectionEmpty()) return;
        String [] var = {"Да","Нет"};

        int [] rows = diaryList.getSelectedRows();
        if(JOptionPane.showOptionDialog(
                                    this,
                                    "Вы собрались удалить "+ rows.length +
                                    " запис" + 
                                    (rows.length==1?"ь": ((rows.length>1&&rows.length<5)?"и":"ей"))+
                                    "\nПродолжить?",
                                    "Удаление",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    var,
                                    var[1])==JOptionPane.OK_OPTION){
            DiaryUnit u;
            //DiaryManager mgr = new DiaryManager(user);
            for(int r:rows){
                u = ((DiaryTableModel)diaryList.getModel())
                        .getUnit(diaryList.convertRowIndexToModel(r));
                manager.deleteUnit(u);
            }
            doSearch();
        }
        
    }
    private void exportEventsToInternet(){
        if (diaryList.getSelectionModel().isSelectionEmpty()) return;
        String [] var = {"Да","Нет"};

        int [] rows = diaryList.getSelectedRows();
        if(JOptionPane.showOptionDialog(
                                    this,
                                    "<html>Вы собрались экспортировать на сервер "+ rows.length +
                                    " запис" + 
                                    (rows.length==1?"ь": ((rows.length>1&&rows.length<5)?"и":"ей"))
                                    + "<br><br>Будут использованны пароль, логин и сервер,<br>"
                                    + "установленные в окне<br><br>"
                                    + "<b>\"Импорт и экспорт базы данных на сервер\"<b><br><br>"
                                    + "Продолжить?<br></html>",
                                    "Экспорт на сервер",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    var,
                                    var[1])==JOptionPane.OK_OPTION){
            
            exportToServerBtn.setEnabled(false);//Отключили кнопку
            //тут запускаем поток и вешаем слушатель, отркываем ProgressBar
            dialogWaitForExport = new JDialog(this,"Выгрузка дневника");
            //Add contents to it. It must have a close button,
            //since some L&Fs (notably Java/Metal) don't provide one
            //in the window decorations for dialogs.
            JLabel label = new JLabel(
                   "<html><center>Происходит выгрузка записей дневника на сервер<br>"
                    + "Пожалуйста, подождите</center>");
            label.setHorizontalAlignment(JLabel.CENTER);
            
            progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
            
            JButton closeButton = new JButton("Остановить");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    progressBar.setIndeterminate(false);
                    dialogWaitForExport.setVisible(false);
                    dialogWaitForExport.dispose();
                    dialogWaitForExport = null;
                    task.cancel(true);
                }
            });
            
            JPanel pane = new JPanel();
            GroupLayout layout = new GroupLayout(pane);
            pane.setLayout(layout);
            
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(label)
                        .addComponent(progressBar)
                        .addComponent(closeButton)
                    );
            layout.setVerticalGroup(
                    layout.createSequentialGroup()
                        .addComponent(label)
                        .addComponent(progressBar)
                        .addComponent(closeButton)
                    );
            
            
            dialogWaitForExport.setContentPane(pane);

            //Show it.
            dialogWaitForExport.setSize(new Dimension(300, 150));
            dialogWaitForExport.setLocationRelativeTo(this);
            dialogWaitForExport.setVisible(true);
            
            task = new ExportEventsTask(rows);
            //task.addPropertyChangeListener(this);
            task.execute();
            
            progressBar.setIndeterminate(true);
        }
    }
    private void editEvent(){
        if (!diaryList.getSelectionModel().isSelectionEmpty()){
            DiaryUnit un = ((DiaryTableModel)diaryList.getModel())
                    .getUnit(diaryList.convertRowIndexToModel(
                        diaryList.getSelectedRow()));

            MyFocusListener fls = new MyFocusListener();
            JLabel lblTime = new JLabel("Время");

            MaskFormatter mask;
            try{
                mask = new MaskFormatter("##:##");
            }catch (Exception e){ mask = null; }
            if (mask!=null) mask.setPlaceholderCharacter('_');


            JFormattedTextField fldTime = new JFormattedTextField(mask);
            Date dt = new Date(un.getTime());
            fldTime.setValue(fr.format(dt));
            fldTime.setInputVerifier(new TimeVerifier());
            //fldTime.setFont(settings.getIn().getFont(fldTime.getFont()));

            JLabel lblDate = new JLabel("Дата");
            DateField fldDt = new DateField(new Date(un.getTime()));

            JLabel lblCom = new JLabel("Коментарий");

            JTextArea commArea = new JTextArea();
            commArea.setLineWrap(true);
            commArea.setWrapStyleWord(true);
            commArea.setText(un.getComment());
            
            JScrollPane sc = new JScrollPane(commArea);
            sc.setPreferredSize(new Dimension(250,50));

            JLabel lblSh = null;
            JFormattedTextField fldSh = null;
            Object [] arr;
            if (un.getType()==DiaryUnit.SUGAR){
                lblSh = new JLabel("Введите СК");
                fldSh = new JFormattedTextField(new DecimalFormat("0.0"));
                fldSh.setValue(new Sugar(un.getSh1()).getSugar(user.isMmol(), user.isPlasma()));
                //fldSh.setFont(settings.getIn().getFont(fldSh.getFont()));
                fldSh.addFocusListener(fls);
                fldSh.setInputVerifier(new PositiveFloatVerifier(false));
                Object [] arr1 = {lblTime,fldTime,lblDate,fldDt,lblCom,sc,lblSh,fldSh};
                arr = arr1;
            }
            else {
                Object [] arr1 = {lblTime,fldTime,lblDate,fldDt,lblCom,sc};
                arr = arr1;
            }
            
            String [] var = {"Сохранить","Нет"};
            if(JOptionPane.showOptionDialog(
                                        this,
                                        arr,
                                        "Редактирование события",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        var,
                                        var[0])==JOptionPane.OK_OPTION){

                //Ответили да
            Date time;
            try{
                time = fr.parse((String)fldTime.getValue());
            }catch (ParseException e){
                time = new Date();
            }

            long datetime = ( time.getTime() -BASE)  +
                    fldDt.getDate().getTime();
            un.setTime(datetime);
            un.setComment(commArea.getText());

            if (un.getType()==DiaryUnit.SUGAR){
                Sugar s = new Sugar();
                s.setSugar(((Number)fldSh.getValue()).floatValue(), user.isMmol()
                    , user.isPlasma());
                un.setSh1(s.getValue());
            }

            manager.updateUnit(un);
            doSearch();
            }
        }
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
    public void propertyChange(PropertyChangeEvent ev){
        if (MainFrame.USER_CHANGED.equals(ev.getPropertyName())){
            this.user = (User)ev.getNewValue();
            fcr.changeUser(user);
            ((DiaryTableModel)diaryList.getModel()).changeUser(user,
                    SearchString.getSQLPart(fldFilter.getText(),SearchString.DIARY,user));
            /*((DiaryTableModel)diaryList.getModel()).refresh(
                    SearchString.getSQLPart(fldFilter.getText(),SearchString.DIARY,user)
                    );*/
            manager.changeUser(user);
            fillMenuPreview();
            //System.out.println("смена пользователя");
        }
        if (flag){
        //Проверяем разницу по времени
          if (fldNextDate.getDate().getTime()-fldPriorDate.getDate().getTime()<=0){
            flag = false;
            if (ev.getSource()==fldNextDate){
                fldNextDate.setDate(new Date(fldPriorDate.getDate().getTime()+DAY));
            }else{
                fldPriorDate.setDate(new Date(fldNextDate.getDate().getTime()-DAY));
            }
            flag = true;
            
          }
          ((DiaryTableModel)diaryList.getModel())
                    .changeDates(fldPriorDate.getDate(), new Date(
                    fldNextDate.getDate().getTime()+DAY),
                    SearchString.getSQLPart(fldFilter.getText(),SearchString.DIARY,user));
        }
    }
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DiaryJob.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public void storeSettings(){
        settings.getIn().setDiaryBounds(getBounds());
        settings.getIn().setDiarySplit(mainSplit.getDividerLocation());
        settings.getIn().setDiaryDateSize(diaryList.getColumnModel().getColumn(0).getWidth());
        settings.getIn().setDiaryCommSize(diaryList.getColumnModel().getColumn(1).getWidth());
        settings.getIn().setDiaryRestSize(diaryList.getColumnModel().getColumn(2).getWidth());
        settings.getIn().setDiaryMenuSplit(splitMenu.getDividerLocation());
    }
    
    class ExportEventsTask extends SwingWorker<Void, Void> {
        private InetUser iUser;
        private boolean error = false;
        private String answer = "";
        private int[] rows;
        public ExportEventsTask(int [] rows){
            super();
            iUser = new InetUserManager().getUser();
            this.rows = rows;
        }
        private void exportCancelled(){
            error = true;
            answer += "Выгрузка отменена\nЭкспорт либо не завершен, либо произведен частично";
        }
        @Override
        public Void doInBackground() {
            if (isCancelled()) return null;
            
            Vector<DiaryUnit> comments = new Vector<>();
            Vector<DiaryUnit> sugars = new Vector<>();
            Vector<DiaryUnit> menus = new Vector<>();
            
            
            for (int r:rows){
                if (isCancelled()){
                    exportCancelled();
                    return null;
                }
                DiaryUnit du = ((DiaryTableModel)diaryList.getModel())
                                    .getUnit(diaryList.convertRowIndexToModel(r));
                switch(du.getType()){
                    case DiaryUnit.COMMENT  : comments.add(du);
                        break;
                    case DiaryUnit.MENU     : menus.add(du);
                        break;
                    case DiaryUnit.SUGAR    : sugars.add(du);
                        break;
                }
            }
            
            JSONObject root = new JSONObject();
                    
            JSONArray shArr = new JSONArray();
            //Записи о сахарах
            for(int i=0;i<sugars.size() && !isCancelled();i++){
                JSONObject o = new JSONObject();
                DiaryUnit du = (DiaryUnit)sugars.elementAt(i);
                o.put("time", du.getTime());
                o.put("sh1", du.getSh1() );
                o.put("comm",du.getComment());
                shArr.add(o);
            }
            root.put("sugars", shArr);
            if (isCancelled()){
                exportCancelled();
                return  null;
            }
            
            JSONArray commArr = new JSONArray();
            //Коментарии
            for(int i=0;i<comments.size() && !isCancelled();i++){
                JSONObject o = new JSONObject();
                DiaryUnit du = (DiaryUnit)comments.elementAt(i);
                o.put("time",du.getTime());
                o.put("comm",du.getComment());
                commArr.add(o);
            }
            root.put("comments", commArr);
            
            if (isCancelled()){
                exportCancelled();
                return  null;
            }
            
            JSONArray menusArr = new JSONArray();
            //Начинаем сложные записи
            for(int i=0;i<menus.size() && !isCancelled();i++){
                JSONObject o = new JSONObject();
                DiaryUnit du = (DiaryUnit)menus.elementAt(i);
                
                o.put("id",du.getId());
                o.put("time",du.getTime());
                o.put("comm", du.getComment());
                o.put("sh1", du.getSh1());
                o.put("sh2", du.getSh2());
                o.put("k1",du.getFactors().
                        getK1Value()*10/user.getFactors().getBEValue());
                o.put("k2",du.getFactors().getK2());
                o.put("k3",du.getFactors().getK3());
                o.put("dose",du.getDose());
                o.put("prot",du.getProduct().getProt());
                o.put("fat",du.getProduct().getFat());
                o.put("carb",du.getProduct().getCarb());
                o.put("gi",du.getProduct().getGi());
                o.put("weight",du.getProduct().getWeight());
                
                //Продукты
                Vector<ProductInMenu> menu = new Vector(manager.getMenu(du));
                Vector<ProductInMenu> snack = new Vector(manager.getSnack(du));
                JSONArray menuContent = new JSONArray();
                Vector [] lists = {menu,snack};
                for(int l=0;l<lists.length;l++){
                    for(int p=0;p<lists[l].size() && !isCancelled();p++){
                        JSONObject prod = new JSONObject();
                        ProductInMenu pr = (ProductInMenu)lists[l].elementAt(p);
                        prod.put("name", pr.getName());
                        prod.put("prot",pr.getProt());
                        prod.put("fat", pr.getFat());
                        prod.put("carb",pr.getCarb());
                        prod.put("gi",pr.getGi());
                        prod.put("weight",pr.getWeight());
                        prod.put("issnack",l);
                        menuContent.add(prod);
                    }
                }
                o.put("products", menuContent );
                menusArr.add(o);
                
                if (isCancelled()){
                    exportCancelled();
                    return  null;
                }
            }
            root.put("menus", menusArr);
            //Теперь все готово, отправляем на сервер
            if (isCancelled()){
                    exportCancelled();
                    return  null;
            }
            
            String query = DoingPOST.addKeyValue("login",iUser.getLogin());
            query = DoingPOST.addKeyValue(query,"pass",iUser.getPass());
            query = DoingPOST.addKeyValue(query,"action","upload diary");
            query = DoingPOST.addKeyValue(query,"data",root.toJSONString());
            query = DoingPOST.addKeyValue(query,"ok","ok");
            
            //А вот тут посылаем большой пост запрос
            DoingPOST post = new DoingPOST(iUser.getServer(),query);
            String res = post.getAnswer();

            if (post.isError()){
                error = true;
                answer += "Не удалось выгрузить записи дневника<br>"+post.getErrorMessage();
                return null;
            }
            
            answer = "Все выгружено:<br>Записей о сахарах: "+sugars.size()+"<br>Комментариев: "+comments.size() +
                    "<br>Записей меню: "+menus.size();
            return null;
        }
        public boolean isError(){
            return error;
        }
        public String getError(){
            return answer;
        }
        public String getMessage(){
            return answer;
        }
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            exportToServerBtn.setEnabled(true);//Включили кнопку
            if (dialogWaitForExport!=null && dialogWaitForExport.isVisible()){
                progressBar.setIndeterminate(false);
                dialogWaitForExport.setVisible(false);
                dialogWaitForExport.dispose();
                dialogWaitForExport = null;
            }
            if (error){
                JOptionPane.showMessageDialog(DiaryJob.this,
                        "При выгрузке произошли ошибки\n"+answer, 
                        "Ошибка", 
                        JOptionPane.ERROR_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(DiaryJob.this,
                        "Выгрузка прошла успешно\n"+answer, 
                        "Успех", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
    }
}
