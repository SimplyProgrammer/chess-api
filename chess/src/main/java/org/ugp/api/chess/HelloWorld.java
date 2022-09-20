
package org.ugp.api.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ugp.api.chess.HelloWorld.ChessGameSession;
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
		}).start("192.168.100.174", 8989);

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
		
		var<ChessGameSession> sessions = new ArrayList<>();
		app.get("/game/new", ctx -> {
			var newSession = new ChessGameSession();
			sessions.add(newSession.begin(app));
			ctx.json(newSession.getSessionId());
		});

		app.get("/games", ctx -> {
			ctx.json(sessions);
		});
	}
	
	public static class ChessGameSession implements SelfSerializable {
		
		protected Javalin app;
		protected String sessionID;
		protected List<WsContext> players = new ArrayList<>();
		protected SimpleChessEngine engine;
		
		@Override
		public Object[] serialize() {
			var args = engine.serialize();
			((Scope) args[0]).put("session", getSessionId());
			return args;
		}
		
//		public ChessGameSession(Scope fromScope) {
//			id = UUID.fromString(fromScope.get("session"));
//		}

		public ChessGameSession() {
			engine = new SimpleChessEngine(8, 8, ChessPiece.WHITE);
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
	            	 if (players.add(ctx)) {
		            	System.out.println("Conected " + ctx.getSessionId() + " " + ctx.host());
		            	ctx.send(new WsScope("init", this));
	            	 }
	            });
	            ws.onMessage(ctx -> {
	            	WsScope req = ctx.messageAsClass(WsScope.class);
	            	
	            	String type = req.getType();
	            	if ("move".equals(type)) {
	            		int x = req.getInt("x", -1), y = req.getInt("y", -1);
						int newX = req.getInt("newX", -1), newY = req.getInt("newY", -1);
						if (!engine.moveIfCan(x, y, newX, newY))
						{
							ctx.send("invalid");
							return;
						}

						Scope moveInfo = new Scope();
						ChessPiece king = engine.getKing(engine.getOnTurn());
						boolean isCheck = engine.isThreatened(king.getX(), king.getY(), false), canMove = false;

						moveInfo.put("isCheck", isCheck);
						xloop: for (int xp = 0; xp < engine.getWidth(); xp++) {
							for (int yp = 0; yp < engine.getHeight(); yp++) {
								if (engine.isOnTurn(xp, yp) && engine.hasAnyMove(xp, yp, true))
								{
									canMove = true;
									break xloop;
								}	
							}
						}
						moveInfo.put("canMove", canMove);
						moveInfo.put("isStalemate", !canMove && !isCheck);

						ctx.send(new WsScope("move", moveInfo));
	            	}
	            	else if ("movmentMetrix".equals(type)) {
	            		int x = req.getInt("x", -1), y = req.getInt("y", -1);

//						double t0 = System.nanoTime(), t;
//						engine.getMovmentMetrix(x, y, true);
//						 t = System.nanoTime();
//						 System.out.println((t-t0)/1000000);
	            		ctx.send(new WsScope("movmentMetrix", engine.getMovmentMetrix(x, y, true)));
	            	}
	            });
	            ws.onClose(ctx -> {
	            	if (players.remove(ctx)) {
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
