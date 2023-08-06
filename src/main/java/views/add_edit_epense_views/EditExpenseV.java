package views.add_edit_epense_views;

import entities.EntityException;
import entities.Expense;
import entities.SessionStorage;
import use_cases.add_edit_expenses_use_case.ExpenseOD;
import views.load_monthly_menu.LoadMonthMenuVB;
import views.monthly_menu.MonthMenuV;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * View class for the EditExpenseV that extends Component class and implements ActionListener interface.
 */

public class EditExpenseV extends Component implements ActionListener, LoadMonthMenuVB {
    private final MonthMenuV monthMenu;
    private final ExpenseC controller;
    private final JComboBox<String> expenseCombo;
    private final JComboBox<String> categoryCombo;
    private final JTextField nameInput;
    private final JTextField valueInput;
    private String selectedCategory;
    private String selectedExpense;
    private final JCheckBox isRecurringCheckBox = new JCheckBox("This is a recurring expense.");
    private boolean isRecurring;
    private final JButton submit = new JButton("Submit");
    private final int monthID;
    private final SessionStorage currSession;

    /**
     * Builds EditExpenseV for user entries.
     * @param monthMenu MonthMenuV that contains the button that creates EditExpenseV.
     * @param controller ExpenseC reacts to user input to return ExpenseOD.
     * @param existingExpense String[] of Expense names that exists in current month.
     * @param existingCategory String[] of Category names that exists in current month.
     * @param monthID int representing the MonthlyStorage.
     * @param currSession SessionStorage the current working session.
     */
    public EditExpenseV(MonthMenuV monthMenu, ExpenseC controller, String[] existingExpense, String[] existingCategory, int monthID, SessionStorage currSession) {
        this.monthMenu = monthMenu;
        this.controller = controller;
        this.monthID = monthID;
        this.currSession = currSession;

        this.expenseCombo = new JComboBox<>(existingExpense);
        this.nameInput = new JTextField(15);
        this.valueInput = new JTextField(15);
        this.categoryCombo = new JComboBox<>(existingCategory);
    }

    /**
     * Open edit expense GUI.
     */
    public void openEditExpense(){
        JLabel select_expense_label = new JLabel(" Select existing expense:");
        JLabel nameLabel = new JLabel("New Expense Name:");
        JLabel valueLabel = new JLabel(" New Expense Budget:");
        JLabel select_category_label = new JLabel(" Select existing category:");
        isRecurringCheckBox.setBounds(100,150,50,50);
        submit.setSize(30,10);

        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));
        panel.setLayout(new GridLayout(0,1));
        JPanel panell = new JPanel();
        panell.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));
        panell.setLayout(new GridLayout(0,1));

        frame.add(panel, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Edit Expense");
        frame.setSize(300,500);
        frame.setSize(500,300);
        panel.add(select_expense_label);
        panel.add(expenseCombo);
        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(nameInput, BorderLayout.CENTER);
        panel.add(valueLabel);
        panel.add(valueInput);
        panel.add(select_category_label);
        panel.add(categoryCombo);
        panel.add(isRecurringCheckBox);
        frame.add(panell, BorderLayout.SOUTH);
        panell.add(submit);

        frame.pack();
        frame.setVisible(true);

        submit.addActionListener(this);
        expenseCombo.addActionListener(this);
        categoryCombo.addActionListener(this);
        isRecurringCheckBox.addActionListener(this);
    }
    /**
     * Checks and formats user input to pass in valid parameters to start a use case.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == expenseCombo) {
            this.selectedExpense = (String) expenseCombo.getSelectedItem();}
        if(currSession.getRecurData().size() > 0){
            for(Expense recurExpense : currSession.getRecurData()){
                if(recurExpense.getName().equals(selectedExpense)){
                    isRecurringCheckBox.setSelected(true);}
            }
        }
        if(evt.getSource() == categoryCombo){
            this.selectedCategory = (String) categoryCombo.getSelectedItem();}
        if (evt.getSource() == isRecurringCheckBox){
                this.isRecurring = isRecurringCheckBox.isSelected();
        } else {
            ExpenseOD message = null;
            try {
                message = controller.expenseInMonth(nameInput.getText(), String.valueOf(valueInput), selectedCategory, isRecurring, monthID, currSession, selectedExpense);
                // Update Month Menu
                loadMonthMenu(currSession,monthID,null);
            } catch (EntityException e) {
                JOptionPane.showMessageDialog( this, "This month does not exist in current session. Please go to add month page.");
            }
            if(message != null){
                JOptionPane.showMessageDialog( this, message.getMessage());}
        }
    }

    /**
     * Load Month Menu and notify user if opening Month Menu of a new MonthlyStorage created.
     *
     * @param session the SessionStorage holding the required MonthlyStorage
     * @param monthID the monthID of the required MonthlyStorage
     * @param message notify user when new MonthlyStorage is created, otherwise null
     */
    @Override
    public void loadMonthMenu(SessionStorage session, int monthID, String message) {
        monthMenu.openMonthMenu(message,false);
    }
}
