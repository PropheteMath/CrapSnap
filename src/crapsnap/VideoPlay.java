package crapsnap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

public class VideoPlay extends MainScreen {
	UiApplication _MonApp;
	String File = "";
	Player player;
	CheckboxField item = new CheckboxField();
	private boolean _state;
	public boolean isscrshtd;
	byte[] _video;
	boolean error = false;

	public VideoPlay(byte[] video, UiApplication MonApp) {
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		_video = video;
		_MonApp = MonApp;
		try {
			this.addMenuItem(_ScreenShootitem);
			ByteArrayInputStream is = new ByteArrayInputStream(video);
			player = javax.microedition.media.Manager.createPlayer(is,
					"video/mp4");
			player.realize();
			player.prefetch();
			player.addPlayerListener(new Playerlst(item));
			VideoControl videoControl = (VideoControl) player
					.getControl("VideoControl");
			Field videoField = (Field) videoControl.initDisplayMode(
					VideoControl.USE_GUI_PRIMITIVE,
					"net.rim.device.api.ui.Field");
			add(videoField);
			VolumeControl volume = (VolumeControl) player
					.getControl("VolumeControl");
			volume.setLevel(30);
			player.start();
			item.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					close();
				}
			});

		} catch (MediaException me) {
			error = true;
			Dialog.alert("Error while opening the snap :" + me.toString());
			return;
		} catch (IOException ioe) {
			error = true;
			Dialog.alert(ioe.toString());
			return;
		}

	}

	public void close() {
		player.close();
		try{
					_MonApp.popScreen(_MonApp.getActiveScreen());
		} catch (Exception e){
					_MonApp.popScreen(_MonApp.getActiveScreen());
		}
	}

	private MenuItem _ScreenShootitem = new MenuItem("Save this ! ", 110, 10) {
		public void run() {
			ScreenShoot();
		}
	};

	protected boolean touchEvent(TouchEvent message) {
		if (_state == false) {
			ScreenShoot();
			try {
				if (new ContextStore().GetStoredValues()[2].equals("true"))
					isscrshtd = true;
			} catch (Exception e) {
				isscrshtd = true;
			}

			_state = true;
			Dialog.inform("ScreenShot : Video Saved !");
		}
		return true;
	}

	private void ScreenShoot() {
		try {
			FileConnection fconn = null;
			OutputStream out = null;
			Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
			fconn = (FileConnection) Connector.open(
					"file:///store/home/user/pictures/Snapcap"
							+ TimeStamp.toString() + " .mpg",
					Connector.READ_WRITE);
			fconn.create();
			out = fconn.openOutputStream();
			out.write(_video);
			out.flush();
			fconn.close();
		} catch (Exception e) {
			Dialog.inform(e.getMessage());
		}
	}

}
