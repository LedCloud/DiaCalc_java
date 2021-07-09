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


import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import lookout.cellroutins.*;
import lookout.datepicker.DateField;
import lookout.dndsupport.*;
import lookout.printing.BasePrinter;
import lookout.printing.MenuPrinter;
import lookout.settings.ProgramSettings;
import manager.*;
import maths.*;
import products.ProdGroup;
import products.ProductInBase;
import products.ProductInMenu;
import products.ProductW;
import tablemodels.*;

public class MainFrame extends JFrame implements ActionListener,
PropertyChangeListener, TableModelListener, ItemListener {
  public static final String PROGRAM_VERSION = "DiaCalcJ v2.55.1";
  public static final int PROGRAM_VER_INT = 2551;

  //Actions name
  private static final String MOVE2MENU = "move to menu";
  private static final String MOVE2PROD = "move to prod";
  private static final String MOVE2GROUP = "move to group";
  private static final String MOVE2CMPL = "move to compl";
  private static final String MOVE2SNACK = "move to snack";
  private static final String SELECTION_SEARCHED = "doSelectionSeached";
  private static final String SELECTION_CANCELED = "doSelectionCanceled";

  private static final String ADD2MENU = "add product to menu";
  private static final String ADD2SNACK = "add product to snack";
  //Colors
  private static final Color ALEX_GREEN = new Color(0,0x92,0x3F);
    
 // Введем сразу имена для кнопок - потом будем их использовать в обработчиках
  private static final String MOVE_GR_UP = "moveGroupUp";
  private static final String MOVE_GR_DOWN = "moveGroupDown";
  private static final String NEW_GR = "newGroup";
  private static final String EDIT_GR = "editGroup";
  private static final String DELETE_GR = "deleteGroup";
  
  private static final String NEW_PROD = "newProd";
  private static final String EDIT_PROD = "editProd";
  private static final String DELETE_PROD = "deleteProd";
  private static final String PRINT_PROD = "dummy";
  private static final String PRINT_PROD_DOSE = "printProd doses";
  private static final String PRINT_PROD_BE = "print prods be";
  private static final String PRINT_PROD_CALOR = "print prods calor";
  private static final String SEARCH_PROD = "searchProd";
  private static final String ADD_PROD_2_MENU = "add product to menu";
  private static final String ADD_PROD_2_SNACK = "add product to snack";
  
  private static final String SUM_WEIGHT_CMPL = "sumWeightComplex";
  private static final String FILL_MENU_CMPL = "fillMenuComplex";
  private static final String DELETE_CMPL = "deleteComplex";
  private static final String CREATE_EMPTY_CMPL_PROD = "create empty complex product";

  private static final String PROG_EXIT = "exitFromProg";
  
  private static final String COEF_CHANGED = "coefsChanged";
  
  private static final String CREATE_PROD_MENU = "createProdFormMenu";
  private static final String CREATE_CMPL_PROD_MENU = "createCmplProdFromMenu";
  private static final String PRINT_MENU = "printMenu";
  private static final String STORE_MENU = "storeMenu";
  private static final String SHOW_DIARY = "recallMenu";
  private static final String DELETE_PROD_FROM_MENU = "deleteProdFromMenu";
  private static final String FLUSH_MENU = "flushMenu";
  
  private static final String PRIOR_USR = "priorUser";
  private static final String NEXT_USR = "nextUser";
  private static final String NEW_USR = "newUser";
  private static final String EDIT_USR ="editUser";
  private static final String DELETE_USR = "deleteUser";
  
  private static final String VALUE_CHANGED = "value";
  //private static final String PROGRESS_DOING = "progress";
  private static final String BEGIN_EDIT_CMPL = "begin cmpl edit";
  private static final String TOGGLE_EDIT_CMPL = "toggle editing cmpl prod";
  
  private static final String CANCEL_SEARCH = "cancel search";
  
  private static final String SHOW_ARC = "show Archive";
  private static final String SHOW_PLATES = "show Plates";
  private static final String SHOW_COEFS = "show Coefs";
  private static final String SHOW_CALCS_CALOR = "show calcs calories";
  private static final String SHOW_CALCS_BLOOD = "show calcs blood";
  private static final String SHOW_INET_BACKUP = "show internet backup";
  
  private static final String PROD_PANE = "products panel";
  private static final String SEARCH_PANE = "search panel";

  private static final String SHOW_ABOUTBOX = "show about box";
  private static final String SHOW_LICENSE = "show license";
  private static final String SHOW_SETTINGS = "show settings";
  private static final String SHOW_HELP = "show help";

  private static final String ROUND_DOSE = "round dose";
  public final static String [] ROUNDS = {"до целой дозы","до 0,5 ед.","до 0,25 ед.",
            "до 0,16(6) ед. - ручка NovoPen3","до 0,08(3) ед. - ручка NovoPen Demi",
            "0,05 ед. - помпа"};
  private static final String ADD_CALORIES = "add calories to counter";
  private static final String CLEAR_CALORIES = "clear calories counter";

  public  static final String USER_CHANGED = "user have changed";
  public static final String HELP_ADDR = "http://www.diacalc.org/help/";

  public static final String MOVE_DOWN_FROM_MENU = "move down from menu";
  public static final String MOVE_UP_FROM_MENU = "move up from menu";
  public static final String DEL_ITEM_FROM_MENU = "delete item from menu";
  public static final String FLUSH_FROM_MENU = "flush items from menu";
  public static final String INSERT_PROD_FROM_MENU = "insert prod from menu";
  public static final String INSERT_CMPLPROD_FROM_MENU = "insert cmpl prod from menu";
  public static final String ROUND_DOSE_MENU = "round dose from menu";
  public static final String STORE_MENU_MENU = "store menu";

  public static final String MOVE_TABLE_UP = "move table up";
  public static final String MOVE_TABLE_DOWN = "move table down";
  public static final String MOVE_TABLE_LEFT = "move table left";
  public static final String MOVE_TABLE_RIGHT = "move table right";

  public static final String PLAY_AS_ENTER = "play as enter";

    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
  
    private JTable grpList = null;
    private JTable prodList = null;
    private JTable cmplList = null;
    private JTable menuList = null;
    private JTable snackList = null;
    
    private JTextField fldCmplName;
    private JFormattedTextField fldCmplWeight;
    private JFormattedTextField fldCmplGi;
    private JToggleButton editCmplBtn=null;
    private JMenuItem menuItemEditCmpl=null;
    private JButton btnMakeEmptyCmpl = null;
    
    private JLabel lblProt = null;
    private JLabel lblProtAll = null;
    private JLabel lblFat = null;
    private JLabel lblFatAll = null;
    private JLabel lblCarb = null;
    private JLabel lblCarbAll = null;
    private JLabel lblGI = null;
    private JLabel lblXE = null;
    
    private DPS currDPS;
   
    private final ComplexManager cmplMgr;
    private final ProductManager prodMgr;
    private final GroupManager groupMgr;
   
    
    //coef zone
    private JFormattedTextField fldK1=null;
    private JFormattedTextField fldK2=null;
    private JFormattedTextField fldK3=null;
    private JFormattedTextField fldXE=null;
    private JFormattedTextField fldSh1;
    private JFormattedTextField fldSh2;
    
    //result zone
    private JLabel lblDPS;
    private JLabel lblQuickDose;
    private JLabel lblSlowCarbDose;
    private JLabel lblProtFatDose;
    private JLabel lblDPSplusQD;
    private JLabel lblCarbDose;
    private JLabel lblSlowDose;
    private JLabel lblWholeDose;
    
    //private ProgressMonitor progressMonitor;
    
    private JLabel lblCalorMenu;
    private JLabel lblProtMenu;
    private JLabel lblFatMenu;
    private JLabel lblCarbMenu;
    private JLabel lblGiMenu;
    private JLabel lblBEMenu;
    private JLabel lblGLMenu;
    
    //Форматирование вывода
    private final FloatRenderer floatRenderer;
    private final String [] precisionPattern = {"0.0","0.00","0.000"};
    
    private JSpinner sp;
    private ProdDialog prodDialog=null;
    
    private JTextField fldUser=null;
    private final UsersManager usersMgr;
    private int currentUser;
    private User user;
    
    private JComboBox factorsChooser=null;
    
    private ProductInBase editedCmplProd = null;
    
    private final ProgramSettings settings;
    private final JSplitPane prodSplitPane;
    private final JPanel searchPane;
    private JTextField searchFld;
    private JTable searchTable;
    private final JPanel productPanel;
    
    private CoefJob coefJobDlg = null;
    
    private boolean storeCoefs = true;
    private final JSplitPane MenuProdPane;
    private JSplitPane menu_snack = null;

    private CaloriesWindow calorWindow=null;
    private TwoColorsProgressBar caloriesInd=null;
    private JProgressBar carbRatioMenu = null;
    private JProgressBar carbRatioSnack = null;

    private DiaryJob diary=null;
    private PlateDlg plate = null;
    private ArchiveJob arc = null;
    private CalcsDialog calcsDlg = null;

    private JSplitPane splitProds=null;
    private final DecimalFormat df = new DecimalFormat("0");
    private final DecimalFormat df0 = new DecimalFormat("0.0");
    private final DecimalFormat df00 = new DecimalFormat("0.00");
    private DecimalFormat df_prec = null;
    private final NumberFormatter formatter0 = new NumberFormatter(df0);
    //private final JFormattedTextField cellField0 = new JFormattedTextField(formatter0);
    //private final FloatEditor cellEditor0 = new FloatEditor(cellField0, df0);

    //row sorter for products
    private java.util.List sortKeys = null;
    private RowSorter<tablemodels.ProductTableModel> rowsorter;

    //row sorter for complex
    private java.util.List cmplSortKeys = null;
    private RowSorter<tablemodels.ComplexTableModel> cmplRowsorter;

    private PrintRequestAttributeSet attr_set = null;

    //private JMenu menuExit;
    //private JMenuItem menuItemExit;
    //Флаг определяющий то, что в данный момент идет сохранение
    //коэ-ов и сахаров
    private boolean propertiesAreBeeignChanged = false;

  public MainFrame(){
    super();
    //Сбацаем иконку
    setIconImage(new ImageIcon(MainFrame.class
            .getResource("images/MainIcon.png")).getImage());

    settings = ProgramSettings.getInstance();
    floatRenderer = new FloatRenderer("0.0");

    currentUser = settings.getIn().getUser();
    
    prodMgr = new ProductManager();
    cmplMgr = new ComplexManager();
    usersMgr = new UsersManager();
    usersMgr.initSugars();

    userChanged();//загружаем все данные по пользователю

    //устанавливаем Layout для всей формы
    getContentPane().setLayout(new BorderLayout());
    
    groupMgr = new GroupManager();
 
    JPanel left = createGroupPane();
    
    //Формируем правую панель
    /////////////////////////////////////////////////////////////////////
    JPanel right = new JPanel(new BorderLayout());
            
    right.add(createProdPane(),BorderLayout.CENTER);
    if (settings.getIn().getSize()==3) right.setMinimumSize(new Dimension(200,200));
    else right.setMinimumSize(new Dimension(275,200));
     
    
    prodSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           left, right);
     
    prodSplitPane.setOneTouchExpandable(false);
    searchPane = createSearchPane();
    
    productPanel = new JPanel(new CardLayout());
    productPanel.add(prodSplitPane,PROD_PANE);
    productPanel.add(searchPane,SEARCH_PANE);
    
    
    prodSplitPane.setDividerLocation(settings.getIn().getGroupSize());
        
   
     MenuProdPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           createMenuPane(), productPanel);
     MenuProdPane.setOneTouchExpandable(true);
     MenuProdPane.setDividerLocation(settings.getIn().getMenuSize());
     
     getContentPane().add(MenuProdPane,BorderLayout.CENTER);
     
     
     getContentPane().add(createMainToolBar(),BorderLayout.NORTH);
    
     setJMenuBar(createMenu());
     
     int width_fr = settings.getIn().getSizedValue( settings.getIn().getSize()==3 ? 850 : 1000 );
     int height_fr = settings.getIn().getSizedValue(600);
     Rectangle rec = settings.getIn().getMainBounds();

     if (rec.getHeight()==0){
        setSize(width_fr, height_fr);
        setLocationRelativeTo(null);
     }
     else{
        setBounds(rec);
     }
     //////////////////
     addMoveUpDown(prodList);
     addMoveUpDown(grpList);
     addMoveLeftRight(grpList,menuList,prodList);
     addMoveLeftRight(prodList,grpList,menuList);
     addMoveLeftRight(menuList,prodList,grpList);
     addMoveLeftRight(cmplList,grpList,prodList);
     if (snackList!=null) addMoveLeftRight(snackList,prodList,grpList);
     //Здесь делаем подготовительные вычисления
     calcMenu();
     addWindowListener(new WindowAdapter(){
            @Override
         public void windowOpened(WindowEvent e){
            GregorianCalendar today = new GregorianCalendar();
            GregorianCalendar birthday = new GregorianCalendar();
            birthday.setTime(new Date(user.getBirthday()) );

            if (today.get(Calendar.DATE)==birthday.get(Calendar.DATE) &&
                today.get(Calendar.MONTH)==birthday.get(Calendar.MONTH)){
            ImageIcon ic = new ImageIcon(MainFrame.class.getResource("images/rose.png"));
            JOptionPane.showMessageDialog(null,
                "С днем рождения!!!",
                "Поздравляю!", JOptionPane.INFORMATION_MESSAGE, ic);
            }
         }
     });
     //if (prodList.getRowSorter()!=null){
                prodList.getRowSorter().setSortKeys(null);
                prodList.setRowSorter(null);
                //cmplList.getRowSorter().setSortKeys(null);
                //cmplList.setRowSorter(null);
       //       }
  }
  
  public void refreshUser(){
      this.pcs.firePropertyChange( USER_CHANGED, null, user );
  }
  public void refreshProducts(){
      if (cmplList!=null) cmplList.getSelectionModel().clearSelection();
      if (prodList!=null) prodList.getSelectionModel().clearSelection();
        if (grpList!=null){
            grpList.getSelectionModel().clearSelection();
            ((GroupTableModel)grpList.getModel()).reloadGroups();
            grpList.repaint();
            if (grpList.getRowCount()>0) grpList.setRowSelectionInterval(0, 0);
        }
  }
  private void setActionMap(JComponent comp){
        comp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_1,
                InputEvent.CTRL_DOWN_MASK ),MOVE2MENU);
        comp.getActionMap().put(MOVE2MENU, move2menu);
        comp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_2,
                InputEvent.CTRL_DOWN_MASK ),MOVE2SNACK);
        comp.getActionMap().put(MOVE2SNACK, move2snack);
        comp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                InputEvent.CTRL_DOWN_MASK ),MOVE2GROUP);
        comp.getActionMap().put(MOVE2GROUP, move2group);
        comp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                InputEvent.CTRL_DOWN_MASK ),MOVE2PROD);
        comp.getActionMap().put(MOVE2PROD, move2prod);
        comp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                InputEvent.CTRL_DOWN_MASK ),MOVE2CMPL);
        comp.getActionMap().put(MOVE2CMPL, move2cmpl);
        
  }

  public void hideChildren(){
      if (diary!=null) diary.setVisible(false);
      if (coefJobDlg!=null) coefJobDlg.setVisible(false);
      if (plate!=null) plate.setVisible(false);
      if (arc!=null) arc.setVisible(false);
      if (calcsDlg!=null) calcsDlg.setVisible(false);
  }

  public void storeSettings(){
      settings.getIn().setMainBounds(this.getBounds());
      settings.getIn().setMenuSize(MenuProdPane.getDividerLocation());
      settings.getIn().setPrecision( ((Number)sp.getValue()).intValue() );
      for (int i=0;i<menuList.getColumnCount();i++){
          if (i==0){
              settings.getIn().setMenuNameWidth(menuList.getColumnModel().getColumn(i).getWidth());
          } else
          if (i==1){
              settings.getIn().setMenuWeightWidth(menuList.getColumnModel().getColumn(i).getWidth());
          }
          else{
              settings.getIn().setMenuRest(menuList.getColumnModel().getColumn(i).getWidth());
              break;
          }
      }
      settings.getIn().setGroupSize(prodSplitPane.getDividerLocation());
      settings.getIn().setUser(currentUser);
      for (int i=0;i<prodList.getColumnCount();i++){
          if (i==0){
              settings.getIn().setProdName(prodList.getColumnModel().getColumn(i).getWidth());
          }
          else{
              settings.getIn().setProdRest(prodList.getColumnModel().getColumn(i).getWidth());
              break;
          }
      }
      if (menu_snack!=null) settings.getIn().setSnackSize(menu_snack.getDividerLocation());
      if (diary!=null){
          diary.storeSettings();
      }
      
      settings.store();
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
        setActionMap(button);
        return button;
    }
  
  private JPanel createSearchPane(){
      JPanel res = new JPanel(new BorderLayout());
      
      JToolBar bar = new JToolBar();
      searchFld = new JTextField();
            //searchFld.setActionCommand(DO_SEARCH);
      //searchFld.addActionListener(this);
      searchFld.addKeyListener(new KeyAdapter(){
            @Override
          public void keyReleased(KeyEvent e){
              if (e.getModifiersEx()==0){
                  if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
                      cancelSearch();
                  }else{
                      //тут дергаем поиск
                      doSearch();
                  }
              }
          }
      });
      bar.add(searchFld);
      bar.addSeparator();
      //bar.add(makeButton("Search","Search",DO_SEARCH,
      //        "Искать продукт")));
      bar.add(makeButton("CancelSrch","Cancel Search",CANCEL_SEARCH,
              "Закончить поиск"));
      
      bar.setFloatable(false);
      
      
      searchTable = new JTable(new SearchTableModel());
      searchTable.setFillsViewportHeight(true);
      searchTable.setRowHeight(settings.getIn().getSizedValue(searchTable.getRowHeight()));
      searchTable.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы

      searchTable.getColumnModel().getColumn(0).setPreferredWidth(settings.getIn().getSizedValue(300));
      searchTable.getColumnModel().getColumn(1).setPreferredWidth(settings.getIn().getSizedValue(400));
      searchTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      searchTable.setDefaultRenderer(Float.class, floatRenderer);
      searchTable.addMouseListener(new MouseAdapter(){
          @Override
          public void mouseClicked(MouseEvent e) { 
             if (SwingUtilities.isLeftMouseButton(e) && 
                     e.getClickCount() % 2 == 0)  { 
                 Rectangle rec = searchTable.getCellRect(searchTable.getSelectedRow(), 0, true);
                 if (e.getY()>=rec.y && e.getY()<=(rec.y+rec.height))
                        doSelectionSearched(); 
             }
          }
     });
     setActionMap(searchTable);
     
     AbstractAction actionDoSearched = new AbstractAction() {//Это реакция на Enter если надо, но нам тут не надо
            @Override
        public void actionPerformed(ActionEvent ae) {
            //This action will get fired on Enter Key
            doSelectionSearched();
        }
     };
     searchTable.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECTION_SEARCHED);
     searchTable.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECTION_SEARCHED);
     searchTable.getActionMap().put(SELECTION_SEARCHED, actionDoSearched);

     AbstractAction actionDoCanceled = new AbstractAction() {//Это реакция на Enter если надо, но нам тут не надо
            @Override
        public void actionPerformed(ActionEvent ae) {
            //This action will get fired on Esc Key
            cancelSearch();
        }
     };
     searchTable.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), SELECTION_CANCELED);
     searchTable.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), SELECTION_CANCELED);
     searchTable.getActionMap().put(SELECTION_CANCELED, actionDoCanceled);

     res.add(bar, BorderLayout.NORTH);
     res.add(new JScrollPane(searchTable),BorderLayout.CENTER);
      
      
     return res;
  }
   
  private void reloadProducts(){
      if ( (prodList!= null) && (grpList.getRowCount()==0 ||
              (!grpList.getSelectionModel().isSelectionEmpty() &&
                    grpList.getRowCount()>0)
              )){
          int grId = 0;
          if (prodList.getRowSorter()!=null){
            rowsorter =
                  (RowSorter<tablemodels.ProductTableModel>)prodList.getRowSorter();
            sortKeys = rowsorter.getSortKeys();
          }
          // Получаем выделенную группу
          if (!grpList.getSelectionModel().isSelectionEmpty()) {
              grId = ((ProdGroup) ((GroupTableModel)grpList.getModel())
                     .getGroup(grpList.getSelectedRow())).getId();
          }

          if (prodList.getRowSorter()!=null) prodList.getRowSorter().setSortKeys(null);
          prodList.setRowSorter(null);

          ((ProductTableModel)prodList.getModel()).reloadProducts(grId);

          if (grId==0){
              if (prodList.getRowSorter()!=null){
                prodList.getRowSorter().setSortKeys(null);
                prodList.setRowSorter(null);
              }
          }
          else if (prodList.getModel().getRowCount()>0){
              prodList.setRowSorter(rowsorter);
              rowsorter.setSortKeys(sortKeys);
          }

           if (prodList.getModel().getRowCount()>0) {
                prodList.setRowSelectionInterval(0, 0);
           }
           else{
                ((ComplexTableModel)cmplList.getModel()).reloadProducts(0);
                fldCmplName.setText("");
                fldCmplWeight.setText("");//Тут используем как исключение
                fldCmplGi.setText("");//Тут используем как исключение
                lblProt.setText("===");
                lblProtAll.setText("===");

                lblFat.setText("===");
                lblFatAll.setText("===");

                lblCarb.setText("===");
                lblCarbAll.setText("===");

                lblGI.setText("==");
           }
        }
 }

 private void reloadComplex(){
     if ((cmplList!=null)&&(!prodList.getSelectionModel().isSelectionEmpty())&&
             (!grpList.getSelectionModel().isSelectionEmpty()))
     {
          if (cmplList.getRowSorter()!=null){
            cmplRowsorter =
                  (RowSorter<tablemodels.ComplexTableModel>)cmplList.getRowSorter();
            cmplSortKeys = cmplRowsorter.getSortKeys();
          }

          ProductInBase prod;
          if (prodList.getRowCount()>0)
          prod = ((ProductTableModel)prodList.getModel()).getProduct(
                    prodList.convertRowIndexToModel(prodList.getSelectedRow()));
          else prod = new ProductInBase();

          if ( ((GroupTableModel)grpList.getModel()).getGroup(grpList.getSelectedRow()).getId()>
             (settings.getIn().getUseUsageGroup()-1) &&
                  editedCmplProd==null && prod.isComplex() ){
              editCmplBtn.setEnabled(true);
              menuItemEditCmpl.setEnabled(true);
          }
          else if (editedCmplProd==null){
              editCmplBtn.setEnabled(false);
              menuItemEditCmpl.setEnabled(false);
          }

      if (editedCmplProd==null)
      {
          if (cmplList.getRowSorter()!=null) cmplList.getRowSorter().setSortKeys(null);
          cmplList.setRowSorter(null);

          ((ComplexTableModel)cmplList.getModel()).reloadProducts(prod.getId());

          if ((prod.isComplex())){//&&(cmplList.getModel().getRowCount()>0)){
                if (cmplList.getRowCount()>0){
                    cmplList.setRowSelectionInterval(0, 0);

                    cmplList.setRowSorter(cmplRowsorter);
                    if (cmplRowsorter!=null) cmplRowsorter.setSortKeys(cmplSortKeys);
                }else{
                    if (cmplList.getRowSorter()!=null){
                        cmplList.getRowSorter().setSortKeys(null);
                        cmplList.setRowSorter(null);
                    }
                }
                ProductW sum = ((ComplexTableModel) cmplList.getModel()).getSumProd();
                sum.changeWeight(prod.getWeight());

                fldCmplName.setText(prod.getName());
                fldCmplWeight.setValue(prod.getWeight());
                fldCmplGi.setValue(prod.getGi());
                lblProt.setText(df0.format(sum.getProt()));
                lblProtAll.setText(df0.format(sum.getAllProt()));

                lblFat.setText(df0.format(sum.getFat()));
                lblFatAll.setText(df0.format(sum.getAllFat()));

                lblCarb.setText(df0.format(sum.getCarb()));
                lblCarbAll.setText(df0.format(sum.getAllCarb()));

                lblGI.setText(""+sum.getGi());
         } else  {
             if (cmplList.getRowSorter()!=null){
                 cmplList.getRowSorter().setSortKeys(null);
                 cmplList.setRowSorter(null);
             }

             fldCmplName.setText("");
             fldCmplWeight.setText("");//Тут используем как исключение
             fldCmplGi.setText("");//Тут используем как исключение
             lblProt.setText("===");
             lblProtAll.setText("===");

             lblFat.setText("===");
             lblFatAll.setText("===");

             lblCarb.setText("===");
             lblCarbAll.setText("===");

             lblGI.setText("==");
         }
      }
     }
 }

 @Override
 public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        // Handle each button.
   
        if (PROG_EXIT.equals(cmd)) {
            //menuItemExit.setSelected(false);
            //menuExit.setSelected(false);
            storeSettings();
            hideChildren();
            setVisible(false);
        }   
        else if (NEW_GR.equals(cmd)){
            addNewGroup();
        }
        else if (EDIT_GR.equals(cmd)){
            changeGroupName();
        }
        else if (DELETE_GR.equals(cmd)){
            deleteGroup();
        }
        else if (NEW_PROD.equals(cmd)){
            addNewProd();
        }
        else if (EDIT_PROD.equals(cmd)){
            editProduct();
        }
        else if (DELETE_PROD.equals(cmd)){
            deleteProduct();
        }
        else if (FLUSH_MENU.equals(cmd)){
            flushMenu();
        }
        else if (CREATE_PROD_MENU.equals(cmd)){
            createProductMenu();
        }
        else if (CREATE_CMPL_PROD_MENU.equals(cmd)){
            addCmplProdMenu();
        }
        else if (DELETE_PROD_FROM_MENU.equals(cmd)){
            deleteProdFromMenu();
        }
        else if (NEXT_USR.equals(cmd)){
            nextUser();
        }
        else if (PRIOR_USR.equals(cmd)){
            priorUser();
        }
        else if (NEW_USR.equals(cmd)){
            newUser();
        }
        else if (EDIT_USR.equals(cmd)){
            editUser();
        }
        else if (DELETE_USR.equals(cmd)){
            deleteUser();
        }
        else if (SUM_WEIGHT_CMPL.equals(cmd)){
            summingCmpl();
        }
        else if (FILL_MENU_CMPL.equals(cmd)){
            fillMenuWithCmpl();
        }
        else if (DELETE_CMPL.equals(cmd)){
            deleteProdFromCmpl();
        }
        else if (MOVE_GR_UP.equals(cmd)){
            moveGroupUp();
        }
        else if (MOVE_GR_DOWN.equals(cmd)){
            moveGroupDown();
        }
        else if (BEGIN_EDIT_CMPL.equals(cmd)){
                calcCmpl();
        }
        else if (SEARCH_PROD.equals(cmd)){
            beginSearch();
        }
        else if (CANCEL_SEARCH.equals(cmd)){
            cancelSearch();
        }
        else if (SHOW_COEFS.equals(cmd)){
            showCoefsDlg();
        }
        else if (SHOW_ABOUTBOX.equals(cmd)){
            showAboutBox();
        }
        else if (SHOW_LICENSE.equals(cmd)){
            showLicenseBox();
        }
        else if (SHOW_SETTINGS.equals(cmd)){
            showSettings();
        }
        else if (ROUND_DOSE.equals(cmd)){
            roundDose();
        }
        else if (ADD_PROD_2_MENU.equals(cmd)){
            addProdToMenu();
        }
        else if (ADD_PROD_2_SNACK.equals(cmd)){
            addProdToSnack();
        }
        else if (TOGGLE_EDIT_CMPL.equals(cmd)){
            if (editCmplBtn.isEnabled()){
                editCmplBtn.setSelected(!editCmplBtn.isSelected());
            }
        }
        else if (ADD_CALORIES.equals(cmd)){
            addCalories();
        }
        else if (CLEAR_CALORIES.equals(cmd)){
            clearCalories();
        }
        else if (SHOW_PLATES.equals(cmd)){
            showPlates();
        }
        else if (STORE_MENU.equals(cmd)){
            storeMenu();
        }
        else if (SHOW_DIARY.equals(cmd)){
            showDiary();
        }
        else if (CREATE_EMPTY_CMPL_PROD.equals(cmd)){
            addEmptyCmplProd();
        }
        else if (SHOW_ARC.equals(cmd)){
            showArc();
        }
        else if (SHOW_CALCS_CALOR.equals(cmd)){
            showCalcs(1);
        }
        else if (SHOW_CALCS_BLOOD.equals(cmd)){
            showCalcs(2);
        }
        else if (SHOW_HELP.equals(cmd)){
            showHelp();
        }
        else if (PRINT_MENU.equals(cmd)){
            printMenu();
        }else if (PRINT_PROD_DOSE.equals(cmd)){
            printProducts(BasePrinter.DOSE);
        }
        else if (PRINT_PROD_BE.equals(cmd)){
            if (!user.isDirect()) printProducts(BasePrinter.BE);
        }
        else if (PRINT_PROD_CALOR.equals(cmd)){
            printProducts(BasePrinter.CALOR);
        }
        else if (SHOW_INET_BACKUP.equals(cmd)){
            showInetBackUpDialog();
        }
}

private void showInetBackUpDialog(){
    InetBackUpDialog dlg = new InetBackUpDialog(this);
    dlg.setModal(true);
    dlg.setVisible(true);
    refreshProducts();
}
private void printProducts(int mode){
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(new BasePrinter(mode,
            new ArrayList(groupMgr.getGroups(GroupManager.ONLY_EXISTS_GROUPS)),
            new ArrayList(prodMgr.getAllProducts()),
            user));
    if (attr_set==null){
        PageFormat pf = job.defaultPage();
        float width = 0.352777f * (float)pf.getWidth();//mm
        float height = 0.352777f * (float)pf.getHeight();//mm
        attr_set = new HashPrintRequestAttributeSet();
        MediaPrintableArea area = new MediaPrintableArea(10f,10f,
            width - 2*10f,
            height - 2*10f,
            MediaPrintableArea.MM);
        attr_set.add(area);
    }
    if (job.printDialog(attr_set)) {
         try {
             job.print(attr_set);
         } catch (PrinterException ex) {
           /* The job did not successfully complete */
             ex.printStackTrace();
         }
    }   
}
private void printMenu(){
    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(new MenuPrinter(user,menuList,snackList,
            new DecimalFormat(precisionPattern[((Number)sp.getValue()).intValue()-1]))
            );
    if (attr_set==null){
        PageFormat pf = job.defaultPage();
        float width = 0.352777f * (float)pf.getWidth();//mm
        float height = 0.352777f * (float)pf.getHeight();//mm
        attr_set = new HashPrintRequestAttributeSet();
        MediaPrintableArea area = new MediaPrintableArea(10f,10f,
            width - 2*10f,
            height - 2*10f,
            MediaPrintableArea.MM);
        attr_set.add(area);
    }
    if (job.printDialog(attr_set)) {
         try {
             job.print(attr_set);
         } catch (PrinterException ex) {
           /* The job did not successfully complete */
             ex.printStackTrace();
         }
    }
}
 private JPanel createCharPane(){//панелька с характеристиками еды
     JPanel CharPane = new JPanel(new GridBagLayout());
     Border brdr = BorderFactory.createLineBorder(Color.GRAY);
     
     ////////БЖУ
     JLabel lbl = new JLabel("Б");
     CharPane.add(lbl,
             new GridBagConstraints(0,0,1,1,0.2,0, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE,  new Insets(2,2,0,1), 0, 0));
     
     lblProtMenu = new JLabel("===");
     lblProtMenu.setBorder(brdr);
     lblProtMenu.setHorizontalAlignment(SwingConstants.CENTER);
     lblProtMenu.setToolTipText("Количество белков в еде");
     CharPane.add(lblProtMenu,
             new GridBagConstraints(1,0,1,1,0.8,0, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,1,0,2) , 0, 0));
     
     
     lbl = new JLabel("Ж");
     CharPane.add(lbl,
          new GridBagConstraints(0,1,1,1,0.2,0, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,2,0,1) , 0, 0));
     
     lblFatMenu = new JLabel("===");
     lblFatMenu.setBorder(brdr);
     lblFatMenu.setHorizontalAlignment(SwingConstants.CENTER);
     lblFatMenu.setToolTipText("Количество жиров в еде");
     CharPane.add(lblFatMenu,
          new GridBagConstraints(1,1,1,1,0.8,0, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,1,0,2) , 0, 0));
     
     
     lbl = new JLabel("У");
     CharPane.add(lbl,
          new GridBagConstraints(0,2,1,1,0.2,0, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,2,0,1) , 0, 0));
     
     lblCarbMenu = new JLabel("===");
     lblCarbMenu.setBorder(brdr);
     lblCarbMenu.setHorizontalAlignment(SwingConstants.CENTER);
     lblCarbMenu.setToolTipText("Количество углеводов в еде");
     CharPane.add(lblCarbMenu,
          new GridBagConstraints(1,2,1,1,0.8,0, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,1,0,2) , 0, 0));
     //////Делаем разрыв
     lbl = new JLabel("Кал");
     CharPane.add(lbl,
             new GridBagConstraints(0,3,1,1,0.2,0, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(4,2,0,1) , 0, 0));

     lblCalorMenu = new JLabel("====");
     lblCalorMenu.setBorder(brdr);
     lblCalorMenu.setHorizontalAlignment(SwingConstants.CENTER);
     lblCalorMenu.setToolTipText("Калорийность еды");
     CharPane.add(lblCalorMenu,
             new GridBagConstraints(1,3,1,1,0.8,0, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(4,1,0,2) , 0, 0));

     ///небольшой разрыв

     lbl = new  JLabel("ГИ");
     CharPane.add(lbl,
          new GridBagConstraints(0,4,1,1,0.2,0, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(4,2,0,1) , 0, 0));
     
     lblGiMenu = new JLabel("==");
     lblGiMenu.setBorder(brdr);
     lblGiMenu.setHorizontalAlignment(SwingConstants.CENTER);
     lblGiMenu.setToolTipText("Гликемический индекс еды");
     CharPane.add(lblGiMenu,
          new GridBagConstraints(1,4,1,1,0.8,0, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(4,1,0,2) , 0, 0));

     lbl = new JLabel("ГН");
     CharPane.add(lbl,
          new GridBagConstraints(0,5,1,1,0.2,0, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(2,2,0,1) , 0, 0));

     lblGLMenu = new JLabel("===");
     lblGLMenu.setBorder(brdr);
     lblGLMenu.setHorizontalAlignment(SwingConstants.CENTER);
     lblGLMenu.setToolTipText("Гликемическая нагрузка еды");
     CharPane.add(lblGLMenu,
          new GridBagConstraints(1,5,1,1,0.8,0, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,1,0,2) , 0, 0));
     
     lbl = new JLabel("ХЕ");
     CharPane.add(lbl,
          new GridBagConstraints(0,6,1,1,0.2,0, GridBagConstraints.LINE_START,
          GridBagConstraints.NONE, new Insets(4,2,2,2) , 0, 0));
     
     lblBEMenu = new JLabel("===");
     lblBEMenu.setBorder(brdr);
     lblBEMenu.setHorizontalAlignment(SwingConstants.CENTER);
     lblBEMenu.setToolTipText("Количество ХЕ в еде");
     CharPane.add(lblBEMenu,
          new GridBagConstraints(1,6,1,1,0.8,0, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(4,1,2,2) , 0, 0));
  
     brdr = BorderFactory.createLineBorder(Color.GRAY);
     CharPane.setBorder(brdr);
     
     return CharPane;
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
          
     return DosesPane;
 }
 
 @Override
 public void propertyChange(PropertyChangeEvent e) {
      String propName = e.getPropertyName();
      if (VALUE_CHANGED.equals(propName)){
        if (e.getSource() instanceof JFormattedTextField){
            propertiesAreBeeignChanged = true;
                if (storeCoefs){
                    if (e.getSource()==fldK1 && settings.getIn().isCalcOUVbyK1() ){
                        float k1_10;
                        if (user.isDirect()){
                            k1_10 = 10f/((Number)fldK1.getValue()).floatValue();
                        } else{
                            k1_10 = ((Number)fldK1.getValue()).floatValue() * 10f /
                                user.getFactors().getBE(user.isDirect());
                        }
                        storeCoefs=false;
                        fldK3.setValue(new Sugar(user.getOUVcoef() / (user.getWeight()*k1_10))
                                .getSugar(user.isMmol(), user.isPlasma()));
                        storeCoefs=true;
                    }
                    user.getFactors().setK1XE(((Number)fldK1.getValue()).floatValue(),
                            user.getFactors().getBE(user.isDirect()),user.isDirect() );
                    user.getFactors().setK2(((Number)fldK2.getValue()).floatValue());
                    Sugar s = new Sugar();
                    s.setSugar(((Number)fldK3.getValue()).floatValue(), user.isMmol(),user.isPlasma());
                    user.getFactors().setK3(s.getValue());
                    Sugar sh1 = new Sugar();
                    sh1.setSugar(((Number)fldSh1.getValue()).floatValue(),
                            user.isMmol(),user.isPlasma());
                    Sugar sh2 = new Sugar();
                    sh2.setSugar(((Number)fldSh2.getValue()).floatValue(),
                            user.isMmol(),user.isPlasma());
                    if ((e.getSource()==fldSh1 || e.getSource()==fldSh2) &&
                            (sh1.getValue()-sh2.getValue())>5f){
                        Object[] options = {"Да","Нет" };
                        Sugar shTrg = new Sugar(sh1.getValue()-5f);
                        if (JOptionPane.showOptionDialog(this,
                                    "Не рекомендуется слишком резко снижать уровень сахара\n" +
                                    "крови. Это отрицательно сказывается мелких кровеносных сосудах." +
                                    "\n\n" +
                                    "Рекомендуемый целевой сахар крови "+
                                    df0.format(shTrg.getSugar(user.isMmol(), user.isPlasma())) +
                                    "\nУстановить рекомендуемый?",
                                    "Корректировка СК",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[0]) == JOptionPane.YES_OPTION) {
                            storeCoefs=false;
                            fldSh2.setValue(shTrg.getSugar(user.isMmol(), user.isPlasma()));
                            storeCoefs=true;
                        }
                    }

                    user.getSh1().setSugar(((Number)fldSh1.getValue()).floatValue(), 
                            user.isMmol(),user.isPlasma());
                    user.getSh2().setSugar(((Number)fldSh2.getValue()).floatValue(), 
                            user.isMmol(),user.isPlasma());

                    usersMgr.updateFactors(user);
                    user = usersMgr.getUser(currentUser);

                    currDPS.setSh1(user.getSh1());
                    currDPS.setSh2(user.getSh2());
                    currDPS.setFs(user.getFactors());
                    if (e.getSource()!=fldSh1 && e.getSource()!=fldSh2){
                        factorsChooser.setForeground(Color.GRAY);
                    }
                    
                    ((MenuTableModel)menuList.getModel()).setFactors(user.getFactors());
                    if (snackList!=null)
                        ((MenuTableModel)snackList.getModel()).setFactors(user.getFactors());

                    calcMenu();
                }
                propertiesAreBeeignChanged = false;
        }
      }
 }
  
 private void calcMenu(){
     df_prec = new DecimalFormat(precisionPattern[((Number)sp.getValue()).intValue()-1]);
     
     float carbAll = 0;
     float carbMenu = 0;
     
     ProductW prod = ((MenuTableModel)menuList.getModel()).getSumProd();
     carbMenu = prod.getAllCarb();

     if (snackList!=null) prod.plusProd(((MenuTableModel)snackList.getModel()).getSumProd());
     carbAll = prod.getAllCarb();
     
     if (carbRatioMenu!=null){
         if (carbAll==0f){
             carbRatioMenu.setValue(0);
             carbRatioMenu.setString("");
             carbRatioSnack.setValue(100);
             carbRatioSnack.setString("");
         }
         else{
             carbRatioMenu.setValue( (int)(100f*carbMenu/carbAll) );
             ProductW menu = ((MenuTableModel)menuList.getModel()).getSumProd();
             String buf = "Б:"+df0.format(menu.getAllProt())+
                     " Ж:"+df0.format(menu.getAllFat())+
                     " У:"+df0.format(menu.getAllCarb())+
                     " | ГИ "+menu.getGi() + " | ГН "+df0.format(menu.getGL());
             carbRatioMenu.setString(buf);
             carbRatioMenu.setToolTipText(buf);

             carbRatioSnack.setValue( (int)(100f*carbMenu/carbAll) );
             menu = ((MenuTableModel)snackList.getModel()).getSumProd();
             buf = "Б:"+df0.format(menu.getAllProt())+
                     " Ж:"+df0.format(menu.getAllFat())+
                     " У:"+df0.format(menu.getAllCarb())+
                     " | ГИ "+menu.getGi() + " | ГН "+df0.format(menu.getGL());
             carbRatioSnack.setString(buf);
             carbRatioSnack.setToolTipText(buf);
         }
     }
     Dose ds = new Dose(prod, user.getFactors(), currDPS);
     
     lblDPS.setText(df_prec.format(ds.getDPSDose()));
     lblQuickDose.setText(df_prec.format(ds.getCarbFastDose()));
     lblSlowCarbDose.setText(df_prec.format(ds.getCarbSlowDose()));
     lblProtFatDose.setText(df_prec.format(ds.getSlowDose()));
     //ДПС и быстрая доза
     lblDPSplusQD.setText(df_prec.format(ds.getDPSDose()+ds.getCarbFastDose()));
     //вся доза углеводов
     lblCarbDose.setText(df_prec.format(ds.getCarbFastDose()+ds.getCarbSlowDose()
             +currDPS.getDPSDose()));
     lblSlowDose.setText(df_prec.format(ds.getCarbSlowDose()+ds.getSlowDose()));
     lblWholeDose.setText(df_prec.format(ds.getWholeDose()));
     
     
     lblCalorMenu.setText(df.format(ds.getCalories()));
     lblGiMenu.setText(df.format(prod.getGi()));
     lblProtMenu.setText(df0.format(prod.getAllProt()));
     lblFatMenu.setText(df0.format(prod.getAllFat()));
     lblCarbMenu.setText(df0.format(prod.getAllCarb()));
     if (user.isDirect()) lblBEMenu.setText("===");
     else lblBEMenu.setText(df0.format(prod
             .getAllCarb()/user.getFactors().getBE(user.isDirect())));
     lblGLMenu.setText(df0.format( prod.getGL() ) );
     
     /*if (user.getCalorLimit()<(user.getEatenFood().getCalories()+ds.getCalories())){
         caloriesInd.setBackground(progressOverEatenBackGrColor);
     }else{
         caloriesInd.setBackground(progressDefaultBackGrColor);
     }*/
     caloriesInd.setForcast( (int)(100 * ds.getCalories()/user.getCalorLimit()) );
  }
  /**
   * Создаем панель коэффициентов
   * @return 
   */
 private JPanel createCoefPane(){
     JPanel coefPane = new JPanel();
     GroupLayout layout = new GroupLayout(coefPane);
     coefPane.setLayout(layout);
     
     MyFocusListener fcLst = new MyFocusListener();
     MyActionListener mal = new MyActionListener(false);
     PositiveFloatVerifier piv = new PositiveFloatVerifier(false);
     
     JLabel lblK1 = new JLabel("k1");
     lblK1.setAlignmentX(Component.RIGHT_ALIGNMENT);
     fldK1 = new JFormattedTextField(df00);
     fldK1.setColumns(settings.getIn().getSizedValue(3));
     
     fldK1.setInputVerifier(piv);
     fldK1.addActionListener(mal);

     fldK1.addFocusListener(fcLst);
     fldK1.addPropertyChangeListener("value", this);
     fldK1.setActionCommand(COEF_CHANGED);
     fldK1.setToolTipText("Коэффициент чувствительности к углеводам");
     setActionMap(fldK1);
     fldK1.setEditable(settings.getIn().isCoefsLocked());

     
     JLabel lblK2 = new JLabel("k2");
     fldK2 = new JFormattedTextField(df00);
     
     fldK2.setColumns(settings.getIn().getSizedValue(3));
     fldK2.setInputVerifier(piv);
     fldK2.addActionListener(mal);
     fldK2.addFocusListener(fcLst);
     fldK2.addPropertyChangeListener("value", this);
     fldK2.setActionCommand(COEF_CHANGED);
     fldK2.setToolTipText("Коэффициент чувствительности к белкам и жирам");
     setActionMap(fldK2);
     fldK2.setEditable(settings.getIn().isCoefsLocked());
     
     JLabel lblK3 = new JLabel("ЦЕИ");
     fldK3 = new JFormattedTextField(df00);
     
     fldK3.setColumns(settings.getIn().getSizedValue(3));
     fldK3.setInputVerifier(piv);
     fldK3.addActionListener(mal);
     fldK3.addFocusListener(fcLst);
     fldK3.addPropertyChangeListener("value", this);
     fldK3.setActionCommand(COEF_CHANGED);
     fldK3.setToolTipText("Чувствительность к инсулину");
     setActionMap(fldK3);
     fldK3.setEditable(settings.getIn().isCoefsLocked());
     
     lblXE = new JLabel();
     if (user.isDirect()) lblXE.setText("<html>Кол. инс.<br>на k1 гр.</html>");
     else lblXE.setText("Вес ХЕ");
     
      //вторая строчка
     JLabel lblSh1 = new JLabel("Нач. СК");
     fldSh1 = new JFormattedTextField(df0);
     fldSh1.setValue(user.getSh1().getSugar(user.isMmol(),user.isPlasma()));
     fldSh1.setColumns(settings.getIn().getSizedValue(3));
     fldSh1.setInputVerifier(piv);
     fldSh1.addActionListener(mal);
     fldSh1.addFocusListener(fcLst);
     fldSh1.addPropertyChangeListener("value", this);
     fldSh1.setActionCommand(COEF_CHANGED);
     fldSh1.setToolTipText("Стартовый сахар крови");
     setActionMap(fldSh1);
     
     JLabel lblSh2 = new JLabel("Цель СК");
     fldSh2 = new JFormattedTextField(df0);
     fldSh2.setValue(user.getSh2().getSugar(user.isMmol(),user.isPlasma()));
     fldSh2.setColumns(settings.getIn().getSizedValue(3));
     fldSh2.setInputVerifier(piv);
     fldSh2.addActionListener(mal);
     fldSh2.addFocusListener(fcLst);
     fldSh2.addPropertyChangeListener("value", this);
     fldSh2.setActionCommand(COEF_CHANGED);
     fldSh2.setToolTipText("Желаемый сахар крови");
     setActionMap(fldSh2);

     fldXE = new JFormattedTextField(df0);
     fldXE.setColumns(settings.getIn().getSizedValue(4));
     fldXE.setEditable(false);
     setActionMap(fldXE);
    
     CoefsManager cfMgr = new CoefsManager(user);
     storeCoefs = false;
     ArrayList<CoefsSet> cfs = new ArrayList(cfMgr.getCoefs());
     for (CoefsSet item:cfs){
         item.setK3(new Sugar(item.getK3()).getSugar(user.isMmol(),user.isPlasma()));
         Factors f = new Factors(
                        item.getK1(),
                        item.getK2(),
                        item.getK3(),
                        user.getFactors().getBEValue());
         item.setK1(f.getK1(user.isDirect()));
     }
     if (cfs.size()==0 && user.isTimeSense()){
         user.setTimeSense(false);
         usersMgr.updateUser(user);
         this.pcs.firePropertyChange( USER_CHANGED, null, user );
     }

     if (user.isTimeSense()){
         //Тут вычисляем время и подставляем нужные коэф-ты
         TimedCoefsSet timedCfs = new TimedCoefsSet(cfs);
         factorsChooser = new JComboBox((new ArrayList(timedCfs.getTimedCoefs())).toArray());
         factorsChooser.setForeground(Color.BLACK);
         if (factorsChooser.getItemCount()>0){
             Date now = new Date();
             SimpleDateFormat hour_format = new SimpleDateFormat("H");
             int hour = Integer.parseInt(hour_format.format(now));
             SimpleDateFormat min_format = new SimpleDateFormat("m");
             int min = Integer.parseInt(min_format.format(now));
             if (min>29) hour++;
             int pos = hour - 6;
             if (pos<0) pos += 24;
             factorsChooser.setSelectedIndex(pos);
             CoefsSet cf = (CoefsSet)factorsChooser.getItemAt(factorsChooser.getSelectedIndex());
            user.getFactors().setK1XE(cf.getK1(), user.getFactors().getBE(user.isDirect()), user.isDirect());
            user.getFactors().setK2(cf.getK2());
            Sugar s = new Sugar();
            s.setSugar(cf.getK3(), user.isMmol(), user.isPlasma());
            user.getFactors().setK3(s.getValue());
            usersMgr.updateFactors(user);
         }
         //вот тут нужно вычислить время и сделать выбор в чузере и только тут
         //потом при изменении чувствительности только менят состав чузера,
         //но выбор делать только при старте

     }else{
         factorsChooser = new JComboBox(cfs.toArray());
         factorsChooser.setForeground(Color.GRAY);
     }
     fldK1.setValue(user.getFactors().getK1(user.isDirect()));
     fldK2.setValue(user.getFactors().getK2());
     fldK3.setValue(new Sugar(user.getFactors().getK3()).getSugar(user.isMmol(),user.isPlasma()));
     //вот тут нужно делать через Sugar
     fldXE.setValue(user.getFactors().getBE(user.isDirect()));

     factorsChooser.addActionListener(new ActionListener()
     {
         @Override
         public void actionPerformed(ActionEvent e){
              if (storeCoefs)
              if (factorsChooser.getItemCount()>0){
                 CoefsSet cfSet = (CoefsSet)factorsChooser.getSelectedItem();
                 if (fldK1!=null){
                    fldK1.setValue(cfSet.getK1());
                    fldK2.setValue(cfSet.getK2());
                    fldK3.setValue(cfSet.getK3());
                    factorsChooser.setForeground(Color.BLACK);
                 }
              }
            }
     }       );
     setActionMap(factorsChooser);
     storeCoefs = true;
     
    //layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    
    layout.setHorizontalGroup(
            layout.createParallelGroup() //coefs + factorchooser
                    .addGroup(layout.createSequentialGroup()//coefs+be
                            .addGroup(layout.createParallelGroup()//coefs
                                    .addGroup(layout.createSequentialGroup()//k1 k2 k3
                                            .addGroup(layout.createSequentialGroup()
                                                    .addComponent(lblK1)
                                                    .addGap(settings.getIn()
                                                            .getSizedValue(2))
                                                    .addComponent(fldK1)
                                            )
                                            .addGap(settings.getIn()
                                                    .getSizedValue(5))
                                            .addGroup(layout.createSequentialGroup()
                                                    .addComponent(lblK2)
                                                    .addGap(settings.getIn()
                                                            .getSizedValue(2))
                                                    .addComponent(fldK2)
                                            )
                                            .addGap(settings.getIn()
                                                    .getSizedValue(5))
                                            .addGroup(layout.createSequentialGroup()
                                                    .addComponent(lblK3)
                                                    .addGap(settings.getIn()
                                                            .getSizedValue(2))
                                                    .addComponent(fldK3)
                                            )
                                    )
                                    .addGroup(layout.createSequentialGroup()//sh1 sh2
                                            .addGroup(layout.createSequentialGroup()
                                                    .addComponent(lblSh1)
                                                    .addGap(settings.getIn()
                                                            .getSizedValue(2))
                                                    .addComponent(fldSh1)
                                            )
                                            .addGap(settings.getIn().getSizedValue(5))
                                            .addGroup(layout.createSequentialGroup()
                                                    .addComponent(lblSh2)
                                                    .addGap(settings.getIn()
                                                            .getSizedValue(2))
                                                    .addComponent(fldSh2)
                                            )
                                    )
                            )
                            .addGap(settings.getIn().getSizedValue(5))
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)//be
                                    .addComponent(lblXE)
                                    .addComponent(fldXE)
                            )
                    )
                    .addComponent(factorsChooser));
    layout.setVerticalGroup(
            layout.createSequentialGroup()//coefs + factorchooser
                .addGroup(layout.createParallelGroup() //coefs+be
                        .addGroup(layout.createSequentialGroup() //coefs
                                .addGroup(layout.createParallelGroup()//k1 k2 k3
                                        .addGroup(layout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                                .addComponent(lblK1)
                                                .addComponent(fldK1)
                                        )
                                        .addGroup(layout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                                .addComponent(lblK2)
                                                .addComponent(fldK2)
                                        )
                                        .addGroup(layout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                                .addComponent(lblK3)
                                                .addComponent(fldK3)
                                        )
                                )
                                .addGap(settings.getIn().getSizedValue(5))
                                .addGroup(layout.createParallelGroup()//sh1 sh2
                                        .addGroup(layout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                                .addComponent(lblSh1)
                                                .addComponent(fldSh1)
                                        )
                                        .addGroup(layout.createParallelGroup(
                                        GroupLayout.Alignment.CENTER)
                                                .addComponent(lblSh2)
                                                .addComponent(fldSh2)
                                        )
                                )
                        )
                        .addGroup(layout.createSequentialGroup()//be
                                .addComponent(lblXE)
                                .addComponent(fldXE)
                        )
                )
                .addGap(settings.getIn().getSizedValue(5))
                .addComponent(factorsChooser));
     
     coefPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
     return coefPane;
 }
 
 private JSplitPane createProdPane(){
     JToolBar bar = new JToolBar();
     //здесь формируем зону базы продуктов
     bar.add(makeButton("New","NP",NEW_PROD,"Добавить продукт"));
     bar.add(makeButton("Edit","EP",EDIT_PROD,"Изменить продукт"));
    
     bar.addSeparator();
     
     bar.add(makeButton("Search","SC",SEARCH_PROD,"Искать продукт"));
    
     bar.addSeparator();
     JButton btn = makeButton("Print","PR",PRINT_PROD,"Печать базы");

     JPopupMenu popup = new JPopupMenu();
     JMenuItem menuItem = new JMenuItem("Печать доз");
     menuItem.addActionListener(this);
     menuItem.setActionCommand(PRINT_PROD_DOSE);
     popup.add(menuItem);
     menuItem = new JMenuItem("Печать ХЕ");
     menuItem.addActionListener(this);
     menuItem.setActionCommand(PRINT_PROD_BE);
     popup.add(menuItem);
     menuItem = new JMenuItem("Печать калорий");
     menuItem.addActionListener(this);
     menuItem.setActionCommand(PRINT_PROD_CALOR);
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
     bar.addSeparator();

     //bar.add(makeButton("Import","IM",IMPORT_PROD,"Импорт базы")));
     //bar.add(makeButton("Export","EX",EXPORT_PROD,"Экспорт базы")));
        
     //bar.addSeparator();
    
     bar.add(makeButton("Delete","DL",DELETE_PROD,"Удалить продукт"));
     
     bar.setFloatable(false);
    
     // И устанавливаем модель для таблицы с новыми данными
     ProdGroup gr;
     
     if (grpList.getRowCount()>0){
       gr = (ProdGroup) ((GroupTableModel)grpList.getModel())
             .getGroup(grpList.getSelectedRow());
     }
     else gr = new ProdGroup();
     
     prodList = new JTable(new ProductTableModel(prodMgr,
             gr.getId())){
     //Implement table cell tool tips.
     @Override
     public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        if (rowIndex>=0 && colIndex>=0){
            ProductInBase prod = ((ProductTableModel)getModel()).getProduct(rowIndex);
            tip = prod.getName();

            int width = prodList.getFontMetrics(prodList.getFont()).stringWidth(tip);
            if (width<=(prodList.getColumnModel().getColumn(0).getWidth()-5)) return null;
        }
        return tip;
     }
    };

     prodList.setSurrendersFocusOnKeystroke(true);
     
     prodList.setAutoCreateRowSorter(true);//дополняем сортировку
     prodList.setFillsViewportHeight(true);//заполняем всю область
     prodList.setRowHeight(settings.getIn().getSizedValue(prodList.getRowHeight()));
     prodList.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
     
     //устанавливаем ширину столбцов
     for (int i=0;i<prodList.getColumnCount();i++){
          if (i==0){
              prodList.getColumnModel().getColumn(i).setPreferredWidth(settings.getIn().getProdName());
          }
          else{
              prodList.getColumnModel().getColumn(i).setPreferredWidth(settings.getIn().getProdRest());
          }
      }
    
     
     if (prodList.getModel().getRowCount()>0) prodList.setRowSelectionInterval(0, 0);
     
     prodList.setDefaultRenderer(Float.class, floatRenderer);
    
     prodList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    //System.out.println("reloadComplex");
                    reloadComplex();
                }
            }
            });
     prodList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          
     prodList.addMouseListener(new MouseAdapter(){
          @Override
          public void mouseClicked(MouseEvent e) {
             if (SwingUtilities.isLeftMouseButton(e)){
                 //System.out.println("ProdList: mouse clicked");
                 Rectangle rec = prodList.getCellRect(prodList.getSelectedRow(), 0, true);
                 if (e.getY()<rec.y || e.getY()>(rec.y+rec.height)) return;
                 
                 if(e.getClickCount() % 2 == 0 || e.isShiftDown()){
                     addProdToMenu();
                 }
                 else if (e.isControlDown()){
                     addProdToSnack();
                 }
             }
          }
     });

     prodList.setTransferHandler(new ProdTableHandler());
     prodList.setDragEnabled(true);
     prodList.setDropMode(DropMode.INSERT_ROWS);

     prodList.addKeyListener(new KeyAdapter(){
         @Override
         public void keyReleased(KeyEvent e){
             if (e.getModifiersEx()==0){
                 switch (e.getKeyCode()) {
                     case KeyEvent.VK_INSERT:
                         addNewProd();
                         break;
                     case KeyEvent.VK_DELETE:
                         deleteProduct();
                         break;
                     case KeyEvent.VK_F4:
                         editProduct();
                         break;
                     case KeyEvent.VK_F7:
                         beginSearch();
                         break;
                     case KeyEvent.VK_M:
                         addProdToMenu();
                         break;
                     case KeyEvent.VK_S:
                         addProdToSnack();
                         break;
                 }

             }
         }
     });
     setActionMap(prodList);
     //добавление в меню продукту
     AbstractAction actionAdd2Menu = new AbstractAction() {//Это реакция на Enter если надо, но нам тут не надо
            @Override
        public void actionPerformed(ActionEvent ae) {
            addProdToMenu();
        }
     };
     prodList.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ADD2MENU);
     prodList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ADD2MENU);
     prodList.getActionMap().put(ADD2MENU, actionAdd2Menu);
     
     //добавление в перекус продукта
     AbstractAction actionAdd2Snack = new AbstractAction() {//Это реакция на Enter если надо, но нам тут не надо
            @Override
        public void actionPerformed(ActionEvent ae) {
            addProdToSnack();
        }
     };
     prodList.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), ADD2SNACK);
     prodList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), ADD2SNACK);
     prodList.getActionMap().put(ADD2SNACK, actionAdd2Snack);

    popup = new JPopupMenu();
    menuItem = new JMenuItem("Добавить в меню");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_M, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(ADD_PROD_2_MENU);
    popup.add(menuItem);

    menuItem = new JMenuItem("Добавить в перекус");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(ADD_PROD_2_SNACK);
    popup.add(menuItem);
    popup.addSeparator();

    menuItem = new JMenuItem("Поиск продукта");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F7, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SEARCH_PROD);
    popup.add(menuItem);
    popup.addSeparator();

    JMenu printmenu = new JMenu("Печать");
    
     menuItem = new JMenuItem("Печать доз");
     menuItem.addActionListener(this);
     menuItem.setActionCommand(PRINT_PROD_DOSE);
     printmenu.add(menuItem);
     menuItem = new JMenuItem("Печать ХЕ");
     menuItem.addActionListener(this);
     menuItem.setActionCommand(PRINT_PROD_BE);
     printmenu.add(menuItem);
     menuItem = new JMenuItem("Печать калорий");
     menuItem.addActionListener(this);
     menuItem.setActionCommand(PRINT_PROD_CALOR);
    printmenu.add(menuItem);
    popup.add(printmenu);
    popup.addSeparator();
    
    menuItem = new JMenuItem("Изменить продукт");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(EDIT_PROD);
    popup.add(menuItem);
    menuItem = new JMenuItem("Создать продукт");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_INSERT, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(NEW_PROD);
    popup.add(menuItem);
    popup.addSeparator();

    menuItem = new JMenuItem("Удалить продукт");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(DELETE_PROD);
    popup.add(menuItem);

    prodList.addMouseListener(new PopupListener(popup));
//////////////////////////////////////
//////Создаем зону сложного продукта//
//////////////////////////////////////
     JPanel top = new JPanel(new BorderLayout());
     top.add(bar,BorderLayout.NORTH);
     top.add(new JScrollPane(prodList),BorderLayout.CENTER);
    
      //Здесь формируем зону сложного продукта
      bar = new JToolBar();
      bar.add(makeButton("Sum","SM",SUM_WEIGHT_CMPL,
              "Просуммировать вес сложного продукта"));
      btn = makeButton("Pan","FlM",FILL_MENU_CMPL,
              "Заполнить меню сложным продуктом");
      bar.add(btn);
      bar.addSeparator();

      btnMakeEmptyCmpl = makeButton("NewCmpl","NC",CREATE_EMPTY_CMPL_PROD,
              "Создать новый пустой сложный продукт");
      bar.add(btnMakeEmptyCmpl);
      bar.addSeparator();
      
      editCmplBtn = new JToggleButton();
      editCmplBtn.setMaximumSize(btn.getPreferredSize());
      editCmplBtn.setToolTipText("Редактирование сложного продукта");
      setActionMap(editCmplBtn);
      
      //Look for the image.
      String imgLocation =  "buttons/" + 
                settings.getIn().getSizedPath(true) + 
                "edit" +
                ".png";
      URL imageURL = MainFrame.class.getResource(imgLocation);
  
      String altText = "EC";
      if (imageURL != null) {                      //image found
            editCmplBtn.setIcon(new ImageIcon(imageURL, altText));
      } else {                                     //no image found
         editCmplBtn.setText(altText);
          System.err.println("Resource not found:");
      }
      editCmplBtn.addItemListener(this);
      editCmplBtn.setEnabled(false);
        /////////
      bar.add(editCmplBtn);
      bar.addSeparator();
      bar.add(makeButton("Delete","Dl",DELETE_CMPL,
              "Удалить позицию"));
      bar.setFloatable(false);
       
      JPanel topCmpl = new JPanel(new GridBagLayout()); 
     
      topCmpl.add(new JLabel("Наименование"),
                 new GridBagConstraints(0,0,1,1,0.8,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,new Insets(0,2,0,2), 0,0));
      topCmpl.add(new JLabel("Вес"),
                 new GridBagConstraints(1,0,1,1,0.15,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,new Insets(0,2,0,2), 0,0));
      topCmpl.add(new JLabel("ГИ"),
                 new GridBagConstraints(3,0,1,1,0.1,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,new Insets(0,2,0,2), 0,0));
      
        
      MyFocusListener fcLsn = new MyFocusListener(){
          @Override
            public void focusLost(FocusEvent arg0) {
                if (arg0.getSource()==fldCmplName)
                {
                   if (editedCmplProd!=null){
                       if (!fldCmplName.getText().equals(editedCmplProd.getName())){
                           calcCmpl();
                       }
                   }
                }
            }
      };
      
      CmplPropertyChanged cmplPrListener = new CmplPropertyChanged();
      fldCmplName = new JTextField(10);
      
      fldCmplName.setActionCommand(BEGIN_EDIT_CMPL);
      fldCmplName.addFocusListener(fcLsn);
      fldCmplName.addActionListener(this);
      fldCmplName.setInputVerifier(new SpaceVerifier());
      fldCmplName.setEditable(false);
      setActionMap(fldCmplName);
      
      topCmpl.add(fldCmplName,
                 new GridBagConstraints(0,1,1,1,0.8,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,new Insets(0,2,4,2), 0,0));
      
        
      fldCmplWeight = new JFormattedTextField(df0);
      fldCmplWeight.setEditable(false);
      fldCmplWeight.addFocusListener(fcLsn);
      fldCmplWeight.setInputVerifier(new PositiveFloatVerifier(true));
      fldCmplWeight.addPropertyChangeListener("value", cmplPrListener);

      fldCmplWeight.setTransferHandler(new FormattedFieldImportHandler());

      setActionMap(fldCmplWeight);
      topCmpl.add(fldCmplWeight,
                 new GridBagConstraints(1,1,1,1,0.15,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,new Insets(0,2,4,2), 0,0));
      
      topCmpl.add(new JLabel("г."),
                 new GridBagConstraints(2,1,1,1,0.05,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,new Insets(0,0,4,0), 0,0));
      
      fldCmplGi = new JFormattedTextField(df);
      fldCmplGi.setEditable(false);
      
      fldCmplGi.addFocusListener(fcLsn);
      fldCmplGi.setInputVerifier( new IntVerifier(0,100) );
      fldCmplGi.addPropertyChangeListener("value", cmplPrListener);
      setActionMap(fldCmplGi);
      topCmpl.add(fldCmplGi,
                 new GridBagConstraints(3,1,1,1,0.1,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,new Insets(0,2,4,2), 0,0));
       
      JPanel botCmpl = new JPanel(new GridBagLayout());
        
            Insets insets = new Insets(1,2,1,2);
            Border brdr = BorderFactory.createLineBorder(Color.GRAY);
            
            botCmpl.add(new JLabel("на 100 гр."),
                 new GridBagConstraints(0,0,1,1,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            botCmpl.add(new JLabel("Всего"),
                 new GridBagConstraints(0,1,1,1,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            botCmpl.add(new JLabel("Б"),
                 new GridBagConstraints(1,0,1,2,0.15,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            
            lblProt = new JLabel("===");
            lblProt.setBorder(brdr);
            lblProt.setHorizontalAlignment(SwingConstants.CENTER);
            botCmpl.add(lblProt,
                 new GridBagConstraints(2,0,1,1,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            
            lblProtAll = new JLabel("===");
            lblProtAll.setBorder(brdr);
            lblProtAll.setHorizontalAlignment(SwingConstants.CENTER);
            botCmpl.add(lblProtAll,
                 new GridBagConstraints(2,1,1,2,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            botCmpl.add(new JLabel("%"),
                 new GridBagConstraints(3,0,1,2,0.1,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            botCmpl.add(new JLabel("г."),
                 new GridBagConstraints(3,1,1,2,0.1,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            botCmpl.add(new JLabel("Ж"),
                 new GridBagConstraints(4,0,1,2,0.15,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            
            lblFat = new JLabel("===");
            lblFat.setBorder(brdr);
            lblFat.setHorizontalAlignment(SwingConstants.CENTER);
            botCmpl.add(lblFat,
                 new GridBagConstraints(5,0,1,1,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            
            lblFatAll = new JLabel("===");
            lblFatAll.setBorder(brdr);
            lblFatAll.setHorizontalAlignment(SwingConstants.CENTER);
            botCmpl.add(lblFatAll,
                 new GridBagConstraints(5,1,1,1,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            botCmpl.add(new JLabel("%"),
                 new GridBagConstraints(6,0,1,1,0.1,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            botCmpl.add(new JLabel("г."),
                 new GridBagConstraints(6,1,1,1,0.1,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            botCmpl.add(new JLabel("У"),
                 new GridBagConstraints(7,0,1,2,0.15,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            
            lblCarb = new JLabel("===");
            lblCarb.setBorder(brdr);
            lblCarb.setHorizontalAlignment(SwingConstants.CENTER);
            botCmpl.add(lblCarb,
                 new GridBagConstraints(8,0,1,1,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            
            lblCarbAll = new JLabel("===");
            lblCarbAll.setBorder(brdr);
            lblCarbAll.setHorizontalAlignment(SwingConstants.CENTER);
            botCmpl.add(lblCarbAll,
                 new GridBagConstraints(8,1,1,1,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            botCmpl.add(new JLabel("%"),
                 new GridBagConstraints(9,0,1,1,0.1,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            botCmpl.add(new JLabel("г."),
                 new GridBagConstraints(9,1,1,1,0.1,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            botCmpl.add(new JLabel("ГИ"),
                 new GridBagConstraints(10,0,1,2,0.15,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
            
            lblGI = new JLabel("==");
            lblGI.setBorder(brdr);
            lblGI.setHorizontalAlignment(SwingConstants.CENTER);
            botCmpl.add(lblGI,
                 new GridBagConstraints(11,0,1,2,0.5,0,GridBagConstraints.BELOW_BASELINE,
                 GridBagConstraints.HORIZONTAL,insets, 0,0));
            
        
         //Делаем таблицу сложного продукта
         ProductInBase pr;
         if (prodList.getRowCount()>0){
            pr = ((ProductTableModel)prodList.getModel())
                 .getProduct(prodList.convertRowIndexToModel(prodList.getSelectedRow()));
         } else{
             pr = new ProductInBase();
         }                 
         cmplList = new JTable(new ComplexTableModel(cmplMgr,pr.getId()));
         cmplList.setDefaultRenderer(Float.class, floatRenderer);
         cmplList.getColumnModel().getColumn(1)
                 .setCellEditor(createCellFloatEditor(cmplList));
         cmplList.setAutoCreateRowSorter(true);//дополняем сортировку
         cmplList.setFillsViewportHeight(true);//заполняем всю область
         cmplList.setRowHeight(settings.getIn().getSizedValue(cmplList.getRowHeight()));
         cmplList.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
         cmplList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

         cmplList.getModel().addTableModelListener(this);

         cmplList.setDropMode(DropMode.INSERT_ROWS);
         cmplList.setTransferHandler(new CmplProdImportHandler());

         cmplList.getColumnModel().getColumn(0).setPreferredWidth(settings.getIn().getSizedValue(350));


         if (cmplList.getModel().getRowCount()>0) cmplList.setRowSelectionInterval(0, 0);
         
         /*cmplList.getModel().addTableModelListener(new TableModelListener(){
            @Override
          public void tableChanged(TableModelEvent ev){
            AbstractTableModel mod = (AbstractTableModel)ev.getSource();
                if (ev.getType()==TableModelEvent.UPDATE &&
                    ev.getFirstRow()<(mod.getRowCount()-1) ){
                    cmplList.scrollRectToVisible(cmplList.getCellRect(cmplList.getSelectedRow()+1,
                        cmplList.getSelectedColumn(), true));
                }
             }
         });*/

         cmplList.addFocusListener( new FocusAdapter(){
            @Override//устанавливаем выделение только на первый столбец
        public void focusGained(FocusEvent e){
              if (e.getSource() instanceof JTable){
                  JTable tb = (JTable)e.getSource();
                  if (tb.getRowCount()>0)
                        if (tb.getSelectionModel().isSelectionEmpty()){
                            tb.setRowSelectionInterval(0, 0);
                            tb.setColumnSelectionInterval(1, 1);
                        }else{
                            tb.setColumnSelectionInterval(1, 1);
                        }
              }
            }
        });
         
        
        cmplList.addKeyListener(new KeyAdapter(){
            @Override
           public void keyReleased(KeyEvent e){//DELETE_CMPL
               if (e.getModifiersEx()==0){
                if (e.getKeyCode()==KeyEvent.VK_DELETE){
                   deleteProdFromCmpl();
                }else if (e.getKeyCode()==KeyEvent.VK_F4){
                   if (editCmplBtn.isEnabled()){
                        editCmplBtn.setSelected(!editCmplBtn.isSelected());
                   }
                }
               }
           }
        });
        
        AbstractAction playASenter = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ev){
                JTable tb = (JTable)ev.getSource();
                if (tb.isEditing()){
                    tb.getCellEditor().stopCellEditing();
                }
                int row = tb.getSelectedRow();
                if (row<(tb.getRowCount()-1)){
                    tb.setRowSelectionInterval(row+1, row+1);
                }
            }
        };
        cmplList.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
        cmplList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
        cmplList.getActionMap().put(PLAY_AS_ENTER, playASenter);

        popup = new JPopupMenu();
        menuItem = new JMenuItem("Просуммировать вес продуктов");
        menuItem.addActionListener(this);
        menuItem.setActionCommand(SUM_WEIGHT_CMPL);
        popup.add(menuItem);
        menuItem = new JMenuItem("Заполнить меню составом сложного продукта");
        menuItem.addActionListener(this);
        menuItem.setActionCommand(FILL_MENU_CMPL);
        popup.add(menuItem);
        popup.addSeparator();
        menuItemEditCmpl = new JMenuItem("Редактировать сложный продукт");
        menuItemEditCmpl.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, 0));
        menuItemEditCmpl.addActionListener(this);
        menuItemEditCmpl.setActionCommand(TOGGLE_EDIT_CMPL);
        menuItemEditCmpl.setEnabled(false);
        popup.add(menuItemEditCmpl);
        popup.addSeparator();

        menuItem = new JMenuItem("Удалить продукт из состава сложного продукта");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(DELETE_CMPL);
        popup.add(menuItem);

        cmplList.addMouseListener(new PopupListener(popup));

        setActionMap(cmplList);
            
        JPanel panCmpl = new JPanel(new BorderLayout());
        panCmpl.add(topCmpl,BorderLayout.NORTH);
        panCmpl.add(new JScrollPane(cmplList),BorderLayout.CENTER);
        panCmpl.add(botCmpl,BorderLayout.SOUTH);
        
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setMinimumSize(new Dimension(0,0));
        bottom.add(bar,BorderLayout.NORTH);
        bottom.add(panCmpl,BorderLayout.CENTER);
  
    
    top.setMinimumSize(new Dimension(0,150));
    
    splitProds = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           top, bottom);
    
    splitProds.setOneTouchExpandable(true);
    splitProds.setResizeWeight(1.0);
    splitProds.resetToPreferredSizes();
        
    reloadProducts();
    
     return splitProds;
 }
 
 class CmplPropertyChanged implements PropertyChangeListener {
     private final Calculator calculator;
     
     public CmplPropertyChanged(){
        calculator = new Calculator();
     }
     @Override
     public void propertyChange(PropertyChangeEvent pr){
         if (pr.getPropertyName().equals("value")){
             if (pr.getSource()==fldCmplWeight){
                 JFormattedTextField ftf = (JFormattedTextField)pr.getSource();
                 ftf.setText(calculator.Calc(ftf.getText(), ftf.getFormatter()));
                 try{
                    ftf.setValue(((Number)ftf.getFormatter()
                            .stringToValue(ftf.getText())).floatValue());
                 }catch (ParseException pe) {
                    pe.printStackTrace();
                 }
             }
                calcCmpl();
         }
     }
 }

 private JPanel createGroupPane(){
     JPanel grpPane = new JPanel(new BorderLayout());
     grpPane.setPreferredSize(new Dimension(settings.getIn().getSizedValue(200), 0));
     
      JToolBar bar = new JToolBar();
          
       bar.add(makeButton("New","NW",NEW_GR,"Создать группу"));
       bar.add(makeButton("Edit","ED",EDIT_GR,"Изменить наименование группы"));
    
       bar.addSeparator();
       
       bar.add(makeButton("Up","UP",MOVE_GR_UP,"Переместить группу вверх"));
       bar.add(makeButton("Down","DWN",MOVE_GR_DOWN,"Переместить группу вниз"));
    
       bar.addSeparator();
       
       bar.add(makeButton("Delete","Dl",DELETE_GR,"Удалить группу"));
    
       bar.setFloatable(false);
    
    //Тут добавляем список и label для групп
    
    
    // Создаем визуальный список и вставляем его в скроллируемую
    // панель, которую в свою очередь уже кладем на панель left
       grpList = new JTable(new GroupTableModel(groupMgr));
       grpList.setFillsViewportHeight(true);
       grpList.setRowHeight(settings.getIn().getSizedValue(grpList.getRowHeight()));
       
       grpList.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
    
       grpList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
           @Override
           public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    reloadProducts();
                }
            }});
       grpList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       grpList.setTransferHandler(new GroupMoveHandler());
       grpList.setDropMode(DropMode.ON);
       
       if (grpList.getRowCount()>0) grpList.setRowSelectionInterval(0, 0);
    
       grpList.addKeyListener(new KeyAdapter(){
           @Override
           public void keyReleased(KeyEvent e){
               if (e.getModifiersEx()==0){
                if (e.getKeyCode()==KeyEvent.VK_INSERT){
                   addNewGroup();
                } else if (e.getKeyCode()==KeyEvent.VK_F4){
                   changeGroupName();
                }else if (e.getKeyCode()==KeyEvent.VK_DELETE){
                   deleteGroup();
                }
               }
           }
       });
       
       setActionMap(grpList);

    JPopupMenu popup = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem("Вверх");
    menuItem.addActionListener(this);
    menuItem.setActionCommand(MOVE_GR_UP);
    popup.add(menuItem);
    menuItem = new JMenuItem("Вниз");
    menuItem.addActionListener(this);
    menuItem.setActionCommand(MOVE_GR_DOWN);
    popup.add(menuItem);
    popup.addSeparator();

    menuItem = new JMenuItem("Изменить имя группы");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(EDIT_GR);
    popup.add(menuItem);
    menuItem = new JMenuItem("Создать группу");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_INSERT, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(NEW_GR);
    popup.add(menuItem);
    popup.addSeparator();

    menuItem = new JMenuItem("Удалить группу");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(DELETE_GR);
    popup.add(menuItem);
    
    
    grpList.addMouseListener(new PopupListener(popup));
    
      grpPane.add(bar,BorderLayout.NORTH); //Добавили бар
      grpPane.add(new JScrollPane(grpList),BorderLayout.CENTER);//Добавили панель со списком
 
     
     return grpPane;
 }
 /**
  * creating Cell Editor for weight field
  * @param tb to include the editor
  * @return editor
  */
private FloatEditor createCellFloatEditor(final JTable tb){
    FloatEditor editor = new FloatEditor(
            new JFormattedTextField(formatter0), df0);
    editor.addCellEditorListener(new CellEditorListener() {
         @Override
         public void editingStopped(ChangeEvent e) {
             if (tb.isEditing()){
                 tb.getCellEditor().stopCellEditing();
             }
         }

         @Override
         public void editingCanceled(ChangeEvent e) {}
     });
    
    return editor;
}
/**
 * Создаем панель с таблицей меню
 * @return 
 */
 private JPanel createMenuPane(){
     JPanel menuPane = new JPanel(new BorderLayout());
    //Создаем бар для зоны меню
    JToolBar bar = new JToolBar();
        
    bar.add(makeButton("New","NP",CREATE_PROD_MENU,"Создать новый продукт"));
    bar.add(makeButton("NewCmpl","NC",CREATE_CMPL_PROD_MENU,"Создать новый сложный продукт"));
    
    bar.addSeparator();
    
    bar.add(makeButton("Print","Pr",PRINT_MENU,"Печать меню"));
    
    bar.addSeparator();
    
    bar.add(makeButton("StoreMn","ST",STORE_MENU,"Запомнить меню"));
        
    bar.addSeparator();
    
    bar.add(makeButton("Delete","DL",DELETE_PROD_FROM_MENU,"Удалить продукт из меню"));
    JButton btn = makeButton("Flush","FL",FLUSH_MENU,"Очистить меню");
    Dimension d = btn.getPreferredSize();
    bar.add(btn);
    
    
    SpinnerModel spModel =
        new SpinnerNumberModel(1, //initial value
                               1, //min
                               3, //max
                               1);                //step
    sp = new JSpinner(spModel);
    d.width += 3;
    sp.setMaximumSize(d);
    sp.setValue(settings.getIn().getPrecision());
    sp.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                     calcMenu();
                }
    });
    sp.setToolTipText("Количество знаков после запятой");
    setActionMap(sp);
    bar.add(sp);
    
    bar.setFloatable(false);
    
    JPanel menuTblPane = new JPanel(new BorderLayout());//зона таблицы и бара
    menuPane.add(bar,BorderLayout.NORTH);
        
    JPanel menuCalcPane = new JPanel(new GridBagLayout());//зона расчетов

    JPopupMenu popup = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem("Округлить дозу");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(ROUND_DOSE);
    popup.add(menuItem);
    //popup.addSeparator();

    menuItem = new JMenuItem("Запомнить меню");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(STORE_MENU);
    popup.add(menuItem);
    popup.addSeparator();

    menuItem = new JMenuItem("Удалить продукт");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(DELETE_PROD_FROM_MENU);
    popup.add(menuItem);
    menuItem = new JMenuItem("Очистить меню");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(FLUSH_MENU);
    popup.add(menuItem);
    popup.addSeparator();
    menuItem = new JMenuItem("Печать");
    menuItem.addActionListener(this);
    menuItem.setActionCommand(PRINT_MENU);
    popup.add(menuItem);
    popup.addSeparator();
    menuItem = new JMenuItem("Создать новый продукт");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_INSERT, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(CREATE_PROD_MENU);
    popup.add(menuItem);
    menuItem = new JMenuItem("Создать сложный продукт");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(CREATE_CMPL_PROD_MENU);
    popup.add(menuItem);

    //Add listener to components that can bring up popup menus.
    //MouseListener popupListener = new PopupListener(popup);
    MouseListener popupListener = new PopupListener(popup);

    menuList = new JTable(new MenuTableModel(usersMgr.getUser(currentUser).getId(),
            user.getFactors(),MenuTableModel.MENU_TABLE)){
    //Implement table cell tool tips.
     @Override
     public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        if (rowIndex>=0 && colIndex>=0){
            ProductInMenu prod = ((MenuTableModel)getModel()).getProduct(rowIndex);
            tip = prod.getName() + "\n";
            tip += "Вес: " + df0.format(prod.getWeight()) + "\n" +
                    " Б: " + df0.format(prod.getAllProt()) +
                    " Ж: " + df0.format(prod.getAllFat()) +
                    " У: " + df0.format(prod.getAllCarb()) +
                    " ГИ: " + prod.getGi();
        }
        return tip;
     }
    };

    menuList.setDefaultRenderer(Float.class, floatRenderer);
    menuList.getColumnModel().getColumn(1)
            .setCellEditor(createCellFloatEditor(menuList));
    
    menuList.setAutoCreateRowSorter(false);//дополняем сортировку
    menuList.setFillsViewportHeight(true);//заполняем всю область
    menuList.setRowHeight(settings.getIn().getSizedValue(menuList.getRowHeight()));
    
    menuList.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
    menuList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         
    menuList.getModel().addTableModelListener(this);

    menuList.setDropMode(DropMode.ON_OR_INSERT_ROWS);
    menuList.setTransferHandler(new  MenuProductImportHandler(this));
    menuList.setDragEnabled(true);

    for (int i=0;i<menuList.getColumnCount();i++){
        if (i==0){
            menuList.getColumnModel().getColumn(i).setPreferredWidth(
                    settings.getIn().getMenuNameWidth());
        }
        else if (i==1){
            menuList.getColumnModel().getColumn(i).setPreferredWidth(
                    settings.getIn().getMenuWeightWidth());
        }
        else{
            menuList.getColumnModel().getColumn(i).setPreferredWidth(
                    settings.getIn().getMenuRest());
        }
    }
    menuList.addMouseListener(popupListener);

    FocusAdapter setterFocus = new FocusAdapter(){
        @Override//устанавливаем выделение только на первый столбец
        public void focusGained(FocusEvent e){
              if (e.getSource() instanceof JTable){
                  //if (e.getOppositeComponent()==)
                  JTable tb = (JTable)e.getSource();
                  if (tb.getRowCount()>0)
                        if (tb.getSelectionModel().isSelectionEmpty()){
                            tb.setRowSelectionInterval(0, 0);
                            tb.setColumnSelectionInterval(1, 1);
                        }else{
                            tb.setColumnSelectionInterval(1, 1);
                        }
              }
        }
    };
    menuList.addFocusListener(setterFocus);
    
    final AbstractAction movedown = new AbstractAction(){
            @Override
        public void actionPerformed(ActionEvent ev){
            JTable tb = (JTable)ev.getSource();
            if (tb.isEditing()) tb.getCellEditor().stopCellEditing();
            else if (!tb.getSelectionModel().isSelectionEmpty()){

                int row = tb.getSelectedRow();
                if (row==(tb.getRowCount()-1) && snackList!=null && snackList.getRowCount()>0){
                    int col = tb.getSelectedColumn();
                    snackList.requestFocusInWindow();
                    snackList.setRowSelectionInterval(0, 0);
                    if (col>=0) snackList.setColumnSelectionInterval(col,col);
                    snackList.scrollRectToVisible(snackList.getCellRect(0, 0, true));
                }
                else if (row<(tb.getRowCount()-1)) {
                    tb.setRowSelectionInterval(row+1, row+1);
                    tb.scrollRectToVisible(tb.getCellRect(row+1, 0, true));
                }
            }
        }
    };

    AbstractAction playASenter = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ev){
                JTable tb = (JTable)ev.getSource();
                if (tb.isEditing()){
                    tb.getCellEditor().stopCellEditing();
                }
                int row = tb.getSelectedRow();
                if (row<(tb.getRowCount()-1)){
                    tb.setRowSelectionInterval(row+1, row+1);
                }
                else if (tb==menuList && snackList!=null) movedown.actionPerformed(ev);
            }
    };

    AbstractAction deleteItem = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent ev){
            deleteProdFromMenu();
        }
    };
    AbstractAction flushAll = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent ev){
            flushMenu();
        }
    };
    AbstractAction insertProduct = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent ev){
            //создаем простой продукт
            createProductMenu();
        }
    };
    AbstractAction insertCmplProduct = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent ev){
            //создаем сложный продукт
            addCmplProdMenu();
        }
    };
    AbstractAction roundDose = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent ev){
            roundDose();
        }
    };
    AbstractAction storeMenu = new AbstractAction(){
        @Override
        public void actionPerformed(ActionEvent ev){
            storeMenu();
        }
    };
    menuList.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
    menuList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
    menuList.getActionMap().put(PLAY_AS_ENTER, playASenter);

    menuList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DEL_ITEM_FROM_MENU);
    menuList.getActionMap().put(DEL_ITEM_FROM_MENU, deleteItem);
    menuList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK), FLUSH_FROM_MENU);
    menuList.getActionMap().put(FLUSH_FROM_MENU, flushAll);
    menuList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), INSERT_PROD_FROM_MENU);
    menuList.getActionMap().put(INSERT_PROD_FROM_MENU, insertProduct);
    menuList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK), INSERT_CMPLPROD_FROM_MENU);
    menuList.getActionMap().put(INSERT_CMPLPROD_FROM_MENU, insertCmplProduct);
    menuList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), ROUND_DOSE_MENU);
    menuList.getActionMap().put(ROUND_DOSE_MENU, roundDose);
    menuList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), STORE_MENU_MENU);
    menuList.getActionMap().put(STORE_MENU_MENU, storeMenu);

    
    
    menuList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), MOVE_DOWN_FROM_MENU);
    menuList.getActionMap().put(MOVE_DOWN_FROM_MENU, movedown);

    setActionMap(menuList);
    

    if (settings.getIn().isUseSnack()){
        snackList = new JTable(new MenuTableModel(usersMgr.getUser(currentUser).getId(),
            user.getFactors(),MenuTableModel.SNACK_TABLE)){
            //Implement table cell tool tips.
                @Override
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                if (rowIndex>=0 && colIndex>=0){
                    ProductInMenu prod = ((MenuTableModel)getModel()).getProduct(rowIndex);
                    tip = prod.getName() + "\n";
                    tip += "Вес: " + df0.format(prod.getWeight()) + "\n" +
                        " Б: " + df0.format(prod.getAllProt()) +
                        " Ж: " + df0.format(prod.getAllFat()) +
                        " У: " + df0.format(prod.getAllCarb()) +
                        " ГИ: " + prod.getGi();
                }
                return tip;
            }
        };
        snackList.setDefaultRenderer(Float.class, floatRenderer);
        snackList.getColumnModel().getColumn(1)
                .setCellEditor(createCellFloatEditor(snackList));
        
        snackList.setAutoCreateRowSorter(false);//дополняем сортировку
        snackList.setFillsViewportHeight(true);//заполняем всю область
        snackList.setRowHeight(settings.getIn().getSizedValue(snackList.getRowHeight()));
        snackList.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
        snackList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        snackList.getModel().addTableModelListener(this);
        
        snackList.setDropMode(DropMode.ON_OR_INSERT_ROWS);
        snackList.setTransferHandler(new  MenuProductImportHandler(this));
        snackList.setDragEnabled(true);
        snackList.addMouseListener(popupListener);
        snackList.addFocusListener(setterFocus);

        snackList.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
        snackList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
        snackList.getActionMap().put(PLAY_AS_ENTER, playASenter);
        snackList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DEL_ITEM_FROM_MENU);
        snackList.getActionMap().put(DEL_ITEM_FROM_MENU, deleteItem);
        snackList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK), FLUSH_FROM_MENU);
        snackList.getActionMap().put(FLUSH_FROM_MENU, flushAll);
        snackList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), INSERT_PROD_FROM_MENU);
        snackList.getActionMap().put(INSERT_PROD_FROM_MENU, insertProduct);
        snackList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK), INSERT_CMPLPROD_FROM_MENU);
        snackList.getActionMap().put(INSERT_CMPLPROD_FROM_MENU, insertCmplProduct);
        snackList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), ROUND_DOSE_MENU);
        snackList.getActionMap().put(ROUND_DOSE_MENU, roundDose);
        snackList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), STORE_MENU_MENU);
        snackList.getActionMap().put(STORE_MENU_MENU, storeMenu);

        AbstractAction moveup = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ev){
                JTable tb = (JTable)ev.getSource();
                if (tb.isEditing()) tb.getCellEditor().stopCellEditing();
                else if (!tb.getSelectionModel().isSelectionEmpty()){
                    int row = tb.getSelectedRow();
                    if (row==0 && menuList.getRowCount()>0){
                        int col = tb.getSelectedColumn();
                        menuList.requestFocusInWindow();
                        menuList.setRowSelectionInterval(menuList.getRowCount()-1,
                                menuList.getRowCount()-1);
                        if (col>=0) menuList.setColumnSelectionInterval(col,col);
                        menuList.scrollRectToVisible(menuList.getCellRect(menuList.getRowCount()-1, 0, true));
                    }
                    else if (row>0){
                        tb.setRowSelectionInterval(row-1, row-1);
                        tb.scrollRectToVisible(tb.getCellRect(row-1, 0, true));
                    }
                }
            }
        };
        snackList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), MOVE_UP_FROM_MENU);
        snackList.getActionMap().put(MOVE_UP_FROM_MENU, moveup);

        setActionMap(snackList);

        for (int i=0;i<snackList.getColumnCount();i++){
            if (i==0){
                snackList.getColumnModel().getColumn(i).setPreferredWidth(
                        settings.getIn().getMenuNameWidth());
            }
            else if (i==1){
                snackList.getColumnModel().getColumn(i).setPreferredWidth(
                        settings.getIn().getMenuWeightWidth());
            }
            else{
                snackList.getColumnModel().getColumn(i).setPreferredWidth(
                        settings.getIn().getMenuRest());
            }
        }
        menuList.addMouseListener(new MouseAdapter(){
          @Override
          public void mouseClicked(MouseEvent e) {
             if (SwingUtilities.isLeftMouseButton(e) &&
                     e.getClickCount() % 2 == 0 && menuList.getSelectedRow()>=0)  {
                 //System.out.println( "row="+menuList.getSelectedRow() );
                 ((MenuTableModel)snackList.getModel()).addProduct(
                         new ProductW(((MenuTableModel)menuList.getModel()).getProductAtRow(
                         menuList.getSelectedRow()))
                         );
                 }
          }
        });

        menuList.getColumnModel().addColumnModelListener(new TableColumnModelListener(){
                @Override
            public void columnAdded(TableColumnModelEvent e){}
                @Override
            public void columnRemoved(TableColumnModelEvent e){}
                @Override
            public void columnMoved(TableColumnModelEvent e){}
                @Override
            public void columnMarginChanged(ChangeEvent e){
                for (int i=0;i<snackList.getColumnCount();i++){
                    snackList.getColumnModel().getColumn(i).setPreferredWidth(
                            menuList.getColumnModel().getColumn(i).getWidth());
                }
            }
                @Override
            public void columnSelectionChanged(ListSelectionEvent e){}
        });
        menuList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
                @Override
            public void valueChanged(ListSelectionEvent e){
                if (!menuList.getSelectionModel().isSelectionEmpty()){
                    snackList.getSelectionModel().clearSelection();
                }
            }
        });
        snackList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
                @Override
            public void valueChanged(ListSelectionEvent e){
                if (!snackList.getSelectionModel().isSelectionEmpty()){
                    menuList.getSelectionModel().clearSelection();
                }
            }
        });
        
    }else{
        //Перекус не используем, почистим таблицу на всякий случай
        new MenuManager(user.getId(),MenuManager.SNACK_TABLE).flush();
    }
    if (snackList!=null){
        FocusAdapter ad =  new FocusAdapter(){
                @Override
            public void focusGained(FocusEvent e){
                if (e.getSource()==menuList) snackList.clearSelection();
                else if (e.getSource()==snackList) menuList.clearSelection();
               
           }
        };
        menuList.addFocusListener(ad);
        snackList.addFocusListener(ad);
    }
    
    JComponent menuTableZone;
    if (snackList!=null){
        JPanel menu = new JPanel(new BorderLayout());
        JPanel snack = new JPanel(new BorderLayout());
        menu.add(new JScrollPane(menuList),BorderLayout.CENTER);
        snack.add(new JScrollPane(snackList),BorderLayout.CENTER);

        if (settings.getIn().isCarbRatio()){
            carbRatioMenu = new JProgressBar();
            Color bgr = carbRatioMenu.getBackground();
            carbRatioMenu.setPreferredSize(new Dimension(Short.MAX_VALUE,settings.getIn().getSizedValue(15)));
            carbRatioMenu.setStringPainted(true);
            carbRatioMenu.setOrientation(JProgressBar.HORIZONTAL);
            carbRatioMenu.setForeground(ALEX_GREEN);
            
            Color uiBgr = UIManager.getColor("ProgressBar.selectionBackground");
            Color uiFgr = UIManager.getColor("ProgressBar.selectionForeground");
            UIManager.put("ProgressBar.selectionBackground", uiFgr);
            UIManager.put("ProgressBar.selectionForeground", uiBgr);

            carbRatioSnack = new JProgressBar();
            carbRatioSnack.setPreferredSize(new Dimension(Short.MAX_VALUE,settings.getIn().getSizedValue(15)));
            carbRatioSnack.setOrientation(JProgressBar.HORIZONTAL);
            carbRatioSnack.setForeground(bgr);
            carbRatioSnack.setStringPainted(true);
             
            carbRatioSnack.setBackground(ALEX_GREEN);

            menu.add(carbRatioMenu,BorderLayout.SOUTH);
            snack.add(carbRatioSnack,BorderLayout.SOUTH);
        }
        
        menu_snack = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                menu,
                snack);
        menu_snack.setOneTouchExpandable(false);
        menu_snack.setDividerLocation(settings.getIn().getSnackSize());
        menuTableZone = menu_snack;
    }
    else menuTableZone = new JScrollPane(menuList);
    menuTblPane.add(menuTableZone,BorderLayout.CENTER);
    
    menuPane.add(menuTblPane,BorderLayout.CENTER);
    

    menuCalcPane.add(createCoefPane(),
            new GridBagConstraints(0,0,2,1,0.8,0.3,GridBagConstraints.CENTER,
                 GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0), 0,0));
    menuCalcPane.add(createDosesPane(),
            new GridBagConstraints(0,1,1,1,0.6,0.7,GridBagConstraints.CENTER,
                 GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0), 0,0));                 
    menuCalcPane.add(createCharPane(),
            new GridBagConstraints(1,1,1,2,0.2,0.7,GridBagConstraints.CENTER,
                 GridBagConstraints.BOTH,new Insets(0,0,0,0), 0,0));
    menuCalcPane.add(createSumDataPane(),
            new GridBagConstraints(2,0,1,2,0.05,1,GridBagConstraints.CENTER,
                 GridBagConstraints.VERTICAL,new Insets(0,0,0,0), 0,0));

    menuPane.add(menuCalcPane,BorderLayout.SOUTH);
    
    if (settings.getIn().getSize()==3) menuPane.setPreferredSize(
            new Dimension(settings.getIn().getSizedValue(320),settings.getIn().getSizedValue(600)));
    else menuPane.setPreferredSize(new Dimension(settings.getIn().getSizedValue(350),settings.getIn().getSizedValue(600)));
    return menuPane;
 }
 private JComponent createSumDataPane(){
     //Градусник калорийности
     JPanel pane = new JPanel(new BorderLayout());
     JButton btnAddCalor = makeButton("CalorPlus","+",ADD_CALORIES,
             "Добавить калорийность еды");
     Dimension d;
     if (settings.getIn().getSize()==4) 
         d = new Dimension(settings.getIn().getSizedValue(24),
                 settings.getIn().getSizedValue(24));
     else d = new Dimension(settings.getIn().getSizedValue(18),
             settings.getIn().getSizedValue(18));
     btnAddCalor.setPreferredSize(d);

     caloriesInd = new TwoColorsProgressBar();
     
     int  vl=(int)(100 * user.getEatenFood().getCalories()/user.getCalorLimit());
     caloriesInd.setValue( vl );
     
     //caloriesInd.setBackground(null);
     //caloriesInd.setBackground(Color.RED);
     
     /*JProgressBar indc = new JProgressBar(0,99);
     indc.setOrientation(JProgressBar.VERTICAL);
     indc.setForeground(ALEX_RED);
     indc.setValue(90);*/
     
     
     calorWindow = new CaloriesWindow();
     calorWindow.setData(user);
     this.addPropertyChangeListener(calorWindow);

     caloriesInd.addMouseListener(new MouseAdapter(){
        private boolean isin = false;
         
        @Override
        public void mouseEntered(MouseEvent e){
            isin = true;
            Thread t = new Thread() {
                @Override
                 public void run(){
                    long now = System.currentTimeMillis();
                    while (isin &&
                            ( (System.currentTimeMillis()-now)<500 )){
                    }
                    if (isin){
                        //calorWindow.setVisible(true);
                        int width;
                        if (settings.getIn().getSize()==4) width = 250;
                        else width = 225;
                        calorWindow.setBounds(
                            new Rectangle(caloriesInd.getLocationOnScreen().x+
                                caloriesInd.getSize().width,
                                caloriesInd.getLocationOnScreen().y
                                ,width,caloriesInd.getSize().height ));
                        calorWindow.setVisible(isin);
                    }
                 }
            };
            t.start();
        }
        
        @Override
        public void mouseExited(MouseEvent e){
            isin = false;
            calorWindow.setVisible(false);
        }
            @Override
            public void mouseClicked(MouseEvent e){
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount()%2==0){
                    if (user.getEatenFood().getCalories()<0.5f) return;//Если при округлении менее 1 калориии, то не добавляем
                    calorWindow.setVisible(false);
                    isin = false;
                    
                    JLabel lblTime = new JLabel("Время");
                    JFormattedTextField fldTime;
                    MaskFormatter mask;
                    try{
                        mask = new MaskFormatter("##:##");
                    }catch (Exception ex){ mask = null; }
                    mask.setPlaceholderCharacter('_');

                    SimpleDateFormat fr = new SimpleDateFormat("HH:mm");

                    fldTime = new JFormattedTextField(mask);
                    Date dt = new Date(user.getEatenTime());
                    fldTime.setValue(fr.format(dt));
                    fldTime.setInputVerifier(new TimeVerifier());

                    JLabel lblDate = new JLabel("Дата");
                    DateField fldDt = new DateField(dt);

                    JLabel lblCom = new JLabel("Коментарий");

                    JTextArea commArea = new JTextArea();
                    commArea.setLineWrap(true);
                    commArea.setWrapStyleWord(true);
                    commArea.setText("Калории - съедено: "+df.format(user.getEatenFood().getCalories())+
                            ", лимит: "+user.getCalorLimit() +
                            ((int)user.getEatenFood().getCalories()>user.getCalorLimit()?
                            ", съедено лишнего: " + ((int)user.getEatenFood().getCalories()-user.getCalorLimit()):
                            ", осталось: " +(user.getCalorLimit()-(int)user.getEatenFood().getCalories()))
                            );

                    JScrollPane sc = new JScrollPane(commArea);
                    sc.setPreferredSize(new Dimension(settings.getIn().getSizedValue(250),
                            settings.getIn().getSizedValue(50)));

                    Object [] arr = {lblTime,fldTime,lblDate,fldDt,lblCom,sc};
                    String [] var = {"Сохранить","Нет"};

                    if(JOptionPane.showOptionDialog(
                                        MainFrame.this,
                                        arr,
                                        "Запись о калорийности",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        var,
                                        var[0])==JOptionPane.OK_OPTION){
                        Date time;
                        try{
                            time = fr.parse((String)fldTime.getValue());
                        }catch (ParseException ex){
                            time = new Date();
                        }

                        long datetime = ( time.getTime() - DiaryJob.BASE)  +
                            fldDt.getDate().getTime();
                        
                        DiaryUnit u = new DiaryUnit(0,datetime,commArea.getText(),
                        user.getId());

                        new DiaryManager(user).addComment(u);
                        MainFrame.this.pcs.firePropertyChange( USER_CHANGED, null, user );
                    }
                }
            }
    });

     JButton btnClearCalor = makeButton("CalorClear","C",CLEAR_CALORIES,
             "Сбросить счетчик");
     btnClearCalor.setPreferredSize(d);
     pane.add(btnAddCalor,BorderLayout.NORTH);
     
     //pane.add(indc,BorderLayout.CENTER);
     
     pane.add(caloriesInd,BorderLayout.CENTER);
     
     
     pane.add(btnClearCalor,BorderLayout.SOUTH);

     
     return pane;
 }

 private JToolBar createMainToolBar(){
     JToolBar bar = new JToolBar();
          
     bar.add(makeButton("Archive","Ar",SHOW_ARC,"Показать архив"));
     bar.add(makeButton("Plates","Pl",SHOW_PLATES,"Показать посуду"));
     bar.add(makeButton("Coefs","Cf",SHOW_COEFS,"Показать коэффициенты"));
    
     bar.addSeparator();
     
     bar.add(makeButton("Diary","RS",SHOW_DIARY,"Дневник"));
     bar.add(makeButton("Calcs","Clcs",SHOW_CALCS_CALOR,"Различные расчеты"));
     bar.add(makeButton("InetBKP","BKP",SHOW_INET_BACKUP,
             "Сохранение и восстановление данных в интернет"));
     bar.addSeparator();

     bar.add(makeButton("Settings","ST",SHOW_SETTINGS,"Настройки программы"));
     bar.addSeparator();
     
     bar.add(makeButton("PrevUSR","PR",PRIOR_USR,"Предыдущий пользователь"));
     
     fldUser = new JTextField();
     fldUser.setText(usersMgr.getUser(currentUser).getName());
     fldUser.setEditable(false);
     fldUser.setMaximumSize(new Dimension(settings.getIn().getSizedValue(150),settings.getIn().getSizedValue(30)));
     fldUser.setPreferredSize(new Dimension(settings.getIn().getSizedValue(150),0));
     fldUser.setBackground(Color.WHITE);
     setActionMap(fldUser);
     
     bar.add(fldUser);
     bar.add(makeButton("NextUSR","NX",NEXT_USR,"Следующий пользователь"));
     bar.addSeparator();
     bar.add(makeButton("NewUSR","NU",NEW_USR,"Новый пользователь"));
     bar.add(makeButton("EditUSR","EU",EDIT_USR,"Редактировать пользователя"));
     bar.addSeparator();
     bar.add(makeButton("DeleteUSR","DU",DELETE_USR,"Удалить пользователя"));

     bar.setFloatable(false);
     
     return bar;
 }
 
 private JMenuBar createMenu(){
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;
    
    //Create the menu bar.
    menuBar = new JMenuBar();

    //Build the first menu.
    menu = new JMenu("Показать");
    menu.setMnemonic('П');
    menuBar.add(menu);
    
    menuItem = new JMenuItem("Архив");
    menuItem.setMnemonic('А');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_ARC);
    menu.add(menuItem);
    menuItem = new JMenuItem("Посуда");
    menuItem.setMnemonic('П');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_PLATES);
    menu.add(menuItem);
    menuItem = new JMenuItem("Коэффициенты");
    menuItem.setMnemonic('К');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_COEFS);
    menu.add(menuItem);
    
    menu = new JMenu("Инструменты");
    menu.setMnemonic('И');
    menuBar.add(menu);
    menuItem = new JMenuItem("Дневник");
    menuItem.setMnemonic('Д');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_DIARY);
    menu.add(menuItem);
    menu.addSeparator();
    menuItem = new JMenuItem("Расчеты: калории и вес");
    menuItem.setMnemonic('в');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_CALCS_CALOR);
    menu.add(menuItem);
    menuItem = new JMenuItem("Расчеты: пересчет крови");
    menuItem.setMnemonic('к');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_CALCS_BLOOD);
    menu.add(menuItem);
    menu.addSeparator();
    menuItem = new JMenuItem("Импорт и экспорт в интернет");
    menuItem.setMnemonic('и');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_INET_BACKUP);
    menu.add(menuItem); 

    menu = new JMenu("Настройка");
    menu.setMnemonic('Н');
    menuBar.add(menu);

    menuItem = new JMenuItem("Настройки");
    menuItem.setMnemonic('Н');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_SETTINGS);
    menu.add(menuItem);

    menuBar.add(menu);

    menu = new JMenu("Справка");
    menu.setMnemonic('С');
    
    menuItem = new JMenuItem("Справка по программе в сети");
    menuItem.setMnemonic('С');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_HELP);
    menu.add(menuItem);
    menu.addSeparator();
    menuItem = new JMenuItem("Лицензия");
    menuItem.setMnemonic('Л');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_LICENSE);
    menu.add(menuItem);
    menuItem = new JMenuItem("О программе");
    menuItem.setMnemonic('О');
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SHOW_ABOUTBOX);
    menu.add(menuItem);
    menuBar.add(menu);
    
    menuBar.add(Box.createHorizontalGlue());
    /*menuExit = new JMenu("Выход");
    menuExit.setMnemonic('В');*/
    
    JButton ex = new JButton("Выход");
    ex.setFocusPainted(false);
    ex.setMargin(new Insets(0, 0, 0, settings.getIn().getSizedValue(5)));
    ex.setContentAreaFilled(false);
    ex.setBorderPainted(false);
    ex.setOpaque(false);
    ex.setActionCommand(PROG_EXIT);
    ex.addActionListener(this);
            
    menuBar.add(ex);
    
    return menuBar;
 }

 @Override
 public void tableChanged(TableModelEvent e) {
     if ( (menuList!=null&&((AbstractTableModel)e.getSource()).equals(menuList.getModel())) ||
             (snackList!=null&&((AbstractTableModel)e.getSource()).equals(snackList.getModel())  ) ){
         calcMenu();
     }else if ( (cmplList!=null&&((AbstractTableModel)e.getSource()).equals(cmplList.getModel()))){
         calcCmpl();
         if (cmplList.getRowCount()>0 && cmplList.getRowSorter()==null){
            cmplList.setRowSorter(cmplRowsorter);
            if (cmplRowsorter!=null) cmplRowsorter.setSortKeys(cmplSortKeys);
         }
     }
    
  }
 //Groups routine

 private void addNewGroup(){
     JLabel lblTitle = new JLabel("Введите название группы:");
     JTextField fldTitle = new JTextField("Новая группа");
     fldTitle.addFocusListener(new MyFocusListener());

     Object[] arr = {lblTitle,fldTitle};
     String [] var = {"Да","Нет"};

   if (JOptionPane.showOptionDialog(    this,
                                        arr,
                                        "Добавление группы",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        var,
                                        var[0])==0){

                    String s = fldTitle.getText();
                    s = s.trim();
                    //If a string was returned, say so.
                    if ((s != null) && (s.length() > 0)) {
                        ((GroupTableModel)grpList.getModel()).addGroup(new ProdGroup(0,s,0));
                        int n = grpList.getModel().getRowCount()-1;
                        Rectangle rect = grpList.getCellRect(n, 0, true);
                        grpList.scrollRectToVisible(rect);
                        grpList.setRowSelectionInterval(n,n);
                    }
                   
   }
 }

 private void changeGroupName(){
     if (!grpList.getSelectionModel().isSelectionEmpty()){
     JLabel lblTitle = new JLabel("Введите новое название группы:");
     JTextField fldTitle = new JTextField(
             (String)((GroupTableModel)grpList.getModel())
                                        .getValueAt(grpList.getSelectedRow(),0)
             );
     fldTitle.addFocusListener(new MyFocusListener());
     Object[] arr = {lblTitle,fldTitle};
     String [] var = {"Да","Нет"};

    if (JOptionPane.showOptionDialog(   this,
                                        arr,
                                        "Изменение названия группы",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        var,
                                        var[0])==0){

                    String s = fldTitle.getText();
                    s = s.trim();

                    //If a string was returned, say so.
                    if ((s != null) && (s.length() > 0)) {
                        ProdGroup gr = ((GroupTableModel)grpList.getModel())
                                .getGroup(grpList.getSelectedRow());
                        if (!s.equals(gr.getName())){
                            gr.setName(s);
                            int n = grpList.getSelectedRow();
                            ((GroupTableModel)grpList.getModel()).updateGroup(gr);
                            grpList.setRowSelectionInterval(n, n);
                        }
                    }
                    
            }
    }
 }

 private void deleteGroup(){
     if (!grpList.getSelectionModel().isSelectionEmpty()){
          boolean doOther = true;
          if ((editedCmplProd!=null) &&( editedCmplProd.getOwner()==
                 ((GroupTableModel)grpList.getModel())
                 .getGroup(grpList.getSelectedRow()).getId() )){
              doOther = false;
          }
          if (doOther){
          Object[] options = {"Да","Нет" };
          if (JOptionPane.showOptionDialog(this,
                                    "Вы собираетесь удалить группу:\n\n" +
                                    (String)grpList.getModel().getValueAt(grpList.getSelectedRow(),0)+
                                    "\n\n" +
                                    "Будут удалены все, входящие в нее, продукты\n"+
                                    "Удалить?",
                                    "Удаление группы",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[1]) == JOptionPane.YES_OPTION) {
                        ProdGroup gr = ((GroupTableModel)grpList.getModel())
                                .getGroup(grpList.getSelectedRow());
                        int pos = grpList.getSelectedRow();
                        ((GroupTableModel)grpList.getModel()).deleteGroup(gr);
                        if (grpList.getModel().getRowCount()>0)
                            if (pos>0) grpList.setRowSelectionInterval(pos-1, pos-1);
                            else grpList.setRowSelectionInterval(0, 0);

                    }
          }
     }

 }


 //Products routine
 private void addNewProd(){
    if (!grpList.getSelectionModel().isSelectionEmpty() &&
        ((GroupTableModel)grpList.getModel()).getGroup(grpList.getSelectedRow()).getId()>
             (settings.getIn().getUseUsageGroup()-1)){
        prodDialog = new ProdDialog(this,null,groupMgr
             .getGroups(0),grpList.getSelectedRow(),ProdDialog.NEW_PRODUCT);
     
        prodDialog.setModal(true);
        prodDialog.setVisible(true);
        if (prodDialog.getResult()){
             ProductInBase newProd = prodDialog.getProduct();
         
            ((ProductTableModel)prodList.getModel()).addProduct(newProd);
            
            grpList.setRowSelectionInterval(prodDialog.getSelectedGroupIndex()+
                    settings.getIn().getUseUsageGroup(),
                 prodDialog.getSelectedGroupIndex()+settings.getIn().getUseUsageGroup());
            int row = prodList.convertRowIndexToView(((ProductTableModel)prodList
                 .getModel()).findRow(prodMgr.getLastInsertedId()));
            
            Rectangle rect = prodList.getCellRect(row, 0, true);
            //System.out.println(row+" "+rect);
            prodList.scrollRectToVisible(rect);
            
            if (row>=0) prodList.setRowSelectionInterval(row, row);
         
        }
     }
     
 }
 
 private void editProduct(){
     if (!grpList.getSelectionModel().isSelectionEmpty()){
         if ((editedCmplProd!=null)&&(editedCmplProd.getId()==((ProductTableModel)prodList.getModel()).getProduct(
             prodList.convertRowIndexToModel(prodList.getSelectedRow())
             ).getId())){
            return;
         }
         ProductInBase prod = null;
         if (settings.getIn().getUseUsageGroup()==1 &&
                 ((GroupTableModel)grpList.getModel()).getGroup(grpList.getSelectedRow()).getId()==0){
                     //Пытаемся редактировать в частоиспользуемых => нужно выделить нужную группу
                     prod = ((ProductTableModel)prodList.getModel())
                       .getProduct(prodList.convertRowIndexToModel(prodList.getSelectedRow()));
                     int grRow = grpList.convertRowIndexToView(
                             ((GroupTableModel)grpList.getModel()).findRow(prod.getOwner()));
                     grpList.setRowSelectionInterval(grRow, grRow);
                     grpList.scrollRectToVisible(grpList.getCellRect(grRow, 0, false));
                     int prRow = prodList.convertRowIndexToView(
                             ((ProductTableModel)prodList.getModel()).findRow(prod.getId()));
                     prodList.setRowSelectionInterval(prRow, prRow);
                     prodList.scrollRectToVisible(prodList.getCellRect(prRow, 0, false));
                 }
     
        if (prod==null) prod = ((ProductTableModel)prodList.getModel())
             .getProduct(prodList.convertRowIndexToModel(prodList.getSelectedRow()));
      
       int mode;
       if (prod.isComplex()) mode = ProdDialog.EDIT_COMPLEX;
       else mode = ProdDialog.EDIT_PRODUCT;
        prodDialog = new ProdDialog(this,prod,groupMgr
             .getGroups(0),grpList.getSelectedRow(),mode);
     
        prodDialog.setModal(true);
        prodDialog.setVisible(true);
        if (prodDialog.getResult()){
            //int pos = prodList.convertRowIndexToModel(prodList.getSelectedRow());
            prod = prodDialog.getProduct();
            ((ProductTableModel)prodList.getModel()).updateProduct(prod);

            int grRow = grpList.convertRowIndexToView(
                             ((GroupTableModel)grpList.getModel()).findRow(prod.getOwner()));
            grpList.setRowSelectionInterval(grRow, grRow);
            grpList.scrollRectToVisible(grpList.getCellRect(grRow, 0, false));
            int prRow = prodList.convertRowIndexToView(
                             ((ProductTableModel)prodList.getModel()).findRow(prod.getId()));
            prodList.setRowSelectionInterval(prRow, prRow);
            prodList.scrollRectToVisible(prodList.getCellRect(prRow, 0, false));
            /*


            int r = prodDialog.getSelectedGroupIndex()+
                    settings.getIn().isUseUsageGroup();
            grpList.setRowSelectionInterval(r,r);//выделяем группу,
                    //т.к. группа при редактировании могла сменится
            
            
            prodList.setRowSelectionInterval(pos, pos);*/
        }
      
     
     }
 }
 
 private void deleteProduct(){
     if (!grpList.getSelectionModel().isSelectionEmpty()){
         if ((editedCmplProd!=null)&&(editedCmplProd.getId()==
             ((ProductTableModel)prodList.getModel()).getProduct(
             prodList.convertRowIndexToModel(prodList.getSelectedRow())
             ).getId() ) ){
             return;
         }
         ProductInBase prod = null;
         if (settings.getIn().getUseUsageGroup()==1 &&
                 ((GroupTableModel)grpList.getModel()).getGroup(grpList.getSelectedRow()).getId()==0){
                     //Пытаемся редактировать в частоиспользуемых => нужно выделить нужную группу
                     prod = ((ProductTableModel)prodList.getModel())
                       .getProduct(prodList.convertRowIndexToModel(prodList.getSelectedRow()));
                     int grRow = grpList.convertRowIndexToView(
                             ((GroupTableModel)grpList.getModel()).findRow(prod.getOwner()));
                     grpList.setRowSelectionInterval(grRow, grRow);
                     grpList.scrollRectToVisible(grpList.getCellRect(grRow, 0, false));
                     int prRow = prodList.convertRowIndexToView(
                             ((ProductTableModel)prodList.getModel()).findRow(prod.getId()));
                     prodList.setRowSelectionInterval(prRow, prRow);
                     prodList.scrollRectToVisible(prodList.getCellRect(prRow, 0, false));
                 }
     
     
     Object[] options = {"Да", "Нет"};
     if (JOptionPane.showOptionDialog(this,
                                    "Вы собираетесь удалить продукт:\n\n" +
                                    ((ProductTableModel)prodList.getModel())
                       .getProduct(prodList.convertRowIndexToModel(prodList.getSelectedRow())).getName()+
                                    
                                    "\n\nБудут удалены все, входящие в него, продукты\nУдалить?",
                                    "Удаление продукта",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[1]) == JOptionPane.YES_OPTION) {
                        
                        int pos = prodList.convertRowIndexToModel(prodList.getSelectedRow());
                        if (prod==null) prod = ((ProductTableModel)prodList.getModel())
                                .getProduct(pos);
                        ((ProductTableModel)prodList.getModel()).deleteProduct(prod);
                        if (prodList.getModel().getRowCount()>0){
                            if (pos==0) pos = 1;
                            prodList.setRowSelectionInterval(pos-1, pos-1);
                        }
                    }
     }
  
 }

 private void addProdToMenu(){
     if ((prodList.getRowCount()>0)&&
             (!prodList.getSelectionModel().isSelectionEmpty()))
     {
     ProductInBase prod = ((ProductTableModel)prodList.getModel()).getProduct(
             prodList.convertRowIndexToModel(prodList.getSelectedRow()));
     prod.setUsage(prod.getUsage()+1);
     prodMgr.updateProductInBase(prod);
     ((MenuTableModel)menuList.getModel()).addProduct(new ProductW(prod));
     prodList.requestFocusInWindow();
     }
 }
 private void addProdToSnack(){
     if ((prodList.getRowCount()>0)&&
             (!prodList.getSelectionModel().isSelectionEmpty()) && snackList!=null)
     {
     ProductInBase prod = ((ProductTableModel)prodList.getModel()).getProduct(
             prodList.convertRowIndexToModel(prodList.getSelectedRow()));
     prod.setUsage(prod.getUsage()+1);
     prodMgr.updateProductInBase(prod);
     ((MenuTableModel)snackList.getModel()).addProduct(new ProductW(prod));
     prodList.requestFocusInWindow();
     }
 }
 private void flushMenu(){
     int rowscount = 0;
     if (menuList.getModel().getRowCount()>0 && !menuList.isEditing()){
         rowscount = menuList.getModel().getRowCount();
         ((MenuTableModel)menuList.getModel()).flushMenu();
     }
     if (snackList!=null && snackList.getModel().getRowCount()>0 && !snackList.isEditing()){
         rowscount += snackList.getModel().getRowCount();
         ((MenuTableModel)snackList.getModel()).flushMenu();
     }
     if ( rowscount>0 && 
             Math.abs(((Number)fldSh1.getValue()).floatValue()-
               ((Number)fldSh2.getValue()).floatValue())>1e-4 ){
         //Числа различаются
         Object[] options = {"Да","Нет" };
         if (JOptionPane.showOptionDialog(this,
                                    "Стартовый и целевой СК различаются\n" +
                                    "Установить на значение целевого СК?",
                                    "СКстарт<>СКцель",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[0]) == JOptionPane.YES_OPTION) {
              fldSh1.setValue(user.getTargetSh().getSugar(user.isMmol(), user.isPlasma()));
              fldSh2.setValue(user.getTargetSh().getSugar(user.isMmol(), user.isPlasma()));
          }
     }
     //тут еще нужно выбирать коэф-т по времени
     if (rowscount>0 && user.isTimeSense()){
         factorsChooser.setForeground(Color.BLACK);
         if (factorsChooser.getItemCount()>0){
             Date now = new Date();
             SimpleDateFormat hour_format = new SimpleDateFormat("H");
             int hour = Integer.parseInt(hour_format.format(now));
             SimpleDateFormat min_format = new SimpleDateFormat("m");
             int min = Integer.parseInt(min_format.format(now));
             if (min>29) hour++;
             int pos = hour - 6;
             if (pos<0) pos += 24;
             factorsChooser.setSelectedIndex(pos);
             CoefsSet cf = (CoefsSet)factorsChooser.getItemAt(factorsChooser.getSelectedIndex());
            user.getFactors().setK1XE(cf.getK1(), user.getFactors().getBE(user.isDirect()), user.isDirect());
            user.getFactors().setK2(cf.getK2());
            Sugar s = new Sugar();
            s.setSugar(cf.getK3(), user.isMmol(), user.isPlasma());
            user.getFactors().setK3(s.getValue());
            usersMgr.updateFactors(user);
         }
    }
 }
 
 private void createProductMenu(){
  if (!grpList.getSelectionModel().isSelectionEmpty())
  if ( ((GroupTableModel)grpList.getModel()).getGroup(grpList.getSelectedRow()).getId()==0 )
  {  
     grpList.setRowSelectionInterval(1, 1);
  }
     if (grpList.getRowCount()==0){
         JOptionPane.showMessageDialog(this, "Должна существовать хотя бы одна группа");
     }
     else if (((MenuTableModel)menuList.getModel()).getSumProd().getWeight()==0){
         JOptionPane.showMessageDialog(this, "Вес компонентов не может быть равен нулю");
     }
     else{
         ProductW prodW = ((MenuTableModel)menuList.getModel()).getSumProd();
         ProductInBase prod = new ProductInBase(
                 prodW.getName(),
                 prodW.getProt(),
                 prodW.getFat(),
                 prodW.getCarb(),
                 prodW.getGi(),
                 prodW.getWeight(),
                 0,
                 false,
                 ((GroupTableModel)grpList.getModel()).getGroup(
                    grpList.getSelectedRow()
                 ).getId(),
                 0
                 );
         prodDialog = new ProdDialog(this,prod,groupMgr
                 .getGroups(0),grpList.getSelectedRow(),ProdDialog.NEW_PRODUCT);
     
     prodDialog.setModal(true);
     prodDialog.setVisible(true);
     if (prodDialog.getResult()){
         ProductInBase newProd = prodDialog.getProduct();
         
         ((ProductTableModel)prodList.getModel()).addProduct(newProd);
         grpList.setRowSelectionInterval(prodDialog.getSelectedGroupIndex()+
                 settings.getIn().getUseUsageGroup(),
                 prodDialog.getSelectedGroupIndex()+settings.getIn().getUseUsageGroup());
         int row = prodList.convertRowIndexToView(((ProductTableModel)prodList
                 .getModel()).findRow(prodMgr.getLastInsertedId()));
         Rectangle rect = prodList.getCellRect(row, 0, true);
         prodList.scrollRectToVisible(rect);
         if (prodList.getRowCount()>0) prodList.setRowSelectionInterval(0, 0);
         
     }
     }
 }
 
 private void addCmplProdMenu(){
  if (!grpList.getSelectionModel().isSelectionEmpty())
  if ( ((GroupTableModel)grpList.getModel()).getGroup(grpList.getSelectedRow()).getId()==0 )
  {
      grpList.setRowSelectionInterval(1, 1);
  }
     if (grpList.getRowCount()==0){
         JOptionPane.showMessageDialog(this, "Должна существовать хотя бы одна группа");
     }
     else{
         ProductW prodW = ((MenuTableModel)menuList.getModel()).getSumProd();
         ProductInBase prod =  new ProductInBase(
                 prodW.getName(),
                 prodW.getProt(),
                 prodW.getFat(),
                 prodW.getCarb(),
                 prodW.getGi(),
                 prodW.getWeight(),
                 0,
                 true,//сложный продукт
                 ((GroupTableModel)grpList.getModel()).getGroup(
                    grpList.getSelectedRow()
                 ).getId(),
                 0
                 );
        if (prod.getName().length()==0)  prod.setName("Новый продукт");
        prodDialog = new ProdDialog(this,prod,groupMgr
                .getGroups(0),grpList.getSelectedRow(),ProdDialog.NEW_COMPLEX);
     
        prodDialog.setModal(true);
        prodDialog.setVisible(true);
        if (prodDialog.getResult()){
            ProductInBase newProd = prodDialog.getProduct();
         
            ((ProductTableModel)prodList.getModel()).addProduct(newProd);
            int Owner = prodMgr.getLastInsertedId();
            Collection cmpls = new ArrayList();
            for (int i=0;i<menuList.getRowCount();i++){
                cmpls.add( ((MenuTableModel)menuList.getModel()).getProductAtRow(i) );
            }
            
            cmplMgr.addComplexProducts(cmpls, Owner);
            grpList.setRowSelectionInterval(prodDialog.getSelectedGroupIndex()+
                    settings.getIn().getUseUsageGroup(),
                 prodDialog.getSelectedGroupIndex()+settings.getIn().getUseUsageGroup());
            int row = prodList.convertRowIndexToView(  
                    ((ProductTableModel)prodList.getModel()).findRow(Owner) );
            Rectangle rect = prodList.getCellRect(row, 0, true);
            prodList.scrollRectToVisible(rect);
            prodList.setRowSelectionInterval(row, row);
        }
     }
  
 }
 
 private void deleteProdFromMenu(){
     if ( !menuList.getSelectionModel().isSelectionEmpty() && !menuList.isEditing() ){
           int row = menuList.getSelectedRow();
           ((MenuTableModel)menuList.getModel()).deleteProduct(
             ((MenuTableModel)menuList.getModel()).getProduct(menuList.getSelectedRow()));
           if (menuList.getRowCount()>0){
               row = row<menuList.getRowCount()?row:(menuList.getRowCount()-1);
               menuList.setRowSelectionInterval(row, row);
               menuList.setColumnSelectionInterval(1, 1);
           }

     }else if ( snackList!=null && !snackList.getSelectionModel().isSelectionEmpty() &&
             !snackList.isEditing()){
         int row = snackList.getSelectedRow();
        ((MenuTableModel)snackList.getModel()).deleteProduct(
             ((MenuTableModel)snackList.getModel()).getProduct(snackList.getSelectedRow()));
        if (snackList.getRowCount()>0){
               row = row<snackList.getRowCount()?row:(snackList.getRowCount()-1);
               snackList.setRowSelectionInterval(row, row);
               snackList.setColumnSelectionInterval(1, 1);
        }
     }
 }
 
 private void nextUser(){
     if (usersMgr.getAmount()>(currentUser+1)) {
        ++currentUser;
        userChanged();
     }
 }
 private void priorUser(){
     if (currentUser>0 ){
        --currentUser;
        userChanged();
     }
 }
 private void newUser(){
     UserDialog usrDlg = new UserDialog(this,null);
     usrDlg.setModal(true);
     usrDlg.setVisible(true);
     if (usrDlg.getResult()){
         User us = usrDlg.getUser();
         usersMgr.addUser(us);
         currentUser = usersMgr.getAmount()-1;
         userChanged();
     }
 }
 private void editUser(){
     User us = usersMgr.getUser(currentUser);
     
     UserDialog usrDlg = new UserDialog(this,us);
     usrDlg.setModal(true);
     usrDlg.setVisible(true);
     if (usrDlg.getResult()){
         us = usrDlg.getUser();
         usersMgr.updateUser(us);
         userChanged();
     }
 }
 private void deleteUser(){
     if (usersMgr.getAmount()>1){
         User us = usersMgr.getUser(currentUser);
         Object[] options = {"Да","Нет"};
         int n = JOptionPane.showOptionDialog(this,
                                    "Вы собираетесь удалить пользователя:\n\n" +
                                    us.getName() +
                                    "\n\nУдалить?",
                                    "Удаление пользователя",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[1]);
                    if (n == JOptionPane.YES_OPTION) {
                        usersMgr.deleteUser(us);
                        if (currentUser>0)  priorUser();
                        else userChanged();
                    }
     }
     
 }
 
 public void userChanged(){
     //Вот тут мы должны загрузить всю информацию по пользователю
     //изменить все зависимые поля и таблицы
     if (menuList!=null)
         menuList.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
     if (snackList!=null)
         snackList.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();

     int oldId = -1;
     if (user!=null) oldId = user.getId();
     user = usersMgr.getUser(currentUser);
     
     if (lblXE!=null){
             if (user.isDirect())
                 lblXE.setText("<html>Кол. инс.<br>на k1 гр.</html>");
             else
                 lblXE.setText("Вес ХЕ");
     }

     if (fldUser!=null) fldUser.setText(user.getName());
     this.setTitle(PROGRAM_VERSION+" " + user.getName());
     
             
     currDPS = new DPS(user.getTargetSh(),user.getTargetSh(),user.getFactors());
     //К этому времени уже должна существовать таблица, поэтому вначале проверяем
     //если нет, то инициализацию переменных делаем в блоке создания таблицы
     storeCoefs = false;
     if (oldId!=user.getId()){//Тут надо просто очистить список и заполнить его
         //новыми значениями в том случае, если пользователь действительно сменился
         if (factorsChooser!=null){
             //Все заполенение делаем тут
                factorsChooser.removeAllItems();
                CoefsManager cfMgr = new CoefsManager(user);
                ArrayList<CoefsSet> cfs = new ArrayList(cfMgr.getCoefs());
                for (CoefsSet item:cfs){
                    item.setK3(new Sugar(item.getK3()).getSugar(user.isMmol(),user.isPlasma()));
                    Factors f = new Factors(
                            item.getK1(),
                            item.getK2(),
                            item.getK3(),
                            user.getFactors().getBEValue());
                    item.setK1(f.getK1(user.isDirect()));
                }
                if (cfs.isEmpty() && user.isTimeSense()){
                    user.setTimeSense(false);
                    usersMgr.updateUser(user);
                }
                if (user.isTimeSense()){
                    //Тут вычисляем время и подставляем нужные коэф-ты
                    TimedCoefsSet timedCfs = new TimedCoefsSet(cfs);
                    ArrayList v = new ArrayList(timedCfs.getTimedCoefs());
                    for (Object item:v){
                        factorsChooser.addItem(item);
                    }
                }
                else{
                    for (Object item:cfs){
                        factorsChooser.addItem(item);
                    }
                }
           
             if (factorsChooser.getItemCount()>0 && user.isTimeSense()){
                Date now = new Date();
                SimpleDateFormat hour_format = new SimpleDateFormat("H");
                int hour = Integer.parseInt(hour_format.format(now));
                SimpleDateFormat min_format = new SimpleDateFormat("m");
                int min = Integer.parseInt(min_format.format(now));
                if (min>29) hour++;
                int pos = hour - 6;
                if (pos<0) pos += 24;
                factorsChooser.setSelectedIndex(pos);
                //CoefsSet cf = (CoefsSet)factorsChooser.getItemAt(factorsChooser.getSelectedIndex());
                /*user.getFactors().setK1XE(cf.getK1(), user.getFactors().getBE(user.isDirect()), user.isDirect());
                user.getFactors().setK2(cf.getK2());
                Sugar s = new Sugar();
                s.setSugar(cf.getK3(), user.isMmol(), user.isPlasma());
                user.getFactors().setK3(s.getValue());
                usersMgr.updateFactors(user);*/
            }

            //factorsChooser.setForeground(Color.GRAY);
         }
       
     }
     storeCoefs = false;
     if (factorsChooser!=null && factorsChooser.getItemCount()>0 && factorsChooser.getSelectedIndex()<0){
         factorsChooser.setSelectedIndex(0);
         factorsChooser.setForeground(Color.GRAY);
     }
     
     if (menuList!=null){
        fldK1.setValue(user.getFactors().getK1(user.isDirect()));
        fldK2.setValue(user.getFactors().getK2());
        fldK3.setValue(new Sugar(user.getFactors().getK3()).getSugar(user.isMmol(),user.isPlasma()));
        fldXE.setValue(user.getFactors().getBE(user.isDirect()));
        fldSh1.setValue(user.getSh1().getSugar(user.isMmol(),user.isPlasma()));
        fldSh2.setValue(user.getSh2().getSugar(user.isMmol(),user.isPlasma()));
        ((MenuTableModel)menuList.getModel()).userChanged(user.getId());
        if (snackList!=null) ((MenuTableModel)snackList.getModel()).userChanged(user.getId());
     }
     storeCoefs = true;
     if (fldK1!=null)
         fldK1.firePropertyChange(VALUE_CHANGED, -100f, user.getFactors().getK1(user.isDirect()));
     if (caloriesInd!=null){
        int  vl=(int)(100 * user.getEatenFood().getCalories()/user.getCalorLimit());
        caloriesInd.setValue( vl );
     }
     this.pcs.firePropertyChange( USER_CHANGED, null, user );

 }
 
 
 private void fillMenuWithCmpl(){
     if (cmplList.getRowCount()>0){
         int res = JOptionPane.NO_OPTION;
         if (menuList.getRowCount()>0){
            Object[] options = {"Да","Нет","Отмена"};
            res = JOptionPane.showOptionDialog(this, 
                    "Меню не пустое\nОчистить?",
                 "", 
                 JOptionPane.YES_NO_CANCEL_OPTION,
                 JOptionPane.PLAIN_MESSAGE,
                 null,
                 options,
                 options[0]);
         }   
         if (res!=JOptionPane.CANCEL_OPTION){
            if (res==JOptionPane.YES_OPTION){
                    ((MenuTableModel)menuList.getModel()).flushMenu();
                    if (snackList!=null) ((MenuTableModel)snackList.getModel()).flushMenu();
            }
            for (int i=0;i<cmplList.getRowCount();i++){
                ((MenuTableModel)menuList.getModel()).insertProduct(
                     new ProductW((ProductW)((ComplexTableModel)cmplList.getModel()).getProduct(i))
                     );
            }
            prodList.requestFocusInWindow();
         }
     }
 }
 
 private void moveGroupUp(){
      //Передвигаем группу вверх
if ((grpList.getRowCount()>2)&&(grpList.getSelectedRow()>0))
 {
  int pos = grpList.getSelectedRow();
  ProdGroup grLo = ((GroupTableModel)grpList.getModel()).getGroup(pos);
  
  ProdGroup grHi = ((GroupTableModel)grpList.getModel()).getGroup(pos-1);
  //Изменяем верхнюю группу
  groupMgr.updateGroup(new ProdGroup(grHi.getId(),grHi.getName(),grLo.getSortInd()),
          settings.getIn().getUseUsageGroup());
  //Изменяем нижнюю групп
  groupMgr.updateGroup(new ProdGroup(grLo.getId(),grLo.getName(),grHi.getSortInd()),
          settings.getIn().getUseUsageGroup());
  
  ((GroupTableModel)grpList.getModel()).reloadGroups();
  grpList.setRowSelectionInterval(pos-1, pos-1);

 }
 }
 
 private void moveGroupDown(){
 if ((grpList.getRowCount()>2)&&(grpList.getSelectedRow()<(grpList.getRowCount()-1)))
 {
  int pos = grpList.getSelectedRow();
  ProdGroup grHi = ((GroupTableModel)grpList.getModel()).getGroup(pos);
  
  ProdGroup grLo = ((GroupTableModel)grpList.getModel()).getGroup(pos+1);
  //Изменяем верхнюю группу
  groupMgr.updateGroup(new ProdGroup(grHi.getId(),grHi.getName(),grLo.getSortInd()),
          settings.getIn().getUseUsageGroup());
  //Изменяем нижнюю групп
  groupMgr.updateGroup(new ProdGroup(grLo.getId(),grLo.getName(),grHi.getSortInd()),
          settings.getIn().getUseUsageGroup());
  
  ((GroupTableModel)grpList.getModel()).reloadGroups();
  grpList.setRowSelectionInterval(pos+1, pos+1);

 }
 }
 
 @Override
 public void itemStateChanged(ItemEvent e) {
     if (e.getStateChange()==ItemEvent.DESELECTED) {
         if (cmplList.isEditing()) cmplList.getCellEditor().stopCellEditing();
         btnMakeEmptyCmpl.setEnabled(true);
         editCmplBtn.setEnabled(false);
         int row;
         if (((GroupTableModel)grpList.getModel()).getGroup(grpList.getSelectedRow()).getId()!=
                 editedCmplProd.getOwner()){
             row = ((GroupTableModel)grpList.getModel())
                     .findRow(editedCmplProd.getOwner());
             if (row>=0) grpList.setRowSelectionInterval(row, row);

         }
         row = prodList.convertRowIndexToView(((ProductTableModel)prodList
                 .getModel()).findRow(editedCmplProd.getId()));
         Rectangle rect = prodList.getCellRect(row, 0, true);
         prodList.scrollRectToVisible(rect);
         if (row>=0) prodList.setRowSelectionInterval(row, row);

         editedCmplProd = null;
         fldCmplName.setEditable(false);
         fldCmplWeight.setEditable(false);
         fldCmplGi.setEditable(false);
         ((ComplexTableModel)cmplList.getModel()).setWeightEditable(false);
         ((ComplexTableModel)cmplList.getModel()).setEditable(false);
         reloadComplex();
     }
     else{
         editedCmplProd = ((ProductTableModel)prodList.getModel())
                 .getProduct(prodList.convertRowIndexToModel(prodList.getSelectedRow()));
         btnMakeEmptyCmpl.setEnabled(false);
         fldCmplName.setEditable(true);
         fldCmplWeight.setEditable(true);
         fldCmplGi.setEditable(true);
         fldCmplName.setText(editedCmplProd.getName());
         fldCmplWeight.setValue(editedCmplProd.getWeight());
         fldCmplGi.setValue(editedCmplProd.getGi());
         ((ComplexTableModel)cmplList.getModel()).setWeightEditable(true);
         ((ComplexTableModel)cmplList.getModel()).setEditable(true);
         calcCmpl();//вначале пересчитаем и заполним lbl значениями
     }
 }
 
 private void calcCmpl(){
     if (editedCmplProd!=null){
        ProductW sum;
        if (cmplList.getRowCount()>0) sum = ((ComplexTableModel)cmplList.getModel()).getSumProd();
        else sum = new ProductW();
        sum.changeWeight( ((Number)fldCmplWeight.getValue()).floatValue() );
        sum.setGi( ((Number)fldCmplGi.getValue()).intValue() );
        sum.setName( fldCmplName.getText() );
        
        if (!((ProductW)editedCmplProd).equals(sum)){
            editedCmplProd.setName(fldCmplName.getText());
            editedCmplProd.setProt(sum.getProt());
            editedCmplProd.setFat(sum.getFat());
            editedCmplProd.setCarb(sum.getCarb());
            editedCmplProd.setGi(sum.getGi());
            editedCmplProd.setWeight( ((Number)fldCmplWeight.getValue()).floatValue() );
            
            prodMgr.updateProductInBase(editedCmplProd);
            
            lblProt.setText(df0.format(sum.getProt()));
            lblProtAll.setText(df0.format(sum.getAllProt()));
             
            lblFat.setText(df0.format(sum.getFat()));
            lblFatAll.setText(df0.format(sum.getAllFat()));
             
            lblCarb.setText(df0.format(editedCmplProd.getCarb()));
            lblCarbAll.setText(df0.format(editedCmplProd.getAllCarb()));
             
            lblGI.setText(""+sum.getGi());
                
            if (  ((GroupTableModel)grpList.getModel())
                    .getGroup(grpList.getSelectedRow()).getId()==
                    editedCmplProd.getOwner()){
                ((ProductTableModel)prodList.getModel())
                        .reloadProducts(editedCmplProd.getOwner());
                int row = prodList.convertRowIndexToView( 
                        ((ProductTableModel)prodList.getModel())
                        .findRow(editedCmplProd.getId())
                        );
                prodList.setRowSelectionInterval(row, row);
            } 
        }
        
     }
    
 }
 
 private void deleteProdFromCmpl(){
     if ( (editedCmplProd!=null) && (cmplList.getRowCount()>0) && 
             (!cmplList.getSelectionModel().isSelectionEmpty())){
         /*if (cmplList.getRowCount()==1){//Остался последний продукт, предупреждаем
             JOptionPane.showMessageDialog(this, "Сложный продукт должен содержать\n"
                     + "хотя бы один продукт в своем составе", 
                     "Ошибка", 
                     JOptionPane.ERROR_MESSAGE);
             
             return;
         }*/
         if (cmplList.isEditing()) cmplList.getCellEditor().stopCellEditing();
         ((ComplexTableModel)cmplList.getModel()).deleteProduct(
                 ((ComplexTableModel)cmplList.getModel()).getProduct(
                 cmplList.convertRowIndexToModel(cmplList.getSelectedRow())
                 ));
     }
 }
 
 private void summingCmpl(){
     if (editedCmplProd!=null){
        ProductW sum = ((ComplexTableModel)cmplList.getModel()).getSumProd();
        editedCmplProd.changeWeight(sum.getWeight());
        fldCmplWeight.setValue(editedCmplProd.getWeight());
     }
 }
 
 public boolean canEditCmpl(){
      return editedCmplProd != null;
 }
 
 private void beginSearch(){
     ((CardLayout)productPanel.getLayout()).show(productPanel,SEARCH_PANE);
     
     searchFld.selectAll();
     searchFld.requestFocusInWindow();
 }
 
 private void cancelSearch(){
     ((CardLayout)productPanel.getLayout()).show(productPanel,PROD_PANE);
 }
 
 private void doSearch(){
     ((SearchTableModel)searchTable.getModel()).doSearch(searchFld.getText());
 }
 
 private void doSelectionSearched(){
     int sel = searchTable.getSelectedRow();
     if (sel<0) return;
     int grInx = ((SearchTableModel)searchTable.getModel()).getGroup(sel).getId();
     int prInx = ((SearchTableModel)searchTable.getModel()).getProduct(sel).getId();
     int grRow;
     int prRow;
     if ( (grRow = ((GroupTableModel)grpList.getModel()).findRow(grInx))>=0 ){
            grpList.setRowSelectionInterval(grRow, grRow);
            if (  (prRow = prodList
                    .convertRowIndexToView(((ProductTableModel)prodList
                    .getModel()).findRow(prInx)) )>=0 ){
                
                Rectangle rect = prodList.getCellRect(prRow, 0, true);
                prodList.scrollRectToVisible(rect);
                prodList.setRowSelectionInterval(prRow, prRow);
            }
            
     }
     cancelSearch();
 }
 
 private void showCoefsDlg(){
     if (coefJobDlg == null){
         SwingUtilities.invokeLater(new Runnable(){
                @Override
             public void run(){
                coefJobDlg = new CoefJob(MainFrame.this,user);
                MainFrame.this.addPropertyChangeListener(coefJobDlg);
                coefJobDlg.setVisible(true);
             }
         });
     }
     else{
         coefJobDlg.setVisible(true);
     }
 }
 
 public JTable getProdTable(){
     return prodList;
 }

 public void setCoefSets(Collection c){
     storeCoefs = false;
     
     factorsChooser.removeAllItems();
     Object [] csAr;
     if (user.isTimeSense()){
         TimedCoefsSet timedCfs = new TimedCoefsSet(c);
         csAr = timedCfs.getTimedCoefs().toArray();
     }else csAr = c.toArray();
     for (Object item:csAr){
         factorsChooser.addItem(item);
     }

     factorsChooser.setForeground(Color.GRAY);

     storeCoefs = true;
 }

 
 public User setBE(User newUser,boolean shouldReCalc){
     //переделать на изменение данных пользователя и отсюда вызывать userChanged
     float newK1 = user.getFactors().getK1(user.isDirect());
     if (shouldReCalc){
         newK1 = ((Number)fldK1.getValue()).floatValue() * 
                    newUser.getFactors().getBE(newUser.isDirect()) /
                        ((Number)fldXE.getValue()).floatValue();
         fldK1.setValue(newK1);
     }
     user.getFactors().setK1XE(newK1,newUser.getFactors().getBE(newUser.isDirect()), user.isDirect());
     usersMgr.updateFactors(user);
     fldXE.setValue(user.getFactors().getBE(user.isDirect()));
     calcMenu();
     
     return user;
 }
 public User changeDirection(User newUser){
     user.setDirect(newUser.isDirect());
     usersMgr.updateFactors(user);
     fldXE.setValue(user.getFactors().getBE(user.isDirect()));
     fldK1.setValue(user.getFactors().getK1(user.isDirect()));
     if (user.isDirect()) lblXE.setText("<html>Кол. инс.<br>на k1 гр.</html>");
     else lblXE.setText("Вес ХЕ");
     return user;
 }
 public void calcOUV(){
     float k1_10;
     if (user.isDirect()){
          k1_10 = 10f/user.getFactors().getK1(user.isDirect());
     } else{
          k1_10 = user.getFactors().getK1(user.isDirect()) * 10f /
                                user.getFactors().getBE(user.isDirect());
     }
     fldK3.setValue(new Sugar(user.getOUVcoef() / (user.getWeight()*k1_10))
                                .getSugar(user.isMmol(), user.isPlasma()));
 }

 public User updateOUVcoef(User newUser){
     user = newUser;
     usersMgr.updateUser(user);
     return user;

 }
 private void showAboutBox(){
     AboutBox about = new AboutBox(this);
     about.setModal(true);
     about.setVisible(true);
 }
 private void showLicenseBox(){
     LicenseBox licenseBox = new LicenseBox(this);
     licenseBox.setModal(true);
     licenseBox.setVisible(true);
 }
 public User getUser(){
     return user;
 }
 private void showSettings(){
     SettingsFrame set = new SettingsFrame(this);
     set.setModal(true);
     set.setVisible(true);
     if (set.getResult()){
         settings.getIn().setUseSnack(set.useSnack());
         settings.getIn().setSize(set.getProgSize());
         settings.getIn().setUsageGroupCount(set.getUsageGroupCount());
         if (settings.getIn().getUseUsageGroup()!=set.useUsageGroup()){
            settings.getIn().setUseUsageGroup(set.useUsageGroup());
            ((GroupTableModel)grpList.getModel()).reloadGroups();
            grpList.repaint();
            grpList.setRowSelectionInterval(0, 0);
         }
         if (settings.getIn().getMenuMask()!=set.getMenuMask()){
             settings.getIn().setMenuMask(set.getMenuMask());
             ((MenuTableModel)menuList.getModel()).changeStructure();
             menuList.getColumnModel().getColumn(1)
                    .setCellEditor(createCellFloatEditor(menuList));
             
             if (snackList!=null){
                 ((MenuTableModel)snackList.getModel()).changeStructure();
                 snackList.getColumnModel().getColumn(1)
                         .setCellEditor(createCellFloatEditor(snackList));
             }
         }
         settings.getIn().setProductOnce(set.isProductOnce());
         settings.getIn().setCoefsLocked(set.lockCoefsFields());
         fldK1.setEditable(settings.getIn().isCoefsLocked());
         fldK2.setEditable(settings.getIn().isCoefsLocked());
         fldK3.setEditable(settings.getIn().isCoefsLocked());
         settings.getIn().setCalcOUVbyK1(set.getShouldCalcOUVbyK1());
         settings.getIn().setRoundLimit(set.getRoundLimit());
         settings.getIn().setCarbRatio(set.showCarbRatio());
         settings.getIn().setVacuum(set.shouldVacuum());
     }
 }
 public String getMenuDescription(){
     String res = "";
     ProductW sum = new ProductW();

     df_prec = new DecimalFormat(precisionPattern[((Number)sp.getValue()).intValue()-1]);

     if (menuList.getRowCount()>0 || (snackList!=null && snackList.getRowCount()>0)){
         sum = ((MenuTableModel)menuList.getModel()).getSumProd();
         if (snackList!=null)
             sum.plusProd(((MenuTableModel)snackList.getModel()).getSumProd());
         int max = 12;//Наименование
         String buf;
         for (int i=0;i<menuList.getRowCount();i++){
             buf  = (String)menuList.getValueAt(i, 0);
             max = buf.length()>max ? buf.length() : max;
         }
         if (snackList!=null)
            for (int i=0;i<snackList.getRowCount();i++){
                buf  = (String)snackList.getValueAt(i, 0);
                max = buf.length()>max ? buf.length() : max;
            }
         res = "Меню" + getSt(max+25,'~') + "\n";
         for(int i=0;i<menuList.getColumnCount();i++){
             buf = ((MenuTableModel)menuList.getModel()).getPureColumnName(i);
             res += buf;
             if (i==0) res += getSt(max-buf.length(),' ');
             res += "\t";
         }
         res +="\n";
         for(int r=0;r<menuList.getRowCount();r++){
             for(int c=0;c<menuList.getColumnCount();c++){
                 Object obj = menuList.getValueAt(r, c);
                 if (obj instanceof String){
                     buf = (String)menuList.getValueAt(r, c);
                     res += buf;
                     if (c==0) res += getSt(max-buf.length(),' ');
                 }else
                 if (obj instanceof Float){
                     res += df0.format(((Number)obj).floatValue());
                 }else
                 if (obj instanceof Integer){
                     res += obj.toString();
                 }
                 res += "\t";
             }
             res += "\n";
         }
         if (snackList!=null && snackList.getRowCount()>0){
             res += "Перекус" + getSt(max+22,'~') + "\n";
             for(int r=0;r<snackList.getRowCount();r++){
                for(int c=0;c<snackList.getColumnCount();c++){
                    Object obj = snackList.getValueAt(r, c);
                    if (obj instanceof String){
                        buf = (String)snackList.getValueAt(r, c);
                        res += buf;
                        if (c==0) res += getSt(max-buf.length(),' ');
                    }else
                    if (obj instanceof Float){
                        res += df0.format(((Number)obj).floatValue());
                    }else
                    if (obj instanceof Integer){
                        res += obj.toString();
                    }
                    res += "\t";
                }
                res += "\n";
            }
         }
         res += getSt(max+29,'~')+"\n";
         res += "Кал:"+df0.format(sum.getCalories())+
                    " Б:"+df0.format(sum.getAllProt()) +
                    " Ж:"+df0.format(sum.getAllFat()) +
                    " У:"+df0.format(sum.getAllCarb()) +
                    " ГИ:"+sum.getGi() +
                    " ГН:"+df0.format(sum.getGL()) +"\n";
         res += getSt(max+29,'~') + "\n";
         res += "k1=" + df00.format(user.getFactors().getK1(user.isDirect())) +
                 " k2=" + df00.format(user.getFactors().getK2());
         if (!user.isDirect()) res += " ХЕ="+df0.format(user.getFactors().getBE(user.isDirect()));
         if (currDPS.getSh1().getValue()!=currDPS.getSh2().getValue()){
             res += " СКнач=" + df0.format(currDPS.getSh1()
                        .getSugar(user.isMmol(), user.isPlasma())) +
                    " СКцель=" + df0.format(currDPS.getSh2()
                        .getSugar(user.isMmol(), user.isPlasma())) +
                    " ЦЕИ=" + df00.format(
                                new Sugar(user.getFactors().getK3())
                                .getSugar(user.isMmol(), user.isPlasma()));
         }
         res += "\n" + getSt(max+29,'~') + "\n";
         Dose ds = new Dose(sum,user.getFactors(),currDPS);
         res += "ДПС+БД=" + df_prec.format(ds.getDPSDose()+ds.getCarbFastDose())
                 + " МДугл+МДбел/ж=" +
                df_prec.format(ds.getCarbSlowDose()+ds.getSlowDose()) +
                " Вся доза =" + df_prec.format(ds.getWholeDose()) + "\n";
     }
     return res;
  
 }
 private String getSt(int n,char c){
        String st = "";
        for(int i=0;i<n;i++){
            st += c;
        }
        return st;
 }

 private void roundDose(){
     if ( !menuList.getSelectionModel().isSelectionEmpty() ||
             (snackList!=null && !snackList.getSelectionModel().isSelectionEmpty() )){
         ProductW prod;
         ProductInMenu prodMenu;
         if (menuList.getSelectionModel().isSelectionEmpty()){
             prod = ((MenuTableModel)snackList.getModel())
                     .getProductAtRow(snackList.getSelectedRow());
             prodMenu = ((MenuTableModel)snackList.getModel())
                     .getProduct(snackList.getSelectedRow());
         } else{
             prod = ((MenuTableModel)menuList.getModel())
                     .getProductAtRow(menuList.getSelectedRow());
             prodMenu = ((MenuTableModel)menuList.getModel())
                     .getProduct(menuList.getSelectedRow());
         }
         ProductW sum = ((MenuTableModel)menuList.getModel()).getSumProd();
         if (snackList!=null) sum.plusProd(((MenuTableModel)snackList
                 .getModel()).getSumProd());
         Dose ds_now = new Dose(sum,user.getFactors(),currDPS);
         ProductW prod100 = new ProductW(prod);
         prod100.setWeight(prod.getWeight()+100f);
         
         double dose_diff = new Dose(prod100,user.getFactors(),currDPS).getWholeDose() -
                    new Dose(prod,user.getFactors(),currDPS).getWholeDose();
         
         double frac = ds_now.getWholeDose() - Math.floor(ds_now.getWholeDose());
         double step;
         switch (settings.getIn().getRoundLimit()){
             case 0: step = 1;
                        break;
                case 1: step = 0.5;
                        break;
                case 2: step = 0.25;
                        break;
                case 3: step = 1/6;//NovoPen 3
                        break;
                case 4: step = 0.5/6;//NovoPen Demi
                        break;
                case 5: step = 0.05;
                        break;
                default: step = 1;
         }
         double i = 0;
         while (i<frac)
                i += step;
         double upDiff = Math.floor( ds_now.getWholeDose() ) + i -
                 (double)ds_now.getWholeDose();
         double downDiff = (double)ds_now.getWholeDose() -
                 Math.floor( ds_now.getWholeDose() ) - (i-step);

          double wUp = upDiff * 100 / dose_diff;
          double wDown = downDiff * 100 / dose_diff;
          
          Object[] options = {"+"+df00.format(wUp)+" г.",
                                    "Отмена",
                                    "-"+df00.format(wDown)+" г."};
          int n = JOptionPane.showOptionDialog(this,
                                    "Округлить дозу по весу продукта\n"+
                                    prod.getName() +
                                    "\n\nПредел округления: " + 
                                    ROUNDS[settings.getIn().getRoundLimit()],
                                    "Округление дозы",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[1]);
                    if (n == JOptionPane.YES_OPTION) {//Ответили, что увеличиваем
                        prodMenu.setWeight(prodMenu.getWeight()+(float)wUp);
                        if (menuList.getSelectionModel().isSelectionEmpty()){
                            ((MenuTableModel)snackList.getModel())
                                    .updateProduct(prodMenu);
                            
                        } else{
                            ((MenuTableModel)menuList.getModel())
                                    .updateProduct(prodMenu);
                        }
                    }
                    else if  (n == JOptionPane.CANCEL_OPTION) {//Ответили, что уменьшаем
                        prodMenu.setWeight(prodMenu.getWeight()-(float)wDown);
                        if (menuList.getSelectionModel().isSelectionEmpty()){
                            ((MenuTableModel)snackList.getModel())
                                    .updateProduct(prodMenu);

                        } else{
                            ((MenuTableModel)menuList.getModel())
                                    .updateProduct(prodMenu);
                        }
                    }
     }
 }
 private void addCalories(){
     Date now = new Date(System.currentTimeMillis());
     if (user.getEatenFood().getCalories()>0){
        Date date = new Date(user.getEatenTime());
        SimpleDateFormat dt_fr = new SimpleDateFormat("yyyy.MM.dd"); //"yyyy.MM.dd"
        if (!dt_fr.format(date).equals(dt_fr.format(now))){
          Object[] options = {"Да", "Нет"};
          int n = JOptionPane.showOptionDialog(this,
                                    "Очистить счетчик?",
                                    "",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[0]);
                    if (n == JOptionPane.YES_OPTION){
                        clearCalories();
                    }
        }
     }
     ProductW prod = ((MenuTableModel)menuList.getModel()).getSumProd();
     if (snackList!=null) prod.plusProd(((MenuTableModel)snackList.getModel()).getSumProd());
     prod.plusProd(user.getEatenFood());
     

     user.setEatenFood(prod);
     user.setEatenTime(now.getTime());

     usersMgr.updateFood(user);
     int  vl=(int)(100 * user.getEatenFood().getCalories()/user.getCalorLimit());
     caloriesInd.setValue( vl );
     //caloriesInd.getUI();
     calorWindow.setData(user);
 }
 private void clearCalories(){
     user.setEatenFood(new ProductW());
     user.setEatenTime(0);
     usersMgr.updateFood(user);
     caloriesInd.setValue( 0 );
     calorWindow.setData(user);
 }
 private void showPlates(){
     if (plate==null){
         SwingUtilities.invokeLater(new Runnable(){
                @Override
             public void run(){
                plate = new PlateDlg();
                plate.setVisible(true);
             }
         });
     }
     else plate.setVisible(true);
 }
 private void storeMenu(){
     //Если в данный момент сохраняют значения коэ-оф и сахаров, 
     //то ничего не делаем
     if (propertiesAreBeeignChanged) return;
     //Если меню пустое, то ничего не деаем.
     if ((menuList.getRowCount()+(snackList==null?0:snackList.getRowCount()))==0){
         return;
     }
     Date now = new Date();
     SimpleDateFormat fr = new SimpleDateFormat("HH:mm");

     int h = Integer.parseInt(new SimpleDateFormat("HH").format(now));
     String comment;
     if (h>=4 && h<6) comment = "Ранний завтрак";
     else if (h>=6 && h<11) comment = "Завтрак";
     else if (h>=11 && h<14) comment = "Обед";
     else if (h>=14 && h<17) comment = "Ланч";
     else if (h>=17 && h<20) comment = "Ужин";
     else comment = "Поздний ужин";

     JLabel lblTime = new JLabel("Время");

     MaskFormatter mask;
     try{
        mask = new MaskFormatter("##:##");
     }catch (Exception e){ return; }
     mask.setPlaceholderCharacter('_');

     JFormattedTextField fldTime = new JFormattedTextField(mask);

     fldTime.setValue(fr.format(now));
     fldTime.setInputVerifier(new TimeVerifier());

     JLabel lblDate = new JLabel("Дата");
     DateField fldDt = new DateField(now);

     JLabel lblCom = new JLabel("Коментарий");
     JTextArea commArea = new JTextArea();
     commArea.setLineWrap(true);
     commArea.setWrapStyleWord(true);
     commArea.setText(comment);

     JScrollPane sc = new JScrollPane(commArea);
     sc.setPreferredSize(new Dimension(settings.getIn().getSizedValue(250),
             settings.getIn().getSizedValue(50)));
     
     Object [] arr = {lblTime,fldTime,lblDate,fldDt,lblCom,sc};

     String [] var = {"Сохранить","Нет"};
     if(JOptionPane.showOptionDialog(   this,
                                        arr,
                                        "Сохранение меню",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        var,
                                        var[0])==JOptionPane.OK_OPTION){
     
                        String s = commArea.getText().trim();
                        
                        Collection<ProductInMenu> menu = null;
                        Collection<ProductInMenu> snack = null;
                        if (menuList.getRowCount()>0){
                            menu = new ArrayList();
                            for (int i=0;i<menuList.getRowCount();i++){
                                menu.add( ((MenuTableModel)menuList.getModel()).getProduct(i) );
                            }
                        }
                        if (snackList!=null && snackList.getRowCount()>0){
                            snack = new ArrayList();
                            for (int i=0;i<snackList.getRowCount();i++){
                                snack.add( ((MenuTableModel)snackList.getModel()).getProduct(i) );
                            }
                        }
                        ProductW prod = ((MenuTableModel)menuList.getModel()).getSumProd();
                        if (snackList!=null) prod.plusProd(((MenuTableModel)snackList.getModel()).getSumProd());
                        Dose ds = new Dose(prod, user.getFactors(), currDPS);
                        
                        Date time;
                        try{
                            time = fr.parse((String)fldTime.getValue());
                        }catch (ParseException e){
                            time = new Date();
                        }
                        long base = 0;
                        try{
                            base = fr.parse("00:00").getTime();
                        }catch(ParseException ex){
                            base = -3600000*3;//Для Москвы
                        }
                        long datetime = ( time.getTime() - base )  +
                            fldDt.getDate().getTime();

                        DiaryUnit u = new DiaryUnit(0,datetime,s,
                                currDPS.getSh1().getValue(),currDPS.getSh2().getValue(),
                                user.getFactors(),ds.getWholeDose(),user.getId(),prod);

                        new DiaryManager(user).addMenu(u, menu, snack);
                        this.pcs.firePropertyChange( USER_CHANGED, null, user );
                    }
 }
 private void showDiary(){
    if (diary==null){
        SwingUtilities.invokeLater(new Runnable(){
                @Override
            public void run(){
                diary = new DiaryJob(user,MainFrame.this);
                MainFrame.this.addPropertyChangeListener(diary);
                diary.setVisible(true);
            }
        });
    }
    else diary.setVisible(true);

 }

 public void setMenu(DiaryUnit u){
     DiaryManager mgr = new DiaryManager(user);
     Collection<ProductInMenu> menu2restore = mgr.getMenu(u);
     Collection<ProductInMenu> snack2restore = mgr.getSnack(u);
     flushMenu();

     for(ProductInMenu item:menu2restore){
         ((MenuTableModel)menuList.getModel()).insertProduct(item);
     }
     if (snack2restore.size()>0 && snackList==null){
         JOptionPane.showMessageDialog(this,
                 "Меню не может быть восстановлено полностью, так как\n" +
                 "в настройках программы отключен перекус,\n" +
                 "а восстанавливаемое меню содержит продукты\n" +
                 "в перекусе",
                 "Восстановление меню",
                 JOptionPane.WARNING_MESSAGE);
     }
     else{
        for(ProductInMenu item:snack2restore){
            ((MenuTableModel)snackList.getModel()).insertProduct(item);
        }
     }
     float old_be = user.getFactors().getBEValue();
     float new_be = u.getFactors().getBEValue();//Установить нужно старое значение хе

     user.setFactors(u.getFactors());
     user.getFactors().setBEValue(old_be);
     user.getFactors().setK1Value(
             user.getFactors().getK1Value() * old_be / new_be
             );
     
     if (factorsChooser!=null && factorsChooser.getItemCount()>0){
         factorsChooser.setForeground(Color.GRAY);
     }

     if (menuList!=null){
         storeCoefs = false;
        fldK1.setValue(user.getFactors().getK1(user.isDirect()));
        fldK2.setValue(user.getFactors().getK2());
        fldK3.setValue(new Sugar(user.getFactors().getK3()).getSugar(user.isMmol(),user.isPlasma()));
        fldXE.setValue(user.getFactors().getBE(user.isDirect()));
        fldSh1.setValue(new Sugar(u.getSh1()).getSugar(user.isMmol(), user.isPlasma()));
        fldSh2.setValue(new Sugar(u.getSh2()).getSugar(user.isMmol(), user.isPlasma()));
        storeCoefs = true;
        this.propertyChange(new PropertyChangeEvent(fldSh2,VALUE_CHANGED,null,fldSh2.getValue()));
     }
     calcMenu();
     
 }
 private void addEmptyCmplProd(){
     if (!grpList.getSelectionModel().isSelectionEmpty() &&
      ((GroupTableModel)grpList.getModel()).getGroup(grpList.getSelectedRow()).getId()>
             (settings.getIn().getUseUsageGroup()-1) &&
                 editedCmplProd==null){
        ProductInBase prod =  new ProductInBase(
                 "Новый продукт",
                 0f,
                 0f,
                 0f,
                 0,
                 0,
                 0,
                 true,//сложный продукт
                 ((GroupTableModel)grpList.getModel()).getGroup(
                    grpList.getSelectedRow()
                 ).getId(),
                 0
                 );
        prodDialog = new ProdDialog(this,prod,groupMgr
                .getGroups(0),grpList.getSelectedRow(),ProdDialog.NEW_COMPLEX);

        prodDialog.setModal(true);
        prodDialog.setVisible(true);
        if (prodDialog.getResult()){
            ProductInBase newProd = prodDialog.getProduct();

            ((ProductTableModel)prodList.getModel()).addProduct(newProd);
            int Owner = prodMgr.getLastInsertedId();
            Collection cmpls = new ArrayList();
            cmplMgr.addComplexProducts(cmpls, Owner);
            
            int r = prodDialog.getSelectedGroupIndex()+
                    settings.getIn().getUseUsageGroup();
            grpList.setRowSelectionInterval(r,r);
            int row = prodList.convertRowIndexToView(
                    ((ProductTableModel)prodList.getModel()).findRow(Owner) );
            Rectangle rect = prodList.getCellRect(row, 0, true);
            prodList.scrollRectToVisible(rect);
            prodList.setRowSelectionInterval(row, row);
        }
     }
 }
 private void showArc(){
    if (arc==null){
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
                arc = new ArchiveJob();
                arc.setVisible(true);
          }
        });
    }
    else{
        arc.setVisible(true);
    }
 }
    private void showCalcs(final int tab){
        if (calcsDlg!=null){
            if (!calcsDlg.isVisible())
            {
               calcsDlg.dispose();
               
               SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        calcsDlg = new CalcsDialog(user);
                        calcsDlg.selectTab(tab);
                        calcsDlg.setVisible(true);
                    }
                });
            }
            else{
               calcsDlg.selectTab(tab);
               calcsDlg.setVisible(true);
            }
        }else{
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    calcsDlg = new CalcsDialog(user);
                    calcsDlg.selectTab(tab);
                    calcsDlg.setVisible(true);
                }
             });
        }
    }
 private void showHelp(){
     if (Desktop.isDesktopSupported()){
       URI uri = null;
       if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
            try {
                uri = new URI(HELP_ADDR);
                Desktop.getDesktop().browse(uri);
            } catch(Exception ex){ ex.printStackTrace(); }
       }
    }
 }
 @Override
 public void addPropertyChangeListener( PropertyChangeListener listener )
 {
        this.pcs.addPropertyChangeListener( listener );
 }
 
 @Override
 public void removePropertyChangeListener( PropertyChangeListener listener )
 {
        this.pcs.removePropertyChangeListener( listener );
 }

 AbstractAction move2menu = new AbstractAction(){
     @Override
     public void actionPerformed(ActionEvent e) {
		menuList.requestFocusInWindow();
        final Color f = menuList.getForeground();
        final Color b = menuList.getBackground();
        menuList.setForeground(b);
        menuList.setBackground(f);

        Thread t = new Thread() {
          @Override
          public void run(){
             try{
                 Thread.sleep(150);
             } catch (Exception ex) {}
             menuList.setForeground(f);
             menuList.setBackground(b);
          }
        };
        t.start();
        if (menuList.getSelectionModel().isSelectionEmpty()&&menuList.getRowCount()>0){
            menuList.setRowSelectionInterval(0, 0);
            menuList.scrollRectToVisible(menuList.getCellRect(0, 0, true));
        }
     }
 };

 AbstractAction move2group = new AbstractAction(){
     @Override
     public void actionPerformed(ActionEvent e) {
		grpList.requestFocusInWindow();
        final Color f = grpList.getForeground();
        final Color b = grpList.getBackground();
        grpList.setForeground(b);
        grpList.setBackground(f);

        Thread t = new Thread() {
          @Override
          public void run(){
             try{
                 Thread.sleep(150);
             } catch (Exception ex) {}
             grpList.setForeground(f);
             grpList.setBackground(b);
          }
        };
        t.start();
        if (grpList.getSelectionModel().isSelectionEmpty()&&grpList.getRowCount()>0){
            grpList.setRowSelectionInterval(0, 0);
            grpList.scrollRectToVisible(grpList.getCellRect(0, 0, true));
        }
     }
 };
 AbstractAction move2prod = new AbstractAction(){
     @Override
     public void actionPerformed(ActionEvent e) {
		prodList.requestFocusInWindow();
        final Color f = prodList.getForeground();
        final Color b = prodList.getBackground();
        prodList.setForeground(b);
        prodList.setBackground(f);

        Thread t = new Thread() {
          @Override
          public void run(){
             try{
                 Thread.sleep(150);
             } catch (Exception ex) {}
             prodList.setForeground(f);
             prodList.setBackground(b);
          }
        };
        t.start();
        if (prodList.getSelectionModel().isSelectionEmpty()&&prodList.getRowCount()>0){
            prodList.setRowSelectionInterval(0, 0);
            prodList.scrollRectToVisible(prodList.getCellRect(0, 0, true));
        }
     }
 };
 AbstractAction move2cmpl = new AbstractAction(){
     @Override
     public void actionPerformed(ActionEvent e) {
        if (splitProds.getDividerLocation()>(splitProds.getHeight()/2) ){
             splitProds.setDividerLocation(splitProds.getHeight()/2);
        }
        cmplList.requestFocusInWindow();
        final Color f = cmplList.getForeground();
        final Color b = cmplList.getBackground();
        cmplList.setForeground(b);
        cmplList.setBackground(f);

        Thread t = new Thread() {
          @Override
          public void run(){
             try{
                 Thread.sleep(150);
             } catch (Exception ex) {}
             cmplList.setForeground(f);
             cmplList.setBackground(b);
          }
        };
        t.start();
        if (cmplList.getSelectionModel().isSelectionEmpty()&&cmplList.getRowCount()>0){
            cmplList.setRowSelectionInterval(0, 0);
            cmplList.scrollRectToVisible(cmplList.getCellRect(0, 0, true));
        }
     }
 };
 AbstractAction move2snack = new AbstractAction(){
     @Override
     public void actionPerformed(ActionEvent e) {
		if (snackList!=null){
            snackList.requestFocusInWindow();
            final Color f = snackList.getForeground();
            final Color b = snackList.getBackground();
            snackList.setForeground(b);
            snackList.setBackground(f);

            Thread t = new Thread() {
                @Override
                public void run(){
                    try{
                        Thread.sleep(150);
                    } catch (Exception ex) {}
                    snackList.setForeground(f);
                    snackList.setBackground(b);
                }
           };
            t.start();
            if (snackList.getSelectionModel().isSelectionEmpty()&&snackList.getRowCount()>0){
                snackList.setRowSelectionInterval(0, 0);
                snackList.scrollRectToVisible(snackList.getCellRect(0, 0, true));
            }
        }
     }
 };
    private void addMoveUpDown(final JTable table){
        AbstractAction moveUp = new AbstractAction(){
            @Override
        public void actionPerformed(ActionEvent ev){
            if (table.getRowCount()>0 && !table.getSelectionModel().isSelectionEmpty()){
                if (table.getSelectedRow()>0){
                    int row = table.getSelectedRow();
                    table.setRowSelectionInterval(row-1, row-1);
                }else{
                    table.getSelectionModel()
                            .setSelectionInterval(table.getRowCount()-1,
                                table.getRowCount()-1);
                }
                table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), 0, true));
            }
        }
        };

        AbstractAction moveDown = new AbstractAction(){
            @Override
        public void actionPerformed(ActionEvent ev){
            if (table.getRowCount()>0 && !table.getSelectionModel().isSelectionEmpty()){
                if (table.getSelectedRow()<(table.getRowCount()-1)){
                    int row = table.getSelectedRow();
                    table.setRowSelectionInterval(row+1, row+1);
                }else{
                    table.setRowSelectionInterval(0, 0);
                }
                table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), 0, true));
            }
        }
        };

        //table.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        //.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), MOVE_TABLE_UP);
        table.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), MOVE_TABLE_UP);
        table.getActionMap().put(MOVE_TABLE_UP, moveUp);

        //table.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        //.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), MOVE_TABLE_DOWN);
        table.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), MOVE_TABLE_DOWN);
        table.getActionMap().put(MOVE_TABLE_DOWN, moveDown);
    }

    private void addMoveLeftRight(final JTable source,final JTable left, final JTable right){
        AbstractAction moveright = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ev){
                if (source.isEditing()){
                    source.getCellEditor().stopCellEditing();
                }
                boolean  move = false;
                move = source.getSelectionModel().isSelectionEmpty();//нет выделения
                int next = -1;
                if (!move){
                    int currCol = source.getSelectedColumn();
                    int c = -1;
                    for (int i=(currCol>=0?(currCol+1):0);i<source.getColumnCount();i++){
                        if (source.isCellEditable(0, i)){
                            c = i;
                            break;
                        }
                    }
                    move = c<0;
                    if (c>=0) next = c;
                }
                if (move){
                   right.requestFocusInWindow();
                   if (right.getRowCount()>0 && right.getSelectionModel().isSelectionEmpty()){
                       right.setRowSelectionInterval(0, 0);
                   }//select first row
                   int c = -1;
                   for (int i=0;i<right.getColumnCount();i++){
                        if (right.isCellEditable(0, i)){
                            c = i;
                            break;
                        }
                   }
                   if (c>=0){
                       right.setColumnSelectionInterval(c, c);
                   }
                   else{
                       right.setColumnSelectionInterval(0, 0);
                   }
                   right.scrollRectToVisible(
                                right.getCellRect(
                                right.getSelectedRow(),
                                0, true));
                }
                else{//Есть редактируемая ячейка, переводим выделение туда
                    source.setColumnSelectionInterval(next, next);
                }
            }
        };
        AbstractAction moveleft = new AbstractAction(){
            @Override
        public void actionPerformed(ActionEvent ev){
            if (!source.getSelectionModel().isSelectionEmpty()){
                if (source.isEditing()){
                    source.getCellEditor().stopCellEditing();
                }
                boolean  move = false;
                move = source.getSelectionModel().isSelectionEmpty();//нет выделения
                int prior = -1;
                if (!move){
                    int currCol = source.getSelectedColumn();
                    int c = -1;
                    for (int i=(currCol>=0?(currCol-1):source.getColumnCount());i>0;i--){
                        if (source.isCellEditable(0, i-1)){
                            c = i-1;
                            break;
                        }
                    }
                    move = c<0;
                    if (c>=0) prior = c;
                }
                if (move){
                   left.requestFocusInWindow();
                   if (left.getRowCount()>0 && left.getSelectionModel().isSelectionEmpty()){
                       left.setRowSelectionInterval(0, 0);
                   }//select first row
                   int c = -1;
                   for (int i=left.getColumnCount();i>0;i--){
                        if (left.isCellEditable(0, i-1)){
                            c = i-1;
                            break;
                        }
                    }
                   if (c>=0){
                       left.setColumnSelectionInterval(c, c);
                   }
                   else{
                       left.setColumnSelectionInterval(0, 0);
                   }
                   left.scrollRectToVisible(
                                left.getCellRect(
                                left.getSelectedRow(),
                                0, true));
                }
                else{//Есть редактируемая ячейка, переводим выделение туда
                    source.setColumnSelectionInterval(prior, prior);
                }
            }
        }
    };

    //source.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    //    .put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), MOVE_TABLE_RIGHT);
    source.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), MOVE_TABLE_RIGHT);
    source.getActionMap().put(MOVE_TABLE_RIGHT, moveright);

    //source.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
      //  .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), MOVE_TABLE_LEFT);
    source.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), MOVE_TABLE_LEFT);
    source.getActionMap().put(MOVE_TABLE_LEFT, moveleft);
    }


}//Конец главного класса

