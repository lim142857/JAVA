package Model;

import java.util.ArrayList;

/**
 * Stores all the table and can search the Table by giving table number
 */
public class TableManager {
    private ArrayList<Table> tables ;
    // the list of all tables

    public TableManager(){
        this.tables = new ArrayList<>();
    }

  /**
   * add table to the list
   * @param table the table needed to be added
   */
    public void addTable(Table table){ tables.add(table); }


    /**
   * get Table by given table number
   * @param tableNumber the table number
   * @return Table matches the given table number
   */
    public Table getTable(int tableNumber){
        for (Table table : tables){
            if (table.getTableNumber() == tableNumber) return table;
        }
        return null;
    }
}