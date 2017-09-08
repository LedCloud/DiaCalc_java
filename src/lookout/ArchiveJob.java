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
import lookout.cellroutins.FloatRenderer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import lookout.settings.ProgramSettings;
import java.net.*;
import tablemodels.*;
import products.ProdGroup;
import products.ProductInBase;
import products.ProductW;
import manager.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;
import lookout.dndsupport.ArcProductHandler;
import lookout.dndsupport.ArcGroupHandler;
import java.text.DecimalFormat;


public class ArchiveJob extends JDialog implements ActionListener{
    private static final String ARCHIVE_PANE = "arc pane";
    private static final String SEARCH_ARC_PANE = "search arc pane";
    private static final String SEARCH = "open search pane";
    private static final String CANCEL_SEARCH = "cancel search";
    private static final String NEW_GROUP = "new group";
    private static final String EDIT_GROUP = "edit group";
    private static final String DEL_GROUP = "delete group";
    private static final String IMPORT = "import base";
    private static final String EXPORT = "export base";
    private static final String DEL_PRODUCT = "delete product";

    private static final String SELECTION_SEARCHED = "doSelectionSeached";
    private static final String SELECTION_CANCELED = "doSelectionCanceled";

    private final ProgramSettings settings = ProgramSettings.getInstance();

    private JPanel mainPane = null;

    private JTable grpList;
    private JTable prodList;
    private JTextField searchFld;
    private JTable searchTable;

    private final ArcGroupManager arcGrmgr = new ArcGroupManager();

    public ArchiveJob(){
        setIconImage(new ImageIcon(ArchiveJob.class
                .getResource("images/ArcIcon.png")).getImage());
        setTitle("Архивная база продуктов");
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createGroupPane(),createProdPane());
        split.setOneTouchExpandable(false);
        split.setDividerLocation(0.3);

        mainPane = new JPanel(new CardLayout());
        mainPane.add(split,ARCHIVE_PANE);
        mainPane.add(createSearchPane(),SEARCH_ARC_PANE);

        add(mainPane);

        reloadProducts();

        setSize(settings.getIn().getSizedValue(500),settings.getIn().getSizedValue(400));
        setModal(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public JComponent createGroupPane(){
        JPanel pane = new JPanel(new BorderLayout());
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.add(makeButton("New","Add",NEW_GROUP,"создать группу"));
        bar.add(makeButton("Edit","Edit",EDIT_GROUP,"редактировать группу"));
        bar.addSeparator();
        bar.add(makeButton("Delete","Del",DEL_GROUP,"удалить группу"));

        grpList = new JTable(new ArcGroupTableModel());
        grpList.setFillsViewportHeight(true);
        grpList.setRowHeight(settings.getIn().getSizedValue(grpList.getRowHeight()));
        grpList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
           @Override
           public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    reloadProducts();
                    //System.out.println("Выбрали группу");
                }
            }});
        if (grpList.getRowCount()>0){
            grpList.getSelectionModel().setSelectionInterval(0, 0);
        }
        grpList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grpList.addKeyListener(new KeyAdapter(){
           @Override
           public void keyReleased(KeyEvent e){
               if (e.getModifiersEx()==0){
                if (e.getKeyCode()==KeyEvent.VK_INSERT){
                   createNewGroup();
                } else if (e.getKeyCode()==KeyEvent.VK_F4){
                   editGroup();
                }else if (e.getKeyCode()==KeyEvent.VK_DELETE){
                   deleteGroup();
                }
               }
           }
        });

        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Изменить имя группы");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, 0));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(EDIT_GROUP);
        popup.add(menuItem);
        menuItem = new JMenuItem("Создать группу");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_INSERT, 0));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(NEW_GROUP);
        popup.add(menuItem);
        popup.addSeparator();

        menuItem = new JMenuItem("Удалить группу");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(DEL_GROUP);
        popup.add(menuItem);


    //Add listener to components that can bring up popup menus.
    //grpList.addMouseListener(new PopupListener(popup));
    //grpList.setComponentPopupMenu(popup);
        grpList.addMouseListener(new PopupListener(popup));

        grpList.setDropMode(DropMode.ON);
        grpList.setTransferHandler(new ArcGroupHandler());

        pane.add(bar,BorderLayout.NORTH);
        pane.add(new JScrollPane(grpList),BorderLayout.CENTER);

        return pane;
    }

    private JComponent createProdPane(){
        JPanel pane = new JPanel(new BorderLayout());
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.add(makeButton("Search","Srch",SEARCH,"Поиск продукта"));
        bar.addSeparator();
        bar.add(makeButton("Import","Import",IMPORT,"Импортировать базу"));
        bar.add(makeButton("Export","Export",EXPORT,"Экспортировать продукты пользователя"));
        bar.addSeparator();
        bar.add(makeButton("Delete","Del",DEL_PRODUCT,"Удалить продукт из базы"));

        int id = -1;
        if (!grpList.getSelectionModel().isSelectionEmpty()){
            id = ((ArcGroupTableModel)grpList.getModel()).getGroup(0).getId();
            //Так это инициализация, то я уверен, что выбран будет первый элемент
        }

        prodList = new JTable(new ArcProductTableModel(id)){
         @Override
        public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            if (rowIndex>=0 && colIndex>=0){
                ProductInBase prod = ((ArcProductTableModel)getModel()).getProduct(rowIndex);
                tip = prod.getName();

                int width = prodList.getFontMetrics(prodList.getFont()).stringWidth(tip);
                if (width<=(prodList.getColumnModel().getColumn(0).getWidth()-5)) return null;
            }
            return tip;
        }};
        prodList.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i=1;i<prodList.getColumnCount();i++){
          prodList.getColumnModel().getColumn(i).setPreferredWidth(45);
        }
        prodList.setAutoCreateRowSorter(true);//дополняем сортировку
        prodList.setFillsViewportHeight(true);//заполняем всю область
        prodList.setRowHeight(settings.getIn().getSizedValue(prodList.getRowHeight()));
        prodList.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
        prodList.setDefaultRenderer(Float.class, new FloatRenderer("0.0"));
        prodList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        prodList.setDropMode(DropMode.INSERT_ROWS);
        prodList.setTransferHandler(new ArcProductHandler());
        prodList.setDragEnabled(true);
        
        prodList.addKeyListener(new KeyAdapter(){
         @Override
         public void keyReleased(KeyEvent e){
             if (e.getModifiersEx()==0){
                if (e.getKeyCode()==KeyEvent.VK_DELETE){
                    deleteProduct();
                }else if (e.getKeyCode()==KeyEvent.VK_I){
                    importArcBase();
                }else if (e.getKeyCode()==KeyEvent.VK_E){
                    exportArcBase();
                }else if (e.getKeyCode()==KeyEvent.VK_F7){
                    beginSearch();
                }
            }
         }
        });

    JPopupMenu popup = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem("Поиск продукта");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F7, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(SEARCH);
    popup.add(menuItem);
    popup.addSeparator();


    menuItem = new JMenuItem("Импорт архивной базы продуктов");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_I, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(IMPORT);
    popup.add(menuItem);
    menuItem = new JMenuItem("Экспорт из архива продуктов пользователя");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(EXPORT);
    popup.add(menuItem);
    popup.addSeparator();

    menuItem = new JMenuItem("Удалить продукт");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
    menuItem.addActionListener(this);
    menuItem.setActionCommand(DEL_PRODUCT);
    popup.add(menuItem);

    prodList.addMouseListener(new PopupListener(popup));

        if (prodList.getRowCount()>0){
            prodList.getSelectionModel().setSelectionInterval(0, 0);
        }
        
        pane.add(bar,BorderLayout.NORTH);
        pane.add(new JScrollPane(prodList),BorderLayout.CENTER);

        return pane;
    }

    private JComponent createSearchPane(){
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
                      doSearch();
                  }
              }
          }
      });
      bar.add(searchFld);
      bar.addSeparator();
      //bar.add(makeButton("Search","Search",DO_SEARCH,
      //        "Искать продукт"));
      bar.add(makeButton("CancelSrch","Cancel Search",CANCEL_SEARCH,
              "Закончить поиск"));

      bar.setFloatable(false);


      searchTable = new JTable(new ArcSearchTableModel());
      searchTable.setFillsViewportHeight(true);
      searchTable.setRowHeight(settings.getIn().getSizedValue(searchTable.getRowHeight()));
      searchTable.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы

      searchTable.getColumnModel().getColumn(0).setPreferredWidth(settings.getIn().getSizedValue(300));
      searchTable.getColumnModel().getColumn(1).setPreferredWidth(settings.getIn().getSizedValue(400));
      searchTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      searchTable.setDefaultRenderer(Float.class, new FloatRenderer("0.0"));
      searchTable.addMouseListener(new MouseAdapter(){
          @Override
          public void mouseClicked(MouseEvent e) {
             if (SwingUtilities.isLeftMouseButton(e) &&
                     e.getClickCount() % 2 == 0)  { doSelectionSearched(); }
          }
     });

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
    private void doSearch(){
        ((ArcSearchTableModel)searchTable.getModel()).doSearch(searchFld.getText());
    }
    private void doSelectionSearched(){
        //Тут перходим назад в базу
        int sel = searchTable.getSelectedRow();
        int grInx = ((ArcSearchTableModel)searchTable.getModel()).getGroup(sel).getId();
        int prInx = ((ArcSearchTableModel)searchTable.getModel()).getProduct(sel).getId();
        int grRow;
        int prRow;
        if ( (grRow = ((ArcGroupTableModel)grpList.getModel()).findRow(grInx))>=0 ){
            grpList.getSelectionModel().setSelectionInterval(grRow, grRow);
            if (  (prRow = prodList
                    .convertRowIndexToView(((ArcProductTableModel)prodList
                    .getModel()).findRow(prInx)) )>=0 ){

                Rectangle rect = prodList.getCellRect(prRow, 0, true);
                prodList.scrollRectToVisible(rect);
                prodList.getSelectionModel().setSelectionInterval(prRow, prRow);
            }
     }
     cancelSearch();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String cmd = e.getActionCommand();
        if (SEARCH.equals(cmd)){
            beginSearch();
        }
        else if (CANCEL_SEARCH.equals(cmd)){
            cancelSearch();
        }
        else if(NEW_GROUP.equals(cmd)){
            createNewGroup();
        }
        else if (EDIT_GROUP.equals(cmd)){
            editGroup();
        }
        else if (DEL_GROUP.equals(cmd)){
            deleteGroup();
        }
        else if (IMPORT.equals(cmd)){
            importArcBase();
        }
        else if (EXPORT.equals(cmd)){
            exportArcBase();
        }
        else if (DEL_PRODUCT.equals(cmd)){
            deleteProduct();
        }
    }
    private void deleteProduct(){
        if (!prodList.getSelectionModel().isSelectionEmpty()){
            ProductInBase prod = ((ArcProductTableModel)prodList.getModel()).getProduct(
                    prodList.convertRowIndexToModel(prodList.getSelectedRow())
                    );
            //if (!prod.isComplex()){
                Object[] options = {"Да","Нет" };
                if (JOptionPane.showOptionDialog(this,
                                    "Вы собираетесь удалить продукт:\n\n" +
                                    prod.getName() +
                                    "\n\n" +
                                    "Удалить?",
                                    "Удаление продукта",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[1]) == JOptionPane.YES_OPTION) {
                    ((ArcProductTableModel)prodList.getModel()).deleteProduct(prod);
                }
           // }
        }
    }
    private void reloadProducts(){
        if ( (prodList!= null) && (grpList.getRowCount()==0 ||
              (!grpList.getSelectionModel().isSelectionEmpty() &&
                    grpList.getRowCount()>0)
              )){

          int grId = 0;
          // Получаем выделенную группу
          if (!grpList.getSelectionModel().isSelectionEmpty()) {
              grId = ((ProdGroup) ((ArcGroupTableModel)grpList.getModel())
                     .getGroup(grpList.getSelectedRow())).getId();
          }

          ((ArcProductTableModel)prodList.getModel()).reloadProducts(grId);


           if (prodList.getModel().getRowCount()>0) {
                prodList.getSelectionModel().setSelectionInterval(0, 0);
           }

        }
    }
    private void exportArcBase(){
        Vector<ProdGroup> groups = new Vector(arcGrmgr.getGroups());
        Vector<ProductInBase> prod2ex = new Vector();
        Vector<ProdGroup> group2ex = new Vector();
        ArcProductManager arcProdmgr = new ArcProductManager();
        Vector<ProductInBase> prods = new Vector(arcProdmgr.getAllProducts());
        for(ProductInBase pr:prods){
            if (!pr.isComplex()){
                prod2ex.add(pr);
                for(ProdGroup gr:groups){
                    if (pr.getOwner()==gr.getId()){
                        if (!group2ex.contains(gr)) group2ex.add(gr);
                        break;
                    }
                }
            }
        }
        //System.out.println(prod2ex.size());
        //System.out.println(group2ex.size());
        if (prod2ex.size()>0){
            JFileChooser fileSelector = new JFileChooser();
            fileSelector.setDialogTitle("Экспорт архивной базы продуктов");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            "Файл с архивом *.dex", "dex");
            fileSelector.setMultiSelectionEnabled(false);
            fileSelector.setFileFilter(filter);
            fileSelector.setAcceptAllFileFilterUsed(false);
            File fl = new File("*.dex");
            fileSelector.setSelectedFile(fl);
           
            
            fileSelector.setAcceptAllFileFilterUsed(false);
            if (fileSelector.showSaveDialog(this) ==
                    JFileChooser.APPROVE_OPTION) {
                DecimalFormat fr = new DecimalFormat("0.0");
                try{
                    String path = fileSelector.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".dex") ){
                        path += ".dex";
                    }
                    
                    File file = new File(path);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter
                                 (new FileOutputStream(
                                 file
                                 ),"UTF8"));
                    for(ProdGroup gr:group2ex){
                        out.write("#"+gr.getName());
                        out.newLine();
                        for(ProductInBase pr:prod2ex){
                            if (gr.getId()==pr.getOwner()){
                                out.write(pr.getName()+" ");
                                out.write(fr.format(pr.getProt())+" ");
                                out.write(fr.format(pr.getFat())+" ");
                                out.write(fr.format(pr.getCarb())+" ");
                                out.write(""+pr.getGi());
                                out.newLine();
                            }
                        }
                    }

                    out.close();
                    JOptionPane.showMessageDialog(this, "Экспорт удачно завершен");
                // System.out.println("Written Process Completed.");
                }
                catch(UnsupportedEncodingException ue){
                    System.err.println("Not supported : ");
                }
                catch(IOException e){
                    System.err.println(e.getMessage());
                }

            }
        }
        else{
            JOptionPane.showMessageDialog(this, "Нечего экспортировать");
        }
    }

    private void importArcBase(){
        JFileChooser fileSelector = new JFileChooser();
                        fileSelector.setDialogTitle("Импорт базы продуктов");
                        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            "Файл архивной базы данных : *.dar", "dar");
                        fileSelector.setMultiSelectionEnabled(false);
                        fileSelector.setFileFilter(filter);
                        fileSelector.setAcceptAllFileFilterUsed(false);

                        
           if(fileSelector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                BufferedReader r=null;
                String line=null;
                ProdGroup group = null;
                ProductInBase prod = null;
                Collection<ProductInBase> toadd = new ArrayList();
                Collection<ProductInBase> tocorrect = new ArrayList();
                ArcProductManager arcProdmgr = new ArcProductManager();
                Collection<ProdGroup> grEx = arcGrmgr.getGroups();
                Collection<ProductInBase> prEx = new ArrayList();
                try{
                  r = new BufferedReader( new InputStreamReader
                             ( new FileInputStream
                                        (fileSelector.getSelectedFile().getAbsolutePath()), "UTF8" ) );
                  boolean exists = false;
                  boolean equal = false;

                  while((line=r.readLine())!=null){
                    line = line.trim();

                    if (line.length()>0){
                        if (line.equals("#CLEAR")){
                            arcProdmgr.clearArchive();
                            grEx = arcGrmgr.getGroups();
                        }
                        else
                        if (line.charAt(0)=='#'){//Значит новая группа
                            //тут добавляем в базу, пока не декодировали новую группу
                            if (group!=null){
                                if (toadd.size()>0){
                                    arcProdmgr.addCollectionProducts(toadd, group.getId());
                                }
                                if (tocorrect.size()>0){
                                    for(ProductInBase pr:tocorrect){
                                        arcProdmgr.updateProductInBase(pr);
                                    }
                                }
                                toadd.clear();
                                tocorrect.clear();
                            }
                            
                            group = decodeGroup(line);
                            exists = false;
                            equal = false;

                            for(ProdGroup gr:grEx){
                                if (gr.getSortInd()==group.getSortInd()){
                                    exists = true;
                                    equal = gr.getName().equals(group.getName());
                                    group.setId(gr.getId());
                                    prEx = arcProdmgr.getProductsFromGroup(group.getId());
                                    break;
                                }
                            }
                            if (exists){
                                 if (!equal) arcGrmgr.updateGroup(group);

                            }else{
                                arcGrmgr.addGroup(group);
                                group.setId(arcGrmgr.getLastInsertedId());
                            }

                        }//А это запись о продукте
                        else if (group!=null){
                            prod = decodeProduct(line);
                            prod.setOwner(group.getId());

                            //Тут вначале проверяем нужно ли добавлять или менять
                            if (exists){
                                //Тут проверяем на наличие
                                boolean prexists = false;
                                boolean prequal = false;
                                for(ProductInBase pr:prEx){
                                    if (pr.getName().equals(prod.getName())){
                                        prexists = true;
                                        prequal =  ((ProductW)pr).equals((ProductW)prod);
                                        prod.setId(pr.getId());
                                        break;
                                    }
                                }
                                if (prexists){
                                    if (!prequal) tocorrect.add(prod);
                                }else{
                                    toadd.add(prod);
                                }
                            }else{//Группа новая, значит все продукты добавляем
                                toadd.add(prod);
                            }
                        }
                    }
                  }
                  //тут нужно осуществить или добавление или коррекцию продукта
                  if (group!=null){
                     if (toadd.size()>0){
                         arcProdmgr.addCollectionProducts(toadd, group.getId());
                     }
                     if (tocorrect.size()>0){
                         for(ProductInBase pr:tocorrect){
                             arcProdmgr.updateProductInBase(pr);
                         }
                     }
                     toadd.clear();
                     tocorrect.clear();
                 }
                  r.close();

               }catch (IOException ex){
                   System.err.println(ex.getMessage());
               }
               ((ArcGroupTableModel)grpList.getModel()).reloadGroups();
               if (grpList.getRowCount()>0){
                   grpList.getSelectionModel().setSelectionInterval(0, 0);
               }
           }
    }
    
    private ProductInBase decodeProduct(String st){
       
     String [] dt = {"","","",""};
     int i = st.length()-1;
     int p = 3;
     boolean flag = false;
     while (i>=0 && p>=0){
         if (st.charAt(i)!=' ' && st.charAt(i)!='\t'){
             dt[p] = st.charAt(i) + dt[p];
             flag = true;
         }
         else if (flag) { p--; flag=false; }
         i--;
     }
     
     String name = st.substring(0, i+1).trim();

     return new ProductInBase(name,Float.parseFloat(dt[0].replace(',', '.')),
             Float.parseFloat(dt[1].replace(',', '.')),
             Float.parseFloat(dt[2].replace(',', '.')),
             (int)Float.parseFloat(dt[3].replace(',', '.')),
             100,0,true,0,0);
    }
    
    private ProdGroup decodeGroup(String v){
        v = v.substring(1);

        String idGr = "";
        int i;
        for (i=v.length()-1;i>=0;i--){
         if (v.charAt(i)==' ' || v.charAt(i)=='\t') break;
         else  idGr = v.charAt(i)+idGr;
     }
     return new ProdGroup( 0 , v.substring(0, i).trim() ,  Integer.parseInt(idGr));
    }
    private void createNewGroup(){
        JLabel lblTitle = new JLabel("Введите название группы:");
        JTextField fldTitle = new JTextField("Новая группа");
        fldTitle.addFocusListener(new MyFocusListener());

        Object[] arr = {lblTitle,fldTitle};
        String [] var = {"Да","Нет"};

        if (JOptionPane.showOptionDialog(
                                        this,
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
                        arcGrmgr.addGroup(new ProdGroup(0,s,0));
                        int id = arcGrmgr.getLastInsertedId();
                        ((ArcGroupTableModel)grpList.getModel()).reloadGroups();
                        int n = ((ArcGroupTableModel)grpList.getModel()).findRow(id);
                        Rectangle rect = grpList.getCellRect(n, 0, true);
                        grpList.scrollRectToVisible(rect);
                        grpList.getSelectionModel().setSelectionInterval(n,n);
                    }
                   //else System.out.println("Ничего не введено");
   }
 }

 private void editGroup(){
    if (grpList.getRowCount()>0 && !grpList.getSelectionModel().isSelectionEmpty()){
        ProdGroup gr = ((ArcGroupTableModel)grpList.getModel())
                .getGroup(grpList.getSelectedRow());
        if (gr.getSortInd()==0){
            JLabel lblTitle = new JLabel("Введите новое название группы:");
            JTextField fldTitle = new JTextField(
                gr.getName() );

            fldTitle.addFocusListener(new MyFocusListener());
            Object[] arr = {lblTitle,fldTitle};
            String [] var = {"Да","Нет"};

            if (JOptionPane.showOptionDialog(
                                        this,
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
                        if (!s.equals(gr.getName())){
                            //System.out.println("Введено: "+s);
                            gr.setName(s);
                            int id = gr.getId();
                            arcGrmgr.updateGroup(gr);
                            ((ArcGroupTableModel)grpList.getModel()).reloadGroups();

                            int n = ((ArcGroupTableModel)grpList.getModel()).findRow(id);
                            Rectangle rect = grpList.getCellRect(n, 0, true);
                            grpList.scrollRectToVisible(rect);
                            grpList.getSelectionModel().setSelectionInterval(n,n);
                        }
                    }
                    //else System.out.println("Ничего не введено");
            }
        }
    }
 }

 private void deleteGroup(){
    if (grpList.getRowCount()>0 && !grpList.getSelectionModel().isSelectionEmpty()){
        ProdGroup gr = ((ArcGroupTableModel)grpList.getModel())
                .getGroup(grpList.getSelectedRow());
        //if (gr.getSortInd()==0){
            Object[] options = {"Да","Нет" };
            if (JOptionPane.showOptionDialog(this,
                                    "Вы собираетесь удалить группу:\n\n" +
                                    gr.getName() +
                                    "\n\n" +
                                    "Будут удалены все, входящие в нее, продукты\n"+
                                    "Удалить?",
                                    "Удаление группы",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[1]) == JOptionPane.YES_OPTION) {
                        //System.out.println("Удаляем");

                        int pos = grpList.getSelectedRow();
                        ((ArcGroupTableModel)grpList.getModel()).deleteGroup(gr);
                        if (grpList.getModel().getRowCount()>0)
                            if (pos>0) grpList.getSelectionModel().setSelectionInterval(pos-1, pos-1);
                            else grpList.getSelectionModel().setSelectionInterval(0, 0);
                    }
         // }
    }
 }

 private void beginSearch(){
     ((CardLayout)mainPane.getLayout()).show(mainPane,SEARCH_ARC_PANE);

     searchFld.selectAll();
     searchFld.requestFocusInWindow();
    }

    private void cancelSearch(){
     ((CardLayout)mainPane.getLayout()).show(mainPane,ARCHIVE_PANE);
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
}
