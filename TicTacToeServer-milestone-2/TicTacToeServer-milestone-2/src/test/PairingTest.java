package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Event;
import model.User;
import server.DatabaseHelper;
import server.SocketServer;
import socket.Request;
import socket.Response;
import socket.PairingResponse;

import java.sql.SQLException;




public class PairingTest {

    public static void main(String[] args) throws Exception {
        // Set up your test environment, e.g., connect to the server
        // Make sure the server is running and reachable
        Thread mainThread = new Thread(() -> {
            try{
                DatabaseHelper.getInstance().truncateTables();
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
            SocketServer.main(null);
        });
        mainThread.start();
        Thread.sleep(1000);

        //GsonG
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

        User user1 = new User("user1", "12345", "nick", false);
        User user2 = new User("user2", "12345", "aidan", false);
        User user3 = new User("user3", "12345", "rick", false);
        User user4 = new User("user4", "12345", "john", false);

        SocketClientHelper user1Client = new SocketClientHelper();
        SocketClientHelper user2Client = new SocketClientHelper();
        SocketClientHelper user3Client = new SocketClientHelper();
        SocketClientHelper user4Client = new SocketClientHelper();


        // Simulate the pairing process

        //Test 1
        System.out.println("TEST 1");
        Request loginRequest1 = new Request(Request.RequestType.LOGIN, gson.toJson(user1));
        Response loginResponse1 = user1Client.sendRequest(loginRequest1, Response.class);
        System.out.println(gson.toJson(loginResponse1));

        // Test 2
        System.out.println("TEST 2");
        Request registerRequest2 = new Request(Request.RequestType.REGISTER, gson.toJson(user1));
        Response registerResponse2 = user2Client.sendRequest(registerRequest2, Response.class);
        System.out.println(gson.toJson(registerResponse2));

        //Test3
        System.out.println("TEST 3");
        user1.setPassword("wrong_password");
        Request loginRequestWrongPassword = new Request(Request.RequestType.LOGIN, gson.toJson(user1));
        Response responseWrongPassword = user1Client.sendRequest(loginRequestWrongPassword, Response.class);
        System.out.println(gson.toJson(responseWrongPassword));

        //Test 4
        System.out.println("TEST 4");
        user1.setPassword("12345");
        Request loginRequestCorrectPassword = new Request(Request.RequestType.LOGIN, gson.toJson(user1));
        Response responseCorrectPassword = user1Client.sendRequest(loginRequestCorrectPassword, Response.class);
        System.out.println(gson.toJson(responseCorrectPassword));

        // Register rest of the users
        Request registerRequestUser2 = new Request(Request.RequestType.REGISTER, gson.toJson(user2));
        user2Client.sendRequest(registerRequestUser2, Response.class);
        Request registerRequestUser3 = new Request(Request.RequestType.REGISTER, gson.toJson(user3));
        user2Client.sendRequest(registerRequestUser3, Response.class);
        Request registerRequestUser4 = new Request(Request.RequestType.REGISTER, gson.toJson(user4));
        user2Client.sendRequest(registerRequestUser4, Response.class);

        //Test5
        System.out.println("TEST 5");
        Request updatePairingRequestUser1 = new Request(Request.RequestType.UPDATE_PAIRING, null);
        PairingResponse pairingResponseUser1 = user1Client.sendRequest(updatePairingRequestUser1, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser1));

        //Test6
        System.out.println("TEST 6");
        Request updatePairingRequestUser2 = new Request(Request.RequestType.UPDATE_PAIRING, null);
        PairingResponse pairingResponseUser2 = user2Client.sendRequest(updatePairingRequestUser2, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser2));

        // Login user2 to the system by sending a LOGIN request.
        Request loginRequestUser2 = new Request(Request.RequestType.LOGIN, gson.toJson(user2));
        user2Client.sendRequest(loginRequestUser2, Response.class);

        // Test 7
        // Send a UPDATE_PAIRING request with user1. It should return PairingResponse with one available user (i.e., user2).
        System.out.println("TEST 7");
        PairingResponse pairingResponseUser1AfterLogin = user1Client.sendRequest(updatePairingRequestUser1, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser1AfterLogin));

        // Login the rest of the users by sending a LOGIN request with user3 and user4.
        Request loginRequestUser3 = new Request(Request.RequestType.LOGIN, gson.toJson(user3));
        Request loginRequestUser4 = new Request(Request.RequestType.LOGIN, gson.toJson(user4));
        user3Client.sendRequest(loginRequestUser3, Response.class);
        user4Client.sendRequest(loginRequestUser4, Response.class);


// Test 8
// Send a UPDATE_PAIRING request with user2. It should return PairingResponse with three available users (i.e., user1, user3, and user4).
        System.out.println("TEST 8");
        PairingResponse pairingResponseUser2AfterLogin = user2Client.sendRequest(updatePairingRequestUser2, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser2AfterLogin));

// Test 9
// Logout user4 by closing the socket connection using the close() function.
        System.out.println("TEST 9");
        user4Client.close();
// Send another UPDATE_PAIRING request with user2. It should now return PairingResponse with two available users (i.e., user1 and user3).
// Because user4 is now offline.
        PairingResponse pairingResponseUser2AfterLogout = user2Client.sendRequest(updatePairingRequestUser2, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser2AfterLogout));

// Test 10
// Login user4 back by sending a LOGIN request.
        System.out.println("TEST 10");
        Request loginRequestUser5 = new Request(Request.RequestType.LOGIN, gson.toJson(user4));
        user4Client = new SocketClientHelper();
        user4Client.sendRequest(loginRequestUser5, Response.class);
        PairingResponse pairingResponseUser2AfterLoginAgain = user2Client.sendRequest(updatePairingRequestUser2, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser2AfterLoginAgain));


// Test 11
// Send a SEND_INVITATION from user1 to user2. It should return a SUCCESS response.
        System.out.println("TEST 11");
        Request sendInvitationRequest = new Request(Request.RequestType.SEND_INVITATION, gson.toJson(user2.getUsername()));
        Response sendInvitationResponse = user1Client.sendRequest(sendInvitationRequest, Response.class);
        System.out.println(gson.toJson(sendInvitationResponse));

// Test 12
// Send a UPDATE_PAIRING request with user2. It should return PairingResponse with an invitation from user1.
        System.out.println("TEST 12");
        PairingResponse pairingResponseUser2AfterInvitation = user2Client.sendRequest(updatePairingRequestUser2, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser2AfterInvitation));

// Test 13
// Send a DECLINE_INVITATION with user2 of the invitation above. It should return a SUCCESS response.
        System.out.println("TEST 13");
        Event invitationEvent = pairingResponseUser2AfterInvitation.getInvitation();
        Request declineInvitationRequest = new Request(Request.RequestType.DECLINE_INVITATION, gson.toJson(invitationEvent.getEventId()));
        Response declineInvitationResponse = user2Client.sendRequest(declineInvitationRequest, Response.class);
        System.out.println(gson.toJson(declineInvitationResponse));

// Test 14
// Send a UPDATE_PAIRING request with user1. It should return PairingResponse with a decline invitation response from user2.
        System.out.println("TEST 14");
        PairingResponse pairingResponseUser1AfterDecline = user1Client.sendRequest(updatePairingRequestUser1, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser1AfterDecline));

// Test 15
// Send an ACKNOWLEDGE_RESPONSE request with user1. It should return a SUCCESS response.
        System.out.println("TEST 15");
        Event declineInvitationResponseEvent = pairingResponseUser1AfterDecline.getInvitationResponse();
        Request acknowledgeResponseRequest = new Request(Request.RequestType.ACKNOWLEDGE_RESPONSE, gson.toJson(declineInvitationResponseEvent.getEventId()));
        Response acknowledgeResponse = user1Client.sendRequest(acknowledgeResponseRequest, Response.class);
        System.out.println(gson.toJson(acknowledgeResponse));

// Test 16
// Send a SEND_INVITATION from user1 to user3. It should return a SUCCESS response.
        System.out.println("TEST 16");
        Request sendInvitationRequestUser3 = new Request(Request.RequestType.SEND_INVITATION, gson.toJson(user3.getUsername()));
        Response sendInvitationResponseUser3 = user1Client.sendRequest(sendInvitationRequestUser3, Response.class);
        System.out.println(gson.toJson(sendInvitationResponseUser3));

// Test 17
// Send a UPDATE_PAIRING request with user3. It should return PairingResponse with an invitation from user1.
        System.out.println("TEST 17");
        PairingResponse pairingResponseUser3AfterInvitation = user3Client.sendRequest(updatePairingRequestUser2, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser3AfterInvitation));

// Test 18
// Send an ACCEPTED_INVITATION with user3 of the invitation above. It should return a SUCCESS response.
        System.out.println("TEST 18");
        Event invitationEventUser3 = pairingResponseUser3AfterInvitation.getInvitation();
        Request acceptInvitationRequestUser3 = new Request(Request.RequestType.ACCEPT_INVITATION, gson.toJson(invitationEventUser3.getEventId()));
        Response acceptInvitationResponseUser3 = user3Client.sendRequest(acceptInvitationRequestUser3, Response.class);
        System.out.println(gson.toJson(acceptInvitationResponseUser3));

// Test 19
// Send a UPDATE_PAIRING request with user1. It should return PairingResponse with an accept invitation response from user3.
        System.out.println("TEST 19");
        PairingResponse pairingResponseUser1AfterAccept = user1Client.sendRequest(updatePairingRequestUser1, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser1AfterAccept));

// Test 20
// Send an ACKNOWLEDGE_RESPONSE request with user1. It should return a SUCCESS response.
        System.out.println("TEST 20");
        Event acceptInvitationResponseEventUser1 = pairingResponseUser1AfterAccept.getInvitationResponse();
        Request acknowledgeResponseRequestUser1 = new Request(Request.RequestType.ACKNOWLEDGE_RESPONSE, gson.toJson(acceptInvitationResponseEventUser1.getEventId()));
        Response acknowledgeResponseUser1 = user1Client.sendRequest(acknowledgeResponseRequestUser1, Response.class);
        System.out.println(gson.toJson(acknowledgeResponseUser1));

// Test 21
// Send a UPDATE_PAIRING request with user2. It should return PairingResponse with one available user (i.e., user4).
        System.out.println("TEST 21");
// Since user1 and user3 are currently playing a game.
        PairingResponse pairingResponseUser2AfterGameStart = user2Client.sendRequest(updatePairingRequestUser2, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser2AfterGameStart));

// Test 22
// Send an ABORT_GAME request with user1. It should return a SUCCESS response.
        System.out.println("TEST 22");
        Request abortGameRequest = new Request(Request.RequestType.ABORT_GAME, null);
        Response abortGameResponse = user1Client.sendRequest(abortGameRequest, Response.class);
        System.out.println(gson.toJson(abortGameResponse));

// Test 23
// Send a UPDATE_PAIRING request with user2. It should return PairingResponse with three available users (i.e., user1, user3, and user4).
// Since user1 and user3 game is aborted.
        System.out.println("TEST 23");
        PairingResponse pairingResponseUser2AfterGameAbort = user2Client.sendRequest(updatePairingRequestUser2, PairingResponse.class);
        System.out.println(gson.toJson(pairingResponseUser2AfterGameAbort));





    }
}
