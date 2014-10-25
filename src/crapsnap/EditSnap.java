package crapsnap;

import java.util.Date;
import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.system.PNGEncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

public class EditSnap extends MainScreen {

	private UiApplication monApp;
	private EncodedImage Img;
	private BitmapField bmpfld;
	private Bitmap bmpd;
	private Bitmap bmp;

	private boolean sended = false;
	private boolean _canceled = true;
	private boolean _drawing = false;
	private boolean touchable = Touchscreen.isSupported();

	private int time = 10;
	private Bitmap _0img = EncodedImage.getEncodedImageResource("clock.png")
			.getBitmap();
	private Bitmap _20img = EncodedImage.getEncodedImageResource("color.png")
			.getBitmap();
	private Bitmap _1img = EncodedImage.getEncodedImageResource("next.png")
			.getBitmap();
	private Bitmap _2img = EncodedImage.getEncodedImageResource("cross.png")
			.getBitmap();
	private Bitmap _10img = EncodedImage.getEncodedImageResource(
			"DrawFalse.png").getBitmap();
	private Bitmap _11img = EncodedImage
			.getEncodedImageResource("DrawTrue.png").getBitmap();
	
	String caption = "";

	EditSnap(UiApplication App, EncodedImage Image) {

		this.addMenuItem(menuitem1);
		this.addMenuItem(menuitem2);
		this.addMenuItem(menuitem3);
		this.addMenuItem(menuitem4);
		this.addMenuItem(menuitem5);
		this.addMenuItem(ColorMenu);
		this.addMenuItem(TimeDraw);

		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		monApp = App;
		Img = Image;
		bmpd = new Bitmap(Display.getWidth(), Display.getHeight());
		Bitmap tempbmp = Image.getBitmap();
		Graphics gd = new Graphics(bmpd);
		gd.fillRect(0, 0, Display.getWidth(), Display.getHeight());
		gd.drawBitmap(((Display.getWidth() / 2) - (tempbmp.getWidth() / 2)), 0,
				tempbmp.getWidth(), tempbmp.getHeight(), tempbmp, 0, 0);
		gd.drawBitmap(0, Display.getHeight() - 100, 100, 100, _0img, 0, 0);
		gd.drawBitmap(Display.getWidth() - 55, 5, 55, 50, _2img, 0, 0);
		gd.drawBitmap(55, 5, 60, 60, _20img, 0, 0);
		if (_drawing == false) {
			gd.drawBitmap(5, 5, 50, 50, _10img, 0, 0);
		} else {
			gd.drawBitmap(5, 5, 50, 50, _11img, 0, 0);
		}
		gd.drawBitmap(Display.getWidth() - 100, Display.getHeight() - 100, 100,
				100, _1img, 0, 0);
		bmp = new Bitmap(Display.getWidth(), Display.getHeight());
		Graphics g = new Graphics(bmp);
		g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
		g.drawBitmap(((Display.getWidth() / 2) - (tempbmp.getWidth() / 2)), 0,
				tempbmp.getWidth(), tempbmp.getHeight(), tempbmp, 0, 0);
		if (touchable == true) {
			bmpfld = new BitmapField(bmpd);
		} else {
			bmpfld = new BitmapField(bmp);
		}
		this.add(bmpfld);
	}

	private MenuItem menuitem1 = new MenuItem("Add text ", 110, 10) {
		public void run() {
			legacyAddText();
		}
	};

	boolean b = false;
	boolean r1 = false;
	boolean a = false;
	boolean z1 = false;
	boolean z2 = false;
	boolean e = false;
	boolean r2 = false;
	private boolean DrawBrazzerLogo = false;

	protected boolean keyUp(int keycode, int time) {
		if (keycode == 655426 && sended == false) {
			Done();
		} else if (b == false && keycode == 4325376) {
			b = true;
		} else if (r1 == false && keycode == 5373952) {
			r1 = true;
		} else if (a == false && keycode == 4259840) {
			a = true;
		} else if (z1 == false && keycode == 5898240) {
			z1 = true;
		} else if (z2 == false && keycode == 5898240) {
			z2 = true;
		} else if (e == false && keycode == 4521994) {
			e = true;
		} else if (r2 == false && keycode == 5373952) {
			DrawBrazzerLogo = true;
			b = false;
			r1 = false;
			a = false;
			z1 = false;
			z2 = false;
			e = false;
			r2 = false;
		} else {
			b = false;
			r1 = false;
			a = false;
			z1 = false;
			z2 = false;
			e = false;
			r2 = false;
		}
		return true;
	}

	private MenuItem menuitem2 = new MenuItem("Set time", 110, 10) {
		public void run() {
			SetTime();
		}
	};
	private MenuItem menuitem3 = new MenuItem("Erase", 110, 10) {
		public void run() {
			Erase();
		}
	};
	private MenuItem menuitem4 = new MenuItem("Send", 110, 10) {
		public void run() {
			Done();
		}
	};
	private MenuItem menuitem5 = new MenuItem("Cancel", 110, 10) {
		public void run() {
			Cancel();
		}
	};

	private int currentcolor = Color.WHITE;
	private MenuItem ColorMenu = new MenuItem("Color...", 110, 10) {
		public void run() {
			setcolor();
		}
	};

	private void setcolor() {
		Dialog ChooseColor = new Dialog("Choose a color : ", new String[] {
				"Black", "White", "Red", "Blue", "Green", "Yellow", "Cancel" },
				new int[] { 1, 2, 3, 4, 5, 6, 0 }, 0, null);
		int i = ChooseColor.doModal();
		if (i == 0)
			return;
		if (i == 1)
			currentcolor = Color.BLACK;
		if (i == 2)
			currentcolor = Color.WHITE;
		if (i == 3)
			currentcolor = Color.RED;
		if (i == 4)
			currentcolor = Color.BLUE;
		if (i == 5)
			currentcolor = Color.GREEN;
		if (i == 6)
			currentcolor = Color.YELLOW;
	}

	private MenuItem TimeDraw = new MenuItem("Draw time", 110, 10) {
		public void run() {
			if (touchable == false) {
				DrawTime(Display.getWidth() / 2, Display.getHeight() / 2);
			} else {
				hour = true;
				Dialog.inform("Now touch the screen to write it !");
			}
		}
	};

	private void DrawTime(int X, int Y) {
		Graphics gd = new Graphics(bmpd);
		Graphics g = new Graphics(bmp);
		Font gsavefong = g.getFont();
		Font myFont = g.getFont();
		g.setColor(currentcolor);
		gd.setColor(currentcolor);
		g.setFont(myFont.derive(Font.LATIN_SCRIPT, 15, Ui.UNITS_mm));
		gd.setFont(myFont.derive(Font.LATIN_SCRIPT, 15, Ui.UNITS_mm));
		Date current = new Date();
		String txt = current.toString().substring(11, 17);
		int size = g.getFont().getAdvance(txt);
		g.drawText(txt, X - size / 2,
				Y - Ui.convertSize(15, Ui.UNITS_mm, Ui.UNITS_px) / 2);
		gd.drawText(txt, X - size / 2,
				Y - Ui.convertSize(15, Ui.UNITS_mm, Ui.UNITS_px) / 2);
		g.setFont(gsavefong);
		gd.setFont(gsavefong);
		actualize();
		hour = false;
	}

	public EncodedImage GetImg() {
		JPEGEncodedImage _img = JPEGEncodedImage.encode(bmp, 80);
		return _img;
	}

	public int GetTime() {
		return time;
	}

	private void Done() {
		if (monApp.getActiveScreen() == this) {
			_canceled = false;
			monApp.popScreen(this);
			sended = true;
		}
	}

	private void Cancel() {
		_canceled = true;
		if (monApp.getActiveScreen() == this) {
			monApp.popScreen(this);
		}
	}

	public boolean IsCanceled() {
		return _canceled;
	}

	private PNGEncodedImage bmpdsave;
	private PNGEncodedImage bmpsave;
	private int X = -1;
	private int Y = -1;
	private boolean LegacyTxtMode = false;

	private void legacyAddText() {
		bmpdsave = PNGEncodedImage.encode(bmpd);
		bmpsave = PNGEncodedImage.encode(bmp);
		X = Display.getHeight() / 2;
		Y = Display.getWidth() / 2;
		AddText(0, X, "Move up or down your text !");
		LegacyTxtMode = true;
	}

	protected boolean trackwheelClick(int status, int time) {
		if (LegacyTxtMode == false) {
			legacyAddText();
		} else {
			LegacyTxtMode = false;
			bmpd = bmpdsave.getBitmap();
			bmp = bmpsave.getBitmap();
			AddText(Y, X, "");
		}
		return true;
	}

	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		if (LegacyTxtMode == true) {
			bmpd = bmpdsave.getBitmap();
			bmp = bmpsave.getBitmap();
			if (X + (10 * dy) > 0 && X + (10 * dy) < Display.getHeight()
					&& Y + (10 * dx) > 0 && Y + (10 * dx) < Display.getWidth()) {
				X = X + (10 * dy);
				Y = Y + (10 * dx);
				AddText(Y, X, "When ok, click the trackwheel !");
			}
		}
		return true;
	}

	BasicEditField inputField;

	private void AddText(int X, int Y, String text) {
		Graphics gd = new Graphics(bmpd);
		final Graphics g = new Graphics(bmp);
		try {
			int i;
			String txt = new String("");
			if (text.equals("") == true) {
				Dialog d = new Dialog(Dialog.D_OK_CANCEL,
						"Enter a text to add :", Dialog.OK, null,
						Dialog.DEFAULT_CLOSE);
				inputField = new BasicEditField();
				inputField.setChangeListener(new FieldChangeListener() {
					public void fieldChanged(Field arg0, int arg1) {
						if (g.getFont().getAdvance(inputField.getText()) > Display
								.getWidth() - 20) {
							inputField
									.setMaxSize(inputField.getText().length());
						} else {
							inputField.setMaxSize(200);
						}
					}
				});
				d.add(inputField);
				i = d.doModal();
				txt = inputField.getText();
				caption = txt;
			} else {
				txt = text;
				i = Dialog.OK;
			}
			Y = Y - 10;
			X = (Display.getWidth() / 2) - ((g.getFont().getAdvance(txt)) / 2);
			if (i == Dialog.OK) {
				g.setColor(Color.BLACK);
				g.setGlobalAlpha(128);
				gd.setColor(Color.BLACK);
				gd.setGlobalAlpha(128);
				g.fillRect(0, Y - 4, Display.getWidth(), 45);
				gd.fillRect(0, Y - 4, Display.getWidth(), 45);
				gd.setColor(currentcolor);
				gd.setGlobalAlpha(255);
				g.setColor(currentcolor);
				g.setGlobalAlpha(255);
				g.drawText(txt, X, Y);
				gd.drawText(txt, X, Y);
				gd.drawBitmap(55, 5, 50, 50, _20img, 0, 0);
				gd.drawBitmap(0, Display.getHeight() - 100, 100, 100, _0img, 0,
						0);
				gd.drawBitmap(Display.getWidth() - 55, 5, 55, 50, _2img, 0, 0);
				if (_drawing == false) {
					gd.drawBitmap(5, 5, 50, 50, _10img, 0, 0);
				} else {
					gd.drawBitmap(5, 5, 50, 50, _11img, 0, 0);
				}
				gd.drawBitmap(Display.getWidth() - 100,
						Display.getHeight() - 100, 100, 100, _1img, 0, 0);
				actualize();
			}
		} catch (Exception E) {

		}
	}

	private void actualize() {
		this.deleteAll();
		if (touchable == true) {
			bmpfld = new BitmapField(bmpd);
		} else {
			bmpfld = new BitmapField(bmp);
		}
		this.add(bmpfld);
	}

	private void SetTime() {
		try {
			BasicEditField inputField = new BasicEditField();
			Dialog d = new Dialog(Dialog.D_OK_CANCEL,
					"Time to Display this snap ( 1 to 10 s) : ", Dialog.OK, null,
					Dialog.DEFAULT_CLOSE);
			d.add(inputField);
			int i = d.doModal();
			if (i == Dialog.OK) {
				time = Integer.parseInt(inputField.getText());
			}
		} catch (Exception E) {

		}
	}

	Vector y = new Vector();
	Vector x = new Vector();

	private boolean hour = false;
	private DrawPointsVector _drawPointsVector = new DrawPointsVector(10);
	private DrawPoint _breakLinePoint = new DrawPoint(-1, -1);
	private int _lastX = -1;
	private int _lastY = -1;
	static int BACKGROUND_COLOR = 0x00E0E0E0;
	static int TRACE_COLOR = 0x00202020;
	static int CIRCLE_RADIUS = 7;
	static boolean FILL_MODE = true;
	private boolean _joinPoints = true;
	private boolean _useGestures = false;
	private boolean _useTouch = true;

	private long lasttouch = System.currentTimeMillis();

	protected boolean touchEvent(TouchEvent message) {

		long Dlasttouch = Math.abs(lasttouch - message.getTime());
		lasttouch = message.getTime();
		int eventType = message.getEvent();
		Graphics gd = new Graphics(bmpd);
		Graphics g = new Graphics(bmp);
		int touchX = message.getX(1);
		int touchY = message.getY(1);
		if (touchX == -1 && touchY == -1) {
			return true;
		} else if (touchY >= Display.getHeight() - 100
				&& touchX >= Display.getWidth() - 100 && Dlasttouch > 500) {
			Done();
		} else if (touchX >= Display.getWidth() - 55 && touchY <= 55
				&& Dlasttouch > 500) {
			Erase();
		} else if (touchX >= 55 && touchX <= 105 && touchY <= 55
				&& Dlasttouch > 500) {
			setcolor();
		} else if (touchX >= 0 && touchX <= 55 && touchY <= 55
				&& Dlasttouch > 500) {
			if (_drawing == true) {
				_drawing = false;
				hour = false;
				gd.drawBitmap(5, 5, 50, 50, _10img, 0, 0);
				this.deleteAll();
				bmpfld = new BitmapField(bmpd);
				this.add(bmpfld);
			} else {
				_drawing = true;
				hour = false;
				gd.drawBitmap(5, 5, 50, 50, _11img, 0, 0);
				this.deleteAll();
				if (touchable == true) {
					bmpfld = new BitmapField(bmpd);
				} else {
					bmpfld = new BitmapField(bmp);
				}
				this.add(bmpfld);
			}
		} else if (touchY >= Display.getHeight() - 100 && touchX <= 100
				&& Dlasttouch > 500) {
			SetTime();
		} else {
			if (DrawBrazzerLogo == true) {
				DrawBrazzer(touchX, touchY);
				DrawBrazzerLogo = false;
			} else if (hour == true) {
				DrawTime(touchX, touchY);
				hour = false;
			} else if (_drawing == true) {
				if (_useTouch) {
					if (eventType == TouchEvent.DOWN
							|| eventType == TouchEvent.CLICK) {
						// We have started a new line
						_drawPointsVector.addDrawItem(_breakLinePoint);
						_drawPointsVector.addDrawItem(new DrawPoint(message
								.getX(1), message.getY(1)));
						invalidate();
					} else if (eventType == TouchEvent.CANCEL
							|| eventType == TouchEvent.UP
							|| eventType == TouchEvent.UNCLICK) {
						// We have stopped a new line
						_drawPointsVector.addDrawItem(_breakLinePoint);
						// Don't bother invalidating, there is actually
						// nothing to draw...
					} else if (eventType == TouchEvent.MOVE) {
						// Get the move points
						int pointsSize = message.getMovePointsSize();
						int[] xPoints = new int[pointsSize];
						int[] yPoints = new int[pointsSize];
						message.getMovePoints(1, xPoints, yPoints, null);
						_drawPointsVector.addDrawItem(new DrawPath(xPoints,
								yPoints));
						invalidate();
					}
				}
				if (_useGestures) {
					if (eventType == TouchEvent.GESTURE) { // Gesture event
															// fired
						TouchGesture gesture = message.getGesture();
						int gestureEventType = gesture.getEvent();
						if (gestureEventType == TouchGesture.TAP) {
							// Since we have a tap only draw a single point
							_drawPointsVector.addDrawItem(new DrawPoint(message
									.getX(1), message.getY(1)));
							invalidate();
						} else if (gestureEventType == TouchGesture.SWIPE) { // Swipe
																				// Gesture
							// Get the move points
							int pointsSize = message.getMovePointsSize();
							int[] xPoints = new int[pointsSize];
							int[] yPoints = new int[pointsSize];
							message.getMovePoints(1, xPoints, yPoints, null);
							_drawPointsVector.addDrawItem(new DrawPath(xPoints,
									yPoints));
							invalidate();
						}
					}
				}
				paintthis();
				gd.drawBitmap(0, Display.getHeight() - 100, 100, 100, _0img, 0,
						0);
				gd.drawBitmap(Display.getWidth() - 55, 5, 55, 50, _2img, 0, 0);
				if (_drawing == false) {
					gd.drawBitmap(5, 5, 50, 50, _10img, 0, 0);
				} else {
					gd.drawBitmap(5, 5, 50, 50, _11img, 0, 0);
				}
				gd.drawBitmap(55, 5, 50, 50, _20img, 0, 0);
				gd.drawBitmap(Display.getWidth() - 100,
						Display.getHeight() - 100, 100, 100, _1img, 0, 0);
			} else if (Dlasttouch > 500) {
				AddText(touchX, touchY, "");
			}
		}
		return true;
	}

	private void DrawBrazzer(int touchX, int touchY) {
		Graphics gd = new Graphics(bmpd);
		Graphics g = new Graphics(bmp);
		EncodedImage logo = EncodedImage
				.getEncodedImageResource("Brazzers-logo.png");
		int y = (touchY - logo.getHeight() / 2);
		int x = (touchX - logo.getWidth() / 2);
		gd.drawBitmap(x, y, logo.getWidth(), logo.getHeight(),
				logo.getBitmap(), 0, 0);
		g.drawBitmap(x, y, logo.getWidth(), logo.getHeight(), logo.getBitmap(),
				0, 0);
	}

	private void Erase() {
		Graphics gd = new Graphics(bmpd);
		Graphics g = new Graphics(bmp);
		this.deleteAll();
		bmpd = new Bitmap(Display.getWidth(), Display.getHeight());
		Bitmap tempbmp = Img.getBitmap();
		gd = new Graphics(bmpd);
		gd.fillRect(0, 0, Display.getWidth(), Display.getHeight());
		gd.drawBitmap(((Display.getWidth() / 2) - (tempbmp.getWidth() / 2)), 0,
				tempbmp.getWidth(), tempbmp.getHeight(), tempbmp, 0, 0);
		gd.drawBitmap(0, Display.getHeight() - 100, 100, 100, _0img, 0, 0);
		gd.drawBitmap(Display.getWidth() - 55, 5, 55, 50, _2img, 0, 0);
		if (_drawing == false) {
			gd.drawBitmap(5, 5, 50, 50, _10img, 0, 0);
		} else {
			gd.drawBitmap(5, 5, 50, 50, _11img, 0, 0);
		}
		gd.drawBitmap(55, 5, 50, 50, _20img, 0, 0);
		gd.drawBitmap(Display.getWidth() - 100, Display.getHeight() - 100, 100,
				100, _1img, 0, 0);
		bmp = new Bitmap(Display.getWidth(), Display.getHeight());
		g = new Graphics(bmp);
		g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
		g.drawBitmap(((Display.getWidth() / 2) - (tempbmp.getWidth() / 2)), 0,
				tempbmp.getWidth(), tempbmp.getHeight(), tempbmp, 0, 0);
		actualize();
	}

	void paintthis() {
		Graphics gd = new Graphics(bmpd);
		Graphics g = new Graphics(bmp);
		Object drawObject;
		DrawPoint paintPoint;
		DrawPath paintPath;
		while ((drawObject = _drawPointsVector.getNext()) != null) {
			if (drawObject instanceof DrawPoint) {
				paintPoint = (DrawPoint) drawObject;
				paintPoint.paint(_lastX, _lastY, gd, currentcolor);
				paintPoint.paint(_lastX, _lastY, g, currentcolor);
				if (_joinPoints) {
					_lastX = paintPoint.getLastX();
					_lastY = paintPoint.getLastY();
				} else {
					_lastX = -1;
					_lastY = -1;
				}
			} else if (drawObject instanceof DrawPath) {
				paintPath = (DrawPath) drawObject;
				paintPath.paint(_lastX, _lastY, gd, currentcolor);
				paintPath.paint(_lastX, _lastY, g, currentcolor);
				_lastX = -1;
				_lastY = -1;
			}
		}
		actualize();
	}
}