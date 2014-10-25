package crapsnap;

import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import org.json.me.JSONException;
import org.json.me.JSONObject;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

public class DisplaySnap extends MainScreen{
	private Timer t;
	private BitmapField bmp;
	private UiApplication monApp;
	private Bitmap _img;
	private EncodedImage _original;
	private JSONObject _Current;
	public boolean isscrshtd = false;
	private String _key;
	
	private boolean _state = false;
	
	public boolean GetState()
	{
		return _state;
	}
	
	DisplaySnap(EncodedImage original, Bitmap img, int time, UiApplication app, JSONObject Current, String key) {
		_original = original;
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		monApp = app;
		_img = img;
		_Current = Current;
		_key = key;
		
		// afficher l'image.
		
		this.addMenuItem(_ScreenShootitem);
		
		HorizontalFieldManager SnapManager = new HorizontalFieldManager();
		bmp = new BitmapField();
		SnapManager.add(bmp);
		this.add(SnapManager);
		
		t = new Timer();
		t.schedule(new Chronometer(time, img), 0, 1000);
	}

	  private MenuItem _ScreenShootitem = new MenuItem("Save this ! ", 110, 10)
	     {
	         public void run()
	         {
	        	 ScreenShoot();
	         }
	     };
	
	class Chronometer extends TimerTask {
		int _time = 0;
		Bitmap _img;
		Chronometer(int time, Bitmap img) {
			_time = time;
			_img = img;
		}

		public void run() {
			synchronized (UiApplication.getEventLock()) {
				if (_time > 0) {
					Bitmap _bmp = new Bitmap(Display.getWidth(),
							Display.getHeight());
					Graphics g = new Graphics(_bmp);
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
					g.drawBitmap(
							((_bmp.getWidth() / 2) - (_img.getWidth() / 2)), 0,
							_img.getWidth(), _img.getHeight(), _img, 0, 0);
					g.setColor(Color.WHITE);
					g.drawText(_time + "s", Display.getWidth() - 60,
							Display.getHeight() - 50);
					bmp.setBitmap(_bmp);
					_time--;				
				} else {
					t.cancel();
					quit();
								
				}
			}
		}
	}
		
	protected boolean touchEvent(TouchEvent message) {
		if (_state ==false)
		{
			_state = true;
			ScreenShoot();
			try{
				if (new ContextStore().GetStoredValues()[2].equals("true"))
				isscrshtd = true;
			} catch (Exception e){
				isscrshtd = true;
			}
        	Dialog.inform("ScreenShot : Snap Saved !");
		}
        return true;
    }
	
	private void ScreenShoot(){
		try {
			FileConnection fconn = null;
			OutputStream out = null;
			Long TimeStamp = new Long(System.currentTimeMillis() / 1000L);
			fconn = (FileConnection) Connector.open("file:///store/home/user/pictures/Snapcap" + TimeStamp.toString()+ ".jpg",
					Connector.READ_WRITE);
			fconn.create();
			out = fconn.openOutputStream();
			out.write(_original.getData());
			out.flush();
			fconn.close();
		} catch (Exception e) {
				Dialog.inform(e.getMessage());
		}

	}
	
	private void quit(){
		if (monApp.getActiveScreen() == this)
		{
			monApp.popScreen();	
		}
	}
	
	
}
