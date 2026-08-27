# 🤖 Simulador de CLP com Diagrama Ladder Visual & Lista de Instruções (IL)

[![Java](https://img.shields.io/badge/Java-21%2B%20%7C%20Swing-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Completo%20%26%20Funcional-brightgreen)]()
[![GitHub Repo](https://img.shields.io/badge/GitHub-Pedrofrac%2FTrabalho--Final--CLP-181717?logo=github)](https://github.com/Pedrofrac/Trabalho-Final-CLP)

📚 **Disciplina:** Controladores Lógicos Programáveis (CLP)  
🎓 **Curso:** Engenharia de Computação  
🏫 **Instituição:** IFTM - Instituto Federal do Triângulo Mineiro  
📍 **Campus:** Uberaba - Parque Tecnológico  
👨‍🏫 **Professor:** Robson Rodrigues  

---

## 🌟 Novidades da Versão Atual (Diagrama Ladder + IL)

Esta versão introduz um **motor gráfico completo de Diagrama Ladder (LD)** com renderização e animação de energia em tempo real, mantendo total sincronização bidirecional com a Lista de Instruções (IL).

### ⚡ Motor Gráfico de Diagrama Ladder (LD)
* **Interface Drag & Drop Intuitiva com Paleta em Abas:** Arraste e solte componentes de uma barra superior organizada por categorias temáticas (*Bit / Lógica*, *Timer / Counter* e *Controle*).
* **Condução de Energia em Tempo Real (Live Wire Animation):** Animação dinâmica das linhas de força. Fios e componentes acendem em **Verde brilhante (`1`)** quando energizados e voltam a **Branco (`0`)** quando desligados.
* **Sincronização Bidirecional Automática:** Alterne entre a aba do **Diagrama Ladder** e a aba de **Lista de Instruções (IL)** com compilação e carregamento instantâneo em tempo real.
* **Bobinas Latch -(L)- e Unlatch -(U)- (Set / Reset):** Suporte nativo à retenção lógica permanente com apenas um pulso, simplificando lógicas biestáveis sem a necessidade de circuitos de selo tradicionais.
* **Temporizador Retentivo (RTO):** Bloco temporizador com memória que **congela o tempo acumulado** caso o sinal de entrada seja interrompido, retomando a contagem de onde parou ao ser reenergizado.
* **Instrução de Reset Dedicado (RES):** Bloco funcional para zeramento atômico e forçado de temporizadores (`T` / `RTO`) e contadores (`C` / `CTU` / `CTD`).
* **Ramais Paralelos Inteligentes (Lógica OR):** Suporte a bifurcações e fechamentos verticais com continuidade elétrica precisa e dissolução automática de ramais vazios.
* **Temporizadores e Contadores com Display ao Vivo:** Blocos `TON`, `TOFF`, `RTO`, `CTU` e `CTD` desenham o valor acumulado (`Acc:`) e a meta (`Pre:`) em tempo real dentro da própria caixinha no circuito.
* **Portas Inversoras / NOT (NF):** Contatos Normalmente Fechados operam com lógica visual de condução invertida (acesos em repouso e abertos quando acionados).
* **Contagem Precisa na Borda de Descida:** Contadores (`CTU` e `CTD`) operam com suporte a contagem crescente e decrescente.
* **Barramentos de Alimentação (24V e 0V):** Identificação visual com barras vermelhas verticais no início (fase) e terminação delimitadora no fim de cada linha.
* **Painel Padrão com Rótulos de Tags e Proporções Calibradas:** Identificação visual nominal (`I0.0` a `I0.7` e `Q0.0` a `Q0.7`) centralizadas no topo de cada chave e LED com proporções vetoriais ajustadas.
* **Manual Didático Integrado (Help):** Guia rápido reescrito com exemplos práticos, sintaxe e explicações detalhadas sobre todas as instruções disponíveis.

---

## 👥 Autoria e Desenvolvimento

### 👨‍💻 Desenvolvedor da Versão Ladder (Atual)
* **Pedro Franco de Camargo** — *Desenvolvimento do Motor Gráfico Ladder, Continuidade Elétrica, Drag & Drop, Cronômetros Dinâmicos, Temporizadores Retentivos (RTO), Bobinas Latch/Unlatch (Set/Reset), Instrução RES, Paleta em Abas Categóricas e Sincronização Bidirecional.*

---

### 📅 Histórico de Membros e Versões Anteriores (Base IL e Cenários)
* **Colaboradores 2025 (Base IL / Cenário Semáforo):** Jamilly Moura, Pedro Franco de Camargo, Pedro Henrique Cândido Silva
* **Membros 2024/02:** Diogo Nunes, José Arantes, Vinicius Barbosa, Yuri Duarte
* **Membros Anteriores:** Bruno Rodrigues, Iasmin Pieraço, Igor Vendramini, Peterson, Vinicius Patrick, Emanuelle Oliveira

*(O projeto é fruto de uma evolução acadêmica contínua no curso de Engenharia de Computação do IFTM).*

---

## 🛠️ Recursos e Funcionalidades

### 📝 Linguagens e Instruções Suportadas (IEC 61131-3)

| Tipo | Mnemônico IL | Elemento Ladder Visual | Descrição |
| :--- | :---: | :---: | :--- |
| **Entrada Direta** | `LD` / `AND` | `—[ ]—` | Contato Normalmente Aberto (NA) |
| **Entrada Invertida** | `LDN` / `ANDN` | `—[/]—` | Contato Normalmente Fechado (NF / NOT) |
| **Paralelo (OR)** | `OR` / `ORN` | `—[+]—` / Ramal | Ramo paralelo de condução alternada |
| **Saída Normal** | `ST` | `—( )—` | Bobina de Saída Direta |
| **Saída Invertida** | `STN` | `—(/)—` | Bobina de Saída Negada |
| **Bobina Latch (Set)** | `S` / `SET` | `—( L )—` | Trava a saída/memória ligada em 1 permanentemente |
| **Bobina Unlatch (Reset)** | `R` / `RST` | `—( U )—` | Destrava e desliga a saída/memória para 0 |
| **Temporizador TON** | `TON T1, Pre` | `[ TON ]` | Temporizador com Retardo na Ligação (Zera ao perder sinal) |
| **Temporizador TOFF** | `TOFF T2, Pre` | `[ TOFF ]` | Temporizador com Retardo no Desligamento |
| **Temporizador RTO** | `RTO T1, Pre` | `[ RTO ]` | Temporizador Retentivo (Congela o acumulador ao perder sinal) |
| **Contador CTU** | `CTU C1, Pre` | `[ CTU ]` | Contador Crescente |
| **Contador CTD** | `CTD C1, Pre` | `[ CTD ]` | Contador Decrescente |
| **Instrução de Reset** | `RES Tag` | `[ RES ]` | Zera forçadamente temporizadores (T/RTO) e contadores (C) |

### 🏷️ Endereçamento de Memória
* **Entradas Físicas:** `I0.0` a `I1.7` (Botoeiras, Sensores e Chaves)
* **Saídas Físicas:** `Q0.0` a `Q1.7` (Lâmpadas, Motores, Atuadores e Sinalizadores)
* **Temporizadores:** `T1` a `T10` (com base de tempo configurável e modo retentivo opcional)
* **Contadores:** `C1` a `C10` (com contagem acumulada, meta e reset via software)
* **Memórias Auxiliares:** `M0`, `M1`, `M2`... (Flags internas com suporte a escrita direta e Set/Reset)

---

## 🎮 Cenários de Simulação Interativos

1. **Painel Padrão (Default Panel):** Chaves com retenção e botões pulsadores com etiquetas nominais centralizadas (`I0.0` a `I0.7`) e LEDs indicadores (`Q0.0` a `Q0.7`) para validação rápida de lógica booleana e sequencial.
2. **Semáforo Inteligente (Traffic Light):** Cruzamento urbano completo com semáforos para veículos (Norte-Sul e Leste-Oeste), semáforos de pedestres com botoeira e sensores indutivos no asfalto com física de colisão.
3. **Controle de Processo em Batelada (Batch Simulation):** Simulação industrial com tanques, válvulas de entrada/saída, bombas (`Pump 1`, `Pump 2`, `Pump 3`), aquecedor, misturador e sensores de nível alto/baixo.

---

## 🕹️ Modos de Operação

* 🛠️ **Modo Edição (Program):** Edição livre do código IL e montagem visual do Diagrama Ladder com marcadores `[+]` de inserção e paleta em abas.
* ▶️ **Modo Execução (Run / Play):** Ciclo de scan contínuo (100ms), fios condutores com animação de corrente e atualização das saídas e cenários.
* ⏸️ **Modo Pausa / Parada (Stop / Pause):** Congela o estado para inspeção ou reseta contadores, temporizadores e saídas para edição segura.
* 🔄 **Reiniciar (Refresh):** Reseta as variáveis lógicas, memória do CLP e reinicia o cenário visual para o repouso.
* 📊 **Tabela de Dados (Data Table):** Janela flutuante para monitoramento em tempo real do estado de todas as entradas, saídas e memórias.

---

## ▶️ Como Executar o Projeto

### Opção 1: Executável Direto (Windows)
1. Certifique-se de ter o **Java (JDK/JRE 21 ou superior)** instalado.
2. Execute o instalador `Instalador_Simulador_CLP.exe` ou rode o arquivo `rodar.bat`.

### Opção 2: Pelo Terminal / Linha de Comando
```bash
# Clone o repositório
git clone https://github.com/Pedrofrac/Trabalho-Final-CLP.git

# Acesse a pasta do projeto
cd Trabalho-Final-CLP

# Execute pelo script batch
.\rodar.bat
```

---

## 📚 Referências e Créditos

* **Norma IEC 61131-3:** Padrão internacional para linguagens de programação de Controladores Lógicos Programáveis.
* **Inspiração Visual:** Software *LogixPro 500 Simulator* e *Siemens TIA Portal*.
* **Repositório do Projeto:** [GitHub - Pedrofrac/Trabalho-Final-CLP](https://github.com/Pedrofrac/Trabalho-Final-CLP)