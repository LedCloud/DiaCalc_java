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
import products.*;
import java.util.*;
import manager.*;
import java.awt.*;


//Необходимо добавить !isCanceled()
public class ImportBaseTask  extends SwingWorker<Void, Void>{
    private int mode = 0;
    public final static int MODE_UPDATE = 0;
    public final static int MODE_ADD = 1;
    public final static int MODE_REPLACE = 2;

    public final static int LOCAL = 3;
    public final static int SERVER = 4;

    private Vector<ProdGroup> gr;
    private Vector<ProductInBase> pr;
    private Vector<ComplexProduct> cm;
    private GroupManager grMgr = new GroupManager();
    private ProductManager prMgr = new ProductManager();
    private ComplexManager cmMgr = new ComplexManager();

    private int todo;
    private int counter;
    private int majority;

    public ImportBaseTask(  Vector<ProdGroup> gr,
                            Vector<ProductInBase> pr,
                            Vector<ComplexProduct> cm,
                            int mode,
                            int majority){
        this.gr = gr;
        this.pr = pr;
        this.cm = cm==null?new Vector<ComplexProduct>():cm;
        this.mode = mode;
        this.majority = majority;
        
    }
    private void mode2(){//То же несложный метод, сначал все удаляем, потом вызываем mode1
        Vector<ProdGroup> grps = new Vector(grMgr.getGroups(GroupManager.ONLY_EXISTS_GROUPS));
        for(ProdGroup g:grps){
            if (!isCancelled()) grMgr.deleteGroup(g, GroupManager.ONLY_EXISTS_GROUPS);
            else break;
        }
        if (!isCancelled()) mode1();
    }
    private void mode1(){//самый простой, добавляем продукты в конец списка
        setProgress(0);
        todo = gr.size() + pr.size() + cm.size();
        counter = 0;
        while (gr.size()>0 && !isCancelled()){
            ProdGroup g = gr.firstElement();
            addGroup(g);
            
            gr.remove(g);
            counter++;
            setProgress( (int)(100f*(float)counter/todo)  );
        }
        setProgress(100);
    }
    private void mode0(){//тут нужно делать сравнение
        setProgress(0);
        todo = gr.size() + pr.size() + cm.size();
        counter = 0;

        while (gr.size()>0 && !isCancelled()){
        
            Vector<ProdGroup> grEx = new Vector(grMgr.getGroups(GroupManager.ONLY_EXISTS_GROUPS));
            ProdGroup g = gr.firstElement();
            ProdGroup grfnd = null;
            for(ProdGroup grExEl:grEx){//сравниваем названия групп
                if (isCancelled()) break;
                if (grExEl.getName().equals(g.getName())){
                    grfnd = grExEl;
                    break;
                }
            }
            if (grfnd!=null && !isCancelled()){//Группа найдена
                //Группы одинаковые, начинаем сверять продукты
                Vector<ProductInBase> prEx = new Vector(prMgr.getProductsFromGroup(grfnd.getId()));
                Vector<ProductInBase> pr2add = new Vector();
                for(int p=0;(p<pr.size() && !isCancelled());p++){
                   if (pr.get(p).getOwner()==g.getId()){//То есть продукт из этой группы
                        boolean found = false;
                        for(ProductInBase prBs:prEx){
                            if (isCancelled()) break;
                            if (pr.get(p).isComplex()){
                                 //тут сравниваем сложные продукты
                                 if (prBs.isComplex() &&
                                       pr.get(p).getName().equals(prBs.getName())){
                                     //продукты оба сложные, названия совпадают
                                     found = true;

                                     //отбираем состав сложного из базы с сервера
                                     Vector<ComplexProduct> cmpl = new Vector();
                                     for(int c=0;c<cm.size();c++){
                                          if (cm.get(c).getOwner()==pr.get(p).getId()){
                                              cmpl.add(cm.get(c));
                                              cm.remove(c);c--;
                                              counter++;
                                              setProgress( (int)(100f*(float)counter/todo)  );
                                          }
                                     }
                                     if (isCancelled()) break;
                                     if (majority==SERVER){
                                         cmMgr.flushProducts(prBs.getId());
                                         //заменяем без всякой проверки
                                         cmMgr.addComplexProducts(cmpl, prBs.getId());
                                     }
                                     if (!prBs.equals(pr.get(p)) && !isCancelled()){
                                            //Продукты не одинаковые по характеристикам
                                            //заменяем старый продукт
                                             if (majority==SERVER){
                                                 //Заменяем местный продукт
                                                pr.get(p).setId(prBs.getId());
                                                pr.get(p).setOwner(prBs.getOwner());
                                                prMgr.updateProductInBase(pr.get(p));
                                             }
                                            pr.remove(pr.get(p));p--;//Продукт обработан, убираем его
                                            counter++;
                                            setProgress( (int)(100f*(float)counter/todo)  );
                                            break;
                                     }
                                     else{//Продукты одинаковые, делать ничего не надо
                                         found = true;
                                         pr.remove(pr.get(p));p--;//Продукт обработан, убираем его
                                         counter++;
                                         setProgress( (int)(100f*(float)counter/todo)  );
                                         break;
                                     }
                                 }
                            }
                            else{
                                //Тут простые
                                if (pr.get(p).getName().equals(prBs.getName()) &&
                                       !prBs.isComplex()){
                                    if (pr.get(p).equals(prBs)){
                                        //нашли одинаковые продукты, делать ничего не надо
                                        found = true;
                                        pr.remove(pr.get(p));p--;//Продукт обработан, убираем его
                                        counter++;
                                        setProgress( (int)(100f*(float)counter/todo)  );
                                        break;//заканчиваем цикл
                                    }else{
                                     //Название одинаковое, но продукты разные - заменяем
                                     if (isCancelled()) break;
                                     found = true;
                                     if (majority==SERVER){
                                         pr.get(p).setId(prBs.getId());
                                         pr.get(p).setOwner(prBs.getOwner());
                                         prMgr.updateProductInBase(pr.get(p));
                                     }
                                     pr.remove(pr.get(p));p--;//Продукт обработан, убираем его
                                     counter++;
                                     setProgress( (int)(100f*(float)counter/todo)  );
                                     break;//заканчиваем цикл
                                    }
                                 }
                            }
                                
                         }
                         if (!found){
                             //добавляем
                             if (!isCancelled())
                             if (pr.get(p).isComplex()){
                                //Добавляем сложный
                                pr.get(p).setOwner(grfnd.getId());
                                prMgr.addProdInBase(pr.get(p));
                                int pid = prMgr.getLastInsertedId();
                                Vector<ComplexProduct> cm2add = new Vector();
                                for(int c=0;(c<cm.size() && !isCancelled());c++){
                                   if (cm.get(c).getOwner()==pr.get(p).getId()){
                                       cm2add.add(cm.get(c));
                                       cm.remove(c);c--;
                                       counter++;
                                       setProgress( (int)(100f*(float)counter/todo)  );
                                    }
                                }
                                if (isCancelled()) break;
                                cmMgr.addComplexProducts(cm2add, pid);

                                pr.remove(pr.get(p));p--;//Продукт обработан, убираем его
                                counter++;
                                setProgress( (int)(100f*(float)counter/todo)  );
                            }
                            else{//добавляем простой
                                pr2add.add(pr.get(p));
                                pr.remove(pr.get(p));p--;//Продукт обработан, убираем его
                                counter++;
                                setProgress( (int)(100f*(float)counter/todo)  );
                            }
                         }
                     }
                    }
                    if (pr2add.size()>0 && !isCancelled()){//добавляем скпом простые продукты
                        prMgr.addCollectionProducts(pr2add, grfnd.getId());
                    }
            }
            else{//Группа не найдена
                addGroup(g);
            }
            
            gr.remove(g);
            counter++;
            setProgress( (int)(100f*(float)counter/todo)  );
        }
        setProgress(100);
        
    }
    private void addGroup(ProdGroup g){
        Vector<ProdGroup> grEx = new Vector(grMgr.getGroups(GroupManager.ONLY_EXISTS_GROUPS));
        int lastSortInd;
        if (grEx.size()>0) lastSortInd = grEx.lastElement().getSortInd();
        else               lastSortInd = 0;
        g.setSortInd(lastSortInd+1);
        int gid = new Vector<ProdGroup>(grMgr.addGroup(g, GroupManager.ONLY_EXISTS_GROUPS)).lastElement().getId();

        Vector<ProductInBase> pr2add = new Vector();
        for (int p=0;(p<pr.size() && !isCancelled());p++){
                if (g.getId()==pr.get(p).getOwner()){
                    ProductInBase prod = pr.get(p);
                    prod.setOwner(gid);

                    if (pr.get(p).isComplex()){
                        //обрабатываем сложный
                        prMgr.addProdInBase(prod);
                        int pid = prMgr.getLastInsertedId();
                        Vector<ComplexProduct> cm2add = new Vector();
                        for(int c=0;(c<cm.size() && !isCancelled());c++){
                            if (prod.getId()==cm.get(c).getOwner() && !isCancelled()){
                                cm2add.add(cm.get(c));
                                cm.remove(cm.get(c)); c--;
                                counter++;
                                setProgress( (int)(100f*(float)counter/todo)  );
                            }
                        }
                        if (cm2add.size()>0 && !isCancelled()){
                            cmMgr.addComplexProducts(cm2add, pid);
                        }
                    }
                    else{
                        pr2add.add(prod);
                    }

                    pr.remove(prod); p--;
                    counter++;
                    setProgress( (int)(100f*(float)counter/todo)  );
                }
        }
        if (pr2add.size()>0 && !isCancelled()) prMgr.addCollectionProducts(pr2add, gid);
    }

    
    @Override
    public Void doInBackground() {
        switch (mode){
            case 0: mode0(); break;
            case 1: mode1(); break;
            case 2: mode2(); break;
        }
        return null;
    }
    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
    }
}
