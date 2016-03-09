package resource;

import hibernate.Hibernate;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import model.Order;
import model.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Path("order")
public class OrderResource {

    @Context
    private UriInfo context;

    public OrderResource() {
    }

    @GET
    @Path("/{id}")
    @Produces("application/json; charset=UTF-8")
    public Response findById(@PathParam("id") int id) {
        SessionFactory sf = Hibernate.getSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        
        Order order = (Order) s.get(Order.class, id);
        t.commit();
        
        s.flush();
        s.close();

        return Response.ok(order).build();
    }

    @POST
    @Path("/new")
    @Produces("application/json; charset=UTF-8")
    public Response newOrder(@FormParam("user_id") int userId) {

        SessionFactory sf = Hibernate.getSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();

        Query query = s.createQuery("FROM Order WHERE user_id = :userId AND status = :status ORDER BY id DESC");
        query.setInteger("userId", userId);
        query.setInteger("status", Order.STATUS_OPENED);
        query.setMaxResults(1);
        Order lastOpenedOrder = (Order) query.uniqueResult();

        if (lastOpenedOrder == null) {
            try {
                User user = (User) s.get(User.class, userId);

                Order newOrder = new Order();
                newOrder.setStatus(Order.STATUS_OPENED);
                newOrder.setBalance(0.0);
                newOrder.setUser(user);

                s.save(newOrder);

                t.commit();
                s.flush();
                s.close();

                return Response.ok(newOrder).build();
            } catch (Exception e) {
                t.rollback();
                s.flush();
                s.close();

                e.printStackTrace();

                return Response.serverError().build();
            }

        } else {
            return Response.ok(lastOpenedOrder).build();
        }
    }

    @PUT
    @Path("/payment")
    @Produces("application/json; charset=UTF-8")
    public Response orderPayment(@FormParam("order_id") int orderId,
            @FormParam("payment_method") int paymentMethod) {

        SessionFactory sf = Hibernate.getSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();

        try {
            Order order = (Order) s.get(Order.class, orderId);

            order.setBalance(0.0);
            order.setPaymentMethod(paymentMethod);
            order.setStatus(Order.STATUS_CLOSED);

            s.update(order);

            t.commit();
            s.flush();
            s.close();
            
            return Response.ok(order).build();
        } catch (Exception e) {
            t.rollback();
            s.flush();
            s.close();
            
            e.printStackTrace();
            
            return Response.serverError().build();
        }
    }
}
