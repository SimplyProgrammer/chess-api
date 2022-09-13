
package org.ugp.api.chess;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.UUID;

import org.ugp.api.chess.HelloWorld.ChessGameSession;
import org.ugp.api.chess.enginev2.ChessPiece;
import org.ugp.api.chess.enginev2.SimpleChessEngine;
import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;

public class HelloWorld 
{
	public static void main(String[] args) {
		var app = Javalin.create(config -> {
			config.jsonMapper(new JavalinSerialXJson());
			config.contextPath = "/api/v1/";
		    config.enableCorsForAllOrigins();
		}).start("192.168.100.88", 8989);
		 
		var<ChessGameSession> sessions = new ArrayList<>();
		app.get("/game/new", ctx -> {
			var newSession = new ChessGameSession();
			sessions.add(newSession.begin(app));
			ctx.json(newSession);
		});
		
		app.get("/games", ctx -> {
			ctx.json(sessions);
		});
	}
	
	public static class ChessGameSession implements SelfSerializable {
		
		protected Javalin app;
		protected String sessionID;
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
			sessionID = UUID.randomUUID() + "-" + hashCode();
			engine = new SimpleChessEngine(8, 8, ChessPiece.WHITE);
			
//			System.out.println(engine.get(3, 1));
			
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
			
//			engine.put("k", 1, 1, 5);
//			engine.put("n", 0, 6, 1);
//			engine.put("n", 1, 6, 6);
//			engine.put("b", 0, 5, 2);
//			engine.put("b", 1, 6, 5);
//			engine.put("r", 0, 2, 1);
//			engine.put("r", 1, 2, 5);
//			engine.put("p", 0, 0, 1);
//			engine.put("p", 1, 0, 6);
//			engine.put("p", 1, 1, 6);
//			engine.put("q", 1, 4, 0);
		}
		
		public ChessGameSession begin(Javalin on) {
			(app = on).routes(() -> {
				path("/game/" + getSessionId(), () -> {
					get("/move", ctx -> {
						int x = parseInt(ctx.queryParam("x")), y = parseInt(ctx.queryParam("y"));
						int newX = parseInt(ctx.queryParam("newX")), newY = parseInt(ctx.queryParam("newY"));
						if (!engine.moveIfCan(x, y, newX, newY))
						{
							ctx.status(500);
							return;
						}
						
						Scope checkInfo = new Scope();
						ChessPiece king = engine.getKing(engine.getOnTurn());
						boolean isCheck = engine.isThreatened(king.getX(), king.getY(), false), isStalemate = !isCheck;

						checkInfo.put("isCheck", isCheck);
						checkInfo.put("canKingMove", isCheck && engine.hasAnyMove(king.getX(), king.getY(), true));

						if (isStalemate) {
							xloop: for (int xp = 0; xp < engine.getWidth(); xp++) {
								for (int yp = 0; yp < engine.getHeight(); yp++) {
									if (engine.isOnTurn(xp, yp) && !engine.hasAnyMove(xp, yp, true))
									{
										isStalemate = false;
										break xloop;
									}	
								}
							}
						}
						checkInfo.put("isStalemate", isStalemate);

						ctx.json(checkInfo);
					});
					
					get("/movmentMetrix", ctx -> {
						int x = parseInt(ctx.queryParam("x")), y = parseInt(ctx.queryParam("y"));
						
//						double t0 = System.nanoTime(), t;
//						engine.getMovmentMetrix(x, y, true);
//						 t = System.nanoTime();
//						 System.out.println((t-t0)/1000000);
						ctx.json(engine.getMovmentMetrix(x, y, true));
					});
					
					get("isThreatened", ctx -> {
						int x = parseInt(ctx.queryParam("x")), y = parseInt(ctx.queryParam("y"));
						ctx.json(engine.isThreatened(x, y, true));
					});
					
					get(ctx -> {
						if (ctx.queryParamMap().size() > 0)
						{
							int x = parseInt(ctx.queryParam("x")), y = parseInt(ctx.queryParam("y"));
							ctx.json(engine.get(x, y));
						}
						else
							ctx.json(this);
					});
				});
			});
			
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
