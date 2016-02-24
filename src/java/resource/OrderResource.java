package resource;

import hibernate.Hibernate;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import model.Order;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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
        Order order = (Order) s.get(Order.class, id);
        s.flush();
        s.close();                
        
        return Response.ok(order).build();
    }
}
