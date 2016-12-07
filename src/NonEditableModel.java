import javax.swing.table.DefaultTableModel;

public class NonEditableModel extends DefaultTableModel 
{
   private Object[][] mData;
   NonEditableModel(Object[][] data, String[] columnNames) 
   {
      super(data, columnNames);
      mData = data;
   }
   
   @Override
   public boolean isCellEditable(int row, int column) {
       return false;
   }
   
}
