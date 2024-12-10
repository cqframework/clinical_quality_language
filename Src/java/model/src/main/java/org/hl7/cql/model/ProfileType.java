package org.hl7.cql.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by Bryn on 8/22/2016.
 *
 * Profiles within CQL allow the same underlying type to be accessed using different labels. When
 * a profile is referenced, the ELM retrieve that is output will use the baseType of the profile as the
 * dataType, and the name of the profile as the identifier. This allows the implementation to detect
 * when the same data type has been accessed in a different way by the author.
 *
 * This mechanism is used in QDM to enable the negation pattern. For example, the Encounter, Performed
 * data type is the same whether it is accessed positively or negatively, but the difference needs to be
 * communicated reliably through the data access layer, so profiles are used, one positive and one negative.
 * The underlying type Encounter, Performed, is not retrievable, only the positive and negative profiles. The
 * identifiers are set to Encounter, Performed, and Encounter, Not Performed, and the resulting retrieve will
 * reflect the EncounterPerformed type, together with the name of the profile, PositiveEncounterPerformed or
 * NegativeEncounterPerformed, depending on which profile was used in the retrieve.
 *
 * NOTE: This behavior was subsequently changed due to the inconsistency it introduces between retrieves
 * and general-purpose expressions. QDM still defines a base type with profiles for positive and negative
 * aspects, but the retrieve will now return the profile type, not the base type. See github issue #131 for
 * a detailed discussion of this change.
 */
public class ProfileType extends ClassType {
    public ProfileType(String name, DataType baseType, List<ClassTypeElement> elements) {
        super(name, baseType, elements, Collections.emptyList());
    }

    public ProfileType(String name, DataType baseType) {
        this(name, baseType, Collections.emptyList());
    }
}
