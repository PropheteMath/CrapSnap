package crapsnap;

import java.util.Calendar;
import java.util.Date;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.AbsoluteFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.text.TextFilter;

public class RegisterScreen1 extends MainScreen{

	public int age = 0;
	public String pwd ="";
	public String birthday="";
	public String email="";
	private UiApplication _App;
	
	public boolean returntouch = true;
	
	private EditField Email = new EditField("  Email : ", "", 100,Field.NON_SPELLCHECKABLE);
	private PasswordEditField PasswordField = new PasswordEditField("  Password : ","", 100, Field.NON_SPELLCHECKABLE);
	private PasswordEditField PasswordField2 = new PasswordEditField("  Confirm password : ","", 100, Field.NON_SPELLCHECKABLE);
	private EditField Birthday = new EditField("  Birthday (yyyy-mm-dd) : ", "", 10,Field.NON_SPELLCHECKABLE);
	private CheckboxField Phone = new CheckboxField("  Attach my phone number : " + net.rim.blackberry.api.phone.Phone.getDevicePhoneNumber(false), false);
	private LocalButon OkBtn = new LocalButon("Next");
	public String phone = null;
	
	RegisterScreen1(UiApplication app){
		
		super(NO_VERTICAL_SCROLL | NO_VERTICAL_SCROLLBAR);
		this.setTitle("Registration (1/3)");
		int direction = Display.DIRECTION_NORTH;
		Ui.getUiEngineInstance().setAcceptableDirections(direction);
		_App = app;	
		Email.setFilter(TextFilter.get(TextFilter.EMAIL));
		
		VerticalFieldManager monManager = (VerticalFieldManager) getMainManager();
		Bitmap back = EncodedImage.getEncodedImageResource("yBack.jpg").getBitmap();
		monManager.setBackground(BackgroundFactory.createBitmapBackground(back));
		
		AbsoluteFieldManager Vodka = new AbsoluteFieldManager();
		
		VerticalFieldManager LoginViewManager = new VerticalFieldManager();
		int g = 0;
		LoginViewManager.add(new LabelField("  Please fill the fields to register :"));
		g = g + PasswordField.getPreferredHeight() * 6 + new SeparatorField().getPreferredHeight() * 4;
		LoginViewManager.add(new LabelField("         "));
		LoginViewManager.add(Email);
		LoginViewManager.add(new SeparatorField());
		LoginViewManager.add(PasswordField);
		LoginViewManager.add(new SeparatorField());
		LoginViewManager.add(PasswordField2);
		LoginViewManager.add(new SeparatorField());
		LoginViewManager.add(Birthday);
		LoginViewManager.add(new SeparatorField());
		OkBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				if (PasswordField2.getText().equals("") || PasswordField.getText().equals("") || Email.getText().equals("") || Birthday.getText().equals("")) {
					Dialog.inform("Please fill all fields."); 
					return;
				}
				if (PasswordField.getText().equals(PasswordField2.getText())){
					String [] datearray;
					try{
						Tools tool = new Tools();
						datearray = tool.split(Birthday.getText(), "-");
						if (Birthday.getText().length() != 10 ) {
							Dialog.inform("Error in birtday date : please respect the yyyy-mm-dd patern.");
							Birthday.setText("");
							return;
						} else if (Integer.parseInt(datearray[1])> 12 || Integer.parseInt(datearray[2]) > 31 ) {
							Dialog.inform("Error in birtday date : please respect the yyyy-mm-dd patern.");
							Birthday.setText("");
							return;
						}
						
					} catch (Exception E){
						Dialog.inform("Error in birtday date : please respect the yyyy-mm-dd patern.");
						Birthday.setText("");
						return;
					}
					try{
						if (Phone.getChecked() == true){
							phone = net.rim.blackberry.api.phone.Phone.getDevicePhoneNumber(false);
						}
					pwd = PasswordField.getText();
					email = Email.getText();
					birthday = Birthday.getText(); 
					age = computeAge(datearray);
					returntouch = false;
					_App.popScreen();
					} catch (Exception E){
						Dialog.inform("Error in birtday date : please respect the yyyy-dd-mm patern.");
						Birthday.setText("");
						return;
					}
				} else {
					Dialog.inform("Password doesn't match confirmation.");
					PasswordField.setText("");
					PasswordField2.setText("");
				}
				
			}
		});
		Vodka.add(LoginViewManager, 0, (Display.getHeight() / 2) - g / 2 - 45 /2);
		Vodka.add(OkBtn,5,Display.getHeight() -85);
		monManager.add(Vodka);
	}
	
	public boolean onSavePrompt() {
		int i = Dialog.ask(Dialog.D_YES_NO,"Do you really want to abort the registration ?");
		if (i == Dialog.YES) {
			return true;
		} else {
			return false;
		}
	}
	
	private class LocalButon extends ButtonField {

		public LocalButon(String nom) {
			super(nom);
		}

		public int getPreferredWidth() {
			return (Display.getWidth() - 50);
		}
		public int getPreferredHeight() {
			return (30);
		}

	}
	
	public int computeAge(String[] Birtday) { 
	    Calendar cBirthday = Calendar.getInstance() ; 
	    Calendar cToday = Calendar.getInstance(); 
	    cBirthday.set(Calendar.YEAR, Integer.parseInt(Birtday[0]));
	    cBirthday.set(Calendar.MONTH, Integer.parseInt(Birtday[1]));
	    cBirthday.set(Calendar.DAY_OF_MONTH, Integer.parseInt(Birtday[2])); 
	    cToday.setTime(new Date(System.currentTimeMillis()));
	 
	    int yearDiff = cToday.get(Calendar.YEAR) - cBirthday.get(Calendar.YEAR); 
	    cBirthday.set(Calendar.YEAR, cToday.get(Calendar.YEAR)); 
	    if (!cBirthday.after(cToday)) { 
	        return yearDiff; //Birthday already celebrated this year 
	    } 
	    else { 
	        return Math.max(0, yearDiff-1); //Need a max to avoid -1 for baby 
	    } 
	}
	
}
