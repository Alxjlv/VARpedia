package models.creation;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * A collection of static {@link Comparator}'s for {@link Creation}'s.
 * @author Tait & Alex
 */
public class CreationComparators {

    /**
     * Sorts {@link Creation}'s based on the priority for a user to review
     */
    public static final Comparator<Creation> TO_REVIEW = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            if (o1.getViewCount() == 0 && o2.getViewCount() == 0) {
                return NAME_A_TO_Z.compare(o1, o2);
            } else if (o1.getViewCount() == 0) {
                return -1;
            } else if (o2.getViewCount() == 0) {
                return 1;
            }

            if (o1.getConfidenceRating() < o2.getConfidenceRating()) {
                return -1;
            } else if (o1.getConfidenceRating() > o2.getConfidenceRating()) {
                return 1;
            } else {
                if (o1.getViewCount() < o2.getViewCount()) {
                    return -1;
                } else if (o1.getViewCount() > o2.getViewCount()) {
                     return 1;
                } else {
                    return o1.getDateLastViewed().compareTo(o2.getDateLastViewed());
                }
            }
        }

        @Override
        public String toString() {
            return "To Review";
        }
    };

    /**
     * Sorts {@link Creation}'s by name A-to-Z
     */
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

    /**
     * Sorts {@link Creation}'s by name Z-to-A
     */
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

    /**
     * Sorts {@link Creation}'s by least viewed
     */
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

    /**
     * Sorts {@link Creation}'s by most viewed
     */
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

    /**
     * Sorts {@link Creation}'s by most confident
     */
    public static final Comparator<Creation> MOST_CONFIDENT = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return Integer.compare(o2.getConfidenceRating(), o1.getConfidenceRating());
        }

        @Override
        public String toString() {
            return "Most Confident";
        }
    };

    /**
     * Sorts {@link Creation}'s by lest confident
     */
    public static final Comparator<Creation> LEAST_CONFIDENT = new Comparator<Creation>() {
        @Override
        public int compare(Creation o1, Creation o2) {
            return Integer.compare(o1.getConfidenceRating(), o2.getConfidenceRating());
        }

        @Override
        public String toString() {
            return "Least Confident";
        }
    };

    /**
     * Sorts {@link Creation}'s by newest
     */
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

    /**
     * Sorts {@link Creation}'s by oldest
     */
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
