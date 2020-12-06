package game_package;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
public class main_activity {
    public static void main(String[] args) throws IOException {
        Game_Control game_control=new Game_Control();
        game_control.startgame();
    }
}

