package edu.upc.epsevg.prop.checkers.players.SoloSoledoso;

import edu.upc.epsevg.prop.checkers.CellType;
import edu.upc.epsevg.prop.checkers.GameStatus;
import edu.upc.epsevg.prop.checkers.IAuto;
import edu.upc.epsevg.prop.checkers.IPlayer;
import edu.upc.epsevg.prop.checkers.MoveNode;
import edu.upc.epsevg.prop.checkers.PlayerMove;
import edu.upc.epsevg.prop.checkers.PlayerType;
import edu.upc.epsevg.prop.checkers.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Jugador MiniMax
 * @author erich
 */
public class PlayerMiniMax implements IPlayer, IAuto {

    private PlayerType _me;
    int _nivell;
    long _nodes;

    public PlayerMiniMax(int nivell) {
        _nivell = nivell;
    }

    @Override
    public void timeout() {
        
    }

    /**
     * Decideix el pròxim moviment que hauria de fer el jugador usant l'algoritme
     * MiniMax amb poda alpha-beta i ehrística que conta les fitxes i dames de cada
     * jugador.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(GameStatus s) {
        
        _nodes = 0;
        _me = s.getCurrentPlayer();
        List<MoveNode> moves =  s.getMoves();
        //List<Point> l = new ArrayList<>();
        List<List<Point>> L = new ArrayList<>();

        for(MoveNode node : moves) allMoves(node, L, new ArrayList<>());
        List<Point> ret = L.get(0);
        int comp = Integer.MIN_VALUE;
        for(List<Point> Move : L){
           GameStatus _s = new GameStatus(s);
           _s.movePiece(Move);
           int aux = calcMin(_s, _nivell-1, Integer.MAX_VALUE, Integer.MIN_VALUE);
           if (aux > comp) {
               comp = aux;
               ret = Move;
           }
        }
        //return new PlayerMove(ret, 1L, 0, SearchType.MINIMAX);
        return new PlayerMove( ret, _nodes, _nivell, SearchType.RANDOM);
    }
    
    void allMoves(MoveNode node, List<List<Point>> L, List<Point> l){
        if(node.getChildren().isEmpty()) {
            l.add(node.getPoint());
            L.add(l);
        }
        else {
            l.add(node.getPoint());
            allMoves(node.getChildren().get(0), L, l);
            
            if (node.getChildren().size() == 1) return;
            
            for (int i = 1; i < node.getChildren().size(); i++) {
                List<Point> lAux = new ArrayList<>();
                for (int j = 0; j < l.size(); j++) {
                    lAux.add(l.get(j));
                }
                allMoves(node.getChildren().get(i), L, lAux);
            }
        }
    }
    
    int calcMin(GameStatus s, int nivell, int alpha, int beta){
        _nodes ++;
        if(s.isGameOver()) {
            if (s.GetWinner() == _me) return Integer.MAX_VALUE;
            else return Integer.MIN_VALUE;
        }
        
        if(nivell == 0) return calcHeur(s);
        
        List<MoveNode> moves =  s.getMoves();
        List<Point> l = new ArrayList<>();
        List<List<Point>> L = new ArrayList<>();
        
        for(MoveNode node : moves) allMoves(node, L, l);
        for(List<Point> Move : L){
           GameStatus _s = new GameStatus(s);
           _s.movePiece(Move);
           int aux = calcMax(_s, nivell -1, alpha, beta);
           if (aux < beta) {
               beta = aux;
           }
           if(beta <= alpha) break;
        }
        return beta;
    }
    
    int calcMax(GameStatus s, int nivell, int alpha, int beta) {
        _nodes ++;
        if(s.isGameOver()) {
            if (s.GetWinner() == _me) return Integer.MAX_VALUE;
            else return Integer.MIN_VALUE;
        }
        
        if(nivell == 0) return calcHeur(s);
        
        List<MoveNode> moves =  s.getMoves();
        List<Point> l = new ArrayList<>();
        List<List<Point>> L = new ArrayList<>();
        
        for(MoveNode node : moves) allMoves(node, L, l);
        for(List<Point> Move : L){
           GameStatus _s = new GameStatus(s);
           _s.movePiece(Move);
           int aux = calcMin(_s, nivell -1, alpha, beta);
           if (aux > alpha) {
               alpha = aux;
           }
           if (alpha >= beta) break;
        }
        return alpha;
    }
    
    int calcHeur(GameStatus s) {
        if(s.isGameOver()) {
            if (s.GetWinner() == _me) return Integer.MAX_VALUE;
            else return Integer.MIN_VALUE;
        }
        int size = s.getSize();
        int ret = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                CellType a = s.getPos(i, j);
                if (a.getPlayer() == _me) {
                    ret ++;
                    if (a.isQueen()) ret = ret --;
                }
                if (a.getPlayer() == PlayerType.opposite(_me)) {
                    ret --;
                    if (a.isQueen()) ret --;
                }
            }
        }
        return ret;
    }
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "MiniMax";
    }

}