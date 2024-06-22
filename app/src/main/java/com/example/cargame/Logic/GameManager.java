package com.example.cargame.Logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    public static final int CLEAR = 0;
    public static final int TRACTOR = 1;
    public static final int BLOCK = 2;

    private int[][] grid;
    private int rows;
    private int cols;
    private int tractorColumn;
    private int score;
    private int lives;
    private Random random;
    private int updateCounter;

    public GameManager(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
        this.random = new Random();
        resetGame();
    }

    public void resetGame() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = CLEAR;
            }
        }
        tractorColumn = cols / 2;
        grid[rows - 1][tractorColumn] = TRACTOR;
        score = 0;
        lives = 3;
        updateCounter = 0;
    }

    public void update() {
        updateCounter++;

        moveObstaclesDown();
        spawnObstacle();

        if (grid[rows - 1][tractorColumn] == BLOCK) {
            lives--;
            grid[rows - 1][tractorColumn] = TRACTOR;
        }

        grid[rows - 1][tractorColumn] = TRACTOR;

        score++;
    }

    private void moveObstaclesDown() {
        for (int i = rows - 1; i > 0; i--) {
            for (int j = 0; j < cols; j++) {
                if (i == rows - 1 && grid[i][j] == BLOCK) {
                    grid[i][j] = CLEAR;
                } else {
                    grid[i][j] = grid[i - 1][j];
                }
            }
        }

        for (int j = 0; j < cols; j++) {
            grid[0][j] = CLEAR;
        }
    }

    private void spawnObstacle() {
        List<Integer> validColumns = new ArrayList<>();
        for (int j = 0; j < cols; j++) {
            if (grid[1][j] != BLOCK) {
                validColumns.add(j);
            }
        }
        if (!validColumns.isEmpty()) {
            int chosenColumn = validColumns.get(random.nextInt(validColumns.size()));
            grid[0][chosenColumn] = BLOCK;
        }
    }

    public void moveTractor(boolean moveLeft) {
        grid[rows - 1][tractorColumn] = CLEAR;
        if (moveLeft && tractorColumn > 0) {
            tractorColumn--;
        } else if (!moveLeft && tractorColumn < cols - 1) {
            tractorColumn++;
        }
        grid[rows - 1][tractorColumn] = TRACTOR;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }
}
