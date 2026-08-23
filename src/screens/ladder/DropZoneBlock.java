package screens.ladder;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import javax.swing.*;

public class DropZoneBlock extends JPanel {
    private LadderRung parentRung;
    private int insertIndex;
    private boolean isLast;

    public DropZoneBlock(LadderRung parentRung, int insertIndex, boolean isLast) {
        this.parentRung = parentRung;
        this.insertIndex = insertIndex;
        this.isLast = isLast;
        this.setOpaque(false);
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        this.setPreferredSize(new Dimension(28, 120));
        this.setMinimumSize(new Dimension(28, 120));
        this.setMaximumSize(new Dimension(28, 120));

        this.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    return parentRung.processDropData(data, insertIndex);
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int centerY = 40;
        int w = getWidth();

        // 1. Determina a cor do fio (Verde se a energia chegou neste trecho, Branco se desligado)
        boolean isConducting = false;
        if (LadderCanvas.isSimulating && parentRung != null) {
            isConducting = parentRung.isPowerReachingIndex(insertIndex);
        }
        Color wireColor = isConducting ? new Color(0, 255, 100) : Color.WHITE;

        // 2. Desenha o fio contínuo (-) do mesmo tamanho do bloco (28px)
        g2.setColor(wireColor);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, centerY, w, centerY);

        // 3. No modo de Edição (fora da simulação), desenha o quadradinho [+]
        if (!LadderCanvas.isSimulating) {
            int size = 16;
            int x = (w - size) / 2;
            int y = centerY - size / 2;

            g2.setColor(new Color(0, 180, 80, 70));
            g2.fillRect(x, y, size, size);

            g2.setColor(new Color(0, 220, 100));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(x, y, size, size);

            // Sinal de +
            g2.drawLine(x + size / 2, y + 3, x + size / 2, y + size - 3);
            g2.drawLine(x + 3, y + size / 2, x + size - 3, y + size / 2);
        }
    }
}
