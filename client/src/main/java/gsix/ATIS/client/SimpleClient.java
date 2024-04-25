package gsix.ATIS.client;

import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.ocsf.AbstractClient;
import gsix.ATIS.entities.Message;
import org.greenrobot.eventbus.EventBus;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Message message = (Message) msg;
		System.out.println("I am in handle from server");
		if(message.getMessage().equals("get all tasks: Done")){
			System.out.println("I am in handle from server get all done");
			//List<Task> allTasks = (List<Task>) message.getData();
			MessageEvent messageEvent=new MessageEvent(message);
			//EventBus.getDefault().post(allTasks);
			EventBus.getDefault().post(messageEvent);
		}
		//ADDED BY Ayal
		if(message.getMessage().equals("get tasks for community: Done")){
			System.out.println("I am in SimpleClient handle from server get tasks for community done");
			MessageEvent messageEvent=new MessageEvent(message);
			EventBus.getDefault().post(messageEvent);
		}

		else if(message.getMessage().equals("get all users: Done")){
			MessageEvent messageEvent=new MessageEvent(message);
			//EventBus.getDefault().post(allTasks);
			EventBus.getDefault().post(messageEvent);
		}else if(message.getMessage().equals("view task info: Done")){
			System.out.println("I am in handle from server view task info done");
			MessageEvent messageEvent=new MessageEvent(message);
			//EventBus.getDefault().post(allTasks);
			EventBus.getDefault().post(messageEvent);
		}else if(message.getMessage().equals("get task by id: Done")){
			System.out.println("I am in handle from server get task by id done");
			MessageEvent messageEvent=new MessageEvent(message);
			//EventBus.getDefault().post(allTasks);
			EventBus.getDefault().post(messageEvent);
		}else if(message.getMessage().equals("change task status: Done")){
			System.out.println("I am in handle from server change task status done");
			MessageEvent messageEvent=new MessageEvent(message);
			//EventBus.getDefault().post(allTasks);
			EventBus.getDefault().post(messageEvent);
		}else if(message.getMessage().equals("login request: Done")){
			MessageEvent messageEvent=new MessageEvent(message);
			EventBus.getDefault().post(messageEvent);
		}else if(message.getMessage().equals("open request: Done")){
			MessageEvent messageEvent=new MessageEvent(message);
			EventBus.getDefault().post(messageEvent);
		}else if(message.getMessage().equals("get pending tasks: Done")){
			MessageEvent messageEvent=new MessageEvent(message);
			EventBus.getDefault().post(messageEvent);
		}else if(message.getMessage().equals("get task for decline: Done")){
			MessageEvent messageEvent=new MessageEvent(message);
			EventBus.getDefault().post(messageEvent);
		}else {
			EventBus.getDefault().post(new MessageEvent(message));
		}
	}
	
	public static SimpleClient getClient(String server_ip,int server_port) {
		if (client == null) {
			client = new SimpleClient(server_ip, server_port);
		}
		return client;
	}

}
