
import processing.core.PApplet;
import processing.video.*;

public class VideoPlayer implements MeditationObserver {

    private boolean debug;
    private boolean muted;
    private PApplet parent;
    private Range signalInfo;
    private Movie[] videos;

    public VideoPlayer(PApplet parent) {
        this.parent = parent;
        this.debug = false;
        this.muted = false;
        this.signalInfo = new Range();
        this.videos = new Movie[5];

        new Thread(() -> {
            for (int i = 0; i < videos.length; i++) {
                String videoName = "v" + String.valueOf(i + 1) + ".mp4";

                try {
                    this.videos[i] = new Movie(this.parent, videoName);
                    this.videos[i].volume(0);
                    this.videos[i].loop();
                } catch (Exception e) {
                    parent.println("Ha ocurrido un error leyendo el archivo de video \"" + videoName + "\"" + e.getMessage());
                }
            }
        }).start();
    }

    public void draw() {
        float d = signalInfo.getEnd() - signalInfo.getStart();
        float zoneSize = (signalInfo.getEnd() - signalInfo.getStart()) / 4;
        int zoneIndex = -1;
        Range[] zones = new Range[4];

        for (Movie video : this.videos) {
            if (video.available()) {
                video.read();
            }
        }

        if (d > 4.0f) {
            for (int i = 0; i < 4; i++) {
                Range zone = new Range()
                        .setValue(signalInfo.getStart() + i * zoneSize)
                        .setValue(signalInfo.getStart() + (i + 1) * zoneSize);

                zones[i] = zone;

                if (zoneIndex < 0) {
                    if (signalInfo.getValue() >= zone.getStart() && signalInfo.getValue() <= zone.getEnd()) {
                        zoneIndex = i;
                    }
                }
            }

            if (zoneIndex < 0 || zoneIndex >= this.videos.length) {
                parent.image(this.videos[0], 0, 0, parent.width, parent.height);
            } else {
                int idx1 = zoneIndex;
                int idx2 = zoneIndex + 1;
                int alpha = (int) signalInfo.map(0.0f, 255.0f);

                if (idx2 >= this.videos.length) {
                    idx2 = 0;
                }

                parent.tint(255, 255);
                parent.image(this.videos[idx1], 0, 0, parent.width, parent.height);
                parent.tint(255, alpha);
                parent.image(this.videos[idx2], 0, 0, parent.width, parent.height);

                if (this.debug) {
                    parent.fill(0, 200);
                    parent.rect(0, 0, parent.width, 30);
                    parent.fill(255);
                    parent.text(String.valueOf(idx1) + " - " + String.valueOf(idx2) + ": " + String.valueOf(alpha), 10, 20);
                    parent.text(this.signalInfo.toString(), parent.width / 2, 20);
                }
            }
        } else {
            parent.image(this.videos[0], 0, 0, parent.width, parent.height);
        }
    }

    @Override
    public void onEvent(Range meditationInfo) {
        this.signalInfo.set(meditationInfo);
    }

    public boolean isDebugging() {
        return this.debug;
    }

    public boolean isMuted() {
        return this.muted;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;

        for (Movie video : this.videos) {
            if (this.muted) {
                video.volume(0);
            } else {
                video.volume(100);
            }
        }
    }
}
