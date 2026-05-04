package org.tudublin.bonsaiapp.util;

import android.widget.TextView;

import org.tudublin.bonsaiapp.R;

public final class DifficultyUtils {

    private DifficultyUtils() {}

    public static int chipBackground(String difficulty) {
        if (difficulty == null) return R.drawable.bg_chip_easy;
        String normalized = difficulty.trim().toLowerCase();
        if (normalized.contains("expert") || normalized.contains("hard")) return R.drawable.bg_chip_hard;
        if (normalized.contains("intermediate") || normalized.contains("medium")) return R.drawable.bg_chip_medium;
        return R.drawable.bg_chip_easy;
    }

    public static int chipTextColor(String difficulty) {
        if (difficulty == null) return R.color.chip_easy_fg;
        String normalized = difficulty.trim().toLowerCase();
        if (normalized.contains("expert") || normalized.contains("hard")) return R.color.chip_hard_fg;
        if (normalized.contains("intermediate") || normalized.contains("medium")) return R.color.chip_medium_fg;
        return R.color.chip_easy_fg;
    }

    public static void applyTo(TextView view, String difficulty) {
        view.setBackgroundResource(chipBackground(difficulty));
        view.setTextColor(view.getContext().getColor(chipTextColor(difficulty)));
    }
}
