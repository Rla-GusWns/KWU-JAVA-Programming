import javax.swing.*;
import java.awt.*;

class CardLayOutPanel extends JFrame{

    JButton b1,b2,b3;
    Container cPane;
    CardLayout layoutm;

    public CardLayOutPanel(){
        setTitle("CardLayOutPanel");
        setSize(300,150);
        cPane = getContentPane();
        layoutm = new CardLayout();
        setLayout(layoutm);

        JButton b1 = new JButton("card 1");
        JButton b2 = new JButton("card 2");
        JButton b3 = new JButton("card 3");

        this.add(b1);
        this.add(b2);
        this.add(b3);

        b1.addActionListener(e-> layoutm.next(cPane));
        b2.addActionListener(e-> layoutm.next(cPane));
        b3.addActionListener(e-> layoutm.next(cPane));

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}

public class homework2 extends JFrame {
    public static void main(String[] args) {
        CardLayOutPanel G = new CardLayOutPanel();
    } 
}
