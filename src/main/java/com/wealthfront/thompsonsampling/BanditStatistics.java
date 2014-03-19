package com.wealthfront.thompsonsampling;

import cern.jet.random.engine.RandomEngine;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import java.util.List;

public class BanditStatistics {
    private final List<Double> armWeights;
    private final Optional<Integer> victoriousArm;

    public List<Double> getArmWeights() {
        return armWeights;
    }

    public Optional<Integer> getVictoriousArm() {
        return victoriousArm;
    }

    public BanditStatistics(List<Double> armWeights, Optional<Integer> victoriousArm) {
        this.armWeights = armWeights;
        this.victoriousArm = victoriousArm;
    }

    public int pickArm(RandomEngine engine) {
        double p = engine.nextFloat();
        double total = 0;
        for (int i = 0; i < armWeights.size(); i++) {
            total += armWeights.get(i);
            if (p < total) {
                return i;
            }
        }
        return armWeights.size() - 1;
    }

    @Override
    public String toString() {
        String weights = "weights: (" + Joiner.on(", ").join(getArmWeights()) + ")";
        String winningArm = "unknown";
        if (getVictoriousArm().isPresent()) {
            winningArm += getVictoriousArm().get();
        }
        return weights + ", winner: " + winningArm;
    }
}
