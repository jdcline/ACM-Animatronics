package animatronics2;

import jssc.SerialPort;
import animatronics2.AnimatronicsShowPlayer;
import animatronics2.MicrocontrollerConnection;

public class JACSTester {
	public static void main (String[] args)
   {
      //// Set up Animatronics Show Context
      // OS ?
      
      // Audio Player?
      
      // Create microcontroller
      MicroController servoController = new MicroMaestro ("Pololu Micro Maester");
      
      // Choose Serial Port
      String portName = /* Get correct one from list or ask user to choose ..maybe search for string with Microcontroller name*/
      SerialPort port = new SerialPort(portName);
      
      // Create MicrocontrollerConnection
      MicrocontrollerConnection microConnection= new MicrocontrollerConnection (servoController, port);
      
      
      /////
      // Load Show  into Show Model structure...or directly into data structures for testing
      //  -- load audio
      //  -- load bytes
      
      // Set ShowTimingConfiguration
      
      // Format show data for AnimatronicsShow
      
      // Play Show
      
      AnimatronicsShowPlayer showPlayer(microConection, audiofiles, servomotions, ?);
      showPlayer.play();
      
    }
}