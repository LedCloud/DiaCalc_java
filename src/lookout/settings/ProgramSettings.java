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

package lookout.settings;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import javax.swing.JOptionPane;

public class ProgramSettings{
    private static String SET_FILE_NAME = System.getProperty("user.dir")+System.getProperty("file.separator")+"settings.xml";
    private static String SET_FILE_NAME_DAT = System.getProperty("user.dir")+System.getProperty("file.separator")+"settings.dat";
    private static ProgramSettings instance = null;

    private static SettingsData data;

    
    private ProgramSettings(){
        if (new File(SET_FILE_NAME_DAT).exists()){
                try {
                    FileInputStream fin = new FileInputStream(SET_FILE_NAME_DAT);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    data
                        = (SettingsData) ois.readObject();

                    ois.close();
                }
                catch (Exception e) {
                    data = new SettingsData();
                    JOptionPane.showMessageDialog(null,
                            "Не найден подходящий файл settings.dat\n" +
                            "Будут использованы настройки по умолчанию\n" +
                            "Будет выбран первый по списку пользователь\n" +
                            "Это не ошибка и никакой потери данных не произойдет!",
                            "Обновление",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                //Все прошло нормально, удаляем settings.dat
                new File(SET_FILE_NAME_DAT).delete();
            }
            else{
                if (new File(SET_FILE_NAME).exists()){
                    try{
                        XMLDecoder d = new XMLDecoder(
                          new BufferedInputStream(
                              new FileInputStream(SET_FILE_NAME)));
                        data  = (SettingsData)d.readObject();
                        d.close();
                    }catch(Exception exc){
                        data = new SettingsData();
                    }
                }else{
                    data = new SettingsData();
                }
            }
    }
    
    public SettingsData getIn(){
        return data;
    }

    public static synchronized ProgramSettings getInstance(){
        if (instance == null) {
            instance = new ProgramSettings();
        }
        return instance;
    }

    
    
    
    public void store(){
       // serialize the Queue
       try{
            XMLEncoder e = new XMLEncoder(
                          new BufferedOutputStream(
                              new FileOutputStream(SET_FILE_NAME)));
            e.writeObject(data);
            e.close();
        }catch (FileNotFoundException exc){
            exc.printStackTrace();
        }
    
    }
}

