package screens.ladder;

import ilcompiler.memoryvariable.MemoryVariable;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.*;

public class ParallelBlock extends LadderBlock {
    public LadderRung topBranch;
    public LadderRung bottomBranch;

    public ParallelBlock() {
        super("", "PARALLEL");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);
        this.setAlignmentY(Component.TOP_ALIGNMENT);

        topBranch = new LadderRung(true);
        bottomBranch = new LadderRung(true);

        this.add(topBranch);
        this.add(bottomBranch);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (LadderCanvas.isSimulating) {
                    LadderCanvas.setSimulatingGlobal(false);
                    return;
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem itemExcluir = new JMenuItem("Excluir Ramo Paralelo");
                    itemExcluir.addActionListener(a -> {
                        Container parent = getParent();
                        if (parent instanceof LadderRung) {
                            ((LadderRung) parent).removeParallelBlockWithoutCleanup(ParallelBlock.this);
                        }
                    });
                    menu.add(itemExcluir);
                    menu.show(ParallelBlock.this, e.getX(), e.getY());
                }
            }
        });
    }

    @Override
    public boolean isOutput() { return false; }

    public boolean isBranchStartingActive(LadderRung branch) {
        if (branch == null || branch.getBlockCount() == 0) return false;
        LadderBlock first = branch.getBlocks().get(0);
        if (first instanceof ParallelBlock) {
            return ((ParallelBlock) first).isStartingActive();
        }
        return first.isActive();
    }

    public boolean isStartingActive() {
        return isBranchStartingActive(topBranch) || isBranchStartingActive(bottomBranch);
    }

    public boolean isBranchFullyActive(LadderRung branch) {
        if (branch == null || branch.getBlockCount() == 0) return false;
        for (LadderBlock b : branch.getBlocks()) {
            if (b instanceof ParallelBlock) {
                if (!((ParallelBlock) b).isFullyActive()) return false;
            } else if (!b.isOutput() && !b.isActive()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFullyActive() {
        return isBranchFullyActive(topBranch) || isBranchFullyActive(bottomBranch);
    }

    @Override
    public void updateState(Map<String, Boolean> inputs, Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        if (topBranch != null) topBranch.updateState(inputs, outputs, memoryVariables);
        if (bottomBranch != null) bottomBranch.updateState(inputs, outputs, memoryVariables);
        this.isActive = isFullyActive();
        this.repaint();
    }

    public void cleanup(LadderRung parentRung) {
        boolean topEmpty = (topBranch == null || topBranch.getBlockCount() == 0);
        boolean botEmpty = (bottomBranch == null || bottomBranch.getBlockCount() == 0);

        if (topEmpty && botEmpty) {
            parentRung.removeParallelBlockWithoutCleanup(this);
        } else if (botEmpty) {
            parentRung.unwrapParallelBlock(this, topBranch);
        } else if (topEmpty) {
            parentRung.unwrapParallelBlock(this, bottomBranch);
        } else {
            this.renderizarSubRungs();
            parentRung.renderizarLinhaComEspacosVazios();
        }
    }

    public void renderizarSubRungs() {
        if (topBranch != null) topBranch.renderizarLinhaComEspacosVazios();
        if (bottomBranch != null) bottomBranch.renderizarLinhaComEspacosVazios();
        this.revalidate();
        this.repaint();
    }

    private int getBranchWidth(LadderRung branch) {
        if (branch == null) return 0;
        int w = 0;
        for (Component c : branch.getComponents()) {
            w += c.getPreferredSize().width;
        }
        return Math.max(w, branch.getPreferredSize().width);
    }

    @Override
    public Dimension getPreferredSize() {
        int wTop = getBranchWidth(topBranch);
        int wBot = getBranchWidth(bottomBranch);
        int maxW = Math.max(wTop, wBot);
        if (maxW < 80) maxW = 80;

        int hTop = topBranch != null ? topBranch.getPreferredSize().height : 120;
        int hBot = bottomBranch != null ? bottomBranch.getPreferredSize().height : 120;
        
        return new Dimension(maxW, hTop + hBot);
    }

    @Override
    public Dimension getMinimumSize() { return getPreferredSize(); }
    @Override
    public Dimension getMaximumSize() { return getPreferredSize(); }

    @Override
    public String compileToIL(boolean isFirstElement) {
        StringBuilder sb = new StringBuilder();
        sb.append(topBranch.compileRung());
        
        String bottomCode = bottomBranch.compileRung();
        String[] lines = bottomCode.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("LDN ")) sb.append(line.replaceFirst("LDN ", "ORN ")).append("\n");
            else if (line.startsWith("LD ")) sb.append(line.replaceFirst("LD ", "OR ")).append("\n");
            else if (line.startsWith("ANDN ")) sb.append(line.replaceFirst("ANDN ", "ORN ")).append("\n");
            else if (line.startsWith("AND ")) sb.append(line.replaceFirst("AND ", "OR ")).append("\n");
            else sb.append(line).append("\n");
        }
        return sb.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int totalW = getWidth();
        int topY = 40;
        int hTop = topBranch != null ? topBranch.getHeight() : 120;
        if (hTop == 0 && topBranch != null) hTop = topBranch.getPreferredSize().height;
        int botY = hTop + 40;

        int wTop = getBranchWidth(topBranch);
        int wBot = getBranchWidth(bottomBranch);

        boolean inputActive = isStartingActive();
        boolean outputActive = isFullyActive();
        boolean topFull = isBranchFullyActive(topBranch);
        boolean botFull = isBranchFullyActive(bottomBranch);

        Color greenColor = new Color(0, 255, 100);

        // 1. Linha Vertical Esquerda (acende se algum ramo começar ligado)
        g2.setColor(inputActive ? greenColor : Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, topY, 0, botY);

        // 2. Extensão Horizontal Superior até a junção direita
        if (wTop < totalW) {
            g2.setColor(topFull ? greenColor : Color.WHITE);
            g2.drawLine(wTop, topY, totalW, topY);
        }

        // 3. Extensão Horizontal Inferior até a junção direita
        if (wBot < totalW) {
            g2.setColor(botFull ? greenColor : Color.WHITE);
            g2.drawLine(wBot, botY, totalW, botY);
        }
        
        // 4. Linha Vertical Direita (acende se ao menos um ramo conduzir até o final)
        g2.setColor(outputActive ? greenColor : Color.WHITE);
        g2.drawLine(totalW - 2, topY, totalW - 2, botY);
    }
}
