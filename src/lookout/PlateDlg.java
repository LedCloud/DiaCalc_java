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
import lookout.cellroutins.StringEditor;
import lookout.cellroutins.FloatRenderer;
import lookout.cellroutins.FloatEditor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import lookout.pictchooser.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.*;
import tablemodels.PlateTableModel;
import maths.Plate;
import lookout.settings.ProgramSettings;
import manager.PlatesManager;
import javax.swing.event.*;
import java.awt.geom.AffineTransform;
import lookout.dndsupport.WeightTransferHandler;
import java.net.URL;
import javax.swing.text.*;
import java.text.*;

public class PlateDlg extends JDialog implements ActionListener{
    private JLabel picture;
    private static final String OPEN_PICTURE = "open picture";
    private static final String ADD_ROW = "add row";
    private static final String DELETE_ROW = "delete row";
    private static final String REMOVE_PICTURE = "remove picture";
    private final static String PLAY_AS_ENTER = "play as enter";
    private static final int MAX = ProgramSettings.getInstance().getIn().getSizedValue(150);
    
    private JTable plateList;
    private JFileChooser fc=null;
    private ProgramSettings settings = ProgramSettings.getInstance();
    private PlatesManager mgr = new PlatesManager();

    public  PlateDlg(){
        setIconImage(new ImageIcon(PlateDlg.class
                .getResource("images/PlateIcon.png")).getImage());
        setTitle("Посуда");

        Box bx = new Box(BoxLayout.X_AXIS);
        plateList = new JTable(new PlateTableModel());
        plateList.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        plateList.setFillsViewportHeight(true);
        plateList.setDefaultRenderer(float.class, new FloatRenderer("0.0"));
        //////////
        DecimalFormat df0 = new DecimalFormat("0.0");
        NumberFormatter formatter0 = new NumberFormatter(df0);
        JFormattedTextField field0 = new JFormattedTextField(formatter0);
        FloatEditor cellEditor0 = new FloatEditor(field0, df0);
        /////////
        plateList.setDefaultEditor(float.class, cellEditor0);

        plateList.setDefaultEditor(String.class, new StringEditor(new JTextField()));
        plateList.getColumnModel().getColumn(0)
                .setPreferredWidth(settings.getIn().getSizedValue(210));
        plateList.getColumnModel().getColumn(1)
                .setPreferredWidth(settings.getIn().getSizedValue(60));
        plateList.setAutoCreateRowSorter(true);//дополняем сортировку
        plateList.getTableHeader().setReorderingAllowed(false);//запрещаем двигать столбцы
        plateList.setRowHeight(settings.getIn().getSizedValue(plateList.getRowHeight()));
        plateList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        plateList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    loadPict();
                }
            }
        });
        //Actions//
        AbstractAction playASenter = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ev){
                JTable tb = (JTable)ev.getSource();
                int rec_id = ((PlateTableModel)tb.getModel())
                        .getPlate(
                        tb.convertRowIndexToModel(tb.getSelectedRow())
                        ).getId();
                int col = tb.getSelectedColumn();

                if (tb.isEditing()){
                    tb.getCellEditor().stopCellEditing();
                }
                int row_now = ((PlateTableModel)tb.getModel()).findRow(rec_id);
                if (col<(tb.getColumnCount()-1)){
                    tb.setRowSelectionInterval(row_now, row_now);
                    tb.setColumnSelectionInterval(col+1, col+1);
                }
                else if (row_now<(tb.getRowCount()-1)){
                    tb.setRowSelectionInterval(row_now+1, row_now+1);
                    tb.setColumnSelectionInterval(0, 0);
                }
            }
        };
        plateList.getInputMap(JInternalFrame.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
        plateList.getInputMap(JInternalFrame.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PLAY_AS_ENTER);
        plateList.getActionMap().put(PLAY_AS_ENTER, playASenter);
        ////////
        

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JScrollPane(plateList),BorderLayout.CENTER);
        
        JToolBar bar = new JToolBar();
        
        bar.add(makeButton("New","Add",ADD_ROW,
                "Добавить строку"));
        //menuBar.add(Box.createHorizontalGlue());

        bar.add(Box.createHorizontalGlue());
        bar.add(makeButton("Delete","Remove",DELETE_ROW,
                "Удалить строку"));
        bar.setFloatable(false);
        left.add(bar,BorderLayout.SOUTH);


        //Set up the picture.
        picture = new JLabel();
        picture.setHorizontalAlignment(SwingConstants.CENTER);
        picture.setVerticalAlignment(SwingConstants.CENTER);

        picture.setPreferredSize(new Dimension(MAX, MAX));
        picture.setMinimumSize(new Dimension(MAX, MAX));
        picture.setMaximumSize(new Dimension(MAX, MAX));
        picture.setBorder(BorderFactory.createLoweredBevelBorder());
        
        picture.setTransferHandler(new WeightTransferHandler());

        // Listen for mouse clicks
        picture.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (SwingUtilities.isLeftMouseButton(evt)){
                    JComponent comp = (JComponent)evt.getSource();
                    TransferHandler th = comp.getTransferHandler();

                    // Start the drag operation
                    th.exportAsDrag(comp, evt, TransferHandler.COPY);
                }
            }
        });



        JPanel right = new JPanel(new BorderLayout());
        right.add(picture,BorderLayout.CENTER);

        bar = new JToolBar();
        bar.add(makeButton("AddPict","Add",OPEN_PICTURE,
                "Добавить фото посуды"));

        bar.add(Box.createHorizontalGlue());
        
        bar.add(makeButton("Delete","Remove",REMOVE_PICTURE,
                "Убрать фото посуды"));
        bar.setFloatable(false);
        right.add(bar,BorderLayout.SOUTH);

        bx.add(left);
        bx.add(right);

        add(bx);

        if (plateList.getRowCount()>0) plateList.getSelectionModel()
                .setSelectionInterval(0, 0);
        
        setResizable(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setModal(false);

        setSize(new Dimension(settings.getIn().getSizedValue(460),settings.getIn().getSizedValue(225)));
    }
    

    

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = PlateDlg.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String cmd = e.getActionCommand();
        if (OPEN_PICTURE.equals(cmd)){
            openPicture();
        }else if (ADD_ROW.equals(cmd)){
            addRow();
        }else if (DELETE_ROW.equals(cmd)){
            deleteRow();
        }
        else if (REMOVE_PICTURE.equals(cmd)){
            removePict();
        }
    }
    private void addRow(){
        boolean canadd = true;
        if (plateList.isEditing()){
            canadd = plateList.getCellEditor().stopCellEditing();
        }
        if (canadd){
            int id = ((PlateTableModel)plateList.getModel()).addPlate(new Plate());
            int row = ((PlateTableModel)plateList.getModel()).findRow(id);
            plateList.scrollRectToVisible(plateList.getCellRect(row, 0, true));
            if (row>=0) plateList.getSelectionModel().setSelectionInterval(row, row);
        
            plateList.requestFocusInWindow();
        }
    }
    
    private void deleteRow(){
        if (!plateList.getSelectionModel().isSelectionEmpty() &&
                !plateList.isEditing()){
            int row = plateList.convertRowIndexToModel(plateList.getSelectedRow());
            Plate plate  = ((PlateTableModel)plateList.getModel()).getPlate(row);
            ((PlateTableModel)plateList.getModel()).deletePlate(plate);
            if (plateList.getRowCount()>0){
                if (row==0) row = 1;
                plateList.getSelectionModel().setSelectionInterval(row-1, row-1);
            }
            else{
                picture.setIcon(new ImageIcon());
            }
        }
    }
    private void openPicture(){
        if (!plateList.getSelectionModel().isSelectionEmpty()){
        if (fc==null){
            fc = new JFileChooser();
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileView(new ImageFileView());
            fc.setAccessory(new ImagePreview(fc));
        }
        
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            BufferedImage img = null;
            try {
                img = ImageIO.read(fc.getSelectedFile());
            } catch (IOException e){
                e.printStackTrace();
            }

            if (img!=null){
                if (img.getHeight()>MAX || img.getWidth()>MAX){
                    double xScale = (double)MAX / img.getWidth();
                    double yScale = (double)MAX / img.getHeight();
                    double scale = xScale<yScale? xScale : yScale;

                    int h = (int)((double)img.getHeight() * scale);
                    int w = (int)((double)img.getWidth() * scale);

                    //System.out.println("h="+h+" w="+w);

                    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = bi.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON );

                    AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
                    g2d.drawRenderedImage(img, at);
                    g2d.dispose();

                    img = bi;
                    
                }
                
                picture.setIcon(new ImageIcon(img));

                int row  = plateList.getSelectedRow();
                int id = ((PlateTableModel)plateList.getModel()).getPlate(row).getId();
                
                mgr.setPict(id, img);
            }
        }
        }
    }
    private void loadPict(){
        int row  = plateList.getSelectedRow();
        if (row>=0 && row<plateList.getRowCount()){
            int id = ((PlateTableModel)plateList.getModel()).getPlate(
                    plateList.convertRowIndexToModel(row)
                    ).getId();
            BufferedImage im = mgr.getPict(id);
            if (im==null) picture.setIcon(new MissingIcon());
            else picture.setIcon(new ImageIcon(im));
            plateList.requestFocusInWindow();
        }

    }
    private void removePict(){
        int row  = plateList.getSelectedRow();
        if (row>=0){
            int id = ((PlateTableModel)plateList.getModel()).getPlate(row).getId();
            mgr.setPict(id, null);
            picture.setIcon(new MissingIcon());
        }
    }

    public Plate getSelectedWeight(){
        if (plateList.getRowCount()>0 && !plateList.getSelectionModel().isSelectionEmpty()){
            return ((PlateTableModel)plateList.getModel()).getPlate(plateList.getSelectedRow());
        }
        
        return null;
    }

    private JButton makeButton(String imageName, String altText,
            String actionCommand, String toolTipText) {
        //Look for the image.
        String imgLocation =  "buttons/" + 
                settings.getIn().getSizedPath(true) + 
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
}