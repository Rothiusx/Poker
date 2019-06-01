/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.gui;

import poker.card.Card;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.swing.ImageIcon;
import poker.card.CardImageLoader;
import poker.exceptions.DeckLimitException;
import poker.exceptions.InvalidBetException;
import poker.exceptions.PlayersNotLoadedException;
import poker.exceptions.SplitNameException;
import poker.game.Game;
import poker.player.Player;
import poker.profiles.ProfileSaver;

/**
 *
 * @author rothi
 */
public class Poker extends javax.swing.JFrame {

    private int displayedCards = 3;
    private boolean firstPlayerStarts = false;
    private boolean firstPlayerTurn = true;
    private boolean winnerFound = false;
    private boolean firstTurn = true;
    
    private Game game = null;
    private Player p1 = null;
    private Player p2 = null;

    public Poker() {
        
        initComponents();
        //buttonsPanel.setBounds(this.getWidth() - (buttonsPanel.getWidth() / 2), this.getHeight() - (buttonsPanel.getHeight() + 1), buttonsPanel.getWidth(), buttonsPanel.getHeight());
        defaultGUI();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); 
        if(game != null && game.gameStarted())
            paintCards(g);
    }
    
    private void paintCards(Graphics g){
        paintTable(g);
        paintFirstPlayer(g);
        paintSecondPlayer(g);
    }
    
    private void paintTable(Graphics g) {
       for (int i = 0; i < 5; i++) {
            if(i < displayedCards)
                paintCard(g, game.tableCards.get(i), "table", 2, 8, i);
            else
                paintCard(g, null, "table", 2, 8, i);
        }
    }
    
    private void paintFirstPlayer(Graphics g) {   
        for (int i = 0; i < 2; i++) {
            if(!firstPlayerTurn || winnerFound)
                paintCard(g, game.playerCards.get(i), "first", 0, 1.7, i);
            else
                paintCard(g, null, "first", 0, 1.7, i);    
        }
    }
    
    private void paintSecondPlayer(Graphics g) {
        for (int i = 0; i < 2; i++) {
            if(firstPlayerTurn || winnerFound)
                paintCard(g, game.playerCards.get(i + 2), "second", 0, 1.7, i);
            else
                paintCard(g, null, "second", 0, 1.7, i);
        }
    }
    
    private void paintCard(Graphics g, Card card, String cardType, double widthOffset, double heightOffset, int position) {
        int cardHeight = 182;
        int cardWidth = 125;
        int cardOffset = 150;
        double width = 0;
        double height = this.getHeight() / heightOffset;
        
        switch (cardType) {
            case "table":
                width = (this.getWidth() / widthOffset - ((cardOffset * 2) + (cardOffset / 2)) + + (position * cardOffset));
                break;
            case "first":
                width = (cardOffset + (position * cardOffset));
                break;
            case "second":
                width = (this.getWidth() - ((cardOffset * 2) + (position * cardOffset)));
                break;
        }
        
        CardImageLoader cil = new CardImageLoader(card);
        Image img = new ImageIcon(cil.getCardImage()).getImage();
        g.drawImage(img, (int)(width), (int)(height), cardWidth, cardHeight, null);
    }

    private void printCards(){
        System.out.println("Table Cards:");
        game.tableCards.forEach((card) -> {
            System.out.println(card);
        });
        System.out.println("\nPlayer Cards:");
        game.playerCards.forEach((card) -> {
            System.out.println(card);
        });
    }
    
    private void defaultGUI() {
        nextRoundButton.setEnabled(false);
        p1Panel.setVisible(false);
        p2Panel.setVisible(false);
    }
    
    private void setRemainingCards() {
        remainingCardsLabel.setText("Remaining cards: " + game.getTotalCards());
    }
    
    private void setWinner() {
        winnerFound = true;
        winnerLabel.setText("Winner is " + game.printWinner());
        if(game.getWinner() != null) {
            game.getWinner().setBank(p1.getCurrentBet() + p2.getCurrentBet());
            game.getWinner().addWin();
            game.getLooser().addLose();
        }
        else {
            p1.setBank(p1.getCurrentBet());
            p2.setBank(p2.getCurrentBet());
            p1.addTie();
            p2.addTie();
        }
        newGameEnable(true);
        nextRoundButton.setEnabled(true);
        saveProfiles();
        disableGUI();
    }
    
    private void disableGUI() {
            p1BetTextField.setEnabled(false);
            p1BetButton.setEnabled(false);
            p1BetLabel.setEnabled(false);
            p1CheckButton.setEnabled(false);
            p1FoldButton.setEnabled(false);
            p2BetTextField.setEnabled(false);
            p2BetButton.setEnabled(false);
            p2BetLabel.setEnabled(false);
            p2CheckButton.setEnabled(false);
            p2FoldButton.setEnabled(false);
    }
    
    private void nextCard() {
        if(displayedCards < 5) {
            game.nextCard();
            game.evaluateHand();
            displayedCards++;     
            refreshGUI();
            repaint();
        }
        else {
            refreshGUI();
            setWinner();
            repaint();
        }
    }
    
    private void newGameEnable(boolean enable) {
        newGameButton.setEnabled(enable);
        p1TextField.setEnabled(enable);
        p2TextField.setEnabled(enable);
    }
    
    private String splitName(String player, String part) {
        String tmp[] = player.split(", ");

        switch (part) {
            case "name":
                return tmp[0];
            case "bank":
                return tmp[1];  
            default:
                throw new SplitNameException();
        }
    }
    
    private void newPlayers() throws IOException {
        if(p1TextField.getText().contains(",")) {
            p1 = new Player(splitName(p1TextField.getText(), "name"), (Integer.parseInt(splitName(p1TextField.getText(), "bank"))));
        }
        else {
            p1 = new Player(p1TextField.getText() + ".txt");
        }
        if(p2TextField.getText().contains(", ")) {
            p2 = new Player(splitName(p2TextField.getText(), "name"), (Integer.parseInt(splitName(p2TextField.getText(), "bank"))));
        }
        else {
            p2 = new Player(p2TextField.getText() + ".txt");
        }
    }
    
    private void newGame() {
        newGameEnable(false);
        try {
            newPlayers();
        } catch (IOException ex) {
            System.err.println("Error with creating new game!");
        }
        if(p1 != null || p2 != null) {
            game = new Game(p1, p2);
            firstPlayerTurn = true;
            nextRound();
        }
        else {
            throw new PlayersNotLoadedException();
        }
    }
    
    private void newRound() {
        displayedCards = 3;
        winnerFound = false;
        firstTurn = true;
        newGameEnable(false);
        nextRoundButton.setEnabled(false);
        p1.resetBet();
        p2.resetBet();
        if(firstPlayerStarts) {
            firstPlayerStarts = false;
        }
        else {
            firstPlayerStarts = true;
        }
    }
    
    private void nextRound() {
        if(game.getTotalCards() > 9) {
            newRound();
            refreshGUI();           
            game.startGame();
            game.evaluateHand();
            setRemainingCards();
            repaint();
        }
        else {
            throw new DeckLimitException();
        }
    }
    
    private void saveProfiles() {
        try {
            ProfileSaver ps;
            ps = new ProfileSaver(p1);
            ps.saveProfile();
            ps = new ProfileSaver(p2);
            ps.saveProfile();
        } catch (IOException ex) {
            System.out.println("Error with saving profiles from current game!");
            ex.getMessage();
        }
    }
    
    private void newPlayer1Bet() {
        int bet = Integer.parseInt(p1BetTextField.getText());
        for(;;) {
            if(bet >= p2.getLastBet()) {
                p1.makeBet(bet);
                p2BetTextField.setText(p1.getLastBet() + "");
                if(firstPlayerStarts) {
                    firstTurn = false;
                    refreshGUI();
                    break;
                }
                else {
                    nextCard();   
                    break;
                }
            }
            else{
                throw new InvalidBetException();
            }
        }
    }

    private void newPlayer2Bet() {
        int bet = Integer.parseInt(p2BetTextField.getText());
        for(;;) {
            if(bet >= p1.getLastBet()) {
                p2.makeBet(bet);
                p1BetTextField.setText(p2.getLastBet() + "");
                if(firstPlayerStarts) {
                    nextCard();
                    break;
                }
                else {
                    firstTurn = false;
                    refreshGUI();   
                    break;
                }
            }
            else{
                throw new InvalidBetException();
            }
        }
    }
    
    private void fold() {
        refreshGUI();
        setWinner();
        repaint();
    }
    
    private void setPlayers() {
        p1Panel.setVisible(true);
        p2Panel.setVisible(true);
        firstPlayerLabel.setText(p1.getName());
        secondPlayerLabel.setText(p2.getName());
        p1BankLabel.setText("Bank: " + p1.getBank());
        p2BankLabel.setText("Bank: " + p2.getBank());
        p1CurrentBetLabel.setText("Current Bet: " + p1.getCurrentBet());
        p2CurrentBetLabel.setText("Current Bet: " + p2.getCurrentBet());
        potLabel.setText("Pot: " + (p1.getCurrentBet() + p2.getCurrentBet()));
    }
    
    private void disablePlayer() {
        if(firstPlayerTurn) {
            winnerLabel.setText("It's " + p1.getName() + "'s turn!");
            firstPlayerTurn = false;
            disableFirstPlayer();
        } 
        else {
            winnerLabel.setText("It's " + p2.getName() + "'s turn!");
            firstPlayerTurn = true;
            disableSecondPlayer();
        }       
    }
    
    private void disableFirstPlayer() {
        p1BetTextField.setEnabled(true);
        p1BetButton.setEnabled(true);
        p1BetLabel.setEnabled(true);
        p1CheckButton.setEnabled(true);
        p1FoldButton.setEnabled(true);    
        p2BetTextField.setEnabled(false);
        p2BetButton.setEnabled(false);
        p2BetLabel.setEnabled(false);
        p2CheckButton.setEnabled(false);
        p2FoldButton.setEnabled(false);
        if(firstTurn) {
            p1FoldButton.setEnabled(false);
            p1CheckButton.setEnabled(false);
        }
        else {
            p1FoldButton.setEnabled(true);
            p1CheckButton.setEnabled(true);
        }
    }
    
    private void disableSecondPlayer() {
        p1BetTextField.setEnabled(false);
        p1BetButton.setEnabled(false);
        p1BetLabel.setEnabled(false);
        p1CheckButton.setEnabled(false);
        p1FoldButton.setEnabled(false);    
        p2BetTextField.setEnabled(true);
        p2BetButton.setEnabled(true);
        p2BetLabel.setEnabled(true);
        p2CheckButton.setEnabled(true);
        p2FoldButton.setEnabled(true);
        if(firstTurn) {
            p2FoldButton.setEnabled(false);
            p2CheckButton.setEnabled(false);
        }
        else {
            p2FoldButton.setEnabled(true);
            p2CheckButton.setEnabled(true);
        }
    }
    
    private void refreshGUI() {
        setPlayers();
        disablePlayer();       
        repaint();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        winnerLabel = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        p1TextField = new javax.swing.JTextField();
        p2TextField = new javax.swing.JTextField();
        newGameButton = new javax.swing.JButton();
        remainingCardsLabel = new javax.swing.JLabel();
        nextRoundButton = new javax.swing.JButton();
        p1Panel = new javax.swing.JPanel();
        p1BetButton = new javax.swing.JButton();
        p1FoldButton = new javax.swing.JButton();
        p1BetTextField = new javax.swing.JTextField();
        p1CheckButton = new javax.swing.JButton();
        p1BankLabel = new javax.swing.JLabel();
        firstPlayerLabel = new javax.swing.JLabel();
        p1BetLabel = new javax.swing.JLabel();
        p1CurrentBetLabel = new javax.swing.JLabel();
        p2Panel = new javax.swing.JPanel();
        secondPlayerLabel = new javax.swing.JLabel();
        p2BetButton = new javax.swing.JButton();
        p2BetTextField = new javax.swing.JTextField();
        p2CheckButton = new javax.swing.JButton();
        p2BankLabel = new javax.swing.JLabel();
        p2BetLabel = new javax.swing.JLabel();
        p2CurrentBetLabel = new javax.swing.JLabel();
        p2FoldButton = new javax.swing.JButton();
        potLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        winnerLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        winnerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        buttonsPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        p1TextField.setText("Daniel");
        p1TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p1TextFieldActionPerformed(evt);
            }
        });

        p2TextField.setText("Logan");
        p2TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p2TextFieldActionPerformed(evt);
            }
        });

        newGameButton.setText("NEW GAME");
        newGameButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });

        remainingCardsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        nextRoundButton.setText("NEXT ROUND");
        nextRoundButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextRoundButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextRoundButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remainingCardsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                        .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(p1TextField)
                            .addComponent(p2TextField, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextRoundButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(124, 124, 124))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nextRoundButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(buttonsPanelLayout.createSequentialGroup()
                            .addComponent(p1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(p2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(newGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(remainingCardsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        p1BetButton.setText("Bet");
        p1BetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p1BetButtonActionPerformed(evt);
            }
        });

        p1FoldButton.setText("Fold");
        p1FoldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p1FoldButtonActionPerformed(evt);
            }
        });

        p1BetTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p1BetTextFieldActionPerformed(evt);
            }
        });

        p1CheckButton.setText("Check");
        p1CheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p1CheckButtonActionPerformed(evt);
            }
        });

        p1BankLabel.setText("Bank:");

        firstPlayerLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        firstPlayerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        firstPlayerLabel.setText("Player 1");

        p1BetLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        p1BetLabel.setText("Bet Amount:");

        p1CurrentBetLabel.setText("Current Bet: 0");

        javax.swing.GroupLayout p1PanelLayout = new javax.swing.GroupLayout(p1Panel);
        p1Panel.setLayout(p1PanelLayout);
        p1PanelLayout.setHorizontalGroup(
            p1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p1PanelLayout.createSequentialGroup()
                .addGroup(p1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p1PanelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(p1BankLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(p1CurrentBetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 23, Short.MAX_VALUE))
                    .addGroup(p1PanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(p1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(p1PanelLayout.createSequentialGroup()
                                .addComponent(p1BetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(p1BetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(p1BetButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(p1PanelLayout.createSequentialGroup()
                                .addComponent(p1CheckButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(p1FoldButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
            .addGroup(p1PanelLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(firstPlayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        p1PanelLayout.setVerticalGroup(
            p1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p1PanelLayout.createSequentialGroup()
                .addComponent(firstPlayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addGroup(p1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p1BankLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p1CurrentBetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(p1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p1BetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p1BetLabel)
                    .addComponent(p1BetButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(p1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p1CheckButton)
                    .addComponent(p1FoldButton))
                .addContainerGap())
        );

        secondPlayerLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        secondPlayerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secondPlayerLabel.setText("Player 2");

        p2BetButton.setText("Bet");
        p2BetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p2BetButtonActionPerformed(evt);
            }
        });

        p2BetTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p2BetTextFieldActionPerformed(evt);
            }
        });

        p2CheckButton.setText("Check");
        p2CheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p2CheckButtonActionPerformed(evt);
            }
        });

        p2BankLabel.setText("Bank:");

        p2BetLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        p2BetLabel.setText("Bet Amount:");

        p2CurrentBetLabel.setText("Current Bet: 0");

        p2FoldButton.setText("Fold");
        p2FoldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p2FoldButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout p2PanelLayout = new javax.swing.GroupLayout(p2Panel);
        p2Panel.setLayout(p2PanelLayout);
        p2PanelLayout.setHorizontalGroup(
            p2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2PanelLayout.createSequentialGroup()
                .addGroup(p2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(p2PanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(p2BankLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(p2CurrentBetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(p2PanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(p2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(p2PanelLayout.createSequentialGroup()
                                .addComponent(p2BetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(p2BetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(p2BetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(p2PanelLayout.createSequentialGroup()
                                .addComponent(p2CheckButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(p2FoldButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(p2PanelLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(secondPlayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        p2PanelLayout.setVerticalGroup(
            p2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(p2PanelLayout.createSequentialGroup()
                .addComponent(secondPlayerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(p2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p2BankLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p2CurrentBetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(p2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p2BetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(p2BetLabel)
                    .addComponent(p2BetButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(p2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p2CheckButton)
                    .addComponent(p2FoldButton))
                .addContainerGap())
        );

        potLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        potLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(p1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(potLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                    .addComponent(winnerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(p2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(139, 139, 139))
            .addGroup(layout.createSequentialGroup()
                .addGap(402, 402, 402)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(256, 256, 256)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(p2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(winnerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(potLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(p1Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 247, Short.MAX_VALUE)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextRoundButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextRoundButtonActionPerformed
        nextRound();
    }//GEN-LAST:event_nextRoundButtonActionPerformed

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameButtonActionPerformed
        newGame();
    }//GEN-LAST:event_newGameButtonActionPerformed

    private void p1BetTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p1BetTextFieldActionPerformed
        
    }//GEN-LAST:event_p1BetTextFieldActionPerformed

    private void p2BetTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p2BetTextFieldActionPerformed

    }//GEN-LAST:event_p2BetTextFieldActionPerformed

    private void p1CheckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p1CheckButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_p1CheckButtonActionPerformed

    private void p1FoldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p1FoldButtonActionPerformed
        fold();
    }//GEN-LAST:event_p1FoldButtonActionPerformed

    private void p2CheckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p2CheckButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_p2CheckButtonActionPerformed

    private void p2BetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p2BetButtonActionPerformed
        newPlayer2Bet();
    }//GEN-LAST:event_p2BetButtonActionPerformed

    private void p2FoldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p2FoldButtonActionPerformed
        fold();
    }//GEN-LAST:event_p2FoldButtonActionPerformed

    private void p1BetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p1BetButtonActionPerformed
        newPlayer1Bet();
    }//GEN-LAST:event_p1BetButtonActionPerformed

    private void p2TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p2TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_p2TextFieldActionPerformed

    private void p1TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_p1TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_p1TextFieldActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(Poker.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Poker.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Poker.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Poker.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Poker().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JLabel firstPlayerLabel;
    private javax.swing.JButton newGameButton;
    private javax.swing.JButton nextRoundButton;
    private javax.swing.JLabel p1BankLabel;
    private javax.swing.JButton p1BetButton;
    private javax.swing.JLabel p1BetLabel;
    private javax.swing.JTextField p1BetTextField;
    private javax.swing.JButton p1CheckButton;
    private javax.swing.JLabel p1CurrentBetLabel;
    private javax.swing.JButton p1FoldButton;
    private javax.swing.JPanel p1Panel;
    private javax.swing.JTextField p1TextField;
    private javax.swing.JLabel p2BankLabel;
    private javax.swing.JButton p2BetButton;
    private javax.swing.JLabel p2BetLabel;
    private javax.swing.JTextField p2BetTextField;
    private javax.swing.JButton p2CheckButton;
    private javax.swing.JLabel p2CurrentBetLabel;
    private javax.swing.JButton p2FoldButton;
    private javax.swing.JPanel p2Panel;
    private javax.swing.JTextField p2TextField;
    private javax.swing.JLabel potLabel;
    private javax.swing.JLabel remainingCardsLabel;
    private javax.swing.JLabel secondPlayerLabel;
    private javax.swing.JLabel winnerLabel;
    // End of variables declaration//GEN-END:variables
}