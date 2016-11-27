package ru.example.michael.saper.gui;

import ru.example.michael.saper.BaseAction;
import ru.example.michael.saper.GeneratorBoard;
import ru.example.michael.saper.MainActivity;
import ru.example.michael.saper.SaperLogic;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by michael on 24.07.2016.
 */
public class GUIAction extends BaseAction implements ImageButton.OnClickListener, ImageButton.OnLongClickListener {
    int x, y;

    public GUIAction(SaperLogic logic, GUIBoard board, GeneratorBoard generator) {
        super(logic, board, generator);
        initGame(); // Initialization field
        int lengthX = board.cells.length;
        int lengthY = board.cells[0].length;
        // Set ClickListener for Buttons (Cells of gaming field)
        for (int x = 0; x != lengthX; x++) {
            for (int y = 0; y != lengthY; y++) {
                ImageButton img = (ImageButton) board.findViewById(x*1000 + y);
                img.setOnClickListener(this);
                img.setOnLongClickListener(this);
            }
        }
    }

    //*** User clicked on Cell ***
    @Override
    public void onClick(View v) {
        if(!MainActivity.stopGame){
            x = v.getId() / 1000;
            y = v.getId() - (x * 1000);
            if (MainActivity.chbMine) {
                select(x, y, true);
            } else {
                select(x, y, false);
            }
        }
    }

    //*** User longClicked on Cell ***
    @Override
    public boolean onLongClick(View v) {
        if(!MainActivity.stopGame){
            x = v.getId() / 1000;
            y = v.getId() - (x * 1000);
            if (MainActivity.chbMine) {
                select(x, y, false);
            } else {
                select(x, y, true);
            }
            return true;
        }
        return false;
    }
}

