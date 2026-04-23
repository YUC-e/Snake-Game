import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class SnakeGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel {
    private final int cellSize = 30;
    private final int gridSize = 20;
    private List<Point> snake = new ArrayList<>();
    private Point food;
    private Point specialFood;
    private int dx = 1;
    private int dy = 0;
    private int nextDx = 1;
    private int nextDy = 0;
    private Timer gameTimer;
    private int score = 0;
    private boolean gameOver = false;
    private Random random = new Random();
    private int tickSpeed = 150;
    private final int MIN_TICK_SPEED = 80;
    private int normalFoodEaten = 0;
    private final int SPECIAL_FOOD_THRESHOLD = 3;
    private int speedUpCount = 0;
    private final int MAX_SPEED_UPS = 8;
    
    public GamePanel() {
        setBackground(new Color(50, 50, 50));
        setFocusable(true);
        initializeSnake();
        spawnFood();
        addKeyListener(new SnakeKeyListener());
        startGameTimer();
    }
    
    private void initializeSnake() {
        snake.clear();
        snake.add(new Point(10, 10));
        snake.add(new Point(9, 10));
        snake.add(new Point(8, 10));
        dx = 1;
        dy = 0;
        nextDx = 1;
        nextDy = 0;
        score = 0;
        gameOver = false;
        tickSpeed = 150;
        normalFoodEaten = 0;
        specialFood = null;
        speedUpCount = 0;
    }
    
    private void spawnFood() {
        boolean foundSpot = false;
        Point newFood = null;
        
        // Create a list of all occupied positions
        List<Point> occupiedPositions = new ArrayList<>(snake);
        
        // Try random placement with guaranteed attempts
        for (int attempt = 0; attempt < 200; attempt++) {
            newFood = new Point(random.nextInt(gridSize), random.nextInt(gridSize));
            if (!occupiedPositions.contains(newFood)) {
                food = newFood;
                foundSpot = true;
                return;
            }
        }
        
        // If random fails, do systematic scan
        if (!foundSpot) {
            for (int x = 0; x < gridSize; x++) {
                for (int y = 0; y < gridSize; y++) {
                    Point candidate = new Point(x, y);
                    if (!occupiedPositions.contains(candidate)) {
                        food = candidate;
                        return;
                    }
                }
            }
        }
    }
    
    private void spawnSpecialFood() {
        boolean foundSpot = false;
        Point newSpecialFood = null;
        
        // Create a list of all occupied positions
        List<Point> occupiedPositions = new ArrayList<>(snake);
        if (food != null) {
            occupiedPositions.add(food);
        }
        
        // Try random placement with guaranteed attempts
        for (int attempt = 0; attempt < 200; attempt++) {
            newSpecialFood = new Point(random.nextInt(gridSize), random.nextInt(gridSize));
            if (!occupiedPositions.contains(newSpecialFood)) {
                specialFood = newSpecialFood;
                foundSpot = true;
                return;
            }
        }
        
        // If random fails, do systematic scan
        if (!foundSpot) {
            for (int x = 0; x < gridSize; x++) {
                for (int y = 0; y < gridSize; y++) {
                    Point candidate = new Point(x, y);
                    if (!occupiedPositions.contains(candidate)) {
                        specialFood = candidate;
                        return;
                    }
                }
            }
        }
    }
    
    private void startGameTimer() {
        gameTimer = new Timer(tickSpeed, e -> {
            if (!gameOver) {
                moveSnake();
            }
        });
        gameTimer.setInitialDelay(0);
        gameTimer.start();
    }
    
    private void moveSnake() {
        // Update direction only if it doesn't reverse
        if (!(nextDx == -dx && nextDy == -dy)) {
            dx = nextDx;
            dy = nextDy;
        }
        
        // Calculate new head position
        Point head = snake.get(0);
        int newX = head.x + dx;
        int newY = head.y + dy;
        
        // Check collision with walls
        if (newX < 0 || newX >= gridSize || newY < 0 || newY >= gridSize) {
            endGame();
            return;
        }
        
        Point newHead = new Point(newX, newY);
        
        // Check collision with self
        for (Point segment : snake) {
            if (segment.equals(newHead)) {
                endGame();
                return;
            }
        }
        
        // Add new head
        snake.add(0, newHead);
        
        // Check if special food was eaten
        if (specialFood != null && newHead.equals(specialFood)) {
            score += 30;
            speedUp();
            // Add 2 extra segments by not removing tail twice
            specialFood = null;
            normalFoodEaten = 0;
        } 
        // Check if normal food was eaten
        else if (food != null && newHead.equals(food)) {
            score += 10;
            normalFoodEaten++;
            speedUp();
            spawnFood();
            
            // Check if special food should spawn
            if (normalFoodEaten >= SPECIAL_FOOD_THRESHOLD) {
                spawnSpecialFood();
                normalFoodEaten = 0;
            }
        } 
        else {
            // Remove tail if no food eaten
            snake.remove(snake.size() - 1);
        }
        
        repaint();
    }
    
    private void speedUp() {
        if (speedUpCount < MAX_SPEED_UPS && tickSpeed > 80) {
            tickSpeed -= 10;
            gameTimer.setDelay(tickSpeed);
            speedUpCount++;
        }
    }
    
    private void endGame() {
        gameOver = true;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw grid
        g.setColor(new Color(80, 80, 80));
        for (int i = 0; i <= gridSize; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, gridSize * cellSize);
            g.drawLine(0, i * cellSize, gridSize * cellSize, i * cellSize);
        }
        
        // Draw normal food
        if (food != null) {
            g.setColor(Color.RED);
            g.fillRect(food.x * cellSize, food.y * cellSize, cellSize, cellSize);
        }
        
        // Draw special food
        if (specialFood != null) {
            g.setColor(Color.YELLOW);
            g.fillRect(specialFood.x * cellSize, specialFood.y * cellSize, cellSize, cellSize);
        }
        
        // Draw snake
        g.setColor(Color.GREEN);
        for (Point segment : snake) {
            g.fillRect(segment.x * cellSize, segment.y * cellSize, cellSize, cellSize);
        }
        
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);
        
        // Draw game over message
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String gameOverText = "Game Over";
            FontMetrics fm = g.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(gameOverText)) / 2;
            int textY = (getHeight() / 2) - 30;
            g.drawString(gameOverText, textX, textY);
            
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String scoreText = "Final Score: " + score;
            fm = g.getFontMetrics();
            textX = (getWidth() - fm.stringWidth(scoreText)) / 2;
            textY += 50;
            g.drawString(scoreText, textX, textY);
            
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String resetText = "Press R to Play Again";
            fm = g.getFontMetrics();
            textX = (getWidth() - fm.stringWidth(resetText)) / 2;
            textY += 40;
            g.drawString(resetText, textX, textY);
        }
    }
    
    private class SnakeKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
                initializeSnake();
                spawnFood();
                gameTimer.restart();
                repaint();
                requestFocusInWindow();
                return;
            }
            
            if (gameOver) return;
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (dy == 0) {
                        nextDx = 0;
                        nextDy = -1;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (dy == 0) {
                        nextDx = 0;
                        nextDy = 1;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (dx == 0) {
                        nextDx = -1;
                        nextDy = 0;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (dx == 0) {
                        nextDx = 1;
                        nextDy = 0;
                    }
                    break;
            }
        }
    }
}