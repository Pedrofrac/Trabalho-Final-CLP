package ilcompiler.memoryvariable;

import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class MemoryVariable {

    public String id;
    public Boolean currentValue;
    public Boolean endTimer;
    public int counter;
    public int maxTimer;
    public Timer timer;
    public String timerType;   // "ON", "OFF", "RTO"
    public String counterType; // "UP", "DOWN"

    public MemoryVariable(String id) {
        this.id = id;
        this.counter = 0;
        this.currentValue = false;
        this.maxTimer = 0;
        this.endTimer = false;
        this.timerType = "";
        this.counterType = "";

        this.timer = new Timer(100, (ActionEvent evt) -> {
            if (counter < maxTimer) {
                counter++;
            }
            if (counter >= maxTimer) {
                if (timerType.equals("ON") || timerType.equals("RTO")) {
                    endTimer = true;
                } else if (timerType.equals("OFF")) {
                    endTimer = false;
                }
                timer.stop();
            }
        });
    }

    public void reset() {
        this.counter = 0;
        this.endTimer = false;
        if (this.timer != null) {
            this.timer.stop();
        }
        if (this.id.startsWith("M")) {
            this.currentValue = false;
        }
    }

    public String getMemory() {
        switch (id.charAt(0)) {
            case 'M' -> {
                return "Boolean memory: " + id + ", State:" + currentValue;
            }
            case 'T' -> {
                return switch (timerType) {
                    case "ON" ->
                        "Timer On memory: " + id + ", State:" + currentValue + ", Accum:" + counter + ", Preset:" + maxTimer + ", DN:" + endTimer;
                    case "OFF" ->
                        "Timer Off memory: " + id + ", State:" + currentValue + ", Accum:" + counter + ", Preset:" + maxTimer + ", DN:" + endTimer;
                    case "RTO" ->
                        "Retentive Timer On memory: " + id + ", State:" + currentValue + ", Accum:" + counter + ", Preset:" + maxTimer + ", DN:" + endTimer;
                    default ->
                        "Timer type error";
                };
            }
            case 'C' -> {
                return switch (counterType) {
                    case "UP" ->
                        "Counter Up: " + id + ", Accum:" + counter + ", Preset:" + maxTimer + ", DN:" + endTimer;
                    case "DOWN" ->
                        "Counter Down: " + id + ", Accum:" + counter + ", Preset:" + maxTimer + ", DN:" + endTimer;
                    default ->
                        "Counter type error";
                };
            }
            default -> {
            }
        }
        return "Memory type error";
    }

    public String getTimerType() {
        return timerType;
    }

    public String getTimer() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDN() {
        return this.endTimer;
    }

    public Boolean getEN() {
        return this.currentValue;
    }

    public int getAccum() {
        return this.counter;
    }

    public int getPreset() {
        return this.maxTimer;
    }

    public Boolean getEndCounter() {
        return this.endTimer;
    }

    public Boolean getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Boolean currentValue) {
        this.currentValue = currentValue;
    }

    public void resetCurrentValue() {
        this.counter = 0;
    }

    public void incrementCounter() {
        this.counter++;
        testEndTimer();
    }

    public void testEndTimer() {
        if (this.counterType.equals("UP")) {
            endTimer = this.counter >= this.maxTimer;
        } else if (this.counterType.equals("DOWN")) {
            endTimer = this.counter <= this.maxTimer;
        }
    }

    public void decrementCounter() {
        this.counter--;
        testEndTimer();
    }

    @Override
    public String toString() {
        return "MemoryVariable{" + "id=" + id + ", currentValue=" + currentValue + '}';
    }
}
