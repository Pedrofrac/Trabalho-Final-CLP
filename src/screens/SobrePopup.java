package screens;

import java.awt.Desktop;
import java.net.URI;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class SobrePopup {

    public static void mostrarSobre() {
        String htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #ffffff; color: #333; padding: 15px; font-size: 12px; }
                    
                    /* Títulos */
                    h2 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 8px; margin-top: 0; }
                    h3 { color: #e74c3c; margin-top: 18px; margin-bottom: 8px; font-size: 13px; font-weight: bold; }
                    
                    /* Caixas de Texto (Conceito) */
                    .concept { background-color: #f0f8ff; padding: 10px; border-radius: 5px; border-left: 5px solid #3498db; margin-bottom: 12px; color: #2c3e50; line-height: 1.4; }
                    
                    /* Listas de Nomes e Recursos */
                    ul { margin: 5px 0; padding-left: 22px; color: #444; }
                    li { margin-bottom: 4px; }
                    
                    /* Estilo dos Links */
                    .repo-container { margin-top: 5px; }
                    .repo-item { 
                        background-color: #f8f9fa; 
                        border: 1px solid #ddd; 
                        margin-bottom: 6px; 
                        padding: 8px; 
                        border-radius: 4px;
                    }
                    a { 
                        color: #0066cc; 
                        text-decoration: none; 
                        font-weight: bold; 
                        font-size: 12px;
                    }
                    .repo-desc { color: #777; font-size: 10px; margin-left: 5px; }

                    /* Rodapé */
                    .note { color: #888; font-style: italic; font-size: 10px; margin-top: 22px; border-top: 1px solid #eee; padding-top: 10px; text-align: center; }
                </style>
            </head>
            <body>
                <h2>ℹ️ Sobre o Projeto - Simulador de CLP</h2>
                
                <div class='concept'>
                    Ambiente integrado para ensino e simulação de <b>Controladores Lógicos Programáveis (CLP)</b>, com suporte bidirecional sincronizado entre <b>Diagrama Ladder Visual (LD)</b> e <b>Lista de Instruções (IL)</b>.
                </div>

                <h3>👨‍💻 Desenvolvedor da Versão Ladder (2025/2026)</h3>
                <ul>
                    <li><b>Pedro Franco de Camargo</b> <span style='color: #27ae60; font-weight: bold;'>(Motor Ladder, Drag & Drop, Continuidade Elétrica e Cronômetros Dinâmicos)</span></li>
                </ul>

                <h3>✨ Principais Recursos</h3>
                <ul>
                    <li><b>Diagrama Ladder Visual:</b> Montagem dinâmica via Drag & Drop com ramais paralelos e contatos NA/NF.</li>
                    <li><b>Condução de Energia em Tempo Real:</b> Linhas de energia que acendem em verde indicando a passagem de corrente.</li>
                    <li><b>Sincronização Bidirecional:</b> Conversão instantânea e automática entre Ladder e Lista de Instruções.</li>
                    <li><b>Temporizadores e Contadores:</b> Blocos TON, TOFF, CTU e CTD com display de contagem em tempo real.</li>
                    <li><b>Cenários Práticos:</b> Semáforo de trânsito inteligente e controle de processo (Batch).</li>
                </ul>

                <h3>📅 Histórico do Projeto (Versões Anteriores - Base IL)</h3>
                <ul>
                    <li><b>Colaboradores 2025:</b> Jamilly Moura,Pedro Franco de Camargo, Pedro Henrique Cândido Silva, </li>
                    <li><b>2024/02:</b> Diogo Nunes, José Arantes, Vinicius Barbosa, Yuri Duarte</li>
                    <li><b>Anteriores:</b> Bruno Rodrigues, Iasmin Pieraço, Igor Vendramini, Peterson, Vinicius Patrick, Emanuelle Oliveira</li>
                </ul>

                <h3>💻 Código Fonte (GitHub)</h3>
                <div class='repo-container'>
                    <!-- Repositório Atual -->
                    <div class='repo-item' style='background-color: #e8f4fd; border-color: #b6e0fe;'>
                        🚀 <a href='https://github.com/Pedrofrac/Trabalho-Final-CLP'>Acessar Repositório Atual (Pedrofrac)</a>
                        <span class='repo-desc'>(Versão com Diagrama Ladder e IL)</span>
                    </div>

                    <!-- Repositórios Antigos -->
                    <div class='repo-item'>
                        📂 <a href='https://github.com/PedroH2003/Trabalho-Final-CLP'>Repositório Base 2025</a>
                        <span class='repo-desc'>(Versão IL)</span>
                    </div>

                    <div class='repo-item'>
                        📂 <a href='https://github.com/Diogo-NB/SimuladorClp'>Repositório 2024/02</a>
                        <span class='repo-desc'>(Base anterior)</span>
                    </div>
                    
                    <div class='repo-item'>
                        📂 <a href='https://github.com/IasminPieraco/Trabalho-Final-CLP'>Repositório 2024/01</a>
                    </div>
                </div>

                <div class='note'>
                    "O sucesso é a soma de pequenos esforços repetidos dia após dia."
                    <br>Desenvolvido para fins acadêmicos e estudo de automação industrial.
                </div>
            </body>
            </html>
            """;

        JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);

        // Listener para cliques nos links
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        String url = e.getURL() != null ? e.getURL().toString() : e.getDescription();
                        if (url != null && !url.isEmpty()) {
                            Desktop.getDesktop().browse(new URI(url));
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, 
                            "Erro ao abrir o link no navegador: " + ex.getMessage(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new java.awt.Dimension(540, 580));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JOptionPane.showMessageDialog(null, scrollPane, "Sobre o Projeto", JOptionPane.PLAIN_MESSAGE);
    }
}