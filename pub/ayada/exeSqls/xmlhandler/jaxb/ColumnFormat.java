package pub.ayada.exeSqls.xmlhandler.jaxb;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
 
<colformat>
	<column>
	   ...
	</column>   
	<column>
	   ...
	</column>		 
</colformat> 
*/

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = { "column" })
@XmlRootElement(name = "colformat")
public class ColumnFormat {
	@XmlElement(name = "column")
	protected ArrayList<Column> column = new ArrayList<Column>();

	public ColumnFormat() {
	}

	public ArrayList<Column> getColumn() {
		return this.column;
	}

	public void setColumn(ArrayList<Column> col) {
		this.column = col;
	}

	public HashMap<String, String[]> getColFmtMap() {
		HashMap<String, String[]> resMap = new HashMap<String, String[]>(this.column.size());

		for (Column col : this.column) {
			String[] vals = new String[2];
			if (col.getReptcolhdr() == null || col.getReptcolhdr().isEmpty())
				vals[0] = col.getName();
			else 
				vals[0] = col.getReptcolhdr();
			
			vals[1] = col.getFormat();
			resMap.put(col.getName(), vals);
			 
		}  
		return resMap;

	}
}
