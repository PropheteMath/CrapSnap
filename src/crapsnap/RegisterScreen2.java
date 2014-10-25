package crapsnap;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class RegisterScreen2 extends NoPromtMainScreen {

	private UiApplication _App;
	private LocalButon OkBtn = new LocalButon("Next");
	
	public boolean returntouch = true;
	
	item item0;
	item item1;
	item item2;
	item item3;
	item item4;
	item item5;
	item item6;
	item item7;
	item item8;

	public String solution;

	RegisterScreen2(UiApplication app, EncodedImage[] imgtab) {
		super(Manager.USE_ALL_WIDTH );
		this.setTitle("Registration (2/3)");
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		_App = app;
		this.add(new LabelField("Select ALL images containing a ghost :"));

		VerticalFieldManager monManager = (VerticalFieldManager) getMainManager();
		Bitmap back = EncodedImage.getEncodedImageResource("yBack.jpg").getBitmap();
		monManager.setBackground(BackgroundFactory.createBitmapBackground(back));

		
		item0 = new item(imgtab[0]);
		item1 = new item(imgtab[1]);
		item2 = new item(imgtab[2]);
		item3 = new item(imgtab[3]);
		item4 = new item(imgtab[4]);
		item5 = new item(imgtab[5]);
		item6 = new item(imgtab[6]);
		item7 = new item(imgtab[7]);
		item8 = new item(imgtab[8]);

		this.add(new SeparatorField());
		this.add(item0);
		this.add(new SeparatorField());
		this.add(item1);
		this.add(new SeparatorField());
		this.add(item2);
		this.add(new SeparatorField());
		this.add(item3);
		this.add(new SeparatorField());
		this.add(item4);
		this.add(new SeparatorField());
		this.add(item5);
		this.add(new SeparatorField());
		this.add(item6);
		this.add(new SeparatorField());
		this.add(item7);
		this.add(new SeparatorField());
		this.add(item8);
		this.add(new SeparatorField());


		OkBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				_App.popScreen();
				returntouch = false;
				solution = "" + item0.IsChecked() + item1.IsChecked()
						+ item2.IsChecked() + item3.IsChecked()
						+ item4.IsChecked() + item5.IsChecked()
						+ item6.IsChecked() + item7.IsChecked()
						+ item8.IsChecked();
			}
		});
		this.add(OkBtn);

	}

	public boolean onSavePrompt() {
		int i = Dialog.ask(Dialog.D_YES_NO, "Do you really want to abort the registration ?");
		if (i == Dialog.YES) return true;
		if (i == Dialog.NO) return false;
		return false;
	}
	
	private class item extends VerticalFieldManager {

		private CheckboxField box = new CheckboxField("", false,CheckboxField.FIELD_VCENTER);

		item(EncodedImage img) {
			super(Manager.FIELD_HCENTER);
			HorizontalFieldManager Hmf = new HorizontalFieldManager(Field.FIELD_HCENTER);
			Hmf.add(new BitmapField(img.getBitmap(),Field.FIELD_VCENTER));
			Hmf.add(box);
			this.add(Hmf);
		}

		public int IsChecked() {
			if (box.getChecked() == true) {
				return 1;
			} else {
				return 0;
			}
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
			return (45);
		}

	}
	

}
