package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.pojos.transactionproperty.Category;
import model.pojos.transaction.externaltransaction.Expense;
import model.pojos.FilterConditions;
import model.pojos.transaction.Transaction;
import model.pojos.transaction.externaltransaction.ExternalTransaction;
import model.pojos.transaction.externaltransaction.Income;
import model.pojos.transactionproperty.Regularity;
import sample_data.SampleData;

public class Model implements Database {

    private static final Model INSTANCE = new Model();

    private Connection conn;

    private Map<Integer, Category> categoriesMap;
    private Category categoryRoot;
    private Category incomeCategoryRoot;
    private Category expenseCategoryRoot;

    private Model() {

    }

    public void connect() {
        close();

        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet rs = dbMetaData.getTables(null, "APP", null, null);
            if (!rs.next()) {
                createTables();
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                handleSQLException(ex);
            }
        }
    }

    public static Model getInstance() {
        return INSTANCE;
    }

    public void loadData() {
        loadTransactionProperties();
    }

    public void test() {
        ResultSet rs = null;
        ResultSetMetaData tablesMetaData;

        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            rs = dbMetaData.getTables(null, "APP", null, null);

            tablesMetaData = rs.getMetaData();

            int columnCount = tablesMetaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(tablesMetaData.getColumnName(i) + " ");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + " ");
                }
                System.out.println();
            }
        } catch (SQLException ex) {
            handleSQLException(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private void executeUpdate(String sqlStmt) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlStmt);
        } catch (SQLException ex) {
            handleSQLException(sqlStmt, ex);
        }
    }

    public void createTables() {
        executeUpdate(CREATE_TABLE_CATEGORIES);
        executeUpdate(CREATE_TABLE_EXTERNAL_TRANSACTIONS);
        executeUpdate(INSERT_MAIN_CATEGORY_INCOME);
        executeUpdate(INSERT_MAIN_CATEGORY_EXPENSE);
    }

    public void insertSample() {
        SampleData.getSampleInserts().forEach(
                (sqlStmt) -> {
                    executeUpdate(sqlStmt);
                });
    }

    private void loadCategories() {
        categoriesMap = new LinkedHashMap<>();
        Map<Integer, Integer> categoryParentMap = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(SELECT_CATEGORIES);
                ResultSet rs = ps.executeQuery();) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                categoryParentMap.put(category.getId(), rs.getInt("parent_id"));
                categoriesMap.put(category.getId(), category);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        for (Category category : categoriesMap.values()) {
            int parentId = categoryParentMap.get(category.getId());
            if (parentId > 0) {
                Category parent = categoriesMap.get(parentId);
                category.setParent(parent);
                parent.getChildren().add(category);
            }
        }

        incomeCategoryRoot = categoriesMap.get(
                Transaction.Type.INCOME.getMainCategoryId());
        expenseCategoryRoot = categoriesMap.get(
                Transaction.Type.EXPENSE.getMainCategoryId());
        categoryRoot = new Category(null, 0, "Category root");
        categoryRoot.getChildren().add(incomeCategoryRoot);
        categoryRoot.getChildren().add(expenseCategoryRoot);
    }

    public Category getCategoryRoot() {
        return categoryRoot;
    }

    public Category getIncomeCategoryRoot() {
        return incomeCategoryRoot;
    }

    public Category getExpenseCategoryRoot() {
        return expenseCategoryRoot;
    }

    public List<ExternalTransaction> getExternalTransactions(
            List<Transaction.Type> transactionTypes,
            List<String> sqlConditions,
            Regularity minRegularity,
            Regularity maxRegularity) {
        String sql = SELECT_EXTERNAL_TRANSACTIONS;

        for (String condition : sqlConditions) {
            sql += " AND\n" + condition;
        }

        List<ExternalTransaction> list = new ArrayList<>();

        try (ResultSet rs = conn.prepareStatement(sql).executeQuery()) {
            while (rs.next()) {
                ExternalTransaction et;
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int amount = rs.getInt("amount");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                Category category = categoriesMap.get(rs.getInt("category_id"));
                Category mainCategory = getMainCategory(category);
                Regularity regularity = new Regularity(
                        rs.getInt("regularity_amount"),
                        Regularity.Unit.valueOf(rs.getString("regularity_unit")));
                String description = rs.getString("description");

                if (mainCategory == getIncomeCategoryRoot()
                        && transactionTypes.contains(Transaction.Type.INCOME)) {
                    et = new Income(id, name, amount, date, category, regularity, description);
                    list.add(et);
                } else if (mainCategory == getExpenseCategoryRoot()
                        && transactionTypes.contains(Transaction.Type.EXPENSE)) {
                    et = new Expense(id, name, amount, date, category, regularity, description);
                    list.add(et);
                }
            }
        } catch (SQLException e) {
            handleSQLException(sql, e);
        }

        if (maxRegularity != null) {
            list.removeIf(
                    (et) -> {
                        Regularity regularity = et.getRegularity();
                        if (regularity.getDuration()
                                .compareTo(minRegularity.getDuration()) < 0) {
                            return true;
                        } else if (regularity.getDuration()
                                .compareTo(maxRegularity.getDuration()) > 0) {
                            return true;
                        } else {
                            return false;
                        }
                    });
        }

        return list;
    }

    private void handleSQLException(SQLException e) {
        System.err.println(e);
    }

    private void handleSQLException(String sql, SQLException e) {
        System.err.println(sql);
        System.err.println(e);
    }

    private void handleSQLException(
            String preparedStatement,
            List<String> parameters,
            SQLException e) {
        System.err.println(preparedStatement);
        System.err.println(String.join(", ", parameters));
        System.err.println(e);
    }

    private void handleSQLException(
            String preparedStatement,
            List<String> parameters,
            Exception e) {
        System.err.println(preparedStatement);
        System.err.println(String.join(", ", parameters));
        System.err.println(e);
    }

    private void loadTransactionProperties() {
        loadCategories();
    }

    public ExternalTransaction addExternalTransaction(ExternalTransaction et) {
        List<String> parameters = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(
                INSERT_EXTERNAL_TRANSACTION, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, et.getName());
            parameters.add(et.getName());

            ps.setInt(2, et.getAmount());
            parameters.add(Integer.toString(et.getAmount()));

            ps.setString(3, et.getDate().toString());
            parameters.add(et.getDate().toString());

            ps.setInt(4, et.getCategory().getId());
            parameters.add(Integer.toString(et.getCategory().getId()));

            ps.setInt(5, et.getRegularity().getAmount());
            parameters.add(Integer.toString(et.getRegularity().getAmount()));

            ps.setString(6, et.getRegularity().getUnit().name());
            parameters.add(et.getRegularity().getUnit().name());

            ps.setString(7, et.getDescription());
            parameters.add(et.getDescription());

            ps.execute();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    et.setId(id);
                }
            }
        } catch (SQLException e) {
            handleSQLException(INSERT_EXTERNAL_TRANSACTION, parameters, e);
        }
        return et;
    }

    public void updateExternalTransaction(ExternalTransaction updatedET) {
        List<String> parameters = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(UPDATE_EXTERNAL_TRANSACTION)) {
            ps.setString(1, updatedET.getName());
            parameters.add(updatedET.getName());

            ps.setInt(2, updatedET.getAmount());
            parameters.add(Integer.toString(updatedET.getAmount()));

            ps.setString(3, updatedET.getDate().toString());
            parameters.add(updatedET.getDate().toString());

            ps.setInt(4, updatedET.getCategory().getId());
            parameters.add(Integer.toString(updatedET.getCategory().getId()));

            ps.setInt(5, updatedET.getRegularity().getAmount());
            parameters.add(Integer.toString(updatedET.getRegularity().getAmount()));

            ps.setString(6, updatedET.getRegularity().getUnit().name());
            parameters.add(updatedET.getRegularity().getUnit().name());

            ps.setString(7, updatedET.getDescription());
            parameters.add(updatedET.getDescription());

            ps.setInt(8, updatedET.getId());
            parameters.add(Integer.toString(updatedET.getId()));

            ps.executeUpdate();

        } catch (SQLException e) {
            handleSQLException(UPDATE_EXTERNAL_TRANSACTION, parameters, e);
        }
    }

    public void deleteExternalTransaction(ExternalTransaction et) {

        try (PreparedStatement ps = conn.prepareStatement(DELETE_EXTERNAL_TRANSACTION)) {
            ps.setInt(1, et.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(DELETE_EXTERNAL_TRANSACTION, e);
        }
    }

    public List<Transaction> getTransactions(FilterConditions conditions) {

        List<Transaction> transactions = new ArrayList<>();

        int i = 0;
        while (i < conditions.getTransactionTypes().size()
                && conditions.getTransactionTypes().get(i).getPartner()
                != Transaction.Partner.EXTERNAL) {
            i++;
        }
        if (i < conditions.getTransactionTypes().size()) {
            List<String> conditionList = conditionsToSql(conditions);
            List<ExternalTransaction> etList = getExternalTransactions(
                    conditions.getTransactionTypes(),
                    conditionList,
                    conditions.getMinRegularity(),
                    conditions.getMaxRegularity());
            transactions.addAll(etList);
        }
        transactions.sort(
                (Transaction o1, Transaction o2)
                -> -(o1.getDate().compareTo(o2.getDate())));

        return transactions;
    }

    private ArrayList<String> conditionsToSql(FilterConditions conditions) {

        ArrayList<String> sqlConditions = new ArrayList<>();

        if (conditions.getCategory() != null) {
            List<Category> categories = getAllCategories(conditions.getCategory());
            List<String> categoryIds = new ArrayList<>();
            for (Category category : categories) {
                categoryIds.add(Integer.toString(category.getId()));
            }
            sqlConditions.add(
                    "category_id IN (" + String.join(",", categoryIds) + ")");
        }

        //fromDateCondition
        if (conditions.getFromDate() != null) {
            sqlConditions.add("date >= " + "'" + conditions.getFromDate().toString() + "'");
        }

        //toDateCondition
        if (conditions.getToDate() != null) {
            sqlConditions.add("date <= " + "'" + conditions.getToDate().toString() + "'");
        }

        return sqlConditions;
    }

    private ArrayList<Category> getAllCategories(Category root) {
        ArrayList<Category> list = new ArrayList<>();
        addCategoryRecursively(root, list);
        return list;
    }

    private void addCategoryRecursively(Category root, List<Category> list) {
        list.add(root);
        for (Category child : root.getChildren()) {
            addCategoryRecursively(child, list);
        }
    }

    public Category getMainCategory(Category category) {
        List<Category> inheritanceList = new ArrayList<>();

        do {
            inheritanceList.add(category);
            category = category.getParent();
        } while (category != null);

        return inheritanceList.get(inheritanceList.size() - 1);

    }

    public Category addCategory(Category category) {
        List<String> parameters = new ArrayList<>();
        int id = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                INSERT_CATEGORY,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            parameters.add(category.getName());
            ps.setInt(2, category.getParent().getId());
            parameters.add(Integer.toString(category.getParent().getId()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                while (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            handleSQLException(INSERT_CATEGORY, parameters, e);
        }
        loadCategories();
        return categoriesMap.get(id);
    }

    public Category updateCategory(Category category) {
        List<String> parameters = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(UPDATE_CATEGORY)) {
            ps.setString(1, category.getName());
            parameters.add(category.getName());
            ps.setInt(2, category.getParent().getId());
            parameters.add(Integer.toString(category.getParent().getId()));
            ps.setInt(3, category.getId());
            parameters.add(Integer.toString(category.getId()));

            ps.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(UPDATE_CATEGORY, parameters, e);
        } catch (Exception e) {
            handleSQLException(UPDATE_CATEGORY, parameters, e);
        }
        loadCategories();
        return categoriesMap.get(category.getId());
    }

    public void deleteCategory(Category category) throws CategoryUsedException {
        List<String> notifications = new ArrayList<>();
        String sql;

        sql = "SELECT *\n"
                + "FROM external_transactions\n"
                + "WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, category.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                notifications.add("A kategória már tartalmaz tranzakciót");
            }
        } catch (SQLException e) {
            handleSQLException(sql, e);
        }

        sql = "SELECT *\n"
                + "FROM categories\n"
                + "WHERE parent_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, category.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                notifications.add("A kategórának már van alkategóriája");
            }
        } catch (SQLException e) {
            handleSQLException(sql, e);
        }

        if (!notifications.isEmpty()) {
            throw new CategoryUsedException(notifications);
        }

        try (PreparedStatement ps = conn.prepareStatement(DELETE_CATEGORY)) {
            ps.setInt(1, category.getId());
            ps.executeUpdate();
            loadCategories();
        } catch (SQLException e) {
            handleSQLException(DELETE_CATEGORY, e);
        }
    }

    public class CategoryUsedException extends Exception {

        private final List<String> notifications;

        public CategoryUsedException(List<String> notifications) {
            this.notifications = notifications;
        }

        public List<String> getNotifications() {
            return notifications;
        }
    }
}
