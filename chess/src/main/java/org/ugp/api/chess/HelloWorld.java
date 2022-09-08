
package org.ugp.api.chess;

import java.util.ArrayList;
import java.util.UUID;

import org.ugp.api.chess.enginev2.ChessPiece;
import org.ugp.api.chess.enginev2.SimpleChessEngine;
import org.ugp.serialx.GenericScope;
import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

import io.javalin.Javalin;

public class HelloWorld 
{
	public static void main(String[] args) {
		var app = Javalin.create(config -> {
			config.jsonMapper(new JavalinSerialXJson());
			config.contextPath = "/api/v1/";
		    config.enableCorsForAllOrigins();
		    config.enableHttpAllowedMethodsOnRoutes();
		}).start("192.168.100.88", 8989);
		 
		var sessions = new ArrayList<>();
		app.get("/game/new", ctx -> {
			var newSession = new ChessGameSession(app);
			sessions.add(newSession);
			ctx.json(newSession);
		});
		
		app.get("/games", ctx -> {
			ctx.json(sessions);
		});
	}
	
	public static class ChessGameSession implements SelfSerializable {
		
		protected Javalin app;
		protected UUID id;
		protected SimpleChessEngine engine;
		
		@Override
		public Object[] serialize() {
			var args = engine.serialize();
			((Scope) args[0]).put("session", id.toString());
			return args;
		}
		
		public ChessGameSession(Javalin app) {
			this.app = app;
			this.id = UUID.randomUUID();
			engine = new SimpleChessEngine(8, 8, ChessPiece.BLACK);
			
//			System.out.println(engine.get(3, 1));
			
//			for (int x = 0; x < 8; x++) {
//				engine.put("p", ChessPiece.BLACK, x, 1);
//				engine.put("p", ChessPiece.WHITE, x, 6);
//			}
//			
//			
//			for (int i = 0; i < 2; i++) {
//				engine.put("r", i, 0, i * 7);
//				engine.put("n", i, 1, i * 7);
//				engine.put("b", i, 2, i * 7);
//				engine.put("q", i, 3, i * 7);
//				engine.put("k", i, 4, i * 7);
//				engine.put("b", i, 5, i * 7);
//				engine.put("n", i, 6, i * 7);
//				engine.put("r", i, 7, i * 7);
//			}
			
			engine.put("k", 0, 1, 0);
			engine.put("k", 1, 6, 1);
			engine.put("n", 0, 6, 1);
			engine.put("n", 1, 6, 6);
			engine.put("b", 0, 5, 1);
			engine.put("b", 1, 6, 5);
			engine.put("r", 0, 2, 1);
			engine.put("r", 1, 2, 5);
			engine.put("p", 0, 0, 1);
			engine.put("p", 1, 0, 6);
						
			System.err.println(engine);
			app.get("/game/" + id + "/move/{x}/{y}/{newX}/{newY}", ctx -> {
				int x = Integer.parseInt(ctx.pathParam("x")), y = Integer.parseInt(ctx.pathParam("y"));
				engine.move(x, y, Integer.parseInt(ctx.pathParam("newX")), Integer.parseInt(ctx.pathParam("newY")));
				ctx.result(engine.toString());
//				System.err.println(engine);
			});
			
			app.get("/game/" + id + "/movmentMetrix/{x}/{y}", ctx -> {
				ctx.json(engine.getMovmentMetrix(Integer.parseInt(ctx.pathParam("x")), Integer.parseInt(ctx.pathParam("y"))));
			});
			
			app.get("/game/" + id, ctx -> {
				System.out.println(engine);
				ctx.json(this);
			});
			
		}

		public Javalin getApp() {
			return app;
		}
		
		public UUID getId() {
			return id;
		}
		
		public SimpleChessEngine getGame() {
			return engine;
		}
	}
}
