package ru.example.michael.saper.gui;

import android.content.Context;
import android.widget.ImageButton;
import ru.example.michael.saper.Cell;
import ru.example.michael.saper.MainActivity;
import ru.example.michael.saper.R;

/**
 * Created by michael on 24.07.2016.
 */
public class GUICell extends ImageButton implements Cell {
    private boolean bomb;
    private boolean suggestBomb=false;
    private boolean suggestEmpty=false;
    private int countMines;

    public GUICell(Context context,boolean bomb) {
        super(context);
        this.bomb=bomb;
    }


    @Override
    public void generateBomb(boolean bomb){
        this.bomb=bomb;
    }

    @Override //Is cell bomb
    public boolean isBomb() {
        return this.bomb;
    }

    @Override //The user suggested that the cell is the Bomb
    public boolean isSuggestBomb() {
        return this.suggestBomb;
    }

    @Override //The user suggested that the cell is empty
    public boolean isSuggestEmpty() {
        return this.suggestEmpty;
    }

    @Override
    public void suggectEmpty() {
        this.suggestEmpty=true;
    }

    @Override
    public void suggectBomb() {
        if(this.suggestEmpty){return;}
        if (this.suggestBomb) {
            this.suggestBomb=false;
            MainActivity.countBomb++;
            MainActivity.tvMine.setText("0" + MainActivity.countBomb);
        }
        else {
            if(MainActivity.countBomb>0){
                this.suggestBomb=true;
                MainActivity.countBomb--;
                MainActivity.tvMine.setText("0" + MainActivity.countBomb);
            }
        }
    }

    @Override
    public void countMines(int countMines){
        this.countMines = countMines;
    }

    public void draw(boolean real){
        this.setImageResource(R.drawable.empty);
        this.setPadding(0,0,0,0); //Set the margins between the cells

        if (real) { //Draw real value
            if(this.isBomb()) { //draw bomb
                if (this.isSuggestEmpty()){
                    this.setImageResource(R.drawable.notmine);
                } else {
                    this.setImageResource(R.drawable.mine);
                }
            } else {
                //draw empty cell
                if (this.isSuggestBomb()){
                    this.setImageResource(R.drawable.notflag);
                }else {
                    if (countMines >= 0) {
                        switch (countMines) {
                            case 0:
                                this.setImageResource(R.drawable.ic0);
                                break;
                            case 1:
                                this.setImageResource(R.drawable.ic1);
                                break;
                            case 2:
                                this.setImageResource(R.drawable.ic2);
                                break;
                            case 3:
                                this.setImageResource(R.drawable.ic3);
                                break;
                            case 4:
                                this.setImageResource(R.drawable.ic4);
                                break;
                            case 5:
                                this.setImageResource(R.drawable.ic5);
                                break;
                            case 6:
                                this.setImageResource(R.drawable.ic6);
                                break;
                            case 7:
                                this.setImageResource(R.drawable.ic7);
                                break;
                            case 8:
                                this.setImageResource(R.drawable.ic8);
                                break;
                        }
                    }
                }
            }
        } else { //draw user's suggest
            MainActivity.btnInit.setImageResource(R.drawable.btnstart);
            if(this.suggestBomb){ //User suggested, that cell is bomb
                this.setImageResource(R.drawable.flag);
            } else if(this.suggestEmpty){ //User suggested, that cell is empty
                if (countMines>=0){
                    switch(countMines) {
                        case 0:
                            this.setImageResource(R.drawable.ic0);
                            break;
                        case 1:
                            this.setImageResource(R.drawable.ic1);
                            break;
                        case 2:
                            this.setImageResource(R.drawable.ic2);
                            break;
                        case 3:
                            this.setImageResource(R.drawable.ic3);
                            break;
                        case 4:
                            this.setImageResource(R.drawable.ic4);
                            break;
                        case 5:
                            this.setImageResource(R.drawable.ic5);
                            break;
                        case 6:
                            this.setImageResource(R.drawable.ic6);
                            break;
                        case 7:
                            this.setImageResource(R.drawable.ic7);
                            break;
                        case 8:
                            this.setImageResource(R.drawable.ic8);
                            break;
                    }
                }
            }
        }
    }
}
