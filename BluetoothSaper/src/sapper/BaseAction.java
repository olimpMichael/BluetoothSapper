package ru.example.michael.saper;

/**
 * Created by michael on 23.07.2016.
 */
public class BaseAction implements UserAction {
    private final GeneratorBoard generator;
    private final Board board;
    private final SaperLogic logic;
    protected Cell[][] cells;


    public BaseAction(final SaperLogic logic, final Board board, final GeneratorBoard generator){
        this.generator=generator;
        this.board=board;
        this.logic=logic;
    }

    public void initGame() {
        MainActivity.stopGame=false;
        this.cells = generator.generate(); //get the generated field
        this.board.drawBoard(cells); //load it into the board
        this.logic.loadBoard(cells);//load it into the logic
    }

    @Override
    public void select(int x, int y, boolean bomb) {
        if (x < cells.length && y < cells[0].length) {
            MainActivity.btnInit.setImageResource(R.drawable.btnstep);
            this.logic.suggest(x, y, bomb);//load the user's choice in the logic


            board.drawCell(x, y); //draw this cell


            if (this.logic.shouldBang(x, y)) {
                this.board.drawBang(); // if need to explode, then explode
            } else if (this.logic.finish()) {
                board.drawCongratulate(); //If all is well, then congratulations
            } else {
            /*** We got to an empty cell and open cell standing next ***/
                if (this.logic.getCountMines(x, y) == 0 && bomb == false) {
                    if (x != 0 && y != 0
                            && this.cells[x - 1][y - 1].isSuggestEmpty() == false
                            && this.cells[x - 1][y - 1].isSuggestBomb() == false) {
                        select(x - 1, y - 1, false);
                    }
                    if (y != 0
                            && this.cells[x][y - 1].isSuggestEmpty() == false
                            && this.cells[x][y - 1].isSuggestBomb() == false) {
                        select(x, y - 1, false);
                    }
                    if (x != cells.length - 1
                            && y != 0
                            && this.cells[x + 1][y - 1].isSuggestEmpty() == false
                            && this.cells[x + 1][y - 1].isSuggestBomb() == false) {
                        select(x + 1, y - 1, false);
                    }
                    if (x != 0
                            && this.cells[x - 1][y].isSuggestEmpty() == false
                            && this.cells[x - 1][y].isSuggestBomb() == false) {
                        select(x - 1, y, false);
                    }
                    if (x != cells.length - 1
                            && this.cells[x + 1][y].isSuggestEmpty() == false
                            && this.cells[x + 1][y].isSuggestBomb() == false) {
                        select(x + 1, y, false);
                    }
                    if (x != 0 && y != cells[x].length - 1
                            && this.cells[x - 1][y + 1].isSuggestEmpty() == false
                            && this.cells[x - 1][y + 1].isSuggestBomb() == false) {
                        select(x - 1, y + 1, false);
                    }
                    if (y != cells[x].length - 1
                            && this.cells[x][y + 1].isSuggestEmpty() == false
                            && this.cells[x][y + 1].isSuggestBomb() == false) {
                        select(x, y + 1, false);
                    }
                    if (x != cells.length - 1 && y != cells[x].length - 1
                            && this.cells[x + 1][y + 1].isSuggestEmpty() == false
                            && this.cells[x + 1][y + 1].isSuggestBomb() == false) {
                        select(x + 1, y + 1, false);
                    }
                }
            }
        }
    }
}
