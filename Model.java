import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;



public class Model extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;

    private final int block_size = 24;
    private final int n_blocks = 15;
    private final int screen_size = n_blocks * block_size;
    private final int max_gun = 12;
    private final int player_SPEED = 6;

    private int n_gun = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] gun_x, gun_y, gun_dx, gun_dy, gunSpeed;

    private Image heart, gun;
    private Image up, down, left, right;

    private int player_x, player_y, playerd_x, playerd_y;
    private int req_dx, req_dy;

    private final short levelData[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 16, 16, 16, 24, 24, 24, 24, 24, 24, 28,
            17, 16, 16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0,  0,  0,
            17, 16, 16, 16, 16, 16, 16, 16, 18, 18, 22,  0, 19, 18, 22,
            25, 24, 24, 24, 24, 16, 16, 16, 16, 16, 20,  0, 17, 16, 20,
            0,   0,  0,  0,  0, 17, 16, 16, 16, 16, 20,  0, 17, 16, 20,
            19, 18, 18, 18, 18, 16, 16, 16, 16, 16, 16, 18, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 24, 24, 24, 24, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 20, 19, 18, 18, 22, 17, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 20, 17, 16, 16, 20, 17, 20,
            17, 24, 24, 24, 16, 16, 16, 16, 20, 17, 16, 16, 20, 17, 20,
            21,  0,  0,  0, 25, 24, 16, 16, 20, 25, 16, 16, 28, 17, 20,
            21,  0,  0,  0,  0,  0, 17, 16, 16, 18, 16, 16, 18, 16, 20,
            17, 18, 18, 18, 18, 18, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28

    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 8;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    //Audio Implementation
    String filePath = "src/mobile_song.wav";

    AudioPlayer audioPlayer = new AudioPlayer(filePath);
    //==================================================



    public Model() {

        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();

    }


    private void loadImages() {
        down = new ImageIcon("src/mobile.gif").getImage();
        up = new ImageIcon("src/mobile.gif").getImage();
        left = new ImageIcon("src/mobile.gif").getImage();
        right = new ImageIcon("src/mobile.gif").getImage();
        gun = new ImageIcon("src/gun.gif").getImage();
        heart = new ImageIcon("src/heart.png").getImage();

    }
    private void initVariables() {

        screenData = new short[n_blocks * n_blocks];
        d = new Dimension(400, 400);
        gun_x = new int[max_gun];
        gun_dx = new int[max_gun];
        gun_y = new int[max_gun];
        gun_dy = new int[max_gun];
        gunSpeed = new int[max_gun];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    private void playGame(Graphics2D g2d) {

        if (dying) {

            death();

        } else {

            moveplayer();
            drawplayer(g2d);
            movegun(g2d);
            checkMaze();

            if (score > 160){
                inGame = false;
                showWinningScreen(g2d);
            }

        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        String start = "Press SPACE to start";

        g2d.setColor(Color.yellow);
        g2d.drawString(start, (screen_size)/4, 150);

        String start2 = "Get 160 Points to Win!";
        g2d.setColor(Color.yellow);
        g2d.drawString(start2, (screen_size)/4, 170);
    }

    private void showWinningScreen(Graphics2D g2d) {

        String start = "Congratulations You've Won!";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (screen_size)/4, 150);
    }

    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, screen_size / 2 + 96, screen_size + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, screen_size + 1, this);
        }
    }

    private void checkMaze() {

        int i = 0;
        boolean finished = true;

        while (i < n_blocks * n_blocks && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (n_gun < max_gun) {
                n_gun++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();


        }
    }

    private void death() {

        lives--;
        String filePath = "src/gunshot.wav";

        // Create an AudioPlayer instance
        AudioPlayer audioPlayer = new AudioPlayer(filePath);

        // Play the audio only if it's not already playing
        audioPlayer.play();




        if (lives == 0) {

            String filePath1 = "src/reload.wav";
            // Create an AudioPlayer instance
            AudioPlayer audioPlayer1 = new AudioPlayer(filePath1);
            audioPlayer1.play();

            String filePath2 = "src/mobile_nikaal.wav";
            // Create an AudioPlayer instance
            AudioPlayer audioPlayer2 = new AudioPlayer(filePath2);
            audioPlayer2.play();


            inGame = false;
        }

        continueLevel();
    }

    private void movegun(Graphics2D g2d) {

        int pos;
        int count;

        //Collision
        for (int i = 0; i < n_gun; i++) {
            if (gun_x[i] % block_size == 0 && gun_y[i] % block_size == 0) {
                pos = gun_x[i] / block_size + n_blocks * (int) (gun_y[i] / block_size);

                count = 0;

                if ((screenData[pos] & 1) == 0 && gun_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && gun_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && gun_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && gun_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        gun_dx[i] = 0;
                        gun_dy[i] = 0;
                    } else {
                        gun_dx[i] = -gun_dx[i];
                        gun_dy[i] = -gun_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    gun_dx[i] = dx[count];
                    gun_dy[i] = dy[count];
                }

            }

            gun_x[i] = gun_x[i] + (gun_dx[i] * gunSpeed[i]);
            gun_y[i] = gun_y[i] + (gun_dy[i] * gunSpeed[i]);
            drawgun(g2d, gun_x[i] + 1, gun_y[i] + 1);

            if (player_x > (gun_x[i] - 12) && player_x < (gun_x[i] + 12)
                    && player_y > (gun_y[i] - 12) && player_y < (gun_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawgun(Graphics2D g2d, int x, int y) {
        g2d.drawImage(gun, x, y, this);
    }

    private void moveplayer() {

        int pos;
        short ch;

        if (player_x % block_size == 0 && player_y % block_size == 0) {
            pos = player_x / block_size + n_blocks * (int) (player_y / block_size);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    playerd_x = req_dx;
                    playerd_y = req_dy;
                }
            }

            // Check for standstill
            if ((playerd_x == -1 && playerd_y == 0 && (ch & 1) != 0)
                    || (playerd_x == 1 && playerd_y == 0 && (ch & 4) != 0)
                    || (playerd_x == 0 && playerd_y == -1 && (ch & 2) != 0)
                    || (playerd_x == 0 && playerd_y == 1 && (ch & 8) != 0)) {
                playerd_x = 0;
                playerd_y = 0;
            }
        }
        player_x = player_x + player_SPEED * playerd_x;
        player_y = player_y + player_SPEED * playerd_y;
    }

    private void drawplayer(Graphics2D g2d) {

        if (req_dx == -1) {
            g2d.drawImage(left, player_x + 1, player_y + 1, this);
        } else if (req_dx == 1) {
            g2d.drawImage(right, player_x + 1, player_y + 1, this);
        } else if (req_dy == -1) {
            g2d.drawImage(up, player_x + 1, player_y + 1, this);
        } else {
            g2d.drawImage(down, player_x + 1, player_y + 1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < screen_size; y += block_size) {
            for (x = 0; x < screen_size; x += block_size) {

                g2d.setColor(new Color(255,127,80));
                g2d.setStroke(new BasicStroke(5));

                if ((levelData[i] == 0)) {
                    g2d.fillRect(x, y, block_size, block_size);
                }

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + block_size - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + block_size - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + block_size - 1, y, x + block_size - 1,
                            y + block_size - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + block_size - 1, x + block_size - 1,
                            y + block_size - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }

    private void initGame() {

        lives = 3;
        score = 0;
        initLevel();
        n_gun = 6;
        currentSpeed = 3;
    }

    private void initLevel() {

        int i;
        for (i = 0; i < n_blocks * n_blocks; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {

        int dx = 1;
        int random;

        for (int i = 0; i < n_gun; i++) {

            gun_y[i] = 4 * block_size; //start position
            gun_x[i] = 4 * block_size;
            gun_dy[i] = 0;
            gun_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            gunSpeed[i] = validSpeeds[random];
        }

        player_x = 7 * block_size;  //start position
        player_y = 11 * block_size;
        playerd_x = 0;	//reset direction move
        playerd_y = 0;
        req_dx = 0;		// reset direction controls
        req_dy = 0;
        dying = false;
    }







    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            audioPlayer.play();
            playGame(g2d);
        } else if (score > 160){
            showWinningScreen(g2d);
        }
        else {
            audioPlayer.stop();
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    //controls
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}
