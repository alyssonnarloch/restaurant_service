package resource;

import hibernate.Hibernate;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import model.Item;
import model.Order;
import model.OrderItem;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Path("orderitem")
public class OrderItemResource {

    @Context
    private UriInfo context;

    public OrderItemResource() {
    }

    @GET
    @Path("/order_items/user/{user_id}")
    @Produces("application/json; charset=UTF-8")
    public Response getByLastOrderUser(@PathParam("user_id") int userId) {
        SessionFactory sf = Hibernate.getSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();

        Query query = s.createQuery("FROM Order WHERE user_id = :userId AND status = :status ORDER BY id DESC");
        query.setInteger("userId", userId);
        query.setInteger("status", Order.STATUS_OPENED);
        query.setMaxResults(1);
        Order lastOpenedOrder = (Order) query.uniqueResult();

        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        
        if (lastOpenedOrder != null) {
            Query queryItems = s.createQuery("FROM OrderItem WHERE order_id = :orderId");
            queryItems.setInteger("orderId", lastOpenedOrder.getId());
            orderItems = queryItems.list();
        }

        GenericEntity<List<OrderItem>> entity = new GenericEntity<List<OrderItem>>(orderItems) {
        };

        t.commit();
        
        s.flush();
        s.close();

        return Response.ok(entity).build();
    }

    @POST
    @Path("/add_item")
    @Produces("application/json; charset=UTF-8")
    public Response newOrderItem(@FormParam("order_id") int orderId,
            @FormParam("item_id") int itemId,
            @FormParam("amount") int amount) {

        SessionFactory sf = Hibernate.getSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();

        try {
            Order order = (Order) s.get(Order.class, orderId);
            Item item = (Item) s.get(Item.class, itemId);

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setAmount(amount);
            s.save(orderItem);

            order.setBalance(order.getBalance() + (item.getPrice() * amount));
            s.update(order);
            
            t.commit();
            s.flush();
            s.close();
            
            return Response.ok(orderItem).build();
        } catch (Exception e) {
            t.rollback();
            s.flush();
            s.close();

            e.printStackTrace();

            return Response.serverError().build();
        }
    }
}
