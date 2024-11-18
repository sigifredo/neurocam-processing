
import processing.core.PApplet;
import processing.video.*;

public class VideoPlayer implements MeditationObserver {

    private boolean loading;
    private PApplet parent;
    private Range signalInfo;
    private Movie[] videos;

    public VideoPlayer(PApplet parent) {
        this.parent = parent;
        this.loading = true;
        this.signalInfo = new Range();
        this.videos = new Movie[5];

        new Thread(() -> {
            for (int i = 0; i < videos.length; i++) {
                try {
                    this.videos[i] = new Movie(this.parent, "v" + String.valueOf(i) + ".mp4");
                    this.videos[i].loop();
                } catch (Exception e) {
                    parent.println(e.getMessage());
                }
            }

            this.loading = false;
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

        if (d > 1.0f) {
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
                int alpha = (int) parent.map(signalInfo.getValue(), zones[zoneIndex].getStart(), zones[zoneIndex].getEnd(), 0.0f, 255.0f);

                if (idx2 >= this.videos.length) {
                    idx2 = 0;
                }

                parent.image(this.videos[idx1], 0, 0, parent.width, parent.height);
                parent.tint(255, alpha);
                parent.image(this.videos[idx2], 0, 0, parent.width, parent.height);
            }
        } else {
            parent.image(this.videos[0], 0, 0, parent.width, parent.height);
        }
    }

    @Override
    public void onEvent(Range meditationInfo) {
        this.signalInfo.set(meditationInfo);
    }
}
