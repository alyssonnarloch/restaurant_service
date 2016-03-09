package resource;

import hibernate.Hibernate;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import model.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Path("item")
public class ItemResource {

    @Context
    private UriInfo context;

    public ItemResource() {
    }

    @GET
    @Path("all")
    @Produces("application/json; charset=UTF-8")
    public Response findAll() {
        SessionFactory sf = Hibernate.getSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
                
        List<Item> items = s.createCriteria(Item.class).list();
        t.commit();
        
        s.flush();
        s.close();

        GenericEntity<List<Item>> entity = new GenericEntity<List<Item>>(items) {
        };

        return Response.ok(entity).build();
    }

    @GET
    @Path("/{id}")
    @Produces("application/json; charset=UTF-8")
    public Response findById(@PathParam("id") int id) {
        SessionFactory sf = Hibernate.getSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        
        Item item = (Item) s.get(Item.class, id);
        t.commit();
        
        s.flush();
        s.close();                
        
        return Response.ok(item).build();
    }
}
