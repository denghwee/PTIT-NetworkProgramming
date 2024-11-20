///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package view;
//
//import java.util.concurrent.Callable;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
//import run.ClientRun;
//import helper.*;
//import java.util.Enumeration;
//import javax.swing.AbstractButton;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.ArrayList;
//import java.util.Random;
//import static view.GameView.pbgTimer;
//
//public class CircleClickGame extends JFrame {
//    private static final int FRAME_WIDTH = 600;
//    private static final int FRAME_HEIGHT = 600;
//    private static final int TARGET_RADIUS = 200;
//    private static final int NUM_RED_TARGETS = 25;
//    private static final int NUM_BLUE_TARGETS = 25;
//    private static final int CENTER_X = FRAME_WIDTH / 2;
//    private static final int CENTER_Y = FRAME_HEIGHT / 2;
//
//    private int redScore = 0;
//    private int blueScore = 0;
//    private JLabel redScoreLabel;
//    private JLabel blueScoreLabel;
//    private ArrayList<JLabel> targets;
//    private Random random;
//    private ImageIcon redTargetIcon;
//    private ImageIcon blueTargetIcon;
//    
//    String competitor = "";
//    CountDownTimer matchTimer;
//    CountDownTimer waitingClientTimer;
//    
//    boolean answer = false;
//
//    public CircleClickGame() {
//        
//        
//        // Initialize and load images
//        loadIcons();
//
//        // Set up the JFrame
//        setupFrame();
//
//        // Initialize score labels
//        setupScoreLabels();
//
//        // Initialize random generator and target list
//        random = new Random();
//        targets = new ArrayList<>();
//
//        // Add red and blue targets
//        spawnTargets(NUM_RED_TARGETS, true);
//        spawnTargets(NUM_BLUE_TARGETS, false);
//
//        // Make the frame visible
//        setVisible(false);
//        
//        // close window event
////        this.addWindowListener(new java.awt.event.WindowAdapter() {
////            @Override
////            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
////                if (JOptionPane.showConfirmDialog(GameView.this, "Are you sure want to leave game? You will lose?", "LEAVE GAME", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
////                    ClientRun.socketHandler.leaveGame(competitor);
////                    ClientRun.socketHandler.setRoomIdPresent(null);
////                    dispose();
////                } 
////            }
////        });
//    }
//
//    private void loadIcons() {
//        redTargetIcon = new ImageIcon(getClass().getResource("red.png"));
//        blueTargetIcon = new ImageIcon(getClass().getResource("blue.png"));
//
//        // Check if icons loaded successfully; otherwise, print warning
//        if (redTargetIcon.getIconWidth() == -1 || blueTargetIcon.getIconWidth() == -1) {
//            System.err.println("Warning: Failed to load target images. Make sure 'red_target.png' and 'blue_target.png' are in the correct directory.");
//        }
//    }
//
//    private void setupFrame() {
//        setTitle("Circle Clicking Game");
//        setSize(FRAME_WIDTH, FRAME_HEIGHT);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//        
//        JPanel panel = new GamePanel();
//        panel.setLayout(null);
//        panel.setBackground(Color.WHITE);
//        add(panel);
//        
//        panel.setVisible(false);
//    }
//
//    private void setupScoreLabels() {
//        redScoreLabel = new JLabel("Red Score: 0");
//        redScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        redScoreLabel.setBounds(20, 20, 150, 30);
//
//        blueScoreLabel = new JLabel("Blue Score: 0");
//        blueScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        blueScoreLabel.setBounds(20, 50, 150, 30);
//
//        add(redScoreLabel);
//        add(blueScoreLabel);
//    }
//
//    private void spawnTargets(int numTargets, boolean isRed) {
//        ImageIcon icon = isRed ? redTargetIcon : blueTargetIcon;
//        MouseAdapter mouseAdapter = createMouseAdapter(isRed);
//
//        for (int i = 0; i < numTargets; i++) {
//            JLabel target = new JLabel(icon);
//            target.setSize(icon.getIconWidth(), icon.getIconHeight());
//            spawnTarget(target);
//            target.addMouseListener(mouseAdapter);
//            targets.add(target);
//            ((JPanel) getContentPane()).add(target);
//        }
//    }
//
//    private MouseAdapter createMouseAdapter(boolean isRed) {
//        return new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (isRed && SwingUtilities.isLeftMouseButton(e)) {
//                    redScore++;
//                    redScoreLabel.setText("Red Score: " + redScore);
//                    removeTarget((JLabel) e.getSource());
//                } else if (!isRed && SwingUtilities.isRightMouseButton(e)) {
//                    blueScore++;
//                    blueScoreLabel.setText("Blue Score: " + blueScore);
//                    removeTarget((JLabel) e.getSource());
//                }
//            }
//        };
//    }
//
//    private void removeTarget(JLabel target) {
//        ((JPanel) getContentPane()).remove(target);
//        targets.remove(target);
////        repaint(target.getBounds()); // Only repaint the area where the target was
//    }
//
//    private void spawnTarget(JLabel target) {
//        double angle = 2 * Math.PI * random.nextDouble();
//        double distance = TARGET_RADIUS * random.nextDouble();
//        int x = (int) (CENTER_X + distance * Math.cos(angle)) - target.getWidth() / 2;
//        int y = (int) (CENTER_Y + distance * Math.sin(angle)) - target.getHeight() / 2;
//        target.setLocation(x, y);
//    }
//
//    private class GamePanel extends JPanel {
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            g.setColor(Color.BLUE);
//            g.drawOval(CENTER_X - TARGET_RADIUS, CENTER_Y - TARGET_RADIUS, 2 * TARGET_RADIUS, 2 * TARGET_RADIUS);
//        }
//    }
//    
//    public void setStateHostRoom () {
//        answer = false;
//        btnStart.setVisible(true);
//        lbWaiting.setVisible(false);
//    }
//    
//    public void setStateUserInvited () {
//        answer = false;
//        btnStart.setVisible(false);
//        lbWaiting.setVisible(true);
//    }
//    
//    public void setStartGame (int matchTimeLimit) {
//        answer = false;
//        
//        btnStart.setVisible(false);
//        lbWaiting.setVisible(false);
//        panel.setVisible(true);
//        pbgTimer.setVisible(true);
//        
//        matchTimer = new CountDownTimer(matchTimeLimit);
//        matchTimer.setTimerCallBack(
//                // end match callback
//                null,
//                // tick match callback
//                (Callable) () -> {
//                    pbgTimer.setValue(100 * matchTimer.getCurrentTick() / matchTimer.getTimeLimit());
//                    pbgTimer.setString("" + CustumDateTimeFormatter.secondsToMinutes(matchTimer.getCurrentTick()));
//                    if (pbgTimer.getString().equals("00:00")) {
////                        afterSubmit();
//                    }
//                    return null;
//                },
//                // tick interval
//                1
//        );
//    }
//    
////    public void waitingReplyClient () {
////        waitingClientTimer = new CountDownTimer(10);
////        waitingClientTimer.setTimerCallBack(
////                null,
////                (Callable) () -> {
////                    lbWaitingTimer.setText("" + CustumDateTimeFormatter.secondsToMinutes(waitingClientTimer.getCurrentTick()));
////                    if (lbWaitingTimer.getText().equals("00:00") && answer == false) {
////                        hideAskPlayAgain();
////                    }
////                    return null;
////                },
////                1
////        );
////    }
//    
//    public void showMessage(String msg){
//        JOptionPane.showMessageDialog(this, msg);
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(CircleClickGame::new);
//    }
//    
//    private javax.swing.JButton btnLeaveGame;
//    private javax.swing.JButton btnNo;
//    private javax.swing.JButton btnStart;
//    private javax.swing.JButton btnYes;
//    private javax.swing.JLabel infoPLayer;
//    private javax.swing.JLabel lbResult;
//    private javax.swing.JLabel lbWaiting;
//    private javax.swing.JLabel lbWaitingTimer;
//    private javax.swing.JPanel panel;
//    private javax.swing.JPanel panelPlayAgain;
//    public static javax.swing.JProgressBar pbgTimer;
//}
