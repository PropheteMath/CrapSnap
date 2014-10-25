package crapsnap;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.GaugeField;

public class ColorSelector extends Dialog{
	
	private GaugeField red;
	private GaugeField blue;
	private GaugeField green;
	private BitmapField preview;
	private Bitmap bmp = new Bitmap(50, 50);
	private Graphics g;
	private Color CurrentColor; 
	
	public ColorSelector(int redc, int bluec, int greenc){
		super(Dialog.D_OK, "Select a color : ", Dialog.OK, null, Dialog.DEFAULT_CLOSE);
		red = new GaugeField("Red : ", 0, 255, redc, GaugeField.VISUAL_STATE_NORMAL);
		blue = new GaugeField("Blue : ", 0, 255, bluec, GaugeField.VISUAL_STATE_NORMAL);
		green = new GaugeField("Green : ", 0, 255, greenc, GaugeField.VISUAL_STATE_NORMAL);
		g = new Graphics(bmp);
		g.fillRect(0, 0, 51, 51);
		preview.setBitmap(bmp);
		this.add(red);
		this.add(blue);
		this.add(green);
		this.add(preview);
	}

}
