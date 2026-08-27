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

        // ------------------ Paleta Superior em Abas ------------------
        JTabbedPane toolTabs = new JTabbedPane();
        toolTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Aba 1: Bit / Lógica
        JPanel tabBit = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        tabBit.setBackground(new Color(225, 232, 240));
        tabBit.add(new DraggableTool("Contato Aberto (NO)", "NO"));
        tabBit.add(new DraggableTool("Contato Fechado (NC)", "NC"));
        tabBit.add(new DraggableTool("Bobina Normal (ST)", "COIL"));
        tabBit.add(new DraggableTool("Bobina Negada (STN)", "COIL_NEG"));
        tabBit.add(new DraggableTool("Bobina Latch -(L)- (Set)", "LATCH"));
        tabBit.add(new DraggableTool("Bobina Unlatch -(U)- (Reset)", "UNLATCH"));
        tabBit.add(new DraggableTool("Ramal Paralelo (OR)", "OR"));

        // Aba 2: Timer / Counter
        JPanel tabTimer = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        tabTimer.setBackground(new Color(225, 232, 240));
        tabTimer.add(new DraggableTool("Timer On-Delay", "TON"));
        tabTimer.add(new DraggableTool("Timer Off-Delay", "TOFF"));
        tabTimer.add(new DraggableTool("Timer Retentivo (RTO)", "RTO"));
        tabTimer.add(new DraggableTool("Contador Crescente (CTU)", "CTU"));
        tabTimer.add(new DraggableTool("Contador Decrescente (CTD)", "CTD"));
        tabTimer.add(new DraggableTool("Reset de Timer/Contador (RES)", "RES"));

        // Aba 3: Controle / Linhas
        JPanel tabControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        tabControl.setBackground(new Color(225, 232, 240));
        JButton btnAddRung = new JButton("+ Adicionar Linha");
        JButton btnClear = new JButton("Limpar Diagrama");
        tabControl.add(btnAddRung);
        tabControl.add(btnClear);

        toolTabs.addTab("Bit / Lógica", tabBit);
        toolTabs.addTab("Timer / Counter", tabTimer);
        toolTabs.addTab("Controle", tabControl);

        this.add(toolTabs, BorderLayout.NORTH);

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

            if (op.equals("TON") || op.equals("TOFF") || op.equals("RTO") || op.equals("CTU") || op.equals("CTD")) {
                String[] subArgs = args.split(",");
                String var = subArgs[0];
                String pre = subArgs.length > 1 ? subArgs[1] : "0";
                declaredBoxes.put(var, new String[]{op, pre});
                expectedLDToIgnore = null;
                continue;
            }

            if (op.equals("RES")) {
                if (currentRung == null) {
                    currentRung = new LadderRung();
                    rungs.add(currentRung);
                    rungsContainer.add(currentRung);
                }
                BoxBlock resBlock = new BoxBlock("RES", args, "0");
                currentRung.addBlockAt(resBlock, currentRung.getBlockCount());
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
                        CoilBlock coil = new CoilBlock(args, "NORMAL");
                        currentRung.addBlockAt(coil, currentRung.getBlockCount());
                        expectedLDToIgnore = null;
                    }
                }
                else if (op.equals("STN")) {
                    CoilBlock coil = new CoilBlock(args, "COIL_NEG");
                    currentRung.addBlockAt(coil, currentRung.getBlockCount());
                    expectedLDToIgnore = null;
                }
                else if (op.equals("S") || op.equals("SET") || op.equals("OTL")) {
                    CoilBlock coil = new CoilBlock(args, "LATCH");
                    currentRung.addBlockAt(coil, currentRung.getBlockCount());
                    expectedLDToIgnore = null;
                }
                else if (op.equals("R") || op.equals("RST") || op.equals("OTU")) {
                    CoilBlock coil = new CoilBlock(args, "UNLATCH");
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
