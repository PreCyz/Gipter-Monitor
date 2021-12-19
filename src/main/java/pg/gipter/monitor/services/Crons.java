package pg.gipter.monitor.services;

public enum Crons {
    ONCE_PER_DAY_15("0 0 15 ? * MON,TUE,WED,THU,FRI"),
    EVERY_SIX_HOURS("0 0 */6 ? * MON,TUE,WED,THU,FRI"),
    EVERY_TEN_SECONDS("0/10 * * ? * MON,TUE,WED,THU,FRI,SAT"),
    ;

    final String expression;

    Crons(String expression) {
        this.expression = expression;
    }

    public String expression() {
        return expression;
    }
}
