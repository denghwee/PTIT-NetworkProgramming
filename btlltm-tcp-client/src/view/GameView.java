/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.concurrent.Callable;
import javax.swing.JOptionPane;
import run.ClientRun;
import helper.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.*;

/**
 *
 * @author admin
 */
public class GameView extends javax.swing.JFrame {
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 600;
    private static final int TARGET_RADIUS = 200;
    private static final int NUM_RED_TARGETS = 25;
    private static final int NUM_BLUE_TARGETS = 25;
    private static final int CENTER_X = FRAME_WIDTH / 2;
    private static final int CENTER_Y = FRAME_HEIGHT / 2;

    String competitor = "";
    CountDownTimer matchTimer;
    CountDownTimer waitingClientTimer;
    
    private int redScore = 0;
    private int blueScore = 0;
    private JLabel redScoreLabel;
    private JLabel blueScoreLabel;
    private ArrayList<JLabel> targets;
    private Random random;
    private ImageIcon redTargetIcon;
    private ImageIcon blueTargetIcon;
    private JLabel backgroundLabel;
    private ImageIcon backgroundImageIcon;
    
    boolean answer = false;
    /**
     * Creates new form GameView
     */
    public GameView() {
        initComponents();
        
        
        panel.setVisible(false);
        panelPlayAgain.setVisible(false);
        pbgTimer.setVisible(false);
        
        // close window event
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(GameView.this, "Are you sure want to leave game? You will lose?", "LEAVE GAME", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
                    ClientRun.socketHandler.leaveGame(competitor);
                    ClientRun.socketHandler.setRoomIdPresent(null);
                    dispose();
                } 
            }
        });
        
    }
    
    private void loadIcons() {
        redTargetIcon = new ImageIcon(getClass().getResource("rice.png"));
        blueTargetIcon = new ImageIcon(getClass().getResource("wheat.jpg"));

        // Check if icons loaded successfully; otherwise, print warning
        if (redTargetIcon.getIconWidth() == -1 || blueTargetIcon.getIconWidth() == -1) {
            System.err.println("Warning: Failed to load target images. Make sure 'red_target.png' and 'blue_target.png' are in the correct directory.");
        }
    }
    
    private void loadBackground() {
        setLayout(new BorderLayout());
        JLabel background=new JLabel(new ImageIcon("D:\\PTIT-NetworkProgramming-master\\btlltm-tcp-client\\src\\view\\background_GameView.jpeg"));
        add(background);
        background.setLayout(new FlowLayout());
    }
    
    private void setupFrame() {
        JPanel panel = new GamePanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel);
    }
    
    
    private void setupScoreLabels() {
        redScoreLabel = new JLabel("Số hạt gạo: 0");
        redScoreLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        redScoreLabel.setBounds(20, 20, 150, 30);

        blueScoreLabel = new JLabel("Số hạt thóc: 0");
        blueScoreLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        blueScoreLabel.setBounds(20, 50, 150, 30);

        add(redScoreLabel);
        add(blueScoreLabel);
    }
    
    

    private void spawnTargets(int numTargets, boolean isRed) {
        ImageIcon icon = isRed ? redTargetIcon : blueTargetIcon;
        MouseAdapter mouseAdapter = createMouseAdapter(isRed);

        for (int i = 0; i < numTargets; i++) {
            JLabel target = new JLabel(icon);
            target.setSize(icon.getIconWidth(), icon.getIconHeight());
            spawnTarget(target);
            target.addMouseListener(mouseAdapter);
            targets.add(target);
            ((JPanel) getContentPane()).add(target);
        }
    }
    
    private MouseAdapter createMouseAdapter(boolean isRed) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isRed && SwingUtilities.isLeftMouseButton(e)) {
                    redScore++;
                    redScoreLabel.setText("Số hạt gạo: " + redScore);
                    removeTarget((JLabel) e.getSource());
                } else if (!isRed && SwingUtilities.isRightMouseButton(e)) {
                    blueScore++;
                    blueScoreLabel.setText("Số hạt thóc: " + blueScore);
                    removeTarget((JLabel) e.getSource());
                }
            }
        };
    }
    
    private void removeTarget(JLabel target) {
    // Xóa target khỏi panel
        ((JPanel) getContentPane()).remove(target);

    // Xóa target khỏi danh sách
        targets.remove(target);

    // Yêu cầu cập nhật lại giao diện
        ((JPanel) getContentPane()).revalidate();
        ((JPanel) getContentPane()).repaint();
    }

    
    private boolean isOverlapping(int x1, int y1, int width1, int height1,
                              int x2, int y2, int width2, int height2) {
    return x1 < x2 + width2 && x1 + width1 > x2 &&
           y1 < y2 + height2 && y1 + height1 > y2;
}

    
    private void spawnTarget(JLabel target) {
    boolean overlap;
    int x, y;

    do {
        overlap = false;
        // Sinh vị trí ngẫu nhiên
        double angle = 2 * Math.PI * random.nextDouble();
        double distance = TARGET_RADIUS * random.nextDouble();
        x = (int) (CENTER_X + distance * Math.cos(angle)) - target.getWidth() / 2;
        y = (int) (CENTER_Y + distance * Math.sin(angle)) - target.getHeight() / 2;

        // Kiểm tra va chạm với các mục tiêu khác
        for (JLabel existingTarget : targets) {
            if (isOverlapping(x, y, target.getWidth(), target.getHeight(),
                              existingTarget.getX(), existingTarget.getY(),
                              existingTarget.getWidth(), existingTarget.getHeight())) {
                overlap = true;
                break;
            }
        }
    } while (overlap);

    // Gán vị trí hợp lệ cho mục tiêu
    target.setLocation(x, y);
}

    
    public void setWaitingRoom () {
        panel.setVisible(false);
        pbgTimer.setVisible(false);
        btnStart.setVisible(false);
        lbWaiting.setText("waiting competitor...");
        waitingReplyClient();
    }
    
    public void showAskPlayAgain (String msg) {
        panelPlayAgain.setVisible(true);
        lbResult.setText(msg);
    }
    
    public void hideAskPlayAgain () {
        panelPlayAgain.setVisible(false);
    }
    
    public void setInfoPlayer (String username) {
        competitor = username;
        infoPLayer.setText("Play game with: " + username);
    }
    
    public void setStateHostRoom () {
        answer = false;
        btnStart.setVisible(true);
        lbWaiting.setVisible(false);
    }
    
    public void setStateUserInvited () {
        answer = false;
        btnStart.setVisible(false);
        lbWaiting.setVisible(true);
    }
    
    public void afterSubmit() {
        panel.setVisible(false);
        lbWaiting.setVisible(true);
        lbWaiting.setText("Waiting result from server...");
    }
    
    public void setStartGame (int matchTimeLimit) {
        answer = false;
        loadIcons();
        setupScoreLabels();
        

        // Initialize random generator and target list
        random = new Random();
        targets = new ArrayList<>();

        // Add red and blue targets
        spawnTargets(NUM_RED_TARGETS, true);
        spawnTargets(NUM_BLUE_TARGETS, false);
        
        btnStart.setVisible(false);
        lbWaiting.setVisible(false);
        panel.setVisible(true);
        pbgTimer.setVisible(true);
        
//        loadBackground();
        
        matchTimer = new CountDownTimer(matchTimeLimit);
        matchTimer.setTimerCallBack(
                // end match callback
                null,
                // tick match callback
                (Callable) () -> {
                    pbgTimer.setValue(100 * matchTimer.getCurrentTick() / matchTimer.getTimeLimit());
                    pbgTimer.setString("" + CustumDateTimeFormatter.secondsToMinutes(matchTimer.getCurrentTick()));
                    if (pbgTimer.getString().equals("00:00")) {
                        ClientRun.socketHandler.submitResult(competitor);
                    }
                    return null;
                },
                // tick interval
                1
        );
        
    }
    
    public void waitingReplyClient () {
        waitingClientTimer = new CountDownTimer(10);
        waitingClientTimer.setTimerCallBack(
                null,
                (Callable) () -> {
                    lbWaitingTimer.setText("" + CustumDateTimeFormatter.secondsToMinutes(waitingClientTimer.getCurrentTick()));
                    if (lbWaitingTimer.getText().equals("00:00") && answer == false) {
                        hideAskPlayAgain();
                    }
                    return null;
                },
                1
        );
    }
    
    public void showMessage(String msg){
        JOptionPane.showMessageDialog(this, msg);
    }
    
    public int getRedScore() {  
        return redScore;
    }
    
    public int getBlueScore() {
        return blueScore;
    }
    
    public void pauseTime () {
        // pause timer
        matchTimer.pause();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        panel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lbQuestion1 = new javax.swing.JLabel();
        answer1a = new javax.swing.JRadioButton();
        answer1b = new javax.swing.JRadioButton();
        answer1c = new javax.swing.JRadioButton();
        answer1d = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        lbQuestion2 = new javax.swing.JLabel();
        answer2a = new javax.swing.JRadioButton();
        answer2b = new javax.swing.JRadioButton();
        answer2c = new javax.swing.JRadioButton();
        answer2d = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        lbQuestion3 = new javax.swing.JLabel();
        answer3a = new javax.swing.JRadioButton();
        answer3b = new javax.swing.JRadioButton();
        answer3c = new javax.swing.JRadioButton();
        answer3d = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        lbQuestion4 = new javax.swing.JLabel();
        answer4a = new javax.swing.JRadioButton();
        answer4b = new javax.swing.JRadioButton();
        answer4c = new javax.swing.JRadioButton();
        answer4d = new javax.swing.JRadioButton();
        jFrame1 = new javax.swing.JFrame();
        jFrame2 = new javax.swing.JFrame();
        infoPLayer = new javax.swing.JLabel();
        btnLeaveGame = new javax.swing.JButton();
        pbgTimer = new javax.swing.JProgressBar();
        btnStart = new javax.swing.JButton();
        lbWaiting = new javax.swing.JLabel();
        panelPlayAgain = new javax.swing.JPanel();
        lbWaitingTimer = new javax.swing.JLabel();
        btnYes = new javax.swing.JButton();
        btnNo = new javax.swing.JButton();
        lbResult = new javax.swing.JLabel();

        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Question"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Question 1"));

        lbQuestion1.setText("1. 61 + 23 = ?");

        buttonGroup1.add(answer1a);
        answer1a.setText("jRadioButton1");

        buttonGroup1.add(answer1b);
        answer1b.setText("jRadioButton2");

        buttonGroup1.add(answer1c);
        answer1c.setText("jRadioButton3");

        buttonGroup1.add(answer1d);
        answer1d.setText("jRadioButton4");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(answer1d)
                    .addComponent(answer1c)
                    .addComponent(answer1b)
                    .addComponent(answer1a)
                    .addComponent(lbQuestion1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbQuestion1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(answer1a)
                .addGap(18, 18, 18)
                .addComponent(answer1b)
                .addGap(18, 18, 18)
                .addComponent(answer1c)
                .addGap(18, 18, 18)
                .addComponent(answer1d)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Question 2"));

        lbQuestion2.setText("1. 61 + 23 = ?");

        buttonGroup2.add(answer2a);
        answer2a.setText("jRadioButton1");

        buttonGroup2.add(answer2b);
        answer2b.setText("jRadioButton2");

        buttonGroup2.add(answer2c);
        answer2c.setText("jRadioButton3");

        buttonGroup2.add(answer2d);
        answer2d.setText("jRadioButton4");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(answer2d)
                    .addComponent(answer2c)
                    .addComponent(answer2b)
                    .addComponent(answer2a)
                    .addComponent(lbQuestion2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbQuestion2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(answer2a)
                .addGap(18, 18, 18)
                .addComponent(answer2b)
                .addGap(18, 18, 18)
                .addComponent(answer2c)
                .addGap(18, 18, 18)
                .addComponent(answer2d)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Question 3"));

        lbQuestion3.setText("1. 61 + 23 = ?");

        buttonGroup3.add(answer3a);
        answer3a.setText("jRadioButton1");

        buttonGroup3.add(answer3b);
        answer3b.setText("jRadioButton2");

        buttonGroup3.add(answer3c);
        answer3c.setText("jRadioButton3");

        buttonGroup3.add(answer3d);
        answer3d.setText("jRadioButton4");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(answer3d)
                    .addComponent(answer3c)
                    .addComponent(answer3b)
                    .addComponent(answer3a)
                    .addComponent(lbQuestion3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbQuestion3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(answer3a)
                .addGap(18, 18, 18)
                .addComponent(answer3b)
                .addGap(18, 18, 18)
                .addComponent(answer3c)
                .addGap(18, 18, 18)
                .addComponent(answer3d)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Question 4"));

        lbQuestion4.setText("1. 61 + 23 = ?");

        buttonGroup4.add(answer4a);
        answer4a.setText("jRadioButton1");

        buttonGroup4.add(answer4b);
        answer4b.setText("jRadioButton2");

        buttonGroup4.add(answer4c);
        answer4c.setText("jRadioButton3");

        buttonGroup4.add(answer4d);
        answer4d.setText("jRadioButton4");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(answer4d)
                    .addComponent(answer4c)
                    .addComponent(answer4b)
                    .addComponent(answer4a)
                    .addComponent(lbQuestion4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbQuestion4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(answer4a)
                .addGap(18, 18, 18)
                .addComponent(answer4b)
                .addGap(18, 18, 18)
                .addComponent(answer4c)
                .addGap(18, 18, 18)
                .addComponent(answer4d)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(57, 57, 57))
        );

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        infoPLayer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        infoPLayer.setText("Play game with:");

        btnLeaveGame.setBackground(new java.awt.Color(255, 51, 51));
        btnLeaveGame.setForeground(new java.awt.Color(255, 255, 255));
        btnLeaveGame.setText("Leave Game");
        btnLeaveGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveGameActionPerformed(evt);
            }
        });

        pbgTimer.setStringPainted(true);

        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        lbWaiting.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbWaiting.setText("Waiting host start game....");

        panelPlayAgain.setBorder(javax.swing.BorderFactory.createTitledBorder("Question?"));

        lbWaitingTimer.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbWaitingTimer.setForeground(new java.awt.Color(255, 204, 51));
        lbWaitingTimer.setText("00:00");

        btnYes.setText("Yes");
        btnYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnYesActionPerformed(evt);
            }
        });

        btnNo.setText("No");
        btnNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoActionPerformed(evt);
            }
        });

        lbResult.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbResult.setForeground(new java.awt.Color(255, 204, 51));
        lbResult.setText("Do you want to play again? ");

        javax.swing.GroupLayout panelPlayAgainLayout = new javax.swing.GroupLayout(panelPlayAgain);
        panelPlayAgain.setLayout(panelPlayAgainLayout);
        panelPlayAgainLayout.setHorizontalGroup(
            panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPlayAgainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbResult, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbWaitingTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(btnYes, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnNo, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        panelPlayAgainLayout.setVerticalGroup(
            panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPlayAgainLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelPlayAgainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbResult, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbWaitingTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnNo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(btnYes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addComponent(lbWaiting, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(panelPlayAgain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoPLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(337, 337, 337)
                        .addComponent(btnLeaveGame, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pbgTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 687, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(btnLeaveGame, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(391, 391, 391)
                        .addComponent(btnStart))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoPLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(416, 416, 416)
                                .addComponent(lbWaiting))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(486, 486, 486)
                                .addComponent(panelPlayAgain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(pbgTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(20, 20, 20))
        );

        pbgTimer.getAccessibleContext().setAccessibleName("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLeaveGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveGameActionPerformed
        if (JOptionPane.showConfirmDialog(GameView.this, "Are you sure want to leave game? You will lose?", "LEAVE GAME", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
            ClientRun.socketHandler.leaveGame(competitor);
            ClientRun.socketHandler.setRoomIdPresent(null);
            dispose();
        } 
    }//GEN-LAST:event_btnLeaveGameActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        ClientRun.socketHandler.startGame(competitor);
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoActionPerformed
        ClientRun.socketHandler.notAcceptPlayAgain();
        answer = true;
        hideAskPlayAgain();
    }//GEN-LAST:event_btnNoActionPerformed

    private void btnYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnYesActionPerformed
        ClientRun.socketHandler.acceptPlayAgain();
        answer = true;
        hideAskPlayAgain();
    }//GEN-LAST:event_btnYesActionPerformed

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Paint the background image using the ImageIcon
            if (backgroundImageIcon != null) {
                g.drawImage(backgroundImageIcon.getImage(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT, this);
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT); // Fallback if background image is not loaded
            }

            g.setColor(Color.BLUE);
            g.drawOval(CENTER_X - TARGET_RADIUS, CENTER_Y - TARGET_RADIUS, 2 * TARGET_RADIUS, 2 * TARGET_RADIUS);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameView().setVisible(true);
            }
        });
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton answer1a;
    private javax.swing.JRadioButton answer1b;
    private javax.swing.JRadioButton answer1c;
    private javax.swing.JRadioButton answer1d;
    private javax.swing.JRadioButton answer2a;
    private javax.swing.JRadioButton answer2b;
    private javax.swing.JRadioButton answer2c;
    private javax.swing.JRadioButton answer2d;
    private javax.swing.JRadioButton answer3a;
    private javax.swing.JRadioButton answer3b;
    private javax.swing.JRadioButton answer3c;
    private javax.swing.JRadioButton answer3d;
    private javax.swing.JRadioButton answer4a;
    private javax.swing.JRadioButton answer4b;
    private javax.swing.JRadioButton answer4c;
    private javax.swing.JRadioButton answer4d;
    private javax.swing.JButton btnLeaveGame;
    private javax.swing.JButton btnNo;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnYes;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JLabel infoPLayer;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lbQuestion1;
    private javax.swing.JLabel lbQuestion2;
    private javax.swing.JLabel lbQuestion3;
    private javax.swing.JLabel lbQuestion4;
    private javax.swing.JLabel lbResult;
    private javax.swing.JLabel lbWaiting;
    private javax.swing.JLabel lbWaitingTimer;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panelPlayAgain;
    public static javax.swing.JProgressBar pbgTimer;
    // End of variables declaration//GEN-END:variables
}
