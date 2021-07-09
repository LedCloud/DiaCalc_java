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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URL;
import javax.swing.ImageIcon;
//import javax.swing.UIManager;
import lookout.MainFrame;

public class SettingsData implements Serializable{
    private int size;
    private int useUsageGroup;
    private int prods_count;
    private int precision;
    private Rectangle main;
    private int menuSize;
    private int menuName;
    private int menuWeight;
    private int menuRest;
    private int groupSize;
    private int prodName;
    private int prodRest;
    private int user;
    private boolean snack;
    private int menuMask;
    private int snackSize;
    private boolean calcOUVbyK1;
    private int roundLimit;
    private boolean carbRatio;
    private Rectangle diary;
    private int diary_split;
    private int diary_date_size;
    private int diary_comm_size;
    private int diary_rest_size;
    private boolean lockCoefs;
    private int diary_menu_split;
    private boolean product_once;
    private boolean vacuum_base;
    private float sizeFactor = 1.0f;
    
    public SettingsData(){
        //System.out.println("font sz = "+font_sz);
        //Рабочий вариант
        sizeFactor = (float)Toolkit.getDefaultToolkit().getScreenResolution()/96.0f;
        //System.out.println(Toolkit.getDefaultToolkit().getScreenResolution() );
        //Тестовый вариант
        //sizeFactor = 96f/96f;
        size = 4;//Два размера 4 большой, 3 - маленький
        useUsageGroup = 1;//1 - Используем 0 - неиспользуем
        precision = 1;//Точность вывода
        prods_count = 20;//количество продуктов в частоиспользуемых
        main = new Rectangle(0,0,0,0);//размер главного окна
        menuSize = 350;//ширина зоны меню
        menuName = 197;//ширина столбцов
        menuWeight = 50;//ширина столбцов
        menuRest = 50;//ширина столбцов
        groupSize = 205;//ширина столбцов
        prodName = 200;//ширина столбцов
        prodRest = 50;//ширина столбцов
        user = 0;//пользователь
        snack = false;//используем ли перекус
        menuMask = 3;//маска столбцов меню
        snackSize = 100;
        calcOUVbyK1 = false;
        roundLimit = 0;
        carbRatio = false;
        diary = new Rectangle(0,0,0,0);
        diary_split = 470;
        diary_date_size = 150;
        diary_comm_size = 200;
        diary_rest_size = 40;
        lockCoefs = true;
        diary_menu_split = 110;
        product_once = true;
        vacuum_base = false;
    }
    
    public ImageIcon getImage4Button(String imageName){
        String imgLocation =  "buttons/" + imageName + ".png";
        URL imageURL = MainFrame.class.getResource(imgLocation);
        ImageIcon image = null;
        if (imageURL != null) {                      //image found
            image = new ImageIcon(imageURL);
        } else {                                     //no image found
            System.err.println("Resource not found: "
                               + imgLocation);
        }
        // sizeFactor = 1 96 dpi 28*28, =3.3(3)   dpi 320 92*92
        float width = 28f*(float)sizeFactor;
        //Теперь корректируем 4-28 3-20
        width = size==3?width/1.4f:width;
        int w = (int)width;
        
        BufferedImage resizedImg = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,//.KEY_ANTIALIASING,//.KEY_INTERPOLATION, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);//.VALUE_ANTIALIAS_DEFAULT);//.VALUE_INTERPOLATION_BILINEAR);
        if (image!=null) g2.drawImage(image.getImage(), 0, 0, w, w, null);
        g2.dispose();

        return new ImageIcon(resizedImg);
    }
    
    public String getSizedPath(boolean sized){
        String p;// = "";
        if (sizeFactor<1.4f){
            p = "96/";
        }else if (sizeFactor<1.97f){
            p = "158/";
        }else if (sizeFactor<2.8f){
            p = "220/";
        }else{
            p = "320/";
        }
        
        if (size==3 && sized){
            p += "sm/";
        }
        
        return p;
    }
    
    public float getSizeFactor(){
        return sizeFactor;
    }
    
    public int getSizedValue(int in){
        return (int)((float)in*sizeFactor);
    }
    
    private Rectangle checkBounds(Rectangle r){
        if (r.x<0 || r.x>Toolkit.getDefaultToolkit().getScreenSize().getWidth()) r.x=0;
        if (r.y<0 || r.y>Toolkit.getDefaultToolkit().getScreenSize().getHeight()) r.y=0;
        
        return r;
    }
    
    public boolean isVacuum(){
        return vacuum_base;
    }
    
    public void setVacuum(boolean v){
        vacuum_base = v;
    }
    
    public boolean isProductOnce(){
        return product_once;
    }
    
    public void setProductOnce(boolean v){
        product_once = v;
    }
    
    public int getDiaryMenuSplit(){
        return diary_menu_split;
    }
    
    public void setDiaryMenuSplit(int v){
        diary_menu_split = v;
    }
    
    public boolean isCoefsLocked(){
        return lockCoefs;
    }
    
    public void setCoefsLocked(boolean v){
        lockCoefs = v;
    }
    
    public Rectangle getDiaryBounds(){
        return checkBounds(diary);
    }
    
    public void setDiaryBounds(Rectangle v){
        diary = v;
    }
    
    public int getDiarySplit(){
        return diary_split;
    }
    
    public void setDiarySplit(int v){
        diary_split = v;
    }
    
    public int getDiaryDateSize(){
        return diary_date_size;
    }
    
    public void setDiaryDateSize(int v){
        diary_date_size = v;
    }
    
    public int getDiaryCommSize(){
        return diary_comm_size;
    }
    
    public void setDiaryCommSize(int v){
        diary_comm_size = v;
    }
    
    public int getDiaryRestSize(){
        return diary_rest_size;
    }
    
    public void setDiaryRestSize(int v){
        diary_rest_size = v;
    }
    
    public boolean isCarbRatio(){
        return carbRatio;
    }
    
    public void setCarbRatio(boolean v){
        carbRatio = v;
    }

    public int getRoundLimit(){
        return roundLimit;
    }
    
    public void setRoundLimit(int v){
        roundLimit = v;
    }
    
    public boolean isCalcOUVbyK1(){
        return calcOUVbyK1;
    }
    
    public void setCalcOUVbyK1(boolean v){
            calcOUVbyK1 = v;
    }
    
    public int getSnackSize(){
        return snackSize;
    }
    
    public void setSnackSize(int v){
        snackSize = v;
    }
    
    public int getMenuMask(){
        return menuMask;
    }
    
    public void setMenuMask(int v){
        menuMask = v;
    }
    
    public boolean isUseSnack(){
        return snack;
    }
    
    public void setUseSnack(boolean use){
        snack = use;
    }
    
    public int getUser(){
        return user;
    }
    
    public void setUser(int v){
        user = v;
    }
    
    public int getProdRest(){
        return prodRest;
    }
    
    public void setProdRest(int v){
        prodRest = v;
    }
    
    public int getProdName(){
        return prodName;
    }
    
    public void setProdName(int v){
        prodName = v;
    }
    
    public int getGroupSize(){
        return groupSize;
    }
    
    public void setGroupSize(int v){
        groupSize = v;
    }
    
    public int getMenuRest(){
        return menuRest;
    }
    
    public void setMenuRest(int v){
        menuRest = v;
    }
    
    public int getMenuNameWidth(){
        return menuName;
    }
    
    public void setMenuNameWidth(int w){
        menuName = w;
    }
    
    public int getMenuWeightWidth(){
        return menuWeight;
    }
    
    public void setMenuWeightWidth(int w){
        menuWeight = w;
    }
    
    public int getMenuSize(){
        return menuSize;
    }
    
    public void setMenuSize(int size){
        menuSize = size;
    }
    
    /**
     * 
     * @return Возврашает два типа размера интерфейса программы
     * 4 - большой, 3 - маленький
     */
    public int getSize(){
        return size;
    }
    
    public void setSize(int size){
        this.size = size;
    }
    
    public void setUseUsageGroup(int v){
        useUsageGroup = v;
    }
    
    public int getUseUsageGroup(){
        return useUsageGroup;
    }
    
    public int getUsageGroupCount(){
        return prods_count;
    }
    
    public void setUsageGroupCount(int v){
        prods_count = v;
    }
    
    public int getPrecision(){
        return precision;
    }
    
    public void setPrecision(int prec){
        precision = prec;
    }
    
    public Rectangle getMainBounds(){
        return checkBounds(main);
    }
    
    public void setMainBounds(Rectangle rec){
        main = rec;
    }
}
