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

package diacalcj;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Set;
import javax.swing.*;
import lookout.MainFrame;
import lookout.settings.ProgramSettings;
import manager.DBVersionCorrect;
import maths.User;

public class Main{
    static private MainFrame mf = null;
    static private boolean flag = false;
    static private boolean realdraw = true;
    static MenuItem showItem;
    static MenuItem exitItem;
    static JWindow w;
    static TrayIcon trayIcon;
    static ServerSocket serverSocket;

    static private ErrorReport report = null;

    public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 44331;

    /**
     * 
     * @param factor - Отношение текущего dpi к 96dpi
     * @param mode  - Два вида размера интерфейса программы, 4 - большой
     *                3 - маленький. Если mode==4, то фон должен быть на 
     *                1.17 больше.
     */
    public static void setDefaultFontSize(float factor,int mode) {

        Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
        Object[] keys = keySet.toArray(new Object[keySet.size()]);

        for (Object key : keys) {
            if (key != null && key.toString().toLowerCase().contains("font")) {
                Font font = UIManager.getDefaults().getFont(key);
                if (font != null) {
                    font = font.deriveFont(font.getSize2D()*factor*
                            (mode==4?1.17f:1f));
                    UIManager.put(key, font);
                }

            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
	    // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
            //UIManager.setLookAndFeel( new com.nilo.plaf.nimrod.NimRODLookAndFeel());
        }
        catch (UnsupportedLookAndFeelException | 
                ClassNotFoundException | 
                InstantiationException | 
                IllegalAccessException e) {
            // handle exception
            e.printStackTrace();
            return;
        }
        // handle exception
        // handle exception
        // handle exception
        //Прежде всего проверяем версию java
        String ver = System.getProperty("java.version");
        String [] dig = ver.replace('.','=').split("=");
        int verI = (new Integer(dig[0]))*10 + new Integer(dig[1]);
        if (verI<17){
            JOptionPane.showMessageDialog(null,
                    "Запуск программы невозможен!\n\n"+
                    "Для запуска программы должна быть\n" +
                    "установлена java машина версии не ниже 1.7\n\n" +
                    "У вас установлена java "+ver+"\n" +
                    "Необходимо обновить java машину",
                    "Ошибка - "+MainFrame.PROGRAM_VERSION, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        
        //Примем, что по умолчанию фонт 12
        setDefaultFontSize(ProgramSettings.getInstance().getIn().getSizeFactor(),
                ProgramSettings.getInstance().getIn().getSize() );
        
        //Блок проверки на уже запущенный экземпляр программы
        try {
            serverSocket =  new ServerSocket(SINGLE_INSTANCE_NETWORK_SOCKET, 10, InetAddress
                    .getLocalHost());
        }
        catch (BindException exc) {
            // Another process is listening on that port.
            // Probably anoher instance of our app.
            JOptionPane.showMessageDialog(null,"Запущена еще одна копия программы\n" +
                    "У Вас есть возможность запускать только один экземпляр",
                    "Ошибка",JOptionPane.ERROR_MESSAGE);
            return;
        }
        catch (IOException exc) {
            // could not listen on that port for some other reason
            JOptionPane.showMessageDialog(null, "Ошибка доступа к порту:\n" + 
                    exc.getLocalizedMessage(),
                    "Ошибка",JOptionPane.ERROR_MESSAGE);
            return;
        }

        LoggingOutputStream los = new LoggingOutputStream(System.err);
        System.setErr(new PrintStream(los, true));
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                 report = new ErrorReport(MainFrame.PROGRAM_VERSION);
            }
        });

        Stack.setAlive(true);
        
        if (args.length>0){
            for(String arg:args){
                if (arg.toLowerCase().equals("tray")){
                    flag = true;
                }
                else if (arg.toLowerCase().equals("old")){
                    realdraw = false;
                }
            }
        }

        if (flag){
            flag = createGUIwithTray();
        }
        if (!flag){
            createGUIwithoutTray();
        }
}
private static void createGUIwithoutTray(){
        w = new JWindow();
        w.setSize(new Dimension(
                ProgramSettings.getInstance().getIn().getSizedValue(250),
                ProgramSettings.getInstance().getIn().getSizedValue(75)));
        w.setLocationRelativeTo(null);
        JPanel pane = new JPanel(new BorderLayout());
        pane.setBorder(BorderFactory.createRaisedBevelBorder());
        pane.add(new JLabel("<html>Программа загружается...</html>",
                createImageIcon("images/MainIcon.png"),
                SwingConstants.CENTER), BorderLayout.CENTER);
        w.add(pane);
        w.setVisible(true);
        
        new DBVersionCorrect(w);
        mf = new MainFrame();
        mf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mf.addWindowListener(new WindowAdapter(){
               @Override
               public void windowClosing(WindowEvent e){
                   //System.out.println("closing")  ;
                   Stack.setAlive(false);
                     mf.setVisible(false);
                     mf.storeSettings();
                     mf.dispose();
                     if (report!=null && !report.isVisible()){
                        //Форма вывода невидна, закрываем ее
                        report.stopThread();
                        report.dispose();
                        System.exit(0);
                    }else if (report!=null){
                        report.stopThread();
                    }
                     
               }
               @Override
               public void windowDeactivated(WindowEvent e){
                   if (!mf.isVisible()){
                       //System.out.println("deactivated") ;
                       Stack.setAlive(false);
                       mf.dispose();
                       if (report!=null && !report.isVisible()){
                            //Форма вывода невидна, закрываем ее
                            report.stopThread();
                            report.dispose();
                            System.exit(0);
                        }else if (report!=null){
                            report.stopThread();
                        }
                   }
               }
            });
        mf.setVisible(true);
        
           
        w.setVisible(false);
        w.dispose();
   
}
private static boolean createGUIwithTray(){
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return false;
        }
        // Create a popup menu components
        showItem = new MenuItem("Показать");
        exitItem = new MenuItem("Выход");

        final PopupMenu popup = new PopupMenu();
        //Add components to popup menu
        popup.add(showItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon =
                new TrayIcon(createImage(true),
                "DiaCalcJ",
                popup);
        final SystemTray tray = SystemTray.getSystemTray();

        trayIcon.setImageAutoSize(true);


        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
            return false;
        }

        trayIcon.addMouseListener(new MouseAdapter(){
                @Override
            public void mouseClicked(MouseEvent e){
                if (SwingUtilities.isLeftMouseButton(e)){
                    showGUI();
                }
            }
        });

        showItem.addActionListener(new ActionListener() {
                @Override
            public void actionPerformed(ActionEvent e) {
                showGUI();
            }
        });

        exitItem.addActionListener(new ActionListener() {
                @Override
            public void actionPerformed(ActionEvent e) {
                if (mf!=null){
                    if (mf.isVisible()){
                        mf.setVisible(false);
                        mf.storeSettings();
                    }
                    mf.dispose();

                }
                tray.remove(trayIcon);
                Stack.setAlive(false);
                if (report!=null && !report.isVisible()){
                   //Форма вывода невидна, закрываем ее
                   report.stopThread();
                   report.dispose();
                   System.exit(0);
                }else if (report!=null){
                   report.stopThread();
                }
                
            }
        });

        return true;
    }
    private static void showGUI(){
        if (mf==null){
            showItem.setLabel("Скрыть");
            w = new JWindow();
            w.setSize(new Dimension(250,75));
            w.setLocationRelativeTo(null);
            JPanel pane = new JPanel(new BorderLayout());
            pane.setBorder(BorderFactory.createRaisedBevelBorder());
            pane.add(new JLabel("<html>Программа загружается...</html>",
                createImageIcon("images/MainIcon.png"),
                SwingConstants.CENTER), BorderLayout.CENTER);
            w.add(pane);
            w.setVisible(true);
            w.addWindowListener(new WindowAdapter(){
                @Override
                public void windowOpened(WindowEvent e){
                    if (w!=null){

                  new DBVersionCorrect(w);
                  mf = new MainFrame();
                  mf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                  mf.addPropertyChangeListener(new PropertyChangeListener(){
                      @Override
                      public void propertyChange(PropertyChangeEvent e){
                        if (e.getPropertyName().equals(MainFrame.USER_CHANGED)){
                            trayIcon.setToolTip(
                                    "DiaCalcJ - "+((User)e.getNewValue()).getName()
                                    );
                        }
                      }
                  });
                  trayIcon.setToolTip(
                                    "DiaCalcJ - "+mf.getUser().getName()
                                    );
                  mf.addWindowListener(new WindowAdapter(){
                    @Override
                    public void windowClosing(WindowEvent e){
                        SwingUtilities.invokeLater(new Runnable(){
                            @Override
                            public void run(){
                                mf.setVisible(false);
                                mf.hideChildren();
                                showItem.setLabel("Показать");
                                mf.storeSettings();
                            }
                        });
                        //System.out.println("testing2");
                    }
                    @Override
                    public void windowDeactivated(WindowEvent e){
                        if (!mf.isVisible()){
                            showItem.setLabel("Показать");
                        }
                        //System.out.println("testing");
                    }
                  });
                  mf.setVisible(true);
                  w.setVisible(false);
                  w.dispose();
              }
                }
        });
        }else{
            if (mf.isVisible()){
                SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run(){
                        mf.setVisible(false);
                        showItem.setLabel("Показать");
                        mf.storeSettings();
                        mf.hideChildren();
                     }
                });
            }else{
                SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run(){
                        mf.setVisible(true);
                        showItem.setLabel("Скрыть");
                        mf.setState(JFrame.NORMAL);
                        mf.toFront();
                     }
                });
            }
        }
    }

    private static Image createImage(boolean drawbackground) {
        BufferedImage i = new BufferedImage(32, 32,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = (Graphics2D) i.getGraphics();

        //System.out.println(i.getWidth()+" "+i.getHeight());
        
        if (realdraw){
            g2.drawImage(createImageIcon("images/MainIcon.png").getImage(),
                    0, 0, null);
        }else{
            if (drawbackground){
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(0, 0, i.getWidth(), i.getHeight());
            }
            int [] xs1 = {0, 31,16, 0};
            int [] ys1 = {31,31,22,31};
            Polygon p = new Polygon(xs1,ys1,4);
            g2.setColor(Color.YELLOW);
            g2.fill( p );

            g2.setColor(new Color(200,140,0));
            g2.fill(new Ellipse2D.Float(4f, 0f, 24f, 24f));

            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Float(9f, 4f, 8f, 8f));
        }
        g2.dispose();
        return i;
    }
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MainFrame.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


}
