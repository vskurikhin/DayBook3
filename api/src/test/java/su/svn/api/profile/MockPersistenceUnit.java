package su.svn.api.profile;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import org.hibernate.reactive.mutiny.Mutiny;
import org.mockito.Mockito;

@Alternative
public class MockPersistenceUnit {
    @Produces
    public Mutiny.SessionFactory sessionFactory() {
        return Mockito.mock(Mutiny.SessionFactory.class);
    }
}
