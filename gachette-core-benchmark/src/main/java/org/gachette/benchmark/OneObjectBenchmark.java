package org.gachette.benchmark;

import com.google.caliper.Benchmark;
import com.google.caliper.Param;
import org.gachette.DefaultGachetteFactory;
import org.gachette.Gachette;
import org.gachette.GachetteFactory;
import org.gachette.benchmark.model.Node;

public class OneObjectBenchmark extends Benchmark {

    @Param({"5"})
    private int depth;
    @Param({"5"})
    private int fanout;


    private Node node;
    private Node gachetteNode;

    private GachetteFactory factory = new DefaultGachetteFactory();
    private Gachette gachette;

    @Override
    protected void setUp() {
        node = buildTree(10, depth, fanout, false);

        gachette = factory.create();
        gachetteNode = node = buildTree(10, depth, fanout, true);
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

    private Node buildTree(long value, int depth, int fanout, boolean isGachette) {
        Node root = null;
        if (depth > 0) {
            if (isGachette) {
                root = gachette.object(new Node());
            } else {
                root = new Node();
            }
            root.setValue(value);
        }

        if (depth > 1) {
            for (int i = 0; i < fanout; i++) {
                root.addChild(buildTree(value, depth - 1, fanout, isGachette));
            }
        }

        return root;
    }
}
