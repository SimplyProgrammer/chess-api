package org.ugp.api.chess;

import java.io.IOException;
import java.io.InputStream;

import org.ugp.serialx.JsonSerializer;

import io.javalin.plugin.json.JsonMapper;

public class JavalinSerialXJson implements JsonMapper {

	@Override
	public String toJsonString(Object obj) {
		try {
			JsonSerializer srl = new JsonSerializer(obj);
			return srl.Stringify();
		} catch (IOException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	@Override
	public InputStream toJsonStream(Object obj) {
		return JsonMapper.super.toJsonStream(obj);
//		try {
//			return new JsonSerializer(obj).SerializeTo(PipedStreamUtil.);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return e.toString();
//		}
	}

	@Override
	public <T> T fromJsonString(String json, Class<T> targetClass) {
		try {
			return new JsonSerializer().LoadFrom(json).toObject(targetClass);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public <T> T fromJsonStream(InputStream json, Class<T> targetClass) {
		try {
			return new JsonSerializer().LoadFrom(json).toObject(targetClass);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
