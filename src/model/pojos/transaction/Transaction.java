package model.pojos.transaction;

import java.time.LocalDate;
import model.pojos.transactionproperty.Category;
import model.pojos.transactionproperty.Regularity;

public abstract class Transaction {

    private String name;
    private int amount;
    private LocalDate date;
    private Category category;
    private Regularity regularity;
    private String description;

    public Transaction() {
        this("", 0, LocalDate.now(), new Category(), new Regularity(), "");
    }

    public Transaction(String name, int amount, LocalDate date, Category category, Regularity regularity, String description) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.regularity = regularity;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public abstract int getSignedAmount();

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Regularity getRegularity() {
        return regularity;
    }

    public void setRegularity(Regularity regularity) {
        this.regularity = regularity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static enum Type {

        INCOME(Partner.EXTERNAL, FlowDirection.INFLOW, 1),
        EXPENSE(Partner.EXTERNAL, FlowDirection.OUTFLOW, 2),
        TRANSFER_IN(Partner.INTERNAL, FlowDirection.INFLOW, 3),
        TRANSFER_OUT(Partner.INTERNAL, FlowDirection.OUTFLOW, 3);

        private final Partner partner;
        private final FlowDirection flowDirection;
        private final int mainCategoryId;

        private Type(Transaction.Partner p, Transaction.FlowDirection fd, int mainCategoryId) {
            this.partner = p;
            this.flowDirection = fd;
            this.mainCategoryId = mainCategoryId;
        }

        public Partner getPartner() {
            return partner;
        }

        public FlowDirection getFlowDirection() {
            return flowDirection;
        }

        public int getMainCategoryId() {
            return mainCategoryId;
        }
    }

    public static enum FlowDirection {
        INFLOW,
        OUTFLOW;
    }

    public static enum Partner {
        INTERNAL,
        EXTERNAL;
    }
}
