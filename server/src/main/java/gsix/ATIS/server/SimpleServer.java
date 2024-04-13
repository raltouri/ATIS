package gsix.ATIS.server;

import gsix.ATIS.server.ocsf.AbstractServer;
import gsix.ATIS.server.ocsf.ConnectionToClient;
import gsix.ATIS.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.NoResultException;

import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.TaskStatus;
import gsix.ATIS.entities.User;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    private SessionFactory sessionFactory;
    private static Session session;
    private static Transaction transaction;



    private static void initializeData() throws Exception {


        // Check if tasks exist before creating and saving
        if (!taskExists(1)) {
            Task t1 = new Task("1",2,"car repair",LocalDateTime.now(),TaskStatus.Request);
            session.save(t1);
        }
        if (!taskExists(2)) {
            Task t2 = new Task("2",3,"wash machine repair",LocalDateTime.now(),TaskStatus.Pending);
            session.save(t2);
        }
        if (!taskExists(3)) {
            Task t3 = new Task("3",8,"fridge repair",LocalDateTime.now(),TaskStatus.Done);
            session.save(t3);
        }
        if (!taskExists(4)) {
            Task t4 = new Task("4",5,"buy groceries",LocalDateTime.now(),TaskStatus.Request);
            session.save(t4);
        }
        if (!taskExists(5)) {
            Task t5 = new Task("5",6,"CHECK CODE PLEASE",LocalDateTime.now(),TaskStatus.Request);
            session.save(t5);
        }
        if (!taskExists(6)) {
            Task t6 = new Task("6",7,"VALIDATE CODE PLEASE",LocalDateTime.now(),TaskStatus.Request);
            session.save(t6);
        }
        if (!taskExists(7)) {
            Task t7 = new Task("7",8,"Task 7",LocalDateTime.now(),TaskStatus.Request);
            session.save(t7);
        }
        if (!taskExists(8)) {
            Task t8 = new Task("8",9,"Task 8",LocalDateTime.now(),TaskStatus.Request);
            session.save(t8);
        }
        if (!taskExists(9)) {
            Task t9 = new Task("9",8,"Task 9",LocalDateTime.now(),TaskStatus.Request);
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
                builder.equal(taskRoot.get("status"), "pending")
        );

        // Executing the criteria query and returning the result
        List<T> list=session.createQuery(criteriaQuery).getResultList();
        System.out.println("I am in getAllByCommunity"+list);
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

    public static void printTasksTest(List<Task> lst){
        for (Task task:lst){
            System.out.println(task.getStatus());
        }
    }
    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(Task.class);
        configuration.addAnnotatedClass(User.class);

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

            }else if(request.equals("get tasks for community")){ //Added by Ayal
                System.out.println("inside SimpleServer get task for community");
                String userId = (String) message.getData();
                message.setData(getAllByCommunity((Task.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get tasks for community: Done");
                client.sendToClient(message);
            }

            else if (request.equals("get all users")) {

                message.setData(getAll(User.class));
                message.setMessage("get all users: Done");
                client.sendToClient(message);

            } else if (request.equals("view task info")) {

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

            }else if (request.equals("change task status")) {

                Task updatedTask = (Task) message.getData();
                updateTask(updatedTask);
                Task testUpdate = getEntityById(Task.class, updatedTask.getTask_id());
                message.setData(testUpdate);
                message.setMessage("change task status: Done");
                client.sendToClient(message);

            }
            else if (request.equals("update task status")) {

                int updatedTaskID = (int) message.getData();
                updateTaskByID(updatedTaskID);

                Task testUpdate = getEntityById(Task.class, updatedTaskID);
                message.setData(testUpdate);
                message.setMessage("change task status: Done");
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
            }
            /* else {
                //add code here to send received message to all clients.
                //The string we received in the message is the message we will send back to all clients subscribed.
                //Example:
                // message received: "Good morning"
                // message sent: "Good morning"
                //see code for changing submitters IDs for help
                message.setMessage(request);
                sendToAllClients(message);
            }*/

            //session.save(entity);

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

    private void updateTaskByID(int updatedTaskID) {
        Transaction transaction = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Retrieve the task entity by ID
            Task task = session.get(Task.class, updatedTaskID);

            // Update the task status to "done"
            task.setStatus(TaskStatus.Done);

            // Commit the transaction
            session.update(task);
            transaction.commit();
            System.out.println("Task with ID " + updatedTaskID + " updated to 'done'.");
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
