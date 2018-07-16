package Model;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Phase2Log {

  private static final Logger LOGGER = Logger.getLogger(Phase2Log.class.getName());
  private static Phase2Log instance = null;

  public static Phase2Log getInstance(){
    if (instance == null){
      instance = new Phase2Log();
    }
    return instance;
  }

  private Phase2Log() {
    LOGGER.setLevel(Level.FINEST);
    FileHandler fh;

    try {
      fh = new FileHandler("phase2/"+ System.currentTimeMillis() +"log.txt");
      LOGGER.addHandler(fh);
      SimpleFormatter sf = new SimpleFormatter();
      fh.setFormatter(sf);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void putLog(Level level, String message){
    LOGGER.log(level, message);

  }
}