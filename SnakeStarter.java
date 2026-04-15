import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class SnakeStarter extends JPanel {
    private final int gridSize = 20;
    private final int cellSize = 25;
    private List<Point> snake = new ArrayList<>();

    public SnakeStarter() {
        snake.add(new Point(10, 10));
        snake.add(new Point(9, 10));
        snake.add(new Point(8, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(22, 28, 39));
        g.fillRect(0, 0, gridSize * cellSize, gridSize * cellSize);
        
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * cellSize, p.y * cellSize, cellSize, cellSize);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Starter");
        SnakeStarter panel = new SnakeStarter();
        frame.add(panel);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
