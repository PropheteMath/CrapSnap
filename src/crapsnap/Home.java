package crapsnap;

import org.json.me.JSONObject;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.AbsoluteFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class Home extends NoPromtMainScreen {
	JSONObject CurrentObj = new JSONObject();
	ViewSnap viewsnap;
	EditField LoginField = new EditField("  Login : ", "", 100,
			Field.NON_SPELLCHECKABLE);
	PasswordEditField PasswordField = new PasswordEditField("  Password : ",
			"", 100, Field.NON_SPELLCHECKABLE);
	ContextStore storage = new ContextStore();
	UiApplication monApp;

	String screenshoot = "true";

	public Home(UiApplication app, boolean unabletologin) {

		super(NO_VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR);

		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		try {
			LoginField.setText(storage.GetStoredValues()[0]);
			PasswordField.setText(storage.GetStoredValues()[1]);
			screenshoot = (String) storage.GetStoredValues()[2];
		} catch (Exception e) {

		}

		this.addMenuItem(Help);
		
		VerticalFieldManager monManager = (VerticalFieldManager) getMainManager();
		Bitmap back = EncodedImage.getEncodedImageResource("yBack.jpg")
				.getBitmap();
		monManager
				.setBackground(BackgroundFactory.createBitmapBackground(back));
		this.setTitle(" CrapSnap");
		monApp = app;
		AbsoluteFieldManager Vodka = new AbsoluteFieldManager();

		// définition de la vue de login
		LabelField MsgField1 = new LabelField("Welcome in CrapSnap !",
				FIELD_HCENTER);
		LabelField MsgField2 = new LabelField("Please log in or register :",
				FIELD_HCENTER);
		VerticalFieldManager MsgManager = new VerticalFieldManager(
				Field.USE_ALL_WIDTH);
		MsgManager.add(MsgField1);
		MsgManager.add(MsgField2);
		Graphics g = new Graphics(new Bitmap(10, 10));

		int corrector = 0;
		if (Display.getHeight() <= 360) {
			corrector = -70;
		}
		Vodka.add(MsgManager, (Display.getWidth() / 2)
				- (g.getFont().getAdvance("Please log in or register :") / 2),
				5);
		VerticalFieldManager LoginViewManager = new VerticalFieldManager();
		LoginViewManager.add(LoginField);
		LoginViewManager.add(new SeparatorField());
		LoginViewManager.add(PasswordField);
		LoginViewManager.add(new SeparatorField());

		int size = (new SeparatorField().getPreferredHeight() * 2 + LoginField
				.getPreferredHeight() * 2) / 2;
		Vodka.add(LoginViewManager, 0, (Display.getHeight() / 2) - size
				+ corrector);

		LocalButon LogInBtn = new LocalButon("Log In");
		LocalButon RegisterBtn = new LocalButon("Register");
		VerticalFieldManager BtnManager = new VerticalFieldManager();
		BtnManager.add(LogInBtn);
		BtnManager.add(RegisterBtn);// Un jour surement :p
		Vodka.add(BtnManager, 0, Display.getHeight() - 145); // penser à
																// remonter ça
																// pour ajouter
																// register
		if (unabletologin)
			Vodka.add(
					new LabelField("Unable to connect to SnapChat servers."),
					Display.getWidth()
							/ 2
							- g.getFont().getAdvance(
									"Unable to connect to SnapChat servers.")
							/ 2, (Display.getHeight() / 2) + size / 2 + 20
							+ corrector);
		monManager.add(Vodka);

		int dispo = (((Display.getHeight() / 2) - (size / 2)) - (MsgField1
				.getPreferredHeight() * 2));
		int Willbeused = dispo - 50;
		if (dispo > 142) {
			EncodedImage eImage = EncodedImage
					.getEncodedImageResource("Snapchat_logo.png");
			int currentHeightFixed32 = Fixed32.toFP(320);
			int requiredHeightFixed32 = Fixed32.toFP(Willbeused);
			int scaleFixed32 = Fixed32.div(currentHeightFixed32,
					requiredHeightFixed32);
			eImage = eImage.scaleImage32(scaleFixed32, scaleFixed32);
			BitmapField bmpfld = new BitmapField(eImage.getBitmap());
			Vodka.add(bmpfld, Display.getWidth() / 2 - Willbeused / 2,
					MsgField1.getPreferredHeight() * 2 + 25);
		}

		// Procédure de connection
		LogInBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				Status.show("Connecting...");
				connect();
			}
		});

		// Procédure d'inscription
		RegisterBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				// monApp.popScreen();
				Register reg = new Register(monApp);
				reg.Do();
			}
		});
	}
	
	private MenuItem Help = new MenuItem("Need Help ?", 110, 10) {
		public void run() {
			Dialog dial = new Dialog(Dialog.OK, "Looking for help ? Want to give us your feedback ?", Dialog.OK, Bitmap.getPredefinedBitmap(Bitmap.QUESTION), 0);
			 ActiveRichTextField txtact = new  ActiveRichTextField("Do not hesitate to contact us at : crap.snap@laposte.net", Field.FIELD_HCENTER);
			 dial.add(txtact);
			 dial.show();
		}
	};

	private void connect() {
		if (CurrentObj != null) {
			NotifyContextStore notify = new NotifyContextStore();
			try{
				if (notify.GetStoredValues() < 0){
					notify.StoreValues(300);
				}
			} catch (Exception e){
				notify.StoreValues(300);
			}
			ViewSnap _snap = new ViewSnap(PasswordField.getText(),
					LoginField.getText(), monApp, "");
			if (_snap.unable == true) {
				try {
					Dialog.inform("Unable to Login : "
							+ _snap.Current.getString("message"));
				} catch (Exception e) {
					Dialog.inform("Unable to Login");
				}
				return;
			} else {
				String[] userinfo = { LoginField.getText(),
						PasswordField.getText(), screenshoot, "" };
				storage.StoreValues(userinfo);
			}
			monApp.popScreen();
			monApp.pushScreen(_snap);
			
		}
	}

	private class LocalButon extends ButtonField {

		public LocalButon(String nom) {
			super(nom);
		}

		public int getPreferredWidth() {
			return (Display.getWidth() - 40);
		}

		public int getPreferredHeight() {
			return (35);
		}
	}

}
