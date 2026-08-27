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
            strong { color: #000; }
            li { margin-bottom: 3px; }
            .footer { margin-top: 15px; padding-top: 8px; border-top: 1px solid #ccc; font-size: 10px; color: #555; }
        </style>
    </head>
    <body>
        <h2>📖 Guia Completo de Programação IL & Ladder</h2>
        
        <div class='concept'>
            <strong>Como funciona?</strong> O CLP lê o código linha por linha. Imagine que você está montando uma frase lógica: <br>
            <i>"SE (botão apertado) E (sensor ativo) ENTÃO (ligue a lâmpada)"</i>.
        </div>

        <h3>1. Endereços (Quem é quem?)</h3>
        <ul>
            <li><strong>I0.0 a I0.7:</strong> Entradas (Botões, Sensores, Chaves).</li>
            <li><strong>Q0.0 a Q0.7:</strong> Saídas (Lâmpadas, Motores, Semáforos).</li>
            <li><strong>M0, M1...:</strong> Memórias (Guardam valor temporariamente, não existem no mundo físico).</li>
            <li><strong>T1, T2...:</strong> Temporizadores (TON, TOFF, RTO).</li>
            <li><strong>C1, C2...:</strong> Contadores (CTU, CTD).</li>
        </ul>

        <h3>2. Comandos Básicos (Lógica)</h3>
        <ul>
            <li><strong>LD (Load):</strong> Começa uma nova lógica ("Se..."). Contato Aberto <code>[ ]</code>.</li>
            <li><strong>LDN (Load Not):</strong> Começa invertido ("Se NÃO..."). Contato Fechado <code>[/]</code>.</li>
            <li><strong>AND:</strong> Adiciona uma condição em série ("E...").</li>
            <li><strong>OR:</strong> Cria uma alternativa em paralelo ("OU...").</li>
            <li><strong>ST (Store):</strong> Finaliza enviando para uma saída normal <code>-( )-</code> ("Então ligue...").</li>
            <li><strong>STN (Store Not):</strong> Bobina invertida <code>-(/)-</code>.</li>
            <li><strong>N (Sufixo):</strong> Negação (Inverso). Ex: <code>LDN</code>, <code>ANDN</code>, <code>ORN</code>.</li>
        </ul>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Se apertar I0.0)<br>
            ANDN I0.1 &nbsp;&nbsp;(E NÃO apertar I0.1)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Então ligue Q0.0)
        </div>

        <h3>3. Temporizadores Comuns (TON / TOFF)</h3>
        <p>Usados para esperar um tempo antes de ligar ou desligar. <em>Zeram automaticamente se perderem energia.</em></p>
        <ul>
            <li><strong>Configurar:</strong> <code>TON T1,20</code> (Cria T1 com 20 décimos de segundo = 2s).</li>
            <li><strong>Ativar:</strong> Use <code>ST T1</code> para iniciar a contagem.</li>
            <li><strong>Ler:</strong> Use <code>LD T1</code> para saber se o tempo acabou.</li>
        </ul>
        <div class='code-box'>
            TON T1,20 &nbsp;&nbsp;(Configura T1 para 2 seg)<br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Se botão I0.0...)<br>
            ST T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Inicia contagem do T1)<br>
            LD T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Se T1 acabou a contagem...)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Ligue Q0.0)
        </div>

        <h3>4. Temporizador Retentivo (RTO)</h3>
        <p>Funciona como o TON, mas <strong>CONGELA o tempo acumulado</strong> se a linha for desenergizada (não zera sozinho, só via comando <code>RES</code>).</p>
        <div class='code-box'>
            RTO T1,50 &nbsp;&nbsp;(Configura T1 Retentivo para 5 seg)<br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Se botão I0.0...)<br>
            ST T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Conta tempo e retém se soltar)<br>
            LD T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Se T1 atingiu 50...)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Ligue Q0.0)
        </div>

        <h3>5. Contadores (CTU / CTD)</h3>
        <p>Contam quantas vezes um evento ocorreu.</p>
        <ul>
            <li><strong>Configurar:</strong> <code>CTU C1,3</code> (Conta até 3 para ativar).</li>
            <li><strong>Contar:</strong> Use <code>ST C1</code> para enviar o pulso de contagem.</li>
            <li><strong>Ler:</strong> Use <code>LD C1</code> para saber se atingiu a meta.</li>
        </ul>
        <div class='code-box'>
            CTU C1,3 &nbsp;&nbsp;&nbsp;(Meta: 3 pulsos)<br>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Ler botão)<br>
            ST C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Envia pulso para C1)<br>
            LD C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Se C1 chegou em 3...)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Ligue a saída)
        </div>

        <h3>6. Instrução de Reset (RES)</h3>
        <p>Zera forçadamente o acumulador de Contadores (<code>C</code>) e Temporizadores (<code>T</code> / <code>RTO</code>):</p>
        <div class='code-box'>
            LD I0.1 &nbsp;&nbsp;&nbsp;&nbsp;(Se apertar o botão de Reset)<br>
            RES T1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Zera o cronômetro do RTO T1)<br>
            LD I0.2 &nbsp;&nbsp;&nbsp;&nbsp;(Se apertar botão de Zerar Peças)<br>
            RES C1 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Zera a contagem do contador C1)
        </div>

        <h3>7. Bobinas Latch -(L)- (Set) e Unlatch -(U)- (Reset)</h3>
        <p>Permitem ligar e manter travado com um pulso, sem precisar de circuito de selo:</p>
        <ul>
            <li><strong>S (Set / Latch):</strong> Bobina <code>-( L )-</code>. Liga e <strong>mantém ligado</strong> mesmo após soltar o botão.</li>
            <li><strong>R (Reset / Unlatch):</strong> Bobina <code>-( U )-</code>. Desliga forçadamente a variável.</li>
        </ul>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Botão Liga)<br>
            S Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Trava a saída Q0.0 ligada)<br>
            LD I0.1 &nbsp;&nbsp;&nbsp;&nbsp;(Botão Desliga)<br>
            R Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Destrava e desliga Q0.0)
        </div>

        <h3>8. Memória e Selo Clássico (Manter ligado)</h3>
        <p>Como fazer um botão de campainha virar um interruptor usando a lógica tradicional de relés? Usamos memória e ramal paralelo!</p>
        <div class='code-box'>
            LD I0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Botão Liga)<br>
            OR M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(OU a memória já ligada)<br>
            ANDN I0.1 &nbsp;&nbsp;(E o botão Desliga solto)<br>
            ST M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Salva na memória M0)<br>
            LD M0 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Lê a memória)<br>
            ST Q0.0 &nbsp;&nbsp;&nbsp;&nbsp;(Liga a luz real)
        </div>

        <br>
        <div class="footer">
            💻 <a href='https://github.com/Pedrofrac/Trabalho-Final-CLP/tree/main/examples/Batch'>Baixar Exemplos Batch</a> &nbsp;|&nbsp; 
            💻 <a href='https://github.com/Pedrofrac/Trabalho-Final-CLP/tree/main/examples/Traffic-light'>Baixar Exemplos Traffic light</a>
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