import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class SpriteStore {
    private static SpriteStore single = new SpriteStore();
    
    public static SpriteStore get() {
        // singleton pattern
        return single;
    }
    
    private HashMap sprites = new HashMap();
    
    public Sprite getSprite(String ref) {
        if (sprites.get(ref) != null) {
            return (Sprite) sprites.get(ref);
        }

        BufferedImage sourceImage = null;
        
        try {
            URL url = this.getClass().getClassLoader().getResource(ref);
            
            if (url == null) {
                fail("Can't find ref: "+ref);
            }
            
            // use ImageIO to read the image in
            sourceImage = ImageIO.read(url);
        } catch (IOException e) {
            fail("Failed to load: "+ref);
        }
        
        // create an accelerated image of the right size to store our sprite in
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        Image image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);
        
        // draw source image into the accelerated image
        image.getGraphics().drawImage(sourceImage,0,0,null);
        
        // create a sprite, add it the cache then return it
        Sprite sprite = new Sprite(image);
        sprites.put(ref,sprite);
        
        return sprite;
    }

    private void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }
}