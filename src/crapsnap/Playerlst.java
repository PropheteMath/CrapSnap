package crapsnap;


import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

import net.rim.device.api.ui.component.CheckboxField;

public class Playerlst implements PlayerListener{
	  private CheckboxField item;

	  public Playerlst(CheckboxField item) {
	    this.item = item;
	  }

	  public void playerUpdate(Player player, String event, Object eventData) {
	    if (event == (PlayerListener.VOLUME_CHANGED)) {
	      VolumeControl vc = (VolumeControl) eventData;
	    //  updateDisplay("Volume Changed to: " + vc.getLevel());
	      if (vc.getLevel() > 60) {
	    //    updateDisplay("Volume higher than 60 is too loud");
	        vc.setLevel(60);
	      }
	    } else if (event == (PlayerListener.STOPPED)) {
	    //  updateDisplay("Player paused at: " + (Long) eventData);
	    } else if (event == (PlayerListener.STARTED)) {
	    // updateDisplay("Player started at: " + (Long) eventData);
	    } else if (event == (PlayerListener.END_OF_MEDIA)) {
	      updateDisplay(true);
	    } else if (event == (PlayerListener.CLOSED)) {
	      updateDisplay(true);
	    } else if (event == (PlayerListener.ERROR)) {
	      updateDisplay(true);
	    }
	  }

	  public void updateDisplay(boolean bool) {
	    item.setChecked(bool);
	    //System.err.println(text);
	  }
	}