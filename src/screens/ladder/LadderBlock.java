package screens.ladder;

import ilcompiler.memoryvariable.MemoryVariable;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.*;

public abstract class LadderBlock extends JPanel {
    protected String variableName;
    protected String blockType; 
    public static LadderBlock draggedBlock = null; 
    protected boolean isActive = false;
    
    public LadderBlock(String varName, String type) {
        this.variableName = varName != null ? varName.toUpperCase() : "";
        this.blockType = type;
        this.setOpaque(false);
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        
        this.setPreferredSize(new Dimension(80, 120));
        this.setMinimumSize(new Dimension(80, 120));
        this.setMaximumSize(new Dimension(80, 120)); 
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        this.setTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) { return COPY_OR_MOVE; }
            
            @Override
            protected Transferable createTransferable(JComponent c) {
                draggedBlock = LadderBlock.this; 
                return new StringSelection("MOVE");
            }
            
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    Container parent = getParent();
                    if (parent instanceof LadderRung) {
                        LadderRung rung = (LadderRung) parent;
                        int index = rung.getIndexOfBlock(LadderBlock.this);
                        return rung.processDropData(data, index);
                    }
                } catch (Exception e) {}
                return false;
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                JComponent jc = (JComponent) e.getSource();
                TransferHandler th = jc.getTransferHandler();
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                jc.paint(img.getGraphics());
                th.setDragImage(img);
                th.exportAsDrag(jc, e, TransferHandler.MOVE);
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (LadderCanvas.isSimulating) {
                    LadderCanvas.setSimulatingGlobal(false);
                    return;
                }
                if (SwingUtilities.isRightMouseButton(e)) mostrarMenu(e);
                else if (e.getClickCount() >= 2) renomear();
            }
        });
    }

    public void updateState(Map<String, Boolean> inputs, Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        if (variableName == null || variableName.isEmpty() || variableName.equals("<???>")) {
            this.isActive = false;
            return;
        }
        boolean val = false;
        if (inputs != null && inputs.containsKey(variableName)) {
            val = Boolean.TRUE.equals(inputs.get(variableName));
        } else if (outputs != null && outputs.containsKey(variableName)) {
            val = Boolean.TRUE.equals(outputs.get(variableName));
        } else if (memoryVariables != null && memoryVariables.containsKey(variableName)) {
            MemoryVariable mv = memoryVariables.get(variableName);
            if (mv != null) {
                boolean isTimerOrCounter = variableName.startsWith("T") || variableName.startsWith("C");
                val = isTimerOrCounter ? mv.endTimer : mv.currentValue;
            }
        }
        this.isActive = val;
    }

    public boolean isActive() { return isActive; }

    public void setActive(boolean active) {
        this.isActive = active;
        this.repaint();
    }

    public void renomear() {
        Window window = SwingUtilities.getWindowAncestor(this);
        String nomeAtual = variableName.equals("<???>") ? "" : variableName;
        String newName = (String) JOptionPane.showInputDialog(window, "Digite o nome da Tag:", "Renomear", JOptionPane.PLAIN_MESSAGE, null, null, nomeAtual);
        if(newName != null && !newName.trim().isEmpty()) {
            this.variableName = newName.toUpperCase();
            this.repaint();
        }
    }

    private void mostrarMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem itemRenomear = new JMenuItem("Renomear Tag");
        JMenuItem itemExcluir = new JMenuItem("Excluir Bloco");
        
        itemRenomear.addActionListener(a -> renomear());
        itemExcluir.addActionListener(a -> {
            Container parent = getParent();
            if (parent instanceof LadderRung) ((LadderRung) parent).removeBlock(LadderBlock.this);
        });
        
        menu.add(itemRenomear);
        menu.addSeparator(); 
        menu.add(itemExcluir);
        menu.show(this, e.getX(), e.getY());
    }

    public String getVariableName() { return variableName; }
    public abstract boolean isOutput();
    public abstract String compileToIL(boolean isFirstElement);
}
