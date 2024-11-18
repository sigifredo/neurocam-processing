
public class Range {

    private float value;
    private float start;
    private float end;

    public Range() {
        this.value = 0.0f;
        this.start = 0.0f;
        this.end = 0.0f;
    }

    public Range setValue(float value) {
        this.value = value;

        if (value < this.start) {
            this.start = value;
        } else if (value > this.end) {
            this.end = value;
        }

        if (this.start > this.end) {
            this.start += this.end;
            this.end = this.start - this.end;
            this.start -= this.end;
        }

        return this;
    }

    public float getValue() {
        return this.value;
    }

    public float getStart() {
        return this.start;
    }

    public float getEnd() {
        return this.end;
    }

    public float map(float start, float end) {
        return start + (this.value - this.start) * (end - start) / (this.end - this.start);
    }

    public static float map(float n, Range in, Range out) {
        return map(n, in.start, in.end, out.start, out.end);
    }

    public static float map(float n, float inStart, float inEnd, float outStart, float outEnd) {
        return outStart + (n - inStart) * (outEnd - outStart) / (inEnd - inStart);
    }

    public void set(Range other) {
        this.value = other.value;
        this.start = other.start;
        this.end = other.end;
    }

    @Override
    public String toString() {
        return "[" + this.start + ", " + this.end + "]: " + this.value;
    }
}
