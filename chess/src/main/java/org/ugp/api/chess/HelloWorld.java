
package org.ugp.api.chess;

import java.util.UUID;

import org.ugp.api.chess.enginev2.ChessPiece;
import org.ugp.api.chess.enginev2.SimpleChessEngine;

import io.javalin.Javalin;

public class HelloWorld 
{
	public static void main(String[] args) {
		var app = Javalin.create(config -> {
			config.jsonMapper(new JavalinSerialXJson());
			config.contextPath = "/api/v1/";
		    config.enableCorsForAllOrigins();
		}).start("192.168.100.88", 8989);
		 
		app.get("/game/new", ctx -> {;
			ctx.result(new ChessGameSession(app).getId().toString());
		});
	}
	
	public static class ChessGameSession {
		
		protected Javalin app;
		protected UUID id;
		protected SimpleChessEngine engine;
		
		public ChessGameSession(Javalin app) {
			this.app = app;
			this.id = UUID.randomUUID();
			engine = new SimpleChessEngine(8, 8);

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

			app.get("/game/" + id, ctx -> {
				ctx.json(engine);
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