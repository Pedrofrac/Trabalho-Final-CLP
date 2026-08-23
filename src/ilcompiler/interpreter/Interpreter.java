package ilcompiler.interpreter;

import ilcompiler.memoryvariable.MemoryVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import screens.HomePg;
import screens.ladder.LadderCanvas;

// Classe que interpreta as instrucoes
public class Interpreter {

    // Cria variaveis
    static Boolean accumulator;
    static List<String> validOperators = new ArrayList<>();

    // Define operadores validos
    public static void initializeValidOperators() {
        validOperators.add("LD");
        validOperators.add("LDN");
        validOperators.add("ST");
        validOperators.add("STN");
        validOperators.add("AND");
        validOperators.add("ANDN");
        validOperators.add("OR");
        validOperators.add("ORN");
        validOperators.add("TON");
        validOperators.add("TOFF");
        validOperators.add("CTD");
        validOperators.add("CTU");
    }

    // Recebe linhas vindas da tela e separa operador e variavel
    public static Map receiveLines(List<String> lineList, Map<String, Boolean> inputs, Map<String, Boolean> outputs,
            Map<String, MemoryVariable> memoryVariables) {

        char character = '-';
        Boolean spaceDetected = false;
        String operator = "";
        String variable = "";
        ArrayList<String> variables = new ArrayList();
        Boolean justEmptyLines = true;

        initializeValidOperators();
        accumulator = null;

        for (int i = 0; i < lineList.size(); i++) {
            if (!lineList.get(i).isBlank()) {
                justEmptyLines = false;
                for (int j = 0; j < lineList.get(i).length(); j++) {
                    character = lineList.get(i).charAt(j);

                    if (character != ' ' && character != '\n' && character != '\t' && character != ','
                            && !spaceDetected) {
                        operator = operator + character;
                    }

                    if ((character == ' ' || character == '\t') && !operator.equals("")) {
                        spaceDetected = true;
                    }

                    if (character == ',' && !operator.equals("")) {
                        variables.add(variable);
                        variable = "";
                    }

                    if (character != ' ' && character != '\n' && character != '\t' && character != ','
                            && spaceDetected) {
                        variable = variable + character;
                    }
                }

                variables.add(variable);
                outputs = executeInstruction(operator, variables, inputs, outputs, memoryVariables);
            }

            spaceDetected = false;
            operator = "";
            variable = "";
            variables.clear();
        }

        if (justEmptyLines) {
            HomePg.showErrorMessage("Insira as intruções para o CLP!");
        }

        // Notifica o diagrama Ladder para animar o brilho verde em tempo real
        LadderCanvas.updateStateGlobal(inputs, outputs, memoryVariables);

        return outputs;
    }

    public static boolean operatorIsValid(String operator) {
        Boolean isValid = false;
        for (int i = 0; i < validOperators.size(); i++) {
            if (!isValid && validOperators.get(i).equals(operator)) {
                isValid = true;
            }
        }
        return isValid;
    }

    public static String getMemoryType(String variable) {
        String type = "";
        String code = "";
        int cod = -1;
        
        for (int i = 0; i < variable.length(); i++) {
            if (variable.charAt(i) >= '0' && variable.charAt(i) <= '9') {
                code = code + variable.charAt(i);
            } else {
                type = type + variable.charAt(i);
            }
        }

        try {
            cod = Integer.parseInt(code);
        } catch (Exception E) {}

        if (!type.equals("M") && !type.equals("T") && !type.equals("C")) {
            HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variable + " não existe!");
            return "";
        } else if (cod != -1) {
            return type;
        } else {
            HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variable + " não existe!");
            return "";
        }
    }

    public static boolean inputIsValid(ArrayList<String> variables, Map<String, Boolean> inputs) {
        Boolean isValid = true;
        if (inputs.get(variables.get(0)) == null) {
            isValid = false;
        }
        return isValid;
    }

    public static boolean outputIsValid(ArrayList<String> variables, Map<String, Boolean> outputs) {
        Boolean isValid = true;
        if (outputs.get(variables.get(0)) == null) {
            isValid = false;
        }
        return isValid;
    }

    public static boolean memoryVariableIsValid(ArrayList<String> variables,
            Map<String, MemoryVariable> memoryVariables) {
        Boolean isValid = true;
        if (memoryVariables.get(variables.get(0)) == null) {
            isValid = false;
        }
        return isValid;
    }

    public static Map executeInstruction(String operator, ArrayList<String> variables, Map<String, Boolean> inputs,
            Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        
        if (operatorIsValid(operator) && (inputIsValid(variables, inputs) || outputIsValid(variables, outputs))) {

            if (operator.equals("LD")) {
                if (variables.get(0).charAt(0) == 'I') accumulator = inputs.get(variables.get(0));
                if (variables.get(0).charAt(0) == 'Q') accumulator = outputs.get(variables.get(0));
            }

            if (operator.equals("LDN")) {
                if (variables.get(0).charAt(0) == 'I') accumulator = !(inputs.get(variables.get(0)));
                if (variables.get(0).charAt(0) == 'Q') accumulator = !(outputs.get(variables.get(0)));
            }

            if (accumulator != null) {
                if (operator.equals("ST") || operator.equals("STN")) {
                    if (outputIsValid(variables, outputs)) {
                        if (operator.equals("ST")) {
                            if (variables.get(0).charAt(0) == 'Q') outputs.put(variables.get(0), accumulator);
                        }
                        if (operator.equals("STN")) {
                            if (variables.get(0).charAt(0) == 'Q') outputs.put(variables.get(0), !accumulator);
                        }
                    } else {
                        HomePg.showErrorMessage(
                                "Entradas não podem ser modificadas, portanto, operadores ST e STN não são válidos para entradas!");
                    }
                }

                if (operator.equals("AND")) {
                    if (variables.get(0).charAt(0) == 'I') accumulator = (accumulator && inputs.get(variables.get(0)));
                    if (variables.get(0).charAt(0) == 'Q') accumulator = (accumulator && outputs.get(variables.get(0)));
                }

                if (operator.equals("ANDN")) {
                    if (variables.get(0).charAt(0) == 'I') accumulator = (accumulator && !(inputs.get(variables.get(0))));
                    if (variables.get(0).charAt(0) == 'Q') accumulator = (accumulator && !(outputs.get(variables.get(0))));
                }

                if (operator.equals("OR")) {
                    if (variables.get(0).charAt(0) == 'I') accumulator = (accumulator || inputs.get(variables.get(0)));
                    if (variables.get(0).charAt(0) == 'Q') accumulator = (accumulator || outputs.get(variables.get(0)));
                }

                if (operator.equals("ORN")) {
                    if (variables.get(0).charAt(0) == 'I') accumulator = (accumulator || !(inputs.get(variables.get(0))));
                    if (variables.get(0).charAt(0) == 'Q') accumulator = (accumulator || !(outputs.get(variables.get(0))));
                }
            } else {
                HomePg.showErrorMessage(
                        "Acumulador vazio! Carregue inicialmente a variável desejada para o acumulador com as funções LD ou LDN!");
            }

        } else if (operatorIsValid(operator) && !inputIsValid(variables, inputs)
                && !outputIsValid(variables, outputs)) {
            if (operator.equals("ST") || operator.equals("STN") || operator.equals("TON") || operator.equals("TOFF")
                    || operator.equals("CTD") || operator.equals("CTU")) {
                String type = getMemoryType(variables.get(0));
                if (!type.equals("")) {
                    if (memoryVariableIsValid(variables, memoryVariables)) {
                        MemoryVariable memVar = memoryVariables.get(variables.get(0));
                        
                        if (operator.equals("ST")) {
                            if (type.equals("C")) {
                                memVar.testEndTimer();
                                if (!memVar.currentValue && accumulator) {
                                    if (memVar.counterType.equals("UP")) memVar.incrementCounter();
                                    else if (memVar.counterType.equals("DOWN")) memVar.decrementCounter();
                                }
                            }
                            memVar.currentValue = accumulator;
                        }

                        if (operator.equals("STN")) {
                            if (type.equals("C")) {
                                memVar.testEndTimer();
                                if (memVar.currentValue && !accumulator) {
                                    if (memVar.counterType.equals("UP")) memVar.incrementCounter();
                                    else if (memVar.counterType.equals("DOWN")) memVar.decrementCounter();
                                }
                            }
                            memVar.currentValue = !accumulator;
                        }

                        if (operator.equals("TON") && type.equals("T")) {
                            memVar.maxTimer = Integer.parseInt(variables.get(1));
                            memVar.timerType = "ON";
                        } else if (operator.equals("TON")) {
                             HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variables.get(0) + " invalido!");
                        }

                        if (operator.equals("TOFF") && type.equals("T")) {
                            memVar.maxTimer = Integer.parseInt(variables.get(1));
                            memVar.timerType = "OFF";
                        } else if (operator.equals("TOFF")) {
                             HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variables.get(0) + " invalido!");
                        }

                        if (operator.equals("CTD") && type.equals("C")) {
                            memVar.maxTimer = Integer.parseInt(variables.get(1));
                            memVar.counterType = "DOWN";
                        } else if (operator.equals("CTD")) {
                             HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variables.get(0) + " invalido!");
                        }

                        if (operator.equals("CTU") && type.equals("C")) {
                            memVar.maxTimer = Integer.parseInt(variables.get(1));
                            memVar.counterType = "UP";
                        } else if (operator.equals("CTU")) {
                             HomePg.showErrorMessage("Sintaxe incorreta! Espaço de memória " + variables.get(0) + " invalido!");
                        }
                    } else {
                        if (operator.equals("ST") || operator.equals("STN")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            if (operator.equals("ST")) newMem.currentValue = accumulator;
                            else newMem.currentValue = !accumulator;
                            memoryVariables.put(variables.get(0), newMem);
                        }

                        if (operator.equals("TON") && type.equals("T")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            newMem.maxTimer = Integer.parseInt(variables.get(1));
                            newMem.timerType = "ON";
                            memoryVariables.put(variables.get(0), newMem);
                        } 

                        if (operator.equals("TOFF") && type.equals("T")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            newMem.maxTimer = Integer.parseInt(variables.get(1));
                            newMem.timerType = "OFF";
                            memoryVariables.put(variables.get(0), newMem);
                        }

                        if (operator.equals("CTD") && type.equals("C")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            newMem.maxTimer = Integer.parseInt(variables.get(1));
                            newMem.counterType = "DOWN";
                            memoryVariables.put(variables.get(0), newMem);
                        }

                        if (operator.equals("CTU") && type.equals("C")) {
                            MemoryVariable newMem = new MemoryVariable(variables.get(0));
                            newMem.maxTimer = Integer.parseInt(variables.get(1));
                            newMem.counterType = "UP";
                            memoryVariables.put(variables.get(0), newMem);
                        }
                    }
                }
            } else {
                if (memoryVariables.get(variables.get(0)) == null) {
                    String typeToCheck = getMemoryType(variables.get(0));
                    if (!typeToCheck.equals("")) {
                        memoryVariables.put(variables.get(0), new MemoryVariable(variables.get(0)));
                    }
                }

                if (memoryVariableIsValid(variables, memoryVariables)) {
                    MemoryVariable memVar = memoryVariables.get(variables.get(0));
                    boolean isTimerOrCounter = variables.get(0).charAt(0) == 'T' || variables.get(0).charAt(0) == 'C';
                    boolean val = isTimerOrCounter ? memVar.endTimer : memVar.currentValue;

                    if (operator.equals("LD"))   accumulator = val;
                    if (operator.equals("LDN"))  accumulator = !val;
                    if (operator.equals("AND"))  accumulator = accumulator && val;
                    if (operator.equals("ANDN")) accumulator = accumulator && !val;
                    if (operator.equals("OR"))   accumulator = accumulator || val;
                    if (operator.equals("ORN"))  accumulator = accumulator || !val;
                } else {
                    HomePg.showErrorMessage("Sintaxe incorreta! Variável " + variables.get(0) + " não existe!");
                }
            }
        } else {
            HomePg.showErrorMessage("Sintaxe incorreta! Operador " + operator + " não existe!");
        }

        return outputs;
    }
}
