package use_cases.monthly_menu;

import entities.*;

import java.util.ArrayList;

/**
 * The use case interactor class for creating the Month Menu output.
 * This class implements the UpdateViewIB interface. The controller
 * class calls this class to get the MonthMenuOD object.
 */
public class UpdateViewUCI implements UpdateViewIB{
    private final MonthMenuOB outputBoundary;

    /**
     * Construct a UpdateViewUCI.
     * @param outputBoundary MonthMenuOB related to using output
     */
    public UpdateViewUCI(MonthMenuOB outputBoundary){
        this.outputBoundary = outputBoundary;
    }

    /**
     * Pass in and use UpdateViewID containing input data to create output data.
     * @param input input passed in from the controller class
     * @return MonthMenuOD object that contains output data
     */
    @Override
    public MonthMenuOD createOutput(UpdateViewID input){
        // Get input data
        SessionStorage session = input.getSession();
        int monthID = input.getMonthID();

        try{
            MonthlyStorage monthData = session.getMonthlyData(monthID); // throws EntityException
            ArrayList<Expense> expenseData= monthData.getExpenseData();
            ArrayList<Category> categoryData= monthData.getCategoryData();
            double monthlyBudget = monthData.getMonthlyBudget();

            return outputBoundary.createOutput(new MonthMenuOD(expenseData,categoryData, monthlyBudget,true));
        }
        catch(EntityException e){ //set String warning as output if EntityException is caught
            return outputBoundary.createOutput(new MonthMenuOD(
                    "An error has occurred. Please reload the program.",false));
        }
    }
}
