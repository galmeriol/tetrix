package capture;

public enum Directions {
    
    LEFT("Left"),
    RIGHT("Right"),
    UP("Up"),
    DOWN("Down");
    
    String name;
    Directions(String name){ this.name = name(); }
}
