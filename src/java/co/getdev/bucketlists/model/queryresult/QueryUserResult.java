package co.getdev.bucketlists.model.queryresult;

import co.getdev.bucketlists.model.Authority;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;

/**
 * API model for returning user details.
 *
 * @author Ogundipe Segun David
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryUserResult {
 
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Set<Authority> authorities;
    private Boolean active;

    public QueryUserResult() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
