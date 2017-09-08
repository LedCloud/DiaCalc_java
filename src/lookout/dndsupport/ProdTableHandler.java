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
import java.awt.datatransfer.*;
import products.ProductInBase;
import tablemodels.ProductTableModel;
import manager.ProductManager;
import products.ProductW;


public class ProdTableHandler extends TransferHandler{
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public Transferable createTransferable(JComponent c){
    if (c instanceof JTable){
        JTable tb = (JTable)c;
        if (tb.getRowCount()>0 && !tb.getSelectionModel().isSelectionEmpty()){
            int row = tb.getSelectedRow();
            ProductInBase prod = ((ProductTableModel)tb.getModel()).getProduct(
                    tb.convertRowIndexToModel(
                    row));
            prod.setUsage(prod.getUsage()+1);
            ProductManager mgr = new ProductManager();
            mgr.updateProductInBase(prod);
            return new ProductTransferable( prod );
        }
    }

        return null;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        return info.isDataFlavorSupported(ArcProductTransferable.dataFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }

        if (!canImport(info)) return false;


        if (info.isDataFlavorSupported(ArcProductTransferable.dataFlavor)){
        if (info.getComponent() instanceof JTable){
            JTable tb = (JTable)info.getComponent();
            int grId = ((ProductTableModel)tb.getModel()).getGrId();
            if (grId<=0) return false;
            ProductW pr=null;
            try{
                pr = (ProductW)info.getTransferable().
                        getTransferData(ArcProductTransferable.dataFlavor);

            }catch (Exception e) { return false; }
            ProductInBase prod = new ProductInBase(
                                pr.getName(),
                                pr.getProt(),
                                pr.getFat(),
                                pr.getCarb(),
                                pr.getGi(),
                                pr.getWeight(),
                                0,
                                false,
                                grId,
                                0);
            
            //Тут нужно установить владельца
            ((ProductTableModel)tb.getModel()).addProduct(prod);

            return true;
        }}

        return false;
    }
}
