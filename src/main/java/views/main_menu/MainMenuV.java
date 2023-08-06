package views.main_menu;

import entities.MonthlyStorage;
import entities.SessionStorage;
import use_cases.create_new_month.NewMonthUCI;
import use_cases.main_menu.SessionSaveOD;
import use_cases.monthly_menu.UpdateViewUCI;
import views.create_new_month.NewMonthC;
import views.create_new_month.NewMonthP;
import views.create_new_month.NewMonthV;
import views.monthly_menu.MonthMenuP;
import views.monthly_menu.MonthMenuV;
import views.monthly_menu.UpdateViewC;
import views.session_load.SessionLoadMainMenuVB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The main menu screen. It contains a button to save the session, a button
 * to create a new month, and also allows the user to open the month menu for any of the
 * existing months in the current session
 */
public class MainMenuV extends JPanel implements SessionLoadMainMenuVB, ActionListener {
    private final MainMenuC controller;
    private SessionStorage session;
    private int selectedMonthID;
    private final JComboBox<String> selectMonthComboBox;
    private final JButton selectMonthButton;
    private final JButton createMonthButton;
    private final JButton saveButton;


    public MainMenuV(MainMenuC controller) {
        this.controller = controller;

        JLabel title = new JLabel("Main Menu");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font(null, Font.PLAIN, 20));

        this.selectMonthComboBox = new JComboBox<>();
        this.selectMonthButton = new JButton("Open month");
        this.createMonthButton = new JButton("Create a new month");
        this.saveButton = new JButton("Save");

        // Disable dropdown and select button by default
        // They will be enabled later in openMainMenu if there are any months to select
        this.selectMonthComboBox.setEnabled(false);
        this.selectMonthButton.setEnabled(false);

        this.selectMonthComboBox.addActionListener(this);
        this.selectMonthButton.addActionListener(this);
        this.createMonthButton.addActionListener(this);
        this.saveButton.addActionListener(this);

        // Pack the dropdown and select buttons together in a JPanel
        JPanel monthSelection = new JPanel();
        monthSelection.setLayout(new BoxLayout(monthSelection, BoxLayout.X_AXIS));
        monthSelection.add(this.selectMonthComboBox);
        monthSelection.add(this.selectMonthButton);
        monthSelection.setMaximumSize(new Dimension(300, 25));

        monthSelection.setAlignmentX(Component.CENTER_ALIGNMENT);
        monthSelection.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.saveButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.createMonthButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.createMonthButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        this.add(new JPanel());
        this.add(title);
        this.add(new JPanel());
        this.add(monthSelection);
        this.add(new JPanel());
        this.add(this.createMonthButton);
        this.add(new JPanel());
        this.add(this.saveButton);
        this.add(new JPanel());

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setVisible(false);
    }

    /**
     * Makes the main menu visible which displays the session data, and opens a popup with the message
     *
     * @param message a message to display in a popup after opening the menu
     * @param session the session data to display, or null if displaying a message is not needed
     */
    @Override
    public void openMainMenu(String message, SessionStorage session) {
        this.session = session;

        ArrayList<MonthlyStorage> monthlyData = session.getAllMonthlyData();
        ArrayList<Integer> monthIDs = new ArrayList<>();

        for (MonthlyStorage month : monthlyData) {
            monthIDs.add(month.getMonthID());
        }

        // Add month ids to dropdown menu to display
        this.selectMonthComboBox.removeAllItems();
        for (Integer id : monthIDs) {
            this.selectMonthComboBox.addItem(id.toString());
        }

        // Only enable dropdown and select buttons if we do have months to display
        this.selectMonthComboBox.setEnabled(!monthIDs.isEmpty());
        this.selectMonthButton.setEnabled(!monthIDs.isEmpty());

        this.setVisible(true);
        if (message != null) {
            JOptionPane.showMessageDialog(this, message);
        }
    }

    /**
     * Reacts to UI events
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.selectMonthComboBox) {
            // Handle month selection dropdown
            if (this.selectMonthComboBox.getSelectedItem() != null) {
                String selectedID = (String) this.selectMonthComboBox.getSelectedItem();
                this.selectedMonthID = Integer.parseInt(selectedID);
            }
        } else if (e.getSource() == this.selectMonthButton) {
            // Open a new MonthMenuV to display the selected month
            UpdateViewC monthMenuController = new UpdateViewC(new UpdateViewUCI(new MonthMenuP()));
            MonthMenuV monthMenuV = new MonthMenuV(monthMenuController, this.session, this.selectedMonthID);
            monthMenuV.openMonthMenu(null, true);
        } else if (e.getSource() == this.createMonthButton) {
            // Open a new NewMonthV so user can create their new month
            NewMonthC newMonthController = new NewMonthC(new NewMonthUCI(new NewMonthP()));
            new NewMonthV(this, newMonthController, this.session);
        } else if (e.getSource() == this.saveButton) {
            // Implementation of file chooser inspired from
            // https://www.geeksforgeeks.org/java-swing-jfilechooser/
            JFileChooser j = new JFileChooser();
            if (j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    String chosenFilename = j.getSelectedFile().getAbsolutePath();
                    SessionSaveOD outputData = this.controller.save(this.session, chosenFilename);
                    JOptionPane.showMessageDialog(this, outputData.getMessage());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "An error occurred while saving session file. Please try again.");
                }
            }
        }
    }
}