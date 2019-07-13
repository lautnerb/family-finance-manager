package model.pojos.transactionproperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Category extends TransactionProperty {

    private Category parent;
    private final List<Category> children = new ArrayList<>();

    public Category() {
        super();
        this.parent = null;
    }

    public Category(Category parent, int id, String name) {
        super(id, name);
        this.parent = parent;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getParent());
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
