package sample_data;

import java.util.ArrayList;
import java.util.List;

public class SampleData {

    public static List<String> getSampleInserts() {
        List<String> stmts = new ArrayList<>();
        stmts.add("INSERT INTO categories (name, parent_id) VALUES ('Fizetés', 1)");
        stmts.add("INSERT INTO categories (name, parent_id) VALUES ('Ebéd', 2)");
        stmts.add("INSERT INTO categories (name, parent_id) VALUES ('Bevásárlás', 2)");
        stmts.add("INSERT INTO categories (name, parent_id) VALUES ('Lakás', 2)");
        stmts.add("INSERT INTO categories (name, parent_id) VALUES ('Fűtés', 13)");

        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 100000, '2018-01-01', 10, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 100000, '2018-02-01', 10, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 100000, '2018-03-01', 10, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 100000, '2018-04-01', 10, 1, 'MONTHS')");
        
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 21000, '2018-01-01', 11, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 20000, '2018-02-01', 11, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 22000, '2018-03-01', 11, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 21000, '2018-04-01', 11, 1, 'MONTHS')");
        
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 21000, '2018-01-01', 12, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 20000, '2018-02-01', 12, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 22000, '2018-03-01', 12, 1, 'MONTHS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 21000, '2018-04-01', 12, 1, 'MONTHS')");
        
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 21000, '2018-01-01', 14, 1, 'YEARS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 20000, '2018-02-01', 14, 1, 'YEARS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('', 10000, '2018-03-01', 14, 1, 'YEARS')");
        stmts.add("INSERT INTO external_transactions (name, amount, date, category_id, regularity_amount, regularity_unit)\n"
                + "VALUES ('TV', 100000, '2018-04-01', 13, 8, 'YEARS')");       

        return stmts;
    }
}
