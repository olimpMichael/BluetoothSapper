package ru.example.michael.saper;

/**
 * Created by michael on 23.07.2016.
 */
public interface UserAction {
    void initGame(); //Button of Initialization of Game
    void select(int x, int y, boolean bomb); //user action, where it assumes a bomb or not
}
