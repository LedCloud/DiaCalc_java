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

package lookout.dndsupport;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */
import javax.swing.*;
import products.ProductInBase;
import tablemodels.GroupTableModel;
import products.ProdGroup;
import manager.ProductManager;
import lookout.settings.ProgramSettings;
import products.ProductW;
import java.sql.SQLException;

public class GroupMoveHandler  extends TransferHandler{
    ProgramSettings settings = ProgramSettings.getInstance();

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        return info.isDataFlavorSupported(ProductTransferable.dataFlavor)||
                info.isDataFlavorSupported(ArcProductTransferable.dataFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }

        if (!canImport(info)) return false;

        if (info.getComponent() instanceof JTable){
            JTable tb = (JTable)info.getComponent();
            JTable.DropLocation dl = (JTable.DropLocation)info.getDropLocation();

            if (dl.getRow()<0) return false;
            if (dl.getColumn()<0) return false;
            if (info.isDataFlavorSupported(ProductTransferable.dataFlavor)){
                ProductInBase prod=null;
                try{
                    prod = (ProductInBase)info.getTransferable().
                        getTransferData(ProductTransferable.dataFlavor);
                }catch (Exception e) { return false; }

                ProdGroup gr = ((GroupTableModel)tb.getModel()).getGroup(dl.getRow());
                if (gr.getId()==prod.getOwner()) return false;
                if (gr.getId()==0) return false;
                Object[] options = {"Да","Нет"};
                int n = JOptionPane.showOptionDialog(null,
                                    "Вы хотите перенести продукт:\n" +
                                    prod.getName()+
                                    "\n\nВ группу:\n"+
                                    gr.getName(),
                                    "Перенос продукта",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    options,
                                    options[1]);
                if (n == JOptionPane.YES_OPTION) {
                    prod.setOwner(gr.getId());
                    //int prId = prod.getId();
                    ProductManager mgr = new ProductManager();
                    mgr.updateProductInBase(prod);

                    tb.getSelectionModel().clearSelection();
                    tb.getSelectionModel().setSelectionInterval(dl.getRow(), dl.getRow());
                    return true;
                }
            }else
            if (info.isDataFlavorSupported(ArcProductTransferable.dataFlavor)){
                ProductW pr=null;
                try{
                    pr = (ProductW)info.getTransferable().
                        getTransferData(ArcProductTransferable.dataFlavor);
                }catch (Exception e) { return false; }
                ProdGroup gr = ((GroupTableModel)tb.getModel()).getGroup(dl.getRow());
                ProductInBase prod = new ProductInBase(
                                pr.getName(),
                                pr.getProt(),
                                pr.getFat(),
                                pr.getCarb(),
                                pr.getGi(),
                                pr.getWeight(),
                                0,
                                false,
                                gr.getId(),
                                0
                        );
                ProductManager mgr = new ProductManager();
                mgr.addProdInBase(prod);
                tb.getSelectionModel().clearSelection();
                tb.getSelectionModel().setSelectionInterval(dl.getRow(), dl.getRow());
                return true;
            }
            
        }
        return false;
    }
}
