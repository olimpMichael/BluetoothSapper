package ru.example.michael.saper;

/**
 * Created by michael on 23.07.2016.
 */
public interface Cell {
    /**
     *Is Cell Bomb
     */
    boolean isBomb();


    void generateBomb(boolean bomb);
    /**
     * User suggested, that cell is Bomb
     */
    boolean isSuggestBomb();

    /**
     * User suggested, that cell is Empty
     */
    boolean isSuggestEmpty();

    /**
     * Set value of cell to empty
     */
    void suggectEmpty();

    /**
     * Set Mine to Cell
     */
    void suggectBomb();

    void countMines(int countMines);

    /**
     * Draw cell
     */
    void draw( boolean real);
}
