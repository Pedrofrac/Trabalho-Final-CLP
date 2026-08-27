package screens.ladder;

import ilcompiler.memoryvariable.MemoryVariable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class LadderRung extends JPanel {
    private List<LadderBlock> blocks;
    private boolean isSelected;
    private boolean isSubRung;

    public LadderRung() {
        this(false);
    }

    public LadderRung(final boolean isSubRung) {
        this.blocks = new ArrayList<>();
        this.isSelected = false;
        this.isSubRung = isSubRung;
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setOpaque(false);
        
        if (!isSubRung) {
            this.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        }
        
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (LadderCanvas.isSimulating) {
                    LadderCanvas.setSimulatingGlobal(false);
                }
                Container parent = LadderRung.this.getParent();
                if (parent != null) {
                    for (Component c : parent.getComponents()) {
                        if (c instanceof LadderRung) {
                            ((LadderRung) c).setSelected(false);
                        }
                    }
                }

                LadderRung.this.setSelected(true);
                if (SwingUtilities.isRightMouseButton(e) && !isSubRung) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem itemExcluir = new JMenuItem("Excluir Linha");
                    itemExcluir.addActionListener((evt) -> {
                        if (parent != null) {
                            parent.remove(LadderRung.this);
                            parent.revalidate();
                            parent.repaint();
                        }
                    });
                    menu.add(itemExcluir);
                    menu.show(LadderRung.this, e.getX(), e.getY());
                }
            }
        });
        this.renderizarLinhaComEspacosVazios();
    }

    public int getBlockCount() {
        return this.blocks.size();
    }

    public List<LadderBlock> getBlocks() {
        return new ArrayList<>(this.blocks);
    }

    public boolean isPowerReachingIndex(int index) {
        if (this.blocks.isEmpty()) return false;

        if (index <= 0) {
            LadderBlock first = this.blocks.get(0);
            if (first instanceof ParallelBlock) {
                return ((ParallelBlock) first).isStartingActive();
            }
            return first.isActive();
        }

        int prevIndex = index - 1;
        if (prevIndex >= this.blocks.size()) {
            prevIndex = this.blocks.size() - 1;
        }

        boolean power = true;
        for (int i = 0; i <= prevIndex; i++) {
            LadderBlock b = this.blocks.get(i);
            if (b instanceof BoxBlock) {
                power = b.isActive();
            } else if (b instanceof CoilBlock) {
                power = b.isActive();
            } else if (b instanceof ParallelBlock) {
                power = ((ParallelBlock) b).isFullyActive();
            } else {
                power = power && b.isActive();
            }
        }
        return power;
    }

    public void resetState() {
        for (LadderBlock b : this.blocks) {
            b.setActive(false);
            if (b instanceof ParallelBlock) {
                ParallelBlock pb = (ParallelBlock) b;
                if (pb.topBranch != null) pb.topBranch.resetState();
                if (pb.bottomBranch != null) pb.bottomBranch.resetState();
            }
        }
        this.repaint();
    }

    public void updateState(Map<String, Boolean> inputs, Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        for (LadderBlock b : this.blocks) {
            b.updateState(inputs, outputs, memoryVariables);
        }
        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int w = this.isSubRung ? 0 : 20;
        int h = 120;

        for (Component c : this.getComponents()) {
            w += c.getPreferredSize().width;
            if (c.getPreferredSize().height > h) {
                h = c.getPreferredSize().height;
            }
        }

        if (w < 80) w = 80;

        if (this.isSubRung) {
            return new Dimension(w, h);
        } else {
            return new Dimension(Math.max(w, 3000), h);
        }
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height);
    }

    public void renderizarLinhaComEspacosVazios() {
        this.removeAll();

        for (int i = 0; i < this.blocks.size(); ++i) {
            LadderBlock b = this.blocks.get(i);
            if (b instanceof ParallelBlock) {
                ((ParallelBlock) b).renderizarSubRungs();
            }
            this.add(new DropZoneBlock(this, i, false));
            this.add(b);
        }
        this.add(new DropZoneBlock(this, this.blocks.size(), true));

        this.revalidate();
        this.repaint();

        for (Container p = this.getParent(); p != null; p = p.getParent()) {
            p.revalidate();
            p.repaint();
        }
    }

    public void removeBlock(LadderBlock block) {
        this.blocks.remove(block);
        
        if (this.isSubRung) {
            Container parent = this.getParent();
            if (parent instanceof ParallelBlock) {
                ParallelBlock pb = (ParallelBlock) parent;
                Container grandParent = pb.getParent();
                if (grandParent instanceof LadderRung) {
                    pb.cleanup((LadderRung) grandParent);
                    return;
                }
            }
        }
        
        this.renderizarLinhaComEspacosVazios();
    }

    public void unwrapParallelBlock(ParallelBlock pb, LadderRung survivorBranch) {
        int index = this.blocks.indexOf(pb);
        if (index != -1) {
            this.blocks.remove(index);
            List<LadderBlock> survivingBlocks = survivorBranch.getBlocks();
            for (int i = 0; i < survivingBlocks.size(); i++) {
                this.blocks.add(index + i, survivingBlocks.get(i));
            }
        }
        this.renderizarLinhaComEspacosVazios();
    }

    public void removeParallelBlockWithoutCleanup(ParallelBlock pb) {
        this.blocks.remove(pb);
        this.renderizarLinhaComEspacosVazios();
    }

    public boolean processDropData(String tool, int index) {
        if (LadderCanvas.isSimulating) {
            LadderCanvas.setSimulatingGlobal(false);
        }

        if (index < 0) index = 0;
        if (index > this.blocks.size()) index = this.blocks.size();

        if (tool.equals("MOVE") && LadderBlock.draggedBlock != null) {
            LadderBlock dragged = LadderBlock.draggedBlock;
            LadderBlock.draggedBlock = null;
            Container parent = dragged.getParent();
            
            if (parent == this) {
                int oldIndex = this.blocks.indexOf(dragged);
                if (oldIndex != -1) {
                    this.blocks.remove(oldIndex);
                    if (oldIndex < index) {
                        index--;
                    }
                }
            } else if (parent instanceof LadderRung) {
                ((LadderRung) parent).removeBlock(dragged);
            }

            this.addBlockAt(dragged, index);
            return true;
        } 
        
        if (tool.equals("OR") || tool.equals("ORN")) {
            ParallelBlock pb = new ParallelBlock();
            if (index > 0 && index <= this.blocks.size()) {
                LadderBlock target = this.blocks.get(index - 1);
                if (!target.isOutput()) {
                    this.blocks.remove(index - 1);
                    pb.topBranch.addBlockAt(target, 0);
                    pb.bottomBranch.addBlockAt(new ContactBlock("<???>", tool.equals("OR")), 0);
                    this.addBlockAt(pb, index - 1);
                    return true;
                }
            }
            pb.bottomBranch.addBlockAt(new ContactBlock("<???>", tool.equals("OR")), 0);
            this.addBlockAt(pb, index);
            return true;
        }

        LadderBlock newBlock = null;
        if (tool.equals("NO")) newBlock = new ContactBlock("<???>", true);
        else if (tool.equals("NC")) newBlock = new ContactBlock("<???>", false);
        else if (tool.equals("COIL")) newBlock = new CoilBlock("<???>", "NORMAL");
        else if (tool.equals("COIL_NEG")) newBlock = new CoilBlock("<???>", "COIL_NEG");
        else if (tool.equals("LATCH") || tool.equals("L")) newBlock = new CoilBlock("<???>", "LATCH");
        else if (tool.equals("UNLATCH") || tool.equals("U")) newBlock = new CoilBlock("<???>", "UNLATCH");
        else if (tool.equals("TON") || tool.equals("TOFF") || tool.equals("RTO") || tool.equals("CTU") || tool.equals("CTD")) {
            newBlock = new BoxBlock(tool, "<???>", "?");
        } else if (tool.equals("RES")) {
            newBlock = new BoxBlock("RES", "<???>", "0");
        }

        if (newBlock != null) {
            this.addBlockAt(newBlock, index);
            LadderBlock finalBlock = newBlock;
            SwingUtilities.invokeLater(() -> finalBlock.renomear());
            return true;
        }
        return false;
    }

    public void addBlockAt(LadderBlock block, int index) {
        if (block.isOutput()) {
            this.blocks.add(block);
        } else {
            int lastInputIndex = this.blocks.size();
            for (int i = 0; i < this.blocks.size(); ++i) {
                if (this.blocks.get(i).isOutput()) {
                    lastInputIndex = i;
                    break;
                }
            }
            if (index > lastInputIndex) index = lastInputIndex;
            if (index < 0) index = 0;

            this.blocks.add(index, block);
        }
        this.renderizarLinhaComEspacosVazios();
    }

    public int getIndexOfBlock(LadderBlock block) {
        return this.blocks.indexOf(block);
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        this.repaint();
    }

    public String compileRung() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (LadderBlock b : this.blocks) {
            sb.append(b.compileToIL(isFirst));
            isFirst = false;
        }
        return sb.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.isSelected && !this.isSubRung) {
            g2.setColor(new Color(0, 100, 200, 100));
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        if (!this.isSubRung) {
            // 1. Barra vermelha à esquerda (24V / Fase)
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawLine(2, 0, 2, this.getHeight());

            // Fio condutor inicial (20px)
            Color wireColor = Color.WHITE;
            if (!this.blocks.isEmpty()) {
                LadderBlock first = this.blocks.get(0);
                boolean firstActive = false;
                if (first instanceof ParallelBlock) {
                    firstActive = ((ParallelBlock) first).isStartingActive();
                } else {
                    firstActive = first.isActive();
                }
                if (firstActive) {
                    wireColor = new Color(0, 255, 100);
                }
            }
            g2.setColor(wireColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(2, 40, 20, 40);

            // 2. Tracinho vermelho no fim do fio (Pequeno e centralizado no fio)
            int rightX = 20;
            if (this.getComponentCount() > 0) {
                Component lastComp = this.getComponent(this.getComponentCount() - 1);
                rightX = lastComp.getX() + lastComp.getWidth();
            }
            if (rightX > 20) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawLine(rightX, 40 - 10, rightX, 40 + 10);
            }
        }

        if (this.isSelected && !this.isSubRung) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(1.0F));
            g2.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        }
    }
}
