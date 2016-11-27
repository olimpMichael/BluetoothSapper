package ru.example.michael.saper;

/**
 * Created by michael on 23.07.2016.
 */
public interface Board {
    /**
     * Draw boar on the basis of the incoming cell array
     * @param cells Cell array
     */
    void drawBoard(Cell[][] cells);

    /**
     * Draw cell
     * @param x - horizontal position
     * @param y - vertical position
     */
    void drawCell(int x, int y);

    /**
     *Draw Bang all mines
     */

    void drawBang();

    /**
     * Draw congratulates, when game is won
     */
    void drawCongratulate();
}
