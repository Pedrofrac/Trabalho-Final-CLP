package ilcompiler.interpreter;

import ilcompiler.memoryvariable.MemoryVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import screens.HomePg;
import screens.ladder.LadderCanvas;

public class Interpreter {

    static Boolean accumulator;
    static List<String> validOperators = new ArrayList<>();

    public static void initializeValidOperators() {
        validOperators.clear();
        validOperators.add("LD");
        validOperators.add("LDN");
        validOperators.add("ST");
        validOperators.add("STN");
        validOperators.add("S");
        validOperators.add("R");
        validOperators.add("SET");
        validOperators.add("RST");
        validOperators.add("OTL");
        validOperators.add("OTU");
        validOperators.add("AND");
        validOperators.add("ANDN");
        validOperators.add("OR");
        validOperators.add("ORN");
        validOperators.add("TON");
        validOperators.add("TOFF");
        validOperators.add("RTO");
        validOperators.add("CTD");
        validOperators.add("CTU");
        validOperators.add("RES");
    }

    public static Map receiveLines(List<String> lineList, Map<String, Boolean> inputs, Map<String, Boolean> outputs,
            Map<String, MemoryVariable> memoryVariables) {

        char character = '-';
        Boolean spaceDetected = false;
        String operator = "";
        String variable = "";
        ArrayList<String> variables = new ArrayList<>();
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

        // Animação em tempo real do diagrama visual
        LadderCanvas.updateStateGlobal(inputs, outputs, memoryVariables);

        return outputs;
    }

    public static boolean operatorIsValid(String operator) {
        for (String valid : validOperators) {
            if (valid.equalsIgnoreCase(operator)) {
                return true;
            }
        }
        return false;
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
        } catch (Exception ignored) {}

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
        return !variables.isEmpty() && inputs.containsKey(variables.get(0));
    }

    public static boolean outputIsValid(ArrayList<String> variables, Map<String, Boolean> outputs) {
        return !variables.isEmpty() && outputs.containsKey(variables.get(0));
    }

    public static boolean memoryVariableIsValid(ArrayList<String> variables,
            Map<String, MemoryVariable> memoryVariables) {
        return !variables.isEmpty() && memoryVariables.containsKey(variables.get(0));
    }

    public static Map executeInstruction(String operator, ArrayList<String> variables, Map<String, Boolean> inputs,
            Map<String, Boolean> outputs, Map<String, MemoryVariable> memoryVariables) {
        
        operator = operator.toUpperCase();

        // 1. Instrução de Reset Explicito (RES) - Só executa se a linha estiver conduzindo (accumulator == true)
        if (operator.equals("RES")) {
            String target = variables.get(0).toUpperCase();
            if (Boolean.TRUE.equals(accumulator)) {
                if (target.startsWith("T") || target.startsWith("C")) {
                    if (memoryVariables.containsKey(target)) {
                        MemoryVariable mv = memoryVariables.get(target);
                        mv.reset();
                    }
                } else if (target.startsWith("Q") && outputs.containsKey(target)) {
                    outputs.put(target, false);
                } else if (target.startsWith("M") && memoryVariables.containsKey(target)) {
                    memoryVariables.get(target).currentValue = false;
                }
            }
            return outputs;
        }

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
                // Bobina Normal e Invertida
                if (operator.equals("ST") || operator.equals("STN")) {
                    if (outputIsValid(variables, outputs)) {
                        if (operator.equals("ST")) {
                            if (variables.get(0).charAt(0) == 'Q') outputs.put(variables.get(0), accumulator);
                        }
                        if (operator.equals("STN")) {
                            if (variables.get(0).charAt(0) == 'Q') outputs.put(variables.get(0), !accumulator);
                        }
                    } else {
                        HomePg.showErrorMessage("Entradas não podem ser modificadas com ST/STN!");
                    }
                }

                // Bobinas Latch (Set) e Unlatch (Reset) para Saídas Físicas (Q)
                if (operator.equals("S") || operator.equals("SET") || operator.equals("OTL")) {
                    if (outputIsValid(variables, outputs)) {
                        if (accumulator) outputs.put(variables.get(0), true);
                    }
                }

                if (operator.equals("R") || operator.equals("RST") || operator.equals("OTU")) {
                    if (outputIsValid(variables, outputs)) {
                        if (accumulator) outputs.put(variables.get(0), false);
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
                HomePg.showErrorMessage("Acumulador vazio! Carregue inicialmente a variável com LD ou LDN!");
            }

        } else if (operatorIsValid(operator) && !inputIsValid(variables, inputs) && !outputIsValid(variables, outputs)) {
            
            if (operator.equals("ST") || operator.equals("STN") || operator.equals("S") || operator.equals("SET")
                    || operator.equals("OTL") || operator.equals("R") || operator.equals("RST") || operator.equals("OTU")
                    || operator.equals("TON") || operator.equals("TOFF") || operator.equals("RTO")
                    || operator.equals("CTD") || operator.equals("CTU")) {
                
                String type = getMemoryType(variables.get(0));
                if (!type.equals("")) {
                    if (!memoryVariableIsValid(variables, memoryVariables)) {
                        memoryVariables.put(variables.get(0), new MemoryVariable(variables.get(0)));
                    }

                    MemoryVariable memVar = memoryVariables.get(variables.get(0));

                    if (operator.equals("ST")) {
                        if (type.equals("C")) {
                            memVar.testEndTimer();
                            if (!memVar.currentValue && Boolean.TRUE.equals(accumulator)) {
                                if (memVar.counterType.equals("UP")) memVar.incrementCounter();
                                else if (memVar.counterType.equals("DOWN")) memVar.decrementCounter();
                            }
                        }
                        memVar.currentValue = accumulator;
                    }

                    if (operator.equals("STN")) {
                        if (type.equals("C")) {
                            memVar.testEndTimer();
                            if (memVar.currentValue && Boolean.FALSE.equals(accumulator)) {
                                if (memVar.counterType.equals("UP")) memVar.incrementCounter();
                                else if (memVar.counterType.equals("DOWN")) memVar.decrementCounter();
                            }
                        }
                        memVar.currentValue = !accumulator;
                    }

                    // Set / Latch em Memória Booleana (M)
                    if (operator.equals("S") || operator.equals("SET") || operator.equals("OTL")) {
                        if (Boolean.TRUE.equals(accumulator)) memVar.currentValue = true;
                    }

                    // Reset / Unlatch em Memória Booleana (M)
                    if (operator.equals("R") || operator.equals("RST") || operator.equals("OTU")) {
                        if (Boolean.TRUE.equals(accumulator)) memVar.currentValue = false;
                    }

                    if (operator.equals("TON") && type.equals("T")) {
                        memVar.maxTimer = Integer.parseInt(variables.get(1));
                        memVar.timerType = "ON";
                    }

                    if (operator.equals("TOFF") && type.equals("T")) {
                        memVar.maxTimer = Integer.parseInt(variables.get(1));
                        memVar.timerType = "OFF";
                    }

                    if (operator.equals("RTO") && type.equals("T")) {
                        memVar.maxTimer = Integer.parseInt(variables.get(1));
                        memVar.timerType = "RTO";
                    }

                    if (operator.equals("CTD") && type.equals("C")) {
                        memVar.maxTimer = Integer.parseInt(variables.get(1));
                        memVar.counterType = "DOWN";
                    }

                    if (operator.equals("CTU") && type.equals("C")) {
                        memVar.maxTimer = Integer.parseInt(variables.get(1));
                        memVar.counterType = "UP";
                    }
                }
            } else {
                if (!memoryVariables.containsKey(variables.get(0))) {
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
