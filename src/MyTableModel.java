import javax.swing.table.DefaultTableModel;


/**
 * Classe per la gestione della tabella che riporta i risultati della ricerca
 * @author 
 *
 */
public class MyTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String[] columnNames = { "#", "Docnos", "Score", "Path"};

	public MyTableModel() {
		super();
	}

	public int getColumnCount() {
		return columnNames.length;
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	@Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
	
	
}
