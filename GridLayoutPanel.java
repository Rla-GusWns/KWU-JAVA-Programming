import javax.swing.*;
import java.awt.*;

class GridLayOutPanel extends JFrame{

    public GridLayOutPanel(){
        setTitle("GirdLayOutPanel");
        setSize(300,150);
        setLayout(new GridLayout(2,3));

        this.add(new JButton("btn"));
        this.add(new JButton("btn1"));
        this.add(new JButton("btn2"));
        this.add(new JButton("btn3"));
        this.add(new JButton("btn4"));

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}

public class homework1 extends JFrame {
    public static void main(String[] args) {
        GridLayOutPanel G = new GridLayOutPanel();
    } 
}
