package use_cases.add_edit_category_use_case;

import entities.Category;
import entities.SessionStorage;

public class CategoryID {
    private String name;
    private Object value;
    private final int monthID;

    private final SessionStorage session;
    private Category old_category;

    /**
     * Constructs Category_Input_Data for adding a new Category Object.
     * @param name Category name
     * @param value Category budget
     * @param monthID An int representing the month which the Category Object belongs to.
     * @param session The current session which the MonthlyStorage Object belongs to.
     */

    public CategoryID(String name, Object value, int monthID, SessionStorage session){
        this.name = name;
        this.value = value;
        this.monthID = monthID;
        this.session = session;
    }

    /**
     * Constructs Category_Inout_Data for editing an existing Category Object.
     * @param name Category name
     * @param value Category budget
     * @param monthID An int representing the month which the Category Object belongs to.
     * @param session The current session which the MonthlyStorage Object belongs to.
     * @param old_category An existing Category in the MonthlyStorage Object the user wish to edit.
     */
    public CategoryID(String name, Object value, int monthID, SessionStorage session, Category old_category) {
        this.name = name;
        this.value = value;
        this.monthID = monthID;
        this.session = session;
        this.old_category = old_category;
    }
    public String getName(){return name;}

    /**
     * Casts @param value from Object to double, and gets category budget.
     * @return Category budget in type double.
     */
    public Object getValue(){return value;}
    public void setValue(double value){this.value = value;}

    public int getMonthID(){return monthID;}
    public SessionStorage getSession(){return session;}
    public Category getOld_category(){return old_category;}

}
