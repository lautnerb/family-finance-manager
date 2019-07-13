package model.pojos.transactionproperty;

public abstract class TransactionProperty {
    
    private int id;
    private String name;
    
    public TransactionProperty() {
        this(0, "");
    }
    
    public TransactionProperty(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
