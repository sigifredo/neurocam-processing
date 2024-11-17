
import processing.core.PApplet;
import processing.serial.*;

public class SerialHandler {

    private eegPort eeg;
    private Serial serialPort;
    private String portName;
    private String portNames[];
    private PApplet parent;

    public SerialHandler(PApplet parent) {
        this.parent = parent;

        portName = "";
        portNames = Serial.list();

        eeg = null;
        serialPort = null;
        // showWavesData = false;
        // showSignal = false;
    }

    public void draw() {
        int lastEventInterval = parent.millis() - eeg.lastEvent;

        if (parent.mousePressed) {
            eeg.refresh();
        }

        parent.background(255);

        /*
        drawWavesData(lastEventInterval);
        drawPoorSignal(lastEventInterval);
        drawAttention();
        drawMeditation();
        drawCharAttention();
        drawCharMeditation();
         */
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

    public void readData(int inByte) {
        eeg.serialByte(inByte);
    }

    /*
    public void serialSelectUI() {
        parent.background(255);

        int hover = (int) Math.round(Math.floor(parent.mouseY / 20));
        int selected = (parent.mousePressed) ? hover : -1;

        for (int i = 0; i < portNames.length; i++) {
            if (i == selected) {
                portName = portNames[i];
                serialPort = new Serial(this, portName, 115200);
                eeg = new eegPort(this, serialPort);
                eeg.refresh();

                parent.fill(0);
                parent.rect(0, i * 20, parent.width, 20);
                parent.fill(255);

                parent.println("selected " + portName);
                appState = APP_CONNECTING;
                delay(500);
            } else if (i == hover) {
                fill(200, 200, 240);
                noStroke();
                rect(0, i * 20, width, 20);
                fill(0);
            } else {
                fill(0);
            }

            text(portNames[i], 5, (i + 1) * 20);
        }
    }

    private void drawAttention() {
        // Draw attention
        noFill();
        stroke(0);
        ellipse(400, 90, 127, 127);
        fill(204, 102, 0);
        noStroke();
        ellipse(400, 90, eeg.attention, eeg.attention);
    }

    private void drawCharAttention() {
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
    }

    private void drawCharMeditation() {
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
    }

    private void drawMeditation() {
        // Draw meditation
        noFill();
        stroke(0);
        ellipse(600, 90, 127, 127);
        fill(108, 102, 240);
        noStroke();
        ellipse(600, 90, eeg.meditation, eeg.meditation);
    }

    private void drawPoorSignal(int lastEventInterval) {
        noStroke();

        if (eeg.poorSignal < 50 && lastEventInterval < 500) {
            // good signal
            fill(0, 255, 0);
            ellipse(150, 320, 100, 100);
        } else {
            // bad signal
            fill(255, 0, 0);
            ellipse(150, 320, 100, 100);
        }

        textAlign(CENTER);
        fill(0);
        text("Attention", 400, 20);
        text("Meditation", 600, 20);

        if (eeg.lastAttention > 0) {
            text(millis() - eeg.lastAttention + " ms old", 400, 180);
        }

        if (eeg.lastMeditation > 0) {
            text(millis() - eeg.lastMeditation + " ms old", 600, 180);
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

        stroke(0);

        // we are drawing between 0 and 800 in width, and between 400 and 600 in height
        while (iterator.hasNext()) {
            eegPort.vectorObs vobs = iterator.next();

            if (++i < skip) {
                continue;
            }

            x = j * 4;
            y = (int) (580 - 200.0 * vobs.vectorValue / maxValue);

            if (j > 0) {
                line(prevX, prevY, x, y);
            }

            prevValue = vobs.vectorValue;
            prevX = x;
            prevY = y;
            j++;
        }
    }

    private void drawWavesData(int lastEventInterval) {
        textAlign(LEFT);
        fill(0);
        text("Port: " + portName, 5, 20);
        text("Dongle state: " + eeg.portDongleState, 5, 40);
        text("Poor signal: " + eeg.poorSignal, 5, 60);
        text("Attention: " + eeg.attention, 5, 80);
        text("Meditation: " + eeg.meditation, 5, 100);
        text("Last event: " + lastEventInterval + " ms ago", 5, 120);
        text("Raw buffer size: " + eeg.rawDataBuffer.size(), 5, 140);
        text("Raw data sequence: " + eeg.rawSequence, 5, 160);
        text("Vector sequence: " + eeg.vectorSequence, 5, 180);
        text("Vector buffer size: " + eeg.vectorBuffer.size(), 5, 200);
        text("Serial read state: " + eeg.portReadState, 5, 220);
        text("Failed checksum count: " + eeg.failedChecksumCount, 5, 240);
        text("Click mouse for a second to reset", 5, 260);
    }
     */
    // Métodos accesores
    public String getPortName() {
        return this.portName;
    }

    public String[] getPortNames() {
        return this.portNames;
    }
}
