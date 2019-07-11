package pub.ayada.exeSqls.xmlhandler.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
 
<colformat>
	<variable>
	   ...
	</variable>   
	<variable>
	   ...
	</variable>		 
</colformat> 
*/
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = { "variable" })
@XmlRootElement(name = "colformat")
public class VarList {
	
	@XmlElement(name = "variable")
	protected ArrayList<Variable> variable = new ArrayList<Variable>();

	public  VarList() {}

	public ArrayList<Variable> getvariable() {
		return this.variable;
	}

	public void setvariable(ArrayList<Variable> col) {
		this.variable = col;
	}
	
/*	
	public HashMap<String, String> getVarMap() {
		HashMap<String, String> resMap = new HashMap<String, String>(this.variable.size());

		for (Variable var : this.variable)
			resMap.put(var.getName(), var.g);

		return resMap;

	}
	
*/	
}
