package views.add_edit_category_views;

import entities.EntityException;
import entities.SessionStorage;
import use_cases.add_edit_category_use_case.CategoryOD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * View class for the EditCategoryV that extends Component class and implements ActionListener interface.
 */
public class EditCategoryV extends Component implements ActionListener{
    private final CategoryC controller;
    private final JComboBox<String> categoryCombo;
    private final JTextField nameInput;
    private final JTextField budgetInput;
    private String selectedCategory;
    private final int monthID;
    private final SessionStorage currSession;
    /**
     * Builds EditCategoryV for user entries.
     * @param controller CategoryC reacts to user input to return a CategoryOD.
     * @param existingCategory String of existing categories in the MonthlyStorage with monthID.
     * @param monthID int representing the MonthlyStorage.
     * @param currSession SessionStorage the current working session.
     */
    public EditCategoryV(CategoryC controller, String[] existingCategory, int monthID, SessionStorage currSession) {
        JLabel selectCategoryLabel = new JLabel(" Select existing category:");
        this.categoryCombo = new JComboBox<>(existingCategory); // category list
        JLabel nameLabel = new JLabel("New Category Name:");
        this.nameInput = new JTextField(15);
        JLabel budgetLabel = new JLabel(" New Category Budget:");
        this.budgetInput = new JTextField(15);
        JButton submit = new JButton("Submit");
        submit.setSize(30,10);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setTitle("Edit Category");
        frame.setSize(500,300);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));
        panel.setLayout(new GridLayout(0,1));

        panel.add(selectCategoryLabel);
        panel.add(categoryCombo);
        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(nameInput, BorderLayout.CENTER);
        panel.add(budgetLabel);
        panel.add(budgetInput);

        frame.add(panel, BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);

        submit.addActionListener(this);
        categoryCombo.addActionListener(this);

        this.controller = controller;
        this.monthID = monthID;
        this.currSession = currSession;
    }

    /**
     * Tries an Edit Category Use Case.
     * Pop-up window with context specific message may be shown to user.
     */
    private void tryUseCaseEdit(){
        try {
            CategoryOD message = controller.categoryInMonth(nameInput.getText(), String.valueOf(budgetInput.getText()), monthID, currSession, selectedCategory);
            JOptionPane.showMessageDialog(this, message.getMessage());
        } catch (EntityException e) {
            JOptionPane.showMessageDialog(this, "This month does not exist in current session. Please go to add month page.");
        }
    }

    /**
     * Checks and formats user input to pass in valid parameters for a CategtoryC to start a use case.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        // Check if user inputs a category name.
        if (nameInput.getText().isEmpty()) {
            JOptionPane.showMessageDialog( this, "Please enter the previous category name if you don't wish to edit. Thanks.");
        }
        // Check if user inputs a category budget.
        if (budgetInput.getText().isEmpty()){
            JOptionPane.showMessageDialog(this,"Please enter the previous category budget if you don't wish to edit. Thanks.");
        }
        // Check if user selects an old category.
        if (categoryCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog( this, "Please select a category to edit.");
        }

        //Two ActionListeners with different behaviours differentiated by checking evt.getSource().
        if (evt.getSource() == categoryCombo) {
            this.selectedCategory = (String) categoryCombo.getSelectedItem();
        }
        else {
            tryUseCaseEdit();
        }
    }
}
