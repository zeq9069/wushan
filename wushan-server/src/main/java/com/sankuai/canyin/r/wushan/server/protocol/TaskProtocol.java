package com.sankuai.canyin.r.wushan.server.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Charsets;
import com.sankuai.canyin.r.wushan.server.message.PacketHeader;
import com.sankuai.canyin.r.wushan.server.worker.Task;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * namenode -> datanode assign task protocol
 * 
 * @author kyrin
 *
 */
public class TaskProtocol implements WushanProtocol {

	public static final int TYPE = 3;

	public ByteBuf encode(Object msg) {

		if (msg == null || !(msg instanceof Task)) {
			return null;
		}

		Task task = (Task) msg;

		int exprLen = task.getExpression().getBytes(Charsets.UTF_8).length;

		int dbLen = 0;

		for (String db : task.getDbs()) {
			dbLen += db.getBytes(Charsets.UTF_8).length;
		}

		int paramsLen = 0;

		if (task.getParams() != null) {
			for (Map.Entry<String, Object> entry : task.getParams().entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(value);
					byte[] array = baos.toByteArray();
					paramsLen += key.getBytes(Charsets.UTF_8).length + array.length;
				} catch (IOException e) {
				}
			}
		}

		int bodyLen = 4 + (4 + exprLen) + (4 + 4 + 4 * task.getDbs().size() + dbLen)
				+ (4 + 4 + (4 + 4) * task.getParams().size() + paramsLen);

		int allLen = PacketHeader.HEADER_PROTO + bodyLen;

		ByteBuf buf = Unpooled.buffer(allLen);

		PacketHeader.writeHeader(buf, (byte) PacketType.REQUEST.getType(), TYPE);

		buf.writeInt(bodyLen);

		buf.writeInt(exprLen);

		buf.writeBytes(task.getExpression().getBytes(Charsets.UTF_8));

		buf.writeInt(task.getDbs().size());// db数量

		buf.writeInt(4 * task.getDbs().size() + dbLen);// 所有总长度

		// 写入 len+db
		for (String db : task.getDbs()) {
			byte[] dbArray = db.getBytes(Charsets.UTF_8);
			buf.writeInt(dbArray.length);
			buf.writeBytes(dbArray);
		}

		buf.writeInt(task.getParams() == null ? 0 : task.getParams().size());// 写入k-v对的数量
		buf.writeInt((4 + 4) * task.getParams().size() + paramsLen);//写入key-value对的总长度
		
		if (task.getParams() != null) {
			for (Map.Entry<String, Object> entry : task.getParams().entrySet()) {
				byte[] keyArray = entry.getKey().getBytes(Charsets.UTF_8);
				Object value = entry.getValue();
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(value);
					byte[] array = baos.toByteArray();
					
					//写key
					buf.writeInt(keyArray.length);
					buf.writeBytes(keyArray);
					
					//写value
					buf.writeInt(array.length);
					buf.writeBytes(array);
					
					oos.close();
				} catch (IOException e) {
				}
			}
		}
		return buf;
	}

	public Object decode(ByteBuf buf) {
		if(buf.readableBytes() < 37){
			return null;
		}
		buf.skipBytes(4);
		int exprlen = buf.readInt();
		byte[] expression = new byte[exprlen];
		buf.readBytes(expression);
		
		int dbsNum = buf.readInt();
		buf.skipBytes(4);
		
		Set<String> dbs = new HashSet<String>();
		for(int i = 0 ; i < dbsNum ; i++){
			int dbLen = buf.readInt();
			byte[] dbArray = new byte[dbLen];
			buf.readBytes(dbArray);
			dbs.add(new String(dbArray,Charsets.UTF_8));
		}
		
		int paramsKvNums = buf.readInt();
		buf.skipBytes(4);
		Map<String,Object> params = new HashMap<String, Object>();
		for(int i = 0 ; i < paramsKvNums ; i++){
			int keyLen = buf.readInt();
			byte[] keyArray = new byte[keyLen];
			buf.readBytes(keyArray);
			
			int valueLen = buf.readInt();
			byte[] valueArray = new byte[valueLen];
			buf.readBytes(valueArray);

			ByteArrayInputStream bais = new ByteArrayInputStream(valueArray);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				Object value = ois.readObject();
				params.put(new String(keyArray , Charsets.UTF_8 ) , value);
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
		return new Task(new String(expression,Charsets.UTF_8), dbs, params);
	}
	
}
