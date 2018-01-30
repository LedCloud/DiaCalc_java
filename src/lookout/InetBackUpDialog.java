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
 * Portions Copyrighted 2017 Toporov Konstantin.
 */
package lookout;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import lookout.settings.ProgramSettings;
import maths.*;
import java.util.*;
import products.*;
import manager.*;
import java.beans.*;
import javax.swing.event.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class InetBackUpDialog extends JDialog implements ActionListener,
        PropertyChangeListener{
    private final ProgramSettings settings = ProgramSettings.getInstance();
    private final MainFrame owner;
    private JTextArea log;
    private JScrollPane scr;
    private JProgressBar prBar;
    private JRadioButton rbIm1;
    private JRadioButton rbIm2;
    private JRadioButton rbIm3;
    private final InetUser inetData;
    private JCheckBox chImMenu;
    private JCheckBox chExMenu;
    private JCheckBox chExProducts;
    private JCheckBox chImProducts;
    private JCheckBox chImDiary;
    private JButton btnStExport;
    private JButton btnStImport;
    private JButton btnSync;
    private JButton btnChPass;
    private JRadioButton rbServerMajor;
    private JRadioButton rbLocalMajor;

    private JTextField fldLogin;
    private JPasswordField fldPass;
    private JTextField fldServer;
    private boolean sync = false;
    private final InetUserManager inetMgr = new InetUserManager();

    private final static String SCRIPT = "server.php";

    private final static String BEGIN_EXPORT = "begin export";
    private final static String BEGIN_IMPORT = "begin import";
    private final static String CANCEL_TASKS = "cancel background tastks";
    private final static String BEGIN_SYNC = "begin sycronisation";
    private final static String CHANGE_PASS = "change password";
    

    private ExportTask taskExport = null;
    private ImportTask taskImport = null;
    private ImportBaseTask taskImportBase = null;
    private ChangePassTask taskChPass = null;

 
    private boolean cancel_was = false;
    private boolean hasCheckedVer = false;
    
    public InetBackUpDialog(MainFrame owner){
        super(owner);
        setTitle("Импорт и экспорт в интеренет базу данных");
        this.owner = owner;
        inetData = inetMgr.getUser();
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.add("Синхронизация",createSyncPane());
        tabPane.add("Экспорт",createExportPane());
        tabPane.add("Импорт",createImportPane());
        JPanel pane = new JPanel(new BorderLayout());
        pane.add(tabPane,BorderLayout.CENTER);
        this.add(pane);
        add(createTopPane(),BorderLayout.NORTH);
        add(createBottomPane(),BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(this.owner);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter(){
               @Override
               public void windowClosing(WindowEvent e){
                   closeDialog();
               }
        });
    }
    private void closeDialog(){
        if (taskExport!=null && !taskExport.isDone()) taskExport.cancel(true);
        if (taskImport!=null && !taskImport.isDone()) taskImport.cancel(true);
        if (taskImportBase!=null && !taskImportBase.isDone()) taskImportBase.cancel(true);
        if (taskChPass!=null && !taskChPass.isDone()) taskChPass.cancel(true);

        setVisible(false);
        saveLogInfo();
    }
    private void enableElements(boolean enable){
        chImProducts.setEnabled(enable);
        rbIm1.setEnabled(enable && chImProducts.isSelected());
        rbIm2.setEnabled(enable && chImProducts.isSelected());
        rbIm3.setEnabled(enable && chImProducts.isSelected());
        chImMenu.setEnabled(enable);
        chImDiary.setEnabled(enable);
        chExMenu.setEnabled(enable);
        chExProducts.setEnabled(enable);
        btnChPass.setEnabled(enable);
        rbServerMajor.setEnabled(enable);
        rbLocalMajor.setEnabled(enable);
    }
    @Override
    public void actionPerformed(ActionEvent ev){
        String cmd = ev.getActionCommand();
        if (BEGIN_EXPORT.equals(cmd)){
            cancelButtons();
            
            taskExport = new ExportTask(chExMenu.isSelected(),chExProducts.isSelected());
            taskExport.addPropertyChangeListener(this);
            taskExport.execute();
        }
        else if (BEGIN_IMPORT.equals(cmd)){
            cancelButtons();

            taskImport = new ImportTask(
                    chImMenu.isSelected(),
                    chImProducts.isSelected(),
                    chImDiary.isSelected()
                    /*rbIm1.isSelected()?ImportBaseTask.MODE_UPDATE:
                        (rbIm2.isSelected()?ImportBaseTask.MODE_ADD:ImportBaseTask.MODE_REPLACE)*/
                    );
            taskImport.addPropertyChangeListener(this);
            taskImport.execute();
        }
        else if (CANCEL_TASKS.equals(cmd)){
            cancel_was = true; //чего бы там дальше не было - не делаем
            log.append("Операция прервана пользователем.\n");

            prBar.setIndeterminate(false);
            prBar.setValue(0);
            restoreButtons();

            if (taskImport!=null && !taskImport.isDone()) taskImport.cancel(true);
            if (taskImportBase!=null && !taskImportBase.isDone()) taskImportBase.cancel(true);
            if (taskExport!=null && !taskExport.isDone()) taskExport.cancel(true);
            if (taskChPass!=null && !taskChPass.isDone()) taskChPass.cancel(true);
        }
        else if (BEGIN_SYNC.equals(cmd)){
            cancelButtons();
            log.append("Синхронизация начата...\n");
            sync = true;
            
            ///////Сначала импортируем в режиме объединения
            taskImport = new ImportTask(
                    false,
                    true,
                    false//ImportBaseTask.MODE_UPDATE
                    );

            taskImport.addPropertyChangeListener(this);
            taskImport.execute();
        }
        else if (CHANGE_PASS.equals(cmd)){
            //Меняем пароль
            changePassword();
        }
    }
    private void changePassword(){
        JLabel lbl1 = new JLabel("Введите новый пароль");
        JPasswordField pass1 = new JPasswordField();
        JLabel lbl2 = new JLabel("Введите пароль повторно");
        JPasswordField pass2 = new JPasswordField();
        JLabel lbl3 = new JLabel(
                "Пароль будет изменен, если в форме\n" +
                "импорта и экспорта указаны верные\n" +
                "действующие логин и пароль");
        Object [] arr = {lbl1,pass1,lbl2,pass2,lbl3};
        String [] var = {"Изменить","Отмена"};
        if(JOptionPane.showOptionDialog(
                                        this,
                                        arr,
                                        "Ввод СК",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        var,
                                        var[0])==JOptionPane.OK_OPTION){
            if (pass1.getPassword().length==pass2.getPassword().length ||
                    !new String(pass1.getPassword()).equals( new String(pass2.getPassword()) ) ){
                cancelButtons();
                log.append("Меняем пароль...\n");

                taskChPass = new ChangePassTask(new String(pass1.getPassword()));
                taskChPass.addPropertyChangeListener(this);
                taskChPass.execute();
            }
            else log.append("Введеные пароли не совпадают\n");
            //yes
        }
    }
    private  void cancelButtons(){
        enableElements(false);

        prBar.setIndeterminate(true);
        btnStExport.setActionCommand(CANCEL_TASKS);
        btnStExport.setText("Отменить");
        btnStImport.setActionCommand(CANCEL_TASKS);
        btnStImport.setText("Отменить");
        btnSync.setActionCommand(CANCEL_TASKS);
        btnSync.setText("Отменить");
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            if (evt.getSource()==taskImportBase && 
                    taskImportBase.getState()==SwingWorker.StateValue.DONE){
                if (cancel_was){
                    cancel_was = false;
                    restoreButtons();
                }else{
                    //Закончили добавлять или обновлять продукты в базе
                    log.append("Импорт продуктов в базу закончен\n");
                    if (sync){//теперь нужно запустить экспорт нет, сначала нужно запустить импорт в базу
                        taskExport = new ExportTask(false,true);
                        taskExport.addPropertyChangeListener(this);
                        taskExport.execute();
                    }else{//закончили нормально импорт
                        restoreButtons();
                        owner.refreshProducts();
                    }
                }
                taskImportBase = null;
                System.gc();
            }
            else if (evt.getSource()==taskImport && 
                    taskImport.getState()==SwingWorker.StateValue.DONE){
                if (cancel_was){
                    cancel_was = false;
                    restoreButtons();
                }
                //Закончили импорт, теперь проверяем нужно ли импортировать базу продуктов
                else if ( chImProducts.isSelected() || sync ){
                    /////////////////
                    Vector<ProdGroup> groups = taskImport.getGroups();
                    Vector<ProductInBase> prods = taskImport.getProds();
                    Vector<ComplexProduct> cmpl = taskImport.getCmpl();
                    if (groups!=null && prods!=null && cmpl!=null){
                        boolean goon = true;
                        int locprods=0;// = new ProductManager().getAllProducts().size();
                        int mode = rbIm1.isSelected()?ImportBaseTask.MODE_UPDATE:
                            (rbIm2.isSelected()?ImportBaseTask.MODE_ADD:ImportBaseTask.MODE_REPLACE);
                        if (mode==ImportBaseTask.MODE_REPLACE &&
                            prods.size()<(locprods=new ProductManager().getAllProducts().size())
                                && !sync){
                                //загружаемых продуктов меньше, чем в текущей базе
                                Object[] options = {"Да","Нет" };
                                goon = JOptionPane.showOptionDialog(InetBackUpDialog.this,
                                    "В загружаемой базе продкутов меньше, чем локальной базе\n" +
                                    "Из локальной базы будет удалено "+locprods+" продуктов\n"+
                                    "Будет импортировано "+prods.size()+" продуктов\n\n" +
                                    "Продолжить?",
                                    "Импорт",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[1])==JOptionPane.YES_OPTION;
                        }
                        if (goon || sync){
                            log.append("Идет импорт продуктов в базу, подождите...\n");
                            int maj = ImportBaseTask.LOCAL;
                            if (sync && rbServerMajor.isSelected()) maj = ImportBaseTask.SERVER;

                            taskImportBase = new ImportBaseTask(groups,prods,cmpl,mode,maj);
                            taskImportBase.addPropertyChangeListener(InetBackUpDialog.this);
                            taskImportBase.execute();
                        }
                    }

                    /////////////////
                }//Значит закончили теперь нужно вернуть кнопки
                else{
                    if (chImDiary.isSelected()) owner.refreshUser();
                    restoreButtons();
                }
                taskImport = null;
                System.gc();
            }
            else if (evt.getSource()==taskExport &&
                    taskExport.getState()==SwingWorker.StateValue.DONE){// &&  !chImProducts.isSelected()
                if (cancel_was){
                    cancel_was = false;
                    restoreButtons();
                }
                else{
                    if (sync){
                        log.append("Синхронизация завершена.\n");
                        owner.refreshProducts();
                        sync = false;
                    }
                    restoreButtons();
                }
                taskExport = null;
                System.gc();
            }else if (evt.getSource()==taskChPass &&
                    taskChPass.getState()==SwingWorker.StateValue.DONE){// &&  !chImProducts.isSelected()
                if (cancel_was){
                    cancel_was = false;
                }
                restoreButtons();
                taskChPass = null;
                System.gc();
            }
            
        }else if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            if (evt.getSource()==taskExport){
                prBar.setIndeterminate(taskExport.getIntermediate());
                prBar.setValue(progress);
            }
            else{
                prBar.setValue(progress);
            }
        }
    }
    private void restoreButtons(){
        btnStExport.setActionCommand(BEGIN_EXPORT);
        btnStExport.setText("Начать");

        btnStImport.setActionCommand(BEGIN_IMPORT);
        btnStImport.setText("Начать");

        btnSync.setActionCommand(BEGIN_SYNC);
        btnSync.setText("Синхронизация");

        prBar.setIndeterminate(false);
        prBar.setValue(0);
        enableElements(true);
    }
    
    private void saveLogInfo(){
        inetData.setLogin(fldLogin.getText());
        inetData.setPass(new String(fldPass.getPassword()));
        inetData.setServer(DoingPOST.checkServerPath(fldServer.getText()));
        inetMgr.setUser(inetData);
    }

    class ChangePassTask extends SwingWorker<Void, Void> {
        private String newpass;
        public ChangePassTask(String newpass){
            super();
            this.newpass = newpass;
        }
        @Override
        public Void doInBackground() {
            if (isCancelled()) return null;
            
            String query = DoingPOST.addKeyValue("login",inetData.getLogin());
            query = DoingPOST.addKeyValue(query,"pass",inetData.getPass());
            query = DoingPOST.addKeyValue(query,"action","change pass");
            query = DoingPOST.addKeyValue(query,"newpass",newpass);
            query = DoingPOST.addKeyValue(query,"ok","ok");
            
            DoingPOST post = new DoingPOST(inetData.getServer(),query);
            
            if (!post.isError()){
                log.append("Пароль успешно изменен\n" +
                        "Информация о новом пароле выслана на контактный email\n" +
                        "Новый пароль внесен в поле ввода пароля\n");
                fldPass.setText(newpass);
                inetData.setPass(newpass);
                saveLogInfo();
            }
            else log.append(post.getErrorMessage()+"\n");
            
            return null;
        }
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
        }
        
    }
    class ImportTask extends SwingWorker<Void, Void> {
        private boolean inter = true;
        private Vector<ProdGroup> groups = null;
        private Vector<ProductInBase> prods = null;
        private Vector<ComplexProduct> cmpls = null;
        
        private boolean domenu;
        private boolean doproducts;
        private boolean dodiary;
        int maj;
       
        public ImportTask(boolean menu,boolean prod,boolean diary){
            super();
            domenu = menu;
            doproducts = prod;
            dodiary = diary;
        }
        public boolean getIntermediate(){
            return inter;
        }
        @Override
        public Void doInBackground() {
            saveLogInfo();
            if (!domenu && !doproducts && !dodiary){
                return null;
            }
        //Всё запрашиваем в одном запросе
        if (!isCancelled()){
            log.append("Импорт начат, подождите...\n");
            String query = DoingPOST.addKeyValue("login",inetData.getLogin());
            query = DoingPOST.addKeyValue(query,"pass",inetData.getPass());
            query = DoingPOST.addKeyValue(query,"action","download");
            //entity
            if (domenu) 
                query = DoingPOST.addKeyValue(query,"entity[]","menu");
            if (doproducts)
                query = DoingPOST.addKeyValue(query,"entity[]","products");
            if (dodiary)
                query = DoingPOST.addKeyValue(query,"entity[]","diary");
            query = DoingPOST.addKeyValue(query,"ok","ok");
            
            DoingPOST post = new DoingPOST(inetData.getServer(),query);
            
            if (!post.isError() && !isCancelled()){
                log.append("Данные скачаны успешно\n");
                
                JSONParser parser = new JSONParser();
                JSONObject root = null;
                try{
                    root = (JSONObject) parser.parse(post.getAnswer());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                
                if (root==null){
                    log.append("Декодирование данных не получилось\n");
                    return null;
                }
                
                if (domenu && !isCancelled()){
                    log.append("Начинаем обработку меню\n");
                    JSONObject menu = (JSONObject)root.get("menu");
                    JSONObject coefs = (JSONObject)menu.get("coefs");
                    
                    User user = owner.getUser();
                    user.getFactors().setK1Value( Float.parseFloat((String) coefs.get("k1")) * 
                            user.getFactors().getBEValue() / 10f );
                    user.getFactors().setK2( Float.parseFloat((String) coefs.get("k2")) );
                    user.getFactors().setK3( Float.parseFloat((String) coefs.get("k3")) );

                    user.setSh1(new Sugar( Float.parseFloat((String) coefs.get("sh1")) ));
                    user.setSh2(new Sugar( Float.parseFloat((String) coefs.get("sh2")) ));
                    
                    JSONArray rows = (JSONArray)menu.get("rows");
                    
                    Vector<ProductInMenu> mn = new Vector<>();
                    Vector<ProductInMenu> sn = new Vector<>();
                    
                    Iterator<JSONObject> iterator = rows.iterator();
                    while (iterator.hasNext()) {
                        JSONObject row = iterator.next();
                        int issnack = Integer.parseInt((String)row.get("issnack"));
                        ProductInMenu pr = new ProductInMenu(
                                (String)row.get("name"),
                                Float.parseFloat((String) row.get("prot")),
                                Float.parseFloat((String) row.get("fat")),
                                Float.parseFloat((String) row.get("carb")),
                                Integer.parseInt((String) row.get("gi")),
                                Float.parseFloat((String) row.get("weight")),
                                0
                            );
                        if (issnack==1){
                            sn.add(pr);
                        }else{
                            mn.add(pr);
                        }
                    }
                    //Всё готово к замене
                    if (!isCancelled()){
                        MenuManager menuMgr =  new MenuManager(owner.getUser().getId(),
                             MenuManager.MENU_TABLE);

                        MenuManager snackMgr =  new MenuManager(owner.getUser().getId(),
                             MenuManager.SNACK_TABLE);
                        UsersManager usrsMgr = new UsersManager();
                        usrsMgr.updateFactors(user);
                        menuMgr.flush();
                        for(int i=0;(i<mn.size() && !isCancelled());i++){
                            menuMgr.addProd(mn.get(i));
                        }
                        snackMgr.flush();
                        for(int i=0;(i<sn.size() && !isCancelled());i++){
                            snackMgr.addProd(sn.get(i));
                        }

                        owner.userChanged();
                        log.append("Меню импортировано успешно\n");
                    }
                }
                if (doproducts && !isCancelled()){
                    log.append("Импорт продуктов начат, подождите...\n");
                    JSONObject products = (JSONObject)root.get("products");
                    JSONArray grs = (JSONArray)products.get("groups");
                    groups = new Vector<>();
                    for(Object gr: grs){
                        groups.add(new ProdGroup(
                                Integer.parseInt( (String)((JSONObject)gr).get("id")),
                                (String)((JSONObject)gr).get("name"),
                                0 ));
                    }
                    JSONArray prs = (JSONArray)products.get("products");
                    prods = new Vector<>();
                    for(Object pr: prs){
                        JSONObject o = (JSONObject)pr;
                        prods.add(new ProductInBase(
                                (String)o.get("name"),
                                Float.parseFloat((String) o.get("prot")),
                                Float.parseFloat((String) o.get("fat")),
                                Float.parseFloat((String) o.get("carb")),
                                Integer.parseInt((String) o.get("gi")),
                                Float.parseFloat((String) o.get("weight")),
                                Integer.parseInt((String) o.get("id")),
                                Integer.parseInt((String) o.get("cmpl"))==1,
                                Integer.parseInt((String) o.get("idgroup")),
                                Integer.parseInt((String) o.get("usage"))  ));
                    }
                    cmpls = new Vector<>();
                    JSONArray cmpx = (JSONArray)products.get("complex");
                    for(Object cx: cmpx){
                        JSONObject o = (JSONObject)cx;
                        cmpls.add(new ComplexProduct(
                                (String)o.get("name"),
                                Float.parseFloat((String) o.get("prot")),
                                Float.parseFloat((String) o.get("fat")),
                                Float.parseFloat((String) o.get("carb")),
                                Integer.parseInt((String) o.get("gi")),
                                Float.parseFloat((String) o.get("weight")),
                                0,
                                Integer.parseInt((String) o.get("idprod"))
                        ));
                    }
                    log.append("Импорт продуктов закончен...\n");
                }
                if (dodiary && !isCancelled()){
                    log.append("Импорт записей дневника начат, подождите...\n");
                    JSONObject diary = (JSONObject)root.get("diary");
                    Vector<DiaryUnit> duS = new Vector<>();
                    JSONArray arr = (JSONArray)diary.get("sugars");
                    for(Object s: arr){
                        JSONObject o = (JSONObject)s;
                        duS.add(new DiaryUnit(
                                -1,
                                Long.parseLong((String)o.get("time")),
                                (String)o.get("comm"),
                                Float.parseFloat((String)o.get("sh1")),
                                owner.getUser().getId() ));
                    }
                    Vector<DiaryUnit> duC = new Vector<>();
                    arr = (JSONArray)diary.get("comments");
                    for(Object s: arr){
                        JSONObject o = (JSONObject)s;
                        duC.add(new DiaryUnit(
                                -1,
                                Long.parseLong((String)o.get("time")),
                                (String)o.get("comm"),
                                owner.getUser().getId() ));
                    }
                    Vector<DiaryUnit> duM = new Vector<>();
                    arr = (JSONArray)diary.get("menus");
                    for(Object s: arr){
                        JSONObject o = (JSONObject)s;
                        duM.add(new DiaryUnit(
                                Integer.parseInt((String)o.get("id")),
                                Long.parseLong((String)o.get("time")),
                                (String)o.get("comm"),
                                Float.parseFloat((String)o.get("sh1")),
                                Float.parseFloat((String)o.get("sh2")),
                                new Factors(
                                        Float.parseFloat((String)o.get("k1")) * 
                                        owner.getUser().getFactors().getBEValue() / 10f,
                                        Float.parseFloat((String)o.get("k2")),
                                        Float.parseFloat((String)o.get("k3")),
                                        owner.getUser().getFactors().getBEValue()
                                ),
                                Float.parseFloat((String)o.get("dose")),
                                owner.getUser().getId(),
                                new ProductW("",
                                        Float.parseFloat((String)o.get("prot")),
                                        Float.parseFloat((String)o.get("fat")),
                                        Float.parseFloat((String)o.get("carb")),
                                        Integer.parseInt((String)o.get("gi")),
                                        Float.parseFloat((String)o.get("weight")))
                        ));
                    }
                    Vector<ComplexProduct> menuS = new Vector<>();
                    Vector<ComplexProduct> snackS = new Vector<>();
                    arr = (JSONArray)diary.get("records");
                    for(Object s: arr){
                        JSONObject o = (JSONObject)s;
                        boolean issnack = Integer.parseInt((String)o.get("issnack"))==1;
                        ComplexProduct pr = new ComplexProduct(
                            (String)o.get("name"),
                            Float.parseFloat((String)o.get("prot")),
                            Float.parseFloat((String)o.get("fat")),
                            Float.parseFloat((String)o.get("carb")),
                            Integer.parseInt((String)o.get("gi")),
                            Float.parseFloat((String)o.get("weight")),
                            -1,
                            Integer.parseInt((String)o.get("owner")) );
                        if (issnack){
                            snackS.add(pr);
                        }else{
                            menuS.add(pr);
                        }
                    }
                    Vector [] duR = new Vector[]{menuS,snackS}; //[menu,snack]
                    
                    DiaryManager dmgr = new DiaryManager(owner.getUser());
                    for(int i=0;i<duS.size() && !isCancelled();i++){
                        dmgr.addUnit(duS.get(i));
                    }
                    for(int i=0;i<duC.size() && !isCancelled();i++){
                        dmgr.addComment(duC.get(i));
                    }
                    for(int i=0;i<duM.size() && !isCancelled();i++){
                        Collection<ProductInMenu>  [] menu = 
                                new Collection[] {new ArrayList(),new ArrayList()};

                        Collection<ProductInMenu> snack = new ArrayList();
                        for(int m=0;m<2;m++){
                            for(int j=0;j<duR[m].size() && !isCancelled();j++){//Проходим по меню и перекусам
                                if ( ((ComplexProduct)duR[m].get(j)).getOwner()==duM.get(i).getId()){
                                    menu[m].add(new ProductInMenu(
                                            ((ComplexProduct)duR[m].get(j)).getName(),
                                            ((ComplexProduct)duR[m].get(j)).getProt(),
                                            ((ComplexProduct)duR[m].get(j)).getFat(),
                                            ((ComplexProduct)duR[m].get(j)).getCarb(),
                                            ((ComplexProduct)duR[m].get(j)).getGi(),
                                            ((ComplexProduct)duR[m].get(j)).getWeight(),
                                            -1
                                            ));
                                }
                            }
                        }
                        dmgr.addMenu(duM.get(i), menu[0], menu[1]);
                    }
                    log.append("Записи добавлены в локальную базу\nОчищаем записи на сервере...\n");
                    //Тут чистим на сервере
                    query = DoingPOST.addKeyValue("login",inetData.getLogin());
                    query = DoingPOST.addKeyValue(query,"pass",inetData.getPass());
                    query = DoingPOST.addKeyValue(query,"action","clean diary");
                    query = DoingPOST.addKeyValue(query,"ok","ok");

                    DoingPOST postInt = new DoingPOST(inetData.getServer(),query);

                    if (!postInt.isError()){
                        log.append("Успешно сделано\n");
                    }
                    else{
                        if (!isCancelled()){
                            log.append(postInt.getErrorMessage()+"\n");
                        }
                    }
                }
                if (!isCancelled() && !hasCheckedVer){//Тут обрабатываем версию
                    hasCheckedVer = true;
                    JSONObject ver = (JSONObject)root.get("ver");
                    
                    long v = (Long)ver.get("dcj");
                    if (MainFrame.PROGRAM_VER_INT>v){
                        log.append("Вы используете самую свежую версию программы\n");
                    }else if (MainFrame.PROGRAM_VER_INT<v){
                        log.append("Доступно обновление программы\n");
                    }
                }
            }
            else{
                if (!isCancelled()) log.append(post.getErrorMessage()+"\n");
                return null;
            }
        }
            return null;
        }
        public Vector<ProdGroup> getGroups(){
            return groups;
        }
        public Vector<ProductInBase> getProds(){
            return prods;
        }
        public Vector<ComplexProduct> getCmpl(){
            return cmpls;
        }

        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    class ExportTask extends SwingWorker<Void, Void> {
        
        private boolean inter = true;
        private boolean domenu;
        private boolean doproducts;

        public ExportTask(boolean m,boolean p){
            domenu = m;
            doproducts = p;
        }

        public boolean getIntermediate(){
            return inter;
        }
        
        @Override
        public Void doInBackground() {
            saveLogInfo();
            MenuManager menuMgr =  new MenuManager(owner.getUser().getId(),
                     MenuManager.MENU_TABLE);
            MenuManager snackMgr =  new MenuManager(owner.getUser().getId(),
                     MenuManager.SNACK_TABLE);
            //Надо проверить, если меню пустое и выбран только экспорт меню,
            //то выходим
            if (domenu && !doproducts && menuMgr.getMenu().isEmpty() && 
                    snackMgr.getMenu().isEmpty() ){
                log.append("Выгружать нечего\n");
                return null;
            }
        if (doproducts||domenu) log.append("Экспорт начат, подождите...\n");
        JSONObject root = new JSONObject();
        if (domenu && !isCancelled()){
            JSONObject menu = new JSONObject();
            JSONArray mnRows = new JSONArray();
            
            MenuManager [] mgr = new MenuManager[]{menuMgr,snackMgr};
            for (int i=0;i<mgr.length && !isCancelled();i++){
                for(ProductInMenu pr: new Vector<ProductInMenu>(mgr[i].getMenu())){
                    JSONObject mnItem = new JSONObject();
                    mnItem.put("name", pr.getName());
                    mnItem.put("prot", pr.getProt());
                    mnItem.put("fat",pr.getFat());
                    mnItem.put("carb", pr.getCarb());
                    mnItem.put("gi",pr.getGi());
                    mnItem.put("weight",pr.getWeight());
                    mnItem.put("issnack",i);

                    mnRows.add(mnItem);
                }
            }
            menu.put("rows", mnRows);
            //Теперь коэф-ты
            JSONObject coefs = new JSONObject();
            User us = owner.getUser();
            coefs.put("k1",
                    us.getFactors().getK1Value()*10/us.getFactors().getBEValue());
            coefs.put("k2",us.getFactors().getK2());
            coefs.put("k3",us.getFactors().getK3());
            coefs.put("sh1",us.getSh1().getValue());
            coefs.put("sh2",us.getSh2().getValue());
            menu.put("coefs",coefs);

            root.put("menu", menu);
            log.append("Меню добавлено\n");
        }
        if (doproducts && !isCancelled()){
            
            GroupManager grMgr = new GroupManager();
            Vector<ProdGroup> groups = new Vector<>(grMgr.getGroups(GroupManager.ONLY_EXISTS_GROUPS));
            ProductManager prMgr = new ProductManager();
            //Vector<ProductInBase> prods = new Vector<>(prMgr.getAllProducts());
            ComplexManager cmplMgr = new ComplexManager();
            //Vector<ComplexProduct> cmpls = new Vector<>();
            //Каждая группа содержит в себе свои продукты, каждый 
            //сложный продукт содержит в себе состав
            JSONArray grs = new JSONArray();
            if (groups.size()>0 && !isCancelled()){
                for(int i=0;(i<groups.size() && !isCancelled());i++){
                    JSONObject gr = new JSONObject();
                    gr.put("name",groups.get(i).getName());
                    gr.put("sortInd", groups.get(i).getSortInd());
                    //Теперь в группу добавим продукты
                    Vector<ProductInBase> prods = 
                            new Vector<>(prMgr.getProductsFromGroup(groups.get(i).getId()));
                    JSONArray prs = new JSONArray();
                    for(int j=0;j<prods.size() && !isCancelled();j++){
                        JSONObject pr = new JSONObject();
                        pr.put("name", prods.get(j).getName());
                        pr.put("prot", prods.get(j).getProt());
                        pr.put("fat", prods.get(j).getFat());
                        pr.put("carb", prods.get(j).getCarb());
                        pr.put("gi", prods.get(j).getGi());
                        pr.put("weight", prods.get(j).getWeight());
                        pr.put("usage", prods.get(j).getUsage());
                        pr.put("complex",prods.get(j).isComplex());
                        if (prods.get(j).isComplex() && !isCancelled()){
                            //Добавляем состав сложного
                            JSONArray cmpls = new JSONArray();
                            Vector<ComplexProduct> content = 
                                    new Vector<>(cmplMgr.getComposition(prods.get(j).getId()));
                            for(int k=0;k<content.size() && !isCancelled();k++){
                                JSONObject cm = new JSONObject();
                                cm.put("name", content.get(k).getName());
                                cm.put("prot", content.get(k).getProt());
                                cm.put("fat", content.get(k).getFat());
                                cm.put("carb", content.get(k).getCarb());
                                cm.put("gi", content.get(k).getGi());
                                cm.put("weight", content.get(k).getWeight());
                                cmpls.add(cm);
                            }
                            pr.put("content",cmpls);
                        }
                        prs.add(pr);
                    }
                    gr.put("prods",prs);
                    grs.add(gr);
                }
                root.put("products",grs );
            }
        }
        
        //Теперь формируем запрос
        String query = DoingPOST.addKeyValue("login",inetData.getLogin());
        query = DoingPOST.addKeyValue(query,"pass",inetData.getPass());
        query = DoingPOST.addKeyValue(query,"action","upload");
        query = DoingPOST.addKeyValue(query, "data", root.toJSONString());
        query = DoingPOST.addKeyValue(query,"ok","ok");

        if (!isCancelled()){
            DoingPOST post = new DoingPOST(inetData.getServer(),query);
            
            if (!post.isError()){
                log.append("Экспорт успешен\n");
            }
            else{
                log.append(post.getErrorMessage()+"\n");
            }
        }
        
            return null;
        }

        /*
         * Executed in event dispatch thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private JComponent createBottomPane(){
        JPanel pane = new JPanel();
        log = new JTextArea();
        log.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e){
                log.setCaretPosition(log.getText().length()-1);
            }
            @Override
            public void removeUpdate(DocumentEvent e){}
            @Override
            public void changedUpdate(DocumentEvent e){}

        });
        log.setEditable(false);
        scr = new JScrollPane(log);
        scr.setPreferredSize(new Dimension(100,100));
        prBar = new JProgressBar();
        JButton closeForm = new JButton("Закрыть");
        closeForm.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ev){
                closeDialog();
            }
        });

        GroupLayout layout = new GroupLayout(pane);
        pane.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(scr)
                .addComponent(prBar)
                .addComponent(closeForm,GroupLayout.Alignment.CENTER)
             );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(scr)
                .addComponent(prBar)
                .addComponent(closeForm)
             );
        return pane;
    }
    private JComponent createTopPane(){
        MyFocusListener fcLst = new MyFocusListener();


        JPanel pane = new JPanel();
        JLabel lblLogin = new JLabel("Логин:");
        lblLogin.setHorizontalAlignment(SwingConstants.TRAILING);
        fldLogin = new JTextField();
        //fldLogin.setColumns(20);
        fldLogin.setText(inetData.getLogin());
        fldLogin.addFocusListener(fcLst);
        
        JLabel lblPass = new JLabel("Пароль:");
        lblPass.setHorizontalAlignment(SwingConstants.TRAILING);
        fldPass = new JPasswordField();
        //fldPass.setColumns(20);
        fldPass.setText(inetData.getPass());
        btnChPass = new JButton();
        btnChPass.setMargin(new Insets(0,0,0,0));
        btnChPass.setToolTipText("Сменить пароль");
        //Look for the image.
        String imgLocation =  "buttons/" + 
                settings.getIn().getSizedPath(false) + 
                "chpass.png";
        btnChPass.setIcon(new ImageIcon(InetBackUpDialog.class.getResource(imgLocation)));
        btnChPass.setActionCommand(CHANGE_PASS);
        btnChPass.addActionListener(this);


        JLabel lblServer = new JLabel("Адрес сервера:");
        lblServer.setHorizontalAlignment(SwingConstants.TRAILING);
        fldServer = new JTextField(inetData.getServer());
        //fldServer.setColumns(20);
        fldServer.setEditable(false);
        fldServer.addFocusListener(fcLst);
        fldServer.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount()%2==0){
                    ((JTextField)e.getSource()).setEditable(true);
                    ((JTextField)e.getSource()).removeMouseListener(this);
                }
            }
        });
        
        //GroupLayout layout = new GroupLayout(pane);
        pane.setLayout(new GridBagLayout());

        pane.add(lblServer,
          new GridBagConstraints(0,0,1,1,0.5,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(5,5,2,2), 0, 0));
        pane.add(fldServer,
          new GridBagConstraints(1,0,2,1,0.5,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(5,0,2,5), 0, 0));

        pane.add(lblLogin,
          new GridBagConstraints(0,1,1,1,0.5,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,5,2,2), 0, 0));
        pane.add(fldLogin,
          new GridBagConstraints(1,1,2,1,0.5,0, GridBagConstraints.LINE_START,
          GridBagConstraints.HORIZONTAL, new Insets(2,0,2,5), 0, 0));

        pane.add(lblPass,
          new GridBagConstraints(0,2,1,1,0.5,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,5,5,2), 0, 0));
        pane.add(fldPass,
          new GridBagConstraints(1,2,1,1,0.6,0, GridBagConstraints.CENTER,
          GridBagConstraints.HORIZONTAL, new Insets(2,0,5,2), 0, 0));
        pane.add(btnChPass,
          new GridBagConstraints(2,2,1,1,0.01,0, GridBagConstraints.LINE_END,
          GridBagConstraints.NONE, new Insets(2,0,5,5), 0, 0));

        

        return pane;
    }
    private JComponent createSyncPane(){
        JLabel lblSync = new JLabel(
                "<html><center>Синхронизация баз программы<br>с базами в интернет</center></html>"
                );
        lblSync.setHorizontalAlignment(SwingConstants.CENTER);
        btnSync = new JButton("Синхронизировать");
        btnSync.addActionListener(this);
        btnSync.setActionCommand(BEGIN_SYNC);

        JLabel explain = new JLabel(
                "<html>Сначала программа импортирует в локальную базу,<br>" +
                "ориентируясь на выбранный приоритет баз,<br>" +
                "продукты с сервера в режиме объединения, а затем<br>" +
                "экспортирует на сервер уже объединенную локальную базу.<br>" +
                "В результате локальная база и база на сервере становятся<br>" +
                "одинаковыми.</html>");
        rbLocalMajor = new JRadioButton(
                "Приоритет продуктов в локальной базе");
        rbServerMajor = new JRadioButton(
                "Приоритет продуктов на сервере");
        
        rbLocalMajor.setSelected(true);
        ButtonGroup bgr = new ButtonGroup();
        bgr.add(rbServerMajor);
        bgr.add(rbLocalMajor);

        JPanel pane = new JPanel();

        GroupLayout layout = new GroupLayout(pane);
        pane.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(lblSync)
                    
                    .addComponent(explain)

                    .addComponent(rbServerMajor,GroupLayout.Alignment.LEADING)
                    .addComponent(rbLocalMajor,GroupLayout.Alignment.LEADING)
                    .addComponent(btnSync)
                );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addComponent(lblSync)
                    
                    .addComponent(explain)
                    .addComponent(rbServerMajor)
                    .addComponent(rbLocalMajor)
                    .addComponent(btnSync)
                );
        return pane;
    }
    private JComponent createExportPane(){
        JLabel lblTop = new JLabel("Выберите сохраняемые на сервер элементы");
        chExMenu = new JCheckBox("Меню, коэффициенты и рассчет");
        chExProducts = new JCheckBox(
                "<html>База продуктов <i>база на сервере<br>" +
                "будет заменена на сохраняемую базу</i></html>");
        

        btnStExport = new JButton("Начать");
        btnStExport.addActionListener(this);
        btnStExport.setActionCommand(BEGIN_EXPORT);

        JPanel pane = new JPanel();

        GroupLayout layout = new GroupLayout(pane);
        pane.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                    .addComponent(lblTop)
                    .addComponent(chExMenu)
                    .addComponent(chExProducts)
                    .addComponent(btnStExport,GroupLayout.Alignment.CENTER)
                    );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addComponent(lblTop)
                    .addComponent(chExMenu)
                    .addComponent(chExProducts)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStExport)
                    );

        return pane;
    }

    private JRadioButton createRdBtn(String name,String tooltip){
        final JRadioButton rb = new JRadioButton(name);
        final JPanel tooltipPane = new JPanel();
        JLabel lbl = new JLabel(tooltip);
        
        tooltipPane.setBorder(BorderFactory.createRaisedBevelBorder());
        tooltipPane.add(lbl);
        final JWindow w = new JWindow(this);
        w.add(tooltipPane);
        w.setSize(lbl.getPreferredSize().width+30, lbl.getPreferredSize().height+20);
        rb.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent ev){
                if ( ((JRadioButton)ev.getSource()).isEnabled() ){
                    int x = ((JRadioButton)ev.getSource()).getLocationOnScreen().x + 30;
                    int y = ((JRadioButton)ev.getSource()).getLocationOnScreen().y +
                            ((JRadioButton)ev.getSource()).getHeight()+1;
                    w.setLocation(x, y);
                    w.setVisible(true);
                }
            }
            @Override
            public void mouseExited(MouseEvent ev){
                if ( ((JRadioButton)ev.getSource()).isEnabled() && w.isVisible())
                w.setVisible(false);
            }
        });


        return rb;
    }
    private JComponent createImportPane(){
        JLabel lblTop = new JLabel("Выберите загружаемые с сервера элементы");
        chImMenu = new JCheckBox("Меню, коэффициенты и рассчет");
        chImProducts = new JCheckBox("База продуктов");
        
        rbIm1 = createRdBtn("Объединение",
                    "<html>В этом режиме происходит объединение<br>" +
                       "базы с сервера и локальной базы, если на<br>" +
                       "сервере есть какие то продукты или группы,<br>" +
                       "которых нет в локальной базе, то они будут<br>" +
                       "добавлены. Это предпочтительный режим</html>");
        rbIm2 = createRdBtn("Добавление",
                    "<html>В этом режиме происходит добавление<br>" +
                       "баз с сервера к локальной базе, если<br>" +
                       "на сервере есть какие то продукты или<br>" +
                       "группы, то они будут добавлены после<br>" +
                       "существующих продуктов.</html>"
                );
        rbIm3 = createRdBtn("Очистка и добавление",
                "<html>В этом режиме существующая локально база<br>" +
                "продуктов удаляется <u>БЕЗВОЗВРАТНО</u> и<br>" +
                "заменяется базой продуктов, скачанной с<br>" +
                "сервера.<br><br>" +
                "<u>Используйте с осторожностью!</u></html>"
                );

        chImProducts.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent ev){
                rbIm1.setEnabled(chImProducts.isSelected());
                rbIm2.setEnabled(chImProducts.isSelected());
                rbIm3.setEnabled(chImProducts.isSelected());

            }
        });
        rbIm1.setEnabled(false);
        rbIm2.setEnabled(false);
        rbIm3.setEnabled(false);

        ButtonGroup btnGr = new ButtonGroup();
        btnGr.add(rbIm1);
        btnGr.add(rbIm2);
        btnGr.add(rbIm3);
        rbIm1.setSelected(true);

        chImDiary = new JCheckBox("Записи дневника");

        btnStImport = new JButton("Начать");
        btnStImport.addActionListener(this);
        btnStImport.setActionCommand(BEGIN_IMPORT);

        JPanel pane = new JPanel();

        GroupLayout layout = new GroupLayout(pane);
        pane.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                    .addComponent(lblTop)
                    .addComponent(chImMenu)
                    .addComponent(chImProducts)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15)
                        .addComponent(rbIm1)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15)
                        .addComponent(rbIm2)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15)
                        .addComponent(rbIm3)
                    )
                    .addComponent(chImDiary)
                    .addComponent(btnStImport,GroupLayout.Alignment.CENTER)
                    );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addComponent(lblTop)
                    .addComponent(chImMenu)
                    .addComponent(chImProducts)
                    .addComponent(rbIm1)
                    .addComponent(rbIm2)
                    .addComponent(rbIm3)
                    .addComponent(chImDiary)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStImport)
                    );

        return pane;
    }
}
