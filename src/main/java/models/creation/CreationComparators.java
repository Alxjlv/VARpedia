package models.creation;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class CreationComparators {

    public static final Comparator<Creation> TO_REVIEW = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            if (o1.getViewCount() == 0 && o2.getViewCount() == 0) {
                if (o1.getConfidenceRating() == o2.getConfidenceRating()) {
                    return Collator.getInstance(Locale.ENGLISH).compare(o1.getName(), o2.getName());
                } else {
                    return Integer.compare(o1.getConfidenceRating(), o2.getConfidenceRating());
                }
            } else if (o1.getViewCount() == 0) {
                return -1;
            } else if (o2.getViewCount() == 0) {
                return 1;
            }
            return Integer.compare(o1.getConfidenceRating(), o2.getConfidenceRating());
        }

        @Override
        public String toString() {
            return "To Review";
        }
    };

    public static final Comparator<Creation> NAME_A_TO_Z = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return Collator.getInstance(Locale.ENGLISH).compare(o1.getName(), o2.getName());
        }

        @Override
        public String toString() {
            return "Name (A-Z)";
        }
    };

    public static final Comparator<Creation> NAME_Z_TO_A = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return Collator.getInstance(Locale.ENGLISH).compare(o2.getName(), o1.getName());
        }

        @Override
        public String toString() {
            return "Name (Z-A)";
        }
    };

    public static final Comparator<Creation> LEAST_VIEWED = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return Integer.compare(o1.getViewCount(), o2.getViewCount());
        }

        @Override
        public String toString() {
            return "Least Viewed";
        }
    };

    public static final Comparator<Creation> MOST_VIEWED = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return Integer.compare(o2.getViewCount(), o1.getViewCount());
        }

        @Override
        public String toString() {
            return "Most Viewed";
        }
    };

    public static final Comparator<Creation> MOST_CONFIDENT = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return Integer.compare(o2.getConfidenceRating(), o1.getConfidenceRating());
        }

        @Override
        public String toString() {
            return "Most confident";
        }
    };

    public static final Comparator<Creation> LEAST_CONFIDENT = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return Integer.compare(o1.getConfidenceRating(), o2.getConfidenceRating());
        }

        @Override
        public String toString() {
            return "Least confident";
        }
    };

    public static final Comparator<Creation> NEWEST = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return o2.getDateCreated().compareTo(o1.getDateCreated());
        }

        @Override
        public String toString() {
            return "Newest";
        }
    };

    public static final Comparator<Creation> OLDEST = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return o1.getDateCreated().compareTo(o2.getDateCreated());
        }

        @Override
        public String toString() {
            return "Oldest";
        }
    };
}
