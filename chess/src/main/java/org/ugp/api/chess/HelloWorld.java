
package org.ugp.api.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ugp.chess.engine.ChessPiece;
import org.ugp.chess.engine.SimpleChessEngine;
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
		
		List<ChessGameSession> sessions = new ArrayList<>();
		app.get("/game/join", ctx -> {
			boolean isSingleplayer = Boolean.parseBoolean(ctx.queryParam("singleplayer"));
			
			for (ChessGameSession session : sessions) {
				if (session.isSingleplayer == isSingleplayer && session.getPlayers().size() <= 1 && session.getTotalTurns() <= 1)
				{
					ctx.json(session.getSessionId());
					return;
				}
			}

			var newSession = new ChessGameSession(ChessPiece.WHITE, isSingleplayer);
			sessions.add(newSession.begin(app));
			ctx.json(newSession.getSessionId());
		});

//		app.get("/games", ctx -> {
//			ctx.json(sessions);
//		});
	}
}
