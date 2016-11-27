package ru.example.michael.saper.gui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import ru.example.michael.saper.Board;
import ru.example.michael.saper.Cell;
import ru.example.michael.saper.MainActivity;
import ru.example.michael.saper.Multiplayer;
import ru.example.michael.saper.R;

public class GUIBoard extends TableLayout implements Board {
    public Cell[][] cells;
    Handler mHandler;
    Context context;

    public GUIBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    //*** Draw playing field ***
    @Override
    public void drawBoard(Cell[][] cells) {
        MainActivity.btnInit.setImageResource(R.drawable.btnstart);
        this.removeAllViews(); //remove all Views from field
        this.cells=cells;

        int lengthX = cells.length;
        int lengthY = cells[0].length;

        for (int x = 0; x != lengthX; x++) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            row.setGravity(Gravity.CENTER);
            for (int y = 0; y != lengthY; y++) {
                cells[x][y].draw(false);
                row.addView((ImageButton) cells[x][y], y);

                ViewGroup.LayoutParams params = ((ImageButton) cells[x][y]).getLayoutParams();
                params.width = MainActivity.PADDING;
                params.height = MainActivity.PADDING;

                ImageView img = (ImageView) cells[x][y];
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ((ImageButton) cells[x][y]).setBackgroundResource(R.drawable.my_without_bg_button);
                ((ImageButton) cells[x][y]).setLongClickable(true);
                ((ImageButton) cells[x][y]).setId(x * 1000 + y);
            }
            this.addView(row, x);
        }
    }

    @Override
    public void drawCell(int x, int y) {
        cells[x][y].draw(false);
    }

    //*** User is lost, draw Bang ***
    @Override
    public void drawBang() {
        MainActivity.stopGame = true;
        if(mHandler!=null){
            mHandler.sendEmptyMessage(Multiplayer.MESSAGE_SAPPER_LOSS);
        }
        MainActivity.btnInit.setImageResource(R.drawable.btnend);
        Toast.makeText(MainActivity.context, R.string.loss, Toast.LENGTH_LONG).show();
        for (int x=0; x!=cells.length; x++) {
            for (int y = 0; y != cells[0].length; y++) {
                if(cells[x][y].isSuggestBomb() && cells[x][y].isBomb()){
                    cells[x][y].draw(false);
                } else
                    cells[x][y].draw(true);
            }
        }
    }

    //*** User is win, draw Congratulate ***
    @Override
    public void drawCongratulate() {
       MainActivity.stopGame = true;
        if(mHandler!=null){
            mHandler.sendEmptyMessage(Multiplayer.MESSAGE_SAPPER_WIN);
        }
        Toast.makeText(MainActivity.context, R.string.victory, Toast.LENGTH_LONG).show();
    }
}
