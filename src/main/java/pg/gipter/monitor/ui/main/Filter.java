package pg.gipter.monitor.ui.main;

public enum Filter {
    DIFF("Diff could not be produced"),
    REPOSITORIES("For given repositories"),
    UNAUTHORIZED("Unauthorized");

    private final String value;
    Filter(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
