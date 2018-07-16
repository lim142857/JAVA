package Model;

import java.util.ArrayList;

public interface Observable {

    void register(Observer observer);

    void notifyObservers();

}
