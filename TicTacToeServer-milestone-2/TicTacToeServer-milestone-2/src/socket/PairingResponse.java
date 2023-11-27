package socket;

import model.User;
import model.Event;

import java.util.List;

/**
 * Server response to an UPDATE_PAIRING request. It is a subclass of Response.java
 */
public class PairingResponse extends Response {

    /**
     * A list of User class. Represents players that are available to receive game invitations.
     */
    private List<User> availableUsers;

    /**
     * An object of type Event. Represents an event for a game invitation from another user.
     */
    private Event invitation;

    /**
     * An object of type Event. Represents an event for a response to a game invitation earlier sent by the current user.
     * Indicating whether the invitation is accepted or declined.
     */
    private Event invitationResponse;

    /**
     * Default constructor for the class. Must call the constructor of the super class.
     */
    public PairingResponse() {
        super();
    }

    /**
     * A constructor that sets all attributes of this class. Must call the constructor of the super class.
     *
     * @param status              The response status.
     * @param message             The response message.
     * @param availableUsers      A list of User class representing players that are available to receive game invitations.
     * @param invitation          An object of type Event representing an event for a game invitation from another user.
     * @param invitationResponse  An object of type Event representing an event for a response to a game invitation earlier sent by the current user.
     */
    public PairingResponse(ResponseStatus status, String message, List<User> availableUsers, Event invitation, Event invitationResponse) {
        super(status, message);
        this.availableUsers = availableUsers;
        this.invitation = invitation;
        this.invitationResponse = invitationResponse;
    }

    /**
     * Getter for availableUsers attribute.
     *
     * @return availableUsers
     */
    public List<User> getAvailableUsers() {
        return availableUsers;
    }

    /**
     * Setter for availableUsers attribute.
     *
     * @param availableUsers A list of User class representing players that are available to receive game invitations.
     */
    public void setAvailableUsers(List<User> availableUsers) {
        this.availableUsers = availableUsers;
    }

    /**
     * Getter for invitation attribute.
     *
     * @return invitation
     */
    public Event getInvitation() {
        return invitation;
    }

    /**
     * Setter for invitation attribute.
     *
     * @param invitation An object of type Event representing an event for a game invitation from another user.
     */
    public void setInvitation(Event invitation) {
        this.invitation = invitation;
    }

    /**
     * Getter for invitationResponse attribute.
     *
     * @return invitationResponse
     */
    public Event getInvitationResponse() {
        return invitationResponse;
    }

    /**
     * Setter for invitationResponse attribute.
     *
     * @param invitationResponse An object of type Event representing an event for a response to a game invitation earlier sent by the current user.
     */
    public void setInvitationResponse(Event invitationResponse) {
        this.invitationResponse = invitationResponse;
    }
}
