package crapsnap;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

public class SplashScreen {

	private Timer t;
	UiApplication monApp;
	
	SplashScreen(UiApplication app){
		
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		monApp = app;
		synchronized (UiApplication.getEventLock()) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					try {
						Status.show("Loading...");
					} catch (Exception e) {
						Dialog.inform(e.getMessage());
					}
				}
			});
			t = new Timer();
			t.schedule(new Chronometer(), 0, 100);
		}
	}
	
	class Chronometer extends TimerTask {

		Chronometer() {

		}

		public void run() {
			synchronized (UiApplication.getEventLock()) {
				snapinit(monApp);
				t.cancel();
			}
		}
	}
		
		public void snapinit (UiApplication App){
			try{
			ContextStore storage = new ContextStore();
			ViewSnap _snap = new ViewSnap(storage.GetStoredValues()[1],
					storage.GetStoredValues()[0], App,"" );

			if (_snap.unable == true) {
				App.pushScreen(new Home(App,true));
			} else {
				App.pushScreen(_snap);
			}
		}catch (Exception E){
			App.pushScreen(new Home(App,false));
		}			
		}
}
