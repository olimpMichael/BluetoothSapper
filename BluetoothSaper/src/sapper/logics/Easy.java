package ru.example.michael.saper.logics;

import ru.example.michael.saper.Cell;
import ru.example.michael.saper.SaperLogic;

/**
 * Created by michael on 23.07.2016.
 */
public class Easy implements SaperLogic {
    private Cell[][] cells;


    @Override
    public void loadBoard(Cell[][] cells) {
        this.cells = cells;
        for(int x=0; x!=cells.length; x++) {
            for (int y = 0; y != cells[0].length; y++) {
                this.cells[x][y].countMines(getCountMines(x, y));
            }
        }
    }

    //*** Counting the number of mines ***
    @Override
    public int getCountMines(int x, int y){
        int count = 0;
        if(x!=0 && y!=0) {if(cells[x-1][y-1].isBomb()) {count++;}}
        if(y!=0) {if(cells[x][y-1].isBomb()) {count++;}}
        if(x!=cells.length-1 && y!=0) {if(cells[x+1][y-1].isBomb()) {count++;}}
        if(x!=0) {if(cells[x-1][y].isBomb()) {count++;}}
        if(x!=cells.length-1) {if(cells[x+1][y].isBomb()) {count++;}}
        if(x!=0 && y!=cells[x].length-1) {if(cells[x-1][y+1].isBomb()) {count++;}}
        if(y!=cells[x].length-1) {if(cells[x][y+1].isBomb()) {count++;}}
        if(x!=cells.length-1 && y!=cells[x].length-1) {if(cells[x+1][y+1].isBomb()) {count++;}}
        return count;
        }

    //*** Check whether you need to explode ***
    @Override
    public boolean shouldBang(int x, int y) {
        final Cell selected = this.cells[x][y];
        if(selected.isSuggestEmpty()){
            return selected.isBomb() && !selected.isSuggestBomb();
        }
        return false;
    }

    //*** Check whether the user has won ***
    @Override
    public boolean finish() {
        boolean finish = true;
        for(Cell[] row:cells){
            for(Cell cell:row){
                finish = finish && ((cell.isSuggestBomb() && cell.isBomb())
                        || (cell.isSuggestEmpty() && !cell.isBomb()));
            }
        }
        return finish;
    }


    /****  Checking the user to select the logic cell  ****/
    @Override
    public void suggest(int x, int y, boolean bomb) {
        if (bomb) { //if is bomb, that check as bomb
            this.cells[x][y].suggectBomb();
        } else{ //if is empty cell, that check as empty cell
            this.cells[x][y].suggectEmpty();
        }
    }
}
