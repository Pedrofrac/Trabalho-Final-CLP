package screens.ladder;

import ilcompiler.memoryvariable.MemoryVariable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class LadderCanvas extends JPanel {
    public static LadderCanvas instance;
    public static boolean isSimulating = false;

    private JPanel rungsContainer;
    private List<LadderRung> rungs = new ArrayList<>();

    public LadderCanvas() {
        instance = this;
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(24, 28, 36));

        JPanel palette = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        palette.setBackground(new Color(230, 240, 230)); 
        palette.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));

        JButton btnAddRung = new JButton("+ Linha");
        JButton btnClear = new JButton("Limpar");

        DraggableTool dragNO = new DraggableTool("Contato Aberto", "NO");
        DraggableTool dragNC = new DraggableTool("Contato Fechado", "NC");
        DraggableTool dragOR = new DraggableTool("Paralelo (OR)", "OR");
        DraggableTool dragCoil = new DraggableTool("Bobina", "COIL");
        DraggableTool dragCoilN = new DraggableTool("Bobina Inv.", "COIL_NEG");
        DraggableTool dragTON = new DraggableTool("Timer TON", "TON");
        DraggableTool dragTOFF = new DraggableTool("Timer TOFF", "TOFF");
        DraggableTool dragCTU = new DraggableTool("Contador CTU", "CTU");
        DraggableTool dragCTD = new DraggableTool("Contador CTD", "CTD");
        
        palette.add(btnAddRung);
        palette.add(dragNO);
        palette.add(dragNC);
        palette.add(dragOR);
        palette.add(dragCoil);
        palette.add(dragCoilN);
        palette.add(dragTON);
        palette.add(dragTOFF);
        palette.add(dragCTU);
        palette.add(dragCTD);
        palette.add(btnClear);

        this.add(palette, BorderLayout.NORTH);

        rungsContainer = new JPanel();
        rungsContainer.setLayout(new BoxLayout(rungsContainer, BoxLayout.Y_AXIS));
        rungsContainer.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(rungsContainer);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setBorder(null);
        this.add(scroll, BorderLayout.CENTER);

        btnAddRung.addActionListener(e -> {
            setSimulationMode(false);
            addRung();
        });
        btnClear.addActionListener(e -> {
            setSimulationMode(false);
            clearAll();
        });
        addRung();
    }

    public void setSimulationMode(boolean simulating) {
        if (isSimulating == simulating) {
            if (!simulating) {
                for (LadderRung rung : rungs) {
                    rung.resetState();
                }
                repaint();
            }
            return;
        }
        isSimulating = simulating;
        for (LadderRung rung : rungs) {
            if (!simulating) {
                rung.resetState();
            }
        }
        repaint();
    }

    public static void setSimulatingGlobal(boolean simulating) {
        if (instance != null) {
            instance.setSimulationMode(simulating);
        } else {
            isSimulating = simulating;
        }
    }

    public void updateSimulationState(Map<String, Boolean> inputs, Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        for (LadderRung rung : rungs) {
            rung.updateState(inputs, outputs, memoryVariables);
        }
        repaint();
    }

    public static void updateStateGlobal(Map<String, Boolean> inputs, Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        if (instance != null) {
            if (!isSimulating) {
                instance.setSimulationMode(true);
            }
            instance.updateSimulationState(inputs, outputs, memoryVariables);
        }
    }

    public void addRung() {
        LadderRung newRung = new LadderRung();
        rungs.add(newRung);
        rungsContainer.add(newRung);
        rungsContainer.revalidate(); 
        rungsContainer.repaint();
    }

    private void clearAll() {
        rungs.clear(); 
        rungsContainer.removeAll(); 
        addRung();
        rungsContainer.revalidate(); 
        rungsContainer.repaint();
    }

    public String compileAllToIL() {
        StringBuilder fullCode = new StringBuilder();
        for (LadderRung rung : rungs) fullCode.append(rung.compileRung());
        return fullCode.toString();
    }

    public void loadFromIL(String code) {
        setSimulationMode(false);
        rungs.clear();
        rungsContainer.removeAll();
        LadderRung currentRung = null;
        
        Map<String, String[]> declaredBoxes = new HashMap<>();
        String expectedLDToIgnore = null;
        List<LadderBlock> inputBlocksBeforeOutput = new ArrayList<>();

        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim().toUpperCase();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String op = parts[0];
            String args = parts.length > 1 ? parts[1] : "";

            if (op.equals("TON") || op.equals("TOFF") || op.equals("CTU") || op.equals("CTD")) {
                String[] subArgs = args.split(",");
                String var = subArgs[0];
                String pre = subArgs.length > 1 ? subArgs[1] : "0";
                declaredBoxes.put(var, new String[]{op, pre});
                expectedLDToIgnore = null;
                continue;
            }

            if (op.equals("LD") || op.equals("LDN")) {
                if (expectedLDToIgnore != null && op.equals("LD") && args.equals(expectedLDToIgnore)) {
                    expectedLDToIgnore = null;
                    continue;
                }
                expectedLDToIgnore = null;

                currentRung = new LadderRung();
                inputBlocksBeforeOutput.clear();
                rungs.add(currentRung);
                rungsContainer.add(currentRung);
                
                ContactBlock cb = new ContactBlock(args, op.equals("LD"));
                currentRung.addBlockAt(cb, currentRung.getBlockCount());
                inputBlocksBeforeOutput.add(cb);
            } 
            else if (currentRung != null) {
                if (op.equals("ST")) {
                    if (declaredBoxes.containsKey(args)) {
                        String[] boxInfo = declaredBoxes.get(args);
                        BoxBlock bb = new BoxBlock(boxInfo[0], args, boxInfo[1]);
                        currentRung.addBlockAt(bb, currentRung.getBlockCount());
                        expectedLDToIgnore = args; 
                    } else {
                        CoilBlock coil = new CoilBlock(args, true);
                        currentRung.addBlockAt(coil, currentRung.getBlockCount());
                        expectedLDToIgnore = null;
                    }
                }
                else if (op.equals("STN")) {
                    CoilBlock coil = new CoilBlock(args, false);
                    currentRung.addBlockAt(coil, currentRung.getBlockCount());
                    expectedLDToIgnore = null;
                }
                else if (op.equals("OR") || op.equals("ORN")) {
                    expectedLDToIgnore = null;
                    if (!inputBlocksBeforeOutput.isEmpty()) {
                        ParallelBlock pBlock = new ParallelBlock();
                        
                        for (LadderBlock b : inputBlocksBeforeOutput) {
                            currentRung.removeBlock(b);
                            pBlock.topBranch.addBlockAt(b, pBlock.topBranch.getBlockCount());
                        }
                        inputBlocksBeforeOutput.clear();
                        
                        pBlock.bottomBranch.addBlockAt(new ContactBlock(args, op.equals("OR")), 0);
                        
                        currentRung.addBlockAt(pBlock, currentRung.getBlockCount());
                        inputBlocksBeforeOutput.add(pBlock);
                    }
                }
                else {
                    expectedLDToIgnore = null;
                    ContactBlock cb = new ContactBlock(args, op.equals("AND"));
                    currentRung.addBlockAt(cb, currentRung.getBlockCount());
                    inputBlocksBeforeOutput.add(cb);
                }
            }
        }
        if (rungs.isEmpty()) addRung();
        rungsContainer.revalidate();
        rungsContainer.repaint();
    }
}
