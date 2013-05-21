package org.gachette.benchmark;

import com.google.caliper.runner.CaliperMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BenchmarkMain {

    public static void main(java.lang.Class<? extends com.google.caliper.Benchmark> benchmarkClass,
                            java.lang.String[] args) {
        List<String> argList = new ArrayList<>();
        argList.add("--run-name");
        argList.add(benchmarkClass.getName());
        argList.addAll(Arrays.asList(args));

        CaliperMain.main(benchmarkClass, argList.toArray(new String[]{}));
    }


}
