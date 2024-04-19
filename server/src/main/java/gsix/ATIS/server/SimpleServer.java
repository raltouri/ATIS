package gsix.ATIS.server;

import gsix.ATIS.server.ocsf.AbstractServer;
import gsix.ATIS.server.ocsf.ConnectionToClient;
import gsix.ATIS.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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


        Task t1 = new Task("1","2","car repair",LocalDateTime.now(),TaskStatus.Request);
        Task t2 = new Task("2","3","wash machine repair",LocalDateTime.now(),TaskStatus.Pending);
        Task t3 = new Task("3","4","fridge repair",LocalDateTime.now(),TaskStatus.Done);
        Task t4 = new Task("4","5","buy groceries",LocalDateTime.now(),TaskStatus.Request);

        session.save(t1);
        session.save(t2);
        session.save(t3);
        session.save(t4);

        session.flush();

        session.getTransaction().commit();
    }

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
    public static void addTask(Task newTask) {
        session.save(newTask); // Add the task
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

            } else if (request.equals("get all users")) {

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
            else if (request.equals("login request")) {
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
                addTask(newTask);
                Task testAdd = getEntityById(Task.class, newTask.getTask_id());
                message.setData(testAdd);
                message.setMessage("open request: Done");
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
