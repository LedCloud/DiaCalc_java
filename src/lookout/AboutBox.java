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
import java.awt.event.*;

import java.io.*;
import java.net.*;
import lookout.settings.ProgramSettings;


public class AboutBox extends JDialog implements ActionListener{
    private static final String OK = "ok";
    private static final String COPY_DIACALC = "http://www.diacalc.org/";
    private static final String COPY_DIACALC_EMAIL = "diacalc@ya.ru";
    private static final String DIA_CLUB_SITE = "http://www.dia-club.ru/";
    private static final String DIA_CLUB_EMAIL = "info@dia-club.ru";
    private JButton btnOk;
    
    private JTextArea thanks;
    private JScrollPane scr;
    private Thread t;
    private boolean shouldStop=false;
    private JFrame owner;
    private JTextField lblSite;
    private JTextField lblEmail;
    private JTextField lblSiteClub;
    private JTextField lblEmailClub;
    private Desktop desktop = null;
    private ProgramSettings settings;

    public AboutBox(JFrame owner){
        super();
        settings = ProgramSettings.getInstance();
        setIconImage(new ImageIcon(AboutBox.class
            .getResource("images/MainIcon.png")).getImage());
        this.owner = owner;
        // Before more Desktop API is used, first check
        // whether the API is supported by this particular
        // virtual machine (VM) on this particular host.
        if (Desktop.isDesktopSupported()){
            desktop = Desktop.getDesktop();
        }
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        setTitle("О программе DiaCalcJ");
        btnOk = new JButton("Закрыть");
        btnOk.addActionListener(this);
        btnOk.setActionCommand(OK);
        btnOk.setDefaultCapable(true);
        getRootPane().setDefaultButton(btnOk);


        final ImageIcon connie = createImageIcon("images/connie.gif");
        final ImageIcon progIcon = createImageIcon("images/progIcon.png");
        final JLabel lblIcon = new JLabel(progIcon);

        lblIcon.addMouseListener(new MouseAdapter(){
            @Override
           public void mouseClicked(MouseEvent e) {
             if (SwingUtilities.isRightMouseButton(e) &&
                     e.getClickCount() % 2 == 0)  {
                lblIcon.setIcon(connie); }
                
            }
        });

        thanks = new JTextArea(5,20);
        thanks.setEditable(false);
        thanks.setLineWrap(true);
        thanks.setWrapStyleWord(true);
        scr = new JScrollPane(thanks);
        
        addWindowListener(new WindowAdapter(){
            @Override
            public  void 	windowClosing(WindowEvent e){
                shouldStop = true;
                setVisible(false);
            }

        });

        DesktopAdapter dadpt = new DesktopAdapter();
        JLabel lblAbout = new JLabel("<html><font size=\""+settings.getIn().getSizedValue(6)+
                "\">"+MainFrame.PROGRAM_VERSION
                +"</font><br><br>" +
                "<font size=\""+settings.getIn().getSizedValue(4)+
                "\">DiaCalcJ, DiaCalc Copyright (C) 2006-2017<br>" +
                "Константин Топоров</font></html>");
        lblSite = new JTextField(COPY_DIACALC);
        lblSite.setEditable(false);
        lblSite.setForeground(Color.BLUE);
        if (Desktop.isDesktopSupported()){
            lblSite.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblSite.addMouseListener(dadpt);
        }

        JButton copySite = makeButton("earth","Копия", 
                COPY_DIACALC,"Копировать адрес в буффер обмена");
        copySite.setPreferredSize(new Dimension(
                settings.getIn().getSizedValue(32),settings.getIn().getSizedValue(17)));

        lblEmail = new JTextField(COPY_DIACALC_EMAIL);
        lblEmail.setEditable(false);
        lblEmail.setForeground(Color.BLUE);
        if (Desktop.isDesktopSupported()){
            lblEmail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblEmail.addMouseListener(dadpt);
        }
        JButton copyEmail = makeButton("email","Копия",
                COPY_DIACALC_EMAIL,"Копировать адрес в буффер обмена");
        copyEmail.setPreferredSize(new Dimension(
                settings.getIn().getSizedValue(32),settings.getIn().getSizedValue(17)));
        
        JLabel lblJuris = new JLabel("<html>Алгоритмы расчета доз<br>" +
                "Copyright (C) 2000-2008 Юрий Кадомский</html>");
        
        lblSiteClub = new JTextField(DIA_CLUB_SITE);
        //lblSiteClub.setFont(settings.getIn().getFont(lblSiteClub.getFont()));
        lblSiteClub.setEditable(false);
        lblSiteClub.setForeground(Color.BLUE);
        if (Desktop.isDesktopSupported()){
            lblSiteClub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblSiteClub.addMouseListener(dadpt);
        }
        JButton copySiteClub = makeButton("earth","Копировать",
                DIA_CLUB_SITE,"Копировать адрес в буффер обмена");
        copySiteClub.setPreferredSize(new Dimension(
                settings.getIn().getSizedValue(32),settings.getIn().getSizedValue(17)));
        
        
        lblEmailClub = new JTextField(DIA_CLUB_EMAIL);
        lblEmailClub.setEditable(false);
        lblEmailClub.setForeground(Color.BLUE);
        if (Desktop.isDesktopSupported()){
            lblEmailClub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblEmailClub.addMouseListener(dadpt);
        }
        JButton copyEmailClub = makeButton("email","Копировать",
                DIA_CLUB_EMAIL,"Копировать адрес в буффер обмена");
        copyEmailClub.setPreferredSize(new Dimension(
                settings.getIn().getSizedValue(32),settings.getIn().getSizedValue(17)));
        
        JLabel lblThanks = new JLabel("<html><font size=\""+settings.getIn().getSizedValue(4)+"\">Благодарности<br>" +
                "Спасибо всем тем, кто мне помогал!</font></html>");


        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

       layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(lblIcon,GroupLayout.Alignment.CENTER)
                        .addComponent(lblThanks)
                        .addComponent(scr))
                    .addGroup(layout.createParallelGroup()
                        .addComponent(lblAbout)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblSite)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(copySite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
          GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblEmail)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(copyEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
          GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblJuris)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblSiteClub)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(copySiteClub, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
          GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblEmailClub)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(copyEmailClub, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
          GroupLayout.PREFERRED_SIZE))
                        
                        ))

                .addComponent(btnOk,GroupLayout.Alignment.CENTER));

       layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblIcon)
                        .addComponent(lblThanks)
                        .addComponent(scr))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblAbout)
                        .addGroup(layout.createParallelGroup()
                            .addComponent(lblSite)
                            .addComponent(copySite,GroupLayout.Alignment.TRAILING))
                        .addGroup(layout.createParallelGroup()
                            .addComponent(lblEmail)
                            .addComponent(copyEmail,GroupLayout.Alignment.TRAILING))
                        .addComponent(lblJuris)
                        .addGroup(layout.createParallelGroup()
                            .addComponent(lblSiteClub)
                            .addComponent(copySiteClub,GroupLayout.Alignment.TRAILING))
                        .addGroup(layout.createParallelGroup()
                            .addComponent(lblEmailClub)
                            .addComponent(copyEmailClub,GroupLayout.Alignment.TRAILING))
                        
                        ))
                .addComponent(btnOk));


        readThanks();
        pack();
        setResizable(false);
        thanks.setCaretPosition(0);

        startVScroll();
        this.setLocationRelativeTo(owner);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    @Override
    public void actionPerformed(ActionEvent ev){
        String cmd = ev.getActionCommand();
        if (COPY_DIACALC.equals(cmd)){
            lblSite.selectAll();
            lblSite.copy();
        } else if (COPY_DIACALC_EMAIL.equals(cmd)){
            lblEmail.selectAll();
            lblEmail.copy();
        }
        else if (DIA_CLUB_SITE.equals(cmd)){
            lblSiteClub.selectAll();
            lblSiteClub.copy();
        }
        else if (DIA_CLUB_EMAIL.equals(cmd)){
            lblEmailClub.selectAll();
            lblEmailClub.copy();
        }
        else if (OK.equals(cmd)) { shouldStop=true; setVisible(false); }
        
    }
    
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = AboutBox.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
   
    private void readThanks(){
        
        InputStream is = null;
        BufferedReader br = null;
        String line = "";
        String list = "";

    try {
      is = AboutBox.class.getResourceAsStream("texts/thanks.txt");
      br = new BufferedReader(new InputStreamReader(is,"UTF8"));
      while (null != (line = br.readLine())) {
          if (list.length()==0) list = line;
          else list += "\n" + line;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (br != null) br.close();
        if (is != null) is.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
        thanks.append(list);

    }

    private void startVScroll(){
      t = new Thread() {
        // Переопределяем в нем метод run
      
            @Override
      public void run(){

       thanks.setCaretPosition(0);
       
       JScrollBar vs = scr.getVerticalScrollBar();

      while (!shouldStop){
            int i;
            try{
               Thread.sleep(1000);
            } catch (Exception e) {}
            while (!shouldStop){
                i = vs.getValue();
                vs.setValue(vs.getValue()+1);
                if (vs.getValue()==i) break;
                //System.out.println("v="+vs.getValue());
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            try{
               Thread.sleep(1000);
            } catch (Exception e) {}
            while (!shouldStop){
                i = vs.getValue();
                vs.setValue(vs.getValue()-1);
                if (vs.getValue()==i) break;
                //System.out.println("v="+vs.getValue());
                try {
                    Thread.sleep(5);
                } catch (Exception e) {}
            }

        }
      }
    };
        // И теперь мы запускаем наш поток
        t.start();
    }

    private JButton makeButton(String imageName, String altText,
            String actionCommand, String toolTipText) {
        String imgLocation =  "buttons/" + 
                settings.getIn().getSizedPath(false) + 
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
    class DesktopAdapter extends MouseAdapter{
        @Override
           public void mouseClicked(MouseEvent e) {
            if (Desktop.isDesktopSupported()){
                if (SwingUtilities.isLeftMouseButton(e))  {
                    JTextField fld = (JTextField)e.getSource();
                    String adr = fld.getText();
                    URI uri = null;
                    if (adr.contains("http") && desktop.isSupported(Desktop.Action.BROWSE)){

                        try {
                            uri = new URI(adr);
                            desktop.browse(uri);
                        } catch(Exception exc){}
                    }
                    else if (desktop.isSupported(Desktop.Action.MAIL)){
                        try {
                            uri = new URI("mailto", adr, null);
                            desktop.mail(uri);
                        }catch(Exception exc){}
                    }
                }
            }
        }

    }
}