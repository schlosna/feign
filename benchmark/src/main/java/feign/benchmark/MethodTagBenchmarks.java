package feign.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 10, time = 1)
@Fork(3)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class MethodTagBenchmarks {

    @Benchmark
    public String logBasic_concat() {
        return logBasic(concatLogger);
    }

    @Benchmark
    public String logBasic_builder() {
        return logBasic(builderLogger);
    }

    private static String logBasic(Logger logger) {
        return logger.toLog("Github#contributors(",
                "---> %s %s HTTP/1.1",
                "GET",
                "https://api.github.com/repos/openfeign/feign");
    }

    interface Logger {
        String toLog(String configKey, String format, Object... args);
    }

    private static final Logger concatLogger = new Logger() {
        @Override
        public String toLog(String configKey, String format, Object... args) {
            String methodTag = new StringBuilder().append('[')
                    .append(configKey.substring(0, configKey.indexOf('(')))
                    .append("] ")
                    .toString();
            return String.format(methodTag + format, args);
        }
    };

    private static final Logger builderLogger = new Logger() {
        @Override
        public String toLog(String configKey, String format, Object... args) {
            StringBuilder methodTag = new StringBuilder().append('[')
                    .append(configKey.substring(0, configKey.indexOf('(')))
                    .append("] ");
            return String.format(methodTag.append(format).toString(), args);
        }
    };

}

