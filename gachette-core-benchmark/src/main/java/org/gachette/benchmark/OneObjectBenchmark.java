package org.gachette.benchmark;

import com.google.caliper.Benchmark;
import org.gachette.DefaultGachetteFactory;
import org.gachette.Gachette;
import org.gachette.GachetteFactory;
import org.gachette.benchmark.model.Node;

public class OneObjectBenchmark extends Benchmark {

    private Node node;
    private Node gachetteNode;

    private GachetteFactory factory = new DefaultGachetteFactory();
    private Gachette gachette;

    @Override
    protected void setUp() {
        node = new Node();
        node.setValue(10);

        gachette = factory.create();
        gachetteNode = gachette.object(new Node());
        gachetteNode.setValue(10);
    }

    public long timeWithoutGachette(long reps) {
        long result = 0;
        for (long i = 0; i < reps; i++) {
            result += node.getValue();
        }

        return result;
    }

    public long timeWithGachette(long reps) {
        long result = 0;
        for (long i = 0; i < reps; i++) {
            result += gachetteNode.getValue();
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        BenchmarkMain.main(OneObjectBenchmark.class, args);
    }
}
