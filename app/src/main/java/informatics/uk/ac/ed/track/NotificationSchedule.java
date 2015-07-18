package informatics.uk.ac.ed.track;

public enum NotificationSchedule {
    RANDOM,
    FIXED;

    public static int toInt(NotificationSchedule schedule) {
        switch (schedule) {
            case RANDOM:
                return 0;
            case FIXED:
                return 1;
            default:
                return -1;
        }
    }

    public static NotificationSchedule fromInt(int scheduleInt) {
        switch (scheduleInt) {
            case 0:
                return RANDOM;
            case 1:
                return FIXED;
            default:
                throw new IllegalArgumentException("Invalid integer.");
        }
    }
}
