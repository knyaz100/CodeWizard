import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public final class MyStrategy implements Strategy {

    public int WIDTH = 4000;
    public int HEIGHT = 4000;
    private Wizard self;
    private World world;
    private Game game;
    private Move move;

    private void goTo(Cell cell) {
        double angle = self.getAngleTo(cell.getX(), cell.getY());
        move.setTurn(angle);
        if (StrictMath.abs(angle) < game.getStaffSector() / 4.0D) {
            move.setSpeed(game.getWizardForwardSpeed());
        }
    }

    private void initTick(Wizard self, World world, Move move, Game game) {
        this.self = self;
        this.world = world;
        this.game = game;
        this.move = move;
    }

    @Override
    public void move(Wizard self, World world, Game game, Move move) {
        initTick(self, world, move, game);
        Cell[][] cellList = new Cell[WIDTH][HEIGHT];
        LinkedList<Cell> currentCell = new LinkedList<Cell>();
        ArrayList<LivingUnit> objects = new ArrayList<>();
        objects.addAll(Arrays.asList(world.getBuildings()));
        objects.addAll(Arrays.asList(world.getTrees()));
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (!objects.contains(cellList[j][i])) {
                    cellList[j][i] = new Cell(j, i, false);
                }
            }
        }
        cellList[(int) self.getX()][(int) self.getY()] = new Cell((int) self.getX(), (int) self.getY(), false);
        Cell start = cellList[(int) self.getX()][(int) self.getY()];
        Cell finish = cellList[2000][2000];
        boolean isFounded = false;
        boolean isNoroute = false;
        currentCell.push(start);
        while (!isFounded && !isNoroute) {
            Cell min = currentCell.getFirst();
            for (Cell cell : currentCell) {
                if (cell.F < min.F) min = cell;
            }
            currentCell.remove(min);
            currentCell.clear();
            currentCell.add(cellList[min.x - 1][min.y - 1]);
            currentCell.add(cellList[min.x][min.y - 1]);
            currentCell.add(cellList[min.x + 1][min.y - 1]);
            currentCell.add(cellList[min.x + 1][min.y]);
            currentCell.add(cellList[min.x + 1][min.y + 1]);
            currentCell.add(cellList[min.x][min.y + 1]);
            currentCell.add(cellList[min.x - 1][min.y + 1]);
            currentCell.add(cellList[min.x - 1][min.y]);
            for (Cell neighbour : currentCell) {
                if (neighbour.isBlocked || !currentCell.contains(neighbour)) continue;
                if (currentCell.contains(neighbour)) {
                    neighbour.parent = min;
                    neighbour.H = (int) neighbour.mandist(finish);
                    neighbour.G = start.price(min);
                    neighbour.F = neighbour.H + neighbour.G;
                    continue;
                }
                if (neighbour.G + neighbour.price(min) < min.G) {
                    neighbour.parent = min;
                    neighbour.H = (int) neighbour.mandist(finish);
                    neighbour.G = start.price(min);
                    neighbour.F = neighbour.H + neighbour.G;
                }
                goTo(min);
            }
        }
        if (currentCell.contains(finish)) {
            isFounded = true;
        }
        if (currentCell.isEmpty()) {
            isNoroute = true;
        }
        if (!isNoroute) {
            Cell rd = finish.parent;
            while (!rd.equals(start)) {
                rd.road = true;
                rd = rd.parent;
                goTo(rd);
                if (rd == null) break;
            }
        } else System.out.println("No more route");
    }

    class Cell {
        public Cell parent = this;
        int x = 4000;
        int y = 0;
        private boolean isBlocked = false;
        private boolean start = false;
        private boolean finish = false;
        private boolean road = false;
        private double F = 0;
        private double G = 0;
        private double H = 0;

        public Cell(int x, int y, boolean isBlocked) {
            this.x = x;
            this.y = y;
            this.isBlocked = isBlocked;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setBlocked(boolean blocked) {
            isBlocked = blocked;
        }

        private void setStart() {
            this.start = true;
        }

        private void setFinish() {
            this.finish = true;
        }


        private double mandist(Cell finish) {
            // finish - конечная клетка
            return 10 * (Math.abs(this.x - finish.x) + Math.abs(this.y - finish.y));
        }

        private int price(Cell finish) {
            if (this.x == finish.x || this.y == finish.y) {
                return 10;
            } else return 14;
        }

        private boolean equalsCellSame(Cell cell) {
            return (this.x == cell.x) && (this.y == cell.y);
        }
    }
}
