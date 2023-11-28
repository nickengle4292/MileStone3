package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.Event;
import socket.GamingResponse;
import socket.Request;
import socket.Response;
import model.User;
import socket.PairingResponse;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;




/**
 * A class that helps SocketServer Handle individual user communication. This class extends {@link Thread}
 *
 * @author Nicholas Engle
 */
public class ServerHandler extends Thread {

	private final Logger LOGGER;
	private final Socket socket;
	private final DataInputStream inputStream;
	private final DataOutputStream outputStream;
	private final Gson gson;

	public String currentUsername;
	private int currentEventId;  // Added currentEventId attribute

	public ServerHandler(Socket socket) throws IOException {
		LOGGER = Logger.getLogger(ServerHandler.class.getName());
		this.socket = socket;
		this.currentUsername = ""; // Initialize the username in the constructor
		this.currentEventId = -1;
		this.gson = new GsonBuilder().serializeNulls().create();
		this.inputStream = new DataInputStream(socket.getInputStream());
		this.outputStream = new DataOutputStream(socket.getOutputStream());
	}

	@Override
	public void run() {
		while (true) {
			try {
				String serializedRequest = inputStream.readUTF();
				Request request = gson.fromJson(serializedRequest, Request.class);
				LOGGER.log(Level.INFO, "Client Request: " + currentUsername + " - " + request.getType());

				Response response = handleRequest(request);
				String serializedResponse = gson.toJson(response);
				outputStream.writeUTF(serializedResponse);
				outputStream.flush();
			} catch (EOFException e) {
				LOGGER.log(Level.INFO, "Server Info: Client Disconnected: " + currentUsername + " - " + socket.getRemoteSocketAddress());
				closeSocket();
				break;
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Server Info: Client Connection Failed", e);
			} catch (JsonSyntaxException e) {
				LOGGER.log(Level.SEVERE, "Server Info: Serialization Error", e);
			}
		}
	}

	private void closeSocket() {
		try {
			// Check if the user has previously logged in
			if (currentUsername != null) {
				// Get the User object corresponding to the currentUsername
				User user = DatabaseHelper.getInstance().getUser(currentUsername);

				// Set the user online attribute to false
				user.setOnline(false);

				// Update the user in the database
				DatabaseHelper.getInstance().updateUser(user);

				// Abort any event that is not either COMPLETED or ABORTED
				DatabaseHelper.getInstance().abortAllUserEvents(currentUsername, currentEventId);
			}

			socket.close();
			inputStream.close();
			outputStream.close();
		} catch (IOException | SQLException e) {
			LOGGER.log(Level.SEVERE, "Server Info: Unable to close socket or update user status", e);
		}
	}


	private Response handleRequest(Request request) {
		switch (request.getType()) {
			case LOGIN:
				User loginUser = gson.fromJson(request.getData(), User.class);
				return handleLogin(loginUser);

			case REGISTER:
				User registerUser = gson.fromJson(request.getData(), User.class);
				return handleRegister(registerUser);

			case UPDATE_PAIRING:
				return handleUpdatePairing();

			case SEND_INVITATION:
				String opponent = gson.fromJson(request.getData(), String.class);
				return handleSendInvitation(opponent);

			case ACCEPT_INVITATION:
				int acceptEventId = gson.fromJson(request.getData(), Integer.class);
				return handleAcceptInvitation(acceptEventId);

			case DECLINE_INVITATION:
				int declineEventId = gson.fromJson(request.getData(), Integer.class);
				return handleDeclineInvitation(declineEventId);

			case ACKNOWLEDGE_RESPONSE:
				int acknowledgeEventId = gson.fromJson(request.getData(), Integer.class);
				return handleAcknowledgeResponse(acknowledgeEventId);

			case REQUEST_MOVE:
				return handleRequestMove();

			case SEND_MOVE:
				int move = gson.fromJson(request.getData(), Integer.class);
				return handleSendMove(move);

			case ABORT_GAME:
				return handleAbortGame();

			case COMPLETE_GAME:
				return handleCompleteGame();

			default:
				return new Response(Response.ResponseStatus.FAILURE, "Invalid Request");
		}
	}



	private GamingResponse handleRequestMove() {
		GamingResponse response = new GamingResponse();

		try {
			if (currentEventId != -1) {
				// Create a local Event variable and set it by using getEvent()
				Event event = DatabaseHelper.getInstance().getEvent(currentEventId);

				// Update the code to get move from the database using currentEventId
				if (event.getMove() != -1 && !event.getTurn().equals(currentUsername)) {
					response.setMove(event.getMove());
					event.setMove(-1);
					event.setTurn(null);

					// Update the database by calling the database helper function updateEvent()
					DatabaseHelper.getInstance().updateEvent(event);

					// Set active to true since the game is still active
					response.setActive(true);
				} else {
					response.setMove(-1);

					// Check the status of the event and update active and message accordingly
					if (event.getStatus() == Event.EventStatus.ABORTED) {
						response.setActive(false);
						response.setMessage("Opponent Abort");
					} else if (event.getStatus() == Event.EventStatus.COMPLETED) {
						response.setActive(false);
						response.setMessage("Opponent Deny Play Again");
					} else {
						response.setActive(true);
					}
				}
			} else {
				response.setMove(-1);
				response.setActive(false);
				response.setMessage("Invalid Event");
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling request move", e);
			response.setStatus(Response.ResponseStatus.FAILURE);
			response.setActive(false);
			response.setMessage("Database Error");
		}

		return response;
	}


	private Response handleRegister(User user) {
		try {
			if (DatabaseHelper.getInstance().isUsernameExists(user.getUsername())) {
				return new Response(Response.ResponseStatus.FAILURE, "Username already exists");
			} else {
				DatabaseHelper.getInstance().createUser(user);
				return new Response(Response.ResponseStatus.SUCCESS, "Registration successful");
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling registration", e);
			return new Response(Response.ResponseStatus.FAILURE, "Database Error");
		}
	}

	private Response handleLogin(User user) {
		try {
			// Get the user with the corresponding username from the database
			User storedUser = DatabaseHelper.getInstance().getUser(user.getUsername());

			// Check if the user exists
			if (storedUser == null) {
				return new Response(Response.ResponseStatus.FAILURE, "User not found");
			}

			// Check if the password is correct
			if (!storedUser.getPassword().equals(user.getPassword())) {
				return new Response(Response.ResponseStatus.FAILURE, "Incorrect password");
			}

			// Set currentUsername and mark the user as online
			currentUsername = storedUser.getUsername();
			storedUser.setOnline(true);

			// Update the user in the database
			DatabaseHelper.getInstance().updateUser(storedUser);

			return new Response(Response.ResponseStatus.SUCCESS, "Login successful");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling login", e);
			return new Response(Response.ResponseStatus.FAILURE, "Database Error");
		}
	}

	private Response handleSendMove(int move) {
		if (move < 0 || move > 8) {
			return new Response(Response.ResponseStatus.FAILURE, "Invalid Move");
		}

		try {
			// Use the currentEventId to get the Event from the database
			// Create a local Event variable and set it by using getEvent()
			Event event = DatabaseHelper.getInstance().getEvent(currentEventId);

			if (event != null) {
				// Save the move in the server
				// Update the code accordingly
				if (event.getTurn() == null || !event.getTurn().equals(currentUsername)) {
					event.setMove(move);
					event.setTurn(currentUsername);

					// Update the database by calling the database helper function updateEvent()
					DatabaseHelper.getInstance().updateEvent(event);

					return new Response(Response.ResponseStatus.SUCCESS, "Move Added");
				} else {
					return new Response(Response.ResponseStatus.FAILURE, "Not your turn to move");
				}
			} else {
				return new Response(Response.ResponseStatus.FAILURE, "Invalid Event");
			}

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling send move", e);
			return new Response(Response.ResponseStatus.FAILURE, "Database Error");
		}
	}



	private Response handleSendInvitation(String opponent) {
		Response response = new Response();

		// Check if the user is logged in
		if (currentUsername == null) {
			response.setStatus(Response.ResponseStatus.FAILURE);
			response.setMessage("User not logged in");
			return response;
		}

		try {
			// Check if the opponent is available to receive an invitation
			if (!DatabaseHelper.getInstance().isUserAvailable(opponent)) {
				response.setStatus(Response.ResponseStatus.FAILURE);
				response.setMessage("Selected opponent is not available for invitation");
				return response;
			}

			// Create a new object of class Event
			Event newEvent = new Event();
			newEvent.setSender(currentUsername);
			newEvent.setOpponent(opponent);
			newEvent.setStatus(Event.EventStatus.PENDING);
			newEvent.setMove(-1);

			// Save the object to the database using the helper function createEvent()
			DatabaseHelper.getInstance().createEvent(newEvent);

			response.setStatus(Response.ResponseStatus.SUCCESS);
			response.setMessage("Invitation sent successfully");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling send invitation", e);
			response.setStatus(Response.ResponseStatus.FAILURE);
			response.setMessage("Database Error");
		}

		return response;
	}

	private PairingResponse handleUpdatePairing() {
		PairingResponse response = new PairingResponse();

		// Check if the user is logged in
		if (currentUsername.isEmpty()) {
			response.setStatus(Response.ResponseStatus.FAILURE);  // Use the fully qualified name
			response.setMessage("User not logged in");
			return response;
		}

		try {
			// Use database helper functions to get the necessary data
			List<User> availableUsers = DatabaseHelper.getInstance().getAvailableUsers(currentUsername);

			Event invitation = DatabaseHelper.getInstance().getUserInvitation(currentUsername);
			Event invitationResponse = DatabaseHelper.getInstance().getUserInvitationResponse(currentUsername);

			// Set the data in the PairingResponse object
			response.setStatus(Response.ResponseStatus.SUCCESS);  // Use the fully qualified name
			response.setMessage("Pairing update successful");
			response.setAvailableUsers(availableUsers);
			response.setInvitation(invitation);
			response.setInvitationResponse(invitationResponse);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling update pairing", e);
			response.setStatus(Response.ResponseStatus.FAILURE);  // Use the fully qualified name
			response.setMessage("Database Error");
		}

		return response;
	}

	private Response handleAcceptInvitation(int eventId) {
		Response response = new Response();

		try {
			// Use database helper function getEvent() to retrieve the Event object
			Event event = DatabaseHelper.getInstance().getEvent(eventId);

			// Check if the event exists, the status is PENDING, and the opponent is the current username
			if (event == null || event.getStatus() != Event.EventStatus.PENDING || !event.getOpponent().equals(currentUsername)) {
				response.setStatus(Response.ResponseStatus.FAILURE);
				response.setMessage("Invalid or expired invitation");
				return response;
			}

			// Change the status of the event to ACCEPTED
			event.setStatus(Event.EventStatus.ACCEPTED);

			// Abort any other pending invitations the user might have from other players
			DatabaseHelper.getInstance().abortAllUserEvents(currentUsername, eventId);

			// Update the event in the database using the helper function updateEvent()
			DatabaseHelper.getInstance().updateEvent(event);

			// Set currentEventId to eventId
			currentEventId = eventId;

			response.setStatus(Response.ResponseStatus.SUCCESS);
			response.setMessage("Invitation accepted successfully");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling accept invitation", e);
			response.setStatus(Response.ResponseStatus.FAILURE);
			response.setMessage("Database Error");
		}

		return response;
	}

	private Response handleDeclineInvitation(int eventId) {
		Response response = new Response();

		try {
			// Use the database helper function getEvent() to retrieve the Event object with the corresponding eventId
			Event event = DatabaseHelper.getInstance().getEvent(eventId);

			// Check if the event exists, the status is PENDING, and the opponent of the event is the current username
			if (event == null || event.getStatus() != Event.EventStatus.PENDING || !event.getOpponent().equals(currentUsername)) {
				response.setStatus(Response.ResponseStatus.FAILURE);
				response.setMessage("Invalid or expired invitation");
				return response;
			}

			// Change the status of the event to DECLINED
			event.setStatus(Event.EventStatus.DECLINED);

			// Update the event in the database using the helper function updateEvent()
			DatabaseHelper.getInstance().updateEvent(event);

			response.setStatus(Response.ResponseStatus.SUCCESS);
			response.setMessage("Invitation declined successfully");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling decline invitation", e);
			response.setStatus(Response.ResponseStatus.FAILURE);
			response.setMessage("Database Error");
		}

		return response;
	}

	private Response handleAcknowledgeResponse(int eventId) {
		Response response = new Response();

		try {
			// Use the database helper function getEvent() to retrieve the Event object with the corresponding eventId
			Event event = DatabaseHelper.getInstance().getEvent(eventId);

			// Check if the event exists, and if the sender of the event is the current username
			if (event == null || !event.getSender().equals(currentUsername)) {
				response.setStatus(Response.ResponseStatus.FAILURE);
				response.setMessage("Invalid or unauthorized request");
				return response;
			}

			// Check the response status and take appropriate actions
			if (event.getStatus() == Event.EventStatus.DECLINED) {
				// If the response was DECLINED, set the status to ABORTED
				event.setStatus(Event.EventStatus.ABORTED);
			} else if (event.getStatus() == Event.EventStatus.ACCEPTED) {
				// If the response was ACCEPTED, set currentEventId to eventId
				currentEventId = eventId;

				// Abort any other pending invitations the user might have from other players
				DatabaseHelper.getInstance().abortAllUserEvents(currentUsername, eventId);
			}

			// Update the event in the database using the helper function updateEvent()
			DatabaseHelper.getInstance().updateEvent(event);

			response.setStatus(Response.ResponseStatus.SUCCESS);
			response.setMessage("Response acknowledged successfully");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling acknowledge response", e);
			response.setStatus(Response.ResponseStatus.FAILURE);
			response.setMessage("Database Error");
		}

		return response;
	}

	private Response handleCompleteGame() {
		Response response = new Response();

		try {
			// Use the currentEventId to get the Event from the database
			Event event = DatabaseHelper.getInstance().getEvent(currentEventId);

			// Check if the event exists and the status is PLAYING
			if (event == null || event.getStatus() != Event.EventStatus.PLAYING) {
				response.setStatus(Response.ResponseStatus.FAILURE);
				response.setMessage("Invalid or unauthorized request");
				return response;
			}

			// Change the status of the event to COMPLETED
			event.setStatus(Event.EventStatus.COMPLETED);

			// Update the event in the database using the helper function updateEvent()
			DatabaseHelper.getInstance().updateEvent(event);

			// Reset currentEventId to -1
			currentEventId = -1;

			response.setStatus(Response.ResponseStatus.SUCCESS);
			response.setMessage("Game completed successfully");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling complete game", e);
			response.setStatus(Response.ResponseStatus.FAILURE);
			response.setMessage("Database Error");
		}

		return response;
	}

	private Response handleAbortGame() {
		Response response = new Response();

		try {
			// Use the currentEventId to get the Event from the database
			Event event = DatabaseHelper.getInstance().getEvent(currentEventId);

			// Check if the event exists and the status is PLAYING
			if (event == null || event.getStatus() != Event.EventStatus.PLAYING) {
				response.setStatus(Response.ResponseStatus.FAILURE);
				response.setMessage("Invalid or unauthorized request");
				return response;
			}

			// Change the status of the event to ABORTED
			event.setStatus(Event.EventStatus.ABORTED);

			// Update the event in the database using the helper function updateEvent()
			DatabaseHelper.getInstance().updateEvent(event);

			// Reset currentEventId to -1
			currentEventId = -1;

			response.setStatus(Response.ResponseStatus.SUCCESS);
			response.setMessage("Game aborted successfully");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error while handling abort game", e);
			response.setStatus(Response.ResponseStatus.FAILURE);
			response.setMessage("Database Error");
		}

		return response;
	}




}
