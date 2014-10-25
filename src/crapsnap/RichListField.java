package crapsnap;

import net.rim.device.api.command.Command;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;

public final class RichListField extends VerticalFieldManager {

	private Command cmd = null;
	private boolean _imageSpaceUsed;
	private int _numberOfLabelFields;
	private int _numberOfDescriptionFields;
	private boolean alternened = false;

	RichListField(Manager manager, boolean imageSpaceUsed,
			int numberOfLabelFields, int numberOfDescriptionFields) {
		_imageSpaceUsed = imageSpaceUsed;
		_numberOfLabelFields = numberOfLabelFields;
		_numberOfDescriptionFields = numberOfDescriptionFields;
		this.setBackground(BackgroundFactory.createBitmapBackground(EncodedImage.getEncodedImageResource("empty.png").getBitmap()));
		manager.add(this);
	}
	
	boolean _Separator = false;
	RichListField(Manager manager, boolean imageSpaceUsed,
			int numberOfLabelFields, int numberOfDescriptionFields, boolean Separator) {
		_imageSpaceUsed = imageSpaceUsed;
		_numberOfLabelFields = numberOfLabelFields;
		_numberOfDescriptionFields = numberOfDescriptionFields;
		this.setBackground(BackgroundFactory.createBitmapBackground(EncodedImage.getEncodedImageResource("empty.png").getBitmap()));
		_Separator = Separator;
		manager.add(this);
	}
	
	RichListField( boolean imageSpaceUsed,
			int numberOfLabelFields, int numberOfDescriptionFields, boolean Separator) {
		_imageSpaceUsed = imageSpaceUsed;
		_numberOfLabelFields = numberOfLabelFields;
		_numberOfDescriptionFields = numberOfDescriptionFields;
		_Separator = Separator;
		this.setBackground(BackgroundFactory.createBitmapBackground(EncodedImage.getEncodedImageResource("empty.png").getBitmap()));
	}
	
	RichListField( boolean imageSpaceUsed,
			int numberOfLabelFields, int numberOfDescriptionFields) {
		_imageSpaceUsed = imageSpaceUsed;
		_numberOfLabelFields = numberOfLabelFields;
		_numberOfDescriptionFields = numberOfDescriptionFields;
		this.setBackground(BackgroundFactory.createBitmapBackground(EncodedImage.getEncodedImageResource("empty.png").getBitmap()));
	}

	public int getFocusRow() {
		for (int i = 0; i < this.getFieldCount(); i++) {
			if (this.getField(i).isFocus() == true) {
				return i;
			}
		}
		return -1;
	}

	public void setFocusPolicy(int policy) {
		//pas compris l'interet. 
		//Juste là pour compatibilité.
	}

	public void setCommand(Command command) {
		cmd = command;
	}

	public void remove(int row) {
		this.delete(this.getField(row));
	}

	public  void insert(int row, Object[] rowObj) {
		this.insert(Factory(rowObj, true), row);
	}

	public Object[] get(int row) {
		return ((ListManager) this.getField(row)).arg;
	}

	public void add(Object[] rowObj) {
		this.add(Factory(rowObj, false));
	}

	protected boolean trackwheelClick(int status, int time) {
		if (cmd == null) {
			return false;
		} else {
			cmd.execute(this);
			return true;
		}
	}
	
	public Manager Factory(Object[] arg, boolean isupdated) {

			int i = 0;
			boolean textfielded = false;
			ListManager Main = new ListManager();
			Main.arg = arg;
			HorizontalFieldManager Head = new HorizontalFieldManager();
			VerticalFieldManager Label = new VerticalFieldManager();
			VerticalFieldManager Description = new VerticalFieldManager();
			if (_imageSpaceUsed == true) {
				Head.add(new BitmapField((Bitmap) arg[0]));
				i = i + 1;
			}
			for (int j = 0; j < _numberOfLabelFields; j++) { 
				if (textfielded == false){
					LabelField txt = new LabelField((String) arg[i] + "                                                                                         ",Field.FOCUSABLE);
					Label.add(txt);
					textfielded = true;
				} else {
				Label.add(new LabelField((String) arg[i], LabelField.ELLIPSIS));
				}
				i = i +1;
			}
			Head.add(Label);
			Main.add(Head);
			for (int k = 0; k < _numberOfDescriptionFields; k++) {
				Description.add(new LabelField((String) arg[i]));
				i = i+1;
			}
			Main.add(Description);
			if (isupdated == false){
				if (alternened == true){
				Main.setBackground(BackgroundFactory.createSolidBackground(Color.IVORY));
				alternened = false;
			} else {
				Main.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));
				alternened = true;
			}
			}else {
				Main.setBackground(BackgroundFactory.createSolidBackground(Color.ANTIQUEWHITE));
			}
			if (_Separator == true) Main.add(new SeparatorField());
			return Main;
	}

	private class ListManager extends VerticalFieldManager {
		
		public Object[] arg;
		
		ListManager() {
			super(Manager.NO_HORIZONTAL_SCROLL );
		}
		
	}
}
