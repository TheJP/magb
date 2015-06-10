// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.rap.help%2Fhelp%2Fhtml%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fgraphics%2Fpackage-summary.html

package imageprocessing;

import gui.TwinView;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import utils.Parallel;

/**
 * Image processing class: contains widely used image processing functions
 * 
 * @author Christoph Stamm
 *
 */
public class ImageProcessing {
	private static class ImageMenuItem {
		private String m_text;
		private int m_accelerator;
		private IImageProcessor m_process;
		
		public ImageMenuItem(String text, int accelerator, IImageProcessor proc) {
			m_text = text;
			m_accelerator = accelerator;
			m_process = proc;
		}
	}
	
	private TwinView m_views;
	private ArrayList<ImageMenuItem> m_menuItems = new ArrayList<ImageMenuItem>();
	
	/**
	 * Registration of image operations
	 * @param views
	 */
	public ImageProcessing(TwinView views) {
		assert views != null : "views are null";
		m_views = views;
		
		//a, b, c, f, g, i, r, s
		m_menuItems.add(new ImageMenuItem("&Invert\tF1", SWT.F1, new Inverter()));
		m_menuItems.add(new ImageMenuItem("RGBTo&Gray\tF2", SWT.F2, new RGBToGray()));
		m_menuItems.add(new ImageMenuItem("GrayTo&Binary\tF3", SWT.F3, new GrayToBinary()));
		m_menuItems.add(new ImageMenuItem("&Rotate\tF4", SWT.F4, new Rotate()));
		m_menuItems.add(new ImageMenuItem("&AllRGB\tF5", SWT.F5, new AllRGB()));
		m_menuItems.add(new ImageMenuItem("&CheckAllRGB\tShift+F5", SWT.SHIFT + SWT.F5, new CheckAllRGB()));
		m_menuItems.add(new ImageMenuItem("&Scale (BiLinear)\tF6", SWT.F6, new Scale()));
		m_menuItems.add(new ImageMenuItem("&Filter\tF7", SWT.F7, new Filter()));
	}
	
	public void createMenuItems(Menu menu) {
		for(final ImageMenuItem item : m_menuItems) {
			MenuItem mi = new MenuItem(menu, SWT.PUSH);
			mi.setText(item.m_text);
			mi.setAccelerator(item.m_accelerator);
			mi.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Image output = null;
					try {
						output = item.m_process.run(m_views.getFirstImage(), m_views.getFirstimageType());
					} catch(Throwable e) {
						int last = item.m_text.indexOf('\t');
						if (last == -1) last = item.m_text.length();
						String location = item.m_text.substring(0, last).replace("&", "");
						m_views.m_mainWnd.showErrorDialog("ImageProcessing", location, e);
					}						
					if (output != null) {
						m_views.showImageInSecondView(output);
					}
				}
			});
		}
	}
	
	public boolean isEnabled(int i) {
		return m_menuItems.get(i).m_process.isEnabled(m_views.getFirstimageType());
	}

	public static int clamp(int c){ return c < 0 ? 0 : (c > 255 ? 255 : c); }
	public static RGB clampRGB(RGB c){
		c.red = clamp(c.red);
		c.green = clamp(c.green);
		c.blue = clamp(c.blue);
		return c;
	}
	public static ImageData convolve(ImageData inData, double[][] filter){ //standard
		return convolve(inData, filter, (int)Math.round((double)filter.length / 2.0), (int)Math.round((double)filter[0].length / 2.0), 1, 0);
	}
	public static ImageData convolve(ImageData inData, double[][] filter, int norm){ //standard
		return convolve(inData, filter, (int)Math.round((double)filter.length / 2.0), (int)Math.round((double)filter[0].length / 2.0), norm, 0);
	}
	public static ImageData convolve(ImageData inData, double[][] filter, int middleX, int middleY, int norm, int offset){
		ImageData outData = (ImageData)inData.clone();
		//Norm filter
		double[][] H = filter.clone();
		if(norm != 1){
		for(int y = 0; y < H.length; ++y){
			for(int x = 0; x < H[y].length; ++x){
				H[y][x] /= (double)norm;
			}
		}
		}
		//Apply filter
		Parallel.For(0, outData.height, v -> {
			for(int u = 0; u < outData.width; ++u){
				//Per pixel: Calculate filter:
				double r = 0, g = 0, b = 0;
				for(int y = -middleY; y < H.length - middleY; ++y){
					for(int x = -middleX; x < H[y+middleY].length - middleX; ++x){
						RGB c = getRGB(inData, u+x, v+y);
						r += c.red * H[y+middleY][x+middleX] + offset;
						g += c.green * H[y+middleY][x+middleX] + offset;
						b += c.blue * H[y+middleY][x+middleX] + offset;
					}
				}
				
				outData.setPixel(u, v, outData.palette.getPixel(new RGB(clamp((int)(Math.round(r))), clamp((int)(Math.round(g))), clamp((int)(Math.round(b))))));
			}
		});
		return outData;
	}
	public static RGB getRGB(ImageData inData, int u, int v){
		//Upper bound
		u = u < inData.width ? u : inData.width - u - 1 + inData.width;
		v = v < inData.height ? v : inData.height - v - 1 + inData.height; 
		//Lower bound
		u = Math.abs(u) % inData.width;
		v = Math.abs(v) % inData.height;
		return inData.palette.getRGB(inData.getPixel(u, v));
	}

}
