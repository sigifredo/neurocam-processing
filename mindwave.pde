
import processing.serial.*;
import processing.video.*;

public enum AppState {
    APP_SERIAL_SELECT,
    APP_CONNECTING,
    APP_CONNECTED,
}

public class SerialSelector {
    public final static int FONT_SIZE = 16;
    public final static int SERIAL_UI_ROW_SIZE = 25;
    public int index;
    public int mouseYHoverPosition;

    public SerialSelector() {
        index = -1;
        mouseYHoverPosition = -1;
    }
}

AppState appState = AppState.APP_SERIAL_SELECT;
SerialHandler serialHandler;
SerialSelector serialSelector;
VideoPlayer videoPlayer;

void setup() {
    size(800, 600);
    serialHandler = new SerialHandler(this);
    serialSelector = new SerialSelector();
    videoPlayer = new VideoPlayer(this);

    serialHandler.addMeditationObserver(videoPlayer);

    try {
        PFont font = createFont("resources/UbuntuMono-Regular.ttf", SerialSelector.FONT_SIZE);
        textFont(font);
    } catch (Exception e) {
        println("Ha ocurrido un error: " + e.getMessage());
    }

    smooth();
}

void draw() {
    switch (appState) {
        case APP_SERIAL_SELECT:
            drawSelectPort();
            break;
        case APP_CONNECTING:
            drawConnecting();
            break;
        case APP_CONNECTED:
            serialHandler.draw();
            videoPlayer.draw();
            break;
    }
}

void drawConnecting() {
    background(255);
    text("Connecting, please wait...", 5, 20);
}

void drawSelectPort() {
    background(255);

    int hover = 0;
    int selected = 0;

    if (mousePressed) {
        hover = serialSelector.mouseYHoverPosition;
        selected = calculeSerialPosition(mouseY);
    } else {
        hover = mouseY;
        selected = -1;
    }

    hover = calculeSerialPosition(hover);

    for (int i = 0; i < serialHandler.getPortNames().length; i++) {
        if (i == hover) {
            if (selected == hover) {
                serialSelector.index = i;
                fill(240, 200, 200);
            } else {
                fill(200, 200, 240);
            }

            noStroke();
            rect(0, i * SerialSelector.SERIAL_UI_ROW_SIZE, width, SerialSelector.SERIAL_UI_ROW_SIZE);
            fill(0);
        } else {
            fill(0);
        }

        text(serialHandler.getPortNames()[i], 5, (i + 1) * SerialSelector.SERIAL_UI_ROW_SIZE - SerialSelector.FONT_SIZE / 2);
    }
}

void mousePressed() {
    if (appState == AppState.APP_SERIAL_SELECT) {
        serialSelector.mouseYHoverPosition = mouseY;
    }
}

void mouseReleased() {
    if (appState == AppState.APP_SERIAL_SELECT) {
        int hover = calculeSerialPosition(serialSelector.mouseYHoverPosition);
        int selected = calculeSerialPosition(mouseY);

        if (hover == selected) {
            appState = AppState.APP_CONNECTING;
            drawConnecting();

            if (serialHandler.connect2port(serialSelector.index)) {
                appState = AppState.APP_CONNECTED;
            } else {
                appState = AppState.APP_SERIAL_SELECT;
            }
        }

        serialSelector.mouseYHoverPosition = -1;
    }
}

int calculeSerialPosition(int relativePosition) {
    return (int) Math.round(Math.floor(relativePosition / SerialSelector.SERIAL_UI_ROW_SIZE));
}

void serialEvent(Serial port) {
    while (port.available() > 0) {
        int inByte = port.read();
        serialHandler.readData(inByte);

        if (inByte == 170 && appState != AppState.APP_CONNECTED) {
            println("Connected");
            appState = AppState.APP_CONNECTED;
            frameRate(10);
        }
    }
}

void keyPressed() {
    if (key == 'd' || key == 'D') {
        videoPlayer.setDebug(!videoPlayer.isDebugging());
    } else if (key == 's' || key == 'S') {
        videoPlayer.setMuted(!videoPlayer.isMuted());
    }
}
