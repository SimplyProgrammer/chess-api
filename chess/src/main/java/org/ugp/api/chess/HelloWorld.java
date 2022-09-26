
package org.ugp.api.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ugp.api.chess.enginev2.ChessPiece;
import org.ugp.api.chess.enginev2.SimpleChessEngine;
import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

public class HelloWorld 
{
	public static void main(String[] args) {
		var app = Javalin.create(config -> {
			config.jsonMapper(new JavalinSerialXJson());
			config.contextPath = "/api/v1/";
		    config.enableCorsForAllOrigins();
		}).start(8989);

//		app.ws("/chat", ws -> {
//            ws.onConnect(ctx -> {
//            	System.out.println("Conected " + ctx.getSessionId() + " " + ctx.host());
//            	
//            });
//            ws.onMessage(ctx -> {
//            	WsRequest request = ctx.messageAsClass(WsRequest.class);
//            	System.out.println();
//            });
//            ws.onClose(ctx -> {
//               System.out.println("Closed " + ctx.getSessionId());
//            });
//        });
		
		List<ChessGameSession> sessions = new ArrayList<>();
		app.get("/game/join", ctx -> {
			for (ChessGameSession session : sessions) {
				if (session.getPlayers().size() <= 1 && session.getTotalTurns() <= 1)
				{
					ctx.json(session.getSessionId());
					return;
				}
			}

			var newSession = new ChessGameSession(ChessPiece.WHITE);
			sessions.add(newSession.begin(app));
			ctx.json(newSession.getSessionId());
		});

//		app.get("/games", ctx -> {
//			ctx.json(sessions);
//		});
	}
	
	public static class ChessGameSession implements SelfSerializable {
		
		public final int whoStarts;
		protected Javalin app;
		protected String sessionID;
		protected List<WsContext> players = new ArrayList<>();
		protected SimpleChessEngine engine;
		protected int totalTurns;
		
		@Override
		public Object[] serialize() {
			var args = engine.serialize();
			((Scope) args[0]).put("session", getSessionId());
			return args;
		}
		
//		public ChessGameSession(Scope fromScope) {
//			id = UUID.fromString(fromScope.get("session"));
//		}

		public ChessGameSession(int whoStarts) {
			engine = new SimpleChessEngine(8, 8, this.whoStarts = whoStarts);
			sessionID = UUID.randomUUID() + "-" + hashCode();

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
//			engine.isThreatened(0, 0, true);
//			double t0 = System.nanoTime(), t;
//			engine.isThreatened(0, 0, true);
//			t = System.nanoTime();
//			System.out.println((t-t0)/1000000);
		}
		
		public ChessGameSession begin(Javalin on) {
			app = on;
			app.ws("/game/" + getSessionId(), ws -> {
	            ws.onConnect(ctx -> {
					int index = addPlayer(ctx);
					if (index < 0) {
						 ctx.closeSession();
						 return;
					}
	            		
		        	System.out.println("Conected " + ctx.getSessionId() + " " + ctx.host());
		        	
		        	WsMessage initMessage = new WsMessage("init", this);
		        	initMessage.put("myColor", index > 1 ? index : (index + whoStarts) % 2);
		        	ctx.send(initMessage);
	            });
	            
	            ws.onMessage(ctx -> {
	            	WsMessage req = ctx.messageAsClass(WsMessage.class);
	            	
	            	String type = req.getType();
	            	if ("move".equals(type)) {
	            		int fromX = req.getInt("fromX", -1), fromY = req.getInt("fromY", -1);
						int toX = req.getInt("toX", -1), toY = req.getInt("toY", -1);
						
						if (!engine.moveIfCan(fromX, fromY, toX, toY))
						{
							ctx.send("invalid");
							return;
						}

						ChessPiece king = engine.getKing(engine.getOnTurn());
						boolean isCheck = engine.isThreatened(king.getX(), king.getY(), false), canMove = false;

						req.put("isCheck", isCheck);
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
						
						for (WsContext pl : players) {
							if (pl != null && pl.session != ctx.session)
								pl.send(new WsMessage("notifyMove", req));
						}
						
						ctx.send(new WsMessage("move", req));
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
	            		System.out.println("Closed " + ctx.getSessionId());
	            	}
	            });
	        });
//			app.routes(() -> {
//				path("/game/" + getSessionId(), () -> {
//					
//					get("/move", ctx -> {
//						int x = parseInt(ctx.queryParam("x")), y = parseInt(ctx.queryParam("y"));
//						int newX = parseInt(ctx.queryParam("newX")), newY = parseInt(ctx.queryParam("newY"));
//						if (!engine.moveIfCan(x, y, newX, newY))
//						{
//							ctx.status(500);
//							return;
//						}
//
//						Scope checkInfo = new Scope();
//						ChessPiece king = engine.getKing(engine.getOnTurn());
//						boolean isCheck = engine.isThreatened(king.getX(), king.getY(), false), canMove = false;
//
//						checkInfo.put("isCheck", isCheck);
//						
//						xloop: for (int xp = 0; xp < engine.getWidth(); xp++) {
//							for (int yp = 0; yp < engine.getHeight(); yp++) {
//								if (engine.isOnTurn(xp, yp) && engine.hasAnyMove(xp, yp, true))
//								{
//									canMove = true;
//									break xloop;
//								}	
//							}
//						}
//						checkInfo.put("canMove", canMove);
//
//						checkInfo.put("isStalemate", !canMove && !isCheck);
//
//						ctx.json(checkInfo);
//					});
//					
//					get("/movmentMetrix", ctx -> {
//						int x = parseInt(ctx.queryParam("x")), y = parseInt(ctx.queryParam("y"));
//						
////						double t0 = System.nanoTime(), t;
////						engine.getMovmentMetrix(x, y, true);
////						 t = System.nanoTime();
////						 System.out.println((t-t0)/1000000);
//						ctx.json(engine.getMovmentMetrix(x, y, true));
//					});
//					
////					get("isThreatened", ctx -> {
////						int x = parseInt(ctx.queryParam("x")), y = parseInt(ctx.queryParam("y"));
////						ctx.json(engine.isThreatened(x, y, true));
////					});
////					
////					get(ctx -> {
////						if (ctx.queryParamMap().size() > 0)
////						{
////							int x = parseInt(ctx.queryParam("x")), y = parseInt(ctx.queryParam("y"));
////							ctx.json(engine.get(x, y));
////						}
////						else
////							ctx.json(this);
////					});
//				});
//			});

			return this;
		}
		
		protected int addPlayer(WsContext player) {
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
		
		protected int removePlayer(WsContext player) {
        	int index = players.indexOf(player);
        	if (index > -1) {
        		players.set(index, null);
        	}
        	return index;
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
	}
}
