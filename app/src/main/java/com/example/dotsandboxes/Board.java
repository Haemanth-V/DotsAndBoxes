package com.example.dotsandboxes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Board extends View {

    private Paint paints[], lastMovePaint, circlePaint;
    private static int STROKE_WIDTH = 12;
    private int numPlyrs, row, column, touchStatus;
    private static final String colors[] = {"#D50000", "#00C853", "#0336FF", "#FFD600",
            "#880E4F", "#FF80AB", "#1B5E20", "#40C4FF", "#FF9100"};
    private static final String lastMoveColor = "#3E2723", circleColor = "#212121";
    private int height, width, turn;
    private float radius, spaceHorizontal, spaceVertical;
    private int horizontalDashes[][];
    private int verticalDashes[][];
    private int boxes[][];
    private ArrayList movesInOrder;
    private int playerScores[],computerscores;
    private int line[];
    private Play playActivity;
    private Random rand;
    private static boolean gameOver;

    public Board(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Board(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paints = new Paint[9];
        for (int i = 0; i < 9; i++) {
            paints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paints[i].setColor(Color.parseColor(colors[i]));
            paints[i].setStyle(Paint.Style.STROKE);
            paints[i].setStrokeWidth(STROKE_WIDTH);
        }
        lastMovePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lastMovePaint.setColor(Color.parseColor(lastMoveColor));
        lastMovePaint.setStyle(Paint.Style.STROKE);
        lastMovePaint.setStrokeWidth(STROKE_WIDTH);
        circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor(circleColor));
        circlePaint.setStyle(Paint.Style.FILL);

    }

    public void initialise(int r, int c, int n) {
        row = r;
        column = c;
        numPlyrs = n;
        turn = 1;
        rand = new Random();
        touchStatus = 0;
        gameOver = false;
        computerscores = 0;
        if (row > 10 || column > 10)
            radius = 18f;
        else
            radius = 25f;
        horizontalDashes = new int[row][column - 1];
        verticalDashes = new int[row - 1][column];
        playerScores = new int[numPlyrs];
        boxes = new int[row - 1][column - 1];
        line = new int[4];
        movesInOrder = new ArrayList();
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column - 1; j++)
                horizontalDashes[i][j] = -1;
        for (int i = 0; i < row - 1; i++)
            for (int j = 0; j < column; j++)
                verticalDashes[i][j] = -1;
        for (int i = 0; i < row - 1; i++)
            for (int j = 0; j < column - 1; j++)
                boxes[i][j] = -1;
        for (int i = 0; i < playerScores.length; i++)
            playerScores[i] = 0;
        for (int i = 0; i < 4; i++)
            line[i] = -1;
    }

    public void setPlayActivity(Play activity) {
        playActivity = activity;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        height = View.MeasureSpec.getSize(heightMeasureSpec);
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        spaceHorizontal = (width - radius * 2 * column) * 1.0F / (column + 1);
        spaceVertical = (height - radius * 2 * row) * 1.0f / (row + 1);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Color boxes
        drawBoxes(canvas);
        //Draw Lines (dashes)
        drawLines(canvas);
        //Draw circles (dots)
        drawDots(canvas);

    }

    private void drawDots(Canvas canvas) {
        float cx, cy;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                cx = (radius * 2 + spaceHorizontal) * j + radius + spaceHorizontal;
                cy = (radius * 2 + spaceVertical) * i + radius + spaceVertical;
                canvas.drawCircle(cx, cy, radius, circlePaint);
            }
        }
    }

    private void drawLines(Canvas canvas) {
        float startX, startY, endX, endY;
        //horizontalDashes[2][3]=1;
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column - 1; j++) {
                if (horizontalDashes[i][j] != -1) {
                    startX = (radius * 2 + spaceHorizontal) * j + radius + spaceHorizontal;
                    startY = (radius * 2 + spaceVertical) * i + radius + spaceVertical;
                    endX = startX + radius * 2 + spaceHorizontal;
                    endY = startY;
                    if (horizontalDashes[i][j] == 0)
                        canvas.drawLine(startX, startY, endX, endY, lastMovePaint);
                    else
                        canvas.drawLine(startX, startY, endX, endY, paints[horizontalDashes[i][j] - 1]);
                }

            }
        //verticalDashes[2][3]=4;
        //verticalDashes[2][4]=2;
        for (int i = 0; i < row - 1; i++)
            for (int j = 0; j < column; j++) {
                if (verticalDashes[i][j] != -1) {
                    startX = (radius * 2 + spaceHorizontal) * j + radius + spaceHorizontal;
                    startY = (radius * 2 + spaceVertical) * i + radius + spaceVertical;
                    endX = startX;
                    endY = startY + 2 * radius + spaceVertical;
                    if (verticalDashes[i][j] == 0)
                        canvas.drawLine(startX, startY, endX, endY, lastMovePaint);
                    else
                        canvas.drawLine(startX, startY, endX, endY, paints[verticalDashes[i][j] - 1]);
                }

            }
    }

    private void drawBoxes(Canvas canvas) {
        // boxes[2][3]=2;
        for (int i = 0; i < row - 1; i++)
            for (int j = 0; j < column - 1; j++) {
                if (boxes[i][j] != -1) {
                    float left = (radius * 2 + spaceHorizontal) * j + 2 * radius + spaceHorizontal;
                    float top = (radius * 2 + spaceVertical) * i + 2 * radius + spaceVertical;
                    float right = left + spaceHorizontal;
                    float bottom = top + spaceVertical;
                    paints[boxes[i][j] - 1].setStyle(Paint.Style.FILL);
                    //Select lighter shades for boxes
                    canvas.drawRect(left, top, right, bottom, paints[boxes[i][j] - 1]);
                    paints[boxes[i][j] - 1].setStyle(Paint.Style.STROKE);
                }
            }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float cx, cy, distance = radius + 10;
        char direction = ' ';

        if (!gameOverCheck() && event.getAction() == MotionEvent.ACTION_DOWN) {
            playActivity.displayDetails(turn, playerScores[turn - 1]);
            return true;
        }

        if (!gameOverCheck() && event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            for (int i = 0; i < row; i++) {
                int flag = 0;
                for (int j = 0; j < column; j++) {
                    cx = (radius * 2 + spaceHorizontal) * j + radius + spaceHorizontal;
                    cy = (radius * 2 + spaceVertical) * i + radius + spaceVertical;
                    distance = (cx - x) * (cx - x) + (cy - y) * (cy - y);
                    if (distance <= radius * radius) {

                        if (line[0] == -1) {
                            line[0] = i;
                            line[1] = j;
                            playActivity.displayDetails(turn, playerScores[turn - 1]);
                            return true;
                        } else if ((line[0] == i && line[1] == j - 1 && j - 1 >= 0)
                                || (i - 1 >= 0 && line[0] == i - 1 && line[1] == j)) {
                            if (line[0] == i) {
                                line[2] = i;
                                line[3] = j;
                                direction = 'h';
                            } else if (line[1] == j) {
                                line[2] = i;
                                line[3] = j;
                                direction = 'v';
                            }
                            flag++;
                            break;
                        } else if ((line[0] == i && line[1] == j + 1)
                                || (line[0] == i + 1 && line[1] == j)) {
                            if (line[0] == i) {
                                line[2] = line[0];
                                line[3] = line[1];
                                line[0] = i;
                                line[1] = j;
                                direction = 'h';

                            } else if (line[1] == j) {
                                line[2] = line[0];
                                line[3] = line[1];
                                line[0] = i;
                                line[1] = j;
                                direction = 'v';
                            }
                            flag++;
                            break;
                        }
                    }
                }
                if (flag != 0)
                    break;
            }
            if (line[0] == -1) {
                playActivity.displayDetails(turn, playerScores[turn - 1]);
                return super.onTouchEvent(event);
            } else if (line[3] == -1) {
                playActivity.displayDetails(turn, playerScores[turn - 1]);
                return true;
            } else if ((direction == 'h' && horizontalDashes[line[0]][line[1]] == -1) ||
                    (direction == 'v' && verticalDashes[line[0]][line[1]] == -1)) {
                int arr[] = {-1, line[0], line[1]};
                if (direction == 'h') {
                    horizontalDashes[line[0]][line[1]] = 0;
                    arr[0] = 1;
                } else if (direction == 'v') {
                    verticalDashes[line[0]][line[1]] = 0;
                    arr[0] = 2;
                }
                movesInOrder.add(arr);
                boolean box = updateBoxes(line[0], line[1], direction);
                if (box) {
                    if (direction == 'h')
                        horizontalDashes[line[0]][line[1]] = turn;
                    else
                        verticalDashes[line[0]][line[1]] = turn;
                    postInvalidate();
                } else {
                    postInvalidate();
                    if (direction == 'h')
                        horizontalDashes[line[0]][line[1]] = turn;
                    else
                        verticalDashes[line[0]][line[1]] = turn;
                    turn = (turn + 1) % numPlyrs;
                    if (turn == 0)
                        turn = numPlyrs;
                    if (numPlyrs == 1) {
                        do {
                            box = deviceEasy();
                        } while (box);
                        turn = 1;
                    }
                }
             }
        }
        for (int i = 0; i < 4; i++)
            line[i] = -1;
        gameOverCheck();
        playActivity.displayDetails(turn, playerScores[turn - 1]);
        return super.onTouchEvent(event);
    }

    public boolean gameOverCheck() {
        if (gameOver)
            return true;
        for (int i = 0; i < row - 1; i++) {
            for (int j = 0; j < column - 1; j++) {
                if (boxes[i][j] == -1)
                    return false;
            }
        }
        gameOver = true;
        int winner = 0;
        if (numPlyrs == 1) {
            if (playerScores[0] < computerscores)
                playActivity.gameOver(-1);
            else if (playerScores[0] == computerscores)
                playActivity.gameOver(0);
            else playActivity.gameOver(1);
        } else {
            for (int i = 1; i < numPlyrs; i++)
                if (playerScores[winner] < playerScores[i])
                    winner = i;
            for (int i = 0; i < numPlyrs; i++)
                if (winner != i && playerScores[winner] == playerScores[i]) {
                    winner = -1;
                    break;
                }
            playActivity.displayDetails(turn, playerScores[turn-1]);
            playActivity.gameOver(winner + 1);
        }
        return true;
    }

    private boolean updateBoxes(int i, int j, char direction) {
        boolean box = false;
        if (direction == 'h') {
            if (i - 1 >= 0 && horizontalDashes[i - 1][j] != -1 &&
                    verticalDashes[i - 1][j] != -1 && verticalDashes[i - 1][j + 1] != -1) {
                boxes[i - 1][j] = turn;
                if(turn <= playerScores.length)
                    playerScores[turn-1]++;
                else computerscores++;
                box = true;
            }
            if (i + 1 < row && horizontalDashes[i + 1][j] != -1 &&
                    verticalDashes[i][j] != -1 && verticalDashes[i][j + 1] != -1) {
                boxes[i][j] = turn;
                if(turn <= playerScores.length)
                    playerScores[turn-1]++;
                else computerscores++;
                box = true;
            }
        } else {
            if (j - 1 >= 0 && verticalDashes[i][j - 1] != -1 &&
                    horizontalDashes[i][j - 1] != -1 && horizontalDashes[i + 1][j - 1] != -1) {
                boxes[i][j - 1] = turn;
                if(turn <= playerScores.length)
                    playerScores[turn-1]++;
                else computerscores++;
                box = true;
            }
            if (j + 1 < column && verticalDashes[i][j + 1] != -1 &&
                    horizontalDashes[i][j] != -1 && horizontalDashes[i + 1][j] != -1) {
                boxes[i][j] = turn;
                if(turn <= playerScores.length)
                    playerScores[turn-1]++;
                else computerscores++;
                box = true;
            }
        }
        return box;
    }

    private boolean deviceEasy() {
        int r, c, direction;
        char t;
        if (!gameOverCheck()) {
            int flag = 0;
            do{
                direction = rand.nextInt(2) + 1;
                if (direction == 1) {
                    t = 'h';
                    r = rand.nextInt(row);
                    c = rand.nextInt(column - 1);
                    if(horizontalDashes[r][c]==-1) {
                        horizontalDashes[r][c] = 2;
                        flag++;
                    }
                }
                else {
                    t = 'v';
                    r = rand.nextInt(row - 1);
                    c = rand.nextInt(column);
                    if(verticalDashes[r][c] ==-1) {
                        verticalDashes[r][c] = 2;
                        flag++;
                    }
                }
            }while(flag==0);
            turn = 2;
            int arr[] = {direction, r, c, 0};
            movesInOrder.add(arr);
            return updateBoxes(r, c, t);
        } else return false;
    }

    public void undo() {

        int arrSize=0;
        if (!gameOverCheck()) {
            do {
                if (movesInOrder != null && movesInOrder.size() != 0) {
                    int arr[] = (int[]) movesInOrder.remove((int) movesInOrder.size() - 1);
                    arrSize = arr.length;
                    int flag = 0;
                    if (arr[0] == 1) {
                        horizontalDashes[arr[1]][arr[2]] = -1;
                        if (arr[1] != row - 1 && boxes[arr[1]][arr[2]] != -1) {
                            boxes[arr[1]][arr[2]] = -1;
                            flag++;
                        }
                        if (arr[1] - 1 >= 0)
                            if (boxes[arr[1] - 1][arr[2]] != -1) {
                                boxes[arr[1] - 1][arr[2]] = -1;
                                flag++;
                            }
                    } else {
                        verticalDashes[arr[1]][arr[2]] = -1;
                        if (arr[2] != column - 1 && boxes[arr[1]][arr[2]] != -1) {
                            boxes[arr[1]][arr[2]] = -1;
                            flag++;
                        }
                        if (arr[2] - 1 >= 0)
                            if (boxes[arr[1]][arr[2] - 1] != -1) {
                                boxes[arr[1]][arr[2] - 1] = -1;
                                flag++;
                            }
                    }
                    if (flag == 0) {
                        turn = (turn - 1) % numPlyrs;
                        if (turn == 0)
                            turn = numPlyrs;
                    } else
                        playerScores[turn - 1]--;
                }
                postInvalidate();
            }while(arrSize!=3);
            playActivity.displayDetails(turn, playerScores[turn - 1]);
        }
    }
}