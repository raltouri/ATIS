package gsix.ATIS.server;

import gsix.ATIS.entities.*;
import gsix.ATIS.server.ocsf.AbstractServer;
import gsix.ATIS.server.ocsf.ConnectionToClient;
import gsix.ATIS.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.NoResultException;

import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    private SessionFactory sessionFactory;
    private static Session session;
    private static Transaction transaction;



    private static void initializeData() throws Exception {

        // Check if tasks exist before creating and saving
        if (!taskExists(1)) {
            Task t1 = new Task(1,"1","car repair",LocalDateTime.now(),TaskStatus.Request);
            session.save(t1);
        }
        if (!taskExists(2)) {
            Task t2 = new Task(2,"2","wash machine repair",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t2);
        }
        if (!taskExists(3)) {
            Task t3 = new Task(3,"3","fridge repair",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t3);
        }
        if (!taskExists(4)) {
            Task t4 = new Task(4,"4","buy groceries",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t4);
        }
        if (!taskExists(5)) {
            Task t5 = new Task(5,"5","CHECK CODE PLEASE",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t5);
        }
        if (!taskExists(6)) {
            Task t6 = new Task(6,"6","VALIDATE CODE PLEASE",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t6);
        }
        if (!taskExists(7)) {
            Task t7 = new Task(7,"7","Task 7",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t7);
        }
        if (!taskExists(8)) {
            Task t8 = new Task(8,"8","Task 8",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t8);
        }
        if (!taskExists(9)) {
            Task t9 = new Task(9,"9","Task 9",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t9);
        }

        session.flush();
        session.getTransaction().commit();
    }

    // Method to check if a task with a given ID exists
    private static boolean taskExists(int taskId) {
        Task task = session.get(Task.class, taskId);
        return task != null;
    }
    public static List<Task> getAllRequestedTasksByCommunity(int communityID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = builder.createQuery(Task.class);
        Root<Task> taskRoot = criteriaQuery.from(Task.class);

        // Joining Task with User entity using the requester_id attribute
        Join<Task, User> userJoin = taskRoot.join("requester");

        // Adding condition to the criteria query based on communityId and status
        criteriaQuery.where(
                builder.equal(userJoin.get("community_id"), communityID),
                builder.equal(taskRoot.get("status"), "Request")
        );

        /*List<Task> list =*/return session.createQuery(criteriaQuery).getResultList();
        //String sqlQuery = session.createQuery(criteriaQuery).unwrap(org.hibernate.query.Query.class).getQueryString();
        //System.out.println("Generated SQL Query: " + sqlQuery);
        //System.out.println("Result: " + list);
        //return list;
    }


    public static <T> List<T> getAllByCommunity(Class<T> object, String userId) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<Task> taskRoot = criteriaQuery.from(Task.class);

        // Joining Task with User entity using the requester_id attribute
        Join<Task, User> userJoin = taskRoot.join("requester"); // Assuming "requester" is the attribute in Task entity referring to User entity

        // Fetching the User entity based on userId
        CriteriaQuery<User> userQuery = builder.createQuery(User.class);
        Root<User> userRoot = userQuery.from(User.class);
        userQuery.select(userRoot).where(builder.equal(userRoot.get("user_id"), userId)); // Assuming the primary key in User entity is "userId"
        User user = session.createQuery(userQuery).getSingleResult();

        // Accessing communityId from the fetched User entity
        int communityId = user.getCommunityId();

        // Adding condition to the criteria query based on communityId
        criteriaQuery.where(
                builder.equal(userJoin.get("community_id"), communityId),
                builder.equal(taskRoot.get("status"), "pending"),
                builder.notEqual(taskRoot.get("requester").get("user_id"), userId)
        );
//
//        // Executing the criteria query and returning the result
//        List<T> list=session.createQuery(criteriaQuery).getResultList();
//        System.out.println("I am in getAllByCommunity"+list);
//        return list;
        // Executing the criteria query and returning the result
        List<T> list = session.createQuery(criteriaQuery).getResultList();
        String sqlQuery = session.createQuery(criteriaQuery).unwrap(org.hibernate.query.Query.class).getQueryString();
        System.out.println("Generated SQL Query: " + sqlQuery);
        System.out.println("Result: " + list);
        return list;
    }


//    private static String getCommunityIdByUserId(String userId) { //Made By Ayal
//        // Your logic to fetch community ID based on user ID
//        String queryString = "SELECT u.community_id FROM User u WHERE u.user_id = :userId";
//        Query<String> query = session.createQuery(queryString, String.class);
//        query.setParameter("userId", userId);
//        return query.uniqueResult();
//    }

    public static <T> List<T> getAll(Class<T> object) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst =allQuery.getResultList();
        System.out.println("I am in getAll"+lst);
        return lst;
    }
    public static <T> List<T> getRequestedTasks(Class<T> object, String userID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Add a condition to filter tasks based on requester_id
        Predicate predicate = builder.equal(rootEntry.get("requester_id"), userID);
        allCriteriaQuery.where(predicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getRequested" + lst);
        return lst;
    }
    public static <T> List<T> getVolunteeredTasks(Class<T> object, String userID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Add a condition to filter tasks based on volunteer_id
        Predicate predicate = builder.equal(rootEntry.get("volunteer_id"), userID);
        allCriteriaQuery.where(predicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getVolunteered" + lst);
        return lst;
    }

    public static <T> List<T> getSentMessages(Class<T> object, String userID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Add a condition to filter tasks based on requester_id
        Predicate predicate = builder.equal(rootEntry.get("sender_id"), userID);
        allCriteriaQuery.where(predicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getSent" + lst);
        return lst;
    }
    public static <T> List<T> getReceivedMessages(Class<T> object, String userID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Add a condition to filter tasks based on requester_id
        Predicate predicate = builder.equal(rootEntry.get("receiver_id"), userID);
        allCriteriaQuery.where(predicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getSent" + lst);
        return lst;
    }



    public static <T> T getUserById(Class<T> object, String Id) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> root = criteriaQuery.from(object);
        if (object.equals(Task.class)) {
            criteriaQuery.select(root).where(builder.equal(root.get("task_id"), Id));
        } else {
            criteriaQuery.select(root).where(builder.equal(root.get("user_id"), Id));
        }
        TypedQuery<T> query = session.createQuery(criteriaQuery);
        return query.getSingleResult();
    }


    public static <T> T getEntityById(Class<T> object, int Id) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> root = criteriaQuery.from(object);
        if (object.equals(Task.class)) {
            criteriaQuery.select(root).where(builder.equal(root.get("task_id"), Id));
        } else {
            criteriaQuery.select(root).where(builder.equal(root.get("user_id"), Id));
        }
        TypedQuery<T> query = session.createQuery(criteriaQuery);
        return query.getSingleResult();
    }
    public static <T> T getUser(Class<T> object,String user_name, String password) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> root = criteriaQuery.from(object);
        if (object.equals(User.class)) {
            Predicate condition1 = builder.equal(root.get("user_name"), user_name);
            Predicate condition2 = builder.equal(root.get("password"), password);
            Predicate finalCondition = builder.and(condition1, condition2);
            criteriaQuery.select(root).where(finalCondition);
        }
        TypedQuery<T> query = session.createQuery(criteriaQuery);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            // User not found
            String result = "Username or password is incorrect";
            Message message = new Message(1,result);
            return (T) message; // or throw a custom exception, or handle it according to your requirement
            // casting to T cause the func must return , maybe needed to be checked
        }
    }

    public static void updateTask(Task updatedTask) {
        session.update(updatedTask); // Update the task
        transaction.commit();
    }
    public static void addTask(Task newTask) {
        session.save(newTask); // Add the task
        transaction.commit();
    }
    public static void deleteTask(Task taskToDelete) {
        System.out.println("in server in delete task from DB function");

        session.delete(taskToDelete); // Delete the task
        transaction.commit();
        System.out.println("Task deleted successfully.");
    }


    public static void printTasksTest(List<Task> lst){
        for (Task task:lst){
            System.out.println(task.getStatus());
        }
    }
    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(Task.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Community.class);
        configuration.addAnnotatedClass(CommunityMessage.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    public SimpleServer(int port) {
        super(port);

        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            initializeData();
        }
        catch (Exception e) {
            if (session != null) {
                System.out.println("I am about to rollback");
                session.getTransaction().rollback();
            }

            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
                session.getSessionFactory().close();
            }
        }
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        String request = message.getMessage();
        System.out.println("Im inside handle from client");
        try {

            sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            //we got an empty message, so we will send back an error message with the error details.
            if (request.isBlank()) {
                message.setMessage("Error! we got an empty message");
                client.sendToClient(message);
            }
            //we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
            // the IDs at data field in Message entity and send back to all subscribed clients a request to update
            //their IDs text fields. An example of use of observer design pattern.
            //message format: "change submitters IDs: 123456789, 987654321"
            else if (request.equals("get all tasks")) {
                //System.out.println("I am in get all tasks");
                message.setData(getAll(Task.class));
                message.setMessage("get all tasks: Done");
                //System.out.println(message);
                //printTasksTest((List<Task>) message.getData());
                //System.out.println(client);
                client.sendToClient(message);
                //sendToAllClients(message);

            }
            else if(request.equals("get tasks for community")){ //Added by Ayal
                System.out.println("inside SimpleServer get task for community");
                String userId = (String) message.getData();
                message.setData(getAllByCommunity((Task.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get tasks for community: Done");
                client.sendToClient(message);
            }
            else if(request.equals("get requested tasks by community")){
                System.out.println("inside SimpleServer get task for community");
                int communityID = (int) message.getData();
                message.setData(getAllRequestedTasksByCommunity(communityID));
                System.out.println(message.getData());
                message.setMessage("get requested tasks by community: Done");
                client.sendToClient(message);
            }
            else if(request.equals("send message")){ //Added by Ayal
                System.out.println("inside SimpleServer send message");
                String messageString = (String) message.getData();

                insertMessageToDataTable(messageString);

                message.setMessage("send masage to manager: Done");
                client.sendToClient(message);
            }
            // added by waheeb modified by ayal
            else if(request.equals("delete requested task")){
                System.out.println("inside SimpleServer delete requested task");
                int taskID = (int) message.getData();
                Task task=getEntityById(Task.class,taskID);//get task by id
                deleteTask(task);
                message.setMessage("delete requested task: Done");
                client.sendToClient(message);
            }
            else if(request.equals("update task volunteer")){ //Added by Ayal
                System.out.println("inside simple server update task volunteer");
                Task dummyTask = (Task) message.getData();
                Task taskTarget=getEntityById(Task.class,dummyTask.getTask_id());
                taskTarget.setVolunteer_id(dummyTask.getVolunteer_id());
                updateTask(taskTarget);
                message.setMessage("update task volunteer : Done");
                client.sendToClient(message);
            }
            else if(request.equals("get manager id")){ //Added by Ayal
                System.out.println("inside SimpleServer get manager id");
                int communityID = (int) message.getData();
                message.setData(getManagerID(communityID));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());//if it didnt reach here it means there is a problem
                message.setMessage("manager id is here");
                client.sendToClient(message);
            }
            else if(request.equals("get requested tasks")){ //Added by Ayal
                System.out.println("inside SimpleServer get requested tasks ");
                String userId = (String) message.getData();
                message.setData(getRequestedTasks((Task.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get requested tasks: Done");
                client.sendToClient(message);
            }
            else if(request.equals("get volunteered tasks")){ //Added by Ayal
                System.out.println("inside SimpleServer get volunteered tasks ");
                String userId = (String) message.getData();
                message.setData(getVolunteeredTasks((Task.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get volunteered tasks: Done");
                client.sendToClient(message);
            }

            else if(request.equals("get sent messages")){ //Added by Ayal
                System.out.println("inside SimpleServer get sent messages ");
                String userId = (String) message.getData();
                message.setData(getSentMessages((CommunityMessage.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get sent messages: Done");
                client.sendToClient(message);
            }
            else if(request.equals("get received messages")){ //Added by Ayal
                System.out.println("inside SimpleServer get received messages ");
                String userId = (String) message.getData();
                message.setData(getReceivedMessages((CommunityMessage.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get received messages: Done");
                client.sendToClient(message);
            }
            else if (request.equals("get all users")) {

                message.setData(getAll(User.class));
                message.setMessage("get all users: Done");
                client.sendToClient(message);

            }
            else if (request.equals("get all community users")) {
                String community_id = (String) message.getData();
                message.setData(getAllByCommunity(User.class, community_id));
                message.setMessage("get all community users: Done");
                client.sendToClient(message);

            }else if (request.equals("view task info")) {

                int taskId = (Integer) message.getData();
                Task task = getEntityById(Task.class, taskId);
                message.setData(task);
                message.setMessage("view task info: Done");
                client.sendToClient(message);

            } else if (request.equals("get task by id")) {

                int taskId = (Integer) message.getData();
                Task updatedTask = getEntityById(Task.class, taskId);
                //updatedTask.setStatus(TaskStatus.Pending);
                updateTask(updatedTask);
                Task testUpdate = getEntityById(Task.class, taskId);
                message.setData(testUpdate);
                message.setMessage("get task by id: Done");
                client.sendToClient(message);

            }else if (request.equals("get task for decline")) {

                int taskId = (Integer) message.getData();
                Task declinedTask = getEntityById(Task.class, taskId);
                message.setData(declinedTask);
                message.setMessage("get task for decline: Done");
                client.sendToClient(message);

            }else if (request.equals("change task status")) {

                Task updatedTask = (Task) message.getData();
                updateTask(updatedTask);
                Task testUpdate = getEntityById(Task.class, updatedTask.getTask_id());
                message.setData(testUpdate);
                message.setMessage("change task status: Done");
                client.sendToClient(message);

            }
            else if (request.equals("update task status")) {

                String updatedTaskIDInfo = (String) message.getData(); // string="taskID,status"
                String[] parts = updatedTaskIDInfo.split(",");
                System.out.println(Arrays.toString(parts)); //////////////////
                int updatedTaskID = Integer.parseInt(parts[0]);
                String newStatus = parts[1];

                updateTaskByID(updatedTaskID, newStatus);

                Task testUpdate = getEntityById(Task.class, updatedTaskID);
                message.setData(testUpdate);
                message.setMessage("change task status: Done");
                client.sendToClient(message);

            } else if (request.equals("delete task")) {

                int taskID = (int) message.getData();
                System.out.println("in server in delete task command");
                Task taskToDelete = getEntityById(Task.class, taskID);
                deleteTask(taskToDelete);
                message.setData(taskID);
                message.setMessage("delete task: Done");
                client.sendToClient(message);

            }else if (request.equals("get user by id")) {

                int userID = (Integer) message.getData();
                User user = getEntityById(User.class, userID);

                message.setData(user);
                message.setMessage("get user by id: Done");
                client.sendToClient(message);

            }
            else if (request.equals("login request")) {
                System.out.println("*********INSIDE LOGING REQUEST*********");
                User userData = (User) message.getData();
                Object result = getUser(User.class,userData.getUser_name(),userData.getPassword());
                if(result instanceof User){
                    message.setMessage("login request: Done");
                    User target = (User) result;
                    message.setData(target);
                }
                //User target = getUser(User.class,userData.getUser_name(),userData.getPassword());
                else /*if(result instanceof Message)*/{
                    message.setMessage("login request: Failed");
                    Message errMsgg = (Message) result;
                    //String errMsg = errMsgg.getMessage();
                    String errMsg ="Username or password is incorrect";
                    message.setData(errMsg);
                }
                client.sendToClient(message);
            }else if (request.equals("open request")) {

                Task newTask = (Task) message.getData();
                System.out.println(newTask.toString());
                addTask(newTask);
                Task testAdd = getEntityById(Task.class, newTask.getTask_id());
                message.setData(testAdd);
                message.setMessage("open request: Done");
                client.sendToClient(message);
                //// Send message to current task requester's community manager to approve message
                User requester = getUserById(User.class,testAdd.getRequester_id());
                String managerID = getManagerID(requester.getCommunityId());
                String messageContent = "New task has been received. Task ID: "+testAdd.getTask_id()+"\nPlease approve it so your community users can volunteer to do it\n";
                saveMessage(requester.getUser_id(),managerID,messageContent);
            }
            // Commit the transaction
            System.out.println("server before transaction commit");
            transaction.commit();
        } catch (IOException e1) {
            if (transaction != null && transaction.isActive()) {
                System.out.println("I am about to roll back2");
                transaction.rollback();
            }
            e1.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
                session.getSessionFactory().close();
            }
        }
    }

    private void saveMessage(String senderID,String receiverID, String messageContent) {

        // Check if the session is available and open
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
        }
        // Check if there is an active transaction
        if (session.getTransaction() != null && session.getTransaction().isActive()) {//should we roll back or commit?
            System.out.println("There is an active transaction. Commiting...");
            session.getTransaction().commit();
        }

        // Continue with transaction handling
        try {
            // Begin a new transaction
            session.beginTransaction();
            System.out.println("Transaction has BEGUN");

            // Create a new CommunityMessage object
            CommunityMessage communityMessage = new CommunityMessage();
            communityMessage.setSender_id(senderID);
            communityMessage.setReceiver_id(receiverID);
            communityMessage.setContent(messageContent);

            // Save the communityMessage object to the database
            session.save(communityMessage);
            System.out.println("After session.save");

            // Commit the transaction
            session.getTransaction().commit();
            System.out.println("Message saved to the database.");
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
    }

    private void insertMessageToDataTable(String messageString) {//MADE BY Ayal
        //my messageString format is : senderID+","+receiverID+","+content
        // Find the position of the first comma
        int firstCommaIndex = messageString.indexOf(",");

        // Find the position of the second comma
        int secondCommaIndex = messageString.indexOf(",", firstCommaIndex + 1);

        // Extract senderID and receiverID
        String senderID = messageString.substring(0, firstCommaIndex);
        String receiverID = messageString.substring(firstCommaIndex + 1, secondCommaIndex);

        // Extract message content (everything after the second comma)
        String messageContent = messageString.substring(secondCommaIndex + 1);

        System.out.println(senderID);
        System.out.println(receiverID);
        System.out.println(messageContent);
        // Check if the session is available and open
        if (session == null || !session.isOpen()) {
            System.out.println("Session is null or closed.");
            return;
        }
        // Check if there is an active transaction
        if (session.getTransaction() != null && session.getTransaction().isActive()) {//should we roll back or commit?
            System.out.println("There is an active transaction. Commiting...");
            session.getTransaction().commit();
        }

        // Continue with transaction handling
        try {
            // Begin a new transaction
            session.beginTransaction();
            System.out.println("Transaction has BEGUN");

            // Create a new CommunityMessage object
            CommunityMessage communityMessage = new CommunityMessage();
            communityMessage.setSender_id(senderID);
            communityMessage.setReceiver_id(receiverID);
            communityMessage.setContent(messageContent);

            // Save the communityMessage object to the database
            session.save(communityMessage);
            System.out.println("After session.save");

            // Commit the transaction
            session.getTransaction().commit();
            System.out.println("Message saved to the database.");
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }


    }

    private String getManagerID(int communityID) {//added by Ayal
        String managerID = null;
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<String> criteriaQuery = builder.createQuery(String.class);
            Root<Community> root = criteriaQuery.from(Community.class);

            Predicate predicate = builder.equal(root.get("community_id"), communityID);
            criteriaQuery.select(root.get("manager_id")).where(predicate);

            managerID = session.createQuery(criteriaQuery).getSingleResult();
            System.out.println("Manager ID for Community ID " + communityID + " is: " + managerID);

            transaction.commit();
        } catch (NoResultException e) {
            System.out.println("No manager found for Community ID: " + communityID);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return managerID;
    }


    private void updateTaskByID(int updatedTaskID, String newStatus) {//added by Ayal
        Transaction transaction = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Retrieve the task entity by ID
            Task task = session.get(Task.class, updatedTaskID);
            // Update the task status to "done"
            if(newStatus.equals("Done")){
                task.setStatus(TaskStatus.Done);
            }
            if(newStatus.equals("Pending")){
                task.setStatus(TaskStatus.Pending);
            }

            // Commit the transaction
            session.update(task);
            transaction.commit();
            System.out.println("Task with ID " + updatedTaskID + " updated to "+newStatus+".");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }


    public void sendToAllClients(Message message) {
        try {
            for (SubscribedClient SubscribedClient : SubscribersList) {
                SubscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
