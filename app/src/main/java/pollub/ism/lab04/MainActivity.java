package pollub.ism.lab04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    public enum Player {
        Circle(0), Cross(1);

        private int value;

        private Player(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    protected int reloadTime = 3;
    Map<Player, String> signs = new TreeMap<>();

    protected char[][] board = new char[3][3];
    protected Player activePlayer = Player.Circle;
    protected boolean gameFinished = false;

    protected MainActivity thatActivity = this;
    protected Toast toast;

    private String KEY_ACTIVEPLAYER = "Aktualny_gracz", KEY_ROW1 = "Wiersz_1", KEY_ROW2 = "Wiersz_2",
        KEY_ROW3 = "Wiersz_3", KEY_GAMEFINISHED = "Stan_gry", KEY_RELOADINGTIME;

    public class ReloadingToast extends TimerTask {
        public void run() {
            String reloadText = "Gra uruchomi się ponownie za... "+Integer.toString(reloadTime) + " sekundy.";
            thatActivity.runOnUiThread(new Runnable() {
                public void run() {
                    toast.setText(reloadText);
                }
            });
            reloadTime--;
            if (reloadTime < 0) {
                reloadGame();
                this.cancel();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signs.put(Player.Circle, "O");
        signs.put(Player.Cross, "X");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public void kliknieciePrzycisku(View view) {
        String id = view.getResources().getResourceEntryName(view.getId());
        String[] arr = id.split("_");
        int row = Integer.parseInt(arr[1]) - 1;
        int column = Integer.parseInt(arr[2]) - 1;

        Button button = (Button) (view);

        if (board[row][column] == ' ' && !(gameFinished)) {
            board[row][column] = signs.get(activePlayer).charAt(0);
            button.setText(Character.toString(board[row][column]));

            activePlayer = Player.values()[(activePlayer.getValue() + 1) % 2];

            checkWhoWins();
        }
    }

    private void checkWhoWins () {
        char winningChar = ' ';

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] &&
            board[0][0] != ' ') {
                winningChar = board[0][0];
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] &&
            board[0][2] != ' ') {
                winningChar = board[0][2];
        }
        for (int i = 0; i < 3; i++) {
            if (winningChar != ' ') break;
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] &&
                board[i][0] != ' ') {
                    winningChar = board[i][0]; break;
            }
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] &&
                board[0][i] != ' ') {
                    winningChar = board[0][i]; break;
            }
        }

        boolean draw = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    draw = false;
                    break;
                }
            }
        }

        if (winningChar != ' ' || draw == true) endGame(winningChar);
    }

    private void endGame (char winningChar) {
        String endGameText = "Wygrały " + winningChar;

        if (winningChar == ' ') {
            endGameText = "Remis";
        }
        toast = Toast.makeText(this, endGameText, Toast.LENGTH_LONG);
        toast.show();

        gameFinished = true;

        Timer timer = new Timer();
        timer.schedule(new ReloadingToast(), 1000, 1000);
    }

    private void reloadGame () {
        reloadTime = 3;
        gameFinished = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                Resources res = getResources();
                String str = "button_"+Integer.toString(i+1)+"_"+Integer.toString(j+1);
                int id = res.getIdentifier(str, "id", getApplicationContext().getPackageName());
                Button btn_tmp = (Button) findViewById(id);
                if (btn_tmp != null)
                    btn_tmp.setText(" ");
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACTIVEPLAYER, activePlayer.getValue());
        outState.putCharArray(KEY_ROW1, board[0]);
        outState.putCharArray(KEY_ROW2, board[1]);
        outState.putCharArray(KEY_ROW3, board[2]);
        outState.putBoolean(KEY_GAMEFINISHED, gameFinished);
        outState.putInt(KEY_RELOADINGTIME, reloadTime);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        activePlayer = Player.values()[savedInstanceState.getInt(KEY_ACTIVEPLAYER, 0)];
        board[0] = savedInstanceState.getCharArray(KEY_ROW1);
        board[1] = savedInstanceState.getCharArray(KEY_ROW2);
        board[2] = savedInstanceState.getCharArray(KEY_ROW3);
        gameFinished = savedInstanceState.getBoolean(KEY_GAMEFINISHED, false);
        reloadTime = savedInstanceState.getInt(KEY_RELOADINGTIME, 3);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Resources res = getResources();
                String str = "button_"+Integer.toString(i+1)+"_"+Integer.toString(j+1);
                int id = res.getIdentifier(str, "id", getApplicationContext().getPackageName());
                Button btn_tmp = (Button) findViewById(id);
                if (btn_tmp != null)
                    btn_tmp.setText(""+board[i][j]);
            }
        }

        if (gameFinished == true) {
            Timer timer = new Timer();
            toast = Toast.makeText(this, "Gra uruchomi się ponownie za... "+Integer.toString(reloadTime) + " sekundy.", Toast.LENGTH_LONG);
            timer.schedule(new ReloadingToast(), 1000, 1000);
        }
    }
}