package screens.ladder;

import ilcompiler.memoryvariable.MemoryVariable;
import java.awt.*;
import java.util.Map;

public class ContactBlock extends LadderBlock {
    private boolean isNormallyOpen;

    public ContactBlock(String varName, boolean isNormallyOpen) {
        super(varName, isNormallyOpen ? "NO" : "NC");
        this.isNormallyOpen = isNormallyOpen;
    }

    public boolean isNormallyOpen() {
        return isNormallyOpen;
    }

    @Override
    public boolean isOutput() { return false; } 

    @Override
    public void updateState(Map<String, Boolean> inputs, Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        super.updateState(inputs, outputs, memoryVariables);
        if (variableName == null || variableName.isEmpty() || variableName.equals("<???>")) {
            this.isActive = false;
            return;
        }
        // Para portas NOT (Contato Fechado / NC): inverte a condução/brilho
        // Em repouso (0), o contato conduz (1) -> Brilha Verde
        // Acionado (1), o contato abre (0) -> Fica Branco
        if (!isNormallyOpen) {
            this.isActive = !this.isActive;
        }
    }

    @Override
    public String compileToIL(boolean isFirstElement) {
        if (isNormallyOpen) return isFirstElement ? "LD " + variableName + "\n" : "AND " + variableName + "\n";
        else return isFirstElement ? "LDN " + variableName + "\n" : "ANDN " + variableName + "\n";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), centerY = 40; 

        g2.setStroke(new BasicStroke(3));
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        int textX = (w - g2.getFontMetrics().stringWidth(variableName)) / 2;
        
        // Nome da Tag: Verde quando conduzindo (1), Branco quando desligado (0), Vermelho se vazio
        if (variableName.equals("<???>")) g2.setColor(Color.RED);
        else if (isActive) g2.setColor(new Color(0, 255, 100));
        else g2.setColor(Color.WHITE);
        
        g2.drawString(variableName, textX, 20);

        // Contato fica Verde se conduz (1) e Branco se bloqueia (0)
        g2.setColor(isActive ? new Color(0, 255, 100) : Color.WHITE);
        g2.drawLine(0, centerY, 30, centerY); 
        g2.drawLine(30, centerY - 15, 30, centerY + 15); 
        g2.drawLine(50, centerY - 15, 50, centerY + 15); 
        g2.drawLine(50, centerY, w, centerY); 
        
        if (!isNormallyOpen) g2.drawLine(20, centerY + 15, 60, centerY - 15); 
    }
}
