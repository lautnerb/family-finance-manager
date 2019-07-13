package application;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import model.Model;
import model.pojos.transactionproperty.Category;
import model.pojos.transaction.externaltransaction.Expense;
import model.pojos.FilterConditions;
import model.pojos.transaction.Transaction;
import model.pojos.transaction.externaltransaction.ExternalTransaction;
import model.pojos.transaction.externaltransaction.Income;
import model.pojos.transactionproperty.Regularity;

public class Controller implements Initializable {

    //Toolbar
    @FXML
    private Button transactionsButton;
    @FXML
    private Button categoriesButton;

    //Panes
    @FXML
    private StackPane centerPane;
    @FXML
    private SplitPane transactionsPane;
    @FXML
    private HBox categoriesPane;
    @FXML
    private TitledPane tpCategoryFilter;
    @FXML
    private TitledPane tpDateFilter;
    @FXML
    private TitledPane tpRegularityFilter;

    //Chart
    @FXML
    private BarChart<String, Number> reportChart;

    //Table
    @FXML
    private TableView<Transaction> reportTable;

    //External transfer details    
    @FXML
    private Label lbTransactionDetailsTitle;
    @FXML
    private TextField transactionDetailsNameTextField;
    @FXML
    private TextField transactionDetailsAmountTextField;
    @FXML
    private DatePicker transactionDetailsDatePicker;
    @FXML
    private TitledPane transactionDetailsCategoryTitledPane;
    @FXML
    private TreeView<Category> transactionDetailsCategoryTreeView;
    @FXML
    private TextField transactionDetailsRegularityAmountTextField;
    @FXML
    private ChoiceBox<Regularity.Unit> transactionDetailsRegularityUnitChoiceBox;
    @FXML
    private TextArea transactionDetailsDescription;
    @FXML
    private Button addTransactionBtn;
    @FXML
    private Button modifyTransactionBtn;
    @FXML
    private Button deleteTransactionBtn;

    //Filter Controls
    //Transfer types
    @FXML
    private CheckBox incomeFilterCheckBox;
    @FXML
    private CheckBox expenseFilterCheckBox;
    //Category
    @FXML
    private CheckBox categoryFilterCheckBox;
    @FXML
    private TreeView<Category> categoryFilterTreeView;
    //Date
    @FXML
    private CheckBox dateFilterCheckBox;
    @FXML
    private DatePicker fromDateFilterDatePicker;
    @FXML
    private DatePicker toDateFilterDatePicker;
    //Regularity
    @FXML
    private CheckBox regularityFilterCheckBox;
    @FXML
    private ToggleGroup regularityFilterToggleGroup;
    @FXML
    private RadioButton oneWeekFilterRadioButton;
    @FXML
    private RadioButton oneMonthFilterRadioButton;
    @FXML
    private RadioButton oneYearFilterRadioButton;
    @FXML
    private RadioButton moreYearFilterRadioButton;
    @FXML
    private RadioButton irregularFilterRadioButton;

    //Categories Pane
    @FXML
    private TextField categoryNameTextField;
    @FXML
    private TitledPane categoryParentTitledPane;
    @FXML
    private TreeView<Category> categoryParentTreeView;
    @FXML
    private TreeView<Category> categoryTreeView;
    @FXML
    private Button addCategoryButton;
    @FXML
    private Button modifyCategoryButton;
    @FXML
    private Button deleteCategoryButton;

    private Model model;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initData();
        initChart();
        initReportTable();
        initTransactionDetailsControls();
        initFilterPane();
        initCategoriesPane();
        loadCategoryTreeViews();
        showPane(transactionsPane);
    }

    private void hideCenterPanes() {
        centerPane.getChildren().forEach((Node node) -> {
            node.setVisible(false);
        });
    }

    private void showPane(Region pane) {
        hideCenterPanes();
        pane.setVisible(true);
    }

    @FXML
    public void menuAction(ActionEvent event) {
        Object source = event.getSource();
        if (source == transactionsButton) {
            showPane(transactionsPane);
        } else if (source == categoriesButton) {
            showPane(categoriesPane);
        }
    }

    private void initReportTable() {
        reportTable.setPlaceholder(new Label("Nincsenek kiválasztott tranzakciók"));
        loadTableItems();

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Kategória");
        categoryCol.setCellValueFactory((param)
                -> new ReadOnlyStringWrapper(param.getValue().getCategory().getName())
        );

        TableColumn<Transaction, String> nameCol = new TableColumn<>("Név");
        nameCol.setCellValueFactory((param)
                -> new ReadOnlyStringWrapper(param.getValue().getName())
        );

        TableColumn<Transaction, Number> amountCol = new TableColumn<>("Összeg");
        amountCol.setCellValueFactory((param)
                -> new ReadOnlyIntegerWrapper(param.getValue().getAmount()));

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Dátum");
        dateCol.setCellValueFactory(
                (param)
                -> new ReadOnlyStringWrapper(param.getValue().getDate().toString()));

        reportTable.getColumns().setAll(Arrays.asList(categoryCol, nameCol, amountCol, dateCol));

        reportTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        if (newValue instanceof Income) {
                            lbTransactionDetailsTitle.setText("Bevétel részletei");
                        } else if (newValue instanceof Expense) {
                            lbTransactionDetailsTitle.setText("Kiadás részletei");
                        } else {
                            lbTransactionDetailsTitle.setText("");
                        }
                        transactionDetailsNameTextField.setText(newValue.getName());
                        transactionDetailsAmountTextField.setText(((Integer) newValue.getAmount()).toString());
                        transactionDetailsDatePicker.setValue(newValue.getDate());
                        transactionDetailsCategoryTreeView.setRoot(
                                createCategoryTreeItemRecursively(
                                        model.getMainCategory(newValue.getCategory())));
                        transactionDetailsCategoryTreeView.setShowRoot(true);
                        transactionDetailsCategoryTreeView.getSelectionModel().select(
                                searchCategory(
                                        newValue.getCategory(),
                                        transactionDetailsCategoryTreeView.getRoot()));

                        Integer regularityAmount = newValue.getRegularity().getAmount();
                        if (regularityAmount != 0) {
                            transactionDetailsRegularityAmountTextField.setText(regularityAmount.toString());
                        } else {
                            transactionDetailsRegularityAmountTextField.setText("");
                        }

                        transactionDetailsRegularityUnitChoiceBox.setValue(
                                newValue.getRegularity().getUnit());
                        transactionDetailsDescription.setText(newValue.getDescription());

                        addTransactionBtn.setText("Új");
                        modifyTransactionBtn.setDisable(false);
                        deleteTransactionBtn.setDisable(false);
                    } else {
                        loadEmptyTransactionControls();
                    }
                });
    }

    private void loadChart(List<Transaction> transactions) {
        LocalDate firstDate = transactions.get(transactions.size() - 1).getDate();
        LocalDate lastDate = transactions.get(0).getDate();
        YearMonth firstMonth = YearMonth.from(firstDate);
        YearMonth lastMonth = YearMonth.from(lastDate);
        Map<YearMonth, Integer> map = new LinkedHashMap<>();
        YearMonth currentMonth = firstMonth;
        do {
            map.put(currentMonth, 0);
            currentMonth = currentMonth.plusMonths(1);
        } while (!currentMonth.isAfter(lastMonth));

        boolean inflow = incomeFilterCheckBox.isSelected();
        boolean outflow = expenseFilterCheckBox.isSelected();

        if (inflow && outflow) {
            reportChart.setTitle("Megtakarítás");
            transactions.forEach((t) -> {
                YearMonth yearMonth = YearMonth.from(t.getDate());
                map.put(yearMonth, map.get(yearMonth) + t.getSignedAmount());
            });
        } else {
            transactions.forEach((t) -> {
                YearMonth yearMonth = YearMonth.from(t.getDate());
                map.put(yearMonth, map.get(yearMonth) + t.getAmount());
            });

            if (inflow) {
                reportChart.setTitle("Bevételek");
            } else {
                reportChart.setTitle("Kiadások");
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        map.forEach((key, value) -> {
            series.getData().add(new XYChart.Data<String, Number>(key.toString(), value));
        });

        reportChart.getData().clear();
        reportChart.getData().add(series);
    }

    private void initChart() {
        reportChart.setAnimated(false);
        reportChart.setLegendVisible(false);
    }

    private void initData() {
        model = Model.getInstance();
        model.connect();
        model.loadData();
    }

    private void initTransactionDetailsControls() {
        initTransactionDetailsCategoryTreeView();
        transactionDetailsRegularityUnitChoiceBox.setItems(FXCollections.observableArrayList(Regularity.Unit.values()));
        transactionDetailsRegularityUnitChoiceBox.setValue(Regularity.Unit.FOREVER);
        transactionDetailsRegularityUnitChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.equals(Regularity.Unit.FOREVER)) {
                        transactionDetailsRegularityAmountTextField.setDisable(true);
                    } else {
                        transactionDetailsRegularityAmountTextField.setDisable(false);
                    }
                });
        loadEmptyTransactionControls();
    }

    private void initTransactionDetailsCategoryTreeView() {
        transactionDetailsCategoryTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        transactionDetailsCategoryTitledPane.setText(newValue.getValue().getName());
                    } else {
                        transactionDetailsCategoryTitledPane.setText("");
                    }
                });

    }

    @FXML
    private void handleAddTransactionBtnClick(ActionEvent event) {
        if (reportTable.getSelectionModel().getSelectedItem() != null) {
            reportTable.getSelectionModel().select(null);
        } else {
            try {
                ExternalTransaction et = createExternalTransactionFromControls();
                et = model.addExternalTransaction(et);
                loadTableItems();
                reportTable.getSelectionModel().select(et);
            } catch (InputInvalidException e) {
                handleInputInvalidException(e);
            }
        }
    }

    private void loadTableItems() {
        List<Transaction> transactions = model.getTransactions(getFilterConditions());
        reportTable.setItems(FXCollections.observableList(transactions));

        if (transactions.isEmpty()) {
            loadEmptyChart();
        } else {
            loadChart(transactions);
        }
    }

    @FXML
    private void handleModifyTransactionBtnClick(ActionEvent event) {
        List<String> notifications = new ArrayList<>();
        Transaction transaction = reportTable.getSelectionModel().getSelectedItem();
        ExternalTransaction et = null;
        ExternalTransaction updatedET;

        try {

            if (transaction == null) {
                notifications.add("Nincs kijelölve tranzakció");
                throw new InputInvalidException(notifications);
            }

            if (transaction instanceof ExternalTransaction) {
                et = (ExternalTransaction) transaction;
            } else {
                notifications.add("Nem külső tranzakció van kijelölve");
                throw new InputInvalidException(notifications);
            }

            updatedET = createExternalTransactionFromControls();
            updatedET.setId(et.getId());
            model.updateExternalTransaction(updatedET);
            loadTableItems();
            reportTable.getSelectionModel().select(updatedET);
        } catch (InputInvalidException e) {
            handleInputInvalidException(e);
        }
    }

    @FXML
    private void handleDeleteTransactionBtnClick(ActionEvent event) {
        Transaction t = reportTable.getSelectionModel().getSelectedItem();
        if (t instanceof ExternalTransaction) {
            ExternalTransaction et = (ExternalTransaction) t;
            model.deleteExternalTransaction(et);
            loadTableItems();
        }
    }

    private ExternalTransaction createExternalTransactionFromControls()
            throws InputInvalidException {
        List<String> notifications = new ArrayList<>();

        ExternalTransaction et;
        String name;
        int amount = 0;
        LocalDate date;
        Category category = null;
        Category mainCategory = null;
        int regularityAmount = 0;
        Regularity.Unit regularityUnit;
        String description;

        name = transactionDetailsNameTextField.getText();

        try {
            amount = Integer.parseInt(transactionDetailsAmountTextField
                    .getText());
        } catch (NumberFormatException e) {
            notifications.add("Érvénytelen összeg");
        }

        date = transactionDetailsDatePicker.getValue();
        if (date == null) {
            notifications.add("Érvénytelen dátum");

        }

        try {
            category = transactionDetailsCategoryTreeView.getSelectionModel()
                    .getSelectedItem().getValue();
            mainCategory = model.getMainCategory(category);
        } catch (Exception e) {
            notifications.add("Nincs kiválasztva kategória");
        }

        try {
            if (transactionDetailsRegularityAmountTextField.getText().equals("")) {
                regularityAmount = 0;
            } else {
                regularityAmount = Integer.parseInt(
                        transactionDetailsRegularityAmountTextField.getText());
            }
        } catch (NumberFormatException e) {
            notifications.add("Érvénytelen gyakoriságmennyiség");
        }

        regularityUnit = transactionDetailsRegularityUnitChoiceBox
                .getSelectionModel().getSelectedItem();
        if (regularityUnit == null) {
            notifications.add("Érvénytelen gyakoriságegység");
        }

        description = transactionDetailsDescription.getText();

        if (!notifications.isEmpty()) {
            throw new InputInvalidException(notifications);
        }

        if (mainCategory == model.getIncomeCategoryRoot()) {
            et = new Income();
        } else {
            et = new Expense();
        }

        et.setName(name);
        et.setAmount(amount);
        et.setDate(date);
        et.setCategory(category);
        et.setRegularity(new Regularity(regularityAmount, regularityUnit));
        et.setDescription(description);

        return et;
    }

    private FilterConditions getFilterConditions() {

        FilterConditions conditions = new FilterConditions();

        //Transaction types
        if (incomeFilterCheckBox.isSelected()) {
            conditions.getTransactionTypes().add(Transaction.Type.INCOME);
        }
        if (expenseFilterCheckBox.isSelected()) {
            conditions.getTransactionTypes().add(Transaction.Type.EXPENSE);
        }

        //Category
        if (categoryFilterCheckBox.isSelected()) {
            TreeItem<Category> selected = categoryFilterTreeView
                    .getSelectionModel()
                    .getSelectedItem();
            if (selected != null) {
                conditions.setCategory(selected.getValue());
            }
        }

        //Date
        if (dateFilterCheckBox.isSelected()) {
            conditions.setFromDate(fromDateFilterDatePicker.getValue());
            conditions.setToDate(toDateFilterDatePicker.getValue());
        }

        //Regularity
        if (regularityFilterCheckBox.isSelected()) {
            Toggle selected = regularityFilterToggleGroup.getSelectedToggle();

            if (selected != null) {
                if (selected.equals(oneWeekFilterRadioButton)) {
                    conditions.setMinRegularity(new Regularity(1, Regularity.Unit.DAYS));
                    conditions.setMaxRegularity(new Regularity(1, Regularity.Unit.WEEKS));
                } else if (selected.equals(oneMonthFilterRadioButton)) {
                    conditions.setMinRegularity(new Regularity(1, Regularity.Unit.DAYS));
                    conditions.setMaxRegularity(new Regularity(1, Regularity.Unit.MONTHS));
                } else if (selected.equals(oneYearFilterRadioButton)) {
                    conditions.setMinRegularity(new Regularity(1, Regularity.Unit.DAYS));
                    conditions.setMaxRegularity(new Regularity(1, Regularity.Unit.YEARS));
                } else if (selected.equals(moreYearFilterRadioButton)) {
                    conditions.setMinRegularity(new Regularity(366, Regularity.Unit.DAYS));
                    conditions.setMaxRegularity(new Regularity(Integer.MAX_VALUE, Regularity.Unit.YEARS));
                } else if (selected.equals(irregularFilterRadioButton)) {
                    conditions.setMinRegularity(new Regularity(1, Regularity.Unit.FOREVER));
                    conditions.setMaxRegularity(new Regularity(1, Regularity.Unit.FOREVER));
                }
            }
        }

        return conditions;
    }

    private void initFilterPane() {
        initCategoryFilterTreeView();

        initFilterTitledPane(tpCategoryFilter, categoryFilterCheckBox);
        initFilterTitledPane(tpDateFilter, dateFilterCheckBox);
        initFilterTitledPane(tpRegularityFilter, regularityFilterCheckBox);
    }

    private void initFilterTitledPane(TitledPane tp, CheckBox cb) {
        tp.expandedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (tp.isExpanded()) {
                        cb.setSelected(true);
                    }
                });
    }

    @FXML
    private void handleRefreshButtonAction(ActionEvent event) {
        loadTableItems();
    }

    private void initCategoryFilterTreeView() {

    }

    @FXML
    private void handleAddCategoryButtonAction(ActionEvent event) {
        if (categoryTreeView.getSelectionModel().getSelectedItem() != null) {
            categoryTreeView.getSelectionModel().select(null);
        } else {
            try {
                Category newCategory = model.addCategory(createCategoryFromControls());

                loadCategoryTreeViews();
                categoryTreeView.getSelectionModel().select(
                        searchCategory(newCategory, categoryTreeView.getRoot()));
            } catch (InputInvalidException e) {
                handleInputInvalidException(e);
            }
        }
    }

    @FXML
    private void handleModifyCategoryButtonAction(ActionEvent event) {
        if (categoryTreeView.getSelectionModel().getSelectedItem() != null) {
            try {
                Category category = createCategoryFromControls();
                category.setId(categoryTreeView.getSelectionModel().getSelectedItem().getValue().getId());

                List<String> notifications = new ArrayList<>();

                if (category.getId() == category.getParent().getId()) {
                    notifications.add("Egy kategória nem lehet önmaga szülőkategóriája.");
                } else {
                    Category actCat = category.getParent();
                    while (actCat.getParent() != null && category.getId() != actCat.getId()) {
                        actCat = actCat.getParent();
                    }
                    if (actCat.getParent() != null) {
                        notifications.add("Egy kategória alkategóriája nem lehet a szülőkategóriája.");
                    }
                }

                if (!notifications.isEmpty()) {
                    throw new InputInvalidException(notifications);
                }

                Category updatedCategory = model.updateCategory(category);
                loadCategoryTreeViews();
                categoryTreeView.getSelectionModel().select(
                        searchCategory(updatedCategory, categoryTreeView.getRoot()));
            } catch (InputInvalidException e) {
                handleInputInvalidException(e);
            }
        }
    }

    @FXML
    private void handleDeleteCategoryButtonAction(ActionEvent event) {
        if (categoryTreeView.getSelectionModel().getSelectedItem() != null) {
            try {
                Category category = categoryTreeView.getSelectionModel().getSelectedItem().getValue();
                model.deleteCategory(category);
                loadCategoryTreeViews();
            } catch (Model.CategoryUsedException e) {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setContentText(String.join("\n", e.getNotifications()));
                alert.getButtonTypes().add(ButtonType.OK);
                alert.setTitle("A kategória nem törölhető");
                alert.showAndWait();
            }
        }
    }

    private Category createCategoryFromControls() throws InputInvalidException {
        Category category = new Category();
        List<String> notifications = new ArrayList<>();

        String name = categoryNameTextField.getText();
        if (name.equals("")) {
            notifications.add("A kategória neve nem lehet üres.");
        } else {
            category.setName(name);
        }

        try {
            category.setParent(categoryParentTreeView.getSelectionModel().getSelectedItem().getValue());
        } catch (Exception e) {
            notifications.add("Nincs kiválasztva szülőkategória.");
        }

        if (!notifications.isEmpty()) {
            throw new InputInvalidException(notifications);
        }
        return category;
    }

    private void initCategoriesPane() {
        initCategoryParentTreeView();
        initCategoryTreeView();
    }

    private void initCategoryParentTreeView() {
        categoryParentTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null) {
                                categoryParentTitledPane.setText(
                                        newValue.getValue().getName());
                            } else {
                                categoryParentTitledPane.setText(" ");
                            }
                        });
    }

    private void initCategoryTreeView() {
        categoryTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        categoryNameTextField.setText(newValue.getValue().getName());
                        categoryParentTreeView.setRoot(
                                createCategoryTreeItemRecursively(
                                        model.getMainCategory(
                                                newValue.getValue())));
                        categoryParentTreeView.setShowRoot(true);
                        addCategoryButton.setText("Új");

                        if (newValue.getValue().equals(model.getIncomeCategoryRoot())
                        || newValue.getValue().equals(model.getExpenseCategoryRoot())) {
                            categoryNameTextField.setDisable(true);
                            categoryParentTreeView.getSelectionModel().select(null);
                            categoryParentTreeView.setDisable(true);
                            modifyCategoryButton.setDisable(true);
                            deleteCategoryButton.setDisable(true);
                        } else {
                            categoryNameTextField.setDisable(false);
                            categoryParentTreeView
                                    .getSelectionModel()
                                    .select(
                                            searchCategory(
                                                    newValue.getValue().getParent(),
                                                    categoryParentTreeView.getRoot()));
                            categoryParentTreeView.setDisable(false);
                            modifyCategoryButton.setDisable(false);
                            deleteCategoryButton.setDisable(false);
                        }
                    } else {
                        categoryNameTextField.setText("");
                        categoryNameTextField.setDisable(false);
                        categoryParentTreeView.setRoot(
                                createCategoryTreeItemRecursively(
                                        model.getCategoryRoot()));
                        categoryParentTreeView.setShowRoot(false);
                        categoryParentTreeView.getSelectionModel().select(null);
                        categoryParentTreeView.setDisable(false);
                        addCategoryButton.setText("Hozzáad");
                        modifyCategoryButton.setDisable(true);
                        deleteCategoryButton.setDisable(true);
                    }
                });
    }

    private void loadCategoryTreeViews() {
        categoryTreeView.setRoot(
                createCategoryTreeItemRecursively(
                        model.getCategoryRoot()));
        categoryParentTreeView.setRoot(
                createCategoryTreeItemRecursively(
                        model.getCategoryRoot()));
        categoryFilterTreeView.setRoot(
                createCategoryTreeItemRecursively(
                        model.getCategoryRoot()));
        transactionDetailsCategoryTreeView.setRoot(
                createCategoryTreeItemRecursively(
                        model.getCategoryRoot()));
    }

    private TreeItem<Category> createCategoryTreeItemRecursively(Category category) {
        TreeItem<Category> categoryTreeItem = new TreeItem<>(category);
        category.getChildren().forEach((child) -> {
            categoryTreeItem
                    .getChildren()
                    .add(createCategoryTreeItemRecursively(child));
        });
        return categoryTreeItem;
    }

    private TreeItem<Category> searchCategory(
            Category searched,
            TreeItem<Category> treeItem) {

        if (searched == null || treeItem == null) {
            return null;
        }

        if (treeItem.getValue().equals(searched)) {
            return treeItem;
        }

        List<TreeItem<Category>> children = treeItem.getChildren();
        int i = 0;
        TreeItem<Category> res = null;
        while (res == null && i < children.size()) {
            res = searchCategory(searched, children.get(i));
            i++;
        }

        return res;
    }

    private void handleInputInvalidException(InputInvalidException e) {
        String message = String.join("\n", e.getNotifications());
        Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        alert.setTitle("Érvénytelen bevitel");
        alert.showAndWait();
    }

    private void loadEmptyChart() {
        if (!incomeFilterCheckBox.isSelected()
                && !expenseFilterCheckBox.isSelected()) {
            reportChart.setTitle("Nincs kiválasztva tranzakciótípus");
        } else {
            reportChart.setTitle("Nincsenek kiválasztott tranzakciók");
        }
        reportChart.getData().clear();
    }

    private void loadEmptyTransactionControls() {
        lbTransactionDetailsTitle.setText("Új tranzakció");
        transactionDetailsNameTextField.setText("");
        transactionDetailsAmountTextField.setText("");
        transactionDetailsDatePicker.setValue(LocalDate.now());
        transactionDetailsCategoryTreeView.setRoot(
                createCategoryTreeItemRecursively(
                        model.getCategoryRoot()));
        transactionDetailsCategoryTreeView.setShowRoot(false);
        transactionDetailsCategoryTreeView.getSelectionModel().select(null);
        transactionDetailsRegularityAmountTextField.setText("");
        transactionDetailsRegularityUnitChoiceBox.setValue(
                Regularity.Unit.FOREVER);
        transactionDetailsDescription.setText("");
        addTransactionBtn.setText("Hozzáad");
        modifyTransactionBtn.setDisable(true);
        deleteTransactionBtn.setDisable(true);
    }

    private class InputInvalidException extends Exception {

        private final List<String> notifications;

        public InputInvalidException(List<String> notifications) {
            this.notifications = notifications;
        }

        public List<String> getNotifications() {
            return notifications;
        }
    }
}
