package com.sankuai.canyin.r.wushan.server.worker;

/**
 * DN -> Worker
 * @author kyrin
 *
 */
public class Command {
	
	CommandType type;
	
	String taskId;
	
	public Command( CommandType type  , String taskId) {
		this.type = type;
		this.taskId = taskId;
	}

	public CommandType getType() {
		return type;
	}
	
	public String getTaskId() {
		return taskId;
	}

	@Override
	public String toString() {
		return "Command [type=" + type + "]";
	}

	//NN -> DN -> Worker 命令
	public enum CommandType {
		DESTROY(0) , //销毁worker
		RESATRT(1);  //重启worker
		
		int code;
		
		private CommandType(int code) {
			this.code = code;
		}
		
		public int code(){
			return code;
		}
		
		public static CommandType codeOf(int code){
			CommandType[] values = values();
			for(CommandType type : values){
				if(type.code() == code){
					return type;
				}
			}
			return null;
		}
	}
}
