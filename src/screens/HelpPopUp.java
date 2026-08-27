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
            body { font-family: Arial, sans-serif; background-color: #ffffff; color: #333333; padding: 10px; font-size: 11px; }
            h2 { color: #0056b3; border-bottom: 2px solid #0056b3; padding-bottom: 5px; margin-top: 0; font-size: 15px; }
            h3 { color: #d9534f; margin-top: 14px; margin-bottom: 4px; font-size: 12px; }
            .concept { background-color: #e9ecef; padding: 6px; border-radius: 4px; margin-bottom: 6px; }
            .code-box { background-color: #2d2d2d; color: #50fa7b; padding: 8px; border-radius: 4px; font-family: Consolas, monospace; margin: 4px 0; font-size: 11px; }
            .comment { color: #8be9fd; font-style: italic; }
            strong { color: #000; }
            li { margin-bottom: 3px; }
            .footer { margin-top: 15px; padding-top: 8px; border-top: 1px solid #ccc; font-size: 10px; color: #555; }
        </style>
    </head>
    <body>
        <h2>📖 Manual Técnico de Programação IL & Ladder</h2>
        
        <div class='concept'>
            <strong>Estrutura da Linguagem IL (Instruction List)</strong><br>
            O CLP processa as instruções sequencialmente de cima para baixo. A lógica básica consiste em: CARREGAR um estado de entrada, processar a LÓGICA BOOLEANA e ARMAZENAR o resultado em uma saída ou memória.
        </div>

        <h3>1. Endereçamento e Variáveis</h3>
        <ul>
            <li><strong>I0.0 a I0.7:</strong> Entradas físicas (Sinais de campo).</li>
            <li><strong>Q0.0 a Q0.7:</strong> Saídas físicas (Atuadores).</li>
            <li><strong>M0, M1...:</strong> Memórias internas (Bits auxiliares para armazenamento temporário).</li>
            <li><strong>T1, T2...:</strong> Temporizadores (Controle de tempo).</li>
            <li><strong>C1, C2...:</strong> Contadores (Controle de pulsos/eventos).</li>
        </ul>

        <h3>2. Instruções Lógicas Básicas</h3>
        <ul>
            <li><strong>LD / LDN:</strong> Inicia a linha lendo um contato Normalmente Aberto (LD) ou Fechado (LDN).</li>
            <li><strong>AND / ANDN:</strong> Adiciona uma condição em SÉRIE (Lógica E).</li>
            <li><strong>OR / ORN:</strong> Adiciona uma condição em PARALELO (Lógica OU).</li>
            <li><strong>ST / STN:</strong> Escreve o resultado em uma bobina normal (ST) ou invertida (STN).</li>
        </ul>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Carrega o estado da entrada I0.0)</span><br>
            OR I0.1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Lógica OU com a entrada I0.1)</span><br>
            ANDN I0.2 &nbsp;&nbsp;&nbsp;<span class='comment'>(Lógica E com a entrada I0.2 negada)</span><br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Armazena o resultado na saída Q0.0)</span>
        </div>

        <h3>3. Temporizadores (TON e TOFF)</h3>
        <p>Instruções baseadas em tempo. <em>O acumulador zera automaticamente se a condição for a falso.</em></p>
        <ul>
            <li><strong>TON (Timer On-Delay):</strong> Atraso na energização. A saída ativa ao fim do tempo.</li>
            <li><strong>TOFF (Timer Off-Delay):</strong> Atraso na desenergização. O tempo conta para desligar.</li>
        </ul>
        <div class='code-box'>
            TON T1,20 &nbsp;&nbsp;&nbsp;<span class='comment'>(Define T1 com preset de 2 segundos)</span><br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Condição de habilitação)</span><br>
            ST T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Energiza o temporizador T1)</span><br>
            LD T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Lê o bit de conclusão do T1)</span><br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Aciona Q0.0 após o atraso de 2s)</span>
        </div>

        <h3>4. Temporizador Retentivo (RTO)</h3>
        <p>Funciona de forma similar ao TON, porém <strong>retém o valor acumulado</strong> quando a linha de habilitação vai a falso. Requer a instrução <code>RES</code> para ser zerado.</p>
        <div class='code-box'>
            RTO T2,50 &nbsp;&nbsp;&nbsp;<span class='comment'>(Define T2 Retentivo para 5 segundos)</span><br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Condição de habilitação)</span><br>
            ST T2 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Conta e retém o valor se I0.0 for a zero)</span><br>
            LD T2 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Lê o bit de conclusão do T2)</span><br>
            ST Q0.1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Aciona Q0.1 ao atingir 5s)</span>
        </div>

        <h3>5. Contadores Bidirecionais (CTU e CTD)</h3>
        <p>Para criar um contador bidirecional (Up/Down), <strong>utilize a mesma variável de memória</strong> (ex: C1) para as instruções de incremento e decremento.</p>
        <div class='code-box'>
            CTU C1,3 &nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Define C1 com preset 3. Prepara incremento)</span><br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Sinal de pulso crescente)</span><br>
            ST C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Incrementa +1 no acumulador de C1)</span><br>
            <br>
            CTD C1,3 &nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Mesma variável C1. Prepara decremento)</span><br>
            LD I0.1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Sinal de pulso decrescente)</span><br>
            ST C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Decrementa -1 no acumulador de C1)</span><br>
            <br>
            LD C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Lê o bit de conclusão de C1)</span><br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Aciona Q0.0 se o acumulador for >= 3)</span>
        </div>

        <h3>6. Instrução de Reset (RES)</h3>
        <p>Instrução utilizada para zerar forçadamente o acumulador de Contadores (<code>C</code>) e Temporizadores Retentivos (<code>RTO</code>).</p>
        <div class='code-box'>
            LD I0.2 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Condição de acionamento do Reset)</span><br>
            RES C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Zera o acumulador do contador C1)</span><br>
            RES T2 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Zera o acumulador do temporizador T2)</span>
        </div>

        <h3>7. Instruções de Retenção: Latch -(L)- e Unlatch -(U)-</h3>
        <p>Um pulso em <strong>S (Set/Latch)</strong> ativa e retém a saída. Um pulso em <strong>R (Reset/Unlatch)</strong> desativa a saída forçadamente.</p>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Pulso de acionamento)</span><br>
            S Q0.2 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Ativa e retém a saída Q0.2 em nível alto)</span><br>
            <br>
            LD I0.1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Pulso de desligamento)</span><br>
            R Q0.2 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Desativa a retenção da saída Q0.2)</span>
        </div>

        <h3>8. Circuito de Selo (Auto-retenção)</h3>
        <p>Lógica de retenção utilizando ramal paralelo. Garante a desenergização automática da saída em caso de interrupção do circuito.</p>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Contato de acionamento)</span><br>
            OR M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Contato de selo em paralelo)</span><br>
            ANDN I0.1 &nbsp;&nbsp;&nbsp;<span class='comment'>(Contato de interrupção NF)</span><br>
            ST M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Bobina da memória auxiliar)</span><br>
            <br>
            LD M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Lê a memória auxiliar)</span><br>
            ST Q0.3 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='comment'>(Aciona a saída física)</span>
        </div>

        <br>
        <div class="footer">
            💻 <a href='https://github.com/Pedrofrac/Trabalho-Final-CLP/tree/main/examples/Batch'>Acessar Diretório de Exemplos: Batch</a> &nbsp;|&nbsp; 
            💻 <a href='https://github.com/Pedrofrac/Trabalho-Final-CLP/tree/main/examples/Traffic-light'>Acessar Diretório de Exemplos: Traffic Light</a>
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
        scrollPane.setPreferredSize(new java.awt.Dimension(580, 560));

        JOptionPane.showMessageDialog(null, scrollPane, "Manual Técnico de Programação - CLP", JOptionPane.PLAIN_MESSAGE);
    }
}