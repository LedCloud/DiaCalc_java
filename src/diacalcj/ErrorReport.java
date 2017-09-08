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
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;


public class ErrorReport extends JFrame{
    private final static String EMAIL_ADDR = "errorlogs@diacalc.org";
    private Desktop desktop = null;
    private JTextArea text;
    private boolean flag = true;

    public ErrorReport(final String ver){
        if (Desktop.isDesktopSupported()){
            desktop = Desktop.getDesktop();
        }
        setTitle("Ошибка в работе программы "+ver);
        text = new JTextArea();
        text.setEditable(false);
        JLabel lbl = new JLabel(
                "<html><center><h3><font color=red>В работе программы произошла ошибка,<br>" +
                "Вы можете отправить сообщение о ошибке автору по электронной почте<br>" +
                "или продолжить работу</font></h3></center><br>" +
                "Ниже находится описание ошибки, которое может помочь разобраться<br>" +
                "с проблемой, для разработчика эта информация очень важна</html>");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        add(lbl,
                BorderLayout.NORTH);

        JButton btnEmail = new JButton("Отправить отчет");
        btnEmail.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ev){
                boolean noemail = false;
                if (Desktop.isDesktopSupported()){
                    if (desktop.isSupported(Desktop.Action.MAIL)){
                        URI uri = null;
                        text.selectAll();
                        text.copy();
                        try {
                            uri = new URI("mailto",
                     EMAIL_ADDR +"?SUBJECT=Error in "+ver+
                     "&BODY=Опишите причину появления ошибки, а так же\n" +
                     "Нажмите Ctrl+V для вставки в письмо лога ошибок\n\n",
                            null);
                            desktop.mail(uri);
                        }catch(Exception ex){
                            //ex.printStackTrace();
                            noemail = true;
                        }
                    }
                }else noemail = true;
                if (noemail){
                    JOptionPane.showMessageDialog(ErrorReport.this,
                            "Ваша система не поддерживает вызов email клиента по умолчанию\n" +
                            "Запустите его сами и вставьте вывод ошибок в это письмо\n" +
                            "с помощью клавиш Ctrl+V\n"+
                            "адрес для отправки ошибок: "+EMAIL_ADDR,
                            "Неудача отправки письма",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(new JScrollPane(text),BorderLayout.CENTER);

        JButton btnClose = new JButton("Закрыть и продолжить работу");
        btnClose.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ev){
                setVisible(false);
                if (!Stack.isAlive()){
                    flag = false;//завершаем тред
                    dispose();
                    System.exit(0);
                }
            }
        });
        JPanel bottom = new JPanel(new GridLayout(1,3,10,5));
        bottom.add(btnEmail);
        bottom.add(btnClose);

        add(bottom,BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600,400);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        runThread();
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent ev){
                setVisible(false);
                //Тут проверяем нужно ли удалить ресурсы
                //занятые окном
                if (!Stack.isAlive()){
                    flag = false;//завершаем тред
                    dispose();
                    System.exit(0);
                }
            }
        });
    }//что бы закрыть запускаем тред, который смотрит за стеком, как только
    //стек исчезает - убиваем данное окно
    public void runThread(){
        Thread t = new Thread(){
            @Override
            public void run(){
                while (flag){
                    try{
                        Thread.sleep(100);
                    }catch(Exception ex){}
                    if (Stack.getInstance().getStackTrace().size()>0){
                        setVisible(true);
                        while (Stack.getInstance().getStackTrace().size()>0){
                            synchronized (Stack.getInstance()) {
                                text.append(Stack.getInstance()
                                        .getStackTrace().remove(0));
                            }
                        }
                    }
                }
            }
        };
        t.start();
    }
    public void stopThread(){
        flag = false;
    }
}