package crapsnap;

import net.rim.device.api.ui.container.MainScreen;

public class NoPromtMainScreen extends MainScreen{
	
	NoPromtMainScreen(long set){
		super (set);
	}
	public boolean onSavePrompt(){
		return true;
	}
}
