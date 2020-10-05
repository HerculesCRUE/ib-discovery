package data;

import java.util.HashMap;
import java.util.Map;

public class Stats {
    private int n;
    private double mean;
    private float min;
    private float max;
    private Map<Float,Integer> nRanges;

    public Stats() {
        resetStats();
    }

    public void resetStats(){
        nRanges = new HashMap<>();
        for (float f = 0.0f ; f <= 1.01f ; f+=0.1f)
            nRanges.put(round(f,1),0);
        mean = .0;
        min = Float.MAX_VALUE;
        max = Float.MIN_VALUE;
    }

    public void addValue(float value) {
        float rounded = round(value,1);
        nRanges.put(rounded,nRanges.get(rounded)+1);
        mean = ((n*mean)+value)/++n;
        if (value<min)
            min = value;
        if (value>max)
            max = value;
    }

    public static float round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(value * scale) / scale;
    }

    public int getN() {
        return n;
    }

    public double getMean() {
        return mean;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getRatioForRange(float threshold) {
        float t = round(threshold,1);
        int total = 0;
        int upperThreshold = 0;
        for (Map.Entry<Float, Integer> e : nRanges.entrySet()) {
            total += e.getValue();
            if (round(e.getKey(),1)>=threshold)
                upperThreshold += e.getValue();
        }
        return Float.valueOf(upperThreshold)/Float.valueOf(total);
    }

    @Override
    public String toString() {
        return "Stats{" +
                "n=" + n +
                ", mean=" + mean +
                ", min=" + min +
                ", max=" + max +
                ", nRanges=" + nRanges +
                '}';
    }
}
