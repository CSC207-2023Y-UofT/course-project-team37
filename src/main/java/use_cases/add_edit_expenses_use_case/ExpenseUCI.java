package use_cases.add_edit_expenses_use_case;

import entities.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ExpenseUCI implements ExpenseIB {
    private final ExpenseOB expenseOB;
    private ExpenseID expenseID;
    private Category selectedCategory;
    private Expense selectedExpense;
    private double valueDouble;
    private ArrayList<Expense> monthExpenseList;
    private ArrayList<Category> monthCategoryList;
    private ArrayList<Expense> recurringExpenseList;
    private final ExpenseFactory expenseFactory;
    private MonthlyStorage month;
    private SessionStorage session;

    /**
     * Constructs ExpenseUCI.
     *
     * @param expenseP presenter that is related to the use case.
     */
    public ExpenseUCI(ExpenseOB expenseP) {
        this.expenseOB = expenseP;
        this.expenseFactory = new ExpenseFactory();
    }

    /**
     * Helper method returns a double to check if the input Object is a valid double.
     *
     * @param value a user input
     * @return double converted from value
     */
    public static double toDouble(Object value) throws NumberFormatException {
        return Double.parseDouble(value.toString());
    }

    /**
     * Overrides method in ExpenseIB.
     * Attempts to add an expense with information from ExpenseID and returns a ExpenseOD indicating whether fail/success after execution.
     * Provides detailed fail messages according to each condition below
     * @param expenseIDAdd ExpenseID required for adding a new expense to the designated monthID MonthlyStorage Object.
     * @return ExpenseOD String message indicating success/fail add attempt.
     * @throws EntityException thrown when expenseIDAdd has invalid expense information.
     */
    @Override
    public ExpenseOD addExpenseInMonth(ExpenseID expenseIDAdd) throws EntityException {
        this.expenseID = expenseIDAdd;
        this.session = expenseIDAdd.getSession();
        this.month = expenseIDAdd.getSession().getMonthlyData(expenseIDAdd.getMonthID());
        this.monthExpenseList = month.getExpenseData();
        this.monthCategoryList = month.getCategoryData();
        this.recurringExpenseList = session.getRecurData();

        try {double valueDouble = toDouble(expenseIDAdd.getValue());
            this.selectedCategory = findCategory(monthCategoryList, expenseID.getCategory());
            if (valueDouble < 0) {
                //Expense value less than 0 fail: When a user tries to add the expense value with a negative number.
                ExpenseOD expenseODFailAdd = new ExpenseOD("Expense value can't be less than $0. Please try again!");
                return expenseOB.fail(expenseODFailAdd);}

            for (Expense expense1 : monthExpenseList) {
                if (expense1.getName().equals(expenseID.getName())) {
                    //Repeated name in month
                    ExpenseOD expenseODFailEdit = new ExpenseOD("There is already a expense with this new name in this month.");
                    return expenseOB.fail(expenseODFailEdit);}}

            if (expenseID.getIsRecurringExpense()) {
                Expense newrecurExpense = expenseFactory.createMonthObject(expenseID.getName(), selectedCategory, valueDouble);
                session.addRecurExpense(newrecurExpense);
                month.addExpense(newrecurExpense);
                ExpenseOD expenseODSuccessAdd = new ExpenseOD("You have created a new recurring expense!");
                return expenseOB.success(expenseODSuccessAdd);
            }else{
                month.addExpense(expenseFactory.createMonthObject(expenseID.getName(), selectedCategory, valueDouble));}
                ExpenseOD expenseODSuccessAdd = new ExpenseOD("You have added a new expense!");
                return expenseOB.success(expenseODSuccessAdd);

        } catch (NumberFormatException e) {
            // NumberFormatException|NullPointerException fail: User tries to edit Expense value to an invalid number.
            ExpenseOD expenseODFailAdd = new ExpenseOD("Expense value needs to be a number. Please try again!");
            return expenseOB.fail(expenseODFailAdd);
        } catch (EntityException e) {
            // EntityException fail: User tries to add an invalid Expense name but failed. (See entities/EntityException.java)
            ExpenseOD expenseODFailAdd = new ExpenseOD("There is already a expense with this new name in this month.");
            return expenseOB.fail(expenseODFailAdd);
        } catch(NoSuchElementException e){
        // NoSuchElementException fail: User tries to assign a category that does not exist in current month.
        ExpenseOD expenseODFailEdit = new ExpenseOD("There is no such category in the current month. Please add a new category or select existing category!");
        return expenseOB.fail(expenseODFailEdit);}}

    /**
     * Attempts to edit an expense with information from ExpenseID and returns a ExpenseOD indicating whether fail/success after execution.
     * Provides detailed fail messages according to each condition below
     * @param expenseIDEdit ExpenseID required for editing a new expense to the designated monthID MonthlyStorage Object.
     * @return ExpenseOD String message indicating success/fail add attempt.
     * @throws EntityException (Although we know MonthlyStorage with monthID is always in the SessionStorage,
     *                         it will be caught at views/add_edit_expense_views/EditExpenseV.java).
     */
    @Override
    public ExpenseOD editExpenseInMonth(ExpenseID expenseIDEdit) throws EntityException {
        this.expenseID = expenseIDEdit;
        this.session = expenseID.getSession();
        this.month = session.getMonthlyData(expenseID.getMonthID());
        this.monthExpenseList = month.getExpenseData();
        this.monthCategoryList = month.getCategoryData();

        try {this.selectedCategory = findCategory(monthCategoryList, expenseID.getCategory());
            this.selectedExpense = findExpense(monthExpenseList, expenseID.getOldExpense());
            this.valueDouble = toDouble(expenseIDEdit.getValue());
            if (valueDouble < 0) {
                //Expense value less than 0 fail: When a user tries to edit the expense value with a negative number.
                ExpenseOD expenseODFailEdit = new ExpenseOD("Expense value can't be less than $0. Please try again!");
                return expenseOB.fail(expenseODFailEdit);}

            if(!Objects.equals(expenseIDEdit.getName(), expenseIDEdit.getOldExpense())){
                if(checkHaveSameNameInList(monthExpenseList, expenseID.getName())) {
                    // Repeated name fail: When a user tries to edit the expense name to a existing expense in month.
                    ExpenseOD expenseODFailEdit = new ExpenseOD("There is already a expense with this new name in this month.");
                    return expenseOB.fail(expenseODFailEdit);}}

            if (changeInRecurringInfo()) {
                if (expenseID.getIsRecurringExpense()) {
                    session.addRecurExpense(expenseFactory.editMonthObject(setExpenseEditorInfo()));
                    // Success edit to new recurring expense.
                    ExpenseOD expenseODSuccessEditRecurring = new ExpenseOD("You have updated all changes of this new recurring expense!");
                    return expenseOB.success(expenseODSuccessEditRecurring);
                }else{
                    expenseFactory.editMonthObject(setExpenseEditorInfo());
                    session.deleteRecurExpense(expenseID.getName());
                    //Success edit to remove recurring expense.
                    ExpenseOD expenseODSuccessEditRecurring = new ExpenseOD("You have updated all changes of this expense and it is no longer a recurring expense!");
                    return expenseOB.success(expenseODSuccessEditRecurring);}
            }else{expenseFactory.editMonthObject(setExpenseEditorInfo());}

            //Success edit to expense
            ExpenseOD expenseODSuccessEdit = new ExpenseOD("You have edited an expense!");
            return expenseOB.success(expenseODSuccessEdit);

            } catch(NoSuchElementException e){
                //NoSuchElementException fail: User tries to edit an expense that does not exist.
                ExpenseOD expenseODFailEdit = new ExpenseOD("There is no such expense in the current month. Please add a new expense or select existing expense!");
                return expenseOB.fail(expenseODFailEdit);
            } catch(NumberFormatException e){
                // NumberFormatException|NullPointerException fail: User tries to edit Expense value to an invalid number.
                ExpenseOD expenseODFailEdit = new ExpenseOD("Expense value needs to be a number. Please try again!");
                return expenseOB.fail(expenseODFailEdit);}
    }

    /**
     * Finds Expense with String name from a provided list of Expenses.
     * @param expenseData An ArrayList of expenses.
     * @param name Expense name.
     * @return Expense with given String name.
     * @throws NoSuchElementException thrown when couldn't find Expense with String name.
     */
        public Expense findExpense(ArrayList<Expense> expenseData, String name)throws NoSuchElementException{
            if(expenseData != null) {
                for (Expense expense : expenseData) {
                    if (Objects.equals(expense.getName(), name)) {
                        return expense;}}
            }
            throw new NoSuchElementException();}

    /**
     * Finds Category with String name from a provided list of Categories.
     * @param monthCategoryData An ArrayList of categories.
     * @param name Category name.
     * @return Category with given String name.
     * @throws NoSuchElementException thrown when couldn't find Category with String name.
     */
    public Category findCategory (ArrayList < Category > monthCategoryData, String name) throws NoSuchElementException {
            for (Category category : monthCategoryData) {
                if (Objects.equals(category.getName(), name)) {
                    return category;}}
            throw new NoSuchElementException();}

    /**
     * Checks if a given Expense arraylist contains a name that is the same as the Expense name input.
     * @param expenseList ArrayList<Expense> containing a list of expenses
     * @return boolean checks if same name exists
     */
    private boolean checkHaveSameNameInList(ArrayList<Expense> expenseList, String name){
        for (Expense expense1 : expenseList) {
            return expense1.getName().equals(name);}
        return false;}

    /**
     * Identifying the only two cases when Session.recurringExpenseData needs to updated when attempting to edit Expense.
     * (The user changes their response for the isRecurringExpense checkbox)
     * @return boolean suggesting there is a need to update Session.recurringExpenseData
     */
    private boolean changeInRecurringInfo() {
        if(expenseID.getIsRecurringExpense()) {
            try {findExpense(recurringExpenseList, expenseID.getOldExpense());
                return false;
            }catch (NoSuchElementException e){return true;}
        }else{
            try{findExpense(recurringExpenseList, expenseID.getOldExpense());
                return true;
            }catch(NoSuchElementException e) {
                return false;}}}

    /**
     * Sets the information needed to create an ExpenseCreatorInputData to call the createMonthObject method in ExpenseFactory
     * @return ExpenseCreatorInputData MonthObjectFactoryInputData Object specifically used in ExpenseFactory for the createMonthObject method.
     */
    private ExpenseCreatorInputData setExpenseCreatorInfo(){
        return ExpenseCreatorInputData expenseCreatorInputData = new ExpenseCreatorInputData(expenseID.getName(),selectedCategory, valueDouble);}
    /**
     * Sets the information needed to edit an ExpenseEditorInputData to call the editMonthObject method in ExpenseFactory
     * @return ExpenseEditorInputData MonthObjectFactoryInputData Object specifically used in ExpenseFactory for the editMonthObject method.
     */
    private ExpenseEditorInputData setExpenseEditorInfo(){
        return ExpenseEditorInputData expenseEditorInputData(expenseID.getName(),selectedCategory, valueDouble, selectedExpense);}

}



