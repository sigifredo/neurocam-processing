
public class Range {

    private float start;
    private float end;

    public Range() {
        this.start = 0.0f;
        this.end = 0.0f;
    }

    public void expand(float number) {
        if (number < this.start) {
            this.start = number;
        } else if (number > this.end) {
            this.end = number;
        }

        if (this.start > this.end) {
            this.start += this.end;
            this.end = this.start - this.end;
            this.start -= this.end;
        }
    }

    public float getStart() {
        return this.start;
    }

    public float getEnd() {
        return this.end;
    }

    public static float map(float n, Range in, Range out) {
        return map(n, in.start, in.end, out.start, out.end);
    }

    public static float map(float n, float inStart, float inEnd, float outStart, float outEnd) {
        return outStart + (n - inStart) * (outEnd - outStart) / (inEnd - inStart);
    }
}
