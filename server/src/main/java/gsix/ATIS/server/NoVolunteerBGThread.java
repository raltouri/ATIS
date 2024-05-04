package gsix.ATIS.server;

import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
 * TO START THE THREAD :
 * NoVolunteerBGThread bgThread = new NoVolunteerBGThread();
 * Thread thread = new Thread(bgThread);
 * thread.start(); // Starts the background thread
 */
public class NoVolunteerBGThread implements Runnable{
    //private boolean isRunning = true; // Flag to control job execution

    private Timer timer;
    private SimpleServer server;
/*    private List<Task> alllOverDuePendingTasks = null;
    private List<User> community_users = null;
    private Task foundTask = null;
    private User taskRequester = null;*/
    NoVolunteerBGThread(SimpleServer server){
        this.server = server;
    }

    public void stopJob() {
        //isRunning = false;
        timer.cancel(); // Cancel the timer
    }

    @Override
    public void run() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Implement your background task here
                // Check if any task has exceeded 20 seconds with no volunteer
                // Notify system users if necessary
                // For demonstration, I'm printing a message to console
                System.out.println("Background thread is running...");
                getAllSystemOverDuePendingTasks();
            }
        }, 0, 10*1000); // Checks every 10 seconds
    }

    private void getAllSystemOverDuePendingTasks() {
        // calling server method to retrieve pending tasks
        List<Task> allOverDuePendingTasks = server.getAllOverDuePendingTasks();
        // Iterate through pending tasks
        if (allOverDuePendingTasks != null) {
            for (Task task : allOverDuePendingTasks) {
                //if (checkNoVolunteerYet(task)) {
                    // If no volunteer yet for this task, notify community users
                    User taskRequester = getTaskRequester(task);
                    //List<User> community_users = getCommunityOfTaskRequester(taskRequester);
                    notifyCommunityUsers(taskRequester.getCommunityId(), task);
                //}
            }
        }
    }

    private User getTaskRequester(Task task) {
        return SimpleServer.getEntityById(User.class, Integer.parseInt(task.getRequester_id()));
    }

    /**
     * we decided not to import all community of overdue task from server but rather
     * send to all clients only community_id with overDueTask then for each user we
     * check if he belongs to the same community then he'll get a pop-up notification.
     **/
    private List<User> getCommunityOfTaskRequester(User task_requester) {
        // Assuming server has a method to retrieve community users by community ID
        return SimpleServer.getAllCommunityMembers(task_requester.getCommunityId());
    }

    /*private boolean checkNoVolunteerYet(Task task) {
        // Get the current time
        Instant currentTime = Instant.now();

        // Get the task creation time
        Instant taskCreationTime = task.getTime().atZone(ZoneId.systemDefault()).toInstant();

        // Calculate the duration between the current time and the task creation time
        Duration duration = Duration.between(taskCreationTime, currentTime);

        // Check if the duration is greater than 20 seconds and the task has no volunteer assigned
        return duration.getSeconds() > 10 && task.getVolunteer_id() == null;
    }*/

    private void notifyCommunityUsers(int community_ID, Task overDueTask) {
        try {
            if (overDueTask != null) {
                // Code to notify users about the yet not volunteered to task after a defined time
                /*for (User user : community_users) {
                    // Notify each user
                    //systemNotify(user.getId(), "There is a pending task without volunteers.");
                    String sendTo_ID = user.getUser_id();
                    if(sendTo_ID.equals(overDueTask.getRequester_id()){
                        systemNotify(sendTo_ID,
                                "community members have been notified about your yet unassisted task");
                    } else {
                        systemNotify(sendTo_ID,"Task of ID= " + foundTask.getTask_id() +
                                "is still unassisted yet");
                    }
                }*/

                /**
                 * overDueTask has no volunteer, so we used volunteer_id field to send the community_id
                 **/
                overDueTask.setVolunteer_id(community_ID + "");
                Message msg = new Message(1, LocalDateTime.now(), "notify overdue task",overDueTask);
                //System.out.println("Svetlana**************************");
                server.sendToAllClients(msg);
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println(e.getMessage());
        }
    }

 /*   public static void systemNotify(String receiverID, String msg) {
        String sender = "System";

    }*/

}

