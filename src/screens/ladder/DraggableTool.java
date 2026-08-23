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
        this.setPreferredSize(new Dimension(65, 45));
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setToolTipText(tooltip);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

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
        BufferedImage img = new BufferedImage(60, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(0, 50, 100)); 
        g2.fillRect(0, 0, 60, 40);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        
        int centerY = 20;
        int w = 60;
        
        if (blockType.equals("NO") || blockType.equals("NC")) {
            g2.drawLine(0, centerY, 15, centerY);
            g2.drawLine(15, centerY-10, 15, centerY+10);
            g2.drawLine(45, centerY-10, 45, centerY+10);
            g2.drawLine(45, centerY, w, centerY);
            if (blockType.equals("NC")) g2.drawLine(10, centerY+12, 50, centerY-12);
        } else if (blockType.equals("OR") || blockType.equals("ORN")) {
            g2.drawLine(0, centerY, w, centerY); 
            g2.drawLine(15, centerY, 15, centerY+10);
            g2.drawLine(45, centerY, 45, centerY+10);
            g2.drawLine(15, centerY+10, 25, centerY+10);
            g2.drawLine(35, centerY+10, 45, centerY+10);
            g2.drawLine(25, centerY+5, 25, centerY+15);
            g2.drawLine(35, centerY+5, 35, centerY+15);
            if(blockType.equals("ORN")) g2.drawLine(20, centerY+18, 40, centerY+2);
        } else if (blockType.equals("COIL") || blockType.equals("COIL_NEG")) {
            g2.drawLine(0, centerY, 15, centerY);
            g2.drawArc(10, centerY-12, 15, 24, 90, 180);
            g2.drawArc(35, centerY-12, 15, 24, 270, 180);
            g2.drawLine(50, centerY, w, centerY);
            if (blockType.equals("COIL_NEG")) g2.drawLine(20, centerY+10, 40, centerY-10);
        } else {
            g2.drawLine(0, centerY, 10, centerY);
            g2.drawRect(10, 5, 40, 30);
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.drawString(blockType, 15, 25);
        }
        g2.dispose();
        return new ImageIcon(img);
    }
}
