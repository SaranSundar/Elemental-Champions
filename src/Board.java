import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

public class Board extends JPanel implements ActionListener {

    private KeyListener keyListener;
    private MouseListener mouseListener;
    private int SCREEN_WIDTH = 800;
    private int SCREEN_HEIGHT = 600;
    private int BLOCK_WIDTH = 40;
    private int BLOCK_HEIGHT = 30;
    private int DELAY = 8;
    private Timer timer;
    private int foodTimer = 100;
    private int foodCounter = 0;
    private Stroke gridStroke;
    private Dimension screenDimensions;
    private boolean inGame;
    private Champion player1;
    private Champion player2;
    private boolean startOnce = true;
    private int timeCount = 0;
    private ArrayList<Champion> players;
    private ArrayList<Rectangle> playerRect;
    private HashSet<Rectangle> collisionsList;
    private ArrayList<Block> food;
    private boolean test = false;
    private boolean startOfOnce = true;
    private int xB, yB;
    private Random random;
    private Font small;
    private FontMetrics metr;
    private int startSplashTimer = 0;
    private boolean mainScreen = false;
    private HashMap<Integer, Integer> player2Map;
    private HashMap<Integer, Integer> player1Map;
    private boolean splashPart1 = true;
    private BufferedImage ss1, ss2, mainMenuScreen, highScoreScreen, tutorialScreen, settingsScreen, credits, settings2Screen, gameOverScreen, backgroundScreen, p1Screen, p2Screen;
    private String[] names;
    private ConcurrentHashMap<String, Font> cache;
    private boolean inTutorial = false;
    private boolean inHighScore = false;
    private boolean inSettings = false;
    private BufferedReader kb;
    private PrintWriter out;
    private ArrayList<String> highScores;
    private Color fontC;
    private boolean shouldUpdateHighScores = false;
    private boolean isChoosingColor = false;
    private boolean isP1Choosing = false;
    private ArrayList<Color> colorChoices;
    private Color p1ColorChoice, p2ColorChoice;
    private int countDownTimer = 30;
    private int countDInterval = 60;
    private int countDCount = 0;
    private boolean isGameOver = false;
    private Font scoreF, lastSF;
    private Preferences prefs;
    private boolean playMusic = true;
    private boolean inCredits = false;

    public Board() {
        setup();
        say();
    }

    public void setupMusicPlayer() {
        Sound.sound2.loop();
        Sound.sound2.setVolume(1);
    }

    public void setup() {
        setBackground(Color.black);
        setFocusable(true);
        screenDimensions = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
        setPreferredSize(screenDimensions);
        setupFont();
        setupImages();
        createPlayers();
        readHighScores();
        setupColors();
        gridStroke = new BasicStroke(1);
        keyListener = new KeyListener();
        mouseListener = new MouseListener();

        addMouseListener(mouseListener);
        addKeyListener(keyListener);
        setupMusicPlayer();
        timer = new Timer(DELAY, this);
        timer.start();
    }


    public void readHighScores() {
        try {
            InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("Data/HighScores.txt"));
            kb = new BufferedReader(in);
            highScores = new ArrayList<String>();
            String line = "";
            highScores = new ArrayList<String>();
            while ((line = kb.readLine()) != null) {
                highScores.add(line);
            }
            kb.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void say() {
        String className = this.getClass().getName().replace('.', '/');
        String classJar =
                this.getClass().getResource("/" + className + ".class").toString();
        if (classJar.startsWith("jar:")) {
            System.out.println("*** running from jar!");
        }
        System.out.println(classJar);
    }

    public void saveHighScores() {
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter("src/Data/HighScores.txt")));
            for (int i = 0; i < highScores.size(); i++) {
                out.println(highScores.get(i));
            }
            console("Finished Saving Highscores");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage loadAndRetrieveImage(String fileName) {
        try {
            InputStream resourceAsStream = getClass().getResourceAsStream(fileName);
            BufferedImage img = ImageIO.read(resourceAsStream);
            return img;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setupImages() {
        ss1 = loadAndRetrieveImage("Images/ss1.png");
        ss2 = loadAndRetrieveImage("Images/ss2.png");
        mainMenuScreen = loadAndRetrieveImage("Images/mainMenuScreen.png");
        settingsScreen = loadAndRetrieveImage("Images/setting1.png");
        settings2Screen = loadAndRetrieveImage("Images/setting2.png");
        tutorialScreen = loadAndRetrieveImage("Images/tutorialScreen.png");
        highScoreScreen = loadAndRetrieveImage("Images/highScoreScreen.png");
        gameOverScreen = loadAndRetrieveImage("Images/gameOverScreen.png");
        backgroundScreen = loadAndRetrieveImage("Images/backgroundScreen.png");
        p1Screen = loadAndRetrieveImage("Images/p1choosing.png");
        p2Screen = loadAndRetrieveImage("Images/p2choosing.png");
        credits = loadAndRetrieveImage("Images/Credits.png");
    }

    public void splashScreen(Graphics g) {
        drawSplashScreen(g);
        int splash1Limit = 150;
        int splash2Limit = 300;
        if (startSplashTimer > splash1Limit && startSplashTimer <= splash2Limit) {
            splashPart1 = false;
        } else if (startSplashTimer > splash2Limit) {
            startSplashTimer = 0;
            startOfOnce = false;
            mainScreen = true;
        }
        startSplashTimer++;
    }


    public void createPlayers() {
        player1Map = new HashMap<Integer, Integer>();
        player1Map.put(KeyEvent.VK_LEFT, 0);
        player1Map.put(KeyEvent.VK_RIGHT, 1);
        player1Map.put(KeyEvent.VK_UP, 2);
        player1Map.put(KeyEvent.VK_DOWN, 3);

        player2Map = new HashMap<Integer, Integer>();
        player2Map.put(KeyEvent.VK_A, 0);
        player2Map.put(KeyEvent.VK_D, 1);
        player2Map.put(KeyEvent.VK_W, 2);
        player2Map.put(KeyEvent.VK_S, 3);
    }


    public void resetGame() {
        player1 = null;
        player2 = null;
        players = null;
        collisionsList = null;
        playerRect = null;
        player1 = new Champion(new Rectangle(BLOCK_WIDTH * 3, BLOCK_HEIGHT * 5, BLOCK_WIDTH, BLOCK_HEIGHT), p1ColorChoice, player1Map);
        player2 = new Champion(new Rectangle(BLOCK_WIDTH * 8, BLOCK_HEIGHT * 15, BLOCK_WIDTH, BLOCK_HEIGHT), p2ColorChoice, player2Map);
        players = new ArrayList<Champion>();
        players.add(player1);
        players.add(player2);
        collisionsList = new HashSet<Rectangle>();
        playerRect = new ArrayList<Rectangle>();
        food = new ArrayList<Block>();
        random = new Random();
        foodCounter = 0;
        timeCount = 0;
        countDCount = 0;
        countDownTimer = 30;
        isGameOver = false;
    }

    public void init() {
        if (startOnce == true) {
            inGame = true;
            startOnce = false;
        } else {
            inGame = true;
            //timer.start();
        }
        resetGame();
        createFood();
    }

    public void createFood() {
        boolean canDo = false;
        while ((canDo == false)) {
            xB = random.nextInt(SCREEN_WIDTH / BLOCK_WIDTH);
            yB = random.nextInt(SCREEN_HEIGHT / BLOCK_HEIGHT);
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).checkForCanPlaceFood(xB, yB, BLOCK_WIDTH, BLOCK_HEIGHT)) {
                    canDo = true;
                    break;
                }
            }
        }
        Rectangle fR = new Rectangle(BLOCK_WIDTH * xB, BLOCK_HEIGHT * yB, BLOCK_WIDTH, BLOCK_HEIGHT);
        Block food1 = new Block(Color.MAGENTA, fR);
        food.add(food1);
    }

    public void drawGrid(Graphics g) {
        g.drawImage(backgroundScreen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        int numVerticalLines = SCREEN_WIDTH / BLOCK_WIDTH;
        int numHorizontalLines = SCREEN_HEIGHT / BLOCK_HEIGHT;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(gridStroke);
        g.setColor(Color.CYAN);
        for (int r = 0; r < numHorizontalLines; r++) {
            int sX = 0;
            int sY = BLOCK_HEIGHT * r;
            g2d.drawLine(sX, sY, SCREEN_WIDTH, sY);
        }
        for (int c = 0; c < numVerticalLines; c++) {
            int sX = BLOCK_WIDTH * c;
            int sY = 0;
            g2d.drawLine(sX, sY, sX, SCREEN_HEIGHT);
        }
        for (int i = 0; i < food.size(); i++) {
            food.get(i).draw(g);
        }
        player1.draw(g);
        player2.draw(g);
        g.setFont(small);
        if (countDownTimer <= 10) {
            g.setColor(Color.RED);
        } else {
            g.setColor(colorChoices.get(4));
        }
        g.drawString("" + countDownTimer, 350, 60);
        g.setFont(scoreF);
        g.setColor(player1.spiritColor);
        g.drawString("P1: " + player1.getBody().size(), 30, 50);
        g.setColor(player2.spiritColor);
        g.drawString("P2: " + player2.getBody().size(), 630, 50);
    }

    public boolean checkForBodyCollisions() {
        //check for player to player collisions
        ArrayList<Block> p1 = player1.getBody();
        ArrayList<Block> p2 = player2.getBody();
        Block p1H = p1.get(0);
        Block p2H = p2.get(0);
        if (p1H.checkForCollisions(p2H)) {
            //System.out.println("Tie");
            inGame = false;
            return true;
        }
        for (int i = 0; i < p2.size(); i++) {
            if (p1H.checkForCollisions(p2.get(i))) {
                //System.out.println("Player 1 Loses");
                inGame = false;
                return true;
            }
        }
        for (int i = 0; i < p1.size(); i++) {
            if (p2H.checkForCollisions(p1.get(i))) {
                //System.out.println("Player 2 Loses");
                inGame = false;
                return true;
            }
        }
        return false;
    }

    public boolean checkForCollisions() {
        if (player1.getCollided()) {
            return true;
        } else if (player2.getCollided()) {
            return true;
        } else {
            if (player1.getUpdate() && player2.getUpdate()) {
                playerRect.clear();

                playerRect = player1.getPositions();
                playerRect.addAll(player2.getPositions());
                collisionsList = new HashSet<>(playerRect);
                if (collisionsList.size() != playerRect.size()) {
                    return true;
                }
            }

        }
        return false;
    }

    public void setupFont() {
        fontC = new Color(38, 100, 194);
        small = new Font("Helvetica", Font.BOLD, 70);//cache.get(names[0]);//new Font("Helvetica", Font.BOLD, 14);
        scoreF = new Font("Helvetica", Font.BOLD, 50);
        lastSF = new Font("Helvetica", Font.BOLD, 50);
        metr = this.getFontMetrics(small);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (startOfOnce) {
            splashScreen(g);
        } else if (mainScreen) {
            drawMainScreen(g);
        } else if (isChoosingColor) {
            drawColorChoosingScreen(g);
        } else if (inTutorial) {
            drawTutorialScreen(g);
        } else if (inHighScore) {
            drawHighScoreScreen(g);
        } else if (inSettings) {
            drawSettingsScreen(g);
        }
        else if(inCredits){
            drawCreditsScreen(g);
        }
        else if (inGame) {
            drawGrid(g);
        } else if (inGame == false) {
            gameOver(g);
        }
    }

    public void drawCreditsScreen(Graphics g){
        g.drawImage(credits,0,0,SCREEN_WIDTH,SCREEN_HEIGHT,this);
    }

    public void drawColorChoosingScreen(Graphics g) {

        if (isP1Choosing) {
            g.drawImage(p1Screen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        } else {
            g.drawImage(p2Screen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        }

        String ruby = "RUBY";
        String emerald = "EMERALD";
        String saphire = "SAPPHIRE";
        String pearl = "PEARL";

        g.setFont(small);
        g.setColor(colorChoices.get(3));
        g.drawRect(470,255,metr.stringWidth(pearl),60);
        g.drawString(pearl, 470, 310);

        g.setColor(colorChoices.get(0));
        g.drawRect(470,335,metr.stringWidth(ruby),60);
        g.drawString(ruby, 470, 390);

        g.setColor(colorChoices.get(1));
        g.drawString(emerald, 100, 310);
        g.drawRect(100,255,metr.stringWidth(emerald),60);

        g.setColor(colorChoices.get(2));
        g.drawRect(100,335,metr.stringWidth(saphire),60);
        g.drawString(saphire, 100, 390);

    }

    public void setupColors() {
        colorChoices = new ArrayList<Color>();
        Color ruby = new Color(255, 66, 81);
        Color emerald = new Color(5, 255, 116);
        Color saphire = new Color(25, 233, 255);
        Color pearl = new Color(255, 107, 153);
        Color azureTimer = new Color(240, 255, 255);
        colorChoices.add(ruby);
        colorChoices.add(emerald);
        colorChoices.add(saphire);
        colorChoices.add(pearl);
        colorChoices.add(azureTimer);
    }

    public void drawTutorialScreen(Graphics g) {

        g.drawImage(tutorialScreen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
    }

    public void drawSettingsScreen(Graphics g) {
        if (playMusic) {
            g.drawImage(settingsScreen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        } else {
            g.drawImage(settings2Screen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        }
    }

    public void drawHighScoreScreen(Graphics g) {

        g.drawImage(highScoreScreen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        int sX = 100;
        int sY = 215;
        int vertSpace = 70;
        g.setColor(fontC);
        g.setFont(small);
        for (int i = 0; i < highScores.size(); i++) {
            g.drawString(highScores.get(i), SCREEN_WIDTH / 2 - 100, sY + (vertSpace * i));
        }
    }

    public void drawMainScreen(Graphics g) {

        g.drawImage(mainMenuScreen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
    }

    public void drawSplashScreen(Graphics g) {

        if (splashPart1) {
            g.drawImage(ss1, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        } else {
            g.drawImage(ss2, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        }
    }

    public void gameOver(Graphics g) {

//        g.setColor(Color.black);
//        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
//
//        g.setColor(new Color(0, 32, 48));
//        g.fillRect(50, SCREEN_WIDTH / 2 - 30, SCREEN_WIDTH - 100, 50);
//        g.setColor(Color.white);
//        g.drawRect(50, SCREEN_WIDTH / 2 - 30, SCREEN_WIDTH - 100, 50);
//
//        g.setColor(Color.white);
//        g.setFont(small);
//        g.drawString("25 35", (SCREEN_WIDTH - metr.stringWidth("25 35")) / 2,
//                SCREEN_WIDTH / 2);
        g.drawImage(gameOverScreen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
        g.setFont(small);
        g.setColor(fontC);
        int p1Score = player1.getBody().size();
        int p2Score = player2.getBody().size();
        g.drawString("" + p1Score, 370, 270);
        g.drawString("" + p2Score, 370, 370);
    }

    public void console(String msg) {
        System.out.println(msg);
    }

    public void updateHighScores() {
        int p1Score = player1.getBody().size();
        int p2Score = player2.getBody().size();
        highScores.add("P1: " + p1Score);
        highScores.add("P2: " + p2Score);
        Collections.sort(highScores, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] p1 = o1.split(" ");
                String[] p2 = o2.split(" ");
                int p1S = Integer.parseInt(p1[1]);
                int p2S = Integer.parseInt(p2[1]);
                return p2S - p1S;
            }
        });
        if (highScores.size() > 6) {
            highScores.subList(6, highScores.size()).clear();
        }
    }

    public void updateGameTimer() {
        if (countDCount < countDInterval) {
            countDCount++;
        } else {
            countDownTimer--;
            countDCount = 0;
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            if (checkForBodyCollisions() == true || countDownTimer < 0) {
                inGame = false;
                if (shouldUpdateHighScores) {
                    updateHighScores();
                    shouldUpdateHighScores = false;
                }
                isGameOver = true;
            }
        }
        if (inGame) {
            //console(timer.getDelay()+"");
            //timeCount++;
            //foodCounter++;
            //console(timeCount + "");
            updateGameTimer();
            if (foodCounter < foodTimer) {
                foodCounter++;
            } else {
                foodCounter = 0;
                //if (food.size() <= players.size() * 2 - 1) {
                createFood();
                //}
            }
            if (timeCount < timer.getDelay()) {
                timeCount++;
            } else {
                timeCount = 0;
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).update(food);
                }
            }
        }
        repaint();
    }

    class KeyListener implements java.awt.event.KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (inGame) {

                for (int i = 0; i < players.size(); i++) {
                    players.get(i).keyPressed(e);
                }
            } else {
                if (inGame == false && mainScreen == false && isGameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    updateHighScores();
                    init();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (inGame) {
                test = true;
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).keyReleased(e);
                }
            }
        }

    }

    class MouseListener implements java.awt.event.MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            //console(e.getX() + " " + e.getY());
            //console("Mouse Clicked");
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //console("Mouse Pressed");
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //console("Mouse Released");
            //console(e.getX() + " " + e.getY());
            if (mainScreen == true && inGame == false) {
                if (e.getX() >= 265 && e.getX() <= 540 && e.getY() >= 335 && e.getY() <= 420) {
                    mainScreen = false;
                    isChoosingColor = true;
                    isP1Choosing = true;
                    isGameOver = false;
                } else if (e.getX() >= 35 && e.getX() <= 261 && e.getY() >= 500 && e.getY() <= 545) {
                    mainScreen = false;
                    inTutorial = true;
                } else if (e.getX() >= 560 && e.getX() <= 760 && e.getY() >= 465 && e.getY() <= 575) {
                    mainScreen = false;
                    inHighScore = true;
                } else if (e.getX() >= 365 && e.getX() <= 430 && e.getY() >= 480 && e.getY() <= 535) {
                    mainScreen = false;
                    inSettings = true;
                }
            } else if (inTutorial) {
                if (e.getX() >= 55 && e.getX() <= 150 && e.getY() >= 525 && e.getY() <= 560) {
                    inTutorial = false;
                    mainScreen = true;
                }
            } else if (inHighScore) {
                if (e.getX() >= 55 && e.getX() <= 150 && e.getY() >= 525 && e.getY() <= 560) {
                    inHighScore = false;
                    mainScreen = true;
                }
            } else if (inSettings) {
                if (e.getX() >= 55 && e.getX() <= 150 && e.getY() >= 525 && e.getY() <= 560) {
                    inSettings = false;
                    mainScreen = true;
                } else if (e.getX() >= 105 && e.getX() <= 235 && e.getY() >= 210 && e.getY() <= 340) {
                    playMusic = !playMusic;
                    if (playMusic == false) {
                        Sound.sound2.mute(true);
                    } else {
                        Sound.sound2.mute(false);
                    }
                }
                else if(e.getX() >= 275 && e.getX() <= 385 && e.getY() >= 215 && e.getY() <= 325){
                    Sound.sound2.setVolume(.1);
                }
                else if(e.getX() >= 435 && e.getX() <= 560 && e.getY() >= 215 && e.getY() <= 325){
                    Sound.sound2.setVolume(.5);
                }
                else if(e.getX() >= 600 && e.getX() <= 725 && e.getY() >= 215 && e.getY() <= 325){
                    Sound.sound2.setVolume(1);
                }
                else if(e.getX() >= 265 && e.getX() <= 530 && e.getY() >= 395 && e.getY() <= 460){
                    inSettings = false;
                    inCredits = true;
                }
//                if (e.getX() >= SCREEN_WIDTH / 2) {
//                    Sound.sound2.setVolume(1);
//                } else if (e.getX() < SCREEN_WIDTH) {
//                    Sound.sound2.setVolume(.1);
//                }
            }
            else if(inCredits){
                if (e.getX() >= 55 && e.getX() <= 150 && e.getY() >= 525 && e.getY() <= 560) {
                    inCredits = false;
                    inSettings = true;
                }
            }
            else if (isChoosingColor) {
                boolean chose = false;
                Color colorChose = null;
                if (e.getX() >= 55 && e.getX() <= 150 && e.getY() >= 525 && e.getY() <= 560) {
                    isChoosingColor = false;
                    mainScreen = true;
                    isP1Choosing = true;
                } else if (e.getX() >= 105 && e.getX() <= 450 && e.getY() >= 260 && e.getY() <= 315) {
                    colorChose = colorChoices.get(1);
                    chose = true;
                } else if (e.getX() >= 105 && e.getX() <= 415 && e.getY() >= 340 && e.getY() <= 400) {
                    colorChose = colorChoices.get(2);
                    chose = true;
                } else if (e.getX() >= 460 && e.getX() <= 670 && e.getY() >= 340 && e.getY() <= 400) {
                    colorChose = colorChoices.get(0);
                    chose = true;
                } else if (e.getX() >= 465 && e.getX() <= 705 && e.getY() >= 260 && e.getY() <= 315) {
                    colorChose = colorChoices.get(3);
                    chose = true;
                }
                if (chose) {
                    if (isP1Choosing) {
                        isP1Choosing = false;
                        p1ColorChoice = new Color(colorChose.getRGB());
                    } else {
                        p2ColorChoice = new Color(colorChose.getRGB());
                        isChoosingColor = false;
                        mainScreen = false;
                        init();
                    }
                    colorChose = null;
                }
            } else if (inGame == false && mainScreen == false) {
                if (e.getX() >= 295 && e.getX() <= 760 && e.getY() >= 485 && e.getY() <= 545) {
                    updateHighScores();
                    init();
                } else if (e.getX() >= 55 && e.getX() <= 150 && e.getY() >= 525 && e.getY() <= 560) {
                    mainScreen = true;
                    shouldUpdateHighScores = true;
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //console("Mouse Entered");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //console("Mouse Exited");
        }

    }
}
