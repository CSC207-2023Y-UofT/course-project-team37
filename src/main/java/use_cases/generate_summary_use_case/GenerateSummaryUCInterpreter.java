package use_cases.generate_summary_use_case;

import entities.Category;
import entities.Expense;
import entities.MonthlyStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that generates all the data in the correct format to make the Bar Graph and Pie Chart.
 */
public class GenerateSummaryUCInterpreter {

    private final Map<String, ArrayList<Double>> statisticalData;
    private final double remainder;

    /**
     * Creates a new GenerateSummaryUCInterpreter with the provided MonthlyStorage.
     * @param monthlyStorage the MonthlyStorage object that the data will be generated for
     */
    public GenerateSummaryUCInterpreter(MonthlyStorage monthlyStorage) {
        Map<Category, ArrayList<Expense>> mappedExpenses = sortExpenses(monthlyStorage);

        this.statisticalData = generateStatisticalData(monthlyStorage, mappedExpenses);
        this.remainder = generateRemainder(monthlyStorage, mappedExpenses);
    }

    /**
     * Gets the statistical data required for creating the graphs.
     * @return a Map holding data used to make the graphs
     */
    public Map<String, ArrayList<Double>> getStatisticalData() {
        return statisticalData;
    }

    /**
     * Gets the remainder of this MonthlyStorage object.
     * @return the remainder of this MonthlyStorage
     */
    public double getRemainder() {
        return remainder;
    }

    /**
     * Generates a map that has Category objects as keys and a list of Expense objects associated with that Category as
     * values.
     * @param monthlyStorage a MonthlyStorage object for the current month
     * @return a Map holding sorted month data
     */
    private Map<Category, ArrayList<Expense>> sortExpenses(MonthlyStorage monthlyStorage){
        Map<Category, ArrayList<Expense>> mappedExpenses = new HashMap<>();

        for (Category c: monthlyStorage.getCategoryData()){
            mappedExpenses.put(c, new ArrayList<>());
        }
        for (Expense e: monthlyStorage.getExpenseData()){
            mappedExpenses.get(e.getCategory()).add(e);
        }
        return mappedExpenses;
    }

    /**
     * Generates a map containing String names of Category objects as the keys and doubles representing money spent
     * and budget as the values.
     * @param monthlyStorage a MonthlyStorage object for the current month
     * @param mappedExpenses a Map that has Category objects as keys and a list of Expense objects associated with that
     *                       Category as values
     * @return a Map holding data used to make the graphs
     */
    private Map<String, ArrayList<Double>> generateStatisticalData(MonthlyStorage monthlyStorage, Map<Category,
            ArrayList<Expense>> mappedExpenses){
        Map<String, ArrayList<Double>> statisticalData = new HashMap<>();
        for (Category c: monthlyStorage.getCategoryData()) {
            statisticalData.put(c.getName(), new ArrayList<>());
            double expenseSum = 0;
            for (Expense e: mappedExpenses.get(c)){
                expenseSum += e.getValue();
            }
            statisticalData.get(c.getName()).add(expenseSum);
            statisticalData.get(c.getName()).add(c.getBudget());
        }
        return statisticalData;
    }

    /**
     * Generates a double representing the money that the user has not spent in their budget.
     * @param monthlyStorage a MonthlyStorage object for the current month
     * @param mappedExpenses a Map that has Category objects as keys and a list of Expense objects associated with that
     * Category as values
     * @return the remainder of this MonthlyStorage
     */
    private double generateRemainder(MonthlyStorage monthlyStorage, Map<Category, ArrayList<Expense>> mappedExpenses){
        double remainder = monthlyStorage.getMonthlyBudget();
        for (Expense e: monthlyStorage.getExpenseData()){
            mappedExpenses.get(e.getCategory()).add(e);
            remainder -= e.getValue();
        }
        return remainder;
    }

}