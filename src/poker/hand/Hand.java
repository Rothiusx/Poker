/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poker.hand;

import poker.game.Deck;
import poker.game.Game;

/**
 *
 * @author rothi
 */
public class Hand extends HandMaker{
    private int[] ranks;
    private int[] orderedRanks;
    private boolean flush = true;
    private boolean straight = false;
    private int sameCards1 = 1, sameCards2 = 1;
    private int largeGroupRank = 0,smallGroupRank = 0;
    private int index = 0;
    private int topStraightValue = 0;
    
    public Hand(Deck d, Game game, int playerPosition) {
        super(d, game, playerPosition);
    }

    public void evaluateHand() {       
        setRanks();
        calculateRanks();
        evaluate();
    }
    
    private void setRanks() {
        ranks = new int[14];
        orderedRanks = new int[7];
        
        for (int i = 0; i < 14; i++)
        {
            ranks[i]=0;
        }
        for (int i = 0; i < game.getNumberOfCards(); i++)
        {
            ranks[cards[i].getRank()]++;
        }        
        for (int i = 0; i < game.getNumberOfCards() - 1; i++) {
            if (cards[i].getSuit() != cards[i + 1].getSuit())
                flush = false;
        }
        for (int i = 13; i >= 1; i--)
        {
            if (ranks[i] > sameCards1)
            {
                if (sameCards1 != 1)
                //if sameCards1 was not the default value
                {
                    sameCards2 = sameCards1;
                    smallGroupRank = largeGroupRank;
                }

                sameCards1 = ranks[i];
                largeGroupRank = i;

            }
            else if (ranks[i] > sameCards2)
            {
                sameCards2 = ranks[i];
                smallGroupRank = i;
            }
        }
    }
    
    private void calculateRanks() {
        if (ranks[1] == 1) //if ace, run this before because ace is highest card
        {
            orderedRanks[index] = 14;
            index++;
        }

        for (int i = 13; i >= 2; i--)
        {
            if (ranks[i] == 1)
            {
                orderedRanks[index] = i; //if ace
                index++;
            }
        }
        
        for (int i = 1; i <= 9; i++)
        //can't have straight with lowest value of more than 10
        {
            if (ranks[i] == 1 && ranks[i + 1] == 1 && ranks[i + 2] == 1 && 
                ranks[i + 3] == 1 && ranks[i + 4] == 1)
            {
                straight = true;
                topStraightValue = i + 4; //4 above bottom value
                break;
            }
        }

        if (ranks[10] == 1 && ranks[11] == 1 && ranks[12] == 1 && 
            ranks[13] == 1 && ranks[1] == 1) //ace high
        {
            straight = true;
            topStraightValue = 14; //higher than king
        }
        
        for (int i = 0; i <= 5; i++)
        {
            value[i] = 0;
        }   
    }
    
    private void evaluate() {
        if (sameCards1 == 1) {
            value[0]=1;
            value[1]=orderedRanks[0];
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
            value[4]=orderedRanks[3];
            value[5]=orderedRanks[4];
        }

        if (sameCards1 == 2 && sameCards2 == 1)
        {
            value[0] = 2;
            value[1] = largeGroupRank; //rank of pair
            value[2] = orderedRanks[0];
            value[3] = orderedRanks[1];
            value[4] = orderedRanks[2];
        }

        if (sameCards1 == 2 && sameCards2 == 2) //two pair
        {
            value[0] = 3;
            //rank of greater pair
            value[1] = largeGroupRank>smallGroupRank ? largeGroupRank : smallGroupRank;
            value[2] = largeGroupRank<smallGroupRank ? largeGroupRank : smallGroupRank;
            value[3] = orderedRanks[0];  //extra card
        }

        if (sameCards1 == 3 && sameCards2 != 2)
        {
            value[0] = 4;
            value[1] = largeGroupRank;
            value[2] = orderedRanks[0];
            value[3] = orderedRanks[1];
        }

        if (straight && !flush)
        {
            value[0] = 5;
            value[1] = 0;
        }

        if (flush && !straight)
        {
            value[0] = 6;
            value[1] = orderedRanks[0]; //tie determined by ranks of cards
            value[2] = orderedRanks[1];
            value[3] = orderedRanks[2];
            value[4] = orderedRanks[3];
            value[5] = orderedRanks[4];
        }

        if (sameCards1 == 3 && sameCards2 == 2)
        {
            value[0] = 7;
            value[1] = largeGroupRank;
            value[2] = smallGroupRank;
        }

        if (sameCards1 == 4)
        {
            value[0] = 8;
            value[1] = largeGroupRank;
            value[2] = orderedRanks[0];
        }

        if (straight && flush)
        {
            value[0] = 9;
            value[1] = 0;
        }
    }
}