package crapsnap;


import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.ButtonField;

public class SplitButon extends ButtonField{
	int _split = 1;
	
	public SplitButon(String nom, int split) {
		super(nom);
		_split = split;
    }
    
    public int getPreferredWidth() {
        return (Display.getWidth()/_split - _split * 10);
    }

}