package screens;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HelpPopUp {

    public static void showHelp() {
        String htmlContent = """
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; background-color: #ffffff; color: #333333; padding: 12px; font-size: 11px; }
            h2 { color: #0056b3; border-bottom: 2px solid #0056b3; padding-bottom: 5px; margin-top: 0; font-size: 15px; }
            h3 { color: #d9534f; margin-top: 14px; margin-bottom: 4px; font-size: 12px; }
            .concept { background-color: #e9ecef; padding: 6px; border-radius: 4px; margin-bottom: 6px; border-left: 3px solid #0056b3; }
            .code-box { background-color: #2d2d2d; color: #50fa7b; padding: 8px; border-radius: 4px; font-family: Consolas, monospace; margin: 4px 0; font-size: 11px; }
            strong { color: #111; }
            li { margin-bottom: 3px; }
            .footer { margin-top: 15px; padding-top: 8px; border-top: 1px solid #ccc; font-size: 10px; color: #555; }
        </style>
    </head>
    <body>
        <h2>📖 Guia Completo de Programação IL & Ladder</h2>
        
        <div class='concept'>
            <strong>Como funciona?</strong> O CLP lê o programa ciclicamente do topo para a base. Cada linha processa condições elétricas de entrada para acionar bobinas e blocos de saída.
        </div>

        <h3>1. Endereços e Variáveis</h3>
        <ul>
            <li><strong>I0.0 a I0.7:</strong> Entradas físicas (Chaves, Botões, Sensores).</li>
            <li><strong>Q0.0 a Q0.7:</strong> Saídas físicas (Lâmpadas, Motores, Atuadores).</li>
            <li><strong>M0, M1...:</strong> Memórias virtuais / Flags booleanas internas.</li>
            <li><strong>T1, T2...:</strong> Temporizadores (TON, TOFF, RTO).</li>
            <li><strong>C1, C2...:</strong> Contadores (CTU, CTD).</li>
        </ul>

        <h3>2. Comandos Básicos (Lógica Booleana)</h3>
        <ul>
            <li><strong>LD (Load):</strong> Inicia a linha com contato aberto <code>[ ]</code>.</li>
            <li><strong>LDN (Load Not):</strong> Inicia a linha com contato fechado <code>[/]</code>.</li>
            <li><strong>AND / ANDN:</strong> Adiciona condição em série (E / E NÃO).</li>
            <li><strong>OR / ORN:</strong> Cria ramais paralelos (OU / OU NÃO).</li>
            <li><strong>ST (Store):</strong> Bobina normal <code>-( )-</code> (Ligada enquanto a linha conduzir).</li>
            <li><strong>STN (Store Not):</strong> Bobina invertida <code>-(/)-</code>.</li>
        </ul>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Se chave I0.0 ligada)<br>
            ANDN I0.1 &nbsp;&nbsp;(E sensor I0.1 desativado)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Então ligue a lâmpada Q0.0)
        </div>

        <h3>3. Bobinas Latch -(L)- (Set) e Unlatch -(U)- (Reset)</h3>
        <p>Permitem ligar uma saída com um pulso e mantê-la retida até receber um comando explícito de desligamento:</p>
        <ul>
            <li><strong>S (Set / Latch):</strong> Bobina <code>-( L )-</code>. Quando ativada, liga a saída/memória em 1 e <strong>continua ligada para sempre</strong> mesmo se a linha for desenergizada.</li>
            <li><strong>R (Reset / Unlatch):</strong> Bobina <code>-( U )-</code>. Quando ativada, desliga forçadamente a saída para 0.</li>
        </ul>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Botão Liga)<br>
            S Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Trava a saída Q0.0 ligada)<br>
            LD I0.1 &nbsp;&nbsp;&nbsp;&nbsp;(Botão Desliga)<br>
            R Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Destrava e desliga Q0.0)
        </div>

        <h3>4. Temporizadores (TON, TOFF e RTO)</h3>
        <p>Temporizam eventos com base na base de tempo configurada:</p>
        <ul>
            <li><strong>TON (On-Delay):</strong> Conta tempo enquanto energizado. <em>Zera automaticamente se perder energia.</em></li>
            <li><strong>TOFF (Off-Delay):</strong> Mantém ligado e inicia contagem ao desligar a entrada.</li>
            <li><strong>RTO (Retentive On-Delay):</strong> <strong>Temporizador Retentivo.</strong> Se a linha for desenergizada no meio da contagem, ele <strong>CONGELA o tempo acumulado</strong> e não zera (só reinicia via comando <code>RES</code>).</li>
        </ul>
        <div class='code-box'>
            RTO T1,50 &nbsp;&nbsp;(Configura T1 Retentivo para 5 seg)<br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Botão de contagem)<br>
            ST T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Energiza o RTO T1)<br>
            LD T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Quando T1 atingir a meta...)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Aciona a saída Q0.0)
        </div>

        <h3>5. Contadores (CTU / CTD)</h3>
        <p>Incrementam ou decrementam o acumulador a cada transição de sinal:</p>
        <ul>
            <li><strong>CTU:</strong> Contador crescente (Up). Ativa saída ao atingir o preset.</li>
            <li><strong>CTD:</strong> Contador decrescente (Down).</li>
        </ul>
        <div class='code-box'>
            CTU C1,5 &nbsp;&nbsp;&nbsp;(Meta: 5 peças/pulsos)<br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Sensor de passagem)<br>
            ST C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Incrementa contagem de C1)<br>
            LD C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Se C1 atingiu 5...)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Aciona esteira/saída)
        </div>

        <h3>6. Instrução de Reset (RES)</h3>
        <p>Zera imediatamente o acumulador e desliga a saída de Contadores (<code>C</code>) e Temporizadores Retentivos (<code>RTO</code>):</p>
        <div class='code-box'>
            LD I0.1 &nbsp;&nbsp;&nbsp;&nbsp;(Botão de Reset)<br>
            RES T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Zera o cronômetro do temporizador T1)<br>
            LD I0.2 &nbsp;&nbsp;&nbsp;&nbsp;(Botão de Zerar Contagem)<br>
            RES C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Zera a contagem do contador C1)
        </div>

        <br>
        <div class="footer">
            💻 <a href='https://github.com/Pedrofrac/Trabalho-Final-CLP/tree/main/examples/Batch'>Baixar Exemplos Tanque (Batch)</a> &nbsp;|&nbsp; 
            💻 <a href='https://github.com/Pedrofrac/Trabalho-Final-CLP/tree/main/examples/Traffic-light'>Baixar Exemplos Semáforo</a>
        </div>
    </body>
    </html>
""";
        JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.setCaretPosition(0);

        editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new java.awt.Dimension(540, 520));

        JOptionPane.showMessageDialog(null, scrollPane, "Manual do Usuário - CLP", JOptionPane.PLAIN_MESSAGE);
    }
}