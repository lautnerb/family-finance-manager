package model.pojos.transaction.externaltransaction;

import java.time.LocalDate;
import model.pojos.transactionproperty.Category;
import model.pojos.transactionproperty.Regularity;

public class Expense extends ExternalTransaction {

    public Expense() {
        super();
    }

    public Expense(int id, String name, int amount, LocalDate date, Category category, Regularity regularity, String description) {
        super(id, name, amount, date, category, regularity, description);
    }

    @Override
    public int getSignedAmount() {
        return -getAmount();
    }
}
