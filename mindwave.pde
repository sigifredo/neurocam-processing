// Sample sketch to illustrate MindWave support in Processing

// for graphing
import processing.serial.*;

SerialHandler serialHandler;
PFont font;

// Application state
final int APP_SERIAL_SELECT = 1;
final int APP_CONNECTING = 2;
final int APP_CONNECTED = 3;

int appState = APP_SERIAL_SELECT;

void setup() {
    size(800, 600);
    font = loadFont("HelveticaNeue-20.vlw");
    textFont(font);

    serialHandler = new SerialHandler();

    smooth();
}

void draw() {
    switch (appState) {
        case APP_SERIAL_SELECT:
            serialHandler.serialSelectUI();
            break;
        case APP_CONNECTING:
            drawConnecting();
            break;
        case APP_CONNECTED:
            drawConnected();
            break;
    }
}

// Drawing when we're connected
void drawConnected() {
    int lastEventInterval = millis() - eeg.lastEvent;

    serialHandler.draw();
}

void drawConnecting() {
    background(255);
    text("Connecting to " + portName + ", please wait…", 5, 20);
}

void serialEvent(Serial port) {
    while (port.available() > 0) {
        int inByte = port.read();
        serialHandler.readData(inByte);

        if (inByte == 170 && appState < APP_CONNECTED) {
            println("Connected");
            appState = APP_CONNECTED;
            frameRate(10);
        }
    }
}
