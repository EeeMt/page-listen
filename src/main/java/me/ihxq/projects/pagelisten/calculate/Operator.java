package me.ihxq.projects.pagelisten.calculate;

import java.util.Arrays;
import java.util.Optional;

/**
 * The comparator to be used for detect change.
 *
 * @author xq.h
 * 2020/4/19 13:26
 **/
public enum Operator {
    eq("equals to") {
        @Override
        public boolean apply(String detect, String target) {
            return target != null && target.equals(detect);
        }
    },
    diff("different from") {
        @Override
        public boolean apply(String detect, String target) {
            return target != null && !target.equals(detect);
        }
    },
    lt("less than") {
        @Override
        public boolean apply(String detect, String target) {
            return detect != null && detect.compareTo(target) < 0;
        }
    },
    gt("greate than") {
        @Override
        public boolean apply(String detect, String target) {
            return detect != null && detect.compareTo(target) > 0;
        }
    };

    public final String description;

    Operator(String description) {
        this.description = description;
    }

    public static Optional<Operator> of(String opt) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(opt))
                .findAny();
    }

    protected abstract boolean apply(String detect, String target);

    public OperateResult operate(String detect, String target) {
        boolean hit = this.apply(detect, target);
        return new OperateResult(hit, this.resultDesc(hit, detect, target));
    }

    private String resultDesc(boolean hit, String detect, String target) {
        return String.format("Detected is %s, target is %s, detect %s %s target.",
                detect, target, hit ? "is" : "isn't", this.description);
    }
}
