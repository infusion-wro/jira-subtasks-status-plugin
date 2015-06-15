package helloworld;

import net.jcip.annotations.Immutable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashSet;

/**
 * JAXB representation of a group of projects.
 */
@Immutable
@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
public class ProjectsRepresentation
{
    @XmlElement
    private Collection<ProjectRepresentation> projects;

    // This private constructor isn't used by any code, but JAXB requires any
    // representation class to have a no-args constructor.
    private ProjectsRepresentation()
    {
        projects = null;
    }

    /**
     * Stores the specified {@code Project}s in this representation.
     * @param projects the projects to store
     */
    public ProjectsRepresentation(Iterable<ProjectRepresentation> projects)
    {
        this.projects = new HashSet<ProjectRepresentation>();
        for (ProjectRepresentation representation : projects)
        {
            this.projects.add(representation);
        }
    }
}
