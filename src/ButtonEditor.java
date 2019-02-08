import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;


/**
 * Classe che si occupa di gestire i bottoni presenti della tabella dei risultati,
 * viene creato un bottone per ogni riga della tabella in modo da permettere l'apertura in modo rapido
 * dei file che vengono recuperati dal sistema di ricerca
 * @author 
 *
 */
class ButtonEditor extends DefaultCellEditor {
		/**
		* 
		*/
		private static final long serialVersionUID = 1L;

		protected JButton button;
		private String label;
		private boolean isPushed;
		private File file;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener(){  
			    public void actionPerformed(ActionEvent e){   
			    	fireEditingStopped();
			    }  
		    });  
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			label = value.toString();
			isPushed = true;
			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				file = new File(label.toString());
				if(file.exists()) {
					try {
						Desktop.getDesktop().open(new File(label.toString()));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null,	"File not found", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else {
					System.out.println(label.toString());
					JOptionPane.showMessageDialog(null,	"File not found", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			isPushed = false;
			return new String(label);
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}