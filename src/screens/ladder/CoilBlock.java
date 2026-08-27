package screens.ladder;

import java.awt.*;

public class CoilBlock extends LadderBlock {
    private String coilMode; // "NORMAL", "COIL_NEG", "LATCH", "UNLATCH"

    public CoilBlock(String varName, boolean isNormal) {
        this(varName, isNormal ? "NORMAL" : "COIL_NEG");
    }

    public CoilBlock(String varName, String mode) {
        super(varName, mode);
        this.coilMode = (mode == null || mode.isEmpty()) ? "NORMAL" : mode.toUpperCase();
    }

    @Override
    public boolean isOutput() { return true; } 

    @Override
    public String compileToIL(boolean isFirstElement) {
        return switch (coilMode) {
            case "LATCH", "L", "SET" -> "S " + variableName + "\n";
            case "UNLATCH", "U", "RST" -> "R " + variableName + "\n";
            case "COIL_NEG" -> "STN " + variableName + "\n";
            default -> "ST " + variableName + "\n";
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), centerY = 40;

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        int textX = (w - g2.getFontMetrics().stringWidth(variableName)) / 2;
        
        // Tag legível
        if (variableName.equals("<???>")) g2.setColor(Color.RED);
        else if (isActive) g2.setColor(new Color(0, 255, 100));
        else g2.setColor(Color.WHITE);
        
        g2.drawString(variableName, textX, 20);

        // Bobina (Verde se 1, Branca se 0)
        g2.setColor(isActive ? new Color(0, 255, 100) : Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, centerY, 20, centerY); 
        g2.drawArc(15, centerY - 15, 20, 30, 90, 180); 
        g2.drawArc(45, centerY - 15, 20, 30, 270, 180); 
        g2.drawLine(60, centerY, w, centerY); 
        
        // Simbologia interna
        if (coilMode.equals("COIL_NEG")) {
            g2.drawLine(20, centerY + 15, 60, centerY - 15);
        } else if (coilMode.equals("LATCH") || coilMode.equals("L")) {
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("L", 36, centerY + 5);
        } else if (coilMode.equals("UNLATCH") || coilMode.equals("U")) {
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("U", 35, centerY + 5);
        }
    }
}
