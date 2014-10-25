package crapsnap;

import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;

/**
 * This class extends the UiApplication class, providing a graphical user
 * interface.
 */
public class App extends UiApplication {

	/**
	 * Flag for replied messages. The lower 16 bits are RIM-reserved, so we have
	 * to use higher 16 bits.
	 */
	static final int FLAG_REPLIED = 1 << 16;

	/**
	 * Flag for deleted messages. The lower 16 bits are RIM-reserved, so we have
	 * to use higher 16 bits.
	 */
	static final int FLAG_DELETED = 1 << 17;
	static final int BASE_STATUS = ApplicationMessage.Status.INCOMING;
	static final int STATUS_NEW = BASE_STATUS
			| ApplicationMessage.Status.UNOPENED;
	static final int STATUS_OPENED = BASE_STATUS
			| ApplicationMessage.Status.OPENED;
	static final int STATUS_REPLIED = BASE_STATUS
			| ApplicationMessage.Status.OPENED | FLAG_REPLIED;
	static final int STATUS_DELETED = BASE_STATUS | FLAG_DELETED;

	/**
	 * Entry point for application
	 * 
	 * @param args
	 *            Command line arguments (not used)
	 */

	public static void main(String[] args) {

		App theApp = new App();
		theApp.enterEventDispatcher();
	}

	public App() {
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		SplashScreen splscr = new SplashScreen(this);
	}

	public void eventOccurred(long guid, int count, int size, Object object0,
			Object object1) {
		if (0x5a9f7caa171ab7b8L == guid) {
			return;
		}
	}
}
