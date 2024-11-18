
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import processing.core.PApplet;
import processing.serial.*;

public class SerialHandler {

    private boolean debug;
    private eegPort eeg;
    private Range meditationInfo;
    private List<MeditationObserver> meditationObservers;
    private PApplet parent;
    private String portName;
    private String portNames[];
    private Serial serialPort;

    public SerialHandler(PApplet parent) {
        this.parent = parent;

        this.meditationInfo = new Range();
        this.debug = false;
        this.eeg = null;
        this.meditationObservers = new ArrayList<>();
        this.portName = "";
        this.portNames = Serial.list();
        this.serialPort = null;
    }

    public void addMeditationObserver(MeditationObserver mo) {
        this.meditationObservers.add(mo);
    }

    public boolean connect2port(int index) {
        if (index >= 0 && index < portNames.length) {
            try {
                this.portName = this.portNames[index];
                this.serialPort = new Serial(parent, this.portName, 115200);
                eeg = new eegPort(parent, this.serialPort);
                eeg.refresh();

                return true;
            } catch (Exception e) {
                this.portName = "";
                this.portNames = Serial.list();

                this.eeg = null;
                this.serialPort = null;

                parent.println("No se ha podido conectar el puerto \"" + e.getMessage() + "\"");

                return false;
            }
        } else {
            return false;
        }
    }

    public void draw() {
        int lastEventInterval = parent.millis() - eeg.lastEvent;

        if (parent.mousePressed) {
            eeg.refresh();
        }

        parent.background(255);

        this.meditationInfo.setValue(eeg.meditation);
        this.notifyMeditationObservers(this.meditationInfo);

        if (this.debug) {
            drawAttention();
            drawCharAttention();
            drawCharMeditation();
            drawMeditation();
            drawPoorSignal(lastEventInterval);
            drawWavesData(lastEventInterval);
        }
    }

    public void readData(int inByte) {
        eeg.serialByte(inByte);
    }

    private void drawAttention() {
        parent.noFill();
        parent.stroke(0);
        parent.ellipse(400, 90, 127, 127);
        parent.fill(204, 102, 0);
        parent.noStroke();
        parent.ellipse(400, 90, eeg.attention, eeg.attention);
    }

    private void drawCharAttention() {
        /*
        // chart attention
        int attentionCount = eeg.attentionBuffer.size();
        skip = 0;

        if (attentionCount > 200) {
            skip = attentionCount - 200;
        }

        Iterator<Integer> attentionIterator = eeg.attentionBuffer.iterator();

        // we are interested in the last 200 observations
        i = -1;
        j = 0;
        prevValue = 0;
        x = 0;
        y = 0;
        prevX = 0;
        prevY = 0;

        stroke(204, 102, 0);

        // we are drawing between 0 and 800 in width, and between 400 and 600 in height
        while (attentionIterator.hasNext()) {
            int attention = attentionIterator.next();

            if (++i < skip) {
                continue;
            }

            x = j * 4;
            y = (int) (580 - 200.0 * attention / 255);

            if (j > 0) {
                line(prevX, prevY, x, y);
            }

            prevValue = attention;
            prevX = x;
            prevY = y;
            j++;
        }
         */
    }

    private void drawCharMeditation() {
        /*
        // chart meditation
        int meditationCount = eeg.meditationBuffer.size();

        skip = 0;
        if (meditationCount > 200) {
            skip = meditationCount - 200;
        }

        Iterator<Integer> meditationIterator = eeg.meditationBuffer.iterator();

        // we are interested in the last 200 observations
        i = -1;
        j = 0;
        prevValue = 0;
        x = 0;
        y = 0;
        prevX = 0;
        prevY = 0;

        stroke(108, 102, 240);

        // we are drawing between 0 and 800 in width, and between 400 and 600 in height
        while (meditationIterator.hasNext()) {
            int meditation = meditationIterator.next();
            if (++i < skip) {
                continue;
            }

            x = j * 4;
            y = (int) (580 - 200.0 * meditation / 255);
            if (j > 0) {
                line(prevX, prevY, x, y);
            }

            prevValue = meditation;
            prevX = x;
            prevY = y;
            j++;
        }
         */
    }

    private void drawMeditation() {
        parent.noFill();
        parent.stroke(0);
        parent.ellipse(600, 90, 127, 127);
        parent.fill(108, 102, 240);
        parent.noStroke();
        parent.ellipse(600, 90, eeg.meditation, eeg.meditation);
    }

    private void drawPoorSignal(int lastEventInterval) {
        parent.noStroke();

        if (eeg.poorSignal < 50 && lastEventInterval < 500) {
            // good signal
            parent.fill(0, 255, 0);
            parent.ellipse(150, 320, 100, 100);
        } else {
            // bad signal
            parent.fill(255, 0, 0);
            parent.ellipse(150, 320, 100, 100);
        }

        parent.textAlign(parent.CENTER);
        parent.fill(0);
        parent.text("Attention", 400, 20);
        parent.text("Meditation", 600, 20);

        if (eeg.lastAttention > 0) {
            parent.text(parent.millis() - eeg.lastAttention + " ms old", 400, 180);
        }

        if (eeg.lastMeditation > 0) {
            parent.text(parent.millis() - eeg.lastMeditation + " ms old", 600, 180);
        }
    }

    private void drawSignal() {
        // Chart vector values
        // first get maximum value
        int maxValue = 0;
        Iterator<eegPort.vectorObs> iterator;
        iterator = eeg.vectorBuffer.iterator();
        int vectorCount = eeg.vectorBuffer.size();

        int skip = 0;
        if (vectorCount > 200) {
            skip = vectorCount - 200;
        }

        int i = -1;

        while (iterator.hasNext()) {
            eegPort.vectorObs vobs = iterator.next();

            if (++i < skip) {
                continue;
            }

            if (vobs.vectorValue > maxValue) {
                maxValue = vobs.vectorValue;
            }
        }

        iterator = eeg.vectorBuffer.iterator();

        // we are interested in the last 400 observations
        i = -1;
        int j = 0;
        int prevValue = 0;
        int x = 0, y = 0;
        int prevX = 0, prevY = 0;

        parent.stroke(0);

        // we are drawing between 0 and 800 in width, and between 400 and 600 in height
        while (iterator.hasNext()) {
            eegPort.vectorObs vobs = iterator.next();

            if (++i < skip) {
                continue;
            }

            x = j * 4;
            y = (int) (580 - 200.0 * vobs.vectorValue / maxValue);

            if (j > 0) {
                parent.line(prevX, prevY, x, y);
            }

            prevValue = vobs.vectorValue;
            prevX = x;
            prevY = y;
            j++;
        }
    }

    private void drawWavesData(int lastEventInterval) {
        parent.textAlign(parent.LEFT);
        parent.fill(0);
        parent.text("Port: " + portName, 5, 20);
        parent.text("Dongle state: " + eeg.portDongleState, 5, 40);
        parent.text("Poor signal: " + eeg.poorSignal, 5, 60);
        parent.text("Attention: " + eeg.attention, 5, 80);
        parent.text("Meditation: " + eeg.meditation, 5, 100);
        parent.text("Last event: " + lastEventInterval + " ms ago", 5, 120);
        parent.text("Raw buffer size: " + eeg.rawDataBuffer.size(), 5, 140);
        parent.text("Raw data sequence: " + eeg.rawSequence, 5, 160);
        parent.text("Vector sequence: " + eeg.vectorSequence, 5, 180);
        parent.text("Vector buffer size: " + eeg.vectorBuffer.size(), 5, 200);
        parent.text("Serial read state: " + eeg.portReadState, 5, 220);
        parent.text("Failed checksum count: " + eeg.failedChecksumCount, 5, 240);
        parent.text("Click mouse for a second to reset", 5, 260);
    }

    private void notifyMeditationObservers(Range meditationInfo) {
        for (MeditationObserver observer : this.meditationObservers) {
            observer.onEvent(meditationInfo);
        }
    }

    // Métodos accesores
    public String getPortName() {
        return this.portName;
    }

    public String[] getPortNames() {
        return this.portNames;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
