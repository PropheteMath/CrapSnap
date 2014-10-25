package crapsnap;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class FriendSelectorObject extends HorizontalFieldManager{

	private String _Name = new String();
	private LabelField LabelName = new LabelField();
	private CheckboxField CheckBox = new CheckboxField();
	
	
	FriendSelectorObject(String Name)
	{
		_Name = Name;
		LabelName = new LabelField(Name,Field.FIELD_VCENTER);
		this.add(new LabelField("  "));
		CheckBox.setLabel(Name);
		this.add(CheckBox);
		this.add(LabelName);
		
	}
	
	public boolean IsSelected(){
		return CheckBox.getChecked();
	}
	
	public String GetName(){
		
		return CheckBox.getLabel();
	}
	
}
