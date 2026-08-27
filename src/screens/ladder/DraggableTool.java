package screens.ladder;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class DraggableTool extends JLabel {
    
    public DraggableTool(String tooltip, String blockType) {
        super();
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(62, 42));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setToolTipText(tooltip);
        this.setBorder(BorderFactory.createLineBorder(new Color(160, 180, 200), 1));

        ImageIcon icon = criarIcone(blockType);
        this.setIcon(icon);

        TransferHandler th = new TransferHandler("text") {
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection(blockType);
            }
            @Override
            public int getSourceActions(JComponent c) { return COPY; }
        };
        th.setDragImage(icon.getImage());
        this.setTransferHandler(th);

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                JComponent jc = (JComponent) e.getSource();
                jc.getTransferHandler().exportAsDrag(jc, e, TransferHandler.COPY);
            }
        });
    }

    private ImageIcon criarIcone(String blockType) {
        BufferedImage img = new BufferedImage(58, 38, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(235, 240, 248)); 
        g2.fillRoundRect(0, 0, 58, 38, 4, 4);
        g2.setColor(new Color(120, 140, 160));
        g2.drawRoundRect(0, 0, 57, 37, 4, 4);
        
        g2.setColor(new Color(20, 40, 80));
        g2.setStroke(new BasicStroke(2));
        
        int centerY = 19;
        int w = 58;
        
        if (blockType.equals("NO") || blockType.equals("NC")) {
            g2.drawLine(2, centerY, 16, centerY);
            g2.drawLine(16, centerY - 10, 16, centerY + 10);
            g2.drawLine(42, centerY - 10, 42, centerY + 10);
            g2.drawLine(42, centerY, w - 2, centerY);
            if (blockType.equals("NC")) g2.drawLine(12, centerY + 11, 46, centerY - 11);
        } else if (blockType.equals("OR") || blockType.equals("ORN")) {
            g2.drawLine(2, centerY, w - 2, centerY); 
            g2.drawLine(14, centerY, 14, centerY + 9);
            g2.drawLine(44, centerY, 44, centerY + 9);
            g2.drawLine(14, centerY + 9, 23, centerY + 9);
            g2.drawLine(35, centerY + 9, 44, centerY + 9);
            g2.drawLine(23, centerY + 4, 23, centerY + 14);
            g2.drawLine(35, centerY + 4, 35, centerY + 14);
        } else if (blockType.equals("COIL") || blockType.equals("COIL_NEG") || blockType.equals("LATCH") || blockType.equals("UNLATCH")) {
            g2.drawLine(2, centerY, 14, centerY);
            g2.drawArc(11, centerY - 11, 14, 22, 90, 180);
            g2.drawArc(33, centerY - 11, 14, 22, 270, 180);
            g2.drawLine(46, centerY, w - 2, centerY);
            if (blockType.equals("COIL_NEG")) {
                g2.drawLine(18, centerY + 9, 40, centerY - 9);
            } else if (blockType.equals("LATCH")) {
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("L", 26, centerY + 4);
            } else if (blockType.equals("UNLATCH")) {
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("U", 25, centerY + 4);
            }
        } else {
            g2.drawLine(2, centerY, 8, centerY);
            g2.drawRoundRect(8, 5, 42, 28, 4, 4);
            g2.drawLine(50, centerY, w - 2, centerY);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            int strW = g2.getFontMetrics().stringWidth(blockType);
            g2.drawString(blockType, (w - strW) / 2, centerY + 4);
        }
        g2.dispose();
        return new ImageIcon(img);
    }
}
