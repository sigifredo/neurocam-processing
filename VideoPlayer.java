
import processing.core.PApplet;
// import processing.core.PImage;
import processing.video.*;

public class VideoPlayer {

    private Movie[] videos;
    private boolean loading;
    private PApplet parent;

    public VideoPlayer(PApplet parent) {
        this.parent = parent;
        this.videos = new Movie[5];
        this.loading = true;

        new Thread(() -> {
            for (int i = 0; i < videos.length; i++) {
                try {
                    this.videos[i] = new Movie(this, "v" + String.valueOf(i) + ".mp4");
                    this.videos[i].loop();
                } catch (Exception e) {
                    parent.println(e.getMessage());
                }
            }

            this.loading = false;
        }).start();
    }

    private int findZoneAndScale(Range thetaRange, int theta) {

        return 0;
        /*
def find_zone_and_scale(number, r):
    zone_size = (r.end - r.start) / 4
    zones = [(r.start + i * zone_size, r.start + (i + 1) * zone_size)
             for i in range(4)]

    zone_index = -1

    for i, (zone_start, zone_end) in enumerate(zones):
        if zone_start <= number < zone_end:
            zone_index = i
            break

    if zone_index == -1:
        return {
            'zone_index': 0,
            'scale_position': 50.0,
            'zone_range': zones[0]
        }

    zone_start, zone_end = zones[zone_index]
    scale_position = ((number - zone_start) / (zone_end - zone_start)) * 100

    return {
        'zone_index': zone_index,
        'scale_position': scale_position,
        'zone_range': zones[zone_index]
    }
         */
    }

    public void showVideo(int theta) {

        for (Movie video : this.videos) {
            if (video.available()) {
                video.read();
            }
        }

        int zones = findZoneAndScale(new Range(), theta);

        /*
    while True:
        ret = True

        for idx, v in enumerate(videos):
            r, frame = v.read()

            if r:
                frames[idx] = cv2.resize(frame, canvas_size)
            else:
                ret = False
                break

        if ret:
            idx1 = 0
            idx2 = 1
            alpha = 0.5
            beta = 0.5

            if current_theta != None and theta.start != None and theta.end != None:
                zones = find_zone_and_scale(current_theta, theta)
                idx1 = zones['zone_index']
                idx2 = zones['zone_index'] + 1
                alpha = zones['scale_position'] / 100.0
                beta = 1.0 - alpha

            combined_frame = cv2.addWeighted(
                frames[idx1],
                alpha,
                frames[idx2],
                beta,
                0
            )

            cv2.imshow('Video', combined_frame)
         */
    }

}
