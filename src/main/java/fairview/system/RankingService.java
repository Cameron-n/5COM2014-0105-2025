package main.java.fairview.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import main.java.fairview.talks.TalkSubmission;

public class RankingService {

    private final Random random = new Random();

    public List<TalkSubmission> rankTalks(List<TalkSubmission> submissions, int slots) {

        List<TalkSubmission> sorted = new ArrayList<>(submissions);

        //sort by average score descending
        sorted.sort(Comparator.comparingDouble(TalkSubmission::getAverageScore).reversed());

        //handle ties with random shuffle
        int i = 0;
        while (i < sorted.size()) {
            double score = sorted.get(i).getAverageScore();

            int j = i + 1;
            while (j < sorted.size() && sorted.get(j).getAverageScore() == score) {
                j++;
            }

            if (j - i > 1) {
                Collections.shuffle(sorted.subList(i, j), random);
            }

            i = j;
        }

        // Return top N
        return sorted.subList(0, Math.min(slots, sorted.size()));
    }
}