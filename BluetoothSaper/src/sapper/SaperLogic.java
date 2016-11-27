package ru.example.michael.saper;

/**
 * Created by michael on 23.07.2016.
 * Logic of Game
 */
public interface SaperLogic {
    void loadBoard(Cell[][] cells); //Load gaming field
    boolean shouldBang(int x, int y); //Check whether you need to bang
    boolean finish(); //Checks whether a user has solved the whole field
    void suggest(int x, int y, boolean bomb); //The event, which comes from the user
    int getCountMines(int x, int y); //Counting the number of mines
}
