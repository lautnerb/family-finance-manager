package model.pojos;

import model.pojos.transactionproperty.Regularity;
import model.pojos.transactionproperty.Category;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.pojos.transaction.Transaction;

public class FilterConditions {

    private final List<Transaction.Type> transactionTypes = new ArrayList<>();
    private Category category;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Regularity minRegularity;
    private Regularity maxRegularity;

    public List<Transaction.Type> getTransactionTypes() {
        return transactionTypes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Regularity getMinRegularity() {
        return minRegularity;
    }

    public void setMinRegularity(Regularity minRegularity) {
        this.minRegularity = minRegularity;
    }

    public Regularity getMaxRegularity() {
        return maxRegularity;
    }

    public void setMaxRegularity(Regularity maxRegularity) {
        this.maxRegularity = maxRegularity;
    }
}
