import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ScreenCaptureApp {

    public ScreenCaptureApp(){

        captureAndShowScreenshot(getActiveScreen());
    }


    public GraphicsDevice getActiveScreen(){ //get the screen where the mouse is

        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        GraphicsDevice targetScreen = null;

        for (GraphicsDevice screen : screenDevices) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            if (bounds.contains(getMouseLocation())) {
                targetScreen = screen;
                break;
            }
        }
        return targetScreen;
    }

    public Point getMouseLocation(){
        return MouseInfo.getPointerInfo().getLocation();
    }

    public void captureAndShowScreenshot(GraphicsDevice targetScreen){
        JFrame screenshotFrame;
        if (targetScreen != null) {

            Robot robot = null;
            try {
                robot = new Robot(targetScreen);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
            Rectangle screenRect = targetScreen.getDefaultConfiguration().getBounds();
            BufferedImage screenshot = robot.createScreenCapture(screenRect);


            //creating colored border
            Graphics2D g2d = screenshot.createGraphics();
            int borderWidth = 4;
            Color borderColor = Color.CYAN;
            g2d.setColor(borderColor);
            g2d.fillRect(0, 0, screenshot.getWidth(), borderWidth);
            g2d.fillRect(0, borderWidth, borderWidth, screenshot.getHeight() - 2 * borderWidth);
            g2d.fillRect(screenshot.getWidth() - borderWidth, borderWidth, borderWidth, screenshot.getHeight() - 2 * borderWidth);
            g2d.fillRect(0, screenshot.getHeight() - borderWidth, screenshot.getWidth(), borderWidth);
            g2d.dispose();


            screenshotFrame = new JFrame("Screenshot");
            screenshotFrame.setSize(screenshot.getWidth(), screenshot.getHeight());
            screenshotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            screenshotFrame.getContentPane().add(new JLabel(new ImageIcon(screenshot)));

            GraphicsConfiguration gc = targetScreen.getDefaultConfiguration();
            Rectangle bounds = gc.getBounds();
            int x = bounds.x + (bounds.width - screenshotFrame.getWidth()) / 2;
            int y = bounds.y + (bounds.height - screenshotFrame.getHeight()) / 2;
            screenshotFrame.setLocation(x, y);

            screenshotFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            screenshotFrame.setUndecorated(true);

            ImageIcon appIcon = new ImageIcon(ScreenCaptureApp.class.getResource("/chroma.png")); //set icon
            screenshotFrame.setIconImage(appIcon.getImage());

            screenshotFrame.setVisible(true);
            screenshotFrame. addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (screenshotFrame != null && screenshotFrame.isVisible()) {

                        copyToClipboard(rgbToHex(getPixelColor(screenshot, e.getX(), e.getY())));
                        screenshotFrame.dispose();
                    }
                }
            });
        }


    }
    private Color getPixelColor(BufferedImage screen,int x, int y) {
        return new Color(screen.getRGB(x, y));
    }

    private String rgbToHex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

}



