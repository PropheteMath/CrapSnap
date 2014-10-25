package crapsnap;

import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
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
import net.rim.device.api.ui.decor.BackgroundFactory;

public class DisplayStory extends MainScreen{
	private Timer t;
	private BitmapField bmp;
	private UiApplication monApp;
	private Bitmap _img;
	private EncodedImage _original;
	
	DisplayStory(EncodedImage original, Bitmap img, UiApplication app, int time) {
		_original = original; 
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);

		monApp = app;
		_img = img;
		
		//this.setTitle(Caption);
		this.addMenuItem(_ScreenShootitem);
		this.addMenuItem(_Nextstoryitem);
		this.addMenuItem(_EndStoryitem);
		// afficher l'image.
		HorizontalFieldManager SnapManager = new HorizontalFieldManager();
		bmp = new BitmapField();
		SnapManager.add(bmp);
		this.add(SnapManager);
		
		t = new Timer();
		t.schedule(new Chronometer(time, img), 0, 1000);
	}

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
	
	  private MenuItem _ScreenShootitem = new MenuItem("Save this ! ", 110, 10)
	     {
	         public void run()
	         {
	        	 ScreenShoot();
	         }
	     };
	private boolean statut = false;
	
	private MenuItem _Nextstoryitem = new MenuItem("Next", 110, 10)
    {
        public void run()
        {
         quit();
        }
    };
	public boolean exited = false;
	private MenuItem _EndStoryitem = new MenuItem("Exit story", 110, 10)
	    {
	        public void run()
	        {
	         exited = true;
	         quit();
	        }
	    };
	
	private void quit(){
		if (monApp.getActiveScreen() == this)
		{
			monApp.popScreen();	
		}
	}
	
	protected boolean touchEvent(TouchEvent message) {
		{
			if (statut  == false){
				statut = true;
				ScreenShoot();
				Dialog.inform("ScreenShot : Snap Saved !");
			}
			
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
}
