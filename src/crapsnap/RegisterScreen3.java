package crapsnap;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.AbsoluteFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class RegisterScreen3 extends NoPromtMainScreen{
	public String username="";
	private UiApplication _App;
	
	public boolean returntouch = true;
	
	private EditField usernamefield = new EditField("  Username : ", "", 15,Field.NON_SPELLCHECKABLE);
	private LocalButon OkBtn = new LocalButon("Next");
	
	RegisterScreen3(UiApplication app){
		
		super(NO_VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR);
		this.setTitle("Registration (3/3)");
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		_App = app;	
		int g = usernamefield.getPreferredHeight() * 1 + new SeparatorField().getPreferredHeight() * 1;
		VerticalFieldManager monManager = (VerticalFieldManager) getMainManager();
		Bitmap back = EncodedImage.getEncodedImageResource("yBack.jpg").getBitmap();
		monManager.setBackground(BackgroundFactory.createBitmapBackground(back));
		
		AbsoluteFieldManager Vodka = new AbsoluteFieldManager();
		
		VerticalFieldManager LoginViewManager = new VerticalFieldManager();
		LoginViewManager.add(usernamefield);
		LoginViewManager.add(new SeparatorField());
		Vodka.add(LoginViewManager, 0, (Display.getHeight() / 2) - 85);
		OkBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				_App.popScreen();
				returntouch = false;
				username = usernamefield.getText();
			}
		});
		VerticalFieldManager BtnManager = new VerticalFieldManager();
		Vodka.add(BtnManager, 0, (Display.getHeight() / 2) - g / 2 - 45 /2);
		Vodka.add(OkBtn,5,Display.getHeight() -105);
		monManager.add(Vodka);
	
	}
	
	public boolean onSavePrompt() {
		int i = Dialog.ask(Dialog.D_YES_NO,"Do you really want to abort the registration ?");
		if (i == Dialog.YES) return true;
		if (i == Dialog.NO) return false;
		return false;
	}
	
	private class LocalButon extends ButtonField {

		public LocalButon(String nom) {
			super(nom);
		}

		public int getPreferredWidth() {
			return (Display.getWidth() - 45);
		}
		public int getPreferredHeight() {
			return (45);
		}

	}
	
}
