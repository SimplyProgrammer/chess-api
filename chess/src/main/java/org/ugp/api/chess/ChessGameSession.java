package org.ugp.api.chess;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.ugp.chess.engine.ChessPiece;
import org.ugp.chess.engine.SimpleChessEngine;
import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

public class ChessGameSession implements SelfSerializable {
		
	public final int whoStarts;
	protected Javalin app;
	protected String sessionID;
	protected List<WsContext> players;
	protected SimpleChessEngine engine;
	protected int totalTurns;
	protected Scope lastMove;
	protected boolean isSingleplayer;
	
	@Override
	public Object[] serialize() {
		var args = engine.serialize();
		((Scope) args[0]).put("session", getSessionId());
		((Scope) args[0]).put("lastMove", getLastMove());
		return args;
	}
	
//		public ChessGameSession(Scope fromScope) {
//			id = UUID.fromString(fromScope.get("session"));
//		}

	public ChessGameSession(int whoStarts, boolean isSingleplayer) {
		engine = new SimpleChessEngine(8, 8, this.whoStarts = whoStarts);
		sessionID = System.currentTimeMillis() + "-" + hashCode();
		this.isSingleplayer = isSingleplayer;

		for (int x = 0; x < 8; x++) {
			engine.put("p", ChessPiece.BLACK, x, 1);
			engine.put("p", ChessPiece.WHITE, x, 6);
		}
		
		for (int i = 0; i < 2; i++) {
			engine.put("r", i, 0, i * 7);
			engine.put("n", i, 1, i * 7);
			engine.put("b", i, 2, i * 7);
			engine.put("q", i, 3, i * 7);
			engine.put("k", i, 4, i * 7);
			engine.put("b", i, 5, i * 7);
			engine.put("n", i, 6, i * 7);
			engine.put("r", i, 7, i * 7);
		}
		
		new Timer().scheduleAtFixedRate(new TimerTask(){
		    @Override
		    public void run(){
		      System.out.println(123);
		    }
		}, 10000, 45 * 60 * 1000);
	}
	
	public ChessGameSession begin(Javalin on) {
		app = on;
		app.ws("/game/" + getSessionId(), ws -> {
			if (players == null)
				players = new ArrayList<>();
			
	        ws.onConnect(ctx -> {
	        	ctx.session.setIdleTimeout(8 * 60 * 1000);
	        	
				int index = addPlayer(ctx);
				if (index < 0) {
					 ctx.closeSession();
					 return;
				}
	        		
	        	System.out.println("Conected " + ctx.getSessionId() + " " + ctx.host() + " " + players);
	        	
	        	WsMessage initMessage = new WsMessage("init", this);
	        	if (isSingleplayer)
	        		initMessage.put("myColor", index <= 0 ? null : index + 2);
	        	else
	        		initMessage.put("myColor", index > 1 ? index : (index + whoStarts) % 2);
	        	ctx.send(initMessage);
	        });
	        
	        ws.onMessage(ctx -> {
	        	WsMessage req = ctx.messageAsClass(WsMessage.class);
	     
	        	String type = req.getType();
	        	if ("move".equals(type)) {
	        		int fromX = req.getInt("fromX", -1), fromY = req.getInt("fromY", -1);
					int toX = req.getInt("toX", -1), toY = req.getInt("toY", -1);
					
					if (engine.moveIfCan(fromX, fromY, toX, toY))
					{
						String promote = req.getString("promote", null);
						if (promote != null && (toY >= engine.getHeight() - 1 || toY <= 0))
						{
							ChessPiece piece = engine.get(toX, toY);
							if (!piece.getType().equals("p"))
							{
								ctx.send("promotion invalid");
								return;
							}
							engine.put(promote, piece.getColor(), toX, toY);
						}
					}
					else 
					{
						ctx.send("invalid");
						return;
					}
					engine.endTurn();
	
					ChessPiece king = engine.getKing(engine.getOnTurn());
					boolean isCheck = engine.isThreatened(king.getX(), king.getY(), false), canMove = false;
	
					if (isCheck) {
						Scope pos = new Scope();
						pos.put("x", king.getX());
						pos.put("y", king.getY());
						req.put("isCheck", pos);
					}
					xloop: for (int xp = 0; xp < engine.getWidth(); xp++) {
						for (int yp = 0; yp < engine.getHeight(); yp++) {
							if (engine.isOnTurn(xp, yp) && engine.hasAnyMove(xp, yp, true)) {
								canMove = true;
								break xloop;
							}	
						}
					}
					req.put("canMove", canMove);
					req.put("isStalemate", !canMove && !isCheck);
	//						req.put("onTurn", engine.getOnTurn());
					
					lastMove = req;
					
					for (WsContext pl : players) {
						if (pl != null)
							pl.send(new WsMessage("move", req));
					}
					totalTurns++;
	        	}
	        	else if ("movmentMetrix".equals(type)) {
	        		
	        		int x = req.getInt("x", -1), y = req.getInt("y", -1);
	//						double t0 = System.nanoTime(), t;
	//						engine.getMovmentMetrix(x, y, true);
	//						t = System.nanoTime();
	//						System.out.println((t-t0)/1000000);
	        		ctx.send(new WsMessage("movmentMetrix", engine.getMovmentMetrix(x, y, true)));
	        	}
	        });
	        
	        ws.onClose(ctx -> {
	        	if (removePlayer(ctx) > -1) {
	        		System.out.println("Closed " + ctx.getSessionId() + " " + players);
	        	}
	        });
	        });
		
			return this;
		}
	
		public void reset() {
			
		}
	
		protected boolean isEmpty() {
			if (players.isEmpty())
				return true;
			
			for (WsContext object : players) {
				if (object != null)
					return false;
			}
			return true;
		}
		
		protected synchronized int addPlayer(WsContext player) {
        	for (int i = 0; i < players.size(); i++) {
				if (players.get(i) == null) {
					players.set(i, player);
					return i;
				}
			}
        	
        	int i = players.size();
        	if (players.add(player))
        		return i;
        	return -1;
		}
		
		protected synchronized int removePlayer(WsContext player) {
			if (player == null)
				return -1;
			
        	for (int i = 0; i < players.size(); i++) {
        		WsContext current = players.get(i);
				if (current != null && player.equals(current)) {
					players.set(i, null);
					return i;
				}
			}
			return -1;
		}
		
		public Scope getLastMove() {
			return lastMove;
		}
		
		public int getTotalTurns() {
			return totalTurns;
		}
 
		public List<WsContext> getPlayers() {
			return players;
		}

		public Javalin getApp() {
			return app;
		}
		
		public String getSessionId() {
			return sessionID;
		}
		
		public SimpleChessEngine getGame() {
			return engine;
		}
		
		public boolean isSingleplayer() {
			return isSingleplayer;
		}
	}