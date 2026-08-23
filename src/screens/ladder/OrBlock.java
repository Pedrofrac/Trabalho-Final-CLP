package screens.ladder;
import java.awt.*;

public class OrBlock extends LadderBlock {
    private boolean isNormallyOpen;

    public OrBlock(String varName, boolean isNormallyOpen) {
        super(varName, isNormallyOpen ? "OR" : "ORN");
        this.isNormallyOpen = isNormallyOpen;
    }

    @Override
    public boolean isOutput() { return false; } 

    @Override
    public String compileToIL(boolean isFirstElement) {
        return (isNormallyOpen ? "OR " : "ORN ") + variableName + "\n";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int mainY = 40;     
        int branchY = 100;  

        g2.setColor(Color.WHITE); 
        g2.setStroke(new BasicStroke(3));

        // Fio Principal (cima)
        g2.drawLine(0, mainY, w, mainY); 

        // Fio Secundário (baixo)
        g2.drawLine(0, branchY, w, branchY);

        // Contato desenhado no fio de baixo
        g2.drawLine(25, branchY - 15, 25, branchY + 15); 
        g2.drawLine(55, branchY - 15, 55, branchY + 15); 
        if (!isNormallyOpen) {
            g2.drawLine(20, branchY + 15, 60, branchY - 15); 
        }

        // Tag
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        int textX = (w - g2.getFontMetrics().stringWidth(variableName)) / 2;
        if (variableName.equals("<???>")) g2.setColor(Color.RED);
        else g2.setColor(Color.BLACK);
        
        g2.drawString(variableName, textX, branchY - 20); 
    }
}