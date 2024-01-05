package org.cqframework.cql.elm;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.hl7.elm.r1.Element;
import org.junit.Test;

public class IdObjectFactoryTest {

    @Test
    public void ensureAllElementsHaveLocalId() {
        var factory = new IdObjectFactory();
        var methods = Arrays.asList(IdObjectFactory.class.getMethods()).stream()
                .filter(x -> Element.class.isAssignableFrom(x.getReturnType()));
        methods.forEach(x -> {
            try {
                Element e = (Element) x.invoke(factory);
                if (e.getLocalId() == null) {
                    throw new RuntimeException(
                            String.format("%s missing localId", e.getClass().getSimpleName()));
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
