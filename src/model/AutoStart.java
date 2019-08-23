
package model;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.pi4j.io.gpio.PinState;
import java.io.IOException;
import java.io.File;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;

public class AutoStart
{
	private static long MIN_PRESS_TIME = 1000;
    private static boolean started;
    private static GpioPinDigitalInput button;
    
    static {
        AutoStart.started = false;
    }
    
    public static void main(final String[] args) throws IOException {
        final GpioController gpio = GpioFactory.getInstance();
        (AutoStart.button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN)).setShutdownOptions(true);
        File movie = null;
        while (movie == null) {
            movie = getMovieFile();
            sleepFor(1);
        }
        System.out.println("setting background");
        Background.show();
        while (true) {
        	waitForPressed();
            playMovie(movie);
            sleepFor(4);
        }
    }
    
    private static File getMovieFile() {
        File[] listFiles;
        for (int length = (listFiles = new File("/media/pi/").listFiles()).length, i = 0; i < length; ++i) {
            final File usb = listFiles[i];
            if (!usb.getName().contains("SETTINGS")) {
                File[] listFiles2;
                for (int length2 = (listFiles2 = usb.listFiles()).length, j = 0; j < length2; ++j) {
                    final File possibleMovie = listFiles2[j];
                    if (possibleMovie.getName().contains(".mp4")) {
                        System.out.println("movie file: " + possibleMovie.getAbsolutePath());
                        return possibleMovie;
                    }
                }
            }
        }
        try {
            File[] listFiles3;
            for (int length3 = (listFiles3 = new File("/home/pi/Bureau/").listFiles()).length, k = 0; k < length3; ++k) {
                final File possibleMovie2 = listFiles3[k];
                if (possibleMovie2.getName().contains(".mp4")) {
                    System.out.println("movie file: " + possibleMovie2.getAbsolutePath());
                    return possibleMovie2;
                }
            }
        }
        catch (NullPointerException ex) {}
        try {
            File[] listFiles4;
            for (int length4 = (listFiles4 = new File("/home/pi/Desktop/").listFiles()).length, l = 0; l < length4; ++l) {
                final File possibleMovie2 = listFiles4[l];
                if (possibleMovie2.getName().contains(".mp4")) {
                    System.out.println("movie file: " + possibleMovie2.getAbsolutePath());
                    return possibleMovie2;
                }
            }
        }
        catch (NullPointerException ex2) {}
        return null;
    }
    
    private static void waitForPressed() {
    	long millis = Long.MAX_VALUE;
    	boolean pressed = false;
    	while(true) {
    		if(AutoStart.button.getState() == PinState.HIGH) {
    			if(pressed == false) {
    				System.out.println("pressed button");
    				pressed = true;
    				millis = System.currentTimeMillis();
    			}else {
    				if(System.currentTimeMillis()-millis>MIN_PRESS_TIME) {
    					System.out.println("pressed button for at least "+MIN_PRESS_TIME+" millis");
    					return;
    				}
    			}
    		}else {
    			if(pressed ==true) {
    				pressed = false;
    				System.out.println("stopped pressing after "+(System.currentTimeMillis()-millis)+" millis ");
    			}
    		}
    	}
    	
    }
    
    private static void playMovie(final File movie) throws IOException {
        if (AutoStart.started) {
            closeOmxPlayer();
        }
        startOmx(movie);
    }
    
    private static void closeOmxPlayer() throws IOException {
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "bash", "-c", "ps -ef |egrep /usr/bin/omxplayer.bin" });
        final Process process = pb.start();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        if ((line = reader.readLine()) != null) {
            System.out.println("egrep: " + line);
            killProcess(getProcessToKill(line));
        }
    }
    
    private static int getProcessToKill(final String line) {
        int index = 0;
        for (int i = 0; i < line.length(); ++i) {
            if (Character.isDigit(line.charAt(i))) {
                index = i;
                break;
            }
        }
        try {
            return Integer.parseInt(line.substring(index, line.indexOf(" ", index)));
        }
        catch (Exception e) {
            e.printStackTrace();
            return 69696969;
        }
    }
    
    private static void killProcess(final int processToKill) throws IOException {
        System.out.println("process to kill: " + processToKill);
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "bash", "-c", "kill -9 " + processToKill });
        final Process process = pb.start();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        if ((line = reader.readLine()) != null) {
            System.out.println("kill: " + line);
            killProcess(getProcessToKill(line));
        }
    }
    
    private static void startOmx(final File movie) throws IOException {
        final ProcessBuilder pb = new ProcessBuilder(new String[] { "bash", "-c", "omxplayer " + movie.getAbsolutePath() });
        pb.start();
        AutoStart.started = true;
    }
    
    private static void sleepFor(final int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
