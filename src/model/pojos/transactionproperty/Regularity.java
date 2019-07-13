package model.pojos.transactionproperty;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Regularity {

    private final int amount;
    private final Unit unit;

    public Regularity() {
        this(0, Unit.FOREVER);
    }

    public Regularity(int amount, Unit unit) {
        if (unit == Unit.FOREVER) {
            this.amount = 0;
        } else {
            this.amount = amount;
        }

        if (amount == 0) {
            this.unit = Unit.FOREVER;
        } else {
            this.unit = unit;
        }
    }

    public int getAmount() {
        return amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public Duration getDuration() {
        try {
            return unit.getDuration().multipliedBy(amount);
        } catch (ArithmeticException e) {
            return Duration.ofSeconds(Long.MAX_VALUE);
        }
    }

    @Override
    public String toString() {
        return "" + amount + " " + unit;
    }

    public static enum Unit {

        FOREVER("<Nincs ismétlődés>", ChronoUnit.FOREVER),
        DAYS("Nap", ChronoUnit.DAYS),
        WEEKS("Hét", ChronoUnit.WEEKS),
        MONTHS("Hónap", ChronoUnit.MONTHS),
        YEARS("Év", ChronoUnit.YEARS);

        private final String nameHun;
        private final ChronoUnit chronoUnit;

        private Unit(String nameHun, ChronoUnit chronoUnit) {
            this.nameHun = nameHun;
            this.chronoUnit = chronoUnit;
        }

        public String getNameHun() {
            return nameHun;
        }

        @Override
        public String toString() {
            return nameHun;
        }

        public Duration getDuration() {
            return chronoUnit.getDuration();
        }
    }
}
