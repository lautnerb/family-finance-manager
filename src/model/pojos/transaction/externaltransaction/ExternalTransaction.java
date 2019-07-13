package model.pojos.transaction.externaltransaction;

import model.pojos.transaction.Transaction;
import java.time.LocalDate;
import java.util.Objects;
import model.pojos.transactionproperty.Category;
import model.pojos.transactionproperty.Regularity;

public abstract class ExternalTransaction extends Transaction {

    private int id;

    public ExternalTransaction() {
        super();
        this.id = 0;
    }

    public ExternalTransaction(int id, String name, int amount, LocalDate date, Category category, Regularity regularity, String description) {
        super(name, amount, date, category, regularity, description);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getName(),
                getAmount(),
                getDate(),
                getCategory().getId(),
                getRegularity());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }
}
