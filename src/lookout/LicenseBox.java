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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import lookout.settings.ProgramSettings;

public class LicenseBox extends JDialog implements ActionListener{
    private static final String CLOSE = "that's all";
    private JTextArea areaLicense;
    private JScrollPane scr;
    private ProgramSettings settings;
    
    public LicenseBox(JFrame owner){
        super();
        settings = ProgramSettings.getInstance();
        setIconImage(new ImageIcon(LicenseBox.class
            .getResource("images/MainIcon.png")).getImage());
        this.setTitle("Лицензионное соглашение");
        JLabel title = new JLabel("<html><font size=\""+
                settings.getIn().getSizedValue(4)+
                "\">Данный экземпляр программы<br>" +
                "зарегистрирован на:</font></html>");
        JTextField user = new JTextField(System.getProperty("user.name"));
        user.setEditable(false);
        
        JLabel lblLicense = new JLabel("Программа используется по лицензии:");
        areaLicense = new JTextArea(20,41);
        areaLicense.setLineWrap(true);
        areaLicense.setWrapStyleWord(true);
        areaLicense.setEditable(false);
        //areaLicense.setFont(settings.getIn().getFont(areaLicense.getFont()));
        scr = new JScrollPane(areaLicense);

        
        JButton btnOk = new JButton("Закрыть");
        btnOk.addActionListener(this);
        btnOk.setActionCommand(CLOSE);
        btnOk.setDefaultCapable(true);
        getRootPane().setDefaultButton(btnOk);
        
        GroupLayout layout = new GroupLayout(this.getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(title)
                .addComponent(user)
                .addComponent(lblLicense)
                .addComponent(scr)
                .addComponent(btnOk, GroupLayout.Alignment.CENTER)
                );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(title)
                .addComponent(user)
                .addComponent(lblLicense)
                .addComponent(scr)
                .addComponent(btnOk)
                );
        
        
        this.pack();
        this.setResizable(false);
        areaLicense.append(readTextFromJar("texts/license.txt"));
        areaLicense.setCaretPosition(0);
        this.setLocationRelativeTo(owner);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        this.setVisible(false);
    }
    public static String readTextFromJar(String s) {
    InputStream is = null;
    BufferedReader br = null;
    String line = "";
    String list = "";

    try { 
      is = LicenseBox.class.getResourceAsStream(s);
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
    return list;
  }
    
}
