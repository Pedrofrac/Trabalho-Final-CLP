package screens.scenes;

import ilcompiler.input.Input.InputType;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DefaultScenePanel extends javax.swing.JPanel implements IScenePanel {

    private InputEventListener inputListener;
    private ImageIcon openSwitchIcon, closedSwitchIcon, buttonIcon, closedButtonIcon, openPiButtonIcon, piButtonIcon,
            offLedIcon, onLedIcon;

    // Componentes de Entrada
    private JLabel inputButton1, inputButton2, inputButton3, inputButton4;
    private JLabel inputButton5, inputButton6, inputButton7, inputButton8;

    // Componentes de Saída
    private JLabel outputLed1, outputLed2, outputLed3, outputLed4;
    private JLabel outputLed5, outputLed6, outputLed7, outputLed8;

    private JLabel inputColumnLabel;
    private JLabel outputColumnLabel;

    public DefaultScenePanel() {
        super();
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        this.setBackground(new Color(142, 177, 199));
        this.setMaximumSize(new Dimension(624, 394));
        this.setMinimumSize(new Dimension(624, 394));
        this.setPreferredSize(new Dimension(624, 394));
        this.setLayout(new BorderLayout(10, 8));
        this.setBorder(new EmptyBorder(8, 25, 10, 25));

        // ------------------ TÍTULOS SUPERIORES ------------------
        JPanel titlesPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        titlesPanel.setOpaque(false);

        inputColumnLabel = new JLabel("Entradas", SwingConstants.CENTER);
        inputColumnLabel.setFont(new Font("Segoe UI Black", Font.ITALIC, 21));
        inputColumnLabel.setForeground(new Color(20, 35, 55));

        outputColumnLabel = new JLabel("Saídas", SwingConstants.CENTER);
        outputColumnLabel.setFont(new Font("Segoe UI Black", Font.ITALIC, 21));
        outputColumnLabel.setForeground(new Color(20, 35, 55));

        titlesPanel.add(inputColumnLabel);
        titlesPanel.add(outputColumnLabel);
        this.add(titlesPanel, BorderLayout.NORTH);

        // ------------------ PAINEL CENTRAL ------------------
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 50, 0));
        centerPanel.setOpaque(false);

        // ===== LADO ESQUERDO: ENTRADAS (2 Colunas x 4 Linhas) =====
        JPanel inputsContainer = new JPanel(new GridLayout(4, 2, 10, 6));
        inputsContainer.setOpaque(false);

        inputButton1 = createInputButton("I0.0");
        inputButton2 = createInputButton("I0.1");
        inputButton3 = createInputButton("I0.2");
        inputButton4 = createInputButton("I0.3");
        inputButton5 = createInputButton("I0.4");
        inputButton6 = createInputButton("I0.5");
        inputButton7 = createInputButton("I0.6");
        inputButton8 = createInputButton("I0.7");

        // Linha 1
        inputsContainer.add(createItemBox(inputButton1, "I0.0"));
        inputsContainer.add(createItemBox(inputButton5, "I0.4"));

        // Linha 2
        inputsContainer.add(createItemBox(inputButton2, "I0.1"));
        inputsContainer.add(createItemBox(inputButton6, "I0.5"));

        // Linha 3
        inputsContainer.add(createItemBox(inputButton3, "I0.2"));
        inputsContainer.add(createItemBox(inputButton7, "I0.6"));

        // Linha 4
        inputsContainer.add(createItemBox(inputButton4, "I0.3"));
        inputsContainer.add(createItemBox(inputButton8, "I0.7"));

        centerPanel.add(inputsContainer);

        // ===== LADO DIREITO: SAÍDAS (2 Colunas x 4 Linhas) =====
        JPanel outputsContainer = new JPanel(new GridLayout(4, 2, 20, 6));
        outputsContainer.setOpaque(false);

        outputLed1 = createOutputLed("Q0.0");
        outputLed2 = createOutputLed("Q0.1");
        outputLed3 = createOutputLed("Q0.2");
        outputLed4 = createOutputLed("Q0.3");
        outputLed5 = createOutputLed("Q0.4");
        outputLed6 = createOutputLed("Q0.5");
        outputLed7 = createOutputLed("Q0.6");
        outputLed8 = createOutputLed("Q0.7");

        // Linha 1
        outputsContainer.add(createItemBox(outputLed1, "Q0.0"));
        outputsContainer.add(createItemBox(outputLed5, "Q0.4"));

        // Linha 2
        outputsContainer.add(createItemBox(outputLed2, "Q0.1"));
        outputsContainer.add(createItemBox(outputLed6, "Q0.5"));

        // Linha 3
        outputsContainer.add(createItemBox(outputLed3, "Q0.2"));
        outputsContainer.add(createItemBox(outputLed7, "Q0.6"));

        // Linha 4
        outputsContainer.add(createItemBox(outputLed4, "Q0.3"));
        outputsContainer.add(createItemBox(outputLed8, "Q0.7"));

        centerPanel.add(outputsContainer);

        this.add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createItemBox(JComponent iconComponent, String tagId) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        // Rótulo pequeno e em negrito no TOPO
        JLabel tagLabel = new JLabel(tagId);
        tagLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tagLabel.setForeground(new Color(25, 45, 70));
        tagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        iconComponent.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(tagLabel);
        box.add(Box.createVerticalStrut(1));
        box.add(iconComponent);
        return box;
    }

    private JLabel createInputButton(String tagId) {
        JLabel btn = new JLabel();
        // Largura compacta (58x34) para preservar o formato natural redondo das chaves
        btn.setPreferredSize(new Dimension(58, 34));
        btn.setMinimumSize(new Dimension(58, 34));
        btn.setMaximumSize(new Dimension(58, 34));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Entrada " + tagId + " (Clique esquerdo: acionar / Clique direito: alterar tipo)");

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Assets/chave_aberta.png"));
            Image img = icon.getImage().getScaledInstance(58, 34, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        } catch (Exception ignored) {}

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (inputListener != null) {
                    inputListener.onPressed(tagId, evt);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (inputListener != null) {
                    inputListener.onReleased(tagId, evt);
                }
            }
        });

        return btn;
    }

    private JLabel createOutputLed(String tagId) {
        JLabel led = new JLabel();
        led.setPreferredSize(new Dimension(36, 44));
        led.setMinimumSize(new Dimension(36, 44));
        led.setMaximumSize(new Dimension(36, 44));
        led.setHorizontalAlignment(SwingConstants.CENTER);
        led.setToolTipText("Saída " + tagId);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Assets/led_desligado.png"));
            Image img = icon.getImage().getScaledInstance(36, 44, Image.SCALE_SMOOTH);
            led.setIcon(new ImageIcon(img));
        } catch (Exception ignored) {}

        return led;
    }

    @Override
    public void initInputs(Map<String, InputType> inputsType, Map<String, Boolean> inputs) {
        for (var key : inputsType.keySet()) {
            inputsType.put(key, InputType.SWITCH);
            inputs.put(key, false);
        }
    }

    @Override
    public void updateUIState(Map<String, InputType> inputsType, Map<String, Boolean> inputs,
            Map<String, Boolean> outputs) {
        
        int ledW = 36, ledH = 44;
        int swW = 40, swH = 44;

        try {
            openSwitchIcon = new ImageIcon(getClass().getResource("/Assets/chave_aberta.png"));
            openSwitchIcon.setImage(openSwitchIcon.getImage().getScaledInstance(swW, swH, Image.SCALE_SMOOTH));

            closedSwitchIcon = new ImageIcon(getClass().getResource("/Assets/chave_fechada.png"));
            closedSwitchIcon.setImage(closedSwitchIcon.getImage().getScaledInstance(swW, swH, Image.SCALE_SMOOTH));

            buttonIcon = new ImageIcon(getClass().getResource("/Assets/buttom.png"));
            buttonIcon.setImage(buttonIcon.getImage().getScaledInstance(swW, swH, Image.SCALE_SMOOTH));

            closedButtonIcon = new ImageIcon(getClass().getResource("/Assets/botao_fechado.png"));
            closedButtonIcon.setImage(closedButtonIcon.getImage().getScaledInstance(swW, swH, Image.SCALE_SMOOTH));

            openPiButtonIcon = new ImageIcon(getClass().getResource("/Assets/button_pi_aberto.png"));
            openPiButtonIcon.setImage(openPiButtonIcon.getImage().getScaledInstance(swW, swH, Image.SCALE_SMOOTH));

            piButtonIcon = new ImageIcon(getClass().getResource("/Assets/buttom_pi.png"));
            piButtonIcon.setImage(piButtonIcon.getImage().getScaledInstance(swW, swH, Image.SCALE_SMOOTH));

            offLedIcon = new ImageIcon(getClass().getResource("/Assets/led_desligado.png"));
            offLedIcon.setImage(offLedIcon.getImage().getScaledInstance(ledW, ledH, Image.SCALE_SMOOTH));

            onLedIcon = new ImageIcon(getClass().getResource("/Assets/led_ligado.png"));
            onLedIcon.setImage(onLedIcon.getImage().getScaledInstance(ledW, ledH, Image.SCALE_SMOOTH));
        } catch (Exception ignored) {}

        if (inputsType != null && inputs != null) {
            inputButton1.setIcon(getInputIcon(inputsType.get("I0.0"), Boolean.TRUE.equals(inputs.get("I0.0"))));
            inputButton2.setIcon(getInputIcon(inputsType.get("I0.1"), Boolean.TRUE.equals(inputs.get("I0.1"))));
            inputButton3.setIcon(getInputIcon(inputsType.get("I0.2"), Boolean.TRUE.equals(inputs.get("I0.2"))));
            inputButton4.setIcon(getInputIcon(inputsType.get("I0.3"), Boolean.TRUE.equals(inputs.get("I0.3"))));
            inputButton5.setIcon(getInputIcon(inputsType.get("I0.4"), Boolean.TRUE.equals(inputs.get("I0.4"))));
            inputButton6.setIcon(getInputIcon(inputsType.get("I0.5"), Boolean.TRUE.equals(inputs.get("I0.5"))));
            inputButton7.setIcon(getInputIcon(inputsType.get("I0.6"), Boolean.TRUE.equals(inputs.get("I0.6"))));
            inputButton8.setIcon(getInputIcon(inputsType.get("I0.7"), Boolean.TRUE.equals(inputs.get("I0.7"))));
        }

        if (outputs != null) {
            outputLed1.setIcon(getOutputIcon(Boolean.TRUE.equals(outputs.get("Q0.0"))));
            outputLed2.setIcon(getOutputIcon(Boolean.TRUE.equals(outputs.get("Q0.1"))));
            outputLed3.setIcon(getOutputIcon(Boolean.TRUE.equals(outputs.get("Q0.2"))));
            outputLed4.setIcon(getOutputIcon(Boolean.TRUE.equals(outputs.get("Q0.3"))));
            outputLed5.setIcon(getOutputIcon(Boolean.TRUE.equals(outputs.get("Q0.4"))));
            outputLed6.setIcon(getOutputIcon(Boolean.TRUE.equals(outputs.get("Q0.5"))));
            outputLed7.setIcon(getOutputIcon(Boolean.TRUE.equals(outputs.get("Q0.6"))));
            outputLed8.setIcon(getOutputIcon(Boolean.TRUE.equals(outputs.get("Q0.7"))));
        }
    }

    @Override
    public void stop() {
    }

    private ImageIcon getInputIcon(InputType inputType, boolean inputState) {
        if (inputType == null) inputType = InputType.SWITCH;
        return switch (inputType) {
            case SWITCH -> inputState ? closedSwitchIcon : openSwitchIcon;
            case NO -> inputState ? closedButtonIcon : buttonIcon;
            case NC -> inputState ? piButtonIcon : openPiButtonIcon;
        };
    }

    private ImageIcon getOutputIcon(boolean outputState) {
        return outputState ? onLedIcon : offLedIcon;
    }

    @Override
    public void setInputListener(InputEventListener listener) {
        this.inputListener = listener;
    }

    @Override
    public void setOnCriticalFailureCallback(Runnable callback) {
    }

    @Override
    public void resetUIState() {
    }
}
