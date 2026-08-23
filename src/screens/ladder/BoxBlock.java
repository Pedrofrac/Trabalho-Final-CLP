package screens.ladder;

import ilcompiler.memoryvariable.MemoryVariable;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import javax.swing.*;

public class BoxBlock extends LadderBlock {
    private String type; 
    private String preset;
    private int currentValueInt = 0;
    private int presetValueInt = 0;
    private boolean isInputEnergized = false;

    public BoxBlock(String type, String varName, String preset) {
        super(varName, type);
        this.type = type;
        this.preset = preset;
        try {
            if (!preset.equals("?")) {
                this.presetValueInt = Integer.parseInt(preset);
            }
        } catch (Exception e) {}
        this.setPreferredSize(new Dimension(120, 120)); 
        this.setMinimumSize(new Dimension(120, 120));
        this.setMaximumSize(new Dimension(120, 120)); 
    }

    @Override
    public boolean isOutput() { return false; } 

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if (!active) {
            this.isInputEnergized = false;
            this.currentValueInt = 0;
        }
        this.repaint();
    }

    private int extractCurrentValue(MemoryVariable mv) {
        if (mv == null) return 0;
        
        // 1. Tenta métodos getters comuns
        for (String methodName : new String[]{"getCurrentTime", "getTime", "getTimer", "getCount", "getCounter", "getCurrentValue", "getValue", "getAcc", "getAccumulated"}) {
            try {
                Method m = mv.getClass().getMethod(methodName);
                Object res = m.invoke(mv);
                if (res instanceof Number) {
                    return ((Number) res).intValue();
                }
            } catch (Exception ignored) {}
        }

        // 2. Tenta campos inteiros por nomes comuns
        for (String fieldName : new String[]{"currentTime", "time", "timer", "count", "counter", "currentCount", "accumulated", "acc", "value", "val", "current", "currentVal", "tempoAtual", "contagemAtual"}) {
            try {
                Field f = mv.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Object res = f.get(mv);
                if (res instanceof Number) {
                    return ((Number) res).intValue();
                }
            } catch (Exception ignored) {}
        }

        // 3. Fallback inteligente: procura qualquer campo inteiro que não seja maxTimer
        try {
            for (Field f : mv.getClass().getDeclaredFields()) {
                if (f.getType() == int.class || f.getType() == Integer.class) {
                    if (!f.getName().equalsIgnoreCase("maxTimer") && !f.getName().equalsIgnoreCase("max")) {
                        f.setAccessible(true);
                        Object res = f.get(mv);
                        if (res instanceof Number) {
                            return ((Number) res).intValue();
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        return 0;
    }

    @Override
    public void updateState(Map<String, Boolean> inputs, Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        if (variableName == null || variableName.isEmpty() || variableName.equals("<???>")) {
            this.isActive = false;
            this.isInputEnergized = false;
            this.currentValueInt = 0;
            return;
        }
        if (memoryVariables != null && memoryVariables.containsKey(variableName)) {
            MemoryVariable mv = memoryVariables.get(variableName);
            if (mv != null) {
                this.isInputEnergized = mv.currentValue;
                this.isActive = mv.endTimer;
                this.presetValueInt = mv.maxTimer;
                this.currentValueInt = extractCurrentValue(mv);
            }
        } else {
            this.isActive = false;
            this.isInputEnergized = false;
            this.currentValueInt = 0;
        }
        this.repaint();
    }

    @Override
    public String compileToIL(boolean isFirstElement) {
        return type + " " + variableName + "," + preset + "\nST " + variableName + "\nLD " + variableName + "\n";
    }

    @Override
    public void renomear() {
        Window window = SwingUtilities.getWindowAncestor(this);
        String currentTag = variableName.equals("<???>") ? "" : variableName;
        String currentPre = preset.equals("?") ? "" : preset;
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Tag (ex: T1, C1):"));
        JTextField tagField = new JTextField(currentTag);
        panel.add(tagField);
        
        panel.add(new JLabel("Preset (Tempo/Meta):"));
        JTextField preField = new JTextField(currentPre);
        panel.add(preField);

        int result = JOptionPane.showConfirmDialog(window, panel, "Configurar " + type, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newTag = tagField.getText().trim().toUpperCase();
            String newPre = preField.getText().trim();
            if (!newTag.isEmpty()) this.variableName = newTag;
            if (!newPre.isEmpty()) {
                this.preset = newPre;
                try {
                    this.presetValueInt = Integer.parseInt(newPre);
                } catch (Exception e) {}
            }
            this.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerY = 40;
        int boxX = 15;
        int boxY = 8;
        int boxW = 90;
        int boxH = 64;

        // 1. Fio de Entrada (Esquerda: 0 a 15)
        g2.setColor(isInputEnergized ? new Color(0, 255, 100) : Color.WHITE); 
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, centerY, boxX, centerY); 

        // 2. Fio de Saída (Direita: 105 a 120) -> Conexão completa com Q
        g2.setColor(isActive ? new Color(0, 255, 100) : Color.WHITE);
        g2.drawLine(boxX + boxW, centerY, getWidth(), centerY);

        // 3. Fundo da Caixinha
        g2.setColor(new Color(230, 235, 242));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 6, 6);

        // Borda Dinâmica
        if (isActive) {
            g2.setColor(new Color(0, 200, 80));
            g2.setStroke(new BasicStroke(2.5f));
        } else if (isInputEnergized) {
            g2.setColor(new Color(0, 130, 230));
            g2.setStroke(new BasicStroke(2.0f));
        } else {
            g2.setColor(new Color(80, 90, 100));
            g2.setStroke(new BasicStroke(1.5f));
        }
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 6, 6);

        // 4. Cabeçalho (Tipo e Tag)
        g2.setColor(isInputEnergized ? new Color(205, 225, 250) : new Color(212, 218, 226));
        g2.fillRoundRect(boxX + 1, boxY + 1, boxW - 2, 20, 6, 6);
        g2.fillRect(boxX + 1, boxY + 10, boxW - 2, 11);
        g2.setColor(new Color(175, 185, 195));
        g2.drawLine(boxX, boxY + 21, boxX + boxW, boxY + 21);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(new Color(20, 30, 45));
        String header = type + " (" + variableName + ")";
        g2.drawString(header, boxX + 8, boxY + 15);

        // 5. Preset (Meta)
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(60, 65, 75));
        String displayPre = preset.equals("?") ? String.valueOf(presetValueInt) : preset;
        g2.drawString("Pre: " + displayPre, boxX + 8, boxY + 36);

        // 6. Cronômetro / Contador (Acc)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        if (isActive) {
            g2.setColor(new Color(0, 150, 60)); // Verde ao atingir meta
        } else if (isInputEnergized) {
            g2.setColor(new Color(0, 100, 220)); // Azul enquanto conta
        } else {
            g2.setColor(new Color(80, 85, 95));  // Cinza em repouso
        }
        g2.drawString("Acc: " + currentValueInt, boxX + 8, boxY + 53);
    }
}
